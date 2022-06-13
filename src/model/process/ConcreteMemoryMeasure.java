package model.process;

import java.util.ArrayList;

public class ConcreteMemoryMeasure implements MemoryMeasure {

//---  Instance Variables   -------------------------------------------------------------------
	
	private long startingMemory;
	
	private ArrayList<Long> spaceUsage;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ConcreteMemoryMeasure() {
		startingMemory = getMemoryUsage();
		spaceUsage = new ArrayList<Long>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void logMemoryUsage() {
		spaceUsage.add(getMemoryUsage() - startingMemory);
	}
	
	@Override
	public String produceOutputLog() {
		return "\t\t\t\tAverage Memory: " + getAverageMemoryUsage() + " Mb, Max Memory: " + getMaximumMemoryUsage() + " Mb";
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	private long getMemoryUsage() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}
	
	public double getAverageMemoryUsage() {
		long add = 0;
		for(Long l : spaceUsage) {
			add += l;
		}
		if(spaceUsage.size() == 0) {
			spaceUsage.add(0L);
		}
		return threeSig(inMB((add / spaceUsage.size())));
	}
	
	public double getMaximumMemoryUsage() {
		long max = 0;
		for(Long l : spaceUsage) {
			if(l > max) {
				max = l;
			}
		}
		return threeSig(inMB(max));
	}
	
//---  Support Methods   ----------------------------------------------------------------------	

	private static double inMB(long in) {
		return (double)in / 1000000;
	}
	
	protected static Double threeSig(double in) {
		String use = in+"000000000";
		int posit = use.indexOf(".") + 4;
		return Double.parseDouble(use.substring(0, posit));
	}

	
	@Override
	public ArrayList<String> getOutputGuide() {
		ArrayList<String> out = new ArrayList<String>();
		
		out.add("Average Memory Consumption");
		out.add("Maximum Memory Consumption");
		
		return out;
	}
	

	@Override
	public ArrayList<Double> getStoredData() {
		ArrayList<Double> out = new ArrayList<Double>();
		
		out.add(getAverageMemoryUsage());
		out.add(getMaximumMemoryUsage());
		
		return out;
	}
	
}
