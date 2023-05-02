package model.process.memory;

import java.util.ArrayList;

public class UStructMemoryMeasure extends ConcreteMemoryMeasure{

	private int stateSize;
	
	private int transSize;
	
	private ArrayList<Integer> numberAgentGroups;
	
	private ArrayList<Integer> sizeStateGroups;
	
	public void assignStateSize(int in) {
		stateSize = in;
	}
	
	public void assignTransitionSize(int in) {
		transSize = in;
	}
	
	public void logAgentGroupSize(int in) {
		if(numberAgentGroups == null) {
			numberAgentGroups = new ArrayList<Integer>();
		}
		numberAgentGroups.add(in);
	}
	
	public void logStateGroupSize(int in) {
		if(sizeStateGroups == null) {
			sizeStateGroups = new ArrayList<Integer>();
		}
		sizeStateGroups.add(in);
	}
	
	private double getAverageAgents() {
		return threeSig(averageList(numberAgentGroups));
	}
	
	private double getAverageStates() {
		return threeSig(averageList(sizeStateGroups));
	}
	
	private double averageList(ArrayList<Integer> in) {
		int total = 0;
		for(int i : in) {
			total += i;
		}
		return (double)total / in.size();
	}
	
	@Override
	public String produceOutputLog() {
		String out = super.produceOutputLog();
		out += "\n\t\t\t\tState Size: " + stateSize + ", Transition Size: " + transSize;
		if(numberAgentGroups != null) {
			out += "\n\t\t\t\tAverage Number of State Groups per Agent: " + getAverageAgents() + "\n";
			out += "\t\t\t\tAverage Number of States per State Group: " + getAverageStates() + "\n";
			out += "\t\t\t\tMaximum Number of States in a State Group: " + getMaximumStates();
		}
		return out;
	}
	
	private int getMaximumStates() {
		int out = -1;
		for(int i : sizeStateGroups) {
			if(out == -1 || i > out) {
				out = i;
			}
		}
		return out;
	}
	
	@Override
	public ArrayList<String> getOutputGuide(){
		ArrayList<String> out = super.getOutputGuide();
		out.add("Number of States in UStructure");
		out.add("Number of Transitions in UStructure");
		if(numberAgentGroups != null) {
			out.add("Average Number of State Groups per Agent");
			out.add("Average Number of States per State Group");
			out.add("Maximum Number of States in a State Group");
		}
		return out;
	}
	
	@Override
	public ArrayList<Double> getStoredData(){
		ArrayList<Double> out = super.getStoredData();
		out.add((double)stateSize);
		out.add((double)transSize);
		if(numberAgentGroups != null) {
			out.add(getAverageAgents());
			out.add(getAverageStates());
			out.add((double)getMaximumStates());
		}
		return out;
	}
	
	public static ConcreteMemoryMeasure produceBlank() {
		UStructMemoryMeasure out = new UStructMemoryMeasure();
		out.assignStateSize(0);
		out.assignTransitionSize(0);
		out.assignTestResult(true);
		return out;
	}
	
}
