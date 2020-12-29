package model.process;

import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;
import model.fsm.component.Entity;
import model.fsm.component.transition.TransitionFunction;

import java.util.ArrayList;

import java.util.HashMap;

public class UStructure {
	
	//TODO: Get subautomota representing the paths that can reach goodBad/badGood states (flip transition direction, set initial as marked, etc.)
	//TODO: Integrate bad state analysis into the construction of the U-structure to save time
	
	private static final String UNOBSERVED_EVENT = "w";
	
	private TransitionSystem plantFSM;
	private Agent[] agents;
	private HashMap<String, ArrayList<String>> badTransitions;
	private HashMap<String, String[]> compositeMapping;
	
	private TransitionSystem uStructure;
	private HashSet<String> goodBadStates;
	private HashSet<String> badGoodStates;
	
	public UStructure(TransitionSystem thePlant, TransitionFunction theBadTransitions, ArrayList<Agent> theAgents) {
		plantFSM = thePlant;
		badTransitions = new HashMap<String, ArrayList<String>>();
		for(String s : theBadTransitions.getStateNames()) {
			ArrayList<String> newTrans = new ArrayList<String>();
			for(String t : theBadTransitions.getStateEvents(s)) {
				newTrans.add(t);
			}
			badTransitions.put(s, newTrans);
		}
		for(String s : thePlant.getStateNames()) {
			if(badTransitions.get(s) == null)
				badTransitions.put(s, new ArrayList<String>());
		}
		agents = new Agent[theAgents.size() + 1];
		
		agents[0] = new Agent(thePlant.getEventNames());
		for(int i = 0; i < theAgents.size(); i++)
			agents[i+1] = theAgents.get(i);
		HashSet<String> allEvents = new HashSet<String>();
		allEvents.add(UNOBSERVED_EVENT);
		for(Agent a : agents) {
			for(String e : a.getEvents()) {
				allEvents.add(e);
			}
		}
		for(Agent a : agents) {
			for(String e : allEvents) {
				if(!a.contains(e)) {
					a.addUnknownEvent(e);
				}
			}
		}
		createUStructure();
		findIllegalStates();
	}
	
	public void createUStructure() {
		uStructure = new TransitionSystem("U-struc", plantFSM.getStateAttributes(), plantFSM.getEventAttributes(), plantFSM.getTransitionAttributes());
		compositeMapping = new HashMap<String, String[]>();
		LinkedList<BatchAgentStates> queue = new LinkedList<BatchAgentStates>();		//initialize
		HashSet<String> visited = new HashSet<String>();
		String[] starting = new String[agents.length];
		String initName = "<";
		for(int i = 0; i < starting.length; i++) {
			starting[i] = plantFSM.getStatesWithAttribute(TransitionSystem.ATTRIBUTE_INITIAl).get(0);
			initName += starting[i] + (i + 1 < starting.length ? "," : "");
		}
		initName += ">";
		uStructure.addState(initName);									//create first state, start queue
		uStructure.setStateAttribute(initName, TransitionSystem.ATTRIBUTE_INITIAl, true);
		compositeMapping.put(initName, starting);
		queue.add(new BatchAgentStates(starting, initName));
		
		while(!queue.isEmpty()) {
			BatchAgentStates stateSet = queue.poll();
			if(visited.contains(stateSet.getIdentityState()))					//access next state from queue, ensure it hasn't been processed yet
				continue;
			visited.add(stateSet.getIdentityState());
			
			HashSet<String> viableEvents = new HashSet<String>();
			for(String s : stateSet.getStates()) {
				for(String e : plantFSM.getStateTransitionEvents(s)) {
					viableEvents.add(e);
				}
			}
			
			for(String s : viableEvents) {
				boolean[] canAct = new boolean[stateSet.getStates().length];		//find out what each individual agent is able to do for the given event at the given state
				for(int i = 0; i < stateSet.getStates().length; i++) {
					if(!agents[i].getEventAttribute(s, Agent.ATTRIBUTE_OBSERVABLE)) {					//if the agent cannot see the event, it has to guess whether it happened
						String[] newSet = new String[stateSet.getStates().length];
						String eventName = "<";
						for(int j = 0; j < stateSet.getStates().length; j++) {
							if(i == j) {
								newSet[j] = stateSet.getStates()[j];
								for(String t : plantFSM.getStateTransitionEvents(stateSet.getStates()[j])) {
									if(t.equals(s))
										newSet[j] = plantFSM.getStateEventTransitionStates(stateSet.getStates()[j], t).get(0);
								}
								eventName += s + (j + 1 < stateSet.getStates().length ? ", " : ">");
							}
							else {
								newSet[j] = stateSet.getStates()[j];
								eventName += "w" + (j + 1 < stateSet.getStates().length ? ", " : ">");
							}
						}
						
						boolean fail = false;
						for(String state : newSet)
							if(state == null)
								fail = true;
						if(!fail) {
							uStructure.addState(eventName);
							queue.add(new BatchAgentStates(newSet, eventName));
							uStructure.getEventMap().addEvent(eventName);
							uStructure.addTransition(stateSet.getIdentityState(), eventName, eventName);
							compositeMapping.put(eventName, newSet);
						}
					}
					else {
						canAct[i] = true;
					}
				}
				String[] newSet = new String[stateSet.getStates().length];
				String eventName = "<";
				for(int i = 0; i < canAct.length; i++) {
					if(canAct[i]) {
						eventName += s + (i + 1 < canAct.length ? ", " : ">");
						for(Transition t : plantFSM.getStateTransitions(stateSet.getStates()[i])) {
							if(t.getTransitionEvent().getEventName().equals(s)){
								newSet[i] = t.getTransitionStates().get(0);
							}
						}
					}
					else {
						eventName += "w" + (i + 1 < canAct.length ? ", " : ">");
						newSet[i] = stateSet.getStates()[i];
					}
				}
				boolean fail = false;
				for(String state : newSet)
					if(state == null)
						fail = true;
				if(!fail) {
					uStructure.addState(eventName);
					queue.add(new BatchAgentStates(newSet, eventName));
					uStructure.addTransition(stateSet.getIdentityState(), uStructure.getEventMap().addEvent(eventName), uStructure.addState(new String(newSet)));
					compositeMapping.put(uStructure.addState(new String(newSet)), newSet);
				}
			}
		}
	}
	
	public void createUStructureAgain() {
		uStructure = new TransitionSystem();
		compositeMapping = new HashMap<String, String[]>();
		LinkedList<BatchAgentStates> queue = new LinkedList<BatchAgentStates>();		//initialize
		HashSet<String> visited = new HashSet<String>();
		String[] starting = new String[agents.length];
		
		for(int i = 0; i < starting.length; i++)
			starting[i] = plantFSM.getInitialStates().get(0);
		String init = uStructure.addState(starting);									//create first state, start queue
		uStructure.addInitialState(init);
		compositeMapping.put(init, starting);
		queue.add(new BatchAgentStates(starting, uStructure.addState(starting)));
		
		while(!queue.isEmpty()) {
			BatchAgentStates stateSet = queue.poll();
			if(visited.contains(stateSet.getIdentityState()))					//access next state from queue, ensure it hasn't been processed yet
				continue;
			visited.add(stateSet.getIdentityState());
			
			HashSet<String> viableEvents = new HashSet<String>();
			for(String s : stateSet.getStates()) {
				for(Transition t : plantFSM.getTransitions().getTransitions(s))		//figure out what the legal moves are for the plant
					viableEvents.add(t.getTransitionEvent().getEventName());
			}
			
			for(String s : viableEvents) {
				/*
				 * For each event, find out which agents can perform that event; generate the base-level vector <a, w, a, w, etc.> representing the plant's action
				 * Then do the permutations on that for every possible way to guess events happening
				 * Each permutation and the base are unique transitions, find the target states to make that state vector
				 * Add the transitions to the U-Structure, add the new States to the queue
				 * 
				 * Extension: Once you have all the transitions for a String, do type-one bad state analysis (when controllability becomes a factor) and type-two, for that matter
				 * 
				 */
				
				
				boolean[] agentVisible = new boolean[stateSet.getStates().length];
				agentVisible[0] = true;
				String plantVector = "<" + s;
				for(int i = 0; i < stateSet.getStates().length; i++) {
					agentVisible[i + 1] = agents[i].getEventAttribute(Agent.ATTRIBUTE_OBSERVABLE, s);
					plantVector += ", " + (agentVisible[i + 1] ? s : UNOBSERVED_EVENT);
				}
				plantVector += ">";
				HashSet<String> eventPaths = new HashSet<String>();
				eventPaths.add(plantVector);
				generateObservablePermutation(eventPaths, 0, "", agentVisible);
				
				for(String vec : eventPaths) {
					
				}
			}
		}
	}
	
	private HashSet<String> generateObservablePermutation(HashSet<String> tags, int index,  String total, boolean[] sight){
		if(index >= sight.length) {
			tags.add(total);
			return tags;
		}
		if(sight[index]) {
			tags.addAll(generateObservablePermutation(tags, index + 1,  total + ", " + UNOBSERVED_EVENT, sight));
			return tags;
		}
		else {
			ArrayList<String> events = agents[index].getEventsAttributeSet(Agent.ATTRIBUTE_OBSERVABLE, true);
			for(int i = 0; i < events.size(); i++) {
				tags.addAll(generateObservablePermutation(tags, index + 1, total + ", " + events.get(i), sight));
			}
			tags.addAll(generateObservablePermutation(tags, index + 1, total + ", " + UNOBSERVED_EVENT, sight));
			return tags;
		}
	}

	public void findIllegalStates() {
		goodBadStates = new HashSet<String>();
		badGoodStates = new HashSet<String>();
		for(String state : uStructure.getStateNames()) {
			for(String event : uStructure.getStateTransitionEvents(state)) {
				String[] eventBrk = event.substring(1, event.length()-1).split(", ");
				String[] states = compositeMapping.get(state);
				Boolean[] legality = new Boolean[states.length];
				for(int i = 0; i < states.length; i++) {
					if(agents[i].getEventAttribute(eventBrk[i], Agent.ATTRIBUTE_CONTROLLABLE)) {
						legality[i] = true;
						for(String bad : badTransitions.get(states[i])) {
							if(bad.equals(eventBrk[i])) {
								legality[i] = false;
							}
						}
					}
					else{
						legality[i] = null;
					}
				}
				Boolean outcome;
				if(legality[0] == null) {
					outcome = null;
				}
				else {
					boolean first = legality[0];
					outcome = first;
					for(int i = 1; i < legality.length; i++)
						if(legality[i] == null || legality[i] != first)
							outcome = outcome;
						else
							outcome = null;
				}
				if(outcome != null) {
					if(outcome) {
						goodBadStates.add(state);
					}
					else {
						badGoodStates.add(state);
					}
				}
			}
		}
	}
	
	public TransitionSystem getUStructure() {
		return uStructure;
	}
		
	public TransitionSystem getPlantFSM() {
		return plantFSM;
	}

	public HashSet<String> getIllegalConfigOneStates(){
		return badGoodStates;
	}
	
	public HashSet<String> getIllegalConfigTwoStates(){
		return goodBadStates;
	}
}

