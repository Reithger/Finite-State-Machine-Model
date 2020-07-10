package ui.page.optionpage;

public class AdjustFSM extends OptionPage{
	
//---  Constant Values   ----------------------------------------------------------------------
	
	private final static String HEADER = "Head";
	private final static String[] CATEGORIES = new String[] {"Generate FSM", "Edit FSM States", "Edit FSM Events", "Edit FSM Transitions"};
	private final static String[][] LABELS = new String[][] {
		{"Number of States", "Number of Events", "Number of Transitions", "Generate"},
		{"Add State", "Remove State", "Change State Name"},
		{"Add Event", "Remove Event", "Change Event Name"},
		{"Add Transition", "Remove Transition", "Change Transition Event"},
	};
	private final static String[][] TYPES = new String[][] {
		{ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_EMPTY},
		{ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_DOUBLE,},
		{ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_DOUBLE},
		{ENTRY_TEXT_TRIPLE, ENTRY_TEXT_TRIPLE, ENTRY_TEXT_QUARTET},
	};
	
	private final static int CODE_ACCESS_NUM_STATES = -5;
	private final static int CODE_ACCESS_NUM_EVENTS = -6;
	private final static int CODE_ACCESS_NUM_TRANS = -7;
	private final static int CODE_ADD_STATE = 8;
	private final static int CODE_REMOVE_STATE = 9;
	private final static int CODE_CHANGE_STATE_NAME = 10;
	private final static int CODE_ADD_EVENT = 11;
	private final static int CODE_REMOVE_EVENT = 12;
	private final static int CODE_CHANGE_EVENT = 13;
	private final static int CODE_ADD_TRANSITION = 14;
	private final static int CODE_REMOVE_TRANSITION = 15;
	private final static int CODE_CHANGE_TRANSITION_EVENT = 16;
	private final static int CODE_GENERATE_FSM = 100;
	
	/** Make sure codes are high values to give buffer for background behaviors*/
	private final static int[][] CODES = new int[][] {
		{CODE_ACCESS_NUM_STATES, CODE_ACCESS_NUM_EVENTS, CODE_ACCESS_NUM_TRANS, CODE_GENERATE_FSM},
		{CODE_ADD_STATE, CODE_REMOVE_STATE, CODE_CHANGE_STATE_NAME},
		{CODE_ADD_EVENT, CODE_REMOVE_EVENT, CODE_CHANGE_EVENT},
		{CODE_ADD_TRANSITION, CODE_REMOVE_TRANSITION, CODE_CHANGE_TRANSITION_EVENT},
	};

	
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
					break;
				default:
					break;
			}
			if(code == CODE_GENERATE_FSM) {
				int numState = Integer.parseInt(getTextFromCode(CODE_ACCESS_NUM_STATES)[0]);
				int numEvent = Integer.parseInt(getTextFromCode(CODE_ACCESS_NUM_EVENTS)[0]);
				int numTrans = Integer.parseInt(getTextFromCode(CODE_ACCESS_NUM_TRANS)[0]);
				System.out.println(numState + " " + numEvent + " " + numTrans);
			}
		}
		drawPage();
	}
	
	private String[] getTextFromCode(int code) {
		return getTextEntry(getCodeIndices(code)[0], getCodeIndices(code)[1]);
	}

}
