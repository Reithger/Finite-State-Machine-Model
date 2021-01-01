package model.convert;

import java.util.ArrayList;
import java.util.HashMap;

import model.AttributeList;
import model.fsm.TransitionSystem;

public class GenerateDot {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static String INITIAL_STATE_MARKER = "ArbitraryUnusedNameNoWorriesJustGrooving";

//---  Operations   ---------------------------------------------------------------------------
	
	public static String generateDot(TransitionSystem in) {
		HashMap<String, String> nameMap = new HashMap<String, String>();
		
		int counter = 0;
		
		ArrayList<String> states = new ArrayList<String>();
		ArrayList<String> transitions = new ArrayList<String>();
		
		for(String e : in.getStateNames()) {
			nameMap.put(e, "n" + counter++);
			String line = "\"" + nameMap.get(e) + "\"[shape=" + generateStateDot(in, e);
			states.add(line);
			if(in.getStateAttribute(e, AttributeList.ATTRIBUTE_INITIAL)) {
				String use = (INITIAL_STATE_MARKER + counter);
				states.add("\"" + use + "\"[fontSize=1 shape=point];");
				transitions.add("{\"" + use + "\"}->{\"" + nameMap.get(e) + "\"};");
			}
		}
		for(String s : in.getStateNames()) {
			for(String e : in.getStateTransitionEvents(s)) {
				for(String t : in.getStateEventTransitionStates(s, e)) {
					String trans = "{\"" + nameMap.get(s) + "\"}->{\"" + nameMap.get(t) + "\"}";
					trans += generateTransitionDot(in, e);
					transitions.add(trans);
				}
			}
		}
		StringBuilder out = new StringBuilder();
		for(String s : states) {
			out.append(s + "\n");
		}
		for(String s : transitions) {
			out.append(s + "\n");
		}
		
		return out.toString();
	}

//---  Support Methods   ----------------------------------------------------------------------
	
	private static String generateStateDot(TransitionSystem in, String ref) {
		boolean bad = in.getStateAttribute(ref, AttributeList.ATTRIBUTE_BAD);
		String line = in.getStateAttribute(ref, AttributeList.ATTRIBUTE_MARKED) ? "doublecircle" : "circle";
		line += " color=\"";
		if(in.getStateAttribute(ref, AttributeList.ATTRIBUTE_PRIVATE)) {
			line += bad ? "red" : "orange";
		}
		else {
			line += bad ? "purple" : "black";
		}
		line += "\"];";
		return line;
	}
	
	private static String generateTransitionDot(TransitionSystem in, String ref) {
		String trans = "[label = \"" + ref + "\" color=\"";
		trans += in.getEventAttribute(ref, AttributeList.ATTRIBUTE_OBSERVABLE) ? "black" : "red";
		trans += "\" arrowhead=\"normal";
		trans += in.getEventAttribute(ref, AttributeList.ATTRIBUTE_ATTACKER_OBSERVABLE) ? "odot" : "";
		trans += in.getEventAttribute(ref, AttributeList.ATTRIBUTE_CONTROLLABLE) ? "diamond" : "";
		trans += "\"];";
		return trans;
	}
	
}
