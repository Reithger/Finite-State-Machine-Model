package model.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;

public class ProcessOperation {

//---  Instance Variables   -------------------------------------------------------------------
	
	public static String attributeObservableRef;
	public static String attributeInitialRef;
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static void assignAttributeReferences(String init, String obs) {
		attributeInitialRef = init;
		attributeObservableRef = obs;
	}
	
	/**
	 * This method creates a modified TransitionSystem or Modal Specification derived from the calling object by removing Observable Events
	 * and enforcing a Determinized status.
	 * 
	 * Collapse Unobservable
	 *  - Map singular Strings to their Collectives
	 * Calculate Transitions from Collective Groups
	 * - Will produce new Strings, need to then process these as well	
	 * 
	 * @return - Returns a TransitionSystem object representing the Determinized Observer-View derivation of the calling TransitionSystem or Modal Specification object.
	 */
	
	public static TransitionSystem buildObserver(TransitionSystem in) {
		if(!in.hasEventAttribute(attributeObservableRef) || !in.hasStateAttribute(attributeInitialRef)) {
			return null;
		}
		
		TransitionSystem out = new TransitionSystem(in.getId() + "_observer");
		out.copyAttributes(in);
		out.mergeEvents(in);
		
		HashMap<String, ArrayList<String>> map = collapseStates(in);
		
		ArrayList<String> initialStrings = new ArrayList<String>();
		for(String s : in.getStatesWithAttribute(attributeInitialRef))
			if(!initialStrings.contains(s)) {
				for(String t : map.get(s)) {
					if(!initialStrings.contains(t))
						initialStrings.add(t);
				}
			}
		String init = in.compileStateName(initialStrings);
		out.addState(init);
		out.compileStateAttributes(initialStrings, in);
		out.setStateAttribute(init, attributeInitialRef, true);
		out.setStateComposition(init, initialStrings);

		LinkedList<String> queue = new LinkedList<String>();
		HashSet<String> visited = new HashSet<String>();

		queue.addFirst(init);
		
		while(!queue.isEmpty()) {
			String top = queue.poll();
			if(visited.contains(top))
				continue;
			visited.add(top);
			HashMap<String, ArrayList<String>> tran = new HashMap<String, ArrayList<String>>();
			for(String s : out.getStateComposition(top)) {
				for(String e : in.getStateTransitionEvents(s)) {
					if(in.getEventAttribute(e, attributeObservableRef)) {
						if(tran.get(e) == null) {
							tran.put(e, new ArrayList<String>());
						}
						for(String t : in.getStateEventTransitionStates(s, e)) {
							for(String u : map.get(t)) {
								if(!tran.get(e).contains(u)) {
									tran.get(e).add(u);
								}
							}
						}
					}
				}
			}
			for(String event : tran.keySet()) {
				String dest = in.compileStateName(tran.get(event));
				out.addState(dest);
				out.compileStateAttributes(tran.get(event), in);
				out.addStateComposition(dest, tran.get(event));
				queue.add(dest);
				out.addTransition(top, event, dest);	//TODO: Retention of transition attributes?
			}
		}
		return out;
	}
	
	/**
	 * This method performs a Product(or Intersection) operation between multiple TransitionSystem objects, one provided as an
	 * argument and the other being the TransitionSystem object calling this method, and returns the resulting TransitionSystem object.
	 * 
	 * @param other - Array of TransitionSystem extending objects that performs the product operation on with the current TransitionSystem.
	 * @return - Returns a TransitionSystem extending object representing the TransitionSystem object resulting from all Product operations.
	 */

 	public static TransitionSystem product(ArrayList<TransitionSystem> fsms) {
 		boolean work = true;
 		for(TransitionSystem t : fsms) {
 			if(!t.hasStateAttribute(attributeInitialRef)) {
 				work = false;
 			}
 		}
 		if(!work) {
 			return null;
 		}
		TransitionSystem out = fsms.get(0);
		for(int i = 1; i < fsms.size(); i++) {
			out = productHelper(out, fsms.get(i));
		}
		out.setId(out.getId() + "_product");
		return out;
	}

	/**
	 * This method performs the Parallel Composition of multiple TransitionSystems: the TransitionSystem calling this method and the TransitionSystems
	 * provided as arguments. The resulting, returned, TransitionSystem will be the same type as the calling TransitionSystem.
	 * 
	 * @param other - Array of TransitionSystem extending objects provided to perform Parallel Composition with the calling TransitionSystem object.
	 * @return - Returns a TransitionSystem extending object representing the result of all Parallel Composition operations.
	 */
	
	public static TransitionSystem parallelComposition(ArrayList<TransitionSystem> fsms){
 		boolean work = true;
 		for(TransitionSystem t : fsms) {
 			if(!t.hasStateAttribute(attributeInitialRef)) {
 				work = false;
 			}
 		}
 		if(!work) {
 			return null;
 		}
		TransitionSystem out = fsms.get(0);
		for(int i = 1; i < fsms.size(); i++) {
			out = parallelCompositionHelper(out, fsms.get(i));
		}
		out.setId(out.getId() + "_parallel");
		return out;
	}

//---  Support Methods   ----------------------------------------------------------------------
	
	private static HashMap<String, ArrayList<String>> collapseStates(TransitionSystem in){
		HashMap<String, ArrayList<String>> out = new HashMap<String, ArrayList<String>>();
		for(String s : in.getStateNames()) {
			collapseStatesRecurs(s, in, out, new HashSet<String>());
		}
		return out;
	}
	
	private static ArrayList<String> collapseStatesRecurs(String curr, TransitionSystem in, HashMap<String, ArrayList<String>> map, HashSet<String> visited){
		if(map.get(curr) != null) {
			return map.get(curr);
		}
		ArrayList<String> local = new ArrayList<String>();
		local.add(curr);
		if(visited.contains(curr)) {
			return local;
		}
		visited.add(curr);
		for(String e : in.getStateTransitionEvents(curr)) {
			if(!in.getEventAttribute(e, attributeObservableRef)){
				for(String t : in.getStateEventTransitionStates(curr, e)) {
					for(String g : collapseStatesRecurs(t, in, map, visited)) {
						if(!local.contains(g)) {
							local.add(g);
						}
					}
				}
			}
		}
		Collections.sort(local);
		map.put(curr, local);
		return local;
	}
	
	/**
	 * Helper method that performs the brunt of the operations involved with a single Product operation
	 * between two TransitionSystem objects, leaving the specialized features in more advanced TransitionSystem types to their
	 * own interpretations after this function has occurred.
	 * 
	 * Performs a product operation on the calling TransitionSystem with the first parameter TransitionSystem, and builds the
	 * resulting TransitionSystem in the second TransitionSystem. Has no return, does its action by side-effect.
	 * 
	 * @param other - TransitionSystem object representing the TransitionSystem object performing the Product operation with the calling TransitionSystem object.
	 * @param newTransitionSystem - TransitionSystem object representing the TransitionSystem holding the contents of the product of the Product operation.
	 */
	
	private static TransitionSystem productHelper(TransitionSystem in, TransitionSystem other) {
		TransitionSystem out = new TransitionSystem(in.getId() + "_product");
		out.copyAttributes(in);
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		use.add(in);
		use.add(other);
		// Get all the events the two have in common
		for(String thisEvent : in.getEventNames()) {
			if(other.getEventNames().contains(thisEvent)) {
				out.addEvent(thisEvent);
				out.compileEventAttributes(thisEvent, use);
			}
		}

		LinkedList<String> thisNextString = new LinkedList<String>();
		LinkedList<String> otherNextString = new LinkedList<String>();
		
		for(String thisInitial : in.getStatesWithAttribute(attributeInitialRef)) {
			for(String otherInitial : other.getStatesWithAttribute(attributeInitialRef)) {
				thisNextString.add(thisInitial);
				otherNextString.add(otherInitial);
			}
		}
		
		HashSet<String> visited = new HashSet<String>();
		
		while(!thisNextString.isEmpty() && !otherNextString.isEmpty()) { // Go through all the states connected
			String stateA = thisNextString.poll();
			String stateB = otherNextString.poll();
			ArrayList<String> nom = new ArrayList<String>();
			nom.addAll(in.getStateComposition(stateA));
			nom.addAll(other.getStateComposition(stateB));
			String newString = out.compileStateName(nom); // Add the new state
			
			if(visited.contains(newString)) {
				continue;
			}
			visited.add(newString);
			out.setStateComposition(newString, nom);
			nom.clear();
			nom.add(stateA);
			nom.add(stateB);
			
			out.addState(newString);
			out.compileStateAttributes(newString, nom, use);
			
			for(String s : in.getStateTransitionEvents(stateA)) {
				if(other.getStateTransitionEvents(stateB).contains(s)) {
					for(String t : in.getStateEventTransitionStates(stateA, s)) {
						for(String u : other.getStateEventTransitionStates(stateB, s)) {
							compileDestination(t, u, in.getStateComposition(t), other.getStateComposition(u), newString, s, out, use);
							thisNextString.add(t);
							otherNextString.add(u);
						}
					}
					
				}
			}
		} // while there are more states connected to the 2-tuple of initial states
		return out;
	} // productHelper(TransitionSystem)

	/**
	 * Helper method that performs the brunt of the operations involved with a single Parallel Composition
	 * operation between two TransitionSystem objects, leaving the specialized features in more advanced TransitionSystem types to
	 * their own interpretations after this function has occurred.
	 * 
	 * Performs a Parallel Composition operation on the TransitionSystem object calling this method with the TransitionSystem object provided as
	 * an argument (other), and places the results of this operation into the provided TransitionSystem object (newTransitionSystem).
	 * 
	 * @param other - TransitionSystem extending object that performs the Parallel Composition operation with TransitionSystem object calling this method.
	 * @param newTransitionSystem - TransitionSystem extending object that is provided to contain the results of this Parallel Composition operation.
	 */
	
	private static TransitionSystem parallelCompositionHelper(TransitionSystem in, TransitionSystem other) {
		TransitionSystem out = new TransitionSystem(in.getId() + "_product");
		out.copyAttributes(in);
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		use.add(in);
		use.add(other);
		// Get all the events the two have in common
		for(String thisEvent : in.getEventNames()) {
			if(other.getEventNames().contains(thisEvent)) {
				out.addEvent(thisEvent);
				out.compileEventAttributes(thisEvent, use);
			}
		}

		// Add all the events unique to each TransitionSystem
		for(String e : in.getEventNames())
			if(!out.eventExists(e))
				out.addEvent(e, in);

		for(String e : other.getEventNames())
			if(!out.eventExists(e))
				out.addEvent(e, other);

		LinkedList<String> thisNextString = new LinkedList<String>();
		LinkedList<String> otherNextString = new LinkedList<String>();

		for(String stateA : in.getStatesWithAttribute(attributeInitialRef)) {
			for(String stateB : other.getStatesWithAttribute(attributeInitialRef)) {
				thisNextString.add(stateA);
				otherNextString.add(stateB);
			}
		}
		HashSet<String> visited = new HashSet<String>();
		while(!thisNextString.isEmpty() && !otherNextString.isEmpty()) { // Go through all the states connected
			String stateA = thisNextString.poll();
			String stateB = otherNextString.poll();
			ArrayList<String> nom = new ArrayList<String>();
			nom.addAll(in.getStateComposition(stateA));
			nom.addAll(other.getStateComposition(stateB));
			String newString = out.compileStateName(nom); // Add the new state
			System.out.println(newString);
			if(visited.contains(newString)) {
				continue;
			}
			
			visited.add(newString);
			
			out.addState(newString);
			out.setStateComposition(newString, nom);
			
			nom.clear();
			nom.add(stateA);
			nom.add(stateB);
			
			out.compileStateAttributes(newString, nom, use);
			
			
			ArrayList<String> events = in.getStateTransitionEvents(stateA);
			events.addAll(other.getStateTransitionEvents(stateB));
			
			for(String s : events) {
				ArrayList<String> inStates = in.getStateEventTransitionStates(stateA, s);
				ArrayList<String> otherStates = other.getStateEventTransitionStates(stateB, s);
				if(inStates != null && inStates.size() != 0 && otherStates != null && otherStates.size() != 0) {
					for(String t : inStates) {
						for(String u : otherStates) {
							compileDestination(t, u, in.getStateComposition(t), other.getStateComposition(u), newString, s, out, use);
							thisNextString.add(t);
							otherNextString.add(u);
						}
					}
				}
				else if(inStates == null || inStates.size() == 0) {
					for(String t : otherStates) {
						compileDestination(stateA, t, in.getStateComposition(stateA), other.getStateComposition(t), newString, s, out, use);
						thisNextString.add(stateA);
						otherNextString.add(t);
					}
				}
				else {
					for(String t : inStates) {
						compileDestination(t, stateB, in.getStateComposition(t), other.getStateComposition(stateB), newString, s, out, use);
						thisNextString.add(t);
						otherNextString.add(stateB);
					}
				}
			}
		} // while there are more states connected to the 2-tuple of initial states
		return out;
	} // parallelCompositionHelper(TransitionSystem)

	
	private static void compileDestination(String stateA, String stateB, ArrayList<String> compA, ArrayList<String> compB, String source, String event, TransitionSystem out, ArrayList<TransitionSystem> use) {
		ArrayList<String> bun = new ArrayList<String>();
		bun.addAll(compA);
		bun.addAll(compB);
		String target = out.compileStateName(bun);
		out.addStateComposition(target, bun);
		bun.clear();
		bun.add(stateA);
		bun.add(stateB);
		out.addState(target);
		out.compileStateAttributes(target, bun, use);
		out.addTransition(source, event, target);
	}
	
	//Modal stuff?

	/*
	
	public static TransitionSystem getSupremalControllableSublanguage(TransitionSystem other) {
		// Store what events are disabled in the map.
		HashMap<String, DisabledEvents> disabledMap = new HashMap<String, DisabledEvents>();
		// Parse the graph and identify disabled states and disabled events
		for(String s : states.getStrings()) {
			HashSet<String> visitedStrings = new HashSet<String>();
			disabledMap.put(s.getStringName(), getDisabledEvents(s, other, visitedStrings, disabledMap));
		} // for every state
		
		// Now, build the TransitionSystem that we will return
		TransitionSystem newTransitionSystem = new TransitionSystem(this.id + " supremal controllable sublanguage");
		newTransitionSystem.copyStrings(this);
		newTransitionSystem.copyEvents(this);
		ArrayList<String> statesToRemove = new ArrayList<String>();
		// Identify and copy the transitions which are legal at each state
		for(String s : newTransitionSystem.getStrings()) {
			DisabledEvents disabled = disabledMap.get(s.getStringName());
			if(disabled.stateIsDisabled()) {
				statesToRemove.add(s);
			} else {
				ArrayList<Transition> allowedTransitions = new ArrayList<Transition>();
				ArrayList<Transition> transitions = this.transitions.getTransitions(getString(s));
				if(transitions != null) {
					for(Transition t : transitions) {
						String e = t.getTransitionEvent();
						if(disabled.eventIsEnabled(e.getEventName())) {
							// Create a list of the states the event leads to
							ArrayList<String> toStrings = new ArrayList<String>();
							for(String toS : (ArrayList<String>)t.getTransitionStrings()) {
								if(!disabledMap.get(toS.getStringName()).stateIsDisabled())
									toStrings.add(getString(toS.getStringName()));
							} // for every toString
							if(toStrings.size() > 0)
								allowedTransitions.add(new Transition(e, toStrings));
						} // if the event is enabled
					} // for every transition
				} // if there are any transitions
				if(allowedTransitions.size() > 0) {
					newTransitionSystem.addStringTransitions(s, allowedTransitions);
				} // if there are allowed transitions
			} // if disabled state/else
		} // for every state
//		System.out.println(disabledMap.toString());
		for(String s : statesToRemove) {
			newTransitionSystem.states.removeString(s.getStringName());
		}
		return newTransitionSystem;
	} // getSupremalControllableSublanguage(TransitionSystem)
	
	/**
	 * This method tries to get the maximally permissive (optimal) supervisor for an fsm
	 * (which will have certain transitions which are controllable and uncontrollable, etc.)
	 * that satisfies the modal specification. That is, it may ONLY have "may" transitions,
	 * and it MUST contain any "must" transitions (unless such a state does not exist, of course).
	 * 
	 * Any transitions which are illegal are removed, but if those transitions are uncontrollable,
	 * then we have a problem and we have to remove the entire state. Strings which do not lead
	 * to any marked states must also be removed. This process is done iteratively until all
	 * no more bad states exist and we have the maximally permissive supervisor.
	 * 
	 * This follows the algorithm explained in Darondeau et al., 2010.
	 * 
	 * @param fsm - TransitionSystem<<r>String, T, Event> object 
	 * @return - Returns a maximally permissive controller for the original TransitionSystem.
	 * @throws IllegalArgumentException - Throws an illegal argument exception if the
	 * TransitionSystem defines one of the events in the modal specification as an unobservable event, which
	 * is illegal in this implementation.
	
	public static ModalSpecification makeOptimalSupervisor(TransitionSystem ModalSpecification) throws IllegalArgumentException {
		return null;
		/*
		//--------------------------------------------
		// Step 1: Create the reachable part of the combo
		ModalSpecification universalObserverView = new ModalSpecification("UniObsView");
		HashMap<String, String> universalObserverViewMap = createUniversalObserverView(ModalSpecification, universalObserverView);
		String universalInitial = universalObserverViewMap.get(ModalSpecification.getInitialStrings().get(0).getStringName());
		universalObserverView.addInitialString(universalInitial);
		
		TransitionSystem specTransitionSystem = getUnderlyingTransitionSystem();
		
		// If we have unobservable events in the specification, that's illegal
		for(Event e : specTransitionSystem.events.getEvents()) {
			// See if it's visible in the ModalSpecification
			Event otherE = ModalSpecification.events.getEvent(e);
			if(otherE instanceof EventObservability && !((EventObservability)otherE).getEventObservability())
				throw new IllegalArgumentException("The modal specification has the event \"" + e.getEventName() + "\", which is an unobservable event in the plant passed in for getting the supervisor. The specification should only have observable events.");
		}
		
		ModalSpecification product = universalObserverView.product(specTransitionSystem);
		
		//--------------------------------------------
		// Step 2: Mark the bad states
		HashSet<String> badStrings = new HashSet<String>();
		boolean keepGoing = true;
		
		while(keepGoing) {
			boolean keepGoing1 = markBadStrings(ModalSpecification, specTransitionSystem, product, badStrings);
			boolean keepGoing2 = markDeadEnds(universalObserverView, universalObserverViewMap, product, badStrings);
			
			keepGoing = keepGoing1 || keepGoing2;
		} // while
		
		// Now, we have to actually create our ModalSpecification
		ModalSpecification supervisor = new ModalSpecification(product, badStrings, fsm.id + " Supervisor");
		return supervisor.makeAccessible();
	}
	
	/**
	 * This marks states that are bad states in a supervisor for an TransitionSystem that is trying to satisfy a
	 * modal specification. There are two types of bad states:<br/>
	 * 1) When there is some uncontrollable and observable event where there is a possible state in
	 * the determinized collection of states where the event is possible, but there is no such possible
	 * transition in the specification; and<br/>
	 * 2) When there is some observable event where the event must be possible according to the specification,
	 * but there is no such transition defined for the determinized collection of states.
	 * 
	 * @param fsm - Original TransitionSystem which needs to be controlled.
	 * @param specTransitionSystem - TransitionSystem underlying the modal specification object.
	 * @param product - TransitionSystem representing the product of the determinized first TransitionSystem with the specification.
	 * @param badStrings - HashMap of all the states already marked as bad, which will be further updated as
	 * we go through this iteration.
	 * @return - Returns true if at least one state was marked as bad, false otherwise.
	
	private boolean markBadStrings(TransitionSystem fsm, TransitionSystem specTransitionSystem, TransitionSystem product, HashSet<String> badStrings) {
		// We need to parse every state in the product, check every component state if there is some uncontrollable observable
		// event that isn't defined in the product.
		// Also look if must transitions exist...
		boolean foundABadOne = false;
		
		System.out.println("Going through bad states");
	
		// Go through every state in the product
		for(String s : product.getStrings()) {
			// Don't process the state if it's already bad
			if(badStrings.contains(s.getStringName())) continue;
			
			// If a must transition does not exist at the state, mark the state
			String specStringName = product.getStringComposition(s).get(1).getStringName(); // Gets the specification
			ArrayList<Transition> specTransitions = this.mustTransitions.getTransitions(this.getString(specStringName));
			if(specTransitions != null) for(Transition t : specTransitions) {
				String event = t.getTransitionEvent();
				ArrayList<String> toStrings = product.transitions.getTransitionStrings(s, product.events.getEvent(event));
				// Mark the state as bad if the must transition does not exist
				if(toStrings == null) {
					System.out.println("There was no must transition for an event, " + event.getEventName() + ", at state " + s.getStringName());
					badStrings.add(s.getStringName());
					foundABadOne = true;
				} else {
					// Mark the state as bad if all the states it leads to are bad
					boolean itsBad = true;
					for(String toString : toStrings)
						if(!badStrings.contains(toString.getStringName())) itsBad = false;
					if(itsBad) {
						badStrings.add(s.getStringName());
						foundABadOne = true;
					}
				}
			} // for all the state's transitions
			
			// If an uncontrollable observable event exists from any of the states in the original fsm, and
			// the event not allowed in the product, then UH-NO NOT HAP'NIN (mark the state)
			String observerString = product.getStringComposition(s).get(0);
			ArrayList<String> origStrings = product.getStringComposition(observerString);
			for(String fromString : origStrings) {
				// Go through all the original transitions
				ArrayList<Transition> origTransitions = fsm.transitions.getTransitions((String)fromString);
				if(origTransitions != null) {
					for(Transition t : origTransitions) {
						String event = t.getTransitionEvent();
						// If the event is observable but NOT controllable, we have a problem
						if(event.getEventObservability() &&  !event.getEventControllability()) {
							// Then the event must be present in the product
							ArrayList<String> toStrings = product.transitions.getTransitionStrings(product.getString(s), product.events.getEvent(event));
							// Mark the state as bad if the event is not allowed in the product.
							if(toStrings == null) {
								System.out.println("There was an uncontrollable, observable event, " + event.getEventName() + ", that did not exist in the spec at state " + s.getStringName());
								badStrings.add(s.getStringName());
								foundABadOne = true;
							} else {
								// Mark the state as bad if all the states it leads to are bad
								boolean itsBad = true;
								for(String toString : toStrings)
									if(!badStrings.contains(toString.getStringName()))
										itsBad = false;
								if(itsBad) {
									badStrings.add(s.getStringName());
									foundABadOne = true;
								}
							}
						} // if it's observable but NOT controllable
					} // for all the transitions
				}//if origTransition is not null
			} // for every component state in the original fsm
		} // for every state
		return foundABadOne;
	} // markBadStrings(TransitionSystem, TransitionSystem, TransitionSystem, HashSet)
	
	/**
	 * Marks the dead ends where there is some state q in the set of states P in the product (P, s)
	 * that cannot reach a marked state in the universalObserverView using only transitions that are
	 * allowed in the product.
	 * 
	 * @param universalObserverView - Observer view of the original TransitionSystem with states for every possible
	 * starting state (for instance, if the TransitionSystem had a state 2, then there will be a state representing
	 * the epsilon-reach of 2 in this universal view).
	 * @param universalObserverViewMap - Map between the original TransitionSystem's states and their epsilon reach state
	 * name, which is present in the universalObserverView.
	 * @param product - Product TransitionSystem between the observer view of the original fsm and the specification.
	 * @param badStrings - HashSet of String state names which are bad and to be removed.
	 * @return - Returns a boolean value; true if the method marked a bad state, false otherwise.

	public static boolean markDeadEnds(TransitionSystem universalObserverView, HashMap<String, String> universalObserverViewMap, TransitionSystem product, HashSet<String> badStrings) {
		// For every combo of states (q,(P,s)) such that q is an element of P and (P,s) is the product, we want to
		// perform the product with initial states being the parameter product and every possible q. If it is
		// possible to reach a marked state in the product from this initial point, then it's ok! If there's one
		// q for the state which cannot reach a good state, then we have to add the state to the bad states.
		
		boolean removedAString = false;
		
		// To recover after operations:
		ArrayList<String> productInitialStrings = product.getInitialStrings();
		
		// Go through all the good product states
		for(String productString : product.getStrings()) if(!badStrings.contains(productString.getStringName())) {
			// Get all the states in the original TransitionSystem
			String leftSideOfProduct = product.getStringComposition(productString).get(0);
			ArrayList<String> originalStrings = product.getStringComposition(leftSideOfProduct);
			for(String q : originalStrings) {
				String universalInitial = universalObserverViewMap.get(q.getStringName());
				universalObserverView.addInitialString(universalInitial);
				product.addInitialString(productString);
				TransitionSystem massiveProduct = (TransitionSystem)universalObserverView.product(product);
				// Now, we get to look through the massive product for a marked state
				boolean reachesMarked = canReachMarked(massiveProduct, massiveProduct.getInitialStrings().get(0), badStrings);
				if(!reachesMarked) {
					System.out.println(massiveProduct.getInitialStrings().get(0).getStringName() + " could not reach a marked state.");
					badStrings.add(productString.getStringName());
					removedAString = true;
					break;
				} // if cannot reach a marked state
			} // for every state q in the product's initial states
		} // for every product state that isn't marked as bad
		
		// Recover original properties of TransitionSystems:
		product.addInitialString(productInitialStrings.get(0));
		
		return removedAString;
	} // markDeadEnds(TransitionSystem, HashMap, TransitionSystem, HashSet)
	
	/**
	 * Checks if the TransitionSystem can reach a marked state from the initial state given. It excludes checking any
	 * states that have a badString in the right part of the product (the second element in the state name).
	 * 
	 * @param fsm Deterministic TransitionSystem to check.
	 * @param state String to look 
	 * @param badStrings
	 * @return - Returns TODO:
	
	static private boolean canReachMarked(TransitionSystem fsm, String state, HashSet<String> badStrings) {
		// Get the name of the state in the right side of the product in the fsm (second part of the state).
		String name = fsm.getStringComposition(state).get(1).getStringName();
		if(badStrings.contains(name)) return false;
		if(state.getStringMarked()) return true;
		
		HashSet<String> visited = new HashSet<String>();
		LinkedList<String> queue = new LinkedList<String>();
		visited.add(state);
		queue.add(state);
		
		while(!queue.isEmpty()) {
			// Go through all neighbours in BFS
			String curr = queue.poll();
			for(Transition t : fsm.transitions.getTransitions(curr)) for(String toString : t.getTransitionStrings()) {
				name = fsm.getStringComposition(toString).get(1).getStringName();
				if(!badStrings.contains(name)) {
					if(toString.getStringMarked()) return true; // if marked, we're all good!
					if(!visited.contains(toString)) { // if it hasn't been visited yet, add to queue
						queue.add(toString);
						visited.add(toString);
					} // if not yet visited
				} // if it's not a bad state
			} // for every transition state
		} // while the queue still has stuff in it
		
		return false;
	} // canReachMarked(TransitionSystem, HashSet)
	*/
	
}
