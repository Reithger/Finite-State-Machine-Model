package model.process.coobservability;

public class IllegalConfig {

	private AgentStates stateSet;
	
	private String event;
	
	public IllegalConfig(AgentStates inStates, String inEvent) {
		stateSet = inStates;
		event = inEvent;
	}
	
	public AgentStates getStateSet() {
		return stateSet;
	}
	
	public String getEventPath() {
		return stateSet.getEventPath();
	}
	
	public String getEvent() {
		return event;
	}
	
}
