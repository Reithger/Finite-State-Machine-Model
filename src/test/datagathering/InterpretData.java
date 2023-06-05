package test.datagathering;

import java.util.ArrayList;
import java.util.Collections;

import model.process.memory.ConcreteMemoryMeasure;

/**
 * 
 * 
 * Run same example 5-10 times and calculate average result for standardized average performance?
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
	
	private ArrayList<ArrayList<Double>> data;
	
	private String[] attributes;
	
	public InterpretData() {
		data = new ArrayList<ArrayList<Double>>();
	}
	
	public void mergeData(InterpretData in) {
		data.addAll(in.getData());
	}
	
	public void assignAttributes(String[] in) {
		attributes = in;
	}
	
	public String[] getAttributes() {
		return attributes;
	}
	
	protected ArrayList<ArrayList<Double>> getData(){
		return data;
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
			int pos1 = column.size() / 4;
			int pos2 = column.size() / 4 + 1;
			if(pos2 >= column.size()) {
				pos2 -= 1;
			}
			if((column.size() / 2) % 2 == 0){
				out.add((column.get(pos1) + (column.get(pos2) / 2)));
			}
			else {
				out.add(column.get(pos2));
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
			int pos1 = column.size() * 3 / 4;
			int pos2 = column.size() * 3 / 4 + 1;
			if(pos2 >= column.size()) {
				pos2 -= 1;
			}
			if((column.size() - column.size() / 2) % 2 == 0) {
				out.add((column.get(pos1) + column.get(pos2)) / 2);
			}
			else {
				out.add((column.get(pos1)));
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
	
	public int getNumberDNF(int size) {
		return size - pullColumn(0).size();
	}
	
	private ArrayList<Double> pullColumn(int column){
		ArrayList<Double> out = new ArrayList<Double>();
		for(int i = 0; i < data.size(); i++) {
			if(data.get(i).size() > column) {
				Double val = data.get(i).get(column);
				if(val != null)
					out.add(val);
			}
		}
		Collections.sort(out);
		if(out.size() == 0) {
			out.add(-1.0);
		}
		return out;
	}
	
}
