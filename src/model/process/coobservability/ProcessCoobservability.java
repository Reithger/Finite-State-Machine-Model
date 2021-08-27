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
	
	public boolean isSBCoobservableUrvashi(TransitionSystem plant, TransitionSystem specification, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		ArrayList<Agent> agen = constructAgents(plant.getEventNames(), attr, agents);
		
		HashMap<String, HashSet<StatePair>> disable = new HashMap<String, HashSet<StatePair>>();
		HashMap<String, HashSet<StatePair>> enable = new HashMap<String, HashSet<StatePair>>();
		
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
			HashMap<String, HashSet<StatePair>> tempDisable = subsetConstruction(product, enable, disable, observable, controllable);
			
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
	
	private HashMap<String, HashSet<StatePair>> subsetConstruction(TransitionSystem prod, HashMap<String, HashSet<StatePair>> enable, HashMap<String, HashSet<StatePair>> disable, ArrayList<String> agentObs, ArrayList<String> agentCont){
		HashMap<String, HashSet<StatePair>> out = new HashMap<String, HashSet<StatePair>>();
		
		ArrayList<String> controllable = prod.getEventsWithAttribute(controllableRef);
		for(String c : controllable) {
			if(agentCont.contains(c)) {
				out.put(c, new HashSet<StatePair>());
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
	
	private HashSet<StatePair> intersection(HashSet<String> conglom, HashSet<StatePair> check){
		HashSet<StatePair> out = new HashSet<StatePair>();
		for(StatePair s : check) {
			if(conglom.contains(s.getPairName())) {
				out.add(s);
			}
		}
		return out;
	}
	
	private boolean intersectionCheck(HashSet<String> conglom, HashSet<StatePair> check) {
		for(StatePair s : check) {
			if(conglom.contains(s.getPairName())) {
				return true;
			}
		}
		return false;
	}
	
	private TransitionSystem constructProductHiding(TransitionSystem plant, TransitionSystem spec, ArrayList<String> agentObs) {
		TransitionSystem out = new TransitionSystem("Product " + plant.getId() + " - " + spec.getId(), plant.getStateAttributes(), plant.getEventAttributes(), plant.getTransitionAttributes());
		
		out.setFSMEventMap(plant.getEventMap());
		
		StatePair init = new StatePair(plant.getStatesWithAttribute(initialRef).get(0), spec.getStatesWithAttribute(initialRef).get(0));
		out.addState(init.getPairName());
		out.addStateComposition(init.getPairName(), init.getListForm());
		
		LinkedList<StatePair> queue = new LinkedList<StatePair>();
		queue.add(init);
		HashSet<StatePair> visited = new HashSet<StatePair>();
		
		while(!queue.isEmpty()) {
			StatePair curr = queue.poll();
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
				StatePair newState = new StatePair(newPlantState, newSpecState);
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
	
	private void initializeEnableDisable(HashMap<String, HashSet<StatePair>> disable, HashMap<String, HashSet<StatePair>> enable, TransitionSystem plant, TransitionSystem specification) {
		ArrayList<String> controllable = plant.getEventsWithAttribute(controllableRef);
		
		for(String o : controllable) {
			disable.put(o, new HashSet<StatePair>());
			enable.put(o, new HashSet<StatePair>());
		}
		
		LinkedList<StatePair> queue = new LinkedList<StatePair>();
		HashSet<StatePair> visited = new HashSet<StatePair>();
		
		queue.add(new StatePair(plant.getStatesWithAttribute(initialRef).get(0), specification.getStatesWithAttribute(initialRef).get(0)));
		
		while(!queue.isEmpty()) {
			StatePair curr = queue.poll();
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
					queue.add(new StatePair(nextPlantState, nextSpecState));
				}
			}
			
		}
		
		
	}
	
	class StatePair{
		
		String plant;
		
		String spec;
		
		public StatePair(String p, String s) {
			plant = p;
			spec = s;
		}
		
		public StatePair(String nom) {
			nom.substring(1, nom.length()-1);
			String[] use = nom.split(", ");
			plant = use[0];
			spec = use[1];
		}
		
		public String getPlantState() {
			return plant;
		}
		
		public String getSpecState() {
			return spec;
		}
		
		public String getPairName() {
			return "(" + plant + ", " + spec + ")";
		}
		
		public ArrayList<String> getListForm(){
			ArrayList<String> out = new ArrayList<String>();
			out.add(plant);
			out.add(spec);
			return out;
		}
		
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
