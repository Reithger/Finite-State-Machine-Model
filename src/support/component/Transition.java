package support.component;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * This interface provides the framework for the structure of Transition objects, leaving
 * the implementation to the classes that use this interface. 
 * 
 * This interface is part of the support.transition package.
 * 
 * @author Ada Clevinger and Graeme Zinck
 */

public class Transition implements Comparable<Transition> {
	
//--- Instance Variables   --------------------------------------------------------------------
	
	/** Event instance variable representing the Event associated to this object*/
	public Event event;
	/** ArrayList<<r>State> object holding all State objects associated to the Event associated to this Transition object*/
	private ArrayList<State> states;
	
//--- Constructors   --------------------------------------------------------------------------
	
	/**
	 * Constructor for a Transition object, assigning a single Event object and a list of States which the event can lead to.
	 * 
	 * @param inEvent - Event object representing the event that leads to the associated transition states.
	 * @param inStates - List of State objects representing the States led to by the Event associated with this Transition object.
	 */
	
	public Transition(Event inEvent, State ... inStates) {
		event = inEvent;
		states = new ArrayList<State>();
		for(int i = 0; i < inStates.length; i++)
			states.add(inStates[i]);
	}
	
	/**
	 * Constructor for a Transition object, assigning a single Event object and a list of States which the event can lead to.
	 * 
	 * @param inEvent - Event object representing the event that leads to the associated transition states.
	 * @param inStates - Collection of State objects representing the States led to by the Event associated with this Transition object.
	 */
	
	public Transition(Event inEvent, Collection<State> inStates) {
		event = inEvent;
		states = new ArrayList<State>(inStates);
	}
	
	/**
	 * Constructor for a Transition object, assigning the event and states empty values until added later on.
	 */
	
	public Transition() {
		event = null;
		states = new ArrayList<State>();
	}
	
//---  Operations   ---------------------------------------------------------------------------

	public String makeDotString(State firstState) {
		String eventDeal = "color = ";
		
		if(event.getEventObservability()) {		//Red means System can't see
			eventDeal += "\"black\"";
		}
		else {
			eventDeal += "\"red\"";
		}
		
		eventDeal += " arrowhead = \"normal";
		
		if(!event.getEventAttackerObservability()) {		//Dot means Attacker can't see
			eventDeal += "odot";
		}
		
		if(!event.getEventControllability()) {		//Diamond means System can't control
			eventDeal += "diamond";
		}
		
		eventDeal += "\"";
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + firstState.getStateName() + "\"->{\"");
		Iterator<State> itr = states.iterator();
		while(itr.hasNext()) {
			State s = itr.next();
			sb.append(s.getStateName());
			if(itr.hasNext())
				sb.append("\",\"");
		} // while there are more states
			
		sb.append("\"} [label = \"" + event.getEventName() + "\" " + eventDeal + " ]; \n");
		return sb.toString();
	}
	
	/**
	 * This method generates a String object which has the dot representation of this Transition,
	 * but with the addition of specifying dotted/solid lines for May/Must transitions in a
	 * ModalSpecification object.
	 * 
	 * @param firstState - State object possessing this Transition; it leads to the instance variable State via the associated Event. 
	 * @return - Returns a String object containing the dot representation of this Transition.
	 */
	
	public String makeDotStringMayTransition(State firstState) {
		String eventDeal = "color = ";
		
		if(event.getEventObservability()) {		//Red means System can't see
			eventDeal += "\"black\"";
		}
		else {
			eventDeal += "\"red\"";
		}
		
		eventDeal += " arrowhead = \"normal";
		
		if(!event.getEventAttackerObservability()) {		//Dot means Attacker can't see
			eventDeal += "odot";
		}
		
		if(!event.getEventControllability()) {		//Diamond means System can't control
			eventDeal += "diamond";
		}
		
		eventDeal += "\"";
		eventDeal += " style=dotted";
		
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + firstState.getStateName() + "\"->{\"");
		Iterator<State> itr = states.iterator();
		while(itr.hasNext()) {
			State s = itr.next();
			sb.append(s.getStateName());
			if(itr.hasNext())
				sb.append("\",\"");
		} // while there are more states
		
		return "\"" + firstState.getStateName() + "\"->{\"" + sb.toString() + "\"} [label = \"" + event.getEventName() + "\" " + eventDeal + " ]; \n";
	}
	
	public Transition generateTransition(){
		Transition outbound = new Transition();
		return outbound;
	}

//--- Setter Methods   ------------------------------------------------------------------------
	
	/**
	 * Setter method to replace the current ArrayList<<r>State> of State names with the provided one
	 * 
	 * @param in - ArrayList<<r>State> object representing the list of States led to by the Event associated to this Transition object
	 */
	
	public void setTransitionState(ArrayList<State> in) {
		states = in;
	}
	
	public void setTransitionState(State in) {
		if(!states.contains(in))
			states.add(in);
	}
	
	public void setTransitionEvent(Event in) {
		event = in;
	}
	
//--- Getter Methods   ------------------------------------------------------------------------
	
	public Event getTransitionEvent() {
		return event;
	}
	
	public ArrayList<State> getTransitionStates() {
		return states;
	}
	
	public boolean stateExists(String stateName) {
		return states.contains(new State(stateName));
	}
	
	public boolean stateExists(State inState) {
		return states.contains(inState);
	}
	
//--- Manipulations   -------------------------------------------------------------------------
	
	public boolean addTransitionState(State stateNew) {
		if(states.indexOf(stateNew) == -1) {
			states.add(stateNew);
			return true;
		}
		return false;
	}
	
	public boolean removeTransitionState(String stateName) {
		states.remove(new State(stateName));
		return (states.size() == 0);
	}
	
	public boolean removeTransitionState(State inState) {
		states.remove(inState);
		return (states.size() == 0);
	}
	
	public boolean removeTransitionStates(Collection<State> inStates) {
		states.removeAll(inStates);
		return (states.size() == 0);
	}

	@Override
	public int compareTo(Transition o) {
		// Simply compares the names of the two tranisitons' events
		return this.event.getEventName().compareTo(o.getTransitionEvent().getEventName());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(event.getEventName() + " goes to the states: ");
		Iterator<State> itr = states.iterator();
		while(itr.hasNext()) {
			sb.append(itr.next().getStateName());
			if(itr.hasNext()) sb.append(", ");
		}
		return sb.toString();
	}

}
