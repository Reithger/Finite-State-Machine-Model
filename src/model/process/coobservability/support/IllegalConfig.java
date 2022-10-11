package model.process.coobservability.support;

import java.util.ArrayList;
import java.util.HashSet;

public class IllegalConfig {

	private AgentStates stateSet;
	/** For each agent, it is their visible version of the true event sequence from the AgentStates object. Does NOT include the plant view, that is in the AgentStates stateSet object*/
	private ArrayList<ArrayList<String>> observedPaths;
	
	private String event;
	
	public IllegalConfig(AgentStates inStates, ArrayList<ArrayList<String>> inPaths, String inEvent) {
		stateSet = inStates;
		observedPaths = inPaths;
		event = inEvent;
	}
	
	public AgentStates getStateSet() {
		return stateSet;
	}
	
	public ArrayList<String> getEventPath() {
		return copy(stateSet.getEventPath());
	}
	
	public ArrayList<ArrayList<String>> getObservedPaths() {
		ArrayList<ArrayList<String>> out = new ArrayList<ArrayList<String>>();
		for(ArrayList<String> s : observedPaths) {
			out.add(copy(s));
		}
		return out;
	}
	
	public int getEventPathLength() {
		return getEventPath().size();
	}
	
	public int getNumberDistinctEvents() {
		HashSet<String> chars = new HashSet<String>();
		for(String c : getEventPath()) {
			chars.add(c);
		}
		return chars.size();
	}
	
	public String getEvent() {
		return event;
	}
	
	@Override
	public String toString() {
		return stateSet.toString() + ", " + stateSet.getEventPath() + ", " + event;
	}
	
	private ArrayList<String> copy(ArrayList<String> in){
		ArrayList<String> out = new ArrayList<String>();
		for(String s : in) {
			out.add(s);
		}
		return out;
	}
	
}
