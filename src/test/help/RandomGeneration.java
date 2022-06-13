package test.help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import model.AttributeList;
import model.Manager;

public class RandomGeneration {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	private static final int TRANSITION_NUMBER_DEFAULT = 2;
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	
//---  Static Preparation   -------------------------------------------------------------------
	
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
	
	public static void setupRandomFSMDefaultEvents(Manager model, ArrayList<String> defaultEvents) {
		model.assignRandomFSMDefaultEventSet(defaultEvents);
	}
	
//---  Operations   ---------------------------------------------------------------------------

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
			model.removeFSM(nom);
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
	 *  - Specs should only use alphabet defined in plants; true random or share events with specific plant components?
	 * Make Agents wrt the events previously generated
	 * 
	 * TODO: Output results and image whenever we run a test with this, don't want to lose it
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
	 * @throws Exception 
	 */
	

	public static ArrayList<String> generateRandomSystemSet(String prefixNom, Manager model, int numPlants, int numSpecs, int stateSizeAverage, int stateVariance, int eventSizeAverage, int eventVariance, double eventShareRate) throws Exception {
		Random rand = new Random();
		HashMap<String, ArrayList<String>> plantEvents = new HashMap<String, ArrayList<String>>();
		
		setupRandomFSMConditions(model, 0, 0, 0);
		
		ArrayList<String> out = new ArrayList<String>();
		
		for(int i = 0; i < numPlants; i++) {
			int numStates = stateSizeAverage + (rand.nextInt(stateVariance * 2 + 1) - (stateVariance));
			int numEvents = eventSizeAverage + (rand.nextInt(eventVariance * 2 + 1) - (eventVariance));
			int numTransitions = TRANSITION_NUMBER_DEFAULT;
			
			int numBorrowed = 0;
			for(int j = 0; j < numEvents - 1; j++) {
				if(plantEvents.keySet().size() != 0 && rand.nextDouble() < eventShareRate)
					numBorrowed++;
			}
			
			ArrayList<String> events = getPlantEvents(numEvents - numBorrowed, ALPHABET.charAt(i)+"", configureName(prefixNom, i, true));
			plantEvents.put(configureName(prefixNom, i, true), copyArrayList(events));
			out.addAll(events);
			
			for(int j = 0; j < numBorrowed; j++) {
				int select = rand.nextInt(i);
				ArrayList<String> choices = plantEvents.get(configureName(prefixNom, select, true));
				String choice = choices.get(rand.nextInt(choices.size()));
				if(!events.contains(choice)) {
					events.add(choice);
				}
				else {
					j--;
				}
			}
			
			setupRandomFSMConditions(model, events.size(), 0, 0);
			setupRandomFSMDefaultEvents(model, events);
			generateRandomFSM(configureName(prefixNom, i, true), model, numStates, numEvents, numTransitions, true);
		}
		
		for(int i = 0; i < numSpecs; i++) {
			int numStates = stateSizeAverage + (rand.nextInt(stateVariance * 2 + 1) - (stateVariance));
			int numEvents = eventSizeAverage + (rand.nextInt(eventVariance * 2 + 1) - (eventVariance));
			int numTransitions = TRANSITION_NUMBER_DEFAULT;
			
			ArrayList<String> events = new ArrayList<String>();
			while(events.size() < numEvents) {
				ArrayList<String> pull = plantEvents.get(configureName(prefixNom, rand.nextInt(numPlants), true));
				String even = pull.get(rand.nextInt(pull.size()));
				if(!events.contains(even))
					events.add(even);
			}
			setupRandomFSMConditions(model, events.size(), 0, 0);
			setupRandomFSMDefaultEvents(model, events);

			generateRandomFSM(configureName(prefixNom, i, false), model, numStates, numEvents, numTransitions, true);
		}
		
		return out;
	}
	
	public static ArrayList<HashMap<String, ArrayList<Boolean>>> generateRandomAgents(ArrayList<String> events, int agentSizeAverage, int agentSizeVariance, double obsRate, double ctrRate){
		Random rand = new Random();
		int numAgents = agentSizeAverage + (agentSizeVariance == 0 ? 0 : (rand.nextInt(agentSizeVariance * 2 + 1) - (agentSizeVariance)));
		boolean[][][] agentInfo = new boolean[numAgents][events.size()][2];
		for(int i = 0; i < numAgents; i++) {
			for(int j = 0; j < events.size(); j++) {
				agentInfo[i][j][0] = rand.nextDouble() < obsRate;
				agentInfo[i][j][1] = rand.nextDouble() < ctrRate;
			}
		}
		String[] evens = new String[events.size()];
		for(int i = 0; i < evens.length; i++) {
			evens[i] = events.get(i);
		}
		return AgentChicanery.generateAgentSet(agentInfo, evens);
	}
	
//---  Getter Functions   ---------------------------------------------------------------------
	
	public static ArrayList<String> getComponentNames(String prefixNom, int numPlants, int numSpecs){
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(getPlantNames(prefixNom, numPlants));
		out.addAll(getSpecNames(prefixNom, numSpecs));
		return out;
	}
	
	public static ArrayList<String> getPlantNames(String prefixNom, int numPlants){
		ArrayList<String> out = new ArrayList<String>();
		for(int i = 0; i < numPlants; i++) {
			out.add(configureName(prefixNom, i, true));
		}
		return out;
	}
	
	public static ArrayList<String> getSpecNames(String prefixNom, int numSpecs){
		ArrayList<String> out = new ArrayList<String>();
		for(int i = 0; i < numSpecs; i++) {
			out.add(configureName(prefixNom, i, false));
		}
		return out;
	}
	
//---  Helper Functions   ---------------------------------------------------------------------
	
	private static ArrayList<String> copyArrayList(ArrayList<String> in){
		ArrayList<String> out = new ArrayList<String>();
		for(String s : in) {
			out.add(s);
		}
		return out;
	}
	
	private static ArrayList<String> getPlantEvents(int numEvents, String eventChar, String plantName){
		ArrayList<String> events = new ArrayList<String>();
		for(int j = 0; j < numEvents; j++) {
			events.add(eventChar + "_{" + j + "}");
		}
		return events;
	}
	
	private static String configureName(String prefix, int num, boolean plant) {
		return prefix + (plant ? "_p_" : "_s_") + num;
	}
	
}
