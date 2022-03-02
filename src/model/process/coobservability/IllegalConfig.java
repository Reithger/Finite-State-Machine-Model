package model.process.coobservability;

import java.util.HashSet;

public class IllegalConfig {

	private AgentStates stateSet;
	
	private String[] observedPaths;
	
	private String event;
	
	public IllegalConfig(AgentStates inStates, String[] inPaths, String inEvent) {
		stateSet = inStates;
		observedPaths = inPaths;
		event = inEvent;
	}
	
	public AgentStates getStateSet() {
		return stateSet;
	}
	
	public String getEventPath() {
		return stateSet.getEventPath();
	}
	
	public String[] getObservedPaths() {
		return observedPaths;
	}
	
	public int getEventPathLength() {
		return getEventPath().length();
	}
	
	public int getNumberDistinctEvents() {
		HashSet<Character> chars = new HashSet<Character>();
		for(char c : getEventPath().toCharArray()) {
			chars.add(c);
		}
		return chars.size();
	}
	
	public String getEvent() {
		return event;
	}
	
}
