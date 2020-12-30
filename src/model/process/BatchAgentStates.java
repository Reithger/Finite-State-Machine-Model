package model.process;

public class BatchAgentStates implements Comparable<BatchAgentStates>{
	
	private String[] currentStates;
	private String confirmedObject;
	
	public BatchAgentStates(String[] states, String identity) {
		currentStates = states;
		confirmedObject = identity;
	}
	
	public String[] getStates() {
		return currentStates;
	}
	
	public void setState(String in) {
		confirmedObject = in;
	}

	@Override
	public int compareTo(BatchAgentStates o) {
		boolean fail = false;
		for(int i = 0; i < this.getStates().length; i++)
			if(!this.getStates()[i].equals(o.getStates()[i]))
				fail = true;
		if(!fail)
			return 0;
		else
			return -1;
	}
		
	public String getIdentityState() {
		return confirmedObject;
	}
	
	@Override
	public boolean equals(Object o1) {
		return this.confirmedObject.equals(((BatchAgentStates)o1).confirmedObject);
	}
	
}