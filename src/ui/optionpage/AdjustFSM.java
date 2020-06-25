package ui.optionpage;

import java.util.Arrays;

public class AdjustFSM extends OptionPage{
	
//---  Constant Values   ----------------------------------------------------------------------
	
	private final static String HEADER = "Head";
	private final static String[] CATEGORIES = new String[] {"Generate FSM", "Edit FSM"};
	private final static String[][] LABELS = new String[][] {
		{"Number of States", "Number of Events", "Number of Transitions", "Generate"},
		{"Option_1b", "Option 2b", "Option 3b"},
	};
	private final static String[][] TYPES = new String[][] {
		{ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_TEXT_SINGLE, ENTRY_EMPTY},
		{ENTRY_TEXT_TRIPLE, ENTRY_TEXT_DOUBLE, ENTRY_TEXT_QUARTET,},
	};
	private final static int CODE_ACCESS_NUM_STATES = -5;
	private final static int CODE_ACCESS_NUM_EVENTS = -6;
	private final static int CODE_ACCESS_NUM_TRANS = -7;
	private final static int CODE_GENERATE_FSM = 100;
	/** Make sure codes are high values to give buffer for background behaviors*/
	private final static int[][] CODES = new int[][] {
		{CODE_ACCESS_NUM_STATES, CODE_ACCESS_NUM_EVENTS, CODE_ACCESS_NUM_TRANS, CODE_GENERATE_FSM},
		{103, 104, 105},
	};
	
//---  Constructors   -------------------------------------------------------------------------

	public AdjustFSM(int x, int y, int width, int height) {
		super(HEADER, CATEGORIES, LABELS, TYPES, CODES);
	}
	
//---  Operations   ---------------------------------------------------------------------------

	@Override
	public void applyCode(int code) {
		if(!toggleCategory(code)) {
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
