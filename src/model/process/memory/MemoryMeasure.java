package model.process.memory;

import java.util.ArrayList;

import model.fsm.TransitionSystem;

public interface MemoryMeasure {

	public abstract double getAverageMemoryUsage();
	
	public abstract double getMaximumMemoryUsage();
	
	public abstract String produceOutputLog();
	
	public abstract ArrayList<String> getOutputGuide();
	
	public abstract ArrayList<Double> getStoredData();
	
	public abstract TransitionSystem getReserveSystem();
	
	public abstract void reserveTransitionSystem(TransitionSystem in);
	
}
