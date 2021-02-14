package model.convert;

import java.util.ArrayList;
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

//---  Operations   ---------------------------------------------------------------------------

	/**
	 * This method generates a file corresponding to the nomenclature of an FSM's file input via the
	 * properties described by its parameters: Number of States, Marked States, Unique Events, Paths,
	 * Initial States, Private States, Unobservable Events, and Uncontrollable Events.
	 * 
	 * The produced FSM object will be randomized according to the bounds described by the provided arguments,
	 * saving the produced file to the location described by the input and returning the exact address of this
	 * new file as a String object.
	 * 
	 * File type: .fsm
	 * 
	 * @param sizeStates - int value describing how many States to include in the FSM.
	 * @param sizeMarked - int value describing how many Marked States to include in the FSM.
	 * @param sizeEvents - int value describing how many Unique Events to include in the FSM.
	 * @param sizePaths - int value describing the maximal number of Paths leading out from a given State in the FSM.
	 * @param sizeInitial - int value describing how many Initial States to include in the FSM.
	 * @param sizePrivate - int value describing how many Private States to include in the FSM.
	 * @param sizeUnobserv - int value describing how many Events to mark as Unobservable to the System in the FSM. 
	 * @param sizeAtacker - int value describing how many Events to mark as Unobservable to the Attacker in the FSM on top of those to the System.
	 * @param sizeControl - int value describing how many Events to mark as Uncontrollable in the FSM.
	 * @param nonDet - boolean value denoting whether or not the FSM is Deterministic or Non-Deterministic.
	 * @param name - String object used to denote the title of the File being generated.
	 * @param filePath - String object specifying where in the file system to place the file.
	 * @return - Returns a String describing the location of the generated File in the file system.
	 */

	public static String createNewFSM(String name, int sizeStates, int sizeEvents, int sizeTrans, boolean nonDet, ArrayList<String> stateAttri, ArrayList<String> eventAttri, ArrayList<String> transAttri, ArrayList<Integer> numbers) {
		StringBuilder out = new StringBuilder();
		out.append(name + "\n");
		out.append(REGION_SEPARATOR + "\n");
		writeAttribute(out, stateAttri);
		writeAttribute(out, eventAttri);
		writeAttribute(out, transAttri);
		out.append(REGION_SEPARATOR + "\n");
		
		ArrayList<Integer> track = new ArrayList<Integer>();
		for(int i : numbers) {
			track.add(0);
		}
		
		Random rand = new Random();
		
		for(int i = 0; i < sizeStates; i++) {
			String line = generateName(i, false);
			line += writeAttributes(getRandomValue(rand), sizeStates, i, stateAttri, 0, numbers, track);
			out.append(line + "\n");
		}
		out.append(REGION_SEPARATOR + "\n");
		for(int i = 0; i < sizeStates; i++) {
			String line = generateName(i, true);
			line += writeAttributes(getRandomValue(rand), sizeEvents, i, eventAttri, stateAttri.size(), numbers, track);
			out.append(line + "\n");
		}
		out.append(REGION_SEPARATOR + "\n");
		for(int i = 0; i < sizeStates; i++) {
			int numTr = rand.nextInt(sizeTrans) + 1;
			HashSet<Integer> det = new HashSet<Integer>();
			for(int j = 0; j < numTr; j++) {
				int state1 = i;
				int state2 = rand.nextInt(sizeStates);
				int event = rand.nextInt(sizeEvents);
				while(nonDet && det.contains(event) && det.size() < sizeEvents) {
					event = rand.nextInt(sizeEvents);
				}
				if(nonDet && det.contains(event)) {
					continue;
				}
				det.add(event);
				String line = generateName(state1, false) + SEPARATOR + generateName(event, true) + SEPARATOR + generateName(state2, false);
				line += writeAttributes(getRandomValue(rand), sizeTrans, i, transAttri, stateAttri.size() + eventAttri.size(), numbers, track);
				out.append(line + "\n");
			}
		}
		
		return out.toString();
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
		String language = character ? ALPHABET_EVENT : ALPHABET_STATE;
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
	
	private static String writeAttributes(int rand, int size, int index, ArrayList<String> attri, int offset, ArrayList<Integer> numbers, ArrayList<Integer> track) {
		String line = "";
		for(int j = 0; j < attri.size(); j++) {
			int prop = MAX_PERCENTAGE_VALUE * numbers.get(j + offset) / size;
			boolean result = (track.get(offset + j) < numbers.get(j + offset) && (rand <= prop || size - index == numbers.get(j + offset) - track.get(j + offset)));
			line += SEPARATOR + (result ? TRUE_SYMBOL : FALSE_SYMBOL);
			track.set(j + offset, track.get(j + offset) + (result ? 1 : 0));
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
