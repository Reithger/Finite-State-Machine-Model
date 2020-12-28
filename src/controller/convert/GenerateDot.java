package controller.convert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import model.fsm.state.State;
import model.fsm.transition.Transition;
import model.fsm.transition.TransitionFunction;

public class GenerateDot {

	//---------- Transition
	
	/**
	 * This method converts the information stored in this TransitionFunction object into the dot-form
	 * representation for use with GraphViz. 
	 * 
	 * @return - Returns a String object representing the dot-form version of the information stored by this TransitionFunction object.
	 */
	
	public String makeDotString() {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, ArrayList<Transition>> entry : transitions.entrySet()) {
			String firstState = entry.getKey();
			ArrayList<Transition> thisTransitions = entry.getValue();
			for(Transition aTransition : thisTransitions) {
				sb.append(aTransition.makeDotString(firstState));
			} // for aTransition
		} // for entry
		return sb.toString();
	}
	
	/**
	 * This method converts the information stored in this TransitionFunction object into the dot-form
	 * representation for use with GraphViz. It excludes the transitions which are present in the other
	 * transition function passed as a parameter.
	 * 
	 * @param other - TransitionFunction which uses the same FSM's states mapping to different things.
	 * @return - Returns a String object representing the dot-form version of the information stored by
	 * this TransitionFunction object, excluding any transitions in the other transition function.
	 */
	
	public String makeDotStringExcluding(TransitionFunction other) {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, ArrayList<Transition>> entry : transitions.entrySet()) {
			String firstState = entry.getKey();
			ArrayList<Transition> thisTransitions = entry.getValue();
			for(Transition aTransition : thisTransitions) {
				ArrayList<State> otherTransitionStates = other.getTransitionStates(firstState, aTransition.getTransitionEvent());
				if(otherTransitionStates == null || otherTransitionStates.size() == 0) {
					sb.append(aTransition.makeDotStringMayTransition(firstState));
				} // if
			} // for aTransition
		} // for entry
		return sb.toString();
	}
	
	public String makeDotString(String firstState) {
		String eventDeal = "color = ";
		
		if(event.getStringObservability()) {		//Red means System can't see
			eventDeal += "\"black\"";
		}
		else {
			eventDeal += "\"red\"";
		}
		
		eventDeal += " arrowhead = \"normal";
		
		if(!event.getStringAttackerObservability()) {		//Dot means Attacker can't see
			eventDeal += "odot";
		}
		
		if(!event.getStringControllability()) {		//Diamond means System can't control
			eventDeal += "diamond";
		}
		
		eventDeal += "\"";
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + firstState + "\"->{\"");
		Iterator<String> itr = states.iterator();
		while(itr.hasNext()) {
			String s = itr.next();
			sb.append(s);
			if(itr.hasNext())
				sb.append("\",\"");
		} // while there are more states
			
		sb.append("\"} [label = \"" + event + "\" " + eventDeal + " ]; \n");
		return sb.toString();
	}
	
	/**
	 * This method generates a String object which has the dot representation of this Transition,
	 * but with the addition of specifying dotted/solid lines for May/Must transitions in a
	 * ModalSpecification object.
	 * 
	 * @param firstState - State object possessing this Transition; it leads to the instance variable State via the associated String. 
	 * @return - Returns a String object containing the dot representation of this Transition.
	 */
	
	public String makeDotStringMayTransition(String firstState) {
		String eventDeal = "color = ";
		
		if(event.getStringObservability()) {		//Red means System can't see
			eventDeal += "\"black\"";
		}
		else {
			eventDeal += "\"red\"";
		}
		
		eventDeal += " arrowhead = \"normal";
		
		if(!event.getStringAttackerObservability()) {		//Dot means Attacker can't see
			eventDeal += "odot";
		}
		
		if(!event.getStringControllability()) {		//Diamond means System can't control
			eventDeal += "diamond";
		}
		
		eventDeal += "\"";
		eventDeal += " style=dotted";
		
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + firstState + "\"->{\"");
		Iterator<String> itr = states.iterator();
		while(itr.hasNext()) {
			String s = itr.next();
			sb.append(s);
			if(itr.hasNext())
				sb.append("\",\"");
		} // while there are more states
		
		return "\"" + firstState + "\"->{\"" + sb.toString() + "\"} [label = \"" + event + "\" " + eventDeal + " ]; \n";
	}
	
	//-------------- Transition System
	
	
	/**
	 * This method processes the information stored by the FSM object to generate a
	 * String in the dot-form format for consumption by the GraphViz program to
	 * create a visual representation of the FSM.
	 * 
	 * @return - Returns a String object containing the dot-form representation of this FSM object.
	 */
	
	public String makeDotString() {
		String statesInDot = states.makeDotString();	//Have the StateMap do its thing
		String transitionsInDot = transitions.makeDotString();	//Have the TransitionFunction do its thing
		return statesInDot + transitionsInDot;	//Return 'em all
	}
	
	
}
