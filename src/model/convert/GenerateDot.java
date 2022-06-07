package model.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import model.AttributeList;
import model.fsm.TransitionSystem;

public class GenerateDot {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static String INITIAL_STATE_MARKER = "ArbitraryUnusedNameNoWorriesJustGrooving";
	//TODO: Make sure there are a lot of potential colors here, preferably prime, to avoid reuse
	private final static ColorPack[] backgroundColorCycle = new ColorPack[] {new ColorPack("4f", "4f", "4f"),
																			 new ColorPack("96", "32", "32"),
																			 new ColorPack("00", "80", "00"),
																		 	 new ColorPack("5b", "32", "92"),
																			 new ColorPack("ff", "a5", "00"),
																			 new ColorPack("7f", "ff", "00"),
																			 new ColorPack("00", "ff", "ff"),
																			 new ColorPack("00", "00", "ff"),
																			 new ColorPack("ff", "00", "ff"),
																			 new ColorPack("ee", "e8", "aa"),
																			 new ColorPack("64", "95", "ed"),
																			 new ColorPack("ff", "69", "b4"),
																			 new ColorPack("8b", "45", "13"),
																			 };
	
	private final static String SUB_START = "_{";
	private final static String SUP_START = "^{";
	private final static String SCRIPT_END = "}";
	
	private final static String SUB_CONVERT_START = "<sub>";
	private final static String SUB_CONVERT_END = "</sub>";
	private final static String SUP_CONVERT_START = "<SUP>";
	private final static String SUP_CONVERT_END = "</SUP>";
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static String generateDot(TransitionSystem in) {
		HashMap<String, String> nameMap = new HashMap<String, String>();
		
		int counter = 0;
		
		ArrayList<String> states = new ArrayList<String>();
		ArrayList<String> transitions = new ArrayList<String>();
		
		for(String e : in.getStateNames()) {
			nameMap.put(e, "n" + counter++);
			String line = "\"" + nameMap.get(e) + "\"[label= <" + processObjectNameScripts(e) + "> shape=" + generateStateDot(in, e);
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

//---  Getter Methods   -----------------------------------------------------------------------
	
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
		line += "\" style=wedged fillcolor=\"";
		
		int count = 0;		// how do we actually record multiple colors onto one node oh god oh no
		boolean first = true;
		boolean second = false;
		while(count < 100) {
			if(in.getStateAttribute(ref, count+"") != null) {
				line+= (first ? "" : ":") + backgroundColorCycle[count % backgroundColorCycle.length].cycleColor(count / backgroundColorCycle.length);
				if(!first) {
					second = true;
				}
				first = false;
			}
			count++;
		}
		
		if(second) {
			line += "\" style=wedged";
		}
		else {
			if(first) {
				line += "white";
			}
			line += "\" style=filled";
		}
		
		return line + "];";
	}
	
	/*

		line += "\" style=filled fillcolor=red];";
		return line;
	 * 
	 */
	
	private static String generateTransitionDot(TransitionSystem in, String state, String event) {
		String trans = "[label = <" + processObjectNameScripts(event) + "> color=\"";
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
	
	private static String processObjectNameScripts(String in) {
		String out = new String(in.toCharArray());
		while(out.contains(SUB_START)) {
			out = out.substring(0, out.indexOf(SUB_START)) + out.substring(out.indexOf(SUB_START)).replaceFirst(SCRIPT_END, SUB_CONVERT_END);
			out = out.replaceFirst(Pattern.quote(SUB_START), SUB_CONVERT_START);
			break;
		}
		while(out.contains(SUP_START)) {
			out = out.substring(0, out.indexOf(SUP_START)) + out.substring(out.indexOf(SUP_START)).replaceFirst(SCRIPT_END, SUP_CONVERT_END);
			out = out.replaceFirst(Pattern.quote(SUP_START), SUP_CONVERT_START);
		}
		return out;
	}
	
}
