package model.convert;

import java.util.ArrayList;

import model.fsm.TransitionSystem;

public class ReadWrite {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private static String SEPARATOR;
	private static String REGION_SEPARATOR;
	private static String TRUE_SYMBOL;
	private static String FALSE_SYMBOL;

//---  Operations   ---------------------------------------------------------------------------

	public static void assignConstants(String separator, String regionSeparator, String trueSymbol, String falseSymbol) {
		SEPARATOR = separator;
		REGION_SEPARATOR = regionSeparator;
		TRUE_SYMBOL = trueSymbol;
		FALSE_SYMBOL = falseSymbol;
	}
	
	public static String generateFile(TransitionSystem in) {
		StringBuilder out = new StringBuilder();
		
		out.append(in.getId() + "\n");
		out.append(REGION_SEPARATOR + "\n");
		
		ArrayList<String> stateAttr = in.getStateAttributes();
		ArrayList<String> eventAttr = in.getEventAttributes();
		ArrayList<String> tranAttr = in.getTransitionAttributes();

		attribute(stateAttr, out);
		attribute(eventAttr, out);
		attribute(tranAttr, out);

		out.append(REGION_SEPARATOR + "\n");
		
		for(String s : in.getStateNames()) {
			String build = s;
			for(int i = 0; i < stateAttr.size(); i++) {
				build += SEPARATOR + (in.getStateAttribute(s, stateAttr.get(i)) ? TRUE_SYMBOL : FALSE_SYMBOL);
			}
			out.append(build + "\n");
		}
		out.append(REGION_SEPARATOR);
		for(String s : in.getEventNames()) {
			String build = s;
			for(int i = 0; i < eventAttr.size(); i++) {
				build += SEPARATOR + (in.getEventAttribute(s, stateAttr.get(i)) ? TRUE_SYMBOL : FALSE_SYMBOL);
			}
			out.append(build + "\n");
		}
		out.append(REGION_SEPARATOR);
		
		for(String s : in.getStateNames()) {
			for(String e : in.getStateTransitionEvents(s)) {
				for(String t : in.getStateEventTransitionStates(s, e)) {
					String build = s + SEPARATOR + e + SEPARATOR + t;
					for(int i = 0; i < tranAttr.size(); i++) {
						build += SEPARATOR + (in.getTransitionAttribute(s, e, stateAttr.get(i)) ? TRUE_SYMBOL : FALSE_SYMBOL);
					}
					out.append(build + "\n");
				}
			}
		}
		return out.toString();
	}
	
	public static TransitionSystem readFile(String in) {
		String[] lines = in.split("\n");
		
		ArrayList<String> stateAttr = new ArrayList<String>();
		for(String s : lines[2].split(SEPARATOR)) {
			if(!s.equals(""))
				stateAttr.add(s);
		}
		ArrayList<String> eventAttr = new ArrayList<String>();
		for(String s : lines[3].split(SEPARATOR)) {
			if(!s.equals(""))
				eventAttr.add(s);
		}
		ArrayList<String> tranAttr = new ArrayList<String>();
		for(String s : lines[4].split(SEPARATOR)) {
			if(!s.equals(""))
				tranAttr.add(s);
		}
		
		TransitionSystem out = new TransitionSystem(lines[0], stateAttr, eventAttr, tranAttr);
		
		int index = 6;
		while(index < lines.length && !lines[index].equals(REGION_SEPARATOR)) {
			String[] info = lines[index++].split(SEPARATOR);
			out.addState(info[0]);
			for(int i = 0; i < stateAttr.size(); i++) {
				out.setStateAttribute(info[0], stateAttr.get(i), info[i+1].equals(TRUE_SYMBOL));
			}
		}
		index++;
		while(index < lines.length && !lines[index].equals(REGION_SEPARATOR)) {
			String[] info = lines[index++].split(SEPARATOR);
			out.addEvent(info[0]);
			for(int i = 0; i < eventAttr.size(); i++) {
				out.setEventAttribute(info[0], eventAttr.get(i), info[i+1].equals(TRUE_SYMBOL));
			}
		}
		index++;
		while(index < lines.length && !lines[index].equals(REGION_SEPARATOR)) {
			String[] info = lines[index++].split(SEPARATOR);
			out.addTransition(info[0], info[1], info[2]);
			for(int i = 0; i < tranAttr.size(); i++) {
				out.setTransitionAttribute(info[0], info[1], tranAttr.get(i), info[i+3].equals(TRUE_SYMBOL));
			}
		}
		return out;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private static StringBuilder attribute(ArrayList<String> use, StringBuilder out) {
		for(int i = 0; i < use.size(); i++) {
			out.append(use.get(i) + (i + 1 < use.size() ? SEPARATOR : ""));
		}
		out.append(SEPARATOR + "\n");
		return out;
	}
	
}
