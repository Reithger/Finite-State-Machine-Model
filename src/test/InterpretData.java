package test;

import java.util.ArrayList;

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
 * @author SirBo
 *
 */

public class InterpretData {

	private ArrayList<ArrayList<Double>> data;
	
	public InterpretData() {
		data = new ArrayList<ArrayList<Double>>();
	}
	
	public void addDataRow(String[] inData) {
		ArrayList<Double> hold = new ArrayList<Double>();
		for(String s : inData) {
			hold.add(Double.parseDouble(s));
		}
		data.add(hold);
	}
	
	public ArrayList<Double> calculateAverages() {
		ArrayList<Double> out = new ArrayList<Double>();
		for(ArrayList<Double> column : data) {
			Double hold = 0.0;
			for(Double d : column) {
				hold += d;
			}
			hold /= column.size();
			out.add(hold);
		}
		return out;
	}
	
	public ArrayList<Double> calculateMinimums(){
		ArrayList<Double> out = new ArrayList<Double>();
		for(ArrayList<Double> column : data) {
			Double hold = null;
			for(Double d : column) {
				if(hold == null || d < hold)
					hold = d;
			}
			out.add(hold);
		}
		return out;
	}
	
	public ArrayList<Double> calculateMaximums(){
		ArrayList<Double> out = new ArrayList<Double>();
		for(ArrayList<Double> column : data) {
			Double hold = null;
			for(Double d : column) {
				if(hold == null || d > hold)
					hold = d;
			}
			out.add(hold);
		}
		return out;
	}
	
	public ArrayList<Double> calculateMedian(){
		ArrayList<Double> out = new ArrayList<Double>();
		for(ArrayList<Double> column : data) {
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
		for(ArrayList<Double> column : data) {
			if((column.size() / 2) % 2 == 0){
				out.add((column.get(column.size() / 4) + (column.get(column.size() / 4 + 1) / 2)));
			}
			else {
				out.add(column.get(column.size() / 4 + 1));
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
		for(ArrayList<Double> column : data) {
			if((column.size() - column.size() / 2) % 2 == 0) {
				out.add((column.get(column.size() * 3 / 4) + column.get(column.size() * 3 / 4 + 1)) / 2);
			}
			else {
				out.add((column.get(column.size() * 3 / 4)));
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
	
}
