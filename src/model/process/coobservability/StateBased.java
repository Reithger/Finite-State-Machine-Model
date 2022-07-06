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
	
	private HashMap<StateSet, ArrayList<ArrayList<String>>> pathTracing;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public StateBased(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<Agent> agents, boolean pathTrack) {
		super();
		disable = new HashMap<String, HashSet<StateSet>>();
		enable = new HashMap<String, HashSet<StateSet>>();
		if(pathTrack) {
			pathTracing = new HashMap<StateSet, ArrayList<ArrayList<String>>>();
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
		//System.out.println("\n" + enable + "\n" + disable + "\n");
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
		
		//System.out.println("\n" + enable + "\n" + disable + "\n");
		
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
				Boolean res = a.getEventAttribute(e, attributeControllableRef);
				if(res && !disable.get(e).isEmpty()) {
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
		StateSet first = initialStateSet(plants, specs);
		
		if(pathTracing != null) {
			logBasePath(first, new ArrayList<String>());
		}
		
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
					boolean cont = controllable.contains(s);
					if(!canProceed(null, specs, curr, s)) {
						if(cont) {
							disable.get(s).add(curr);
							logNewPath(curr, s);
							bail = false;
						}
					}
					else {
						if(cont) {
							enable.get(s).add(curr);
							logNewPath(curr, s);
							bail = false;
						}
						StateSet next = stateSetStep(plants, specs, curr, s);
						if(pathTracing != null) {
							logBasePath(next, copy(pathTracing.get(curr).get(0), s));
						}
						queue.add(next);
					}
				}
				
			}
			if(pathTracing != null && bail) {
				pathTracing.remove(curr);
			}
		}
	}
	
	private void logNewPath(StateSet curr, String cont) {
		if(pathTracing != null) {
			pathTracing.get(curr).add(copy(pathTracing.get(curr).get(0), cont));
		}
	}
	
	private void logBasePath(StateSet in, ArrayList<String> path) {
		if(!pathTracing.containsKey(in)) {
			pathTracing.put(in, new ArrayList<ArrayList<String>>());
		}
		pathTracing.get(in).add(path);
	}
	
	private HashMap<String, HashSet<StateSet>> observerConstructHiding(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, HashMap<String, HashSet<StateSet>> enable, HashMap<String, HashSet<StateSet>> disable, ArrayList<String> agentObs, ArrayList<String> agentCont, HashSet<String> controllable){
		HashMap<String, HashSet<StateSet>> out = new HashMap<String, HashSet<StateSet>>();
		
		for(String c : controllable) {
			out.put(c, agentCont.contains(c) ? new HashSet<StateSet>() : disable.get(c));
		}
		
		LinkedList<HashSet<StateSet>> queue = new LinkedList<HashSet<StateSet>>();
		HashSet<HashSet<StateSet>> visited = new HashSet<HashSet<StateSet>>();
		
		HashSet<StateSet> initial = new HashSet<StateSet>();
		initial.add(initialStateSet(plants, specs));
		
		queue.add(reachableStateSets(plants, specs, initial, agentObs));
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
				queue.add(reachableStateSets(plants, specs, reachable, agentObs));
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
	
	public ArrayList<String> getStateSetPathMinusEvent(StateSet in){
		if(pathTracing == null) {
			return null;
		}
		return copy(pathTracing.get(in).get(0), null);
	}

	public ArrayList<String> getStateSetPathEvent(StateSet in) {
		if(pathTracing == null) {
			return null;
		}
		ArrayList<String> out = new ArrayList<String>();
		for(int i = 1; i < pathTracing.get(in).size(); i++) {
			ArrayList<String> use = pathTracing.get(in).get(i);
			out.add(use.get(use.size() - 1));
		}
		return out;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
 	private ArrayList<String> copy(ArrayList<String> start, String next){
		ArrayList<String> out = new ArrayList<String>();
		for(String s : start) {
			out.add(s);
		}
		if(next != null)
			out.add(next);
		return out;
	}
	
	private StateSet initialStateSet(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs) {
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
		return new StateSet(out);
	}
	
	private HashSet<StateSet> reachableStateSets(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, HashSet<StateSet> initial, ArrayList<String> agentObs){
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
