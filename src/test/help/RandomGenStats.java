package test.help;

public class RandomGenStats {

//---  Instance Variables   -------------------------------------------------------------------
	
	private int numPlants;
	
	private int numSpecs;
	
	private int numStates;
	
	private int numStatesVar;
	
	private int numEvents;
	
	private int numEventsVar;
	
	private double eventShareRate;
	
	private int numControllers;
	
	private int numControllersVar;
	
	private double controllerObserveRate;
	
	private double controllerControlRate;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public RandomGenStats(int inNumPlants, int inNumSpecs, int inNumStates, int inNumStateVar, int inNumEve, int inNumEveVar, double inShareRate, int inNumAgents, int inNumAgentVar, double inObsRate, double inCtrRate) {
		numPlants = inNumPlants;
		numSpecs = inNumSpecs;
		numStates = inNumStates;
		numStatesVar = inNumStateVar;
		numEvents = inNumEve;
		numEventsVar = inNumEveVar;
		eventShareRate = inShareRate;
		numControllers = inNumAgents;
		numControllersVar = inNumAgentVar;
		controllerObserveRate = inObsRate;
		controllerControlRate = inCtrRate;
	}
	
//---  Getter Functions   ---------------------------------------------------------------------
	
	public int getNumPlants() {
		return numPlants;
	}
	
	public int getNumSpecs() {
		return numSpecs;
	}

	public int getNumStates() {
		return numStates;
	}
	
	public int getNumStatesVar() {
		return numStatesVar;
	}
	
	public int getNumEvents() {
		return numEvents;
	}
	
	public int getNumEventsVar() {
		return numEventsVar;
	}
	
	public double getEventShareRate() {
		return eventShareRate;
	}
	
	public int getNumControllers() {
		return numControllers;
	}
	
	public int getNumControllersVar() {
		return numControllersVar;
	}
	
	public double getControllerObserveRate() {
		return controllerObserveRate;
	}
	
	public double getControllerControlRate() {
		return controllerControlRate;
	}
	
//---  Setter Functions   ---------------------------------------------------------------------
	
	public void setNumPlant(int in) {
		numPlants = in;
	}
	
	public void setNumSpecs(int in) {
		numSpecs = in;
	}
	
	public void setNumStates(int in) {
		numStates = in;
	}
	
	public void setNumStatesVar(int in) {
		numStatesVar = in;
	}
	
	public void setNumEvents(int in) {
		numEvents = in;
	}
	
	public void setNumEventsVar(int in) {
		numEventsVar = in;
	}
	
	public void setNumControllers(int in) {
		numControllers = in;
	}
	
//---  Utility Functions   --------------------------------------------------------------------
	
	public String toString() {
		return "Plants: " + numPlants + ", Specs: " + numSpecs + ", # States Average: " + numStates + ", State Variance: " + numStatesVar + ", # Events Average: " + numEvents +
				", Event Variance: " + numEventsVar + 
				", Event Share Rate: " + eventShareRate + ", # Agents: " + numControllers + ", Agent Variance: " + numControllersVar + ", Agent Obs. Event Rate: " + controllerObserveRate + 
				", Agent Ctr. Event Rate: " + controllerControlRate;
	}
	
	public String shortToString() {
		return numPlants + ", " + numSpecs + ", " + numStates + ", " + numStatesVar + ", " + numEvents + ", " + numEventsVar + ", " + 
				eventShareRate + ", " + numControllers + ", " + numControllersVar + ", " + controllerObserveRate + ", " + controllerControlRate;
	}
	
}
