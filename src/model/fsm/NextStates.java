package model.fsm;

import model.fsm.state.State;

/**
	 * This class models an object containing three states: the state from some transition system A,
	 * the state from some transition system B, and the state from some new transition system which
	 * is being created. This simply makes cleaner code in other areas.
	 * 
	 * This class is a part of the fsm package.
	 * 
	 * @author Ada Clevinger and Graeme Zinck
	 */

public class NextStates {
	
	/** State objects representing composite States and their joined product.*/
	State stateA, stateB, stateNew;
	
	/**
	 * Constructor for a NextStates object.
	 * 
	 * @param stateFromA - State object representing the State from the first Modal Specification being joined.
	 * @param stateFromB - State object representing the State from the second Modal Specification being joined.
	 * @param stateFromNew - State object representing the produced State from joining two States.
	 */
	
	public NextStates(State stateFromA, State stateFromB, State stateFromNew) {
		stateA = stateFromA;
		stateB = stateFromB;
		stateNew = stateFromNew;
		if(stateFromA.getStatePrivate() || stateFromB.getStatePrivate())
			stateFromNew.setStatePrivate(true);
		else
			stateFromNew.setStatePrivate(false);
		}		
	
	/**
	 * Helper method to assist in building the library of States and their compositions; i.e, what
	 * States were adjoined to create a new State.
	 * 
	 * @param ms - ModalSpecification object into which the new pairing of States will be added.
	 */
	
	public void addToComposition(ModalSpecification ms) {
		ms.setStateComposition(stateNew, stateA, stateB);
		}
	
}
