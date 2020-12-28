package model.process;

import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.FSM;
import model.fsm.event.Event;
import model.fsm.state.State;
import model.fsm.transition.Transition;
import model.fsm.transition.TransitionFunction;

import java.util.ArrayList;

import java.util.HashMap;

public class UStructure {
	
	//TODO: Get subautomota representing the paths that can reach goodBad/badGood states (flip transition direction, set initial as marked, etc.)
	//TODO: Integrate bad state analysis into the construction of the U-structure to save time
	
	private static final String UNOBSERVED_EVENT = "w";
	
	private FSM plantFSM;
	private Agent[] agents;
	private HashMap<String, ArrayList<Transition>> badTransitions;
	private HashMap<State, State[]> compositeMapping;
	
	private FSM uStructure;
	private HashSet<State> goodBadStates;
	private HashSet<State> badGoodStates;
	
	public UStructure(FSM thePlant, TransitionFunction theBadTransitions, Agent ... theAgents) {
		plantFSM = thePlant;
		badTransitions = new HashMap<String, ArrayList<Transition>>();
		for(State s : theBadTransitions.getStates()) {
			ArrayList<Transition> newTrans = new ArrayList<Transition>();
			for(Transition t : theBadTransitions.getTransitions(s)) {
				newTrans.add(t);
			}
			badTransitions.put(s.getStateName(), newTrans);
		}
		for(State s : thePlant.getStates()) {
			if(badTransitions.get(s.getStateName()) == null)
				badTransitions.put(s.getStateName(), new ArrayList<Transition>());
		}
		agents = new Agent[theAgents.length + 1];
		agents[0] = new Agent(thePlant.getEventMap().getEvents().toArray(new Event[thePlant.getEventMap().getEvents().size()]));
		for(int i = 0; i < theAgents.length; i++)
			agents[i+1] = theAgents[i];
		HashSet<String> allEvents = new HashSet<String>();
		allEvents.add(UNOBSERVED_EVENT);
		for(Agent a : agents)									//Bad habits! But it's so small...
			for(Event e : a.getAgentEvents())
				allEvents.add(e.getEventName());
		for(Agent a : agents)
			for(String e : allEvents)
				if(!a.contains(e))
					a.addNonPresentEvent(e);
		createUStructure();
		findIllegalStates();
	}
	
	public UStructure(FSM thePlant, TransitionFunction theBadTransitions, ArrayList<Agent> theAgents) {
		plantFSM = thePlant;
		badTransitions = new HashMap<String, ArrayList<Transition>>();
		for(State s : theBadTransitions.getStates()) {
			ArrayList<Transition> newTrans = new ArrayList<Transition>();
			for(Transition t : theBadTransitions.getTransitions(s)) {
				newTrans.add(t);
			}
			badTransitions.put(s.getStateName(), newTrans);
		}
		for(State s : thePlant.getStates()) {
			if(badTransitions.get(s.getStateName()) == null)
				badTransitions.put(s.getStateName(), new ArrayList<Transition>());
		}
		agents = new Agent[theAgents.size() + 1];
		agents[0] = new Agent(thePlant.getEventMap().getEvents().toArray(new Event[thePlant.getEventMap().getEvents().size()]));
		for(int i = 0; i < theAgents.size(); i++)
			agents[i+1] = theAgents.get(i);
		HashSet<String> allEvents = new HashSet<String>();
		allEvents.add(UNOBSERVED_EVENT);
		for(Agent a : agents)									//Bad habits! But it's so small...
			for(Event e : a.getAgentEvents())
				allEvents.add(e.getEventName());
		for(Agent a : agents)
			for(String e : allEvents)
				if(!a.contains(e))
					a.addNonPresentEvent(e);
		createUStructure();
		findIllegalStates();
	}
	
	public void createUStructure() {
		uStructure = new FSM();
		compositeMapping = new HashMap<State, State[]>();
		LinkedList<BatchAgentStates> queue = new LinkedList<BatchAgentStates>();		//initialize
		HashSet<State> visited = new HashSet<State>();
		State[] starting = new State[agents.length];
		
		for(int i = 0; i < starting.length; i++)
			starting[i] = plantFSM.getInitialStates().get(0);
		State init = uStructure.addState(starting);									//create first state, start queue
		uStructure.addInitialState(init);
		compositeMapping.put(init, starting);
		queue.add(new BatchAgentStates(starting, uStructure.addState(starting)));
		
		while(!queue.isEmpty()) {
			BatchAgentStates stateSet = queue.poll();
			if(visited.contains(stateSet.getIdentityState()))					//access next state from queue, ensure it hasn't been processed yet
				continue;
			visited.add(stateSet.getIdentityState());
			
			HashSet<String> viableEvents = new HashSet<String>();
			for(State s : stateSet.getStates()) {
				for(Transition t : plantFSM.getTransitions().getTransitions(s))		//figure out what the legal moves are for the plant
					viableEvents.add(t.getTransitionEvent().getEventName());
			}
			
			for(String s : viableEvents) {
				boolean[] canAct = new boolean[stateSet.getStates().length];		//find out what each individual agent is able to do for the given event at the given state
				for(int i = 0; i < stateSet.getStates().length; i++) {
					if(!agents[i].getObservable(s)) {					//if the agent cannot see the event, it has to guess whether it happened
						State[] newSet = new State[stateSet.getStates().length];
						String eventName = "<";
						for(int j = 0; j < stateSet.getStates().length; j++) {
							if(i == j) {
								newSet[j] = stateSet.getStates()[j];
								for(Transition t : plantFSM.getStateTransitions(stateSet.getStates()[j])) {
									if(t.getTransitionEvent().getEventName().equals(s))
										newSet[j] = t.getTransitionStates().get(0);
								}
								eventName += s + (j + 1 < stateSet.getStates().length ? ", " : ">");
							}
							else {
								newSet[j] = stateSet.getStates()[j];
								eventName += "w" + (j + 1 < stateSet.getStates().length ? ", " : ">");
							}
						}
						
						boolean fail = false;
						for(State state : newSet)
							if(state == null)
								fail = true;
						if(!fail) {
							queue.add(new BatchAgentStates(newSet, uStructure.addState(new State(newSet))));
							uStructure.addTransition(uStructure.addState(new State(stateSet.getStates())), uStructure.getEventMap().addEvent(eventName), uStructure.addState(new State(newSet)));
							compositeMapping.put(uStructure.addState(new State(newSet)), newSet);
						}
					}
					else {
						canAct[i] = true;
					}
				}
				State[] newSet = new State[stateSet.getStates().length];
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
				for(State state : newSet)
					if(state == null)
						fail = true;
				if(!fail) {
					queue.add(new BatchAgentStates(newSet, uStructure.addState(new State(newSet))));
					uStructure.addTransition(uStructure.addState(new State(stateSet.getStates())), uStructure.getEventMap().addEvent(eventName), uStructure.addState(new State(newSet)));
					compositeMapping.put(uStructure.addState(new State(newSet)), newSet);
				}
			}
		}
	}
	
	public void createUStructureAgain() {
		uStructure = new FSM();
		compositeMapping = new HashMap<State, State[]>();
		LinkedList<BatchAgentStates> queue = new LinkedList<BatchAgentStates>();		//initialize
		HashSet<State> visited = new HashSet<State>();
		State[] starting = new State[agents.length];
		
		for(int i = 0; i < starting.length; i++)
			starting[i] = plantFSM.getInitialStates().get(0);
		State init = uStructure.addState(starting);									//create first state, start queue
		uStructure.addInitialState(init);
		compositeMapping.put(init, starting);
		queue.add(new BatchAgentStates(starting, uStructure.addState(starting)));
		
		while(!queue.isEmpty()) {
			BatchAgentStates stateSet = queue.poll();
			if(visited.contains(stateSet.getIdentityState()))					//access next state from queue, ensure it hasn't been processed yet
				continue;
			visited.add(stateSet.getIdentityState());
			
			HashSet<String> viableEvents = new HashSet<String>();
			for(State s : stateSet.getStates()) {
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
				 * Extension: Once you have all the transitions for a State, do type-one bad state analysis (when controllability becomes a factor) and type-two, for that matter
				 * 
				 */
				
				
				boolean[] agentVisible = new boolean[stateSet.getStates().length];
				agentVisible[0] = true;
				String plantVector = "<" + s;
				for(int i = 0; i < stateSet.getStates().length; i++) {
					agentVisible[i + 1] = agents[i].getObservable(s);
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
			ArrayList<String> events = agents[index].getUnobservableEvents();
			for(int i = 0; i < events.size(); i++) {
				tags.addAll(generateObservablePermutation(tags, index + 1, total + ", " + events.get(i), sight));
			}
			tags.addAll(generateObservablePermutation(tags, index + 1, total + ", " + UNOBSERVED_EVENT, sight));
			return tags;
		}
	}

	public void findIllegalStates() {
		goodBadStates = new HashSet<State>();
		badGoodStates = new HashSet<State>();
		for(State s : uStructure.getStates()) {
			for(Transition t : uStructure.getStateTransitions(s)) {
				Event e = t.getTransitionEvent();
				String[] event = e.getEventName().substring(1, e.getEventName().length()-1).split(", ");
				State[] states = compositeMapping.get(s);
				Boolean[] legality = new Boolean[states.length];
				for(int i = 0; i < states.length; i++) {
					if(agents[i].getControllable(event[i])) {
						legality[i] = true;
						for(Transition bad : badTransitions.get(states[i].getStateName())) {
							if(bad.getTransitionEvent().getEventName().equals(event[i])) {
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
						goodBadStates.add(s);
					}
					else {
						badGoodStates.add(s);
					}
				}
			}
		}
	}
	
	public FSM getUStructure() {
		return uStructure;
	}
		
	public FSM getPlantFSM() {
		return plantFSM;
	}

	public HashSet<State> getIllegalConfigOneStates(){
		return badGoodStates;
	}
	
	public HashSet<State> getIllegalConfigTwoStates(){
		return goodBadStates;
	}
}

