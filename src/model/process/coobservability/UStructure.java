package model.process.coobservability;

import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;

import java.util.ArrayList;
import java.util.HashMap;

public class UStructure {
	
	//TODO: Get subautomota representing the paths that can reach goodBad/badGood states (flip transition direction, set initial as marked, etc.)
	//TODO: Integrate bad state analysis into the construction of the U-structure to save time
	
//---  Constants   ----------------------------------------------------------------------------
	
	private static final String UNOBSERVED_EVENT = "~";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static String attributeInitialRef;
	private static String attributeObservableRef;
	private static String attributeControllableRef;
	private static String attributeBadRef;
	private static String attributeGoodRef;
	
	private TransitionSystem uStructure;
	private HashSet<IllegalConfig> goodBadStates;
	private HashSet<IllegalConfig> badGoodStates;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public UStructure(TransitionSystem thePlant, ArrayList<String> attr, ArrayList<Agent> theAgents) {
		HashMap<String, HashSet<String>> badTransitions = initializeBadTransitions(thePlant);
		Agent[] agents = initializeAgents(thePlant, attr, theAgents);

		goodBadStates = new HashSet<IllegalConfig>();
		badGoodStates = new HashSet<IllegalConfig>();
		createUStructure(thePlant, badTransitions, agents);
	}
	
	private HashMap<String, HashSet<String>> initializeBadTransitions(TransitionSystem thePlant){
		HashMap<String, HashSet<String>> badTransitions = new HashMap<String, HashSet<String>>();
		for(String s : thePlant.getStateNames()) {
			if(badTransitions.get(s) == null) {
				badTransitions.put(s, new HashSet<String>());
			}
			for(String e : thePlant.getStateTransitionEvents(s)) {
				if(thePlant.getTransitionAttribute(s, e, attributeBadRef)) {
					badTransitions.get(s).add(e);
				}
			}
		}
		return badTransitions;
	}
	
	private Agent[] initializeAgents(TransitionSystem thePlant, ArrayList<String> attr, ArrayList<Agent> theAgents) {
		Agent[] agents = new Agent[theAgents.size() + 1];
		
		agents[0] = new Agent(attr, thePlant.getEventNames());
		
		for(String s : attr) {
			agents[0].setAttributeTrue(s, thePlant.getEventNames());
		}
		
		for(int i = 0; i < theAgents.size(); i++) {
			agents[i+1] = theAgents.get(i);
		}
		
		HashSet<String> allEvents = new HashSet<String>();
		allEvents.add(UNOBSERVED_EVENT);
		
		for(Agent a : agents) {
			allEvents.addAll(a.getEvents());
		}
		
		for(Agent a : agents) {
			a.addUnknownEvents(allEvents);
		}
		return agents;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static void assignAttributeReferences(String init, String obs, String cont, String bad, String good) {
		attributeInitialRef = init;
		attributeObservableRef = obs;
		attributeControllableRef = cont;
		attributeBadRef = bad;
		attributeGoodRef = good;
	}
	
	private TransitionSystem initializeUStructure(TransitionSystem plant) {
		TransitionSystem out = new TransitionSystem("U-struc", plant.getStateAttributes(), plant.getEventAttributes(), plant.getTransitionAttributes());
		ArrayList<String> attr = uStructure.getStateAttributes();
		attr.add(attributeBadRef);
		attr.add(attributeGoodRef);
		uStructure.setStateAttributes(attr);
		return out;
	}
	
	public void createUStructure(TransitionSystem plant, HashMap<String, HashSet<String>> badTransitions, Agent[] agents) {
		uStructure = initializeUStructure(plant);
		
		LinkedList<AgentStates> queue = new LinkedList<AgentStates>();		//initialize queue
		HashSet<String> visited = new HashSet<String>();

		String[] starting = new String[agents.length];
		for(int i = 0; i < starting.length; i++) {
			starting[i] = plant.getStatesWithAttribute(attributeInitialRef).get(0);
		}
		AgentStates bas = new AgentStates(starting, "");
		uStructure.addState(bas.getCompositeName());									//create first state, start queue
		uStructure.setStateAttribute(bas.getCompositeName(), attributeInitialRef, true);
		queue.add(bas);
		
		while(!queue.isEmpty()) {
			AgentStates stateSet = queue.poll();
			String currState = stateSet.getCompositeName();
			
			if(visited.contains(currState))	//access next state from queue, ensure it hasn't been processed yet
				continue;
			
			String[] stateSetStates = stateSet.getStates();
			int stateSetSize = stateSetStates.length;
			visited.add(currState);
			
			
			HashSet<String> viableEvents = new HashSet<String>();
			for(String s : stateSetStates) {
				for(String e : plant.getStateTransitionEvents(s)) {
					viableEvents.add(e);
				}
			}
			
			for(String s : viableEvents) {
				
				//TODO: Pretty sure, but double check: Can we proceed with this event? If an agent is in a position that can't perform it, does it matter if they can see it?
				boolean bail = false;								//If not every agent can act on this event, skip
				for(String t : stateSetStates) {
					if(getNextState(plant, t, s) == null) {
						bail = true;
					}
				}
				if(bail) {
					continue;
				}
				
				boolean[] canAct = new boolean[stateSetSize];     //Find out what each individual agent is able to do for the given event at their given state
				for(int i = 0; i < stateSetSize; i++) {
					if(agents[i].getEventAttribute(s, attributeObservableRef)) {
						canAct[i] = true;					
					}
					else {														//if the agent cannot see the event, it guesses that it happened
						String[] newSet = new String[stateSetSize];				//can do these individually because the next state could perform other guesses, captures all permutations
						String eventName = "<";
						for(int j = 0; j < stateSetSize; j++) {
							if(i != j) {
								newSet[j] = stateSetStates[j];
								eventName += UNOBSERVED_EVENT + (j + 1 < stateSetSize ? ", " : ">");
							}
							else {
								newSet[j] = getNextState(plant, stateSetStates[j], s);
								eventName += s + (j + 1 < stateSetSize ? ", " : ">");
							}
						}

						queue.add(handleNewTransition(newSet, eventName, currState, stateSet.getEventPath()));	//adds the guess transition to our queue
					}
				}
				
				String[] newSet = new String[stateSetSize];				//Knowing visibility, we make the actual event occurrence
				String eventName = "<";
				for(int i = 0; i < stateSetSize; i++) {
					if(canAct[i]) {
						eventName += s + (i + 1 < canAct.length ? ", " : ">");
						newSet[i] = getNextState(plant, stateSetStates[i], s);
					}
					else {
						eventName += UNOBSERVED_EVENT + (i + 1 < canAct.length ? ", " : ">");
						newSet[i] = stateSetStates[i];
					}
				}
				
				queue.add(handleNewTransition(newSet, eventName, currState, stateSet.getEventPath() + s));		//adds the real event occurring transition to our queue
				
				if(plant.getEventAttribute(s, attributeControllableRef)) {			//If the event is controllable, does it violate co-observabilty?
					Boolean result = badTransitions.get(stateSetStates[0]).contains(s);
					for(int i = 1; i < stateSetStates.length; i++) {
						if(agents[i].getEventAttribute(s, attributeControllableRef)) {
							if(badTransitions.get(stateSetStates[i]).contains(s) == result || plant.getStateEventTransitionStates(stateSetStates[i], s) == null) {
								result = null;
								break;
							}
						}
					}
					if(result != null) {
					  uStructure.setStateAttribute(currState, !result ? attributeGoodRef : attributeBadRef, true);
					  if(result) {
						  goodBadStates.add(new IllegalConfig(stateSet, s));
					  }
					  else {
						  badGoodStates.add(new IllegalConfig(stateSet, s));
					  }
					}
				}
			}
		}
		uStructure.setEventAttributes(new ArrayList<String>());
	}
	
	private String getNextState(TransitionSystem plant, String currState, String event) {
		for(String t : plant.getStateTransitionEvents(currState)) {
			if(t.equals(event)){
				return plant.getStateEventTransitionStates(currState, t).get(0);
			}
		}
		return null;
	}
	
	private AgentStates handleNewTransition(String[] newSet, String eventName, String currState, String newPath) {
		AgentStates next = new AgentStates(newSet, newPath);
		uStructure.addEvent(eventName);
		uStructure.addState(next.getCompositeName());
		uStructure.addTransition(currState, eventName, next.getCompositeName());
		return next;
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	public TransitionSystem getUStructure() {
		return uStructure;
	}
		
	public HashSet<IllegalConfig> getIllegalConfigOneStates(){
		return badGoodStates;
	}
	
	public HashSet<IllegalConfig> getIllegalConfigTwoStates(){
		return goodBadStates;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	/*
	
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
			ArrayList<String> events = agents[index].getEventsAttributeSet(attributeObservableRef, true);
			for(int i = 0; i < events.size(); i++) {
				tags.addAll(generateObservablePermutation(tags, index + 1, total + ", " + events.get(i), sight));
			}
			tags.addAll(generateObservablePermutation(tags, index + 1, total + ", " + UNOBSERVED_EVENT, sight));
			return tags;
		}
	}
	
	*/

}

