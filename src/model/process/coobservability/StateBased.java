package model.process.coobservability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;
import model.process.coobservability.support.Agent;
import model.process.coobservability.support.StateSet;
import model.process.memory.ConcreteMemoryMeasure;

public class StateBased extends ConcreteMemoryMeasure {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static String attributeInitialRef;
	private static String attributeObservableRef;
	private static String attributeControllableRef;
	
	private HashMap<String, HashSet<StateSet>> disable;
	private HashMap<String, HashSet<StateSet>> enable;
	
	private HashMap<StateSet, ArrayList<ArrayList<StateSet>>> pathTracing;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public StateBased(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<Agent> agents, boolean pathTrack) {
		super();
		disable = new HashMap<String, HashSet<StateSet>>();
		enable = new HashMap<String, HashSet<StateSet>>();
		if(pathTrack) {
			pathTracing = new HashMap<StateSet, ArrayList<ArrayList<StateSet>>>();
		}
		operate(plants, specs, attr, agents);
	}
	
	public static void assignAttributeReference(String init, String obs, String cont) {
		attributeInitialRef = init;
		attributeObservableRef = obs;
		attributeControllableRef = cont;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public boolean isSBCoobservable() {
		//printEnableDisableSets();
		for(String c : disable.keySet()) {
			if(!disable.get(c).isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	private void operate(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<Agent> agen) {
		HashSet<String> eventNamesHold = new HashSet<String>();
		HashSet<String> controllable = new HashSet<String>();
		
		for(Agent a : agen) {
			controllable.addAll(a.getEventsAttributeSet(attributeControllableRef, true));
		}
		
		for(TransitionSystem t : plants) {
			eventNamesHold.addAll(t.getEventNames());
		}
		
		ArrayList<String> eventNames = new ArrayList<String>();
		eventNames.addAll(eventNamesHold);
		
		initializeEnableDisable(disable, enable, plants, specs, controllable);

		logMemoryUsage();
		
		boolean giveUp = false;
		
		for(String s : eventNames) {
			if(!controllable.contains(s) && disable.containsKey(s)) {
				giveUp = true;
			}
		}
		
		if(giveUp) {
			return;
		}
		
		//printEnableDisableSets();
		
		boolean pass = true;
		
		for(String e : controllable) {
			if(!disable.get(e).isEmpty()) {
				pass = false;
			}
		}
		
		if(pass) {
			return;	//false
		}
		
		for(Agent a : agen) {
			boolean skip = true;
			
			for(String e : controllable) {
				Boolean canControl = a.getEventAttribute(e, attributeControllableRef);
				if(canControl && !disable.get(e).isEmpty()) {
					skip = false;
				}
			}
			
			if(skip) {
				continue;
			}

			HashMap<String, HashSet<StateSet>> tempDisable = observerConstructHiding(plants, specs, enable, disable, a.getEventsAttributeSet(attributeObservableRef, true), a.getEventsAttributeSet(attributeControllableRef, true), controllable);

			logMemoryUsage();
			
			pass = true;
			
			for(String c : controllable) {
				if(!tempDisable.get(c).isEmpty()) {
					pass = false;
				}
			}
			disable = tempDisable;
			if(pass) {
				return;	//true
			}
			
		}
	}
	
	private void initializeEnableDisable(HashMap<String, HashSet<StateSet>> disable, HashMap<String, HashSet<StateSet>> enable, ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, HashSet<String> controllable) {
		for(String c : controllable) {
			disable.put(c, new HashSet<StateSet>());
			enable.put(c, new HashSet<StateSet>());
		}
		StateSet.assignSizes(plants.size(), specs.size());
		
		LinkedList<StateSet> queue = new LinkedList<StateSet>();
		HashSet<StateSet> visited = new HashSet<StateSet>();
		StateSet first = initialStateSetPath(plants, specs);
		
		queue.add(first);
		while(!queue.isEmpty()) {
			StateSet curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			boolean bail = true;
			for(String s : getAllEvents(plants)) {
				if(canProceed(plants, null, curr, s)) {
					boolean specCan = canProceed(null, specs, curr, s);
					if(specCan) {
						StateSet next = stateSetStep(plants, specs, curr, s);
						queue.add(next);
					}
					if(controllable.contains(s)) {
						bail = false;
						if(specCan) {
							enable.get(s).add(curr);
						}
						else {
							disable.get(s).add(curr);
						}
					}
					else if(!specCan) {
						//So what do we do here, when a transition is not in the specification but its event is not controllable.
						//Causing a hard fail here should be fine? False negative won't contradict
						//Except it only retains stuff it is confused about because it's an asinine system...
						if(disable.get(s) == null) {
							disable.put(s,  new HashSet<StateSet>());
						}
						disable.get(s).add(curr);
					}
				}
				
			}
			if(pathTracing != null && bail) {
				pathTracing.remove(curr);
			}
		}
	}

	/*
	 * Need a structure that maps each <state, event path> tuple consisting of a disablement decision state to a sequence of sets of states consisting of the enablement decision states that that controller confuses for
	 * the original disablement decision state.
	 * 
	 * <state, event path> -> {{<state, event path>, <state, event path>}, {<state, event path>, <state, event path>}}
	 * 
	 * So anytime we see confusion between a disablement state and any enablement states, all versions of the disablement state need to map to all enablement states that controller also reached.
	 * 
	 * 
	 * 
	 */

	private HashMap<String, HashSet<StateSet>> observerConstructHiding(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, HashMap<String, HashSet<StateSet>> enable, HashMap<String, HashSet<StateSet>> disable, ArrayList<String> agentObs, ArrayList<String> agentCont, HashSet<String> controllable){
		HashMap<String, HashSet<StateSet>> out = new HashMap<String, HashSet<StateSet>>();
		
		for(String c : controllable) {
			out.put(c, agentCont.contains(c) ? new HashSet<StateSet>() : disable.get(c));
		}
		
		LinkedList<HashSet<StateSet>> queue = new LinkedList<HashSet<StateSet>>();
		HashSet<HashSet<StateSet>> visited = new HashSet<HashSet<StateSet>>();
		
		HashSet<StateSet> initial = new HashSet<StateSet>();
		initial.add(initialStateSetPath(plants, specs));
		
		queue.add(reachableStateSetPaths(plants, specs, initial, agentObs));
		while(!queue.isEmpty()) {
			HashSet<StateSet> curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			
			for(String e : agentObs) {
				HashSet<StateSet> reachable = new HashSet<StateSet>();
				for(StateSet s : curr) {
					if(canProceed(plants, specs, s, e)) {
						reachable.add(stateSetStep(plants, specs, s, e));
					}
				}
				queue.add(reachableStateSetPaths(plants, specs, reachable, agentObs));
				logMemoryUsage();
			}
		}

		logMemoryUsage();
		for(HashSet<StateSet> group : visited) {
			for(String e : agentCont) {
				HashSet<StateSet> holdEna = intersection(group, enable.get(e));
				if(!holdEna.isEmpty()) {
					HashSet<StateSet> holdDis = intersection(group, disable.get(e));
					out.get(e).addAll(holdDis);
				}
			}
		}
		
		return out;
	}

//---  Getter Methods   -----------------------------------------------------------------------

	public ArrayList<ArrayList<StateSet>> getEquivalentPaths(StateSet in){
		return pathTracing == null ? null : pathTracing.get(in);
	}
	
	private String getInitialState(TransitionSystem t) {
		return t.getStatesWithAttribute(attributeInitialRef).get(0);
	}
	
	private ArrayList<String> getAllEvents(ArrayList<TransitionSystem> plants){
		HashSet<String> hold = new HashSet<String>();
		
		for(TransitionSystem t : plants) {
			hold.addAll(t.getEventNames());
		}
		
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(hold);
		return out;
	}
	
	public ArrayList<StateSet> getRemainingDisableStates(){
		ArrayList<StateSet> out = new ArrayList<StateSet>();
		HashSet<StateSet> use = new HashSet<StateSet>();
		for(String c : disable.keySet()) {
			use.addAll(disable.get(c));
		}
		out.addAll(use);
		return out;
	}
	
	public ArrayList<StateSet> getRemainingEnableStates(){
		ArrayList<StateSet> out = new ArrayList<StateSet>();
		HashSet<StateSet> use = new HashSet<StateSet>();
		for(String c : enable.keySet()) {
			use.addAll(enable.get(c));
		}
		out.addAll(use);
		return out;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private StateSet initialStateSetPath(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs) {
		String[] use = new String[plants.size() + specs.size()];
		for(int i = 0; i < plants.size(); i++) {
			use[i] = getInitialState(plants.get(i));
		}
		for(int i = 0; i < specs.size(); i++) {
			use[i + plants.size()] = getInitialState(specs.get(i));
		}
		return new StateSet(use);
	}

	private StateSet stateSetStep(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, StateSet curr, String event) {
		String[] out = new String[plants.size() + specs.size()];

		for(int i = 0; i < plants.size(); i++) {
			TransitionSystem t = plants.get(i);
			out[i] = knowsEvent(t, event) ? t.getStateEventTransitionStates(curr.getPlantState(i), event).get(0) : curr.getPlantState(i);
		}
		
		for(int i = 0; i < specs.size(); i++) {
			TransitionSystem t = specs.get(i);
			out[i + plants.size()] = canPerformEvent(t, curr.getSpecState(i), event) ? t.getStateEventTransitionStates(curr.getSpecState(i), event).get(0) : curr.getSpecState(i);
		}
		//System.out.println(curr.toString() + " " + event + " " + Arrays.toString(out) + " " + curr.getEventPath());
		StateSet use = new StateSet(out);
		return use;
	}
	
	private HashSet<StateSet> reachableStateSetPaths(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, HashSet<StateSet> initial, ArrayList<String> agentObs){
		HashSet<StateSet> out = new HashSet<StateSet>();
		for(StateSet i : initial) {
			out.add(i);
		}
		ArrayList<String> unobs = new ArrayList<String>();
		for(String s : getAllEvents(plants)) {
			if(!agentObs.contains(s)) {
				unobs.add(s);
			}
		}
		
		LinkedList<StateSet> queue = new LinkedList<StateSet>();
		HashSet<StateSet> visited = new HashSet<StateSet>();
		queue.addAll(initial);
		
		while(!queue.isEmpty()) {
			StateSet curr = queue.poll();
			
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);

			for(String e : unobs) {
				if(canProceed(plants, specs, curr, e)) {
					queue.add(stateSetStep(plants, specs, curr, e));
				}
			}
			
		}
		return visited;
	}
	
	private boolean knowsEvent(TransitionSystem system, String event) {
		return system.getEventNames().contains(event);
	}
	
	private boolean canPerformEvent(TransitionSystem system, String state, String event) {
		return knowsEvent(system, event) && system.getStateTransitionEvents(state).contains(event);
	}
	
	private boolean canProceed(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, StateSet curr, String event) {
		if(plants != null) {
			for(int i = 0; i < plants.size(); i++) {
				TransitionSystem t = plants.get(i);
				if(knowsEvent(t, event) && !canPerformEvent(t, curr.getPlantState(i), event)){
					return false;
				}
			}
		}
		if(specs != null) {
			for(int i = 0; i < specs.size(); i++) {
				TransitionSystem t = specs.get(i);
				if(knowsEvent(t, event) && !canPerformEvent(t, curr.getSpecState(i), event)){
					return false;
				}
			}
		}
		return true;
	}

	private void printEnableDisableSets() {
		System.out.println("Enable/Disable Sets:\nEnable: " + enable + "\nDisable: " + disable + "\n");
	}
	
	//-- SubsetConstructHiding  ---------------------------------------------------------------
	
	private HashSet<StateSet> intersection(HashSet<StateSet> conglom, HashSet<StateSet> check){
		HashSet<StateSet> out = new HashSet<StateSet>();
		for(StateSet s : check) {
			if(conglom.contains(s)) {
				out.add(s);
			}
		}
		return out;
	}
	
}
