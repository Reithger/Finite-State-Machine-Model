package model.process.coobservability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;
import model.process.ProcessDES;

public class ProcessCoobservability {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static String controllableRef;
	private static String observableRef;
	private static String initialRef;
	private static String badTransRef;
	
//---  Meta   ---------------------------------------------------------------------------------
	
	public static void assignReferences(String cont, String obs, String init, String badTrans) {
		controllableRef = cont;
		observableRef = obs;
		initialRef = init;
		badTransRef = badTrans;
	}

//---  Operations   ---------------------------------------------------------------------------
	
	//-- Coobservable  ----------------------------------------
	
	public static boolean isCoobservableUStruct(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean enableByDefault) {
		UStructure ustr = constructUStruct(plant, attr, agents);
		
		HashSet<IllegalConfig> typeOne = new HashSet<IllegalConfig>();
		typeOne.addAll(ustr.getIllegalConfigOneStates());
		HashSet<IllegalConfig> typeTwo = new HashSet<IllegalConfig>();
		typeTwo.addAll(ustr.getIllegalConfigTwoStates());
		CrushMap[] crushes = ustr.getCrushMappings();
		
		for(int i = 0; i < crushes.length; i++) {
			HashSet<Integer> typeOneGroup = new HashSet<Integer>();
			HashSet<Integer> typeTwoGroup = new HashSet<Integer>();
			CrushMap crush = crushes[i];
			for(IllegalConfig ic : typeOne) {
				String st = ic.getStateSet().getCompositeName();
				for(int j : crush.getStateMemberships(st)) {
					typeOneGroup.add(j);
				}
			}
			for(IllegalConfig ic : typeTwo) {
				String st = ic.getStateSet().getCompositeName();
				for(int j : crush.getStateMemberships(st)) {
					typeTwoGroup.add(j);
				}
			}
			
			for(int j : typeOneGroup) {
				if(typeTwoGroup.contains(j)) {
					
				}
			}
			
		}
		
		
		/*
		 * 
		 * It's not so simple.
		 * 
		 * Need to calculate the crush to figure out what states are functionally equivalent to one another according to an agent's
		 * particular observability of events (guessing being also an equivalence) and ensure that no illegal config 1 state is considered
		 * equivalent to an illegal config 2 state.
		 * 
		 * Only a problem if every single agent is incapable of discerning between the two in their particular context.
		 * 
		 * The agents are the specifications' event maps, but not the plants
		 * 
		 * U-Structure is not a map, it's all possible avenues, each agent can know they are in a certain selection of states connected
		 * by unobservable states and we are co-observable as long as they are never able to confuse a violation 1 situation with a violation
		 * 2 situation, as we can post-analysis fix the system to behave in an appropriate way.
		 * 
		 */
		
		HashSet<IllegalConfig> badGood = enableByDefault ? ustr.getIllegalConfigOneStates() : ustr.getIllegalConfigTwoStates();
		return badGood.isEmpty();
	}
	
	private static boolean isCoobservableUStruct(UStructure ustr, boolean enableByDefault) {
		HashSet<IllegalConfig> badGood = enableByDefault ? ustr.getIllegalConfigOneStates() : ustr.getIllegalConfigTwoStates();
		return badGood.isEmpty();
	}
	
	public static boolean isCoobservableUStruct(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean enableByDefault) {
		TransitionSystem ultPlant = parallelComp(plants);
		TransitionSystem ultSpec = parallelComp(specs);
		
		//TODO: Confirm this is the right approach, can we really ignore the specs once they've denoted bad controllable transitions?
		
		LinkedList<StateSet> queue = new LinkedList<StateSet>();
		HashSet<StateSet> visited = new HashSet<StateSet>();
		ArrayList<String> stAttr = ultPlant.getStateAttributes();
		attr.add(badTransRef);
		ultPlant.setStateAttributes(stAttr);
			
		StateSet.assignSizes(1, 1);
		String[] use = new String[] {ultPlant.getStatesWithAttribute(initialRef).get(0), ultSpec.getStatesWithAttribute(initialRef).get(0)};
		
		queue.add(new StateSet(use));
		
		while(!queue.isEmpty()) {
			StateSet curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			
			String plantState = curr.getPlantState(0);
			String specState = curr.getSpecState(0);
			
			for(String e : ultPlant.getStateTransitionEvents(plantState)) {
				if(ultSpec.getStateEventTransitionStates(specState, e).isEmpty() ) {	//Do we need to check for bad transitions behind this?
					if(ultPlant.getEventAttribute(e, controllableRef)) {
						ultPlant.setTransitionAttribute(plantState, e, badTransRef, true);
					}
				}
				else {
					use = new String[] {ultPlant.getStateEventTransitionStates(plantState, e).get(0), ultSpec.getStateEventTransitionStates(specState, e).get(0)};
					queue.add(new StateSet(use));
				}
			}
		}
		
		return isCoobservableUStruct(ultPlant, attr, agents, enableByDefault);
	}

	public static boolean isCoobservableLiu(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean enableByDefault) {
		ArrayList<TransitionSystem> copyPlants = new ArrayList<TransitionSystem>();
		ArrayList<TransitionSystem> copySpecs = new ArrayList<TransitionSystem>();
		copyPlants.addAll(plants);
		copySpecs.addAll(specs);
		
		while(!copySpecs.isEmpty()) {
			TransitionSystem pick = pickSpec(copySpecs);								//Get initial spec to use (heuristics choose here)
			ArrayList<TransitionSystem> hold = new ArrayList<TransitionSystem>();		//List to hold all the plants/specs used in the current iteration
			copySpecs.remove(pick);
			hold.add(pick);
			pick = parallelComp(generateSigmaStarion(plants), pick);			//Immediately merge our sigmaStarion plant with the spec we chose
			UStructure uStruct = constructUStruct(pick, attr, agents);
			while(!isCoobservableUStruct(uStruct, enableByDefault)) {
				if(copyPlants.isEmpty() && copySpecs.isEmpty()) {
					return false;
				}
				IllegalConfig counterexample = pickCounterExample(uStruct, enableByDefault);	//Get a single bad state, probably, maybe write something so UStruct can trace it
				TransitionSystem use = pickComponent(copyPlants, copySpecs, counterexample);	//Heuristics go here
				pick = parallelComp(pick, use);
				uStruct = constructUStruct(pick, attr, agents);
				if(copySpecs.contains(use)) {
					hold.add(use);
					copySpecs.remove(use);
				}
				else {
					hold.add(use);
					copyPlants.remove(use);
				}
				//Get counterexample - here this should mean one of our problem states (badGood/goodBad states)
				//Find plant or spec that rejects the counterexample - that can make the correct control decision/removes it as a problem
				// NOTE: Have to just do full coobservability again via parallel comp.
				// Should also email Dr. Mallik (?) about some stuff and get some context/help; his 2004 paper has the heuristics!
				//if none exists, return false
				//otherwise add it to progress and continue on
			}
			copyPlants.addAll(hold);
		}
		return true;
	}

	private static TransitionSystem generateSigmaStarion(ArrayList<TransitionSystem> plants) {
		TransitionSystem sigmaStar = new TransitionSystem("sigmaStarion");
		sigmaStar.copyAttributes(plants.get(0));
		String init = "0";
		sigmaStar.addState(init);
		sigmaStar.setStateAttribute(init, initialRef, true);
		for(String e : getAllEvents(plants)) {
			for(TransitionSystem t : plants) {
				if(t.eventExists(e)) {
					sigmaStar.addEvent(e, t);
					sigmaStar.addTransition(init, e, init);
					break;
				}
			}
		}
		return sigmaStar;
	}
	
	/**
	 * 
	 * Can probably introduce heuristics for choosing the counterexample? Shortest length, fewest unique events, etc.?
	 * 
	 * Counterexample only counts if it's in both lists of illegalconfig
	 * 
	 * @param ustruct
	 * @param enableByDefault
	 * @return
	 */
	
	private static IllegalConfig pickCounterExample(UStructure ustruct, boolean enableByDefault) {
		HashSet<IllegalConfig> counters = enableByDefault ? ustruct.getIllegalConfigOneStates() : ustruct.getIllegalConfigTwoStates();
		if(counters != null && counters.size() > 0)
			return counters.iterator().next();
		return null;
	}
	
	/**
	 * 
	 * Heuristics:
	 *  - Always choose plant over spec
	 *  - Always choose spec over plant
	 *  - Randomly choose
	 *  - Choose a component that rejects the counterexample the 'soonest'
	 *  - Choose a component that rejects the counterexample the 'latest'
	 *  - Choose a component with the fewest states
	 *  - Choose a component with the fewest transitions
	 *  - Choose a component that shares the most events with the current plant
	 *  - Choose a component with the fewest events 
	 * 
	 * TODO: Needs some way to know that a component can even reject the counterexample in the first place, how to do quickly?
	 *     - Can get the true event path that led to a problem scenario, now how to handle guessing and populating our agents?
	 * 
	 * @param plants
	 * @param specs
	 * @param counterexample
	 * @return
	 */
	
	private static TransitionSystem pickComponent(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, IllegalConfig counterexample) {
		String eventPath = counterexample.getEventPath();
		String controlEvent = counterexample.getEvent();
		
		//Run eventPath through a plant or spec and see if it can make the right control decision for the controlEvent
		//This should represent 'rejecting' the counterexample
		
		//Although this doesn't account for when components guessed along the way of the event path?
		//Who are our agents in this case? Specs are autonomous FSMs just like plants, not observers with an event map
		//Our agents are the event maps of each plant and/or spec observing the conglomerate structure
		
		if(plants.size() != 0) {
			return plants.get(0);
		}
		return specs.get(0);
	}
	
	/**
	 * 
	 * How to model guessing? How do we define rejection if we have to include it to know that it removed it properly, and
	 * how can we check that the counterexample is gone when the means by which we identify it will change once a new component
	 * is added?
	 * 
	 * And if there is an event in the eventPath that this plant/spec knows about and can see which it cannot perform while tracing
	 * the eventPath, does that mean it rejects the counterexample by writ of blocking the progression that would have led to that
	 * error?
	 * 
	 * Are making the right control decision and stopping the eventPath from happening both examples of rejecting the counterexample?
	 * 
	 * @param plant
	 * @param eventPath
	 * @return
	 */
	
	private String navigateTransitionSystem(TransitionSystem plant, String eventPath) {
		String curr = plant.getStatesWithAttribute(initialRef).get(0);
		for(String s : eventPath.split("")) {
			if(plant.getEventNames().contains(s) && plant.getEventsWithAttribute(observableRef).contains(s)) {
				ArrayList<String> next = plant.getStateEventTransitionStates(curr, s);
				if(next != null && next.size() > 0) {
					curr = next.get(0);
				}
				else {
					return null;
				}
			}
		}
		return curr;
	}
	
	/**
	 * 
	 * Function to filter an eventPath to only the events that are relevant to the transition system
	 * 
	 * @param plant
	 * @param eventPath
	 * @return
	 */
	
	private String observablePath(TransitionSystem plant, String eventPath) {
		//TODO: This, so we can get functional path length in determining the shortest paths for our heuristics
		return null;
	}
	
	//-- SB Coobservable  -------------------------------------
	
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
		
		boolean pass = true;
		
		ArrayList<String> controllable = new ArrayList<String>();
		controllable.addAll(controllableHold);
		
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
	
	public static boolean isSBCoobservableLiu(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {

		
		return false;
	}
	
	//-- Support  ---------------------------------------------
	
	public static TransitionSystem convertSoloPlantSpec(TransitionSystem plant) {
		TransitionSystem out = plant.copy();
		
		for(String s : plant.getStateNames()) {
			for(String e : plant.getStateTransitionEvents(s)) {
				if(plant.getTransitionAttribute(s, e, badTransRef)) {
					for(String t : plant.getStateEventTransitionStates(s, e)) {
						out.removeTransition(s, e, t);
					}
				}
			}
		}
		return out;
	}

	public static UStructure constructUStruct(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		ArrayList<Agent> agen = constructAgents(plant.getEventNames(), attr, agents);
		return new UStructure(plant, attr, agen);
	}
	
	//-- Helper  ----------------------------------------------

	private static TransitionSystem pickSpec(ArrayList<TransitionSystem> specs) {
		return specs.get(0);
	}
	
	private static TransitionSystem parallelComp(TransitionSystem ... in) {
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		for(TransitionSystem t : in) {
			use.add(t);
		}
		return ProcessDES.parallelComposition(use);
	}
	
	private static TransitionSystem parallelComp(ArrayList<TransitionSystem> in) {
		return ProcessDES.parallelComposition(in);
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
