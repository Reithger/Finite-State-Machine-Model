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
			String line = "\"" + nameMap.get(e) + "\"[label=\"" + e + "\"shape=" + generateStateDot(in, e);
			states.add(line);
			Boolean init = in.getStateAttribute(e, AttributeList.ATTRIBUTE_INITIAL);
			if(init != null && init) {
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
		Boolean bad = in.getStateAttribute(ref, AttributeList.ATTRIBUTE_BAD);
		Boolean mark = in.getStateAttribute(ref, AttributeList.ATTRIBUTE_MARKED);
		String line = mark != null && mark ? "doublecircle" : "circle";
		line += " color=\"";
		Boolean priv = in.getStateAttribute(ref, AttributeList.ATTRIBUTE_PRIVATE);
		if(priv != null && priv) {
			line += bad != null && bad ? "red" : "orange";
		}
		else {
			line += bad != null && bad ? "purple" : "black";
		}
		line += "\"];";
		return line;
	}
	
	private static String generateTransitionDot(TransitionSystem in, String ref) {
		String trans = "[label = \"" + ref + "\" color=\"";
		Boolean obs = in.getEventAttribute(ref, AttributeList.ATTRIBUTE_OBSERVABLE);
		Boolean atkObs = in.getEventAttribute(ref, AttributeList.ATTRIBUTE_ATTACKER_OBSERVABLE);
		Boolean cont = in.getEventAttribute(ref, AttributeList.ATTRIBUTE_CONTROLLABLE);
		trans += obs == null || obs ? "black" : "red";
		trans += "\" arrowhead=\"normal";
		trans += atkObs != null && atkObs ? "odot" : "";
		trans += cont != null && cont ? "diamond" : "";
		trans += "\"];";
		return trans;
	}
	
}
