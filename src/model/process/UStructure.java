package model.process;

import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;

import java.util.ArrayList;
import java.util.HashMap;

public class UStructure {
	
	//TODO: Get subautomota representing the paths that can reach goodBad/badGood states (flip transition direction, set initial as marked, etc.)
	//TODO: Integrate bad state analysis into the construction of the U-structure to save time
	
//---  Constants   ----------------------------------------------------------------------------
	
	private static final String UNOBSERVED_EVENT = "w";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static String attributeInitialRef;
	private static String attributeObservableRef;
	private static String attributeControllableRef;
	
	private TransitionSystem plantFSM;
	private Agent[] agents;
	private HashMap<String, HashSet<String>> badTransitions;
	private HashMap<String, String[]> compositeMapping;
	
	private TransitionSystem uStructure;
	private HashSet<String> goodBadStates;
	private HashSet<String> badGoodStates;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public UStructure(TransitionSystem thePlant, ArrayList<String> attr, HashMap<String, HashSet<String>> theBadTransitions, ArrayList<Agent> theAgents) {
		plantFSM = thePlant;
		badTransitions = theBadTransitions;
		for(String s : thePlant.getStateNames()) {
			if(badTransitions.get(s) == null)
				badTransitions.put(s, new HashSet<String>());
		}
		agents = new Agent[theAgents.size() + 1];
		
		agents[0] = new Agent(attr, thePlant.getEventNames());
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
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static void assignAttributeReferences(String init, String obs, String cont) {
		attributeInitialRef = init;
		attributeObservableRef = obs;
		attributeControllableRef = cont;
	}
	
	public void createUStructure() {
		uStructure = new TransitionSystem("U-struc", plantFSM.getStateAttributes(), plantFSM.getEventAttributes(), plantFSM.getTransitionAttributes());
		compositeMapping = new HashMap<String, String[]>();
		LinkedList<BatchAgentStates> queue = new LinkedList<BatchAgentStates>();		//initialize
		HashSet<String> visited = new HashSet<String>();
		String[] starting = new String[agents.length];
		String initName = "<";
		for(int i = 0; i < starting.length; i++) {
			starting[i] = plantFSM.getStatesWithAttribute(attributeInitialRef).get(0);
			initName += starting[i] + (i + 1 < starting.length ? "," : "");
		}
		initName += ">";
		uStructure.addState(initName);									//create first state, start queue
		uStructure.setStateAttribute(initName, attributeInitialRef, true);
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
					if(!agents[i].getEventAttribute(s, attributeObservableRef)) {					//if the agent cannot see the event, it has to guess whether it happened
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
							ArrayList<String> sta = new ArrayList<String>();
							for(String q : newSet) {
								sta.add(q);
							}
							String newName = uStructure.compileStateName(sta);
							queue.add(new BatchAgentStates(newSet, newName));
							uStructure.addEvent(eventName);
							uStructure.addState(newName);
							uStructure.addTransition(stateSet.getIdentityState(), eventName, newName);
							compositeMapping.put(newName, newSet);
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
						for(String t : plantFSM.getStateTransitionEvents(stateSet.getStates()[i])) {
							if(t.equals(s)){
								newSet[i] = plantFSM.getStateEventTransitionStates(stateSet.getStates()[i], t).get(0);
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
					ArrayList<String> sta = new ArrayList<String>();
					for(String q : newSet) {
						sta.add(q);
					}
					String newName = uStructure.compileStateName(sta);
					queue.add(new BatchAgentStates(newSet, newName));
					uStructure.addEvent(eventName);
					uStructure.addState(newName);
					uStructure.addTransition(stateSet.getIdentityState(), eventName, newName);
					compositeMapping.put(newName, newSet);
				}
			}
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
					if(agents[i].getEventAttribute(eventBrk[i], attributeControllableRef)) {
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
						if(legality[i] != null && legality[i] == first)
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

//---  Getter Methods   -----------------------------------------------------------------------
	
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
	
//---  Support Methods   ----------------------------------------------------------------------
	
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

}

