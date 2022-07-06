package model.process.coobservability.support;

import java.util.ArrayList;
import java.util.Arrays;

public class AgentStates implements Comparable<AgentStates>{
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private String[] currentStates;
	
	private ArrayList<String> eventPath;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public AgentStates(String[] states, ArrayList<String> inPath) {
		currentStates = states;
		eventPath = new ArrayList<String>();
		for(String s : inPath) {
			eventPath.add(s);
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String[] getStates() {
		return currentStates;
	}
	
	public ArrayList<String> getEventPath() {
		return eventPath;
	}

	public String getCompositeName() {
		StringBuilder out = new StringBuilder();
		out.append("(");
		for(int i = 0; i < currentStates.length; i++) {
			out.append(currentStates[i] + (i + 1 < currentStates.length ? ", " : ")"));
		}
		return out.toString();
	}
	
//---  Mechanics   ----------------------------------------------------------------------------

	@Override
	public int compareTo(AgentStates o) {
		boolean fail = false;
		for(int i = 0; i < this.getStates().length; i++)
			if(!this.getStates()[i].equals(o.getStates()[i]))
				fail = true;
		if(!fail)
			return 0;
		else
			return -1;
	}
		
	@Override
	public boolean equals(Object o1) {
		return this.getCompositeName().equals(((AgentStates)o1).getCompositeName());
	}

	@Override
	public String toString() {
		return Arrays.toString(currentStates);
	}
	
}