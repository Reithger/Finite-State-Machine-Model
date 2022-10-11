package model.process.coobservability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;
import model.process.coobservability.support.Agent;
import model.process.coobservability.support.StateSetPath;
import model.process.memory.ConcreteMemoryMeasure;

public class StateBased extends ConcreteMemoryMeasure {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static String attributeInitialRef;
	private static String attributeObservableRef;
	private static String attributeControllableRef;
	
	private HashMap<String, HashSet<StateSetPath>> disable;
	private HashMap<String, HashSet<StateSetPath>> enable;
	
	private HashMap<StateSetPath, ArrayList<ArrayList<StateSetPath>>> pathTracing;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public StateBased(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<Agent> agents, boolean pathTrack) {
		super();
		disable = new HashMap<String, HashSet<StateSetPath>>();
		enable = new HashMap<String, HashSet<StateSetPath>>();
		if(pathTrack) {
			pathTracing = new HashMap<StateSetPath, ArrayList<ArrayList<StateSetPath>>>();
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
		printEnableDisableSets();
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
		
		printEnableDisableSets();
		
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

			HashMap<String, HashSet<StateSetPath>> tempDisable = observerConstructHiding(plants, specs, enable, disable, a.getEventsAttributeSet(attributeObservableRef, true), a.getEventsAttributeSet(attributeControllableRef, true), controllable);

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
	
	private void initializeEnableDisable(HashMap<String, HashSet<StateSetPath>> disable, HashMap<String, HashSet<StateSetPath>> enable, ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, HashSet<String> controllable) {
		for(String c : controllable) {
			disable.put(c, new HashSet<StateSetPath>());
			enable.put(c, new HashSet<StateSetPath>());
		}
		StateSetPath.assignSizes(plants.size(), specs.size());
		
		LinkedList<StateSetPath> queue = new LinkedList<StateSetPath>();
		HashSet<StateSetPath> visited = new HashSet<StateSetPath>();
		StateSetPath first = initialStateSetPath(plants, specs);
		
		queue.add(first);
		while(!queue.isEmpty()) {
			StateSetPath curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			boolean bail = true;
			for(String s : getAllEvents(plants)) {
				if(canProceed(plants, null, curr, s)) {
					boolean specCan = canProceed(null, specs, curr, s);
					if(specCan) {
						StateSetPath next = stateSetStep(plants, specs, curr, s);
						queue.add(next);
					}
					if(controllable.contains(s)) {
						bail = false;
						curr.setProblemEvent(s);
						if(specCan) {
							enable.get(s).add(curr);
						}
						else {
							disable.get(s).add(curr);
							initializePathTracingDisablement(curr);
						}
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

	private HashMap<String, HashSet<StateSetPath>> observerConstructHiding(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, HashMap<String, HashSet<StateSetPath>> enable, HashMap<String, HashSet<StateSetPath>> disable, ArrayList<String> agentObs, ArrayList<String> agentCont, HashSet<String> controllable){
		HashMap<String, HashSet<StateSetPath>> out = new HashMap<String, HashSet<StateSetPath>>();
		
		for(String c : controllable) {
			out.put(c, agentCont.contains(c) ? new HashSet<StateSetPath>() : disable.get(c));
		}
		
		int index = instantiatePathTracingController();
				
		LinkedList<HashSet<StateSetPath>> queue = new LinkedList<HashSet<StateSetPath>>();
		HashSet<HashSet<StateSetPath>> visited = new HashSet<HashSet<StateSetPath>>();
		
		HashSet<StateSetPath> initial = new HashSet<StateSetPath>();
		initial.add(initialStateSetPath(plants, specs));
		
		queue.add(reachableStateSetPaths(plants, specs, initial, agentObs));
		while(!queue.isEmpty()) {
			HashSet<StateSetPath> curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			
			for(String e : agentObs) {
				HashSet<StateSetPath> reachable = new HashSet<StateSetPath>();
				for(StateSetPath s : curr) {
					if(canProceed(plants, specs, s, e)) {
						reachable.add(stateSetStep(plants, specs, s, e));
					}
				}
				queue.add(reachableStateSetPaths(plants, specs, reachable, agentObs));
				logMemoryUsage();
			}
		}

		logMemoryUsage();
		
		for(HashSet<StateSetPath> group : visited) {
			for(String e : agentCont) {
				HashSet<StateSetPath> holdEna = intersection(group, enable.get(e));
				if(!holdEna.isEmpty()) {
					HashSet<StateSetPath> holdDis = intersection(group, disable.get(e));
					out.get(e).addAll(holdDis);
					
					trackPathTracingEquivalence(holdDis, holdEna, index);
				}
			}
		}
		
		return out;
	}

//---  Getter Methods   -----------------------------------------------------------------------

	public ArrayList<ArrayList<StateSetPath>> getEquivalentPaths(StateSetPath in){
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
	
	public ArrayList<StateSetPath> getRemainingDisableStates(){
		ArrayList<StateSetPath> out = new ArrayList<StateSetPath>();
		HashSet<StateSetPath> use = new HashSet<StateSetPath>();
		for(String c : disable.keySet()) {
			use.addAll(disable.get(c));
		}
		out.addAll(use);
		return out;
	}
	
	public ArrayList<StateSetPath> getRemainingEnableStates(){
		ArrayList<StateSetPath> out = new ArrayList<StateSetPath>();
		HashSet<StateSetPath> use = new HashSet<StateSetPath>();
		for(String c : enable.keySet()) {
			use.addAll(enable.get(c));
		}
		out.addAll(use);
		return out;
	}
	
	public ArrayList<String> getStateSetPath(StateSetPath in){
		if(pathTracing == null) {
			return null;
		}
		ArrayList<String> out = in.getEventPath();
		return out;
	}

	public ArrayList<String> getStateSetPathEvents(StateSetPath in) {
		if(pathTracing == null) {
			return null;
		}
		ArrayList<String> out = new ArrayList<String>();
		for(String s : disable.keySet()) {
			if(disable.get(s).contains(in)) {
				out.add(s);
			}
		}
		return out;
	}

//---  Support Methods   ----------------------------------------------------------------------
	
	private StateSetPath initialStateSetPath(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs) {
		String[] use = new String[plants.size() + specs.size()];
		for(int i = 0; i < plants.size(); i++) {
			use[i] = getInitialState(plants.get(i));
		}
		for(int i = 0; i < specs.size(); i++) {
			use[i + plants.size()] = getInitialState(specs.get(i));
		}
		return new StateSetPath(use);
	}

	private StateSetPath stateSetStep(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, StateSetPath curr, String event) {
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
		StateSetPath use = new StateSetPath(out, curr);
		if(use.isNew())
			use.addEvent(event);
		return use;
	}
	
	private HashSet<StateSetPath> reachableStateSetPaths(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, HashSet<StateSetPath> initial, ArrayList<String> agentObs){
		HashSet<StateSetPath> out = new HashSet<StateSetPath>();
		for(StateSetPath i : initial) {
			out.add(i);
		}
		ArrayList<String> unobs = new ArrayList<String>();
		for(String s : getAllEvents(plants)) {
			if(!agentObs.contains(s)) {
				unobs.add(s);
			}
		}
		
		LinkedList<StateSetPath> queue = new LinkedList<StateSetPath>();
		HashSet<StateSetPath> visited = new HashSet<StateSetPath>();
		queue.addAll(initial);
		
		while(!queue.isEmpty()) {
			StateSetPath curr = queue.poll();
			
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
	
	private boolean canProceed(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, StateSetPath curr, String event) {
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
	
	private HashSet<StateSetPath> intersection(HashSet<StateSetPath> conglom, HashSet<StateSetPath> check){
		HashSet<StateSetPath> out = new HashSet<StateSetPath>();
		for(StateSetPath s : check) {
			if(conglom.contains(s)) {
				out.add(s);
			}
		}
		return out;
	}
	
	private void initializePathTracingDisablement(StateSetPath in) {
		if(pathTracing != null) {
			if(!pathTracing.containsKey(in)) {
				pathTracing.put(in, new ArrayList<ArrayList<StateSetPath>>());
			}
		}
	}

	private int instantiatePathTracingController() {
		int index = 0;
		
		if(pathTracing == null) {
			return -1;
		}
		
		for(StateSetPath s : pathTracing.keySet()) {
			pathTracing.get(s).add(new ArrayList<StateSetPath>());
			index = pathTracing.get(s).size() - 1;
		}
		
		return index; 
	}
	
	private void trackPathTracingEquivalence(HashSet<StateSetPath> holdDis, HashSet<StateSetPath> holdEna, int index) {
		
		if(pathTracing != null) {
			for(StateSetPath st : holdDis) {
				for(StateSetPath et : holdEna) {
					pathTracing.get(st).get(index).add(et);
				}
			}
		}
	}

}
