package model.process.coobservability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;
import model.process.ProcessOperation;

public class ProcessCoobservability {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static String controllableRef;
	private static String observableRef;
	private static String initialRef;
	
//---  Meta   ---------------------------------------------------------------------------------
	
	public static void assignReferences(String cont, String obs, String init) {
		controllableRef = cont;
		observableRef = obs;
		initialRef = init;
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public static boolean isCoobservableUStruct(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean enableByDefault) {
		UStructure ustr = constructUStruct(plant, attr, agents);
		HashSet<String> badGood = enableByDefault ? ustr.getIllegalConfigOneStates() : ustr.getIllegalConfigTwoStates();
		return badGood.isEmpty();
	}
	
	public static boolean isSBCoobservableUrvashi(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		HashSet<String> eventNamesHold = new HashSet<String>();
		HashSet<String> controllableHold = new HashSet<String>();
		
		for(TransitionSystem t : plants) {
			eventNamesHold.addAll(t.getEventNames());
			controllableHold.addAll(t.getEventsWithAttribute(controllableRef));
		}
		
		ArrayList<String> eventNames = new ArrayList<String>();
		eventNames.addAll(eventNamesHold);
		
		ArrayList<Agent> agen = constructAgents(eventNames, attr, agents);
		
		HashMap<String, HashSet<StateSet>> disable = new HashMap<String, HashSet<StateSet>>();
		HashMap<String, HashSet<StateSet>> enable = new HashMap<String, HashSet<StateSet>>();
		
		initializeEnableDisable(disable, enable, plants, specs);
		
		System.out.println(disable);
		System.out.println(enable);
		
		boolean pass = true;
		
		ArrayList<String> controllable = new ArrayList<String>();
		controllable.addAll(controllable);
		
		for(String e : controllable) {
			if(!disable.get(e).isEmpty()) {
				pass = false;
			}
		}
		
		if(pass) {
			return true;
		}
		
		for(Agent a : agen) {
			boolean skip = true;
			
			for(String e : controllable) {
				Boolean res = a.getEventAttribute(e, controllableRef);
				if(res && !disable.get(e).isEmpty()) {
					skip = false;
				}
			}
			
			if(skip) {
				continue;
			}
			
			ArrayList<String> observable = a.getEventsAttributeSet(observableRef, true);
			controllable = a.getEventsAttributeSet(controllableRef, true);
			
			HashMap<String, HashSet<StateSet>> tempDisable = subsetConstructHiding(plants, specs, enable, disable, observable, controllable);
			
			pass = true;
			
			for(String c : controllable) {
				if(!tempDisable.get(c).isEmpty()) {
					pass = false;
				}
			}
			disable = tempDisable;
			if(pass) {
				return true;
			}
			
		}
		
		return false;
	}

	public static boolean isCoobservableLiu(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean enableByDefault) {
		ArrayList<TransitionSystem> copyPlants = new ArrayList<TransitionSystem>();
		ArrayList<TransitionSystem> copySpecs = new ArrayList<TransitionSystem>();
		copyPlants.addAll(plants);
		copySpecs.addAll(specs);
		
		TransitionSystem progress = new TransitionSystem("progression");
		progress.copyAttributes(plants.get(0));
		String init = "0";
		progress.addState(init);
		progress.setStateAttribute(init, initialRef, true);
		for(String e : getAllEvents(plants)) {
			for(TransitionSystem t : plants) {
				if(t.eventExists(e)) {
					progress.addEvent(e, t);
					progress.addTransition(init, e, init);
					break;
				}
			}
		}
		while(!copySpecs.isEmpty()) {
			TransitionSystem pick = pickSpec(copySpecs);
			copySpecs.remove(pick);
			progress = parallelComp(progress, pick);
			while(!isCoobservableUStruct(progress, attr, agents, enableByDefault)) {
				//Get counterexample - here this should mean one of our problem states (badGood/goodBad states)
				//Find plant or spec that rejects the counterexample - that can make the correct control decision/removes it as a problem
				//if none exists, return false
				//otherwise add it to progress and continue on
			}
		}
		
		
		return false;
	}
	
	public static boolean isSBCoobservableLiu(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {

		
		return false;
	}
	
	private static TransitionSystem parallelComp(TransitionSystem ... in) {
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		for(TransitionSystem t : in) {
			use.add(t);
		}
		return ProcessOperation.parallelComposition(use);
	}
	
	private static TransitionSystem pickSpec(ArrayList<TransitionSystem> specs) {
		return specs.get(0);
	}
	
	public static UStructure constructUStruct(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		ArrayList<Agent> agen = constructAgents(plant.getEventNames(), attr, agents);
		return new UStructure(plant, attr, agen);
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	//-- Urvashi  ---------------------------------------------
	
	private static void initializeEnableDisable(HashMap<String, HashSet<StateSet>> disable, HashMap<String, HashSet<StateSet>> enable, ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs) {
		ArrayList<String> controllable = getAllTypedEvents(plants, controllableRef);
		for(String c : controllable) {
			disable.put(c, new HashSet<StateSet>());
			enable.put(c, new HashSet<StateSet>());
		}
		StateSet.assignSizes(plants.size(), specs.size());
		LinkedList<StateSet> queue = new LinkedList<StateSet>();
		HashSet<StateSet> visited = new HashSet<StateSet>();
		queue.add(initialStateSet(plants, specs));
		while(!queue.isEmpty()) {
			StateSet curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			for(String s : getAllEvents(plants)) {
				if(canProceed(plants, null, curr, s)) {
					boolean cont = controllable.contains(s);
					if(!canProceed(null, specs, curr, s)) {
						if(cont) {
							disable.get(s).add(curr);
						}
					}
					else {
						if(cont) {
							enable.get(s).add(curr);
						}
						queue.add(stateSetStep(plants, specs, curr, s));
					}
				}
				
			}
		}
	}
	
	private static HashMap<String, HashSet<StateSet>> subsetConstructHiding(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, HashMap<String, HashSet<StateSet>> enable, HashMap<String, HashSet<StateSet>> disable, ArrayList<String> agentObs, ArrayList<String> agentCont){
		HashMap<String, HashSet<StateSet>> out = new HashMap<String, HashSet<StateSet>>();
		
		ArrayList<String> controllable = getAllTypedEvents(plants, controllableRef);
		
		for(String c : controllable) {
			out.put(c, agentCont.contains(c) ? new HashSet<StateSet>() : disable.get(c));
		}
		
		ArrayList<String> agentUnobs = getAllEvents(plants);
		agentUnobs.removeAll(agentObs);
		
		HashSet<StateSet> actualInit = new HashSet<StateSet>();
		actualInit.add(initialStateSet(plants, specs));
		
		LinkedList<HashSet<StateSet>> queue = new LinkedList<HashSet<StateSet>>();
		queue.add(actualInit);
		HashSet<HashSet<StateSet>> visited = new HashSet<HashSet<StateSet>>();
		
		//This is in case two distinct states to the visited HashSet generate the same observer view set of states
		HashSet<HashSet<StateSet>> handled = new HashSet<HashSet<StateSet>>();
		
		HashSet<HashSet<StateSet>> alreadyGenerated = new HashSet<HashSet<StateSet>>();
		
		while(!queue.isEmpty()) {
			HashSet<StateSet> curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			
			HashSet<StateSet> totalGrouping = new HashSet<StateSet>();
			LinkedList<StateSet> diminishGroup = new LinkedList<StateSet>();
			diminishGroup.addAll(curr);
			while(!diminishGroup.isEmpty()) {
				StateSet currSet = diminishGroup.poll();
				if(totalGrouping.contains(currSet)) {
					continue;
				}
				totalGrouping.add(currSet);
				
				for(String u : agentUnobs) {
					if(canProceed(plants, specs, currSet, u)) {
						diminishGroup.add(stateSetStep(plants, specs, currSet, u));
					}
				}
				
			}
			if(!handled.contains(totalGrouping)) {
				handled.add(totalGrouping);
				for(String s : controllable) {
					if(agentCont.contains(s) && intersectionCheck(totalGrouping, enable.get(s)) && intersectionCheck(totalGrouping, disable.get(s))) {
						out.get(s).addAll(intersection(totalGrouping, disable.get(s)));
					}
				}
				for(String o : agentObs) {
					HashSet<StateSet> next = new HashSet<StateSet>();
					for(StateSet s : totalGrouping) {
						if(canProceed(plants, specs, s, o)) {
							next.add(stateSetStep(plants, specs, s, o));
						}
					}
					if(!alreadyGenerated.contains(next)) {
						alreadyGenerated.add(next);
						queue.add(next);
					}
				}
			}
		}
		
		return out;
	}
	
	private static StateSet initialStateSet(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs) {
		String[] use = new String[plants.size() + specs.size()];
		for(int i = 0; i < plants.size(); i++) {
			use[i] = getInitialState(plants.get(i));
		}
		for(int i = 0; i < specs.size(); i++) {
			use[i + plants.size()] = getInitialState(specs.get(i));
		}
		return new StateSet(use);
	}
	
	private static String getInitialState(TransitionSystem t) {
		return t.getStatesWithAttribute(initialRef).get(0);
	}
	
	private static StateSet stateSetStep(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, StateSet curr, String event) {
		String[] out = new String[plants.size() + specs.size()];

		for(int i = 0; i < plants.size(); i++) {
			TransitionSystem t = plants.get(i);
			out[i] = knowsEvent(t, event) ? t.getStateEventTransitionStates(curr.getPlantState(i), event).get(0) : curr.getPlantState(i);
		}
		
		for(int i = 0; i < specs.size(); i++) {
			TransitionSystem t = specs.get(i);
			out[i + plants.size()] = knowsEvent(t, event) ? t.getStateEventTransitionStates(curr.getSpecState(i), event).get(0) : curr.getSpecState(i);
		}
		return new StateSet(out);
	}
	
	private static boolean knowsEvent(TransitionSystem system, String event) {
		return system.getEventNames().contains(event);
	}
	
	private static boolean canPerformEvent(TransitionSystem system, String state, String event) {
		return knowsEvent(system, event) && system.getStateTransitionEvents(state).contains(event);
	}
	
	private static boolean canProceed(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, StateSet curr, String event) {
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
	
	private static ArrayList<String> getAllEvents(ArrayList<TransitionSystem> plants){
		HashSet<String> hold = new HashSet<String>();
		
		for(TransitionSystem t : plants) {
			hold.addAll(t.getEventNames());
		}
		
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(hold);
		return out;
	}
	
	private static ArrayList<String> getAllTypedEvents(ArrayList<TransitionSystem> plants, String type){
		HashSet<String> hold = new HashSet<String>();
		
		for(TransitionSystem t : plants) {
			hold.addAll(t.getEventsWithAttribute(type));
		}
		
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(hold);
		return out;
	}
	
	private static HashSet<StateSet> intersection(HashSet<StateSet> conglom, HashSet<StateSet> check){
		HashSet<StateSet> out = new HashSet<StateSet>();
		for(StateSet s : check) {
			if(conglom.contains(s)) {
				out.add(s);
			}
		}
		return out;
	}
	
	private static boolean intersectionCheck(HashSet<StateSet> conglom, HashSet<StateSet> check) {
		for(StateSet s : check) {
			if(conglom.contains(s)) {
				return true;
			}
		}
		return false;
	}
	
	//-- Other  -----------------------------------------------

	private static ArrayList<Agent> constructAgents(ArrayList<String> event, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents){
		ArrayList<Agent> agen = new ArrayList<Agent>();
		
		for(HashMap<String, ArrayList<Boolean>> h : agents) {
			Agent a = new Agent(attr, event);
			for(String s : event) {
				for(int i = 0; i < attr.size(); i++) {
					Boolean b = h.get(s).get(i);
					if(b)
						a.setAttribute(attr.get(i), s, true);
				}
			}
			agen.add(a);
		}
		return agen;
	}
	
}
