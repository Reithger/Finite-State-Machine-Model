package ui.page.optionpage;

public class Operations extends OptionPage{
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static int CODE_TRIM = 100;
	private final static int CODE_ACCESSIBLE = 101;
	private final static int CODE_CO_ACCESSIBLE = 102;
	private final static int CODE_OBSERVER = 103;
	private final static int CODE_PRODUCT = 104;
	private final static int CODE_PARALLEL_COMPOSITION = 105;
	private final static int CODE_SUP_CNT_SBL = 106;
	private final static int CODE_UNDER_FSM = 107;
	private final static int CODE_OPT_OPQ_CONTROLLER = 108;
	private final static int CODE_OPT_SPVR = 109;
	private final static int CODE_GRT_LWR_BND = 110;
	private final static int CODE_PSD_LWR_BND = 111;
	private final static int CODE_PRUNE = 112;
	private final static int CODE_BLOCKING = 113;
	private final static int CODE_STATE_EXISTS = 114;
	
	//-- Scripts  ---------------------------------------------
	
	private final static String HEADER = "Operations";
	private final static String[] CATEGORIES = new String[] {"Transition Systems", "FSM", "Modal", "Queries"};
	private final static String[][] LABELS = new String[][] {
		{"Trim", "Make Accessible", "Make Co-Accessible"},
		{"Build Observer", "Product", "Parallel Composition", "Generate Supremal Controllable Sublanguage"},
		{"Get Underlying FSM", "Build Optimal Opaque Controller", "Get Optimal Supervisor", "Get Greatest Lower Bound",
			"Get Pseudo Lower Bound", "Prune"},
		{"Is Blocking", "State Exists"},
	};
	private final static String[][] TYPES = new String[][] {
		{ENTRY_EMPTY, ENTRY_EMPTY, ENTRY_EMPTY},
		{ENTRY_EMPTY, ENTRY_SELECT_FSMS, ENTRY_SELECT_FSMS, ENTRY_SELECT_FSM},
		{ENTRY_EMPTY, ENTRY_EMPTY, ENTRY_SELECT_FSM, ENTRY_SELECT_FSM, ENTRY_SELECT_FSM, ENTRY_EMPTY},
		{ENTRY_EMPTY, ENTRY_EMPTY},
	};
	/** Make sure codes are high values to give buffer for background behaviors*/
	private final static int[][] CODES = new int[][] {
		{CODE_TRIM, CODE_ACCESSIBLE, CODE_CO_ACCESSIBLE},
		{CODE_OBSERVER, CODE_PRODUCT, CODE_PARALLEL_COMPOSITION, CODE_SUP_CNT_SBL},
		{CODE_UNDER_FSM, CODE_OPT_OPQ_CONTROLLER, CODE_OPT_SPVR, CODE_GRT_LWR_BND, CODE_PSD_LWR_BND, CODE_PRUNE},
		{CODE_BLOCKING, CODE_STATE_EXISTS},
	};
	private final static String HELP = 
			"Some words\n"
			+ "And more\n" 
			+ "And more\n";
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Operations(int x, int y, int wid, int hei) {
		super(HEADER, CATEGORIES, LABELS, TYPES, CODES, HELP);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyCode(int code) {
		if(!toggleCategory(code)) {
			
		}
		drawPage();
	}

}
