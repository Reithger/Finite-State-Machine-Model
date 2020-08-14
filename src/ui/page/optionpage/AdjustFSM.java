package ui.page.optionpage;

import java.util.Random;

import fsm.NonDetObsContFSM;
import graphviz.FSMToDot;
import support.GenerateFSM;
import ui.FSMUI;

public class AdjustFSM extends OptionPage{
	
//---  Constant Values   ----------------------------------------------------------------------
	

	//-- Generate FSM  ----------------------------------------
	private final static int CODE_ACCESS_NUM_STATES = -1;
	private final static int CODE_ACCESS_NUM_EVENTS = -2;
	private final static int CODE_ACCESS_NUM_TRANS = -3;
	private final static int CODE_ACCESS_FSM_NAME = -4;
	private final static int CODE_ACCESS_NON_DETERMINISTIC = -5;
	private final static int CODE_GENERATE_FSM = 100;
	//-- Generate Complex FSM  --------------------------------
	private final static int CODE_ACCESS_COMPLEX_NUM_STATES = -6;
	private final static int CODE_ACCESS_COMPLEX_NUM_EVENTS = -7;
	private final static int CODE_ACCESS_COMPLEX_NUM_TRANS = -8;
	private final static int CODE_ACCESS_COMPLEX_INITIAL = -9;
	private final static int CODE_ACCESS_COMPLEX_MARKED = -10;
	private final static int CODE_ACCESS_COMPLEX_SECRET = -11;
	private final static int CODE_ACCESS_COMPLEX_CONTROLLED = -12;
	private final static int CODE_ACCESS_COMPLEX_UNOBSERVED = -13;
	private final static int CODE_ACCESS_COMPLEX_ATTACKER = -14;
	private final static int CODE_ACCESS_COMPLEX_NON_DETERMINISTIC = -15;
	private final static int CODE_ACCESS_COMPLEX_FSM_NAME = -16;
	private final static int CODE_GENERATE_COMPLEX_FSM = 101;
	//--
	private final static int CODE_ADD_STATE = 102;
	private final static int CODE_REMOVE_STATE = 103;
	private final static int CODE_INITIAL_STATE = 104;
	private final static int CODE_MARKED_STATE = 105;
	private final static int CODE_SECRET_STATE = 106;
	private final static int CODE_BAD_STATE = 107;
	//--
	private final static int CODE_ADD_TRANSITION = 108;
	private final static int CODE_REMOVE_TRANSITION = 109;
	//--
	private final static int CODE_SAVE_FSM = 110;
	private final static int CODE_SAVE_IMG = 111;
	private final static int CODE_LOAD_SOURCE = 112;
	private final static int CODE_DELETE_SOURCE = 113;
	private final static int CODE_RENAME_FSM = 114;
	private final static int CODE_DUPLICATE_FSM = 115;
	private final static int CODE_CLOSE_FSM = 116;
	
	//-- Scripts  ---------------------------------------------
	
	private final static String HEADER = "Adjust FSM";
	private final static String[] CATEGORIES = new String[] {"Generate FSM Simple", "Generate FSM Complicated", "Edit FSM States", "Edit FSM Transitions", "Admin"};
	private final static String[][] LABELS = new String[][] {
		{"Number of States", "Number of Events", "Number of Transitions", "Non-Deterministic", "Name", "Generate"},
		{"Number of States", "Number of Events", "Number of Transitions", "Number of Initial States", "Number of Marked States",
			"Number of Secret States", "Number of Controlled Events", "Number of Unobservable Events", "Number of Attacker Invisible Events",
			"Non-Deterministic", "Name", "Generate"},
		{"Add State", "Remove State", "Add Initial State", "Set State Marked", "Set State Secret", "Set State Bad"},
		{"Add Transition", "Remove Transition"},	//TODO: Option to disable delete in case of misclicking
		{"Save Source", "Save Image", "Load Source", "Rename FSM", "Generate Copy", "Close FSM", "Delete Source"},
	};	//# marked states, # start states, # end states, # unobserv events, # attacker aware events, # controlled events, det/non-det, name
	private final static String[][] TYPES = new String[][] {
		{ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_CHECKBOX, ENTRY_TEXT_LONG, ENTRY_EMPTY},
		{ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE,
			ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_CHECKBOX, ENTRY_TEXT_LONG, ENTRY_EMPTY},
		{ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE},
		{ENTRY_TEXT_TRIPLE, ENTRY_TEXT_TRIPLE},
		{ENTRY_EMPTY, ENTRY_EMPTY, ENTRY_TEXT_LONG, ENTRY_TEXT_LONG, ENTRY_TEXT_LONG, ENTRY_EMPTY, ENTRY_EMPTY}
	};
	/** Make sure codes are high values to give buffer for background behaviors*/
	private final static int[][] CODES = new int[][] {
		{CODE_ACCESS_NUM_STATES, CODE_ACCESS_NUM_EVENTS, CODE_ACCESS_NUM_TRANS, CODE_ACCESS_NON_DETERMINISTIC, CODE_ACCESS_FSM_NAME,  CODE_GENERATE_FSM},
		{CODE_ACCESS_COMPLEX_NUM_STATES, CODE_ACCESS_COMPLEX_NUM_EVENTS, CODE_ACCESS_COMPLEX_NUM_TRANS, CODE_ACCESS_COMPLEX_INITIAL, CODE_ACCESS_COMPLEX_MARKED, 
			CODE_ACCESS_COMPLEX_SECRET, CODE_ACCESS_COMPLEX_CONTROLLED, CODE_ACCESS_COMPLEX_UNOBSERVED, CODE_ACCESS_COMPLEX_ATTACKER, CODE_ACCESS_COMPLEX_NON_DETERMINISTIC,
			CODE_ACCESS_COMPLEX_FSM_NAME, CODE_GENERATE_COMPLEX_FSM},
		{CODE_ADD_STATE, CODE_REMOVE_STATE, CODE_INITIAL_STATE, CODE_MARKED_STATE, CODE_SECRET_STATE, CODE_BAD_STATE},
		{CODE_ADD_TRANSITION, CODE_REMOVE_TRANSITION},
		{CODE_SAVE_FSM, CODE_SAVE_IMG, CODE_LOAD_SOURCE, CODE_RENAME_FSM, CODE_DUPLICATE_FSM, CODE_CLOSE_FSM, CODE_DELETE_SOURCE},
	};
	private final static String HELP = 
			"Some line\n"
			+ "Another line\n"
			+ "This will be the help page";
	private final static String LANGUAGE = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private final static int RANDOM_NAME_LENGTH = 8;

	
//---  Constructors   -------------------------------------------------------------------------

	public AdjustFSM(int x, int y, int width, int height) {
		super(HEADER, CATEGORIES, LABELS, TYPES, CODES, HELP);
	}
	
//---  Operations   ---------------------------------------------------------------------------

	@Override
	public void applyCode(int code) {
		if(!toggleCategory(code)) {
			switch(code) {
				case CODE_SAVE_FSM:
					getFSMUI().getActiveFSM().toTextFile(FSMUI.ADDRESS_SOURCES, getFSMUI().getActiveFSM().getId());
					break;
				case CODE_SAVE_IMG:
					FSMToDot.createImgFromFSM(getFSMUI().getActiveFSM(), FSMUI.ADDRESS_IMAGES + getFSMUI().getActiveFSM().getId(), FSMUI.ADDRESS_IMAGES, FSMUI.ADDRESS_CONFIG);
					break;
				case CODE_DUPLICATE_FSM:
					String tex = this.getTextEntryFromCode(CODE_DUPLICATE_FSM, 0); 
					tex = tex.equals("") || tex == null ? getFSMUI().getActiveFSM().getId() + "(copy)" : tex;
					getFSMUI().allotTransitionSystem(getFSMUI().getActiveFSM().copy(), tex);
					break;
				case CODE_RENAME_FSM:
					getFSMUI().renameActiveFSM(getTextEntryFromCode(CODE_RENAME_FSM, 0));
					break;
				case CODE_LOAD_SOURCE:
					getFSMUI().allotTransitionSystem(FSMUI.ADDRESS_SOURCES + getTextEntryFromCode(CODE_LOAD_SOURCE, 0), getTextEntryFromCode(CODE_LOAD_SOURCE, 0).replaceAll(".fsm", ""));
					getFSMUI().refreshActiveImage();	//TODO: Add actual file selecting thing, relying on exact names is bad form
					resetCodeEntries(code);
					break;
				case CODE_DELETE_SOURCE:
					getFSMUI().deleteActiveFSM();
					break;
				case CODE_ADD_STATE: 
					getFSMUI().getActiveFSM().addState(this.getTextEntryFromCode(CODE_ADD_STATE, 0));
					getFSMUI().refreshActiveImage();
					break;
				case CODE_REMOVE_STATE: 
					getFSMUI().getActiveFSM().removeState(this.getTextEntryFromCode(CODE_REMOVE_STATE, 0));
					getFSMUI().refreshActiveImage();
					break;
				case CODE_ADD_TRANSITION: 
					getFSMUI().getActiveFSM().addTransition(this.getTextEntryFromCode(CODE_ADD_TRANSITION, 0), this.getTextEntryFromCode(CODE_ADD_TRANSITION, 1), this.getTextEntryFromCode(CODE_ADD_TRANSITION, 2));
					getFSMUI().refreshActiveImage();
					resetCodeEntries(code);	//TODO: Let user decide whether or not to clear entries
					break;
				case CODE_REMOVE_TRANSITION: 
					getFSMUI().getActiveFSM().removeTransition(this.getTextEntryFromCode(CODE_REMOVE_TRANSITION, 0), this.getTextEntryFromCode(CODE_REMOVE_TRANSITION, 1), this.getTextEntryFromCode(CODE_REMOVE_TRANSITION, 2));
					getFSMUI().refreshActiveImage();
					resetCodeEntries(code);
					break;
				case CODE_SECRET_STATE:
					getFSMUI().getActiveFSM().toggleSecretState(this.getTextEntryFromCode(CODE_SECRET_STATE, 0));
					getFSMUI().refreshActiveImage();
					break;
				case CODE_INITIAL_STATE:
					getFSMUI().getActiveFSM().addInitialState(this.getTextEntryFromCode(CODE_INITIAL_STATE, 0));
					getFSMUI().refreshActiveImage();
					break;
				case CODE_MARKED_STATE:
					getFSMUI().getActiveFSM().toggleMarkedState(this.getTextEntryFromCode(CODE_MARKED_STATE, 0));
					getFSMUI().refreshActiveImage();
					break;
				case CODE_BAD_STATE:
					getFSMUI().getActiveFSM().toggleBadState(this.getTextEntryFromCode(CODE_BAD_STATE, 0));
					getFSMUI().refreshActiveImage();
					break;
				case CODE_CLOSE_FSM:
					getFSMUI().removeActiveTransitionSystem();
					getFSMUI().refreshActiveImage();
					break;
				case CODE_GENERATE_FSM: 
					int numState = getIntegerFromCode(CODE_ACCESS_NUM_STATES, 0);
					int numEvent = getIntegerFromCode(CODE_ACCESS_NUM_EVENTS, 0);
					int numTrans = getIntegerFromCode(CODE_ACCESS_NUM_TRANS, 0);
					boolean nonDet = getCheckboxContentsFromCode(CODE_ACCESS_NON_DETERMINISTIC);
					String name = getTextEntryFromCode(CODE_ACCESS_FSM_NAME, 0);
					if(name == null) {
						name = "";
						Random rand = new Random();
						for(int i = 0; i < RANDOM_NAME_LENGTH; i++) {
							name += LANGUAGE.charAt(rand.nextInt(LANGUAGE.length()));
						}
					}
					String path = GenerateFSM.createNewFSM(numState, 0, numEvent, numTrans, 1, 0, 0, 0, 0, nonDet, name, FSMUI.ADDRESS_SOURCES);
					getFSMUI().allotTransitionSystem(path, name);
					break;
				case CODE_GENERATE_COMPLEX_FSM:
					int numStateC = getIntegerFromCode(CODE_ACCESS_COMPLEX_NUM_STATES, 0);
					int numEventC = getIntegerFromCode(CODE_ACCESS_COMPLEX_NUM_EVENTS, 0);
					int numTransC = getIntegerFromCode(CODE_ACCESS_COMPLEX_NUM_TRANS, 0);
					int numInitC = getIntegerFromCode(CODE_ACCESS_COMPLEX_INITIAL, 0);
					int numMarkedC = getIntegerFromCode(CODE_ACCESS_COMPLEX_MARKED, 0);
					int numSecretC = getIntegerFromCode(CODE_ACCESS_COMPLEX_SECRET, 0);
					int numControlledC = getIntegerFromCode(CODE_ACCESS_COMPLEX_CONTROLLED, 0);
					int numUnobservedC = getIntegerFromCode(CODE_ACCESS_COMPLEX_UNOBSERVED, 0);
					int numAttackerC = getIntegerFromCode(CODE_ACCESS_COMPLEX_ATTACKER, 0);
					boolean nonDetC = getCheckboxContentsFromCode(CODE_ACCESS_NON_DETERMINISTIC);
					String nameC = getTextEntryFromCode(CODE_ACCESS_COMPLEX_FSM_NAME, 0);
					if(nameC == null) {
						name = "";
						Random rand = new Random();
						for(int i = 0; i < RANDOM_NAME_LENGTH; i++) {
							name += LANGUAGE.charAt(rand.nextInt(LANGUAGE.length()));
						}
					}
					String pathC = GenerateFSM.createNewFSM(numStateC, numMarkedC, numEventC, numTransC, numInitC, numSecretC, numUnobservedC, numAttackerC, numControlledC, nonDetC, nameC, FSMUI.ADDRESS_SOURCES);
					getFSMUI().allotTransitionSystem(pathC, nameC);
					break;
				default:
					break;
			}
		}
		drawPage();
	}
	
}
