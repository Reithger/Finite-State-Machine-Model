package test;

import java.util.ArrayList;

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
