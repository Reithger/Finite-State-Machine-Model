package model.process;

import model.fsm.state.State;

public class BatchAgentStates implements Comparable<BatchAgentStates>{
	
	State[] currentStates;
	State confirmedObject;
	
	public BatchAgentStates(State[] states, State identity) {
		currentStates = states;
		confirmedObject = identity;
	}
	
	public State[] getStates() {
		return currentStates;
	}
	
	public void setState(State in) {
		confirmedObject = in;
	}

	@Override
	public int compareTo(BatchAgentStates o) {
		boolean fail = false;
		for(int i = 0; i < this.getStates().length; i++)
			if(!this.getStates()[i].getStateName().equals(o.getStates()[i].getStateName()))
				fail = true;
		if(!fail)
			return 0;
		else
			return -1;
	}
		
	public State getIdentityState() {
		return confirmedObject;
	}
	
	@Override
	public boolean equals(Object o1) {
		return this.confirmedObject.equals(((BatchAgentStates)o1).confirmedObject);
	}
	
}