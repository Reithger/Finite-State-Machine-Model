package model.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * This class is used to generate files readable as Finite State Machines to the constructors
 * that accept file input from the classes in the fsm package. It does so randomly with no
 * oversight to handle strange productions.
 * 
 * The class creates a file in a location defined by a class constant and named howsoever the
 * user of this class wants; any usage of the class' production is then done by accessing that
 * location in your file system, given as a String returned by this method or by directly accessing
 * your file system yourself.
 * 
 * Important note: Must write to file the following:
 * # of special types
 * # of elements of special type 'n'
 * 	- the elements
 *  - repeat for all special types
 * All transitions (State, State, Event)
 * 
 * This class is a part of the support package.
 * 
 * @author Ada Clevinger and Graeme Zinck
 *
 */

public class GenerateFSM {

//--- Constant Values  ------------------------------------------------------------------------
	
	/** String constant referenced for consistent naming practices of States*/
	private static final String ALPHABET_STATE = "0123456789";
	/** String constants referenced for consistent naming practices of Events*/
	private static final String ALPHABET_EVENT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private final static int MAX_PERCENTAGE_VALUE = 100;
	
	private static String SEPARATOR;
	private static String REGION_SEPARATOR;
	private static String TRUE_SYMBOL;
	private static String FALSE_SYMBOL;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static ArrayList<String> stateAttributes;
	private static ArrayList<Integer> stateNumbers;
	private static ArrayList<String> eventAttributes;
	private static ArrayList<Integer> eventNumbers;
	private static ArrayList<String> transitionAttributes;
	private static ArrayList<Integer> transitionNumbers;
	
	private static ArrayList<String> defaultStateSet;
	private static ArrayList<String> defaultEventSet;
	
//---  Static Assignment   --------------------------------------------------------------------
	
	public static void assignStateAttributes(ArrayList<String> in, ArrayList<Integer> amounts) {
		stateAttributes = in;
		stateNumbers = amounts;
	}
	
	public static void assignEventAttributes(ArrayList<String> in, ArrayList<Integer> amounts) {
		eventAttributes = in;
		eventNumbers = amounts;
	}
	
	public static void assignTransitionAttributes(ArrayList<String> in, ArrayList<Integer> amounts) {
		transitionAttributes = in;
		transitionNumbers = amounts;
	}
	
	public static void assignDefaultStateSet(ArrayList<String> in) {
		defaultStateSet = in;
	}
	
	public static void wipeDefaultStateSet() {
		defaultStateSet = null;
	}
	
	public static void assignDefaultEventSet(ArrayList<String> in) {
		defaultEventSet = in;
	}
	
	public static void wipeDefaultEventSet() {
		defaultEventSet = null;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * The produced FSM object will be randomized according to the bounds described by the provided arguments.
	 * 
	 * Arguments for sizeStates and sizeEvents are ignored in the case that default sets have been assigned to the GenerateFSM class.
	 * 
	 * @param name
	 * @param sizeStates
	 * @param sizeEvents
	 * @param sizeTrans
	 * @param nonDet
	 * @return
	 * @throws Exception
	 */

	public static String createNewFSM(String name, int sizeStates, int sizeEvents, int sizeTrans, boolean det) throws Exception{
		if(stateAttributes == null || eventAttributes == null || transitionAttributes == null) {
			throw new Exception("Error: FSM Attribute Component Not Defined; Check State/Event/Transition Attribute Assignment");
		}
		StringBuilder out = new StringBuilder();
		out.append(name + "\n");
		out.append(REGION_SEPARATOR + "\n");
		writeAttribute(out, stateAttributes);
		writeAttribute(out, eventAttributes);
		writeAttribute(out, transitionAttributes);
		out.append(REGION_SEPARATOR + "\n");
		
		Random rand = new Random();

		HashMap<Integer, String> stateNames = defaultStateSet == null ? writeComponentGenerative(out, rand, sizeStates, stateAttributes, stateNumbers, true) : writeComponentDefaultSet(out, rand, defaultStateSet, stateAttributes, stateNumbers);

		HashMap<Integer, String> eventNames = defaultEventSet == null ? writeComponentGenerative(out, rand, sizeEvents, eventAttributes, eventNumbers, false) : writeComponentDefaultSet(out, rand, defaultEventSet, eventAttributes, eventNumbers);

		writeTransitions(out, rand, stateNames, eventNames, sizeTrans, det);
		return out.toString();
	}
	
	private static HashMap<Integer, String> writeComponentGenerative(StringBuilder out, Random rand, int sizeComponent, ArrayList<String> attributes, ArrayList<Integer> numbers, boolean stateNames) {
		ArrayList<Integer> track = new ArrayList<Integer>();
		for(Integer i : numbers) {
			track.add(0);
		}
		HashMap<Integer, String> nameMapping = new HashMap<Integer, String>();
		for(int i = 0; i < sizeComponent; i++) {
			String name = generateName(i, stateNames);
			nameMapping.put(i, name);
			String line = name + writeAttributes(getRandomValue(rand), sizeComponent, i, attributes, numbers, track);
			out.append(line + "\n");
		}
		out.append(REGION_SEPARATOR + "\n");
		return nameMapping;
	}
	
	private static HashMap<Integer, String> writeComponentDefaultSet(StringBuilder out, Random rand, ArrayList<String> components, ArrayList<String> attributes, ArrayList<Integer> numbers){
		ArrayList<Integer> track = new ArrayList<Integer>();
		for(Integer i : numbers) {
			track.add(0);
		}
		HashMap<Integer, String> nameMapping = new HashMap<Integer, String>();
		for(int i = 0; i < components.size(); i++) {
			String name = components.get(i);
			nameMapping.put(i, name);
			String line = name + writeAttributes(getRandomValue(rand), components.size(), i, attributes, numbers, track);
			out.append(line + "\n");
		}
		out.append(REGION_SEPARATOR + "\n");
		return nameMapping;
	}
	
	private static StringBuilder writeTransitions(StringBuilder out, Random rand, HashMap<Integer, String> stateNames, HashMap<Integer, String> eventNames, int sizeTrans, boolean isDet) {	
		ArrayList<Integer> track = new ArrayList<Integer>();
		for(Integer i : transitionNumbers) {
			track.add(0);
		}
		int sizeStates = stateNames.keySet().size();
		int sizeEvents = eventNames.keySet().size();
		ArrayList<Integer> numTransPerState = new ArrayList<Integer>();
		for(int i = 0; i < sizeStates; i++) {
			numTransPerState.add(rand.nextInt(sizeTrans) + 1);
		}
		for(int i = 0; i < sizeStates; i++) {
			int numTr = numTransPerState.get(i);
			HashSet<Integer> det = new HashSet<Integer>();
			for(int j = 0; j < numTr; j++) {
				int state1 = i;
				int state2 = rand.nextInt(sizeStates);
				int event = rand.nextInt(sizeEvents);
				int count = 0;
				while(isDet && det.contains(event) && det.size() < sizeEvents && count < (10 * sizeEvents)) {
					event = rand.nextInt(sizeEvents);
					count++;
				}
				if(isDet && det.contains(event)) {
					continue;
				}
				det.add(event);
				String line = stateNames.get(state1) + SEPARATOR + eventNames.get(event) + SEPARATOR + stateNames.get(state2);
				//TODO: Examine use of sizeTrans here for proportion of transition attributes
				line += writeAttributes(getRandomValue(rand), numTransPerState.size(), i, transitionAttributes, transitionNumbers, track);
				out.append(line + "\n");
			}
		}
		return out;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public static void assignConstants(String separator, String regionSeparator, String trueSymbol, String falseSymbol) {
		SEPARATOR = separator;
		REGION_SEPARATOR = regionSeparator;
		TRUE_SYMBOL = trueSymbol;
		FALSE_SYMBOL = falseSymbol;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private static String generateName(int i, boolean character) {
		String out = "";
		String language = character ? ALPHABET_STATE : ALPHABET_EVENT;
		do {
			int use = i % (language.length());
			out = language.charAt(use) + out;
			i /= language.length();
		}while(i > 0);
		
		return out;
	}
	
	private static int getRandomValue(Random rand) {
		return rand.nextInt(MAX_PERCENTAGE_VALUE);
	}
	
	private static String writeAttributes(int rand, int size, int index, ArrayList<String> attri, ArrayList<Integer> numbers, ArrayList<Integer> track) {
		String line = "";
		for(int j = 0; j < attri.size(); j++) {
			int prop = MAX_PERCENTAGE_VALUE * numbers.get(j) / size;
			boolean result = (track.get(j) < numbers.get(j) && (rand <= prop || size - index == numbers.get(j) - track.get(j)));
			line += SEPARATOR + (result ? TRUE_SYMBOL : FALSE_SYMBOL);
			track.set(j, track.get(j) + (result ? 1 : 0));
		}
		return line;
	}
	
	private static void writeAttribute(StringBuilder out, ArrayList<String> attri) {
		for(int i = 0; i < attri.size(); i++) {
			out.append(attri.get(i) + (i + 1 < attri.size() ? SEPARATOR : ""));
		}
		out.append(SEPARATOR + "\n");
	}

}
