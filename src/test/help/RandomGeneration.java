package test.help;

import java.util.ArrayList;

import model.AttributeList;
import model.Manager;

public class RandomGeneration {
	
	public static void setupRandomFSMConditions(Manager model, int numObsEve, int numContrEve, int badTran) {
		ArrayList<String> stateAtt = new ArrayList<String>();
		ArrayList<String> eventAtt = new ArrayList<String>();
		ArrayList<String> transAtt = new ArrayList<String>();
		
		stateAtt.add(AttributeList.ATTRIBUTE_INITIAL);
		for(String s : EventSets.EVENT_ATTR_LIST) {
			eventAtt.add(s);
		}
		transAtt.add(AttributeList.ATTRIBUTE_BAD);
		
		ArrayList<Integer> stat = new ArrayList<Integer>();
		
		stat.add(1);
		
		model.assignRandomFSMStateConfiguration(stateAtt, stat);

		ArrayList<Integer> even = new ArrayList<Integer>();

		even.add(numObsEve);
		even.add(numContrEve);
		
		model.assignRandomFSMEventConfiguration(eventAtt, even);
		
		ArrayList<Integer> tran = new ArrayList<Integer>();
		
		tran.add(badTran);

		model.assignRandomFSMTransitionConfiguration(transAtt, tran);
	}
	
	/**
	 * 
	 * @param nom
	 * @param model
	 * @param numStates
	 * @param numEvents
	 * @param numTransition
	 * @param numObsEve
	 * @param numContrEve
	 * @param badTran
	 * @return
	 * @throws Exception 
	 */
	
	public static String generateRandomFSM(String nom, Manager model, int numStates, int numEvents, int numTransition, boolean accessible) throws Exception {
		String out;
		do {
			out = model.readInFSM(model.generateRandomFSM(nom, numStates, numEvents, numTransition, false));
		}while(!accessible || !model.isAccessible(out));
		return out;
	}
	
	/**
	 * 
	 * Total event set size will be 1/2 # of plants + specs * eventSizeAverage
	 * 
	 * EventShareRate is proportional to how many other components already contain an event? Or for how many events for a component are shared by others tempered by the prevalence of its events. Hmm...
	 * 
	 * OR: each component has x number of events, and during its setup it decides whether to pull events from other existing components.
	 * 
	 * Can eventually integrate 'trends' where some components share a lot of events? Specs need to only have events found in plants, with high share-rate.
	 * 
	 * Given conditions, create the plants and specs
	 *  - States and transitions are independent of other components, but need intentional overlap of events
	 *  - Make Agents wrt the events previously generated
	 * 
	 * @param prefixNom
	 * @param model
	 * @param numPlants
	 * @param numSpecs
	 * @param stateSizeAverage
	 * @param stateVariance
	 * @param eventSizeAverage
	 * @param eventVariance
	 * @param eventShareRate
	 */
	

	public static void generateRandomSystemSet(String prefixNom, Manager model, int numPlants, int numSpecs, int stateSizeAverage, int stateVariance, int eventSizeAverage, int eventVariance, double eventShareRate) {
		
		
		
		
	}
	
}
