package model.process;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;

public class ProcessClean {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	public static String attributeInitialRef;
	public static String attributeMarkedRef;
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static void assignAttributeReferences(String init, String mark) {
		attributeInitialRef = init;
		attributeMarkedRef = mark;
	}
	
	/**
	 * This method performs a trim operation on the calling TransitionSystem (performing the
	 * makeAccessible() and makeCoAccessible() methods) to make sure only states
	 * that are reachable from Initial Strings and can reach Marked Strings are
	 * included.
	 * 
	 * @return - Returns a TransitionSystem<<r>T> object representing the trimmed
	 * version of the calling TransitionSystem object.
	 */
	
	public static TransitionSystem trim(TransitionSystem in) {
		if(!in.hasStateAttribute(attributeInitialRef) || !in.hasStateAttribute(attributeMarkedRef)) {
			return null;
		}
		TransitionSystem out = in.copy();
		out = makeAccessible(out);
		out = makeCoAccessible(out);
		out.setId(in.getId() + "_trim");
		return out;
	}
	
	/**
	 * Searches through the graph represented by the TransitionFunction object, and removes
	 * disjoint elements.
	 * 
	 * Algorithm starts from all initial Strings, and adds them to a queue. They are
	 * then placed into the new TransitionFunction object, and all Strings reachable by these
	 * initial states are placed in a queue for processing. Strings are checked against being
	 * already present in the newTransitionSystem object, not being added to the queue if already handled.
	 * 
	 * Some post-processing may be required by more advanced types of TransitionSystem.
	 * 
	 * @return - Returns a TransitionSystem object representing the accessible
	 * version of the calling TransitionSystem object. 
	 */
	
	public static TransitionSystem makeAccessible(TransitionSystem in) {
		if(!in.hasStateAttribute(attributeInitialRef)) {
			return null;
		}
		// Make a queue to keep track of states that are accessible and their neighbours.
		TransitionSystem out = new TransitionSystem(in.getId() + "_accessible");
		out.copyAttributes(in);
		out.mergeEvents(in);
		LinkedList<String> queue = new LinkedList<String>();
		
		// Initialize a new TransitionSystem with initial states.
		for(String initial : in.getStatesWithAttribute(attributeInitialRef)) {
			queue.add(initial);
		} // for initial state
		
		HashSet<String> visited = new HashSet<String>();
		
		while(!queue.isEmpty()) {
			String curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			out.addState(curr, in);
			
			for(String t : in.getStateTransitionEvents(curr)) {
				for(String d : in.getStateEventTransitionStates(curr, t)) {
					out.addTransition(curr, t, d, in);
					queue.add(d);
				}
			}
		} // while
		
		return out;
	} // makeAccessible()
	
	/**
	 * Searches through the graph represented by the TransitionFunction object, and removes any
	 * states that cannot reach a marked state.
	 * 
	 * A helper method is utilized to generate a list of Strings and their legality in the new TransitionSystem object,
	 * after which the contents of the old TransitionSystem object are processed with respect to this list as they construct
	 * the new TransitionSystem object.
	 * 
	 * @return - Returns an TransitionSystem object representing the CoAccessible version of the original
	 * TransitionSystem.
	 */
	
	public static TransitionSystem makeCoAccessible(TransitionSystem in) {
		if(!in.hasStateAttribute(attributeInitialRef) || !in.hasStateAttribute(attributeMarkedRef)) {
			return null;
		}
		TransitionSystem out = new TransitionSystem(in.getId() + "_coaccess");
		out.copyAttributes(in);
		out.mergeEvents(in);
		// First, find what states we need to add.
		ArrayList<String> processedStrings = getCoAccessibleMap(in);	//Use helper method to generate list of legal/illegal Strings

		System.out.println(processedStrings);		// [2, 3, 7, 8, 9]
		
		// Secondly, create the states and add the transitions
		for(String s : processedStrings) {
			out.addState(s, in);
			
			for(String e : in.getStateTransitionEvents(s)) {
				for(String t : in.getStateEventTransitionStates(s, e)) {
					if(processedStrings.contains(t)) {
						out.addTransition(s, e, t, in);
					}
				}
			}
		} // if coaccessible

		// Finally, add the initial state
		for(String state : in.getStatesWithAttribute(attributeInitialRef)) {
			if(processedStrings.contains(state))
				out.setStateAttribute(state, attributeInitialRef, true);
		}
		return out;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	/**
	 * Helper method that processes the calling FSM object to generate a list of States for that
	 * object describing their status as CoAccessible, or able to reach a Marked State.
	 * 
	 * @return - Returns a HashMap<<r>String, Boolean> object mapping String state names to true if the state is coaccessible, and false if it is not.
	 */
	
	private static ArrayList<String> getCoAccessibleMap(TransitionSystem in) {
		// When a state is processed, add it to the map and state if it reached a marked state.
		HashSet<String> positive = new HashSet<String>();
		HashSet<String> negative = new HashSet<String>();
		HashSet<String> visited = new HashSet<String>();
		
		for(String curr : in.getStateNames()) {
			if(!positive.contains(curr))
				recursivelyFindMarked(curr, positive, negative, visited, in);
		}
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(positive);
		return out;
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
	
	private static boolean recursivelyFindMarked(String curr, HashSet<String> positive, HashSet<String> negative, HashSet<String> visited, TransitionSystem in) {
		if(positive.contains(curr) || (in.getStateAttribute(curr, attributeMarkedRef) != null && in.getStateAttribute(curr, attributeMarkedRef))) {
			positive.add(curr);
			return true;
		}
		else if(negative.contains(curr)) {
			return false;
		}
		else if(visited.contains(curr)) {
			return false;
		}
		visited.add(curr);
		for(String t : in.getStateTransitionEvents(curr)) {
			for(String g : in.getStateEventTransitionStates(curr, t)) {
				if(recursivelyFindMarked(g, positive, negative, visited, in)) {
					positive.add(curr);
					return true;
				}
			}
		}
		negative.add(curr);
		return false;
	} // recursivelyFindMarked(State, HashMap<String, Boolean>, HashSet<String>)
	
	
	//-- Weird, do it later vvvv
	
	/**
	 * This method implements the new method of performing a 'prune' on a Modal Specification as
	 * described by Graeme Zinck's research; the crux point of it being that if a String is found
	 * to be Bad, it may not be by virtue of finding a legalizing Transition using only Strings
	 * unique to one of the combining Modal Specifications.
	 * 
	 * tldr; There's a second way for a String to be Good.
	 * 
	 * Overall the method finds all bad states which break the rules of a Modal Specification
	 * and removes them alongside their Transitions, leaving only a non-blocking workable system.
	 * 
	 * @param modal1 - The first of the two Modal Specification objects being combined in a Greatest Lower Bound operation.
	 * @param modal2 - The second of the two Modal Specification objects being combined in a Greatest Lower Bound operation.
	 * @param composedModal - The composed Modal Specification object that we are pruning after having been created.
	 * @return - Returns the pruned version of the provided composed Modal Specification, composedModal.
	
	
	 public static TransitionSystem newPrune(TransitionSystem modal1, TransitionSystem modal2, TransitionSystem composedModal) {
		boolean mustIterate = true;
		HashSet<String> badStrings = new HashSet<String>();
		
		while(mustIterate) {				//Iterate through all Strings until a pass through does not change anything
			mustIterate = false;			//In each iteration, check if the String is bad via stateIsBad() method.
			for(String s : composedModal.getStrings()) {
				if(badStrings.contains(s))	//Unless already dealt with and found to be bad. (If good, need check again.)
					continue;
				if(stateIsBad(modal1, modal2, composedModal, s)) {
					badStrings.add(s);		//If found bad, add to list, and remove from the Modal Specification entirely.
					mustIterate = true;		//Requires n-state traversal because we don't have back-referencing.
					for(String top : composedModal.getStrings()) {
						for(int i = 0; i < composedModal.getTransitions().getTransitions(top).size(); i++){
							Transition t = composedModal.getTransitions().getTransitions(top).get(i);
							if(s.equals(t.getTransitionStrings().get(0)))
								composedModal.getTransitions().getTransitions(top).remove(t);
						}
					}
				}
			}
		}
		for(String s : badStrings) {
			composedModal.getStringMap().removeString(s);	//Now remove the bad Strings
		}
		composedModal.getTransitions().removeStrings(badStrings);
		for(String s : composedModal.getStrings()) {				//Some retention of bad Transitions; we know the String is good, just remove the Transition.
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
		for(String s : badStrings) {
			composedModal.getMustTransitions().removeString(s);
			composedModal.getTransitions().removeString(s);
		}
		
		return composedModal.makeAccessible();		//And some bits will be left in but disjoint, so clean that up.
	}

	 * This method prunes a TransitionSystem by going through all states and removing those that
	 * have a must transition without a corresponding may transition, and then removing all states
	 * with a transition going to those bad states.
	 * 
	 * Superceded by the newPrune() algorithm.
	 * 
	 * @return - Returns a pruned TransitionSystem object.

	public static TransitionSystem prune(TransitionSystem in) {
		// First, get all the inconsistent states
		HashSet<String> badStrings = transitions.getInconsistentStrings(mustTransitions);
		// Now, go through all the states and see if there are must transitions to these inconsistent
		// states. If there are, those states must be removed (iteratively).
		while(getBadMustTransitionStrings(badStrings));
		
		return (new TransitionSystem(this, badStrings, this.id)).makeAccessible();
	}

	*/
	
}
