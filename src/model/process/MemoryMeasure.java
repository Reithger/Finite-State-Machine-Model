package model.process;

import java.util.ArrayList;

public interface MemoryMeasure {

	public abstract double getAverageMemoryUsage();
	
	public abstract double getMaximumMemoryUsage();
	
	public abstract String produceOutputLog();
	
	public abstract ArrayList<String> getOutputGuide();
	
	public abstract ArrayList<Double> getStoredData();
	
}
