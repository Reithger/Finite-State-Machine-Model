package model.process.coobservability.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CrushMap {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	// Maps the State name to a list of group IDs they belong to
	private HashMap<String, HashSet<Integer>> crushMapping;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public CrushMap() {
		crushMapping = new HashMap<String, HashSet<Integer>>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void assignStateGroup(String state, int group) {
		if(crushMapping.get(state) == null) {
			crushMapping.put(state, new HashSet<Integer>());
		}
		crushMapping.get(state).add(group);
	}
	
	public String getOutput(ArrayList<String> importantStates) {
		StringBuilder sb = new StringBuilder();
		
		HashMap<Integer, HashSet<String>> mapCrush = new HashMap<Integer, HashSet<String>>();
		
		for(String s : crushMapping.keySet()) {
			for(int i : crushMapping.get(s)) {
				if(mapCrush.get(i) == null) {
					mapCrush.put(i, new HashSet<String>());
				}
				mapCrush.get(i).add(s);
			}
		}
		
		for(int i : mapCrush.keySet()) {
			sb.append("\t" + i +": ");
			for(String s : mapCrush.get(i)) {
				sb.append(s + ",");
			}
			sb.append("\n");
		}
		
		
		if(importantStates != null && importantStates.size() != 0) {
			sb.append("By request, in particular:\n");
			for(String s : importantStates) {
				sb.append("\t" + s + ": ");
				for(int i : crushMapping.get(s)) {
					sb.append(i + ", ");
				}
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
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
	
}
