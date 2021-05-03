package model.fsm.component.transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
	private HashMap<String, ArrayList<Transition>> transitions;
	
	private ArrayList<String> attributes;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for TransitionFunction objects that initializes the HashMap<State, ArrayList<Transition>> for this object, and
	 * accepts an object extending Transition<<r>S, E> for use as an instance variable.
	 * 
	 * @param obj - Object of the type Transition that extends Transition<<r>S, E> to provide to the TransitionFunction object.
	 */
	
	public TransitionFunction(ArrayList<String> defAttrib) {
		transitions = new HashMap<String, ArrayList<Transition>>();
		attributes = defAttrib == null ? new ArrayList<String>() : defAttrib;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void mergeTransitionFunctions(TransitionFunction in) {
		mergeTransitions(in);
	}
	
	public void mergeTransitions(TransitionFunction in) {
		for(String s : in.getStateNames()) {
			addTransitions(s, in.getTransitions(s));
		}
	}
	
	public void renameEvent(String old, String newNom) {
		for(String s : getStateNames()) {
			Transition ol = getTransition(s, old);
			Transition noo = getTransition(s, newNom);
			if(noo != null) {
				for(String t : ol.getStates()) {
					noo.addTransitionState(t);
				}
				removeTransition(s, old);
			}
			else {
				ol.setName(newNom);
			}
		}
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
		Transition t = getTransition(inState, event);
		if(t != null) {
			t.addTransitionState(outState);
		}
		else {
			if(transitions.get(inState) == null) {
				transitions.put(inState, new ArrayList<Transition>());
			}
			transitions.get(inState).add(new Transition(event, outState));
			t = getTransition(inState, event);
		}
		LinkedList<String> use = new LinkedList<String>();
		use.addAll(attributes);
		t.setAttributes(use);
	}
	
	/**
	 * Setter method that assigns a new paired <<r>State, ArrayList<<r>Transition>> data set to the Transitions data structure,
	 * overwriting any previous entry for that State.
	 * 
	 * @param state - State object representing the Key in the stored <<r>Key, Value> data structure, <<r>State, ArrayList<<r>Transition>>.
	 * @param inTransitions - ArrayList<<r>Transition> of Transition objects to become the new Value stored in a <<r>Key, Value> data structure.
	 */
	
	public void addTransitions(String state, ArrayList<Transition> inTransitions) {
		if(transitions.get(state) == null) {
			transitions.put(state, new ArrayList<Transition>());
		}
		for(Transition t : inTransitions) {
			Transition ref = getTransition(state, t.getEvent());
			if(ref == null) {
				transitions.get(state).add(t);
			}
			else {
				for(String s : t.getStates()) {
					if(!ref.hasState(s)) {
						ref.addTransitionState(s);
					}
				}
			}
		}
	}
	
	public void addTransition(String state, String event, String target, TransitionFunction context) {
		if(transitions.get(state) == null) {
			transitions.put(state, new ArrayList<Transition>());
		}
		if(getTransition(state, event) == null) {
			transitions.get(state).add(context.getTransition(state, event).copy());
			getTransition(state, event).removeTargetStates();
			getTransition(state, event).addTransitionState(target);
		}
		else {
			getTransition(state, event).addTransitionState(target);
			getTransition(state, event).copyAttributes(context.getTransition(state, event));
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
			for(Transition transition : entry.getValue()) {
				transition.removeTransitionState(state);
				if(transition.isEmpty())
					tToRemove.add(transition);
			}
			entry.getValue().removeAll(tToRemove);
		} // for every entry
	}

	public void removeEventTransitions(String event) {
		for(String s : transitions.keySet()) {
			Transition t = getTransition(s, event);
			if(t != null) {
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
	
	public void removeTransition(String stateFrom, String event, String stateTo) {
		ArrayList<Transition> thisTransitions = transitions.get(stateFrom);
		for(int i = 0; i < thisTransitions.size(); i++) {
			Transition transition = thisTransitions.get(i);
			if(transition.getEvent().equals(event)) {
				if(transition.hasState(stateTo)) {
					transition.removeTransitionState(stateTo);
					if(transition.isEmpty())
						thisTransitions.remove(transition);
				}
			}
		}
	}

	public void removeTransition(String stateFrom, String event) {
		for(int i = 0; i < getTransitions(stateFrom).size(); i++) {
			Transition t = getTransitions(stateFrom).get(i);
			if(t.getEvent().equals(event)) {
				getTransitions(stateFrom).remove(i);
				i--;
			}
		}
	}
	
//---  Setter Methods   -----------------------------------------------------------------------

	public void setAttributes(ArrayList<String> attrib) {
		attributes = attrib;
		for(String s : getStateNames()) {
			for(Transition e : getTransitions(s)) {
				LinkedList<String> use = new LinkedList<String>();
				use.addAll(attributes);
				e.wipeAttributes();
				e.setAttributes(use);
			}
		}

	}

	public void setTransitionAttribute(String state, String event, String ref, boolean val) {
		getTransition(state, event).setAttributeValue(ref, val);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	public ArrayList<String> getTransitionsWithAttribute(String attrib){
		ArrayList<String> out = new ArrayList<String>();
		for(String s : getStateNames()) {
			for(String e : getStateEvents(s)) {
				if(getTransitionAttribute(s, e, attrib)) {
					out.add(s);
				}
			}
		}
		return out;
	}
	
	public ArrayList<String> getAttributes(){
		return attributes;
	}
	
	public Boolean getTransitionAttribute(String state, String event, String ref) {
		return getTransition(state, event).getAttributeValue(ref);
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

	public ArrayList<String> getStateEvents(String state){
		ArrayList<String> out = new ArrayList<String>();
		if(transitions.get(state) == null) {
			return out;
		}
		for(Transition t : transitions.get(state)) {
			out.add(t.getEvent());
		}
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
	 * Getter method to acquire a set of all states and its corresponding transition objects.
	 * 
	 * @return - Returns a Set of map entries with State objects and an ArrayList of the Transitions. (Set<<r>Map, Entry<<r>S, ArrayList<<r>Transition>>>)
	 */
	 
	protected HashMap<String, ArrayList<Transition>> getTransitions() {
		return transitions;
	}

	protected ArrayList<Transition> getTransitions(String state){
		return transitions.get(state);
	}
	
	protected Transition getTransition(String state, String event) {
		if(transitions.get(state) == null) {
			return null;
		}
		for(Transition t : transitions.get(state)) {
			if(t.getEvent().equals(event)) {
				return t;
			}
		}
		return null;
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
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
		for(Transition t : transitions.get(reference)) {
			if(t.equals(transition))
				return true;
		}
		return false;
	}

}
