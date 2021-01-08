package model.fsm;

import java.util.ArrayList;

import model.AttributeList;
import model.fsm.component.EventMap;
import model.fsm.component.StateMap;
import model.fsm.component.transition.TransitionFunction;

/**
 * This abstract class provides the framework for Finite State Machine objects
 * and their like, handling the presence of States, Events, and Transitions of
 * generic types to permit different variations using the same design.
 * 
 * This abstract class is a part of the fsm package.
 * 
 * @author Ada Clevinger and Graeme Zinck
 *
 */

public class TransitionSystem {
	
//---  Instance Variables   -------------------------------------------------------------------

	/** StateMap object possessing all the States associated to this TransitionSystem object */
	private StateMap states;
	/** EventMap object possessing all the Events associated to this TransitionSystem object */
	private EventMap events;
	/** TransitionFunction<<r>T> object mapping states to sets of transitions (which contain the state names). */
	private TransitionFunction transitions;
	/** String object possessing the identification for this TransitionSystem object. */
	private String id;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public TransitionSystem(String inId, ArrayList<String> attribState, ArrayList<String> attribEvent, ArrayList<String> attribTransition) {
		states = new StateMap(attribState);
		events = new EventMap(attribEvent);
		transitions = new TransitionFunction(attribTransition);
		id = inId;
	}
	
	public TransitionSystem(String inId) {
		id = inId;
	}
	
//---  Operations   ---------------------------------------------------------------------------

	/**
	 * Renames all the states in the set of states in the FSM so that
	 * states are named sequentially ("0", "1", "2"...).
	 */
	
	public void renameStates() {
		states.renameStates();
	}
	
	public void renameState(String old, String newName) {
		states.renameState(old, newName);
	}
	
	public String compileStateName(ArrayList<String> in) {
		String out = "(";
		for(int i = 0; i < in.size(); i++) {
			out += in.get(i) + (i + 1 < in.size() ? "," : "");
		}
		return out + ")";
	}

	public void compileStateAttributes(ArrayList<String> in, TransitionSystem context) {
		String nom = compileStateName(in);
		if(!states.stateExists(nom)) {
			states.addState(nom);
		}
		for(String s : getStateAttributes()) {
			boolean cond = AttributeList.getAON(s);
			boolean use = cond;
			for(String t : in) {
				use = cond ? (use && context.getStateAttribute(t, s)) : (use || context.getStateAttribute(t, s));
			}
			states.setStateAttribute(nom, s, use);
		}
	}
	
	public void compileStateAttributes(ArrayList<String> in, ArrayList<TransitionSystem> context) {
		String nom = compileStateName(in);
		if(!states.stateExists(nom)) {
			states.addState(nom);
		}
		for(String s : getStateAttributes()) {
			boolean cond = AttributeList.getAON(s);
			boolean use = cond;
			for(int i = 0; i < in.size(); i++) {
				boolean loc = context.get(i).getStateAttribute(in.get(i), s);
				use = cond ? (use && loc) : (use || loc);
			}
			states.setStateAttribute(nom, s, use);
		}
	}
	
	public void compileEventAttributes(String eventName, ArrayList<TransitionSystem> context) {
		if(!events.contains(eventName)) {
			events.addEvent(eventName);
		}
		for(String s : getEventAttributes()) {
			boolean cond = AttributeList.getAON(s);
			boolean use = cond;
			for(TransitionSystem e : context) {
				use = cond ? (use && e.getEventAttribute(eventName, s)) : (use || e.getEventAttribute(eventName, s));
			}
			events.setEventAttribute(eventName, s, use);
		}
	}

	public void copyAttributes(TransitionSystem ot) {
		setStateAttributes(ot.getStateAttributes());
		setEventAttributes(ot.getEventAttributes());
		setTransitionAttributes(ot.getTransitionAttributes());
	}
	
	//-- Merge  -----------------------------------------------
	
	/**
	 * This method copies the states of a provided FSM object into the current FSM object.
	 * 
	 * @param other - FSM object whose states are copied.
	 */
	
	public void mergeStates(TransitionSystem other) {
		states.mergeStates(other.getStateMap());
	} 

	/**
	 * This method copies the events of a provided FSM object into the current FSM object.
	 * 
	 * @param other - An FSM object whose events are copied.
	 */
	
	public void mergeEvents(TransitionSystem other) {
		events.mergeEvents(other.getEventMap());
	} 
	
	/**
	 * This method copies the transitions of another FSM into the current FSM.
	 * 
	 * @param other - An FSM object whose transitions are copied.
	 */
	
	public void mergeTransitions(TransitionSystem other) {
		transitions.mergeTransitions(other.getTransitionFunction());
	} 
	
	//-- Meta  ------------------------------------------------
	
	/**
	 * This method converts an FSM object into a text file which can be read back in and used to recreate
	 * an FSM later, or used for analytical purposes. A helper class, ReadWrite, manages the brunt
	 * of this process, but for the various special features of FSM objects, each has to handle
	 * itself separately.
	 * 
	 * @param filePath - String object representing the path to the folder to place the text file.
	 * @param name - String object representing the name of the text file to create.
	 */
	
	public void toTextFile(String filePath, String name) {
		
	}
	
//---  Adder Methods   ------------------------------------------------------------------------
	
	/**
	 * This method adds a new State to the StateMap<<r>State> object, returning the State object in the FSM
	 * that was either newly added or the already existing State corresponding to the provided State name.
	 * 
	 * @param stateName - String object representing the name of a State to add to the StateMap
	 * @return - Returns a State object representing the corresponding State in the FSM that was just added.
	 */
	
	public void addState(String stateName) {
		if(!states.stateExists(stateName))
			states.addState(stateName);
	}

	public void addState(String stateName, StateMap context) {
		if(!states.stateExists(stateName))
			states.addState(stateName, context);
	}
	
	/**
	 * Adds a map of states to the composition of an FSM.
	 * @param composed HashMap mapping States to an ArrayList of States, which is
	 * which is to be added to the state composition of the transition system.
	 */

	public void addStateComposition(String main, ArrayList<String> pieces) {
		states.addStateComposition(main, pieces);
	}

	public void addEvent(String eventName) {
		if(!events.eventExists(eventName))
			events.addEvent(eventName);
	}

	public void addEvent(String eventName, EventMap context) {
		if(!events.eventExists(eventName))
			events.addEvent(eventName, context);
	}
	
	/**
	 * This method permits the adding of a new Transition to the Transition System in the format
	 * of two States and an Event, representing a State leading to another State by the defined
	 * Event; a corresponding method exists within the TransitionFunction to take these arguments.
	 * 
	 * @param state - State object representing the State that can lead to the other State by the defined Event.
	 * @param event - Event object representing the Event by which the State -> State2 Transition occurs.
	 * @param state2 - State object representing the target State that is led to by the defined Event from the defined State.
	 */
	
	public void addTransition(String state, String event, String state2) {
		addState(state);
		addState(state2);
		addEvent(event);
		transitions.addTransition(state, event, state2);
	}

	public void addTransition(String state, String event, String state2, TransitionFunction context) {
		transitions.addTransition(state, event, state2, context);
	}
	
//---  Remover Methods   ----------------------------------------------------------------------
	
	/**
	 * This method removes a State object from the calling FSM object as described by the
	 * provided String object, further handling the cases of the State being Initial or appearing
	 * in the TransitionFunction associated to this FSM object.
	 * 
	 * @param stateName - String object representing the name of the State object to remove from the calling FSM object.
	 * @return - Returns a boolean value representing the outcome of the operation: true if the state was removed, false if the state did not exist.
	 */
	
	public void removeState(String stateName) {
		if(states.stateExists(stateName)) {
			transitions.removeStateTransitions(stateName);
			states.removeState(stateName);
		}
	}
	
	public void removeEvent(String event) {
		transitions.removeEventTransitions(event);
		events.removeEvent(event);
	}
	
	/**
	 * This method handles the removing of a Transition object from the calling FSM object's
	 * TransitionFunction, as described by the provided format of Transition information: 3 String objects
	 * representing the State leading, by a defined Event, to another State.
	 * 
	 * @param state1 - String object corresponding to the origin State object for the Transition object.
	 * @param eventName - String object corresponding to the Event for the Transition object.
	 * @param state2 - String object corresponding to the destination State object for the Transition object.
	 * @return - Returns a boolean value; true if the Transition was removed, false if it did not exist.
	 */
		
	public void removeTransition(String state1, String eventName, String state2) {
		transitions.removeTransition(state1, eventName, state2);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------

	/**
	 * Setter method that assigns the parameter inId as the id for the FSM, which is used to identify the FSM.
	 * 
	 * @param inId - String object representing the new name associated to this FSM object.
	 */
	
	public void setId(String inId) {
		id = inId;
	}
	
	/**
	 * Setter method that assigns a new StateMap<<r>State> to replace the previously assigned set of States.
	 * 
	 * @param inState - StateMap<<r>State> object that assigns a new set of Events to this FSM object
	 */
	
	public void setFSMStateMap(StateMap inState) {
		states = inState;
	}
	
	/**
	 * Setter method that assigns a new EventMap<<r>Event> object to replace the previously assigned set of Events.
	 * 
	 * @param inEvent - EventMap<<r>Event> object that assigns a new set of Events to this FSM object
	 */
	
	public void setFSMEventMap(EventMap inEvent) {
		events = inEvent;
	}
	
	/**
	 * Setter method that assigns a new TransitionFunction<<r>State, T, Event> object to replace the previously assigned set of Transitions.
	 * 
	 * @param inTrans - TransitionFunction<<r>State, T, Event> object that assigns a new set of Transitions to this FSM object
	 */
	
	public void setFSMTransitionFunction(TransitionFunction inTrans) {
		transitions = inTrans;
	}
	
	//-- State  -----------------------------------------------
	
	public void setStateAttributes(ArrayList<String> attrib) {
		states.setAttributes(attrib);
	}
	
	public void setStateAttribute(String nom, String ref, boolean val) {
		states.setStateAttribute(nom, ref, val);
	}
	
	/**
	 * Setter method that assigns a set of States as the States that compose the single defined State; that is,
	 * the parameter State aggregate is defined to have been made up of the parameter State ... varargs pieces.
	 * 
	 * @param aggregate - State object that represents a State which has been previously generated as the composition of a set of States.
	 * @param pieces - State ... varargs object that represents the set of States which compose the State aggregate.
	 */
	
	public void setStateComposition(String aggregate, ArrayList<String> pieces) {
		states.setStateComposition(aggregate, pieces);
	}

	/**
	 * Setter method that allows for the assignation of a new HashMap<<r>State, ArrayList<<r>State>> object
	 * as the set of States and their corresponding set of States which compose them in this TransitionSystem
	 * object's StateMap.
	 * 
	 * @param composed - A HashMap<<r>State, ArrayList<<r>State>> object to replace the StateMap's composition instance variable.  
	 */
	
	public void setStateCompositions(StateMap in) {
		states.setStateCompositions(in);
	}

	//-- Event  -----------------------------------------------
	
	public void setEventAttributes(ArrayList<String> attrib) {
		events.setAttributes(attrib);
	}
	
	public void setEventAttribute(String nom, String ref, boolean val) {
		events.setEventAttribute(nom, ref, val);
	}
	
	//-- Transition  ------------------------------------------
	
	public void setTransitionAttributes(ArrayList<String> attrib) {
		transitions.setAttributes(attrib);
	}
	
	public void setTransitionAttribute(String nom, String event, String ref, boolean val) {
		transitions.setTransitionAttribute(nom, event, ref, val);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method that is used to acquire the ID (represented as a String object) associated to this FSM object.
	 * 
	 * @return - Returns a String object representing the ID associated to this FSM object.
	 */
	
	public String getId() {
		return id;
	}
	
	/**
	 * Getter method that returns the StateMap object associated to the TransitionSystem object.
	 * 
	 * @return - Returns a StateMap object containing the Event objects associated to this TransitionSystem object.
	 */
	
	public StateMap getStateMap() {
		return states;
	}
	
	public EventMap getEventMap() {
		return events;
	}
	
	/**
	 * Getter method that returns the TransitionFunction<<r>T> object containing all
	 * the Transitions associated to this FSM object.
	 * 
	 * @return - Returns a TransitionFunction<<r>T> object containing all the Transitions associated to this FSM object.
	 */
	
	public TransitionFunction getTransitionFunction() {
		return transitions;
	}

	//-- State  -----------------------------------------------

	public boolean hasStateAttribute(String ref) {
		return states.getAttributes().contains(ref);
	}
	
	public ArrayList<String> getStatesWithAttribute(String attrib){
		return states.getStatesWithAttribute(attrib);
	}
	
	public ArrayList<String> getStateAttributes(){
		return states.getAttributes();
	}
	
	public ArrayList<String> getStateNames(){
		return states.getNames();
	}

	/**
	 * Getter method that requests a particular ArrayList<<r>State> object corresponding to a particular
	 * State object that, if found within the HashMap<<r>State, ArrayList<<r>State>>, is composed of
	 * a set of States store within the ArrayList<<r>State> object.
	 * 
	 * @param state - State object representing the aggregate-State composed of the returned set of States.
	 * @return - Returns an ArrayList<<r>State> object representing the States that compose the provided State object.
	 */
	
	public ArrayList<String> getStateComposition(String state){
		return states.getStateComposition(state);
	}

	/**
	 * Getter method that requests whether or not a State object exists in the calling FSM object's
	 * StateMap, the supplied String object representing the State object.
	 * 
	 * @param stateName - String object representing the State object to query the FSM object's StateMap for; represents its name.
	 * @return - Returns a boolean value; true if the State object exists in the FSM, false otherwise.
	 */
	
	public boolean stateExists(String stateName) {
		return states.stateExists(stateName);
	}

	public Boolean getStateAttribute(String nom, String ref) {
		return states.getStateAttribute(nom, ref);
	}
	
	//-- Event  -----------------------------------------------

	public boolean hasEventAttribute(String ref) {
		return events.getAttributes().contains(ref);
	}
	
	public ArrayList<String> getEventsWithAttribute(String attrib){
		return events.getEventsWithAttribute(attrib);
	}
	
	public ArrayList<String> getEventAttributes(){
		return events.getAttributes();
	}
	
	public ArrayList<String> getEventNames(){
		return events.getEventNames();
	}

	public boolean eventExists(String eventName) {
		return events.contains(eventName);
	}
	
	public Boolean getEventAttribute(String nom, String ref) {
		return events.getEventAttribute(nom, ref);
	}
	
	//-- Transition  ------------------------------------------

	public boolean hasTransitionAttribute(String ref) {
		return transitions.getAttributes().contains(ref);
	}
	
	public ArrayList<String> getTransitionsWithAttribute(String attrib){
		return transitions.getTransitionsWithAttribute(attrib);
	}
	
	public ArrayList<String> getTransitionAttributes(){
		return transitions.getAttributes();
	}
	
	public ArrayList<String> getStateTransitionEvents(String state){
		return transitions.getStateEvents(state);
	}
	
	public ArrayList<String> getStateEventTransitionStates(String state, String event){
		return transitions.getTransitionStates(state, event);
	}
	
	public Boolean getTransitionAttribute(String nom, String event, String ref) {
		return transitions.getTransitionAttribute(nom, event, ref);
	}

//---  Mechanics   ----------------------------------------------------------------------------

	public TransitionSystem copy() {
		TransitionSystem out = new TransitionSystem(getId(), getStateAttributes(), getEventAttributes(), getTransitionAttributes());
		out.mergeEvents(this);
		out.mergeStates(this);
		out.mergeTransitions(this);
		return out;
	}
	
}
