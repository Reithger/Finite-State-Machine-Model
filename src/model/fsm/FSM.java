package model.fsm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import controller.convert.ReadWrite;
import model.fsm.event.DisabledEvents;
import model.fsm.event.Event;
import model.fsm.event.EventMap;
import model.fsm.state.State;
import model.fsm.state.StateMap;
import model.fsm.transition.Transition;
import model.fsm.transition.TransitionFunction;

/**
 * This abstract class models a Finite State Machine with some of the essential elements.
 * It must be extended to be used (eg. by NonDeterministic or Deterministic to
 * determine how transitions and initial states are handled).
 * 
 * It is part of the fsm package.
 * 
 * @author Ada Clevinger and Graeme Zinck
 */

public class FSM extends TransitionSystem {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	/** String constant designating this object as a specific type of FSM for clarification purposes*/
	public static final String FSM_TYPE = "FSM";
	/** String constant designating the file extension to append to the file name when writing to the system*/
	public static final String FSM_EXTENSION = ".fsm";
	/** String value describing the prefix assigned to all States in an FSM to differentiate it from another FSM*/
	public static final String STATE_PREFIX_1 = "a";
	/** String value describing the prefix assigned to all States in an FSM to differentiate it from another FSM*/
	public static final String STATE_PREFIX_2 = "b";

//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for an FSM object that takes in a file encoding the contents of the FSM.
	 * 
	 * FSM File Order for Special: Initial, Marked.
	 * 
	 * @param in - File object provided in order to create the FSM.
	 * @param id - String object representing the id for the FSM (can be any String).
	 */
	
	public FSM(File in, String inId) throws Exception{
		id = inId;									//Assign id
		states = new StateMap();	//Initialize the storage for States, Event, and Transitions
		events = new EventMap();	//51: Create a ReadWrite object for file reading/writing (reading in this case), denote generics
		transitions = new TransitionFunction();
		initialStates = new ArrayList<State>();
		
		ReadWrite redWrt = new ReadWrite();
		ArrayList<ArrayList<String>> special = redWrt.readFromFile(states, events, transitions, in);
		
		if(special == null) {
			throw new Exception("Error in reading file to construct FSM object");
		}
		
		for(int i = 0; i < special.get(0).size(); i++) {	//Special ArrayList 0-entry is InitialState
			if(states.getState(special.get(0).get(i)) == null)
				states.addState(new State(special.get(0).get(i)));
			states.getState(special.get(0).get(i)).setStateInitial(true);
			initialStates.add(states.addState(special.get(0).get(i)));
		}
		for(int i = 0; i < special.get(1).size(); i++) {			//Special ArrayList 1-entry is MarkedState
			if(states.getState(special.get(1).get(i)) == null)
				states.addState(new State(special.get(1).get(i)));
			states.getState(special.get(1).get(i)).setStateMarked(true);
		}
		for(int i = 0; i < special.get(2).size(); i++) {			//Special ArrayList 2-entry is Private State
			if(states.getState(special.get(2).get(i)) == null)
				states.addState(new State(special.get(2).get(i)));
			states.getState(special.get(2).get(i)).setStatePrivate(true);
		}
		for(int i = 0; i < special.get(3).size(); i++) {			//Special ArrayList 3-entry is ObservableEvent
			if(events.getEvent(special.get(3).get(i)) == null)
				events.addEvent(special.get(3).get(i));
			events.getEvent(special.get(3).get(i)).setEventObservability(false);
		}
		for(int i = 0; i < special.get(4).size(); i++) {			//Special ArrayList 4-entry is ObservableEvent
			if(events.getEvent(special.get(4).get(i)) == null)
				events.addEvent(special.get(4).get(i));
			events.getEvent(special.get(4).get(i)).setEventAttackerObservability(false);
		}
		for(int i = 0; i < special.get(5).size(); i++) {			//Special ArrayList 5-entry is Controllable Event
			if(events.getEvent(special.get(5).get(i)) == null)
				events.addEvent(special.get(5).get(i));
			events.getEvent(special.get(5).get(i)).setEventControllability(false);
		}
	}
	
	/**
	 * Constructor for a FSM that takes any FSM as a parameter and creates a new
	 * FSM using that as the basis. Any information which is not permissible in a
	 * FSM is thrown away, because it does not have any means to handle it.
	 * 
	 * @param other - TransitionSysem object to copy as a FSM (can be any kind of TS).
	 * @param inId - String object representing the Id for the new FSM to carry.
	 */
	
	public FSM(TransitionSystem other, String inId) {
		id = inId;
		states = new StateMap();
		events = new EventMap();
		transitions = new TransitionFunction();
		
		// Add in all the states
		for(State s : other.states.getStates())
			this.states.addState(s);
		// Add in all the events
		for(Event e : other.events.getEvents())
			this.events.addEvent(e);
		// Add in all the transitions
		for(State s : other.states.getStates()) {
			for(Transition t : other.transitions.getTransitions(s)) {
				// Add every state the transition leads to
				for(State toState : t.getTransitionStates())
					this.addTransition(s.getStateName(), t.getTransitionEvent().getEventName(), toState.getStateName());
			} // for every transition
		} // for every state
		// Add in the initial states
		initialStates = new ArrayList<State>();
		for(State s: other.getInitialStates())
			initialStates.add(this.getState(s));
	} // NonDetFSM(FSM, String)
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements themselves.
	 * 
	 * @param - String object representing the new id for this FSM object
	 */
	
	public FSM(String inId) {
		id = inId;
		events = new EventMap();
		states = new StateMap();
		transitions = new TransitionFunction();
		initialStates = new ArrayList<State>();
	} // DetFSM()
	
	/**
	 * Constructor for an FSM object that contains no transitions or states, allowing the
	 * user to add those elements themselves. It has no id, either.
	 */
	
	public FSM() {
		id = "";
		events = new EventMap();
		states = new StateMap();
		transitions = new TransitionFunction();
		initialStates = new ArrayList<State>();
	} // FSM()

	
//---  Single-FSM Operations   ----------------------------------------------------------------

	/**
	 * This method creates a modified FSM or Modal Specification derived from the calling object by removing Observable Events
	 * and enforcing a Determinized status.
	 * 
	 * Collapse Unobservable
	 *  - Map singular States to their Collectives
	 * Calculate Transitions from Collective Groups
	 * - Will produce new States, need to then process these as well	
	 * 
	 * @return - Returns a TransitionSystem object representing the Determinized Observer-View derivation of the calling FSM or Modal Specification object.
	 */
	
	public FSM buildObserver() {
		FSM newFSM = new FSM();
		
		HashMap<State, State> map = new HashMap<State, State>();
		
		for(State s : getStates()) {
			
			HashSet<State> reach = new HashSet<State>();
			LinkedList<State> queue = new LinkedList<State>();
			queue.add(s);
			
			while(!queue.isEmpty()) {
				State top = queue.poll();
				if(reach.contains(top))
					continue;
				reach.add(top);
			    for(Transition t : getTransitions().getTransitions(top)) {
				   if(!t.getTransitionEvent().getEventObservability()) {
					  queue.addAll(t.getTransitionStates());
			 	  }
			  }
			}
			ArrayList<State> composite = new ArrayList<State>(reach);
			Collections.sort(composite);
			State made = new State(composite.toArray(new State[composite.size()]));
			newFSM.setStateComposition(made, composite.toArray(new State[composite.size()]));
			map.put(s, made);
		}
		
		LinkedList<State> queue = new LinkedList<State>();
		HashSet<String> visited = new HashSet<String>();
		
		HashSet<State> initialStates = new HashSet<State>();
		for(State s : getInitialStates())
			initialStates.addAll(newFSM.getStateComposition(map.get(s)));
		State init = newFSM.addState(initialStates.toArray(new State[initialStates.size()]));
		newFSM.setStateComposition(init,  initialStates.toArray(new State[initialStates.size()]));
		queue.addFirst(init);
		newFSM.addInitialState(init);
		newFSM.addState(init);
		
		while(!queue.isEmpty()) {
			State top = queue.poll();
			if(visited.contains(top.getStateName()))
				continue;
			visited.add(top.getStateName());
			HashMap<Event, HashSet<State>> tran = new HashMap<Event, HashSet<State>>();
			for(State s : newFSM.getStateComposition(top)) {
				for(Transition t : getTransitions().getTransitions(s)) {
					if(t.getTransitionEvent().getEventObservability()) {
						if(tran.get(t.getTransitionEvent()) == null) {
							tran.put(t.getTransitionEvent(), new HashSet<State>());
						}
						for(State led : t.getTransitionStates())
							tran.get(t.getTransitionEvent()).addAll(newFSM.getStateComposition(map.get(led)));
					}
				}
			}
			for(Event e : tran.keySet()) {
				State bot = newFSM.addState(tran.get(e).toArray(new State[tran.get(e).size()]));
				newFSM.setStateComposition(bot, tran.get(e).toArray(new State[tran.get(e).size()]));
				queue.add(bot);
				newFSM.addTransition(top, e, bot);
				newFSM.addState(top);
				newFSM.addState(bot);
			}
		}
		return newFSM;
	}
	
	public void toTextFile(String filePath, String name) {
		if(name == null)
			name = id;
		String truePath = "";
		truePath = filePath + (filePath.charAt(filePath.length()-1) == '/' ? "" : "/") + name;
		String special = "6\n";
		ArrayList<String> init = new ArrayList<String>();
		ArrayList<String> mark = new ArrayList<String>();
		ArrayList<String> priv = new ArrayList<String>();
		ArrayList<String> unob = new ArrayList<String>();
		ArrayList<String> atta = new ArrayList<String>();
		ArrayList<String> cont = new ArrayList<String>();
		for(State s : this.getStates()) {
			if(s.getStateMarked()) 
				mark.add(s.getStateName());
			if(s.getStateInitial()) 
				init.add(s.getStateName());
			if(s.getStatePrivate())
				priv.add(s.getStateName());
		}
		for(Event e : this.getEvents()) {
			if(!e.getEventObservability())
				unob.add(e.getEventName());
			if(!e.getEventControllability())
				cont.add(e.getEventName());
			if(!e.getEventAttackerObservability())
				atta.add(e.getEventName());
		}
		special += init.size() + "\n";
		for(String s : init)
			special += s + "\n";
		special += mark.size() + "\n";
		for(String s : mark)
			special += s + "\n";
		special += priv.size() + "\n";
		for(String s : priv)
			special += s + "\n";
		special += unob.size() + "\n";
		for(String s : unob)
			special += s + "\n";
		special += atta.size() + "\n";
		for(String s : atta)
			special += s + "\n";
		special += cont.size() + "\n";
		for(String s : cont)
			special += s + "\n";
		ReadWrite rdWrt = new ReadWrite();
		rdWrt.writeToFile(truePath,  special, this.getTransitions(), FSM_EXTENSION);
	}
	
	public DisabledEvents getDisabledEvents(State curr, FSM otherFSM, HashSet<String> visitedStates, HashMap<String, DisabledEvents> disabledMap) {
		String currName = curr.getStateName();
		State otherCurr = otherFSM.getState(currName);
		
		// If we already have an answer for the state, return it
		if(disabledMap.containsKey(currName))
			return disabledMap.get(currName);
		// If we already visited the state, return null
		if(visitedStates.contains(currName))
			return null;
		
		visitedStates.add(currName); // Mark the state
		// If we need to disable the state...
		if(otherCurr == null) {
			DisabledEvents de = new DisabledEvents(true);
			disabledMap.put(currName, de);
			return de; // Then we need to disable the state
		}
		
		// Otherwise, go through the neighbours and identify which events we need to disable.
		DisabledEvents currDE = new DisabledEvents(false);
		ArrayList<Transition> thisTransitions = transitions.getTransitions(curr);
		if(thisTransitions != null)
		for(Transition t : thisTransitions) {
			DisabledEvents tempDE = new DisabledEvents(false);
			boolean transitionEventDisabled = false;
			
			loopThroughTransitionStates:
			for(State s : (ArrayList<State>)(t.getTransitionStates())) {
				DisabledEvents nextDE = getDisabledEvents(s, otherFSM, visitedStates, disabledMap);
				Event e = t.getTransitionEvent();
				
				// If the event is not present in the specification, then break
				if(!otherFSM.transitions.eventExists(otherFSM.getState(curr), e)) {
					currDE.disableEvent(e.getEventName());
					transitionEventDisabled = true;
					break loopThroughTransitionStates;
				} // if event not present in spec
				
				if(nextDE != null) { // As long as we're not backtracking...
					// If the transition state is bad, must disable the event.
					if(nextDE.stateIsDisabled()) {
						// If the event is uncontrollable, disable this entire state
						if(e.getEventControllability()) {
							currDE.disableState();
							disabledMap.put(currName, currDE);
							return currDE;
						} // if uncontrollable event
						// Else, disable the transition event (but none of the other events)
						currDE.disableEvent(e.getEventName());
						transitionEventDisabled = true;
						break loopThroughTransitionStates;
					} // if state is disabled
					
					// If the state is good, but has disabled events, AND the transition event to the toState is unobservable
					// remove the disabled events from this state.
					if(!nextDE.allEventsEnabled() && !e.getEventObservability()) {
						tempDE.disableEvents(nextDE);
					} // if there are disabled events and the transition's event is unobservable
				} // if the disabled events for the next state is NOT null
			} // for each destination state
			
			// As long as the event hasn't been disabled (in which case, the event was already marked as disabled),
			// add the temporary disabled events in for the current state.
			if(!transitionEventDisabled)
				currDE.disableEvents(tempDE);
		} // for each transition
		return currDE;
	} // getDisabledEvents
	
	public ArrayList<State> testCurrentStateOpacity(){
		ArrayList<State> secrets = new ArrayList<State>();
		for(State s : this.getStates()) {
			if(s.getStatePrivate())
				secrets.add(s);
		}
		return secrets;
	}
	
	/**
	 * This method performs a trim operation on the calling FSM (performing the
	 * makeAccessible() and makeCoAccessible() methods) to make sure only states
	 * that are reachable from Initial States and can reach Marked States are
	 * included.
	 * 
	 * @return - Returns a TransitionSystem<<r>T> object representing the trimmed
	 * version of the calling TransitionSystem object.
	 */
	
	public FSM trim() {
		FSM newFSM = this.makeAccessible();
		return newFSM.makeCoAccessible();
	}
	
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
	
	public FSM makeAccessible() {
		// Make a queue to keep track of states that are accessible and their neighbours.
		LinkedList<State> queue = new LinkedList<State>();
		
		// Initialize a new FSM with initial states.
		FSM newFSM = new FSM();
		for(State initial : getInitialStates()) {
			newFSM.addInitialState(initial);
			queue.add(initial);
		} // for initial state
		
		while(!queue.isEmpty()) {
			State curr = queue.poll();
			newFSM.addState(curr);
			// Go through the transitions
			ArrayList<Transition> currTransitions = this.transitions.getTransitions(getState(curr));
			if(currTransitions != null) {
				for(Transition t : currTransitions) {
					// Add the states; it goes to to the queue if not already present in the newFSM
					for(State s : t.getTransitionStates())
						if(!newFSM.stateExists(s.getStateName()))
							queue.add(s);
					// Add the transition by copying the old one.
					newFSM.addTransition(newFSM.getState(curr.getStateName()), t);
				} // for
			} // if not null
		} // while
		
		return newFSM;
	} // makeAccessible()
	
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
	
	public FSM makeCoAccessible() {
		FSM newTS = new FSM();
		// First, find what states we need to add.
		HashMap<String, Boolean> processedStates = getCoAccessibleMap();	//Use helper method to generate list of legal/illegal States

		// Secondly, create the states and add the transitions
		for(Map.Entry<String, Boolean> entry : processedStates.entrySet()) {
			// If the state is coaccessible, add it!
			if(entry.getValue()) {
				State oldState = getState(entry.getKey());
				newTS.addState(oldState);
				if(transitions.getTransitions(oldState) != null) { // Only continue if there are transitions from the state
					for(Transition t : transitions.getTransitions(oldState)) {
						Transition trans = t.generateTransition();
						trans.setTransitionEvent(t.getTransitionEvent());
						for(State state : t.getTransitionStates()) {
							if(processedStates.get(state.getStateName()))
								trans.setTransitionState(state);
						}
						if(trans.getTransitionStates().size() != 0)
							newTS.addTransition(oldState, trans);
					}
				} // if not null
			} // if coaccessible
		} // for processed state
	
		// Finally, add the initial state
		for(State state : this.getInitialStates()) {
			if(processedStates.get(state.getStateName()))
				newTS.addInitialState(state.getStateName());
		}
		return newTS;
	}
	
	
//---  Multi-FSM Operations   -----------------------------------------------------------------

	/**
	 * This method performs a Product(or Intersection) operation between multiple FSM objects, one provided as an
	 * argument and the other being the FSM object calling this method, and returns the resulting FSM object.
	 * 
	 * @param other - Array of FSM extending objects that performs the product operation on with the current FSM.
	 * @return - Returns a FSM extending object representing the FSM object resulting from all Product operations.
	 */

	public FSM product(FSM ... other) {
		FSM newFSM = new FSM();
		this.productHelper(other[0], newFSM);
		for(int i = 1; i < other.length; i++) {
			FSM newerFSM = new FSM();
			newFSM.productHelper(other[i], newerFSM);
			newFSM = newerFSM;
		}
		return newFSM;
	}

	/**
	 * This method performs the Parallel Composition of multiple FSMs: the FSM calling this method and the FSMs
	 * provided as arguments. The resulting, returned, FSM will be the same type as the calling FSM.
	 * 
	 * @param other - Array of FSM extending objects provided to perform Parallel Composition with the calling FSM object.
	 * @return - Returns a FSM extending object representing the result of all Parallel Composition operations.
	 */
	
	public FSM parallelComposition(FSM ... other){
		FSM newFSM = this;
		for(int i = 0; i < other.length; i++) {
			FSM newerFSM = new FSM();
			newFSM.parallelCompositionHelper(other[i], newerFSM);
			newFSM = newerFSM;
		}
		return newFSM;
	}

	public FSM getSupremalControllableSublanguage(FSM other) {
		// Store what events are disabled in the map.
		HashMap<String, DisabledEvents> disabledMap = new HashMap<String, DisabledEvents>();
		// Parse the graph and identify disabled states and disabled events
		for(State s : states.getStates()) {
			HashSet<String> visitedStates = new HashSet<String>();
			disabledMap.put(s.getStateName(), getDisabledEvents(s, other, visitedStates, disabledMap));
		} // for every state
		
		// Now, build the FSM that we will return
		FSM newFSM = new FSM(this.id + " supremal controllable sublanguage");
		newFSM.copyStates(this);
		newFSM.copyEvents(this);
		ArrayList<State> statesToRemove = new ArrayList<State>();
		// Identify and copy the transitions which are legal at each state
		for(State s : newFSM.getStates()) {
			DisabledEvents disabled = disabledMap.get(s.getStateName());
			if(disabled.stateIsDisabled()) {
				statesToRemove.add(s);
			} else {
				ArrayList<Transition> allowedTransitions = new ArrayList<Transition>();
				ArrayList<Transition> transitions = this.transitions.getTransitions(getState(s));
				if(transitions != null) {
					for(Transition t : transitions) {
						Event e = t.getTransitionEvent();
						if(disabled.eventIsEnabled(e.getEventName())) {
							// Create a list of the states the event leads to
							ArrayList<State> toStates = new ArrayList<State>();
							for(State toS : (ArrayList<State>)t.getTransitionStates()) {
								if(!disabledMap.get(toS.getStateName()).stateIsDisabled())
									toStates.add(getState(toS.getStateName()));
							} // for every toState
							if(toStates.size() > 0)
								allowedTransitions.add(new Transition(e, toStates));
						} // if the event is enabled
					} // for every transition
				} // if there are any transitions
				if(allowedTransitions.size() > 0) {
					newFSM.addStateTransitions(s, allowedTransitions);
				} // if there are allowed transitions
			} // if disabled state/else
		} // for every state
//		System.out.println(disabledMap.toString());
		for(State s : statesToRemove) {
			newFSM.states.removeState(s.getStateName());
		}
		return newFSM;
	} // getSupremalControllableSublanguage(FSM)

	/**
	 * Helper method that performs the brunt of the operations involved with a single Product operation
	 * between two FSM objects, leaving the specialized features in more advanced FSM types to their
	 * own interpretations after this function has occurred.
	 * 
	 * Performs a product operation on the calling FSM with the first parameter FSM, and builds the
	 * resulting FSM in the second FSM. Has no return, does its action by side-effect.
	 * 
	 * @param other - FSM object representing the FSM object performing the Product operation with the calling FSM object.
	 * @param newFSM - FSM object representing the FSM holding the contents of the product of the Product operation.
	 */
	
	protected void productHelper(FSM other, FSM newFSM) {
		// Get all the events the two have in common
		for(Event thisEvent : this.events.getEvents()) {
			for(Event otherEvent : other.events.getEvents()) {
				// All common events are added
				if(thisEvent.getEventName().equals(otherEvent.getEventName())) {
					newFSM.events.addEvent(thisEvent, otherEvent);
				} // if the event is identical
			} // for otherEvent
		} // for thisEvent
		
		// Go through all the initial states and add everything they connect to with shared events.
		for(State thisInitial : this.getInitialStates()) {
			for(State otherInitial : other.getInitialStates()) {
				// Now, start going through the paths leading out from this new initial state.
				LinkedList<State> thisNextState = new LinkedList<State>();
				thisNextState.add(thisInitial);
				LinkedList<State> otherNextState = new LinkedList<State>();
				otherNextState.add(otherInitial);
				
				while(!thisNextState.isEmpty() && !otherNextState.isEmpty()) { // Go through all the states connected
					State thisState = thisNextState.poll();
					State otherState = otherNextState.poll();
					State newState = newFSM.states.addState(thisState, otherState); // Add the new state
					if(thisState.getStateInitial() && otherState.getStateInitial())
						newFSM.addInitialState(newState);
					
					newFSM.setStateComposition(newState, thisState, (State)otherState);
					
					// Go through all the transitions in each, see what they have in common
					ArrayList<Transition> thisTransitions = this.transitions.getTransitions(thisState);
					ArrayList<Transition> otherTransitions = other.transitions.getTransitions(otherState);
					if(thisTransitions != null && otherTransitions != null) {
						for(Transition thisTrans : thisTransitions) {
							for(Transition otherTrans : otherTransitions) {
								
								// If they share the same event
								Event thisEvent = thisTrans.getTransitionEvent();
								if(thisEvent.getEventName().equals(otherTrans.getTransitionEvent().getEventName())) {
									
									// Then create transitions to all the combined neighbours
									for(State thisToState : thisTrans.getTransitionStates()) {
										for(State otherToState : otherTrans.getTransitionStates()) {
											
											// If the state doesn't exist, add to queue
											if(!newFSM.stateExists("(" + thisToState.getStateName() + "," + otherToState.getStateName() + ")")) {
												thisNextState.add(thisToState);
												otherNextState.add(otherToState);
											} // if state doesn't exist
											
											// Add the state, then add the transition
											State newToState = newFSM.states.addState(thisToState, otherToState);
											newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
										} // for every state in other transition
									} // for every state in this transition
								} // if they share the event
							} // for other transitions
						} // for this transitions
					} // if transitions not null
				} // while there are more states connected to the 2-tuple of initial states
			} // for otherInitial
		} // for thisInitial
		newFSM.addStateComposition(this.getComposedStates());
		newFSM.addStateComposition(other.getComposedStates());
	} // productHelper(FSM)

	/**
	 * Helper method that performs the brunt of the operations involved with a single Parallel Composition
	 * operation between two FSM objects, leaving the specialized features in more advanced FSM types to
	 * their own interpretations after this function has occurred.
	 * 
	 * Performs a Parallel Composition operation on the FSM object calling this method with the FSM object provided as
	 * an argument (other), and places the results of this operation into the provided FSM object (newFSM).
	 * 
	 * @param other - FSM extending object that performs the Parallel Composition operation with FSM object calling this method.
	 * @param newFSM - FSM extending object that is provided to contain the results of this Parallel Composition operation.
	 */
	
	protected  void parallelCompositionHelper(FSM other, FSM newFSM) {
		// Get all the events the two have in common
		HashSet<String> commonEvents = new HashSet<String>();
		for(Event thisEvent : this.events.getEvents()) {
			for(Event otherEvent : other.events.getEvents()) {
				// If it is a common event
				if(thisEvent.getEventName().equals(otherEvent.getEventName())) {
					Event newEvent = newFSM.events.addEvent(thisEvent, otherEvent);
					commonEvents.add(newEvent.getEventName());
				} // if the event is identical
			} // for otherEvent
		} // for thisEvent
		
		// Add all the events unique to each FSM
		for(Event thisEvent : this.events.getEvents())
			if(!commonEvents.contains(thisEvent.getEventName()))
				newFSM.events.addEvent(thisEvent);
		for(Event otherEvent : other.events.getEvents())
			if(!commonEvents.contains(otherEvent.getEventName()))
				newFSM.events.addEvent(otherEvent);
		
		// Go through all the initial states and add everything they connect to.
		for(State thisInitial : this.getInitialStates()) {
			for(State otherInitial : other.getInitialStates()) {
				
				// Now, start going through the paths leading out from this new initial state.
				LinkedList<State> thisNextState = new LinkedList<State>();
				thisNextState.add(thisInitial);
				LinkedList<State> otherNextState = new LinkedList<State>();
				otherNextState.add(otherInitial);
				
				while(!thisNextState.isEmpty() && !otherNextState.isEmpty()) { // Go through all the states connected
					State thisState = thisNextState.poll();
					State otherState = otherNextState.poll();
					State newState = newFSM.states.addState(thisState, otherState); // Add the new state
					if(newState.getStateInitial())
						newFSM.addInitialState(newState);
					
					newFSM.setStateComposition(newState, thisState, (State)otherState);
					
					// Go through all the transitions in each, see what they have in common
					ArrayList<Transition> thisTransitions = this.transitions.getTransitions(thisState);
					ArrayList<Transition> otherTransitions = other.transitions.getTransitions(otherState);
					if(thisTransitions != null && otherTransitions != null) {
						for(Transition thisTrans : thisTransitions) {
							for(Transition otherTrans : otherTransitions) {
								
								// If they share the same event
								Event thisEvent = thisTrans.getTransitionEvent();
								if(thisEvent.getEventName().equals(otherTrans.getTransitionEvent().getEventName())) {
									
									// Then create transitions to all the combined neighbours
									for(State thisToState : thisTrans.getTransitionStates()) {
										for(State otherToState : otherTrans.getTransitionStates()) {
											
											// If the state doesn't exist, add to queue
											if(!newFSM.stateExists("(" + thisToState.getStateName() + "," + otherToState.getStateName() + ")")) {
												thisNextState.add(thisToState);
												otherNextState.add(otherToState);
											} // if state doesn't exist
											
											// Add the state, then add the transition
											State newToState = newFSM.states.addState(thisToState, otherToState);
											newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
										} // for every state in other transition
									} // for every state in this transition
								} // if they share the event
							} // for other transitions
						} // for this transitions
					} // if transitions are not null
					// Go through all the transitions and see what is unique
					if(thisTransitions != null) {
						for(Transition thisTrans : thisTransitions) {
							// If it's NOT a common event
							Event thisEvent = thisTrans.getTransitionEvent();
							if(!commonEvents.contains(thisTrans.getTransitionEvent().getEventName())) {
								// Then, add all the transitions
								for(State thisToState : thisTrans.getTransitionStates()) {
									
									// If it doesn't exist, add it to the queue
									if(!newFSM.stateExists("(" + thisToState.getStateName() + "," + otherState.getStateName() + ")")) {
										thisNextState.add(thisToState);
										otherNextState.add(otherState);
									} // if state doesn't exist
									
									// Add the state, then add the transition
									State newToState = newFSM.states.addState(thisToState, otherState);
									newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
								} // for the toStates
							} // if not a common event
						} // for this transitions
					} // if transitions are not null
					if(otherTransitions != null) {
						for(Transition otherTrans : other.transitions.getTransitions(otherState)) {
							// If it's NOT a common event
							Event thisEvent = otherTrans.getTransitionEvent();
							if(!commonEvents.contains(otherTrans.getTransitionEvent().getEventName())) {
								// Then, add all the transitions
								for(State otherToState : otherTrans.getTransitionStates()) {
									
									// If it doesn't exist, add it to the queue
									if(!newFSM.stateExists("(" + thisState.getStateName() + "," + otherToState.getStateName() + ")")) {
										thisNextState.add(thisState);
										otherNextState.add(otherToState);
									} // if state doesn't exist
									
									// Add the state, then add the transition
									State newToState = newFSM.states.addState(thisState, otherToState);
									newFSM.addTransition(newState.getStateName(), thisEvent.getEventName(), newToState.getStateName());
								} // for the toStates
							} // if not a common event
						} // for other transitions
					} // if transitions are not null
				} // while there are more states connected to the 2-tuple of initial states
			} // for otherInitial
		} // for thisInitial
	} // parallelCompositionHelper(FSM)

//---  Getter Methods   -----------------------------------------------------------------------

	@Override
	public ArrayList<State> getInitialStates() {
		return initialStates;
	}

	@Override
	public boolean hasInitialState(String stateName) {
		return initialStates.contains(getState(stateName));
	}
	
	public Boolean getEventObservability(String eventName) {
		Event curr = events.getEvent(eventName);
		if(curr != null)
			return curr.getEventObservability();
		return null;
	}
	
	public Boolean getEventControllability(String eventName) {
		Event curr = events.getEvent(eventName);
		if(curr != null)
			return curr.getEventControllability();
		return null;
	}

	public Boolean getEventAttackerObservability(String eventName) {
		Event curr = events.getEvent(eventName);
		if(curr != null)
			return curr.getEventAttackerObservability();
		return null;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public boolean setEventObservability(String eventName, boolean status) {
		Event curr = events.getEvent(eventName);
		if(curr != null) {
			curr.setEventObservability(status);
			return true;
		}
		return false;
	}
	
	public void setEventControllability(String eventName, boolean value) {
		Event curr = events.getEvent(eventName);
		if(curr != null)
			curr.setEventControllability(value);
	}
	
	public boolean setEventAttackerObservability(String eventName, boolean status) {
		Event curr = events.getEvent(eventName);
		if(curr != null)
			curr.setEventAttackerObservability(status);
		else
			return false;
		return true;
	}
	
//---  Manipulations   ------------------------------------------------------------------------
	
	@Override
	public void addInitialState(String newInitial) {
		State curr = states.addState(newInitial);
		curr.setStateInitial(true);
		// Look and see if it's already an initial state
		if(!initialStates.contains(curr))
			initialStates.add(curr);
	}

	@Override
	public void addInitialState(State newState) {
		State curr = states.addState(newState);
		curr.setStateInitial(true);
		initialStates.add(curr);
	}

	@Override
	public boolean removeInitialState(String stateName) {
		State theState = states.getState(stateName);
		if(theState != null) {
			theState.setStateInitial(false);
			if(initialStates.remove(theState)) return true;
		}
		return false;
	}

	public FSM copy() {
		return new FSM(this, this.getId());
	}

} // class FSM
