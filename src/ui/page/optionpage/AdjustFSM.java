package ui.page.optionpage;

import java.util.Random;

import support.GenerateFSM;
import ui.FSMUI;

public class AdjustFSM extends OptionPage{
	
//---  Constant Values   ----------------------------------------------------------------------
	

	//-- Generate FSM  ----------------------------------------
	private final static int CODE_ACCESS_NUM_STATES = -5;
	private final static int CODE_ACCESS_NUM_EVENTS = -6;
	private final static int CODE_ACCESS_NUM_TRANS = -7;;
	private final static int CODE_ACCESS_NON_DETERMINISTIC = -11;
	private final static int CODE_GENERATE_FSM = 100;
	//-- Generate Complex FSM  --------------------------------
	private final static int CODE_ACCESS_COMPLEX_NUM_STATES = -8;
	private final static int CODE_ACCESS_COMPLEX_NUM_EVENTS = -9;
	private final static int CODE_ACCESS_COMPLEX_NUM_TRANS = -10;
	private final static int CODE_ACCESS_COMPLEX_NON_DETERMINISTIC = -12;
	private final static int CODE_GENERATE_COMPLEX_FSM = 101;
	//--
	private final static int CODE_ADD_STATE = 8;
	private final static int CODE_REMOVE_STATE = 9;
	private final static int CODE_CHANGE_STATE_NAME = 10;
	//--
	private final static int CODE_ADD_EVENT = 11;
	private final static int CODE_REMOVE_EVENT = 12;
	private final static int CODE_CHANGE_EVENT = 13;
	//TODO: More complex editing
	//--
	private final static int CODE_ADD_TRANSITION = 14;
	private final static int CODE_REMOVE_TRANSITION = 15;
	private final static int CODE_CHANGE_TRANSITION_EVENT = 16;
	
	//-- Scripts  ---------------------------------------------
	
	private final static String HEADER = "Head";
	private final static String[] CATEGORIES = new String[] {"Generate FSM Simple", "Generate FSM Complicated", "Edit FSM States", "Edit FSM Events", "Edit FSM Transitions"};
	private final static String[][] LABELS = new String[][] {
		{"Number of States", "Number of Events", "Number of Transitions", "Non-Deterministic", "Generate"},
		{"Number of States", "Number of Events", "Number of Transitions", "Non-Deterministic", "Generate"},
		{"Add State", "Remove State", "Change State Name"},
		{"Add Event", "Remove Event", "Change Event Name"},
		{"Add Transition", "Remove Transition", "Change Transition Event"},
	};	//# marked states, # start states, # end states, # unobserv events, # attacker aware events, # controlled events, det/non-det, name
	private final static String[][] TYPES = new String[][] {
		{ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_CHECKBOX, ENTRY_EMPTY},
		{ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_CHECKBOX, ENTRY_EMPTY},
		{ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_DOUBLE,},
		{ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_DOUBLE},
		{ENTRY_TEXT_TRIPLE, ENTRY_TEXT_TRIPLE, ENTRY_TEXT_QUARTET},
	};
	/** Make sure codes are high values to give buffer for background behaviors*/
	private final static int[][] CODES = new int[][] {
		{CODE_ACCESS_NUM_STATES, CODE_ACCESS_NUM_EVENTS, CODE_ACCESS_NUM_TRANS, CODE_ACCESS_NON_DETERMINISTIC, CODE_GENERATE_FSM},
		{CODE_ACCESS_COMPLEX_NUM_STATES, CODE_ACCESS_COMPLEX_NUM_EVENTS, CODE_ACCESS_COMPLEX_NUM_TRANS, CODE_ACCESS_COMPLEX_NON_DETERMINISTIC, CODE_GENERATE_COMPLEX_FSM},
		{CODE_ADD_STATE, CODE_REMOVE_STATE, CODE_CHANGE_STATE_NAME},
		{CODE_ADD_EVENT, CODE_REMOVE_EVENT, CODE_CHANGE_EVENT},
		{CODE_ADD_TRANSITION, CODE_REMOVE_TRANSITION, CODE_CHANGE_TRANSITION_EVENT},
	};
	
	private final static String LANGUAGE = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private final static int RANDOM_NAME_LENGTH = 8;

	
//---  Constructors   -------------------------------------------------------------------------

	public AdjustFSM(int x, int y, int width, int height) {
		super(HEADER, CATEGORIES, LABELS, TYPES, CODES);
	}
	
//---  Operations   ---------------------------------------------------------------------------

	@Override
	public void applyCode(int code) {
		if(!toggleCategory(code)) {
			switch(code) {
				case CODE_ADD_STATE: 
					break;
				case CODE_REMOVE_STATE: 
					break;
				case CODE_CHANGE_STATE_NAME: 
					break;
				case CODE_ADD_EVENT: 
					break;
				case CODE_REMOVE_EVENT: 
					break;
				case CODE_CHANGE_EVENT: 
					break;
				case CODE_ADD_TRANSITION: 
					break;
				case CODE_REMOVE_TRANSITION: 
					break;
				case CODE_CHANGE_TRANSITION_EVENT: 
					break;
				case CODE_GENERATE_FSM: 
					int numState = getIntegerFromCode(CODE_ACCESS_NUM_STATES, 0);
					int numEvent = getIntegerFromCode(CODE_ACCESS_NUM_EVENTS, 0);
					int numTrans = getIntegerFromCode(CODE_ACCESS_NUM_TRANS, 0);
					boolean nonDet = getCheckboxContents(CODE_ACCESS_NON_DETERMINISTIC);
					String name = "";
					Random rand = new Random();
					for(int i = 0; i < RANDOM_NAME_LENGTH; i++) {
						name += LANGUAGE.charAt(rand.nextInt(LANGUAGE.length()));
					}
					String path = GenerateFSM.createNewFSM(numState, 0, numEvent, numTrans, 1, 0, 0, 0, 0, nonDet, name, FSMUI.ADDRESS_SOURCES);
					getFSMUI().allotTransitionSystem(path, name);
					break;
				default:
					break;
			}
		}
		drawPage();
	}
	


}
