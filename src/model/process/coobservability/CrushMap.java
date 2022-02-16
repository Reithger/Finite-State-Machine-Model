package model.process.coobservability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CrushMap {
	
	private final static String GROUP_EVENT_NOTATION = "_~_";

	// Maps the State name to a list of group IDs they belong to
	private HashMap<String, HashSet<Integer>> crushMapping;
	// Maps a <group, event> pair to the group that that pair leads to
	private HashMap<String, Integer> groupEntryMapping;
	
	private int newGroupID;
	
	public CrushMap(String firstState) {
		crushMapping = new HashMap<String, HashSet<Integer>>();
		groupEntryMapping = new HashMap<String, Integer>();
		assignStateGroup(firstState, newGroupID++);
	}
	
	public void assignStateGroup(String state, int group) {
		if(crushMapping.get(state) == null) {
			crushMapping.put(state, new HashSet<Integer>());
		}
		crushMapping.get(state).add(group);
	}
	
	public void inheritStateGroups(String stateParent, String stateChild) {
		if(crushMapping.get(stateParent) != null) {
			for(int i : crushMapping.get(stateParent)) {
				assignStateGroup(stateChild, i);
			}
		}
	}
	
	public void stateGroupTransfer(String stateParent, String stateChild, String event) {
		if(stateParent.equals(stateChild)) {
			return;
		}
		for(int i : crushMapping.get(stateParent)) {
			if(groupEntryMapping.get(groupEventName(i, event)) == null) {
				groupEntryMapping.put(groupEventName(i, event), newGroupID++);
			}
			assignStateGroup(stateChild, groupEntryMapping.get(groupEventName(i, event)));
		}
	}
	
	private String groupEventName(int in, String event) {
		return in + GROUP_EVENT_NOTATION + event;
	}
	
	public ArrayList<Integer> getStateMemberships(String stateName){
		if(!crushMapping.containsKey(stateName)) {
			return null;
		}
		ArrayList<Integer> out = new ArrayList<Integer>();
		out.addAll(crushMapping.get(stateName));
		return out;
	}
	
	public boolean hasStateMembership(String stateName, int group) {
		if(crushMapping.get(stateName) != null) {
			return crushMapping.get(stateName).contains(group);
		}
		return false;
	}
	
	public ArrayList<Integer> getPotentialTargetGroups(String stateName, String event){
		if(crushMapping.get(stateName) != null) {
			ArrayList<Integer> out = new ArrayList<Integer>();
			for(int i : getStateMemberships(stateName)) {
				Integer test = groupEntryMapping.get(groupEventName(i, event));
				if(test == null) {
					groupEntryMapping.put(groupEventName(i, event), newGroupID++);
					test = groupEntryMapping.get(groupEventName(i, event));
				}
				out.add(test);
			}
			return out;
		}
		return null;
	}
	
}
