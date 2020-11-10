package ui.page.optionpage.implementation;

import java.io.File;

import fsm.FSM;
import fsm.ModalSpecification;
import ui.FSMUI;
import ui.page.optionpage.OptionPage;
import ui.page.optionpage.implementation.popup.MultiFSMSelection;
import visual.composite.popout.PopoutAlert;

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
	private final static int CODE_PRODUCT_SELECT = 104;
	private final static int CODE_PRODUCT = 105;
	private final static int CODE_PARALLEL_COMPOSITION_SELECT = 106;
	private final static int CODE_PARALLEL_COMPOSITION = 107;
	private final static int CODE_SUP_CNT_SBL_SELECT = 108;
	private final static int CODE_SUP_CNT_SBL = 109;
	private final static int CODE_UNDER_FSM = 110;
	private final static int CODE_OPT_OPQ_CONTROLLER =111;
	private final static int CODE_OPT_SPVR_SELECT = 112;
	private final static int CODE_OPT_SPVR = 113;
	private final static int CODE_GRT_LWR_BND_SELECT = 114;
	private final static int CODE_GRT_LWR_BND = 115;
	private final static int CODE_PRUNE = 116;
	private final static int CODE_BLOCKING = 117;
	private final static int CODE_STATE_EXISTS = 118;
	
	private final static String ERROR_TEXT_ONE = "Error accessing some of the FSM objects, please check that their files exist in "
			+ "\"Finite State Machine Model\"/sources.";
	private final static String ERROR_TEXT_TWO = "Error during the process you attempted to run, please contact the programmer to fix it.";
	private final static String ERROR_MODAL_CASTING = "Error during Modal operation: Selected TransitionSystem was not able to be cast to a ModalSpecification"
			+ "object, double check the TransitionSystem object you selected.";
	
	//-- Scripts  ---------------------------------------------
	
	private final static String HEADER = "Operations";
	private final static String[] CATEGORIES = new String[] {"Transition Systems", "FSM", "Modal - WIP", "Queries"};
	private final static Object[][][] DATA = new Object[][][] {
		{
			{"Trim", OptionPage.ENTRY_EMPTY, CODE_TRIM, true},
			{"Make Accessible", OptionPage.ENTRY_EMPTY, CODE_ACCESSIBLE, true},
			{"Make Co-Accessible", OptionPage.ENTRY_EMPTY, CODE_CO_ACCESSIBLE, true},
		},
		{
			{"Build Observer", OptionPage.ENTRY_EMPTY, CODE_OBSERVER, true},
			{"Product", OptionPage.ENTRY_BUTTON_LIST, CODE_PRODUCT_SELECT, true},	//H, respond to input to make MultiFSMSelection
			{"", OptionPage.ENTRY_EMPTY, CODE_PRODUCT, true},
			{"Parallel Composition", OptionPage.ENTRY_BUTTON_LIST, CODE_PARALLEL_COMPOSITION_SELECT, true},	//H
			{"", OptionPage.ENTRY_EMPTY, CODE_PARALLEL_COMPOSITION, true},
			{"Generate Supremal Controllable Sublanguage", OptionPage.ENTRY_BUTTON_LIST, CODE_SUP_CNT_SBL_SELECT, true},	//H
			{"", OptionPage.ENTRY_EMPTY, CODE_SUP_CNT_SBL, true},
		},
		{
			{"Get Underlying FSM", OptionPage.ENTRY_EMPTY, CODE_UNDER_FSM, true},
			{"Build Optimal Opaque Controller", OptionPage.ENTRY_EMPTY, CODE_OPT_OPQ_CONTROLLER, true},
			{"Make Optimal Supervisor", OptionPage.ENTRY_BUTTON_LIST, CODE_OPT_SPVR_SELECT, true},	//H
			{"", OptionPage.ENTRY_EMPTY, CODE_OPT_SPVR, true},
			{"Get Greatest Lower Bound", OptionPage.ENTRY_BUTTON_LIST, CODE_GRT_LWR_BND_SELECT, true},	//H
			{"", OptionPage.ENTRY_EMPTY, CODE_GRT_LWR_BND, true},
			{"Prune", OptionPage.ENTRY_EMPTY, CODE_PRUNE, true},
		},
		{
			{"Is Blocking", OptionPage.ENTRY_EMPTY, CODE_BLOCKING, true},
			{"State Exists", OptionPage.ENTRY_TEXT_SINGLE, CODE_STATE_EXISTS, true},
		},

	};
	private final static String HELP = 
			"Some words\n"
			+ "And more\n" 
			+ "And more\n";
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Operations(int x, int y, int wid, int hei) {
		super(HEADER, HELP, CATEGORIES, DATA);
	}

	@Override
	public void applyCode(int code) {
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
			case CODE_PRODUCT_SELECT:
				new MultiFSMSelection(OptionPage.getFSMUI().getFSMList(), getEntrySetFromCode(code));
				break;
			case CODE_PARALLEL_COMPOSITION_SELECT:
				new MultiFSMSelection(OptionPage.getFSMUI().getFSMList(), getEntrySetFromCode(code));
				break;
			case CODE_SUP_CNT_SBL_SELECT:
				new MultiFSMSelection(OptionPage.getFSMUI().getFSMList(), getEntrySetFromCode(code));
				break;
			case CODE_OPT_SPVR_SELECT:
				new MultiFSMSelection(OptionPage.getFSMUI().getFSMList(), getEntrySetFromCode(code));
				break;
			case CODE_GRT_LWR_BND_SELECT:
				new MultiFSMSelection(OptionPage.getFSMUI().getFSMList(), getEntrySetFromCode(code));
				break;
			case CODE_PRODUCT:
				try {
					FSM[] fsms = getFSMArray(CODE_PRODUCT_SELECT);
					try {
						getFSMUI().allotTransitionSystem(getFSMUI().getActiveFSM().product(fsms), getFSMUI().getActiveFSM().getId() + "_product");
					}
					catch(Exception e1) {
						e1.printStackTrace();
						new PopoutAlert(250, 250, ERROR_TEXT_TWO);
					}
					getFSMUI().refreshActiveImage();
				}
				catch(Exception e) {
					e.printStackTrace();
					new PopoutAlert(250, 250, ERROR_TEXT_ONE);
				}
				break;
			case CODE_PARALLEL_COMPOSITION:
				try {
					FSM[] fsms = getFSMArray(CODE_PARALLEL_COMPOSITION_SELECT);
					try {
						getFSMUI().allotTransitionSystem(getFSMUI().getActiveFSM().parallelComposition(fsms), getFSMUI().getActiveFSM().getId() + "_parallelcomp");
					}
					catch(Exception e1) {
						e1.printStackTrace();
						new PopoutAlert(250, 250, ERROR_TEXT_TWO);
					}
					getFSMUI().refreshActiveImage();
				}
				catch(Exception e) {
					e.printStackTrace();
					new PopoutAlert(250, 250, ERROR_TEXT_ONE);
				}
				break;
			case CODE_SUP_CNT_SBL:
				try {
					FSM nfsm = getFSM(CODE_SUP_CNT_SBL_SELECT);
					try {
						getFSMUI().allotTransitionSystem(getFSMUI().getActiveFSM().getSupremalControllableSublanguage(nfsm), getFSMUI().getActiveFSM().getId() + "_supcntsublng");
					}
					catch(Exception e1) {
						e1.printStackTrace();
						new PopoutAlert(250, 250, ERROR_TEXT_TWO);
					}
					getFSMUI().refreshActiveImage();
				}
				catch(Exception e) {
					e.printStackTrace();
					new PopoutAlert(250, 250, ERROR_TEXT_ONE);
				}
				break;
			case CODE_BLOCKING:
				new PopoutAlert(250, 250, getFSMUI().getActiveFSM().isBlocking() ? "Is Blocking" : "Is Not Blocking");
				break;
			case CODE_STATE_EXISTS: 
				String state = this.getTextFromCode(CODE_STATE_EXISTS, 0);
				new PopoutAlert(250, 250, getFSMUI().getActiveFSM().stateExists(state) ? state + " exists" : state + " does not exist");
				break;
				/* TODO: Figure out what ModalSpecification is supposed to look like before integrating
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
				*/
			default:
				break;
		}
	}
	
//---  Helper Functions   ---------------------------------------------------------------------
	
	private FSM[] getFSMArray(int code) throws Exception{
		String[] fsmPaths = getContentFromCode(code);
		FSM[] fsms = new FSM[fsmPaths.length];
		int propLen = fsms.length;
		for(int i = 0; i < fsmPaths.length; i++) {
			if(fsmPaths[i] == null || fsmPaths[i].equals("")) {
				propLen = i;
				break;
			}
			File f = new File(FSMUI.ADDRESS_SOURCES + fsmPaths[i]);
			fsms[i] = new FSM(f, fsmPaths[i]);
		}
		FSM[] out = new FSM[propLen];
		for(int i = 0; i < out.length; i++) {
			out[i] = fsms[i];
		}
		return out;
	}
	
	private FSM getFSM(int code) throws Exception{
		String path = getTextFromCode(code, 0);
		File f = new File(FSMUI.ADDRESS_SOURCES + path);
		return new FSM(f, path);
	}
	
	private ModalSpecification getModal(int code) throws Exception{
		String path = getTextFromCode(code, 0);
		return new ModalSpecification(new File(FSMUI.ADDRESS_SOURCES + path), path);
	}
	
}
