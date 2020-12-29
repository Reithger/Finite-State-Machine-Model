package model.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import model.fsm.FSM;
import model.fsm.ModalSpecification;
import model.fsm.State;
import model.fsm.Transition;
import model.fsm.TransitionSystem;

public class ProcessClean {
	
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

	/**
	 * This method implements the new method of performing a 'prune' on a Modal Specification as
	 * described by Graeme Zinck's research; the crux point of it being that if a State is found
	 * to be Bad, it may not be by virtue of finding a legalizing Transition using only Events
	 * unique to one of the combining Modal Specifications.
	 * 
	 * tldr; There's a second way for a State to be Good.
	 * 
	 * Overall the method finds all bad states which break the rules of a Modal Specification
	 * and removes them alongside their Transitions, leaving only a non-blocking workable system.
	 * 
	 * @param modal1 - The first of the two Modal Specification objects being combined in a Greatest Lower Bound operation.
	 * @param modal2 - The second of the two Modal Specification objects being combined in a Greatest Lower Bound operation.
	 * @param composedModal - The composed Modal Specification object that we are pruning after having been created.
	 * @return - Returns the pruned version of the provided composed Modal Specification, composedModal.
	 */
	
	public ModalSpecification newPrune(ModalSpecification modal1, ModalSpecification modal2, ModalSpecification composedModal) {
		boolean mustIterate = true;
		HashSet<State> badStates = new HashSet<State>();
		
		while(mustIterate) {				//Iterate through all States until a pass through does not change anything
			mustIterate = false;			//In each iteration, check if the State is bad via stateIsBad() method.
			for(State s : composedModal.getStates()) {
				if(badStates.contains(s))	//Unless already dealt with and found to be bad. (If good, need check again.)
					continue;
				if(stateIsBad(modal1, modal2, composedModal, s)) {
					badStates.add(s);		//If found bad, add to list, and remove from the Modal Specification entirely.
					mustIterate = true;		//Requires n-state traversal because we don't have back-referencing.
					for(State top : composedModal.getStates()) {
						for(int i = 0; i < composedModal.getTransitions().getTransitions(top).size(); i++){
							Transition t = composedModal.getTransitions().getTransitions(top).get(i);
							if(s.equals(t.getTransitionStates().get(0)))
								composedModal.getTransitions().getTransitions(top).remove(t);
						}
					}
				}
			}
		}
		for(State s : badStates) {
			composedModal.getStateMap().removeState(s.getStateName());	//Now remove the bad States
		}
		composedModal.getTransitions().removeStates(badStates);
		for(State s : composedModal.getStates()) {				//Some retention of bad Transitions; we know the State is good, just remove the Transition.
			ArrayList<Transition> mustTrans = composedModal.getMustTransitions().getTransitions(s);
			ArrayList<Transition> mayTrans = composedModal.getTransitions().getTransitions(s);
			for(int i = 0; i < mustTrans.size(); i++) {
				Transition t = mustTrans.get(i);
				if(!mayTrans.contains(t)) {
					composedModal.getMustTransitions().getTransitions(s).remove(t);
					i--;
				}
			}
		}
		for(State s : badStates) {
			composedModal.getMustTransitions().removeState(s);
			composedModal.getTransitions().removeState(s);
		}
		
		return composedModal.makeAccessible();		//And some bits will be left in but disjoint, so clean that up.
	}

	/**
	 * This method prunes a ModalSpecification by going through all states and removing those that
	 * have a must transition without a corresponding may transition, and then removing all states
	 * with a transition going to those bad states.
	 * 
	 * Superceded by the newPrune() algorithm.
	 * 
	 * @return - Returns a pruned ModalSpecification object.
	 */

	public ModalSpecification prune() {
		// First, get all the inconsistent states
		HashSet<String> badStates = transitions.getInconsistentStates(mustTransitions);
		// Now, go through all the states and see if there are must transitions to these inconsistent
		// states. If there are, those states must be removed (iteratively).
		while(getBadMustTransitionStates(badStates));
		
		return (new ModalSpecification(this, badStates, this.id)).makeAccessible();
	}

}
