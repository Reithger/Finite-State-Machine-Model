package ui.page.optionpage;


import java.io.File;
import java.util.ArrayList;

import fsm.FSM;
import fsm.ModalSpecification;
import fsm.NonDetObsContFSM;
import ui.FSMUI;

/**
 * 
 * TODO: Test for opacity
 * 
 * @author Borinor
 *
 */

public class Operations extends OptionPage{
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static int CODE_TRIM = 100;
	private final static int CODE_ACCESSIBLE = 101;
	private final static int CODE_CO_ACCESSIBLE = 102;
	private final static int CODE_OBSERVER = 103;
	private final static int CODE_PRODUCT_SELECT = -104;
	private final static int CODE_PRODUCT = 104;
	private final static int CODE_PARALLEL_COMPOSITION_SELECT = -105;
	private final static int CODE_PARALLEL_COMPOSITION = 105;
	private final static int CODE_SUP_CNT_SBL_SELECT = -106;
	private final static int CODE_SUP_CNT_SBL = 106;
	private final static int CODE_UNDER_FSM = 107;
	private final static int CODE_OPT_OPQ_CONTROLLER = 108;
	private final static int CODE_OPT_SPVR_SELECT = -109;
	private final static int CODE_OPT_SPVR = 109;
	private final static int CODE_GRT_LWR_BND_SELECT = -110;
	private final static int CODE_GRT_LWR_BND = 110;
	private final static int CODE_PRUNE = 111;
	private final static int CODE_BLOCKING = 112;
	private final static int CODE_STATE_EXISTS = 113;
	
	private final static String ERROR_TEXT_ONE = "Error accessing some of the FSM objects, please check that their files exist in "
			+ "\"Finite State Machine Model\"/sources.";
	private final static String ERROR_TEXT_TWO = "Error during the process you attempted to run, please contact the programmer to fix it.";
	private final static String ERROR_MODAL_CASTING = "Error during Modal operation: Selected TransitionSystem was not able to be cast to a ModalSpecification"
			+ "object, double check the TransitionSystem object you selected.";
	
	//-- Scripts  ---------------------------------------------
	
	private final static String HEADER = "Operations";
	private final static String[] CATEGORIES = new String[] {"Transition Systems", "FSM", "Modal", "Queries"};
	private final static String[][] LABELS = new String[][] {
		{"Trim", "Make Accessible", "Make Co-Accessible"},
		{"Build Observer", "Product", "", "Parallel Composition", "", "Generate Supremal Controllable Sublanguage", ""},
		{"Get Underlying FSM", "Build Optimal Opaque Controller", "Make Optimal Supervisor", "", "Get Greatest Lower Bound", "", "Prune"},
		{"Is Blocking", "State Exists"},
	};
	private final static String[][] TYPES = new String[][] {
		{ENTRY_EMPTY, ENTRY_EMPTY, ENTRY_EMPTY},
		{ENTRY_EMPTY, ENTRY_SELECT_FSMS, ENTRY_EMPTY, ENTRY_SELECT_FSMS, ENTRY_EMPTY, ENTRY_SELECT_FSM, ENTRY_EMPTY},
		{ENTRY_EMPTY, ENTRY_EMPTY, ENTRY_SELECT_FSM, ENTRY_EMPTY, ENTRY_SELECT_FSM, ENTRY_EMPTY, ENTRY_EMPTY},
		{ENTRY_EMPTY, ENTRY_TEXT_SINGLE},
	};
	/** Make sure codes are high values to give buffer for background behaviors*/
	private final static int[][] CODES = new int[][] {
		{CODE_TRIM, CODE_ACCESSIBLE, CODE_CO_ACCESSIBLE},
		{CODE_OBSERVER, CODE_PRODUCT_SELECT, CODE_PRODUCT, CODE_PARALLEL_COMPOSITION_SELECT, CODE_PARALLEL_COMPOSITION, CODE_SUP_CNT_SBL_SELECT, CODE_SUP_CNT_SBL},
		{CODE_UNDER_FSM, CODE_OPT_OPQ_CONTROLLER, CODE_OPT_SPVR_SELECT, CODE_OPT_SPVR, CODE_GRT_LWR_BND_SELECT, CODE_GRT_LWR_BND, CODE_PRUNE},
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
			switch(code) {
				case CODE_TRIM:
					getFSMUI().allotTransitionSystem(getFSMUI().getActiveFSM().trim(), getFSMUI().getActiveFSM().getId() + "_trim");
					getFSMUI().refreshActiveImage();
					break;
				case CODE_ACCESSIBLE:
					getFSMUI().allotTransitionSystem(getFSMUI().getActiveFSM().makeAccessible(), getFSMUI().getActiveFSM().getId() + "_accessible");
					getFSMUI().refreshActiveImage();
					break;
				case CODE_CO_ACCESSIBLE:
					getFSMUI().allotTransitionSystem(getFSMUI().getActiveFSM().makeCoAccessible(), getFSMUI().getActiveFSM().getId() + "_coaccessible");
					getFSMUI().refreshActiveImage();
					break;
				case CODE_OBSERVER:
					FSM fsm = (FSM)(getFSMUI().getActiveFSM());
					getFSMUI().allotTransitionSystem(fsm.buildObserver(), getFSMUI().getActiveFSM().getId() + "_observer");
					getFSMUI().refreshActiveImage();
					break;
				case CODE_PRODUCT:
					try {
						FSM[] fsms = getFSMArray(CODE_PRODUCT_SELECT);
						try {
							getFSMUI().allotTransitionSystem(((FSM)getFSMUI().getActiveFSM()).product(fsms), getFSMUI().getActiveFSM().getId() + "_product");
						}
						catch(Exception e1) {
							e1.printStackTrace();
							getFSMUI().popupDisplayText(ERROR_TEXT_TWO);
						}
						getFSMUI().refreshActiveImage();
					}
					catch(Exception e) {
						e.printStackTrace();
						getFSMUI().popupDisplayText(ERROR_TEXT_ONE);
					}
					break;
				case CODE_PARALLEL_COMPOSITION:
					try {
						FSM[] fsms = getFSMArray(CODE_PARALLEL_COMPOSITION_SELECT);
						try {
							getFSMUI().allotTransitionSystem(((FSM)getFSMUI().getActiveFSM()).parallelComposition(fsms), getFSMUI().getActiveFSM().getId() + "_parallelcomp");
						}
						catch(Exception e1) {
							e1.printStackTrace();
							getFSMUI().popupDisplayText(ERROR_TEXT_TWO);
						}
						getFSMUI().refreshActiveImage();
					}
					catch(Exception e) {
						e.printStackTrace();
						getFSMUI().popupDisplayText(ERROR_TEXT_ONE);
					}
					break;
				case CODE_SUP_CNT_SBL:
					try {
						FSM nfsm = getFSM(CODE_PARALLEL_COMPOSITION_SELECT);
						try {
							getFSMUI().allotTransitionSystem(((FSM)getFSMUI().getActiveFSM()).getSupremalControllableSublanguage(nfsm), getFSMUI().getActiveFSM().getId() + "_supcntsublng");
						}
						catch(Exception e1) {
							e1.printStackTrace();
							getFSMUI().popupDisplayText(ERROR_TEXT_TWO);
						}
						getFSMUI().refreshActiveImage();
					}
					catch(Exception e) {
						e.printStackTrace();
						getFSMUI().popupDisplayText(ERROR_TEXT_ONE);
					}
					break;
				case CODE_UNDER_FSM: 
					try {
						ModalSpecification mod = (ModalSpecification)(getFSMUI().getActiveFSM());
						getFSMUI().allotTransitionSystem(mod.getUnderlyingFSM(), mod.getId() + "_underFSM");
						getFSMUI().refreshActiveImage();
					}
					catch(Exception e) {
						e.printStackTrace();
						getFSMUI().popupDisplayText(ERROR_MODAL_CASTING);
					}
					break;
				case CODE_OPT_OPQ_CONTROLLER:
					try {
						ModalSpecification mod = (ModalSpecification)(getFSMUI().getActiveFSM());
						getFSMUI().allotTransitionSystem(mod.buildOptimalOpaqueController(), mod.getId() + "_optopqcnt");
						getFSMUI().refreshActiveImage();
					}
					catch(Exception e) {
						e.printStackTrace();
						getFSMUI().popupDisplayText(ERROR_MODAL_CASTING);
					}
					break;
				case CODE_OPT_SPVR: 
					try {
						ModalSpecification mod = (ModalSpecification)(getFSMUI().getActiveFSM());
						FSM nfsm = getFSM(code);
						getFSMUI().allotTransitionSystem(mod.makeOptimalSupervisor(nfsm), mod.getId() + "_optspvr");
						getFSMUI().refreshActiveImage();
					}
					catch(Exception e) {
						e.printStackTrace();
						getFSMUI().popupDisplayText(ERROR_TEXT_ONE);
					}
					break;
				case CODE_GRT_LWR_BND: 
					try {
						ModalSpecification mod = (ModalSpecification)(getFSMUI().getActiveFSM());
						ModalSpecification nfsm = getModal(code);
						getFSMUI().allotTransitionSystem(mod.getGreatestLowerBound(nfsm), mod.getId() + "_grtlwrbnd");
						getFSMUI().refreshActiveImage();
					}
					catch(Exception e) {
						e.printStackTrace();
						getFSMUI().popupDisplayText(ERROR_TEXT_ONE);
					}
					break;
				case CODE_PRUNE: 
					try {
						ModalSpecification mod = (ModalSpecification)(getFSMUI().getActiveFSM());
						getFSMUI().allotTransitionSystem(mod.prune(), mod.getId() + "_prune");
						getFSMUI().refreshActiveImage();
					}
					catch(Exception e) {
						getFSMUI().popupDisplayText(ERROR_MODAL_CASTING);
					}
					break;
				case CODE_BLOCKING:
					getFSMUI().popupDisplayText(getFSMUI().getActiveFSM().isBlocking() ? "Is Blocking" : "Is Not Blocking");
					break;
				case CODE_STATE_EXISTS: 
					String state = this.getTextEntryFromCode(CODE_STATE_EXISTS, 0);
					getFSMUI().popupDisplayText(getFSMUI().getActiveFSM().stateExists(state) ? state + " exists" : state + " does not exist");
					break;
				default:
					break;
			}
		}
		drawPage();
	}
	
	private FSM[] getFSMArray(int code) throws Exception{
		String[] fsmPaths = getFSMsFromCode(code);
		FSM[] fsms = new FSM[fsmPaths.length];
		for(int i = 0; i < fsmPaths.length; i++) {
			fsms[i] = new NonDetObsContFSM(new File(FSMUI.ADDRESS_SOURCES + fsmPaths[i]), fsmPaths[i]);
		}
		return fsms;
	}
	
	private FSM getFSM(int code) throws Exception{
		String path = getTextFromCode(code, 0);
		return new NonDetObsContFSM(new File(FSMUI.ADDRESS_SOURCES + path), path);
	}
	
	private ModalSpecification getModal(int code) throws Exception{
		String path = getTextFromCode(code, 0);
		return new ModalSpecification(new File(FSMUI.ADDRESS_SOURCES + path), path);
	}
	
}
