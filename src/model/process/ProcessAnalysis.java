package model.process;

import java.util.ArrayList;

import model.AttributeList;
import model.fsm.TransitionSystem;

public class ProcessAnalysis {
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * Gets if the FSM is blocking; that is, if there are possible words which are not
	 * part of the prefix closure of the marked language of the FSM. In other words, if
	 * the FSM is NOT coaccessible, then the FSM is blocking.
	 * It marks bad states along the way.
	 * 
	 * @return - Returns a boolean value; true if the FSM is found to be blocking, false otherwise.
	 */
	
	public static boolean isBlocking(TransitionSystem in) {
		TransitionSystem coAccess = ProcessClean.makeCoAccessible(in);
		return in.getStateNames().size() == coAccess.getStateNames().size();
	} // isBlocking()
	
	public static ArrayList<String> findPrivateStates(TransitionSystem in){
		return in.getStatesWithAttribute(AttributeList.ATTRIBUTE_PRIVATE);
	}
	
	public static boolean testOpacity(TransitionSystem in) {
		return findPrivateStates(in).size() == 0;
	}

	
	/*
	
	/**
	 * This method gets the ModalSpecification representing the greatest lower bound between
	 * the calling and parameter ModalSpecifications.
	 * 
	 * @param other - ModalSpecification to use for calculating the greatest lower bound.
	 * @return - Returns a ModalSpecification representing the greatest lower bound between the two
	 * ModalSpecifications.
	
	public ModalSpecification getGreatestLowerBound(ModalSpecification other) {
		ModalSpecification newMS = getPseudoLowerBound(other);
		return newPrune(this, other, newMS);
	}
	
	/**
	 * This method gets the pseudo-modal specification representing the lower bound of
	 * the calling and parameter ModalSpecification inputs. This means:
	 * - All "may" transitions for a common event must be present in both to exist.
	 * - All "may" transitions for a private event must only be present in one to exist.
	 * - All "must" transitions for any event must be present in at least one to exist.
	 * 
	 * @param other - ModalSpecification which will be used in conjunction with the calling
	 * modal specification to create a new ModalSpecification. The result will be the lower
	 * bound of the two.
	 * @return - Returns a Pseudo modal specification representing the lower bound of the two.
	 * This result needs to be pruned to remove states where there exists a must transition but
	 * no corresponding may transition.
	
	private ModalSpecification getPseudoLowerBound(ModalSpecification other) {
		ModalSpecification newMS = new ModalSpecification(this.id + " Lower Bound");
		
		// Also, identify which states are already visited so we don't go in loops
		HashSet<String> visited = new HashSet<String>();
		
		// Start at the beginning of each MS
		LinkedList<NextStates> next = new LinkedList<NextStates>();
		if(this.initialState != null && other.initialState != null) { // If one doesn't have an initial, we have a problem.
			State newInitial = newMS.states.addState(this.initialState, other.initialState);
			newMS.initialState = newInitial;
			next.add(new NextStates(this.initialState, other.initialState, newInitial));
		}
		while(!next.isEmpty()) {
			NextStates curr = next.poll();
			
			if(visited.contains(curr.stateNew.getStateName()))
				continue; // If we already added the state, skip this iteration
			curr.addToComposition(newMS);
			visited.add(curr.stateNew.getStateName());
			
			// Go through all the MAY transitions common in both
			next.addAll(newMS.copyCommonTransitions(curr, this, other));
			next.addAll(newMS.copyPrivateTransitions(curr, this, other));
			next.addAll(newMS.copyMustTransitions(curr, this, other));
		} // while there are states in the queue
		return newMS;
	}
	
	/**
	 * This gets the epsilon reaches of all each state, mapping the state to a set
	 * of states which are reachable with unobservable events.
	 * 
	 * @param fsmStates Collection of states which exists in the FSM.
	 * @return Hashmap mapping each state to a hashset of states (which are all reachable
	
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
				Entity e = t.getTransitionEvent();
				
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

	/**
	 * This method finds the epsilon-reach of a given State in a ModalSpecification (can be generalized easily);
	 * that is, all States that that first State can reach via Unobservable Events, returned as a HashSet to avoid
	 * duplicates.
	 * 
	 * @param ts - ModalSpecification object provided to have its States searched through.
	 * @param s - State object provided as the root State to search for's epsilon-reach.
	 * @param must - boolean value describing whether or not the algorithm should respect May/Must Transitions or not.
	 * @return - Returns a HashSet<<r>State> object containing all States in the epsilon-reach of the provided first State.
	
	public HashSet<State> epsilonReach(ModalSpecification ts, State s, boolean must){
		HashSet<State> outbound = new HashSet<State>();
		
		LinkedList<State> queue = new LinkedList<State>();
		HashSet<State> visited = new HashSet<State>();
		queue.add(s);
		
		while(!queue.isEmpty()) {
			State st = queue.poll();
			if(visited.contains(st))
				continue;
			visited.add(st);
			outbound.add(st);
			for(Transition t : ts.getTransitions().getTransitions(st)) {
				if(!t.getTransitionEvent().getEventAttackerObservability()) {
					queue.addAll(t.getTransitionStates());
				}
			}
		}
		return outbound;
	}

	/**
	 * This is a helper method to the newPrune algorithm that searches through a provided State's transitions
	 * to decide if it is a 'good' State or a 'bad' State.
	 * 
	 * @param modal1 - Modal Specification object representing the first of the two composed objects.
	 * @param modal2 - Modal Specification object representing the second of the two composed objects.
	 * @param composedModal - Modal Specification object representing the composition of the two Modal Specifications
	 * @param s - State object representing the State being checked for its status as Good or Bad.
	 * @return - Returns a boolean value describing whether or not a State is good or bad.
	
	public boolean stateIsBad(ModalSpecification modal1, ModalSpecification modal2, ModalSpecification composedModal, State s) {
		ArrayList<Transition> mustTrans = composedModal.getMustTransitions().getTransitions(s);
		ArrayList<Transition> mayTrans = composedModal.getTransitions().getTransitions(s);
		ArrayList<State> compos = composedModal.getStateComposition(s);
		
		State modalState1 = compos.get(0);	//Get Transitions of the composed Modal Spec. and a State from one of its forebears
											//Need to get info on event privacy and a reference into one of the composing Modal Spec.
		EventMap shared = modal1.getEventMap().getSharedEvents(modal2.getEventMap());
		
		for(Transition t : mustTrans) {	//For each Must Transition, there needs to be a May as well
			if(!mayTrans.contains(t)) {		//If there isn't, can it reach via the second approach? (Traverse along invisible events)
				boolean isBad = true;		//Use reference State from original Modal Spec. to see which events are allowed.
				if(!modal1.getMustTransitions().getTransitions(modalState1).contains(t)) {
					isBad = composedModal.privateEventSearch(modal1.getEventMap(), s, shared, t.getTransitionEvent());
				}
				else {
					isBad = composedModal.privateEventSearch(modal2.getEventMap(), s, shared, t.getTransitionEvent());
				}
				if(isBad) {					//If the result was a positive, then it's bad (no alternate route), say so.
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This helper method searches through the calling Modal Specification for a specified Event, traversing using
	 * only those Events from the provided EventMap that are not found in the second provided EventMap; the former
	 * representing Event from one of the two Modal Specifications that composed to create the calling Modal Specification,
	 * and the latter representing the Events shared between them. (Used to find unique Events.) 
	 * 
	 * @param mod - EventMap object holding Events from a Modal Specification that was used to create the calling object.
	 * @param modState - State object representing the starting State to perform a search from.
	 * @param shared - EventMap object holding the Events shared between the two Modal Specifications that formed the calling object.
	 * @param e - Event object representing the Event being searched for via an alternate route.
	 * @return - Returns a boolean denoting the result of the search; true if it did not find the event, false otherwise.
	
	public boolean privateEventSearch(EventMap mod, State modState, EventMap shared, Entity e) {
		LinkedList<State> queue = new LinkedList<State>();
		HashSet<State> visited = new HashSet<State>();
		queue.add(modState);
		boolean isBad = true;
		bfs:
		while(!queue.isEmpty()) {	//Breadth-First Search approach
			State top = queue.poll();
			if(visited.contains(top)) 
				continue;
			visited.add(top);
			for(Transition trans : this.getMustTransitions().getTransitions(top)){	//Find matching Transitions
				if(this.getTransitions().contains(top, trans)) {						//Either add to queue or finish.
					if(!shared.contains(trans.getTransitionEvent()) && mod.contains(trans.getTransitionEvent())) {
						queue.add(trans.getTransitionStates().get(0));
					}
					else if(trans.getTransitionEvent().equals(e)) {
						isBad = false;
						break bfs;
					}
				}
			}
		}
		return isBad;
	}
	
	/**
	 * Gets all the states that have must transitions to a state marked as a bad state
	 * in the badStates parameter. All the states with these must transitions are then added
	 * to badStates.
	 * 
	 * @param badStates - HashSet of Strings representing the States which are bad and will
	 * be removed from the TransitionSystem.
	 * @return - Returns a boolean value representing if the method added any bad states to the set.

	private boolean getBadMustTransitionStates(HashSet<String> badStates) {
		boolean markedAState = false;
		// Go through all OK nodes, and if there is a state that leads to a badState with a
		// must transition, add the state to the badStates.
		for(State curr : states.getStates()) if(!badStates.contains(curr.getStateName())) {
			for(Transition transition : mustTransitions.getTransitions(curr)) {
				if(badStates.contains(transition.getTransitionStates().get(0).getStateName())) {
					badStates.add(curr.getStateName());
					markedAState = true;
				}
			}
		}
		return markedAState;
	} // getBadMustTransitionStates(HashSet<String>)
	*/

}
