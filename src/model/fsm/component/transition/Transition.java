package model.fsm.component.transition;

import java.util.Iterator;

import model.fsm.component.Entity;

import java.util.ArrayList;

/**
 * This interface provides the framework for the structure of Transition objects, leaving
 * the implementation to the classes that use this interface. 
 * 
 * This interface is part of the support.transition package.
 * 
 * @author Ada Clevinger and Graeme Zinck
 */

public class Transition extends Entity implements Comparable<Transition> {
	
//--- Instance Variables   --------------------------------------------------------------------
	
	/** ArrayList<<r>State> object holding all State objects associated to the String associated to this Transition object*/
	private ArrayList<String> states;
	
//--- Constructors   --------------------------------------------------------------------------

	public Transition(String inEvent, String state) {
		super(inEvent);
		states = new ArrayList<String>();
		states.add(state);
	}
	
	/**
	 * Constructor for a Transition object, assigning a single String object and a list of States which the event can lead to.
	 * 
	 * @param inString - String object representing the event that leads to the associated transition states.
	 * @param inStates - List of State objects representing the States led to by the String associated with this Transition object.
	 */
	
	public Transition(String inString, ArrayList<String> inStates) {
		super(inString);
		states = inStates;
	}
	
	protected Transition(Entity base) {
		super(base.getName());
		copyAttributes(base);
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public Transition copy() {
		Transition out = new Transition(this);
		for(String s : states) {
			out.addTransitionState(s);
		}
		return out;
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
		setName(in);
	}
	
//--- Getter Methods   ------------------------------------------------------------------------
	
	public String getEvent() {
		return getName();
	}
	
	public ArrayList<String> getStates() {
		return states;
	}
	
	public boolean hasState(String stateName) {
		return states.contains(stateName);
	}
	
	public boolean isEmpty() {
		return states.size() == 0;
	}
	
//---  Adder Methods   ------------------------------------------------------------------------
	
	public void addTransitionState(String in) {
		if(!states.contains(in))
			states.add(in);
	}
	
//---  Remover Methods   ----------------------------------------------------------------------
	
	public void removeTransitionState(String stateName) {
		states.remove(stateName);
	}
	
	public void removeTargetStates() {
		states.clear();
	}

//---  Mechanics   ----------------------------------------------------------------------------
	
	@Override
	public int compareTo(Transition o) {
		return toString().compareTo(o.toString());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getName() + " goes to the states: ");
		Iterator<String> itr = states.iterator();
		while(itr.hasNext()) {
			sb.append(itr.next());
			if(itr.hasNext()) sb.append(", ");
		}
		return sb.toString();
	}

}
