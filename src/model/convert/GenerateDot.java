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
					trans += generateTransitionDot(in, s, e);
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
		Boolean good = in.getStateAttribute(ref, AttributeList.ATTRIBUTE_GOOD);
		Boolean mark = in.getStateAttribute(ref, AttributeList.ATTRIBUTE_MARKED);
		Boolean priv = in.getStateAttribute(ref, AttributeList.ATTRIBUTE_PRIVATE);
		bad = bad == null ? false : bad;
		good = good == null ? false : good;
		mark = mark == null ? false : mark;
		priv = priv == null ? false : priv;
		String line = mark || bad || good ? "doublecircle" : "circle";
		line += " color=\"";
		if(priv) {
			line += bad ? "purple" : "orange";
		}
		else {
			line += bad ? "red" : good ? "green" : "black";
		}
		line += "\"];";
		return line;
	}
	
	private static String generateTransitionDot(TransitionSystem in, String state, String event) {
		String trans = "[label = \"" + event + "\" color=\"";
		Boolean obs = in.getEventAttribute(event, AttributeList.ATTRIBUTE_OBSERVABLE);
		Boolean atkObs = in.getEventAttribute(event, AttributeList.ATTRIBUTE_ATTACKER_OBSERVABLE);
		Boolean cont = in.getEventAttribute(event, AttributeList.ATTRIBUTE_CONTROLLABLE);
		Boolean bad = in.getTransitionAttribute(state, event, AttributeList.ATTRIBUTE_BAD);
		trans += obs == null || obs ? "black" : "red";
		trans += "\" arrowhead=\"normal";
		trans += atkObs != null && atkObs ? "odot" : "";
		trans += cont != null && cont ? "diamond" : "";
		trans += "\" style=\"";
		trans += bad != null && bad ? "dashed" : "";
		trans += "\"];";
		return trans;
	}
	
}
