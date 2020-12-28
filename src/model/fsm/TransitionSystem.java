package model.fsm;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import model.fsm.event.EventMap;
import model.fsm.state.StateMap;
import model.fsm.transition.TransitionFunction;

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

public abstract class TransitionSystem {
	
//---  Instance Variables   -------------------------------------------------------------------

	/** StateMap object possessing all the States associated to this TransitionSystem object */
	protected StateMap states;
	/** EventMap object possessing all the Events associated to this TransitionSystem object */
	protected EventMap events;
	/** TransitionFunction<<r>T> object mapping states to sets of transitions (which contain the state names). */
	protected TransitionFunction transitions;
	/** String object possessing the identification for this TransitionSystem object. */
	protected String id;

	/** ArrayList<<r>State> object that holds a list of Initial States for this Non Deterministic FSM object. */
	protected ArrayList<String> initialStates;
	
//---  STUFF I HAD OTHER CLASSES DOING THAT THEY SHOULDN't BE DOING   -------------------
	
	
	/**
	 * This gets the epsilon reaches of all each state, mapping the state to a set
	 * of states which are reachable with unobservable events.
	 * 
	 * @param fsmStates Collection of states which exists in the FSM.
	 * @return Hashmap mapping each state to a hashset of states (which are all reachable
	 */
	
	public HashMap<String, HashSet<String>> getEpsilonReaches(Collection<String> fsmStates) {
		HashMap<State, HashSet<State>> epsilonReach = new HashMap<State, HashSet<State>>();	//Maps a State to all States it is attached to
		for(String s : fsmStates) {						//For all States in the FSM:
			HashSet<String> thisSet = new HashSet<String>();			//Keeps track of all States attached to this State
			thisSet.add(s);										//Add the original State as one in the group
			LinkedList<String> queue = new LinkedList<String>();		//Queue to process all States connected via Unobservable Events
			queue.add(s);											//First Queue entry is the original State
			HashSet<String> visited = new HashSet<String>();			//Keeps track of revisited States
			// Go through all the states connected by unobservable events
			while(!queue.isEmpty()) {
				String top = queue.poll();					//Get the next State
				if(visited.contains(top))					//If already processed, don'Transition re-process the State
					continue;
				visited.add(top);							//Mark it as visited
				for(Transition Transition : this.getTransitions(top)) {	//Process all the State's Transitions
					// If it's an unobservable event, go through all transition states
					if(!(Transition.getEvent()).getEventObservability()) 
						for(State sr : Transition.getTransitionStates()) {
							if(!thisSet.contains(sr)) {
								thisSet.add(sr); //If the Event is unobservable and has not yet been seen, add the State
								queue.add(sr); //As the State is a part of the new aggregated State, check its transitions too
						} // if it doesn'Transition contain it
					} // if we have an unobservable event, go through all the transition states
				} // for each transition
			} // while queue not empty
			epsilonReach.put(s, thisSet);
		} // for each state
		return epsilonReach;
	} // getEpsilonReaches(Collection<S>)
	
	/**
	 * Method to be called on the may transition function with a must transition function passed
	 * in (for all the transitions which must occur). It returns all the states that have transitions
	 * that exist in the mustTransitionFunction but not in the calling function, making the state
	 * inconsistent with the definition of a ModalSpecification.
	 * 
	 * @param mustTransitionFunction - TransitionFunction with the transitions that must occur for
	 * the TransitionSystem.
	 * @return - HashSet of all the States which were found to be inconsistent.
	 */
	
	public HashSet<String> getInconsistentStates(TransitionFunction mustTransitionFunction) {
		// This is the must transitions; we want all the states where there is no transition in
		// other that corresponds to the one in this.
		HashSet<String> badStates = new HashSet<String>();
		
		for(String fromState : mustTransitionFunction.transitions.keySet()) {
			// Get the corresponding sorted transitions from each
			ArrayList<Transition> mustTransitions = mustTransitionFunction.getSortedTransitions(fromState);
			ArrayList<Transition> mayTransitions = this.getSortedTransitions(fromState);
			int mustIndex = 0, mayIndex = 0;
			while(mustIndex < mustTransitions.size() && mayIndex < mayTransitions.size()) {
				// Find the matching may transition
				while(mayIndex < mayTransitions.size() && !mustTransitions.get(mustIndex).equals(mayTransitions.get(mayIndex))) mayIndex++;
				// If there is no corresponding may transition, we have a bad state.
				if(mayIndex >= mayTransitions.size()) badStates.add(fromState);
				mustIndex++;
			}
			if(mustIndex < mustTransitions.size()) badStates.add(fromState);
		}
		return badStates;
	} // getInconsistentStates(TransitionFunction<Transition>)

//---  Operations   ---------------------------------------------------------------------------

	/**
	 * Setter method that aggregates the other setter methods to assign new values to the instance variables containing
	 * information about the State, Transitions, and Events.
	 * 
	 * @param inStates - StateMap<<r>State> object that stores a new set of States to assign to this FSM object
	 * @param inEvents - TransitionFunction<<r>State, T, Event> object that stores a new set of Transitions to assign to this FSM object
	 * @param inTrans - EventMap<<r>Event> object that stores a new set of Events to assign to this FSM object
	 */
	
	public void constructFSM(StateMap inStates, TransitionFunction inTrans, EventMap inEvents) {
		setFSMStateMap(inStates);
		setFSMEventMap(inEvents);
		setFSMTransitionFunction(inTrans);
	}
	
//---  Single-FSM Operations   ----------------------------------------------------------------

	/**
	 * Renames all the states in the set of states in the FSM so that
	 * states are named sequentially ("0", "1", "2"...).
	 */
	
	public void renameStates() {
		states.renameStates();
	} // renameStates()

	/**
	 * This method performs a trim operation on the calling FSM (performing the
	 * makeAccessible() and makeCoAccessible() methods) to make sure only states
	 * that are reachable from Initial States and can reach Marked States are
	 * included.
	 * 
	 * @return - Returns a TransitionSystem<<r>T> object representing the trimmed
	 * version of the calling TransitionSystem object.
	 */
	
	public abstract TransitionSystem trim();
	
	/**
	 * Searches through the graph represented by the TransitionFunction object, and removes
	 * disjoint elements.
	 * 
	 * Algorithm starts from all initial States, and adds them to a queue. They are
	 * then placed into the new TransitionFunction object, and all States reachable by these
	 * initial states are placed in a queue for processing. States are checked against being
	 * already present in the newFSM object, not being added to the queue if already handled.
	 * 
	 * Some post-processing may be required by more advanced types of FSM.
	 * 
	 * @return - Returns a TransitionSystem object representing the accessible
	 * version of the calling TransitionSystem object. 
	 */
	
	public abstract TransitionSystem makeAccessible();
	/**
	 * Searches through the graph represented by the TransitionFunction object, and removes any
	 * states that cannot reach a marked state.
	 * 
	 * A helper method is utilized to generate a list of States and their legality in the new FSM object,
	 * after which the contents of the old FSM object are processed with respect to this list as they construct
	 * the new FSM object.
	 * 
	 * @return - Returns an TransitionSystem object representing the CoAccessible version of the original
	 * TransitionSystem.
	 */
	
	public abstract TransitionSystem makeCoAccessible();
	
	/**
	 * Helper method that processes the calling FSM object to generate a list of States for that
	 * object describing their status as CoAccessible, or able to reach a Marked State.
	 * 
	 * @return - Returns a HashMap<<r>String, Boolean> object mapping String state names to true if the state is coaccessible, and false if it is not.
	 */
	
	protected HashMap<String, Boolean> getCoAccessibleMap() {
		// When a state is processed, add it to the map and state if it reached a marked state.
		HashMap<String, Boolean> results = new HashMap<String, Boolean>();
		
		for(String curr : states.getStateNames()) {
			// Recursively check for a marked state, and keep track of a HashSet of states
			// which have already been visited to avoid loops.
			boolean isCoaccessible = recursivelyFindMarked(curr, results, new HashSet<String>());
			if(!isCoaccessible)
				results.put(curr, false);
		}
		return results;
	} // getCoAccessibleMap(State, HashMap<String, Boolean>)

	/**
	 * Helper method to the getCoAccessibleMap method that recursively checks States in the calling FSM object's StateMap,
	 * declaring them as either CoAccessible, not CoAccessible, or unvisited (true, false, null in the results HashMap)
	 * over the course of its processing, creating via side-effect the list of States' statuses once all have been visited.
	 * 
	 * Algorithm works by recursively exploring all the neighbors of each observed State until it either finds a Marked State,
	 * runs out of neighbors, or finds a State it has already seen. In the first case, all States along that path are set as 
	 * 'true', and if States remain, another is processed in this same way. In either other case, it returns false, and either
	 * another pathway is explored or that path of States is marked as not being CoAccessible ('false').
	 * 
	 * The method getCoAccessibleMap() uses this to generate the list of States and their statuses.
	 * 
	 * @param curr - State object that represents the current 'State' to process recursively.
	 * @param results - HashMap<<r>String, Boolean> object that records the status of each State as CoAccessible or not.
	 * @param visited - HashSet<<r>String> object that keeps track of which States have been already visited.
	 * @return - Returns a boolean value: true if the State object curr is coaccessible, false otherwise.
	 */
	
	private boolean recursivelyFindMarked(String curr, HashMap<String, Boolean> results, HashSet<String> visited) {
		visited.add(curr);
		
		// If the state is marked, return true
		if(states.getStateMarked(curr)) {
			results.put(curr, true);
			return true;
		}
		
		// Base cases when already checked if the state was coaccessible
		Boolean check = results.get(curr);
		if(check != null)
			return check;
		
		// Go through each unvisited state and recurse until find a marked state
		ArrayList<Transition> thisTransitions = transitions.getTransitions(curr);
		if(thisTransitions == null) return false;
		for(Transition t : thisTransitions) {
			for(State next : (ArrayList<State>)t.getTransitionStates()) {
				if(!visited.contains(next.getStateName())) { // If not already visited
					// If next is coaccessible, so is curr.
					if(recursivelyFindMarked(next, results, visited)) {
						results.put(curr.getStateName(), true);
						return true;
					} // if coaccessible
				} // if not already visited
			} // for each transition state
		} // for each transition object
		return false;
	} // recursivelyFindMarked(State, HashMap<String, Boolean>, HashSet<String>)
	
	/**
	 * Gets if the FSM is blocking—that is, if there are possible words which are not
	 * part of the prefix closure of the marked language of the FSM. In other words, if
	 * the FSM is NOT coaccessible, then the FSM is blocking.
	 * It marks bad states along the way.
	 * 
	 * @return - Returns a boolean value; true if the FSM is found to be blocking, false otherwise.
	 */
	
	public boolean isBlocking() {
		// First, find what states we need to indicate
		HashMap<String, Boolean> processedStates = getCoAccessibleMap();	//Use helper method to generate list of legal/illegal States

		boolean isBlocking = false;
		
		// Secondly, indicate blocking states
		for(Map.Entry<String, Boolean> entry : processedStates.entrySet()) {
			if(!entry.getValue()) {
				isBlocking = true;
				this.getState(entry.getKey()).setStateBad(true);
			} // if
			else
				// Reset the badness to false of good states (accommodates multiple different
				// operations doing marking by overwriting).
				this.getState(entry.getKey()).setStateBad(false);
		} // for
		
		return isBlocking;
	} // isBlocking()
	
	/**
	 * This method converts an FSM object into a text file which can be read back in and used to recreate
	 * an FSM later, or used for analytical purposes. A helper class, ReadWrite, manages the brunt
	 * of this process, but for the various special features of FSM objects, each has to handle
	 * itself separately.
	 * 
	 * @param filePath - String object representing the path to the folder to place the text file.
	 * @param name - String object representing the name of the text file to create.
	 */
	
	public abstract void toTextFile(String filePath, String name);
	
//---  Copy Methods that steal from other FSMs   -----------------------------------------------------------------------

	/**
	 * This method copies the states of another FSM object into the current FSM object with a prepended
	 * String prefix (which can be an empty String).
	 * 
	 * @param other - FSM object whose states are copied for renaming.
	 * @param prefix - String object representing the prefix for the new state names (can be the empty string).
	 */
	
	public void copyStates(TransitionSystem other, String prefix) {
		for(State s : other.getStates())
			states.addState(s, prefix);
		for(State s : other.getInitialStates())
			addInitialState(prefix + s.getStateName());
	} // copyStates (FSM, String)
	
	/**
	 * This method copies the states of a provided FSM object into the current FSM object.
	 * 
	 * @param other - FSM object whose states are copied.
	 */
	
	public void copyStates(TransitionSystem other) {
		copyStates(other, "");
	} // copyStates(FSM)
	
	/**
	 * This method copies the states of a provided FSM object into the current FSM object,
	 * excluding the states in badStates.
	 * 
	 * @param other - FSM object whose states are copied.
	 * @param badStates - HashSet of State String names which are excluded from copying.
	 */
	
	public void copyStates(TransitionSystem other, HashSet<String> badStates) {
		for(State s : other.getStates()) if(!badStates.contains(s.getStateName()))
			states.addState(s).setStateInitial(false);
		for(State s : other.getInitialStates()) if(!badStates.contains(s.getStateName()))
			addInitialState(s.getStateName());
	}
	
	/**
	 * This method copies the events of a provided FSM object into the current FSM object.
	 * 
	 * @param other - An FSM object whose events are copied.
	 */
	
	public void copyEvents(TransitionSystem other) {
		events.add(other.getEvents());
	} // copyEvents(FSM)
	
	/**
	 * This method copies the transitions of another FSM into the current FSM.
	 * 
	 * @param other - An FSM object whose transitions are copied.
	 */
	
	public void copyTransitions(TransitionSystem other) {
		transitions.add(other.getTransitions());
	} // copyTransitions(FSM)
	
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
	
	/**
	 * Setter method that assigns a set of States as the States that compose the single defined State; that is,
	 * the parameter State aggregate is defined to have been made up of the parameter State ... varargs pieces.
	 * 
	 * @param aggregate - State object that represents a State which has been previously generated as the composition of a set of States.
	 * @param pieces - State ... varargs object that represents the set of States which compose the State aggregate.
	 */
	
	public void setStateComposition(String aggregate, String ... pieces) {
		ArrayList<String> composed = new ArrayList<String>();
		for(String in : pieces)
			if(composed.indexOf(in) == -1)
				composed.add(in); 
		states.setStateComposition(aggregate, composed);
	}
	
	public void setStateCompositionDuplicate(String aggregate, String ... pieces) {
		ArrayList<String> composed = new ArrayList<String>();
		for(String in : pieces)
			composed.add(in); 
		states.setStateComposition(aggregate, composed);
	}

	/**
	 * Setter method that allows for the assignation of a new HashMap<<r>State, ArrayList<<r>State>> object
	 * as the set of States and their corresponding set of States which compose them in this TransitionSystem
	 * object's StateMap.
	 * 
	 * @param composed - A HashMap<<r>State, ArrayList<<r>State>> object to replace the StateMap's composition instance variable.  
	 */
	
	public void setCompositionStates(HashMap<String, ArrayList<String>> composed) {
		states.setCompositionStates(composed);
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

	public ArrayList<String> getStateNames(){
		return states.getStateNames();
	}

	/**
	 * Getter method that requests a HashMap<<r>State, ArrayList<<r>State>> object holding pairs made up of a 
	 * State and the Composite States that it was built fromt.
	 * 
	 * @return - Returns a HashMap<<r>State, ArrayList<<r>State>> object containing pairs of States and their Composite State.
	 */
	
	public HashMap<String, ArrayList<String>> getComposedStates(){
		return states.getComposedStates();
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
	
	/**
	 * Getter method that requests whether a specified State object is a Marked State.
	 * 
	 * @param stateName - String object representing the name of the State object being queried for its status as Marked.
	 * @return - Returns a boolean value; true if the state is Marked, false otherwise.
	 */
	
	public boolean isMarked(String stateName) {
		return states.getState(stateName).getStateMarked();
	}

	/**
	 * Getter method that returns all of the Initial States in the FSM object as an ArrayList of State objects.
	 * 
	 * @return - Returns an ArrayList<<r>State> of State objects which are the Initial States associated to this FSM object.
	 */
	
	public ArrayList<String> getInitialStates(){
		return initialStates;
	}
	
	/**
	 * Getter method that requests whether the calling FSM object possesses an Initial State with
	 * the provided String name or not.
	 * 
	 * @param stateName - String object representing the name of the State object.
	 * @return - Returns a boolean value, true if the State object is an Initial State, false otherwise.
	 */
	
	public abstract boolean hasInitialState(String stateName);

	//-- Event  -----------------------------------------------

	public ArrayList<String> getEventNames(){
		return events.getEventNames();
	}

	//-- Transition  ------------------------------------------

//---  Adder Methods   ------------------------------------------------------------------------
	
	/**
	 * This method adds a new State to the StateMap<<r>State> object, returning the State object in the FSM
	 * that was either newly added or the already existing State corresponding to the provided State name.
	 * 
	 * @param stateName - String object representing the name of a State to add to the StateMap
	 * @return - Returns a State object representing the corresponding State in the FSM that was just added.
	 */
	
	public void addState(String stateName) {
		states.addState(stateName);
	}

	public void addEvent(String eventName) {
		events.addEvent(eventName);
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

	/**
	 * This method handles the introduction of a new State object as an Initial State
	 * via a String object representing its name, behaving differently between Deterministic and 
	 * NonDeterministic FSM objects as their definitions specify and require.
	 * 
	 * @param newInitial - String object representing the name of the State object being introduced as an Initial State.
	 */
	
	public void addInitialState(String newInitial) {
		if(initialStates == null) {
			initialStates = new ArrayList<String>();
		}
		initialStates.add(newInitial);
		addState(newInitial);
		states.setStateInitial(newInitial, true);
	}

	/**
	 * Adds a map of states to the composition of an FSM.
	 * @param composed HashMap mapping States to an ArrayList of States, which is
	 * which is to be added to the state composition of the transition system.
	 */

	public void addStateComposition(HashMap<String, ArrayList<String>> composed) {
		HashMap<String, ArrayList<String>> map = getComposedStates();
		map.putAll(composed);
		setCompositionStates(map);
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
	
	public boolean removeState(String stateName) {
		// If the state exists...
		if(states.stateExists(stateName)) {
			// If it is the initial state, it shouldn't be anymore
			removeInitialState(stateName);
			// Then, we need to remove the state from every reference to it in the transitions.
			transitions.removeStateTransitions(stateName);
			states.removeState(stateName);
			return true;
		}
		return false;
	}
	
	public boolean removeEvent(String event) {
		if(!events.contains(event)) {
			return false;
		}
		transitions.removeEventTransitions(event);
		events.removeEvent(event);
		return true;
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
		
	public boolean removeTransition(String state1, String eventName, String state2) {
		return transitions.removeTransition(state1, eventName, state2);
	}
	
	/**
	 * This method removes a State object from the calling FSM object's method of storing
	 * Initial States, the State being described by a provided String representation of its name.
	 * 
	 * The exact details are handled by the FSM class implementing this, as Deterministic and NonDeterministic
	 * FSMs handle Initial States differently.
	 * 
	 * @param stateName - String object representing the State object's name, denoting which State to remove from storage of Initial States.
	 * @return - Returns a boolean value; true if the denoted State was successfully removed from the set of Initial States, false otherwise.
	 */
	
	public abstract boolean removeInitialState(String stateName);

//---  Manipulations - Changing Many States at the Same Time   ---------------------------------------------------------------
	
	/**
	 * This method marks all states in the transition system.
	 */
	
	public void markAllStates() {
		for(String s : states.getStateNames())
			states.setStateMarked(s, true);
	}
	
	/**
	 * This method unmarks all states in the transition system.
	 */
	
	public void unmarkAllStates() {
		for(String s : states.getStateNames())
			states.setStateMarked(s, false);
	}
	
	/**
	 * This method removes any bad state markings in the transition system.
	 */
	
	public void makeAllStatesGood() {
		for(String s : states.getStateNames())
			states.setStateBad(s, false);
	}
	
	/**
	 * This method removes all bad states from the transition system.
	 */
	
	public void removeBadStates() {
		// Collect all the bad states
		ArrayList<String> badStates = new ArrayList<String>();
		for(String s : states.getStateNames())
			if(states.getStateBad(s))
				badStates.add(s);
		// Remove the states from the state map
		for(String s : badStates) {
			states.removeState(s);
			removeInitialState(s);
		}
		// Remove any state in the bad map from transitions
		for(String s : badStates) 
			transitions.removeStateTransitions(s);
	}

//---  Manipulations - Other   ----------------------------------------------------------------

	/**
	 * This method handles the toggling of a State object's status as Marked, reversing
	 * its current status to its opposite. (true -> false, false -> true). The State object
	 * is so defined by a provided String object representing its name.
	 * 
	 * @param stateName - String object representing the name of the State object to have its status as Marked be toggled.
	 * @return - Returns a Boolean object; true if the state is now marked, false if the state is now unmarked, or null if it did not exist.
	 */
	
	public void toggleMarkedState(String stateName) {
		states.setStateMarked(stateName, !states.getStateMarked(stateName));
	}
	
	/**
	 * This method handles the toggling of a State object's status as bad, reversing
	 * its current status to its opposite. (true -> false, false -> true). The State is identified
	 * by passing in its String name.
	 * 
	 * @param stateName - String object representing the name of the State object to have its status as Marked be toggled.
	 * @return - Returns a Boolean object; true if the state is now bad, false if the state is now not bad, or null if it did not exist.
	 */
	
	public void toggleBadState(String stateName) {
		states.setStateBad(stateName, !states.getStateBad(stateName));
	}
	
	/**
	 * This method handles the toggling of a State object's status as secret, reversing
	 * its current status to its opposite. (true -> false, false -> true). The State is identified
	 * by passing in its String name.
	 * 
	 * @param stateName - String object representing the name of the State object to have its status as secret be toggled.
	 * @return - Returns a Boolean object; true if the state is now secret, false if the state is now not secret, or null if it did not exist.
	 */
	
	public void toggleSecretState(String stateName) {
		states.setStatePrivate(stateName, !states.getStatePrivate(stateName));
	}
	
	public abstract TransitionSystem copy();
	
}
