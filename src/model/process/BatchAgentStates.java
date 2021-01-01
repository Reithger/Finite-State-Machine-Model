package model.process;

public class BatchAgentStates implements Comparable<BatchAgentStates>{
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private String[] currentStates;
	private String confirmedObject;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public BatchAgentStates(String[] states, String identity) {
		currentStates = states;
		confirmedObject = identity;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setState(String in) {
		confirmedObject = in;
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	public String[] getStates() {
		return currentStates;
	}

	public String getIdentityState() {
		return confirmedObject;
	}
	
//---  Mechanics   ----------------------------------------------------------------------------

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
		
	@Override
	public boolean equals(Object o1) {
		return this.confirmedObject.equals(((BatchAgentStates)o1).confirmedObject);
	}
	
}