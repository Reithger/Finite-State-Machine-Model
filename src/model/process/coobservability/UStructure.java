package model.process.coobservability;

import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;
import model.process.coobservability.support.Agent;
import model.process.coobservability.support.AgentStates;
import model.process.coobservability.support.CrushIdentityGroup;
import model.process.coobservability.support.CrushMap;
import model.process.coobservability.support.IllegalConfig;
import model.process.memory.UStructMemoryMeasure;

import java.util.ArrayList;
import java.util.HashMap;

public class UStructure extends UStructMemoryMeasure{
	
//---  Constants   ----------------------------------------------------------------------------
	
	private static final String UNOBSERVED_EVENT = "~";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	protected static String attributeInitialRef;
	protected static String attributeObservableRef;
	protected static String attributeControllableRef;
	protected static String attributeBadRef;
	protected static String attributeGoodRef;
	
	private TransitionSystem uStructure;
	private HashSet<IllegalConfig> goodBadStates;
	private HashSet<IllegalConfig> badGoodStates;
	
	private HashMap<String, AgentStates> objectMap;
	private HashMap<String, String[]> eventNameMap;
	
	private CrushMap[] crushMap;
	
	private Agent[] agents;
	
	private static boolean endAtFirstCounterexample;
	
//---  Constructors   -------------------------------------------------------------------------

	public UStructure(TransitionSystem thePlant, ArrayList<String> attr, ArrayList<Agent> theAgents) throws Exception{
		super();
		HashMap<String, HashSet<String>> badTransitions = initializeBadTransitions(thePlant);
		agents = initializeAgents(thePlant, attr, theAgents);
		crushMap = new CrushMap[agents.length];

		goodBadStates = new HashSet<IllegalConfig>();
		badGoodStates = new HashSet<IllegalConfig>();
		objectMap = new HashMap<String, AgentStates>();
		eventNameMap = new HashMap<String, String[]>();
		createUStructure(thePlant, badTransitions, agents, endAtFirstCounterexample);
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
		
		agents[0] = new Agent(thePlant.getEventMap());
		
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

//---  Static Assignments   -------------------------------------------------------------------

	public static void assignAttributeReferences(String init, String obs, String cont, String bad, String good) {
		attributeInitialRef = init;
		attributeObservableRef = obs;
		attributeControllableRef = cont;
		attributeBadRef = bad;
		attributeGoodRef = good;
	}
	
	public static void setEndAtFirstCounterexample(boolean in) {
		endAtFirstCounterexample = in;
	}
	
//---  Operations   ---------------------------------------------------------------------------

	public void createUStructure(TransitionSystem plant, HashMap<String, HashSet<String>> badTransitions, Agent[] agents, boolean endAtFirstCounterexample) throws Exception{
		uStructure = initializeUStructure(plant);
		uStructure.setId("U-Struct - " + plant.getId());
		
		//System.out.println("---" + uStructure.getId());
		
		LinkedList<AgentStates> queue = new LinkedList<AgentStates>();		//initialize queue
		HashSet<String> visited = new HashSet<String>();
		
		String[] starting = new String[agents.length];
		for(int i = 0; i < starting.length; i++) {
			starting[i] = plant.getStatesWithAttribute(attributeInitialRef).get(0);
		}
		AgentStates bas = new AgentStates(starting, new ArrayList<String>());
		objectMap.put(bas.getCompositeName(), bas);
		uStructure.addState(bas.getCompositeName());									//create first state, start queue
		uStructure.setStateAttribute(bas.getCompositeName(), attributeInitialRef, true);
		queue.add(bas);
		
		// Experiment with whether it matters that a system thinks an event is controllable or not
		HashSet<String> controllable = new HashSet<String>();
		for(Agent a : agents) {
			controllable.addAll(a.getEventsAttributeSet(attributeControllableRef, true));
		}
		
		while(!queue.isEmpty()) {
			AgentStates stateSet = queue.poll();
			String currState = stateSet.getCompositeName();

			if(visited.contains(currState)) {	//access next state from queue, ensure it hasn't been processed yet
				continue;
			}
			
			logMemoryUsage();
			
			String[] stateSetStates = stateSet.getStates();
			int stateSetSize = stateSetStates.length;
			visited.add(currState);
			
			
			HashSet<String> viableEvents = new HashSet<String>();
			for(String s : stateSetStates) {
				for(String e : plant.getStateTransitionEvents(s)) {
					viableEvents.add(e);
				}
			}
			
			//System.out.println("------" + currState);
			
			for(String s : viableEvents) {
				//System.out.println("---------" + s);
				boolean bail = false;								//If not every agent who can see it can act on this event, skip the actual transition
				for(int i = 0; i < stateSetStates.length; i++) {	//Still have to guess about it though
					String t = stateSetStates[i];
					if(getNextState(plant, t, s) == null && agents[i].getEventAttribute(s, attributeObservableRef)) {
						bail = true;
					}
				}
				
				boolean[] canAct = new boolean[stateSetSize];     //Find out what each individual agent is able to do for the given event at their given state
				for(int i = 0; i < stateSetSize; i++) {
					//System.out.println("------------" + i + " " + agents[i].getEventAttribute(s, attributeObservableRef));
					if(agents[i].getEventAttribute(s, attributeObservableRef)) {
					//if(getNextState(plant, stateSetStates[i], s) != null) {
						canAct[i] = true;					
					}
					else if(getNextState(plant, stateSetStates[i], s) != null){		//if the agent cannot see the event, it guesses that it happened
						String[] newSet = new String[stateSetSize];				//can do these individually because the next state could perform other guesses, captures all permutations
						String[] eventName = new String[stateSetSize];
						for(int j = 0; j < stateSetSize; j++) {
							if(i != j) {
								newSet[j] = stateSetStates[j];
								eventName[j] = null;
							}
							else {
								newSet[j] = getNextState(plant, stateSetStates[j], s);
								eventName[j] = s;
							}
						}
						
						//This condition may not be needed anymore, was having all-null issues before
						if(!isMeaninglessTransition(stateSetStates, eventName, badTransitions)) {
							AgentStates aS = stateSet.deriveChild(newSet, i, s);
							handleNewTransition(aS, eventName, currState);
							queue.add(aS);	//adds the guess transition to our queue
						}

					}
				}
				
				if(bail) {
					continue;
				}
				
				String[] newSet = new String[stateSetSize];				//Knowing visibility, we make the actual event occurrence
				String[] eventName = new String[stateSetSize];
				for(int i = 0; i < stateSetSize; i++) {
					if(canAct[i]) {
						eventName[i] = s;
						newSet[i] = getNextState(plant, stateSetStates[i], s);
					}
					else {
						eventName[i] = null;
						newSet[i] = stateSetStates[i];
					}
				}
				
				if(!isMeaninglessTransition(stateSetStates, eventName, badTransitions)) {
					AgentStates aS = stateSet.deriveChild(newSet, canAct, s);
					handleNewTransition(aS, eventName, currState);
					queue.add(aS);		//adds the real event occurring transition to our queue
				}
				
				if(controllable.contains(s) || badTransitions.get(stateSetStates[0]).contains(s)) {
				//if() {						//If the event is controllable, does it violate co-observabilty?
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
					  ArrayList<ArrayList<String>> observed = new  ArrayList<ArrayList<String>>();
					  for(int i = 0; i < agents.length-1; i++) {
						  observed.add(stateSet.getObservedPath(i+1));
					  }
					  if(result) {
						  goodBadStates.add(new IllegalConfig(stateSet, observed, s));	//result == true means it was a bad transition (don't enable)
					  }
					  else {
						  badGoodStates.add(new IllegalConfig(stateSet, observed, s));
					  }
					  if(endAtFirstCounterexample) {
							assignStateSize(uStructure.getStateNames().size());
							assignTransitionSize(uStructure.getNumberTransitions());
							
							uStructure.overwriteEventAttributes(new ArrayList<String>());
							return;
					  }
					}
				}
			}
		}
		
		assignStateSize(uStructure.getStateNames().size());
		assignTransitionSize(uStructure.getNumberTransitions());
		
		uStructure.overwriteEventAttributes(new ArrayList<String>());		//Not sure why, probably to avoid weird stuff on the output graph?
	}
	
	private TransitionSystem initializeUStructure(TransitionSystem plant) {
		TransitionSystem out = new TransitionSystem("U-struc", copy(plant.getStateAttributes()), copy(plant.getEventAttributes()), copy(plant.getTransitionAttributes()));
		ArrayList<String> attr = out.getStateAttributes();
		attr.add(attributeBadRef);
		attr.add(attributeGoodRef);
		out.setStateAttributes(attr);
		return out;
	}

	private void calculateCrush(boolean display) throws Exception{
		//TODO: For actual calculation, we ignore the plant crush map, so need a way to reduce work when wanting result but have it available when wanting the print out
		for(int i = display ? 0 : 1; i < agents.length; i++) {
			Agent age = agents[i];
			HashSet<String> init = getReachableStates(uStructure.getStatesWithAttribute(attributeInitialRef).get(0), age, i);
			if(crushMap[i] != null) {
				continue;
			}
			crushMap[i] = new CrushMap();
			
			//TODO: Probably just need these HashSet<String> object to incorporate the event and state set that led to them
			
			LinkedList<CrushIdentityGroup> queue = new LinkedList<CrushIdentityGroup>();
			queue.add(new CrushIdentityGroup(null, null, init));
			HashSet<CrushIdentityGroup> visited = new HashSet<CrushIdentityGroup>();
			while(!queue.isEmpty()) {
				CrushIdentityGroup curr = queue.poll();
				
				if(visited.contains(curr)) {
					continue;
				}
				
				logMemoryUsage();
				
				visited.add(curr);
				
				for(String s : getPossibleVisibleEvents(curr.getGroup(), age, i)) {
					HashSet<String> reachable = new HashSet<String>();
					for(String t : getTargetStates(curr.getGroup(), s, age, i)) {
						reachable.addAll(getReachableStates(t, age, i));
					}
					queue.add(new CrushIdentityGroup(curr, s, reachable));
				}
			}

			int count = 0;
			for(CrushIdentityGroup group : visited) {
				for(String s : group.getGroup()) {
					crushMap[i].assignStateGroup(s, count);
				}
				logStateGroupSize(group.getSize());
				count++;
			}
			logAgentGroupSize(count);
		}
	}

	public String printOutCrushMaps(boolean pointOut) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < crushMap.length; i++) {
			CrushMap cm = crushMap[i];
			sb.append("Agent " + i + " Crush Map Info\n");
			if(cm == null) {
				continue;
			}
			ArrayList<String> important = new ArrayList<String>();
			
			if(pointOut) {
				for(IllegalConfig ic : goodBadStates) {
					important.add(ic.getStateSet().getCompositeName());
				}
				for(IllegalConfig ic : badGoodStates) {
					important.add(ic.getStateSet().getCompositeName());
				}
			}
			
			sb.append(cm.getOutput(important) + "\n");
		}
		return sb.toString();
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	public HashSet<IllegalConfig> getFilteredIllegalConfigStates() throws Exception{
		calculateCrush(false);
		HashSet<IllegalConfig> typeOne = new HashSet<IllegalConfig>();
		typeOne.addAll(getIllegalConfigOneStates());
		HashSet<IllegalConfig> typeTwo = new HashSet<IllegalConfig>();
		typeTwo.addAll(getIllegalConfigTwoStates());
		
		filterGroups(typeOne, typeTwo);
		
		if(typeTwo.isEmpty())
			return typeTwo;
		
		filterGroups(typeTwo, typeOne);
		typeOne.addAll(typeTwo);
		return typeOne;
		
	}
	
	public TransitionSystem getUStructure() {
		return uStructure;
	}
	
	public ArrayList<TransitionSystem> getCrushUStructures() throws Exception{
		ArrayList<TransitionSystem> out = new ArrayList<TransitionSystem>();

		calculateCrush(true);
		
		for(int i = 0; i < crushMap.length; i++) {
			TransitionSystem local = uStructure.copy();
			local.setId(uStructure.getId() + (i == 0 ? " - Plant Observer" : " - Observer " + i));
			for(String s : local.getStateNames()) {
				for(int j : crushMap[i].getStateMemberships(s)) {
					local.addAttributeToState(s, j+"", true);
				}
			}
			out.add(local);
		}
		
		return out;
	}
		
	public HashSet<IllegalConfig> getIllegalConfigOneStates(){
		return badGoodStates;
	}
	
	public HashSet<IllegalConfig> getIllegalConfigTwoStates(){
		return goodBadStates;
	}
	
	public CrushMap[] getCrushMappings() throws Exception{
		calculateCrush(true);
		return crushMap;
	}

	private String getNextState(TransitionSystem plant, String currState, String event) {
		for(String t : plant.getStateTransitionEvents(currState)) {
			if(t.equals(event)){
				return plant.getStateEventTransitionStates(currState, t).get(0);
			}
		}
		return null;
	}
	
	private String getState(TransitionSystem plant, ArrayList<String> eventPath) {
		String curr = plant.getStatesWithAttribute(attributeInitialRef).get(0);
		for(String s : eventPath) {
			ArrayList<String> next = plant.getStateEventTransitionStates(curr, s);
			if(next != null && next.size() > 0) {
				curr = next.get(0);
			}
			else {
				return null;
			}
		}
		return curr;
	}
	
	//-- Crush  -----------------------------------------------
	
	private HashSet<String> getTargetStates(HashSet<String> states, String event, Agent age, int index){
		HashSet<String> out = new HashSet<String>();
		
		for(String s : states) {
			for(String e : uStructure.getStateTransitionEvents(s)) {
				String actual = eventNameMap.get(e)[index];
				if(event.equals(actual)) {
					out.add(uStructure.getStateEventTransitionStates(s, e).get(0));
				}
			}
		}
		return out;
	}
	
	private HashSet<String> getPossibleVisibleEvents(HashSet<String> states, Agent age, int index){
		HashSet<String> out = new HashSet<String>();
		
		for(String s : states) {
			for(String e : uStructure.getStateTransitionEvents(s)) {
				String actual = eventNameMap.get(e)[index];
				if(actual != null && age.getEventAttribute(actual, attributeObservableRef)) {
					out.add(actual);
				}
			}
		}
		return out;
	}
	
	private HashSet<String> getReachableStates(String start, Agent age, int index){
		LinkedList<String> queue = new LinkedList<String>();
		HashSet<String> visited = new HashSet<String>();
		queue.add(start);
		
		while(!queue.isEmpty()) {
			String curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			
			for(String s : uStructure.getStateTransitionEvents(curr)) {
				String actualEvent = eventNameMap.get(s)[index];
				if(actualEvent == null || !age.getEventAttribute(actualEvent, attributeObservableRef)) {
					queue.add(uStructure.getStateEventTransitionStates(curr, s).get(0));
				}
			}
		}
		return visited;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private ArrayList<String> copy(ArrayList<String> in){
		ArrayList<String> out = new ArrayList<String>();
		for(String s : in) {
			out.add(s);
		}
		return out;
	}
	
	private boolean isMeaninglessTransition(String[] stateSetStates, String[] events, HashMap<String, HashSet<String>> badTransitions) {
		
		//First, did we somehow get an event sequence that doesn't have any events in it? Also gets the relevant event.
		String event = null;
		for(String s : events) {
			if(s != null) {
				event = s;
			}
		}
		
		if(event == null) {
			return true;
		}
		
		//Checks if any transition would lead through a bad transition to the 'trash' state
		for(int i = 0; i < stateSetStates.length; i++) {
			if(events[i] != null && badTransitions.get(stateSetStates[i]).contains(event)) {
				return true;
			}
		}
		
		return false;
	}
	
	private ArrayList<String> filterEventPath(ArrayList<String> events, String contr, Agent age) {
		ArrayList<String> out = new ArrayList<String>();
		for(String s : events) {
			if(age.contains(s) && age.getEventAttribute(s, attributeObservableRef)) {
				out.add(s);
			}
		}
		if(age.contains(contr) && age.getEventAttribute(contr, attributeObservableRef)) {
			out.add(contr);
		}
		return out;
	}

	private void handleNewTransition(AgentStates next, String[] eventName, String currState) {
		String eventNom = constructEventName(eventName);
		uStructure.addEvent(eventNom);
		uStructure.addState(next.getCompositeName());
		uStructure.addTransition(currState, eventNom, next.getCompositeName());
		objectMap.put(next.getCompositeName(), next);
		eventNameMap.put(eventNom, eventName);
	}
	
	private String constructEventName(String[] es) {
		String out = "<";
		for(int i = 0; i < es.length; i++) {
			out += (es[i] == null ? UNOBSERVED_EVENT : es[i]) + (i + 1 < es.length ? ", " : ">");
		}
		return out;
	}
	
	private void filterGroups(HashSet<IllegalConfig> typeOne, HashSet<IllegalConfig> typeTwo){
		for(int i = 1; i < crushMap.length; i++) {
			HashSet<Integer> typeOneGroup = new HashSet<Integer>();
			CrushMap crush = crushMap[i];
			for(IllegalConfig ic : typeOne) {
				String st = ic.getStateSet().getCompositeName();
				for(int j : crush.getStateMemberships(st)) {
					typeOneGroup.add(j);
				}
			}
			HashSet<IllegalConfig> typeTwoRemove = new HashSet<IllegalConfig>();
			for(IllegalConfig ic : typeTwo) {
				String st = ic.getStateSet().getCompositeName();
				boolean conflict = false;
				for(int j : typeOneGroup) {
					if(crush.hasStateMembership(st, j)) {
						conflict = true;
					}
				}
				if(!conflict) {
					typeTwoRemove.add(ic);
				}
			}
			typeTwo.removeAll(typeTwoRemove);
		}
	}
	
}

