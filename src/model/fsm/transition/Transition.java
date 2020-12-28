package model.fsm.transition;

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
	
	/** String instance variable representing the String associated to this object*/
	public String event;
	/** ArrayList<<r>State> object holding all State objects associated to the String associated to this Transition object*/
	private ArrayList<String> states;
	
//--- Constructors   --------------------------------------------------------------------------

	/**
	 * Constructor for a Transition object, assigning a single String object and a list of States which the event can lead to.
	 * 
	 * @param inString - String object representing the event that leads to the associated transition states.
	 * @param inStates - List of State objects representing the States led to by the String associated with this Transition object.
	 */
	
	public Transition(String inString, String ... inStates) {
		event = inString;
		states = new ArrayList<String>();
		for(int i = 0; i < inStates.length; i++)
			states.add(inStates[i]);
	}
	
	/**
	 * Constructor for a Transition object, assigning a single String object and a list of States which the event can lead to.
	 * 
	 * @param inString - String object representing the event that leads to the associated transition states.
	 * @param inStates - Collection of State objects representing the States led to by the String associated with this Transition object.
	 */
	
	public Transition(String inString, Collection<String> inStates) {
		event = inString;
		states = new ArrayList<String>(inStates);
	}

//--- Setter Methods   ------------------------------------------------------------------------
	
	/**
	 * Setter method to replace the current ArrayList<<r>State> of State names with the provided one
	 * 
	 * @param in - ArrayList<<r>State> object representing the list of States led to by the String associated to this Transition object
	 */
	
	public void setTransitionStates(ArrayList<String> in) {
		states = in;
	}

	public void setTransitionEvent(String in) {
		event = in;
	}
	
//--- Getter Methods   ------------------------------------------------------------------------
	
	public String getEvent() {
		return event;
	}
	
	public ArrayList<String> getStates() {
		return states;
	}
	
	public boolean hasState(String stateName) {
		return states.contains(stateName);
	}
	
//---  Adder Methods   ------------------------------------------------------------------------
	
	public void addTransitionState(String in) {
		if(!states.contains(in))
			states.add(in);
	}
	
//---  Remover Methods   ----------------------------------------------------------------------
	
	public boolean removeTransitionState(String stateName) {
		states.remove(stateName);
		return (states.size() == 0);
	}
	
	public boolean removeTransitionStates(Collection<String> inStates) {
		states.removeAll(inStates);
		return (states.size() == 0);
	}

//---  Mechanics   ----------------------------------------------------------------------------
	
	@Override
	public int compareTo(Transition o) {
		// Simply compares the names of the two transitions' events
		return toString().compareTo(o.toString());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(event + " goes to the states: ");
		Iterator<String> itr = states.iterator();
		while(itr.hasNext()) {
			sb.append(itr.next());
			if(itr.hasNext()) sb.append(", ");
		}
		return sb.toString();
	}

}
