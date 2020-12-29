package model.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.DisabledEvents;
import model.fsm.FSM;
import model.fsm.ModalSpecification;
import model.fsm.State;
import model.fsm.Transition;
import model.fsm.component.Entity;

public class ProcessOperation {

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
			HashMap<Entity, HashSet<State>> tran = new HashMap<Entity, HashSet<State>>();
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
			for(Entity e : tran.keySet()) {
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
						Entity e = t.getTransitionEvent();
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
		for(Entity thisEvent : this.events.getEvents()) {
			for(Entity otherEvent : other.events.getEvents()) {
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
								Entity thisEvent = thisTrans.getTransitionEvent();
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
	
	protected void parallelCompositionHelper(FSM other, FSM newFSM) {
		// Get all the events the two have in common
		HashSet<String> commonEvents = new HashSet<String>();
		for(Entity thisEvent : this.events.getEvents()) {
			for(Entity otherEvent : other.events.getEvents()) {
				// If it is a common event
				if(thisEvent.getEventName().equals(otherEvent.getEventName())) {
					Entity newEvent = newFSM.events.addEvent(thisEvent, otherEvent);
					commonEvents.add(newEvent.getEventName());
				} // if the event is identical
			} // for otherEvent
		} // for thisEvent
		
		// Add all the events unique to each FSM
		for(Entity thisEvent : this.events.getEvents())
			if(!commonEvents.contains(thisEvent.getEventName()))
				newFSM.events.addEvent(thisEvent);
		for(Entity otherEvent : other.events.getEvents())
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
								Entity thisEvent = thisTrans.getTransitionEvent();
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
							Entity thisEvent = thisTrans.getTransitionEvent();
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
							Entity thisEvent = otherTrans.getTransitionEvent();
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

	//Modal stuff?
	
	public FSM getSupremalControllableSublanguage(FSM other) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * This method tries to get the maximally permissive (optimal) supervisor for an fsm
	 * (which will have certain transitions which are controllable and uncontrollable, etc.)
	 * that satisfies the modal specification. That is, it may ONLY have "may" transitions,
	 * and it MUST contain any "must" transitions (unless such a state does not exist, of course).
	 * 
	 * Any transitions which are illegal are removed, but if those transitions are uncontrollable,
	 * then we have a problem and we have to remove the entire state. States which do not lead
	 * to any marked states must also be removed. This process is done iteratively until all
	 * no more bad states exist and we have the maximally permissive supervisor.
	 * 
	 * This follows the algorithm explained in Darondeau et al., 2010.
	 * 
	 * @param fsm - FSM<<r>State, T, Event> object 
	 * @return - Returns a maximally permissive controller for the original FSM.
	 * @throws IllegalArgumentException - Throws an illegal argument exception if the
	 * FSM defines one of the events in the modal specification as an unobservable event, which
	 * is illegal in this implementation.
	 */
	
	public ModalSpecification makeOptimalSupervisor(FSM ModalSpecification) throws IllegalArgumentException {
		return null;
		/*
		//--------------------------------------------
		// Step 1: Create the reachable part of the combo
		ModalSpecification universalObserverView = new ModalSpecification("UniObsView");
		HashMap<String, String> universalObserverViewMap = createUniversalObserverView(ModalSpecification, universalObserverView);
		String universalInitial = universalObserverViewMap.get(ModalSpecification.getInitialStates().get(0).getStateName());
		universalObserverView.addInitialState(universalInitial);
		
		FSM specFSM = getUnderlyingFSM();
		
		// If we have unobservable events in the specification, that's illegal
		for(Event e : specFSM.events.getEvents()) {
			// See if it's visible in the ModalSpecification
			Event otherE = ModalSpecification.events.getEvent(e);
			if(otherE instanceof EventObservability && !((EventObservability)otherE).getEventObservability())
				throw new IllegalArgumentException("The modal specification has the event \"" + e.getEventName() + "\", which is an unobservable event in the plant passed in for getting the supervisor. The specification should only have observable events.");
		}
		
		ModalSpecification product = universalObserverView.product(specFSM);
		
		//--------------------------------------------
		// Step 2: Mark the bad states
		HashSet<String> badStates = new HashSet<String>();
		boolean keepGoing = true;
		
		while(keepGoing) {
			boolean keepGoing1 = markBadStates(ModalSpecification, specFSM, product, badStates);
			boolean keepGoing2 = markDeadEnds(universalObserverView, universalObserverViewMap, product, badStates);
			
			keepGoing = keepGoing1 || keepGoing2;
		} // while
		
		// Now, we have to actually create our ModalSpecification
		ModalSpecification supervisor = new ModalSpecification(product, badStates, fsm.id + " Supervisor");
		return supervisor.makeAccessible();
		*/
	}
	
	/**
	 * This marks states that are bad states in a supervisor for an FSM that is trying to satisfy a
	 * modal specification. There are two types of bad states:<br/>
	 * 1) When there is some uncontrollable and observable event where there is a possible state in
	 * the determinized collection of states where the event is possible, but there is no such possible
	 * transition in the specification; and<br/>
	 * 2) When there is some observable event where the event must be possible according to the specification,
	 * but there is no such transition defined for the determinized collection of states.
	 * 
	 * @param fsm - Original FSM which needs to be controlled.
	 * @param specFSM - FSM underlying the modal specification object.
	 * @param product - FSM representing the product of the determinized first FSM with the specification.
	 * @param badStates - HashMap of all the states already marked as bad, which will be further updated as
	 * we go through this iteration.
	 * @return - Returns true if at least one state was marked as bad, false otherwise.
	 */
	
	private <T extends Transition>
	boolean markBadStates(FSM fsm, FSM specFSM, FSM product, HashSet<String> badStates) {
		// We need to parse every state in the product, check every component state if there is some uncontrollable observable
		// event that isn't defined in the product.
		// Also look if must transitions exist...
		boolean foundABadOne = false;
		
		System.out.println("Going through bad states");
	
		// Go through every state in the product
		for(State s : product.getStates()) {
			// Don't process the state if it's already bad
			if(badStates.contains(s.getStateName())) continue;
			
			// If a must transition does not exist at the state, mark the state
			String specStateName = product.getStateComposition(s).get(1).getStateName(); // Gets the specification
			ArrayList<Transition> specTransitions = this.mustTransitions.getTransitions(this.getState(specStateName));
			if(specTransitions != null) for(Transition t : specTransitions) {
				Entity event = t.getTransitionEvent();
				ArrayList<State> toStates = product.transitions.getTransitionStates(s, product.events.getEvent(event));
				// Mark the state as bad if the must transition does not exist
				if(toStates == null) {
					System.out.println("There was no must transition for an event, " + event.getEventName() + ", at state " + s.getStateName());
					badStates.add(s.getStateName());
					foundABadOne = true;
				} else {
					// Mark the state as bad if all the states it leads to are bad
					boolean itsBad = true;
					for(State toState : toStates)
						if(!badStates.contains(toState.getStateName())) itsBad = false;
					if(itsBad) {
						badStates.add(s.getStateName());
						foundABadOne = true;
					}
				}
			} // for all the state's transitions
			
			// If an uncontrollable observable event exists from any of the states in the original fsm, and
			// the event not allowed in the product, then UH-NO NOT HAP'NIN (mark the state)
			State observerState = product.getStateComposition(s).get(0);
			ArrayList<State> origStates = product.getStateComposition(observerState);
			for(State fromState : origStates) {
				// Go through all the original transitions
				ArrayList<Transition> origTransitions = fsm.transitions.getTransitions((State)fromState);
				if(origTransitions != null) {
					for(Transition t : origTransitions) {
						Entity event = t.getTransitionEvent();
						// If the event is observable but NOT controllable, we have a problem
						if(event.getEventObservability() &&  !event.getEventControllability()) {
							// Then the event must be present in the product
							ArrayList<State> toStates = product.transitions.getTransitionStates(product.getState(s), product.events.getEvent(event));
							// Mark the state as bad if the event is not allowed in the product.
							if(toStates == null) {
								System.out.println("There was an uncontrollable, observable event, " + event.getEventName() + ", that did not exist in the spec at state " + s.getStateName());
								badStates.add(s.getStateName());
								foundABadOne = true;
							} else {
								// Mark the state as bad if all the states it leads to are bad
								boolean itsBad = true;
								for(State toState : toStates)
									if(!badStates.contains(toState.getStateName()))
										itsBad = false;
								if(itsBad) {
									badStates.add(s.getStateName());
									foundABadOne = true;
								}
							}
						} // if it's observable but NOT controllable
					} // for all the transitions
				}//if origTransition is not null
			} // for every component state in the original fsm
		} // for every state
		return foundABadOne;
	} // markBadStates(FSM, FSM, FSM, HashSet)
	
	/**
	 * Marks the dead ends where there is some state q in the set of states P in the product (P, s)
	 * that cannot reach a marked state in the universalObserverView using only transitions that are
	 * allowed in the product.
	 * 
	 * @param universalObserverView - Observer view of the original FSM with states for every possible
	 * starting state (for instance, if the FSM had a state 2, then there will be a state representing
	 * the epsilon-reach of 2 in this universal view).
	 * @param universalObserverViewMap - Map between the original FSM's states and their epsilon reach state
	 * name, which is present in the universalObserverView.
	 * @param product - Product FSM between the observer view of the original fsm and the specification.
	 * @param badStates - HashSet of String state names which are bad and to be removed.
	 * @return - Returns a boolean value; true if the method marked a bad state, false otherwise.
	 */

	static public boolean markDeadEnds(FSM universalObserverView, HashMap<String, String> universalObserverViewMap, FSM product, HashSet<String> badStates) {
		// For every combo of states (q,(P,s)) such that q is an element of P and (P,s) is the product, we want to
		// perform the product with initial states being the parameter product and every possible q. If it is
		// possible to reach a marked state in the product from this initial point, then it's ok! If there's one
		// q for the state which cannot reach a good state, then we have to add the state to the bad states.
		
		boolean removedAState = false;
		
		// To recover after operations:
		ArrayList<State> productInitialStates = product.getInitialStates();
		
		// Go through all the good product states
		for(State productState : product.getStates()) if(!badStates.contains(productState.getStateName())) {
			// Get all the states in the original FSM
			State leftSideOfProduct = product.getStateComposition(productState).get(0);
			ArrayList<State> originalStates = product.getStateComposition(leftSideOfProduct);
			for(State q : originalStates) {
				String universalInitial = universalObserverViewMap.get(q.getStateName());
				universalObserverView.addInitialState(universalInitial);
				product.addInitialState(productState);
				FSM massiveProduct = (FSM)universalObserverView.product(product);
				// Now, we get to look through the massive product for a marked state
				boolean reachesMarked = canReachMarked(massiveProduct, massiveProduct.getInitialStates().get(0), badStates);
				if(!reachesMarked) {
					System.out.println(massiveProduct.getInitialStates().get(0).getStateName() + " could not reach a marked state.");
					badStates.add(productState.getStateName());
					removedAState = true;
					break;
				} // if cannot reach a marked state
			} // for every state q in the product's initial states
		} // for every product state that isn't marked as bad
		
		// Recover original properties of FSMs:
		product.addInitialState(productInitialStates.get(0));
		
		return removedAState;
	} // markDeadEnds(FSM, HashMap, FSM, HashSet)
	
	/**
	 * Checks if the FSM can reach a marked state from the initial state given. It excludes checking any
	 * states that have a badState in the right part of the product (the second element in the state name).
	 * 
	 * @param fsm Deterministic FSM to check.
	 * @param state State to look 
	 * @param badStates
	 * @return - Returns TODO:
	 */
	
	static protected boolean canReachMarked(FSM fsm, State state, HashSet<String> badStates) {
		// Get the name of the state in the right side of the product in the fsm (second part of the state).
		String name = fsm.getStateComposition(state).get(1).getStateName();
		if(badStates.contains(name)) return false;
		if(state.getStateMarked()) return true;
		
		HashSet<State> visited = new HashSet<State>();
		LinkedList<State> queue = new LinkedList<State>();
		visited.add(state);
		queue.add(state);
		
		while(!queue.isEmpty()) {
			// Go through all neighbours in BFS
			State curr = queue.poll();
			for(Transition t : fsm.transitions.getTransitions(curr)) for(State toState : t.getTransitionStates()) {
				name = fsm.getStateComposition(toState).get(1).getStateName();
				if(!badStates.contains(name)) {
					if(toState.getStateMarked()) return true; // if marked, we're all good!
					if(!visited.contains(toState)) { // if it hasn't been visited yet, add to queue
						queue.add(toState);
						visited.add(toState);
					} // if not yet visited
				} // if it's not a bad state
			} // for every transition state
		} // while the queue still has stuff in it
		
		return false;
	} // canReachMarked(FSM, HashSet)
	
	
}
