package test.datagathering;

import java.util.ArrayList;
import java.util.Collections;

import model.process.memory.ConcreteMemoryMeasure;

/**
 * 
 * 
 * Deviance over multiple runnings? Make sure standardized over loading-in from memory or generation.
 *  - Generate all my examples ahead of time, then on a fresh run calculate multiple times.
 *  - # transitions, state space data as well
 *  - Report on number of plants, specifications, controllers, unique events
 * 
 * Pre-existing examples, just a table with data for # states, # transitions, run each ~5 times for average performance
 * 
 * Bar graph, runtime/state-space/memory usage of each example w/ context of plants/specs/controllers
 * 
 * Split-bar graph that stacks relative run-times on top of eachother to show relative performance
 * 
 * Case-to-case comparison of relative performance between approaches to say general quality of improvement
 * 
 * Classify examples by size of state-space for specific data interpretation
 * 
 * Split results by when it returns true or false
 * 
 * Make sure we can see data for just true results and data for just false results; static variable to swap states, probably, for ease of function use?
 * 
 * @author Ada Clevinger
 *
 */

public class InterpretData {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private ArrayList<ArrayList<Double>> data;
	
	private String[] attributes;
	
	private int totalNumberTests;
	
	private Integer filterColumn;
	
	private Double filterValue;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public InterpretData() {
		data = new ArrayList<ArrayList<Double>>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public InterpretData copy() {
		InterpretData out = new InterpretData();
		
		for(ArrayList<Double> d : data) {
			ArrayList<Double> newD = new ArrayList<Double>();
			for(Double v : d) {
				newD.add(v);
			}
			out.addDataRow(newD);
		}
		
		out.assignAttributes(getAttributes());
		
		out.assignTotalNumberTests(getTotalNumberTests());
		
		out.setFilterValue(filterValue);
		out.setColumnFilter(filterColumn);
		
		return out;
	}
	
	public void deleteFirstValue() {
		for(ArrayList<Double> d : data) {
			d.remove(0);
		}
	}
	
	public void mergeData(InterpretData in) {
		data.addAll(in.getData());
	}
	
	public void addDataRow(String[] inData) {
		ArrayList<Double> hold = new ArrayList<Double>();
		for(String s : inData) {
			if(!s.equals(""))
				hold.add(Double.parseDouble(s));
			else {
				hold.add(null);
			}
		}
		data.add(hold);
	}

	public void addDataRow(ArrayList<Double> inData) {
		if(data == null) {
			data = new ArrayList<ArrayList<Double>>();
		}
		data.add(inData);
	}
	
	public ArrayList<Double> calculateAverages() {
		ArrayList<Double> out = new ArrayList<Double>();
		for(int i = 0; i < data.get(0).size(); i++) {
			double total = 0.0;
			ArrayList<Double> column = pullColumn(i);
			for(Double d : column) {
				total += d;
			}
			total /= column.size();
			out.add(total);
		}
		return out;
	}
	
	public ArrayList<Double> calculateMinimums(){
		ArrayList<Double> out = new ArrayList<Double>();
		for(int i = 0; i < data.get(0).size(); i++) {
			out.add(pullColumn(i).get(0));
		}
		return out;
	}
	
	public ArrayList<Double> calculateMaximums(){
		ArrayList<Double> out = new ArrayList<Double>();
		for(int i = 0; i < data.get(0).size(); i++) {
			out.add(pullColumn(i).get(pullColumn(i).size() - 1));
		}
		return out;
	}
	
	public ArrayList<Double> calculateMedians(){
		ArrayList<Double> out = new ArrayList<Double>();
		for(int i = 0; i < data.get(0).size(); i++) {
			ArrayList<Double> column = pullColumn(i);
			if(column.size() % 2 == 0) {
				out.add((column.get(column.size() / 2) + column.get(column.size() / 2 - 1)) / 2);
			}
			else {
				out.add(column.get(column.size() / 2));
			}
		}
		return out;
	}
	
	public ArrayList<Double> calculateFirstQuartile(){
		ArrayList<Double> out = new ArrayList<Double>();
		for(int i = 0; i < data.get(0).size(); i++) {
			ArrayList<Double> column = pullColumn(i);
			int upperBound = column.size() / 2;
			
			if(column.size() % 2 != 0) {
				upperBound--;
			}
			
			if(upperBound % 2 == 0) {
				out.add((column.get(upperBound / 2) + column.get(upperBound / 2 + 1)) / 2);
			}
			else {
				out.add(column.get(upperBound / 2));
			}
		}
		return out;
	}
	
	/*
	 * TODO: Test it to make sure it's right
	 * 
	 */
	
	public ArrayList<Double> calculateThirdQuartile(){
		ArrayList<Double> out = new ArrayList<Double>();
		for(int i = 0; i < data.get(0).size(); i++) {
			ArrayList<Double> column = pullColumn(i);
			int lowerBound = column.size() / 2;
			
			if(column.size() % 2 != 0 && column.size() > 1) {
				lowerBound++;
			}
			
			if((column.size() - lowerBound % 2) == 0) {
				double val1 = column.get((lowerBound + column.size()) / 2);
				double val2 = column.get((lowerBound + column.size()) / 2 - 1);
				out.add((val1 + val2) / 2);
			}
			else {
				out.add(column.get((lowerBound + column.size()) / 2));
			}

		}
		return out;
	}
	
	public ArrayList<Double> calculateInterquartileRange(){
		ArrayList<Double> out = new ArrayList<Double>();
		ArrayList<Double> first = calculateFirstQuartile();
		ArrayList<Double> third = calculateThirdQuartile();
		for(int i = 0; i < third.size(); i++) {
			out.add(third.get(i) - first.get(i));
		}
		return out;
	}
	
	private ArrayList<Double> pullColumn(int column){
		ArrayList<Double> out = new ArrayList<Double>();
		for(int i = 0; i < data.size(); i++) {
			if(data.get(i).size() > column) {
				if((filterColumn == null || filterValue == null) || (data.get(i).get(filterColumn).equals(filterValue))) {
					Double val = data.get(i).get(column);
					if(val != null) {
						out.add(val);
					}
				}
			}
		}
		Collections.sort(out);
		if(out.size() == 0) {
			out.add(-1.0);
		}
		return out;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setColumnFilter(Integer in) {
		filterColumn = in;
	}
	
	public void setFilterValue(Double in) {
		filterValue = in;
	}
	
	public void assignTotalNumberTests(int in) {
		totalNumberTests = in;
	}
	
	public void assignAttributes(String[] in) {
		attributes = in;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public int getTotalNumberTests() {
		return totalNumberTests;
	}

	public String[] getAttributes() {
		return attributes;
	}
	
	protected ArrayList<ArrayList<Double>> getData(){
		return data;
	}

	public ArrayList<Integer> getColumnSizes(){
		ArrayList<Integer> out = new ArrayList<Integer>();
		for(int i = 0; i < data.get(0).size(); i++) {
			out.add(pullColumn(i).size());
		}
		return out;
	}
	
	public int getNumberTrueResults() {
		int counter = 0;
		for(Double v : pullColumn(2)) {
			if(v.equals(ConcreteMemoryMeasure.TEST_RESULT_TRUE)) {
				counter++;
			}
		}
		return counter;
	}
	
	public int getNumberFalseResults() {
		int counter = 0;
		for(Double v : pullColumn(2)) {
			if(v.equals(ConcreteMemoryMeasure.TEST_RESULT_FALSE)) {
				counter++;
			}
		}
		return counter;
	}
	
	public int getNumberDNF() {
		return getTotalNumberTests() - (getNumberTrueResults() + getNumberFalseResults());
	}

}
