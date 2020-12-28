package model.fsm.transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class models all Transitions in an FSM, storing States and an ArrayList<<r>Transition> of Transitions as <<r>Key, Value> pairs.
 * 
 * This class is a part of the support package.
 * 
 * @author Ada Clevinger and Graeme Zinck
 */

public class TransitionFunction {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** HashMap<<r>String, ArrayList<Transition>> object containing all the transitions from a given state with various events that are possible. */
	protected HashMap<String, ArrayList<Transition>> transitions;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for TransitionFunction objects that initializes the HashMap<State, ArrayList<Transition>> for this object, and
	 * accepts an object extending Transition<<r>S, E> for use as an instance variable.
	 * 
	 * @param obj - Object of the type Transition that extends Transition<<r>S, E> to provide to the TransitionFunction object.
	 */
	
	public TransitionFunction() {
		transitions = new HashMap<String, ArrayList<Transition>>();
	}
	
//---  Adder Methods   ------------------------------------------------------------------------
	
	/**
	 * This method adds a new Transition to the TransitionFunction in a format defined as State1, Event, State2;
	 * State1 leading to State2 via the Event. It creates a new Transition object and appends it to the
	 * ArrayList associated to the leading State in the Transition.
	 * 
	 * @param inState - State object representing the leading State1 in the format State1 -> State2 via Event.
	 * @param event - Event object representing the Event in the format State1 -> State2 via Event.
	 * @param outState - State object representing the target State2 in the format State1 -> State2 via Event.
	 */
	
	public void addTransition(String inState, String event, String outState) {
		ArrayList<Transition> currT = transitions.get(inState);
		if(currT == null) {
			transitions.put(inState, new ArrayList<Transition>());
			currT = transitions.get(inState);
		}
		boolean did = false;
		for(Transition transition : currT) {
			if(transition.getEvent().equals(event)) {
				transition.addTransitionState(outState);
				did = true;
				break;
			}
		}
		if(!did) {
			currT.add(new Transition(event, outState));
		}
	}
	
//---  Remover Methods   ----------------------------------------------------------------------
	
	/**
	 * This method removes entries in the <<r>S, ArrayList<<r>Transition>> Map that correspond to the provided State.
	 * 
	 * @param state - State object representing the Key-set to remove from the <<r>S, ArrayList<<r>Transition>> data set.
	 */
	
	public void removeStateTransitions(String state) {
		transitions.remove(state);
		for(Map.Entry<String, ArrayList<Transition>> entry : transitions.entrySet()) {
			ArrayList<Transition> tToRemove = new ArrayList<Transition>();
			for(Transition transition : entry.getValue())
				if(transition.removeTransitionState(state))
					tToRemove.add(transition);
			entry.getValue().removeAll(tToRemove);
		} // for every entry
	}

	public void removeEventTransitions(String event) {
		for(String s : transitions.keySet()) {
			HashSet<Transition> remv = new HashSet<Transition>();
			for(Transition t : transitions.get(s)) {
				if(t.getEvent().equals(event)) {
					remv.add(t);
				}
			}
			for(Transition t : remv) {
				transitions.get(s).remove(t);
			}
		}
	}
	
	/**
	 * Removes the Transition in this TransitionFunction that corresponds to the provided values in 
	 * the form: State1 leading to State2 via an Event.
	 * 
	 * @param stateFrom - State object that the transition starts from.
	 * @param event - Event object associated with the transition.
	 * @param stateTo - State object that the transition ends at.
	 * @return - Returns a boolean value; true if the transition existed and was removed; false otherwise.
	 */
	
	public boolean removeTransition(String stateFrom, String event, String stateTo) {
		ArrayList<Transition> thisTransitions = transitions.get(stateFrom);
		for(Transition transition : thisTransitions) {
			if(transition.getEvent().equals(event)) {
				if(transition.hasState(stateTo)) {
					boolean shouldDeleteTransition = transition.removeTransitionState(stateTo);
					if(shouldDeleteTransition) thisTransitions.remove(transition);
					return true;
				}
			}
		}
		return false;
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method to acquire an ArrayList<<r>Transition> of Transition objects associated to the provided State object 
	 * 
	 * @param state - State object in an FSM associated to the returned ArrayList<<r>Transition> of Transition objects
	 * @return - Returns an ArrayList<<r>Transition> of Transition objects that are associated to a defined State in an FSM
	 */
	
	public ArrayList<Transition> getStateTransitions(String state) {
		return transitions.get(state) != null ? transitions.get(state) : new ArrayList<Transition>();
	}

	public ArrayList<String> getStateEvents(String state){
		ArrayList<String> out = new ArrayList<String>();
		for(Transition t : transitions.get(state)) {
			out.add(t.getEvent());
		}
		return out;
	}
	
	/**
	 * Getter method to acquire a set of all states and its corresponding transition objects.
	 * 
	 * @return - Returns a Set of map entries with State objects and an ArrayList of the Transitions. (Set<<r>Map, Entry<<r>S, ArrayList<<r>Transition>>>)
	 */
	 
	public HashMap<String, ArrayList<Transition>> getTransitions() {
		return transitions;
	}
	
	/**
	 * Getter method that returns a Collection of State objects, those being each of the stored
	 * States leading to an ArrayList of Transitions associated to that leading State.
	 * 
	 * @return - Returns a Collection<<r>State> object containing all the States in this TransitionFunction which have Transitions.
	 */
	
	public ArrayList<String> getStateNames(){
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(transitions.keySet());
		return out;
	}

	/**
	 * Getter method that retrieves the Transition States at a designated State that correspond to the provided Event.
	 * 
	 * @param state - State object whose Transitions are searched through.
	 * @param event - Event object provided to denote which State Transitions to return corresponding to the provided State object.
	 * @return - Returns an ArrayList<<r>S> of Transition States that the provided State leads to, or null if there are none.
	 */
	
	public ArrayList<String> getTransitionStates(String state, String event) {
		ArrayList<Transition> thisTransitions = transitions.get(state);
		if(thisTransitions != null)
			for(Transition transition : thisTransitions)
				if(transition.getEvent().equals(event))
					return transition.getStates();
		return null;
	}

	/**
	 * Getter method that retrieves if a certain event exists at a certain state.
	 * 
	 * @param state - State object in whose Transitions to search for an Event in.
	 * @param event - Event object to search for in the Transitions of the provided State object.
	 * @return - Returns a boolean value; true if the State has the provided Event in one of its Transitions, false otherwise.
	 */
	
	public boolean eventExists(String state, String event) {
		ArrayList<Transition> thisTransitions = transitions.get(state);
		if(thisTransitions != null) {
//			System.out.println("In state " + state.getStateName() + ", we're looking for " + event.getEventName() + " in " + thisTransitions.toString());
			for(Transition Transition : thisTransitions)
				if(Transition.getEvent().equals(event))
					return true;
		}
		return false;
	}
	
	/**
	 * This method searches among the Transitions stored by this TransitionFunction object
	 * for the presence of a Transition provided as an argument for reference. (Does this
	 * TransitionFunction contain the defined Transition?)
	 * 
	 * @param reference - State object whose associated Transitions in this TransitionFunction object are searched through.
	 * @param transition - Transition extending object that is to be searched for in the Transitions stored by this TransitionFunction object.
	 * @return - Returns a boolean value representing the result of this search; true if the Transition exists, false otherwise.
	 */
	
	public boolean contains(String reference, Transition transition) {
		for(Transition t : getStateTransitions(reference)) {
			if(t.equals(transition))
				return true;
		}
		return false;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Setter method that assigns a new paired <<r>State, ArrayList<<r>Transition>> data set to the Transitions data structure,
	 * overwriting any previous entry for that State.
	 * 
	 * @param state - State object representing the Key in the stored <<r>Key, Value> data structure, <<r>State, ArrayList<<r>Transition>>.
	 * @param inTransitions - ArrayList<<r>Transition> of Transition objects to become the new Value stored in a <<r>Key, Value> data structure.
	 */
	
	public void putTransitions(String state, ArrayList<Transition> inTransitions) {
		transitions.put(state, inTransitions);
	}
	
}
