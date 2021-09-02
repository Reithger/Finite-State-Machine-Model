package model.process.coobservability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;

public class ProcessCoobservability {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static String UNOBSERVABLE_EVENT = "~";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static String controllableRef;
	private static String observableRef;
	private static String initialRef;
	private static String markedRef;
	
//---  Meta   ---------------------------------------------------------------------------------
	
	public static void assignAttributes(String cont, String obs, String init, String mark) {
		controllableRef = cont;
		observableRef = obs;
		initialRef = init;
		markedRef = mark;
	}

	public boolean isCoobservableUStruct(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean enableByDefault) {
		ArrayList<Agent> agen = constructAgents(plant.getEventNames(), attr, agents);
		
		UStructure ustr = new UStructure(plant, attr, agen);
		HashSet<String> badGood = enableByDefault ? ustr.getIllegalConfigOneStates() : ustr.getIllegalConfigTwoStates();
		return badGood.isEmpty();
	}
	
	public boolean isSBCoobservableUrvashi(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
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
			
			TransitionSystem product = constructProductHiding(plants, specifications, observable);
			HashMap<String, HashSet<StateSet>> tempDisable = subsetConstruction(product, enable, disable, observable, controllable);
			
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
	
	public boolean isSBCoobservableUrvashi(TransitionSystem plant, TransitionSystem specification, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		ArrayList<Agent> agen = constructAgents(plant.getEventNames(), attr, agents);
		
		HashMap<String, HashSet<StateSet>> disable = new HashMap<String, HashSet<StateSet>>();
		HashMap<String, HashSet<StateSet>> enable = new HashMap<String, HashSet<StateSet>>();
		
		initializeEnableDisable(disable, enable, plant, specification);
		
		boolean pass = true;
		
		ArrayList<String> controllable = plant.getEventsWithAttribute(controllableRef);
		
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
			
			TransitionSystem product = constructProductHiding(plant, specification, observable);
			HashMap<String, HashSet<StateSet>> tempDisable = subsetConstruction(product, enable, disable, observable, controllable);
			
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

	public boolean isCoobservableLiu() {

		return false;
	}
	
	public boolean isSBCoobservableLiu() {

		return false;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	//-- Urvashi  ---------------------------------------------
	
	private void initializeEnableDisable(HashMap<String, HashSet<StateSet>> disable, HashMap<String, HashSet<StateSet>> enable, ArrayList<TransitionSystem> plant, ArrayList<TransitionSystem> specification) {
		
	}
	
	private HashMap<String, HashSet<StateSet>> subsetConstructHiding(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, HashMap<String, HashSet<StateSet>> enable, HashMap<String, HashSet<StateSet>> disable, ArrayList<String> agentObs, ArrayList<String> agentCont){
		HashMap<String, HashSet<StateSet>> out = new HashMap<String, HashSet<StateSet>>();
		
		
		return out;
	}
	
	private void initializeEnableDisable(HashMap<String, HashSet<StateSet>> disable, HashMap<String, HashSet<StateSet>> enable, TransitionSystem plant, TransitionSystem specification) {
		ArrayList<String> controllable = plant.getEventsWithAttribute(controllableRef);
		
		for(String o : controllable) {
			disable.put(o, new HashSet<StateSet>());
			enable.put(o, new HashSet<StateSet>());
		}
		
		LinkedList<StateSet> queue = new LinkedList<StateSet>();
		HashSet<StateSet> visited = new HashSet<StateSet>();
		
		queue.add(new StateSet(plant.getStatesWithAttribute(initialRef).get(0), specification.getStatesWithAttribute(initialRef).get(0)));
		
		while(!queue.isEmpty()) {
			StateSet curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			
			String plantState = curr.getPlantState();
			String specState = curr.getSpecState();
			
			for(String e : plant.getStateTransitionEvents(plantState)) {
				boolean isCont = plant.getEventAttribute(e, controllableRef);
				if(!specification.getStateTransitionEvents(specState).contains(e)) {
					if(isCont) {
						disable.get(e).add(curr);
					}
				}
				else {
					if(isCont) {
						enable.get(e).add(curr);
					}
					String nextPlantState = plant.getStateEventTransitionStates(plantState, e).get(0);
					String nextSpecState = specification.getStateEventTransitionStates(specState, e).get(0);
					queue.add(new StateSet(nextPlantState, nextSpecState));
				}
			}
			
		}
		
		
	}

	private HashMap<String, HashSet<StateSet>> subsetConstruction(TransitionSystem prod, HashMap<String, HashSet<StateSet>> enable, HashMap<String, HashSet<StateSet>> disable, ArrayList<String> agentObs, ArrayList<String> agentCont){
		HashMap<String, HashSet<StateSet>> out = new HashMap<String, HashSet<StateSet>>();
		
		ArrayList<String> controllable = prod.getEventsWithAttribute(controllableRef);
		for(String c : controllable) {
			if(agentCont.contains(c)) {
				out.put(c, new HashSet<StateSet>());
			}
			else {
				out.put(c, disable.get(c));
			}
		}
		
		LinkedList<HashSet<String>> queue = new LinkedList<HashSet<String>>();
		HashSet<HashSet<String>> visited = new HashSet<HashSet<String>>();
		
		HashSet<HashSet<String>> glompChecked = new HashSet<HashSet<String>>();
		
		HashSet<HashSet<String>> generatedStateSet = new HashSet<HashSet<String>>();
		
		HashSet<String> init = new HashSet<String>();
		init.add(prod.getStatesWithAttribute(initialRef).get(0));
		queue.add(init);
		
		while(!queue.isEmpty()) {
			HashSet<String> curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			
			HashSet<String> conglom = new HashSet<String>();
			LinkedList<String> dwindle = new LinkedList<String>();
			conglom.addAll(curr);
			dwindle.addAll(curr);
			while(!dwindle.isEmpty()) {
				String state = dwindle.poll();
				ArrayList<String> target = prod.getStateEventTransitionStates(state, UNOBSERVABLE_EVENT);
				if(!target.isEmpty()) {
					for(String s : target) {
						if(!conglom.contains(s)) {
							conglom.add(s);
							dwindle.add(s);
						}
					}
				}
			}
			if(!glompChecked.contains(conglom)) {
				for(String c : controllable) {
					if(intersectionCheck(conglom, enable.get(c)) && intersectionCheck(conglom, disable.get(c)) && agentCont.contains(c)) {
						out.get(c).addAll(intersection(conglom, disable.get(c)));
					}
				}
				glompChecked.add(conglom);
				for(String o : agentObs) {
					HashSet<String> targetStates = new HashSet<String>();
					for(String s : conglom) {
						ArrayList<String> target = prod.getStateEventTransitionStates(s, o);
						if(!target.isEmpty()) {
							targetStates.add(target.get(0));
						}
					}
					if(!generatedStateSet.contains(targetStates)) {
						queue.add(targetStates);
						generatedStateSet.add(targetStates);
					}
				}
			}
		}
		
		return out;
	}

	private TransitionSystem constructProductHiding(TransitionSystem plant, TransitionSystem spec, ArrayList<String> agentObs) {
		TransitionSystem out = new TransitionSystem("Product " + plant.getId() + " - " + spec.getId(), plant.getStateAttributes(), plant.getEventAttributes(), plant.getTransitionAttributes());
		
		out.setFSMEventMap(plant.getEventMap());
		
		StateSet init = new StateSet(plant.getStatesWithAttribute(initialRef).get(0), spec.getStatesWithAttribute(initialRef).get(0));
		out.addState(init.getPairName());
		out.addStateComposition(init.getPairName(), init.getListForm());
		
		LinkedList<StateSet> queue = new LinkedList<StateSet>();
		queue.add(init);
		HashSet<StateSet> visited = new HashSet<StateSet>();
		
		while(!queue.isEmpty()) {
			StateSet curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			
			String pairName = curr.getPairName();
			String plantState = curr.getPlantState();
			String specState = curr.getSpecState();
			
			if(plant.getStateAttribute(plantState, markedRef) && spec.getStateAttribute(specState, markedRef)) {
				out.setStateAttribute(pairName, markedRef, true);
			}
			boolean added = false;
			for(String e : plant.getStateTransitionEvents(plantState)) {
				if(spec.getStateEventTransitionStates(specState, e).isEmpty()) {
					continue;
				}
				String newPlantState = plant.getStateEventTransitionStates(plantState, e).get(0);
				String newSpecState = spec.getStateEventTransitionStates(specState, e).get(0);
				StateSet newState = new StateSet(newPlantState, newSpecState);
				out.addState(newState.getPairName());
				out.addStateComposition(newState.getPairName(), newState.getListForm());
				if(agentObs.contains(e)) {
					out.addTransition(curr.getPairName(), e, newState.getPairName());
				}
				else {
					out.addTransition(curr.getPairName(), UNOBSERVABLE_EVENT, newState.getPairName());
				}
				if(!added) {
					queue.add(newState);
					added = true;
				}
			}
			
		}
		
		
		return out;
	}

	//-- Other  -----------------------------------------------
	
	private HashSet<StateSet> intersection(HashSet<String> conglom, HashSet<StateSet> check){
		HashSet<StateSet> out = new HashSet<StateSet>();
		for(StateSet s : check) {
			if(conglom.contains(s.getPairName())) {
				out.add(s);
			}
		}
		return out;
	}
	
	private boolean intersectionCheck(HashSet<String> conglom, HashSet<StateSet> check) {
		for(StateSet s : check) {
			if(conglom.contains(s.getPairName())) {
				return true;
			}
		}
		return false;
	}
	
	private ArrayList<Agent> constructAgents(ArrayList<String> event, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents){
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
