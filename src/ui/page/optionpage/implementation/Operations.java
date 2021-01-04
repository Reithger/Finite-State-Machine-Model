package ui.page.optionpage.implementation;

import controller.CodeReference;
import ui.page.optionpage.OptionPage;

/**
 * 
 * TODO: Test for opacity
 * 
 * @author Ada Clevinger
 *
 */

public class Operations extends OptionPage{
	
//---  Constants   ----------------------------------------------------------------------------
	
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
			{"Trim", OptionPage.ENTRY_EMPTY, CodeReference.CODE_TRIM, true},
			{"Make Accessible", OptionPage.ENTRY_EMPTY, CodeReference.CODE_ACCESSIBLE, true},
			{"Make Co-Accessible", OptionPage.ENTRY_EMPTY, CodeReference.CODE_CO_ACCESSIBLE, true},
		},
		{
			{"Build Observer", OptionPage.ENTRY_EMPTY, CodeReference.CODE_OBSERVER, true},
			{"Product", OptionPage.ENTRY_BUTTON_LIST, CodeReference.CODE_PRODUCT_SELECT, true},	//H, respond to input to make MultiFSMSelection
			{"", OptionPage.ENTRY_EMPTY, CodeReference.CODE_PRODUCT, true},
			{"Parallel Composition", OptionPage.ENTRY_BUTTON_LIST, CodeReference.CODE_PARALLEL_COMPOSITION_SELECT, true},	//H
			{"", OptionPage.ENTRY_EMPTY, CodeReference.CODE_PARALLEL_COMPOSITION, true},
			{"Generate Supremal Controllable Sublanguage", OptionPage.ENTRY_BUTTON_LIST, CodeReference.CODE_SUP_CNT_SBL_SELECT, true},	//H
			{"", OptionPage.ENTRY_EMPTY, CodeReference.CODE_SUP_CNT_SBL, true},
		},
		{
			{"Get Underlying FSM", OptionPage.ENTRY_EMPTY, CodeReference.CODE_UNDER_FSM, true},
			{"Build Optimal Opaque Controller", OptionPage.ENTRY_EMPTY, CodeReference.CODE_OPT_OPQ_CONTROLLER, true},
			{"Make Optimal Supervisor", OptionPage.ENTRY_BUTTON_LIST, CodeReference.CODE_OPT_SPVR_SELECT, true},	//H
			{"", OptionPage.ENTRY_EMPTY, CodeReference.CODE_OPT_SPVR, true},
			{"Get Greatest Lower Bound", OptionPage.ENTRY_BUTTON_LIST, CodeReference.CODE_GRT_LWR_BND_SELECT, true},	//H
			{"", OptionPage.ENTRY_EMPTY, CodeReference.CODE_GRT_LWR_BND, true},
			{"Prune", OptionPage.ENTRY_EMPTY, CodeReference.CODE_PRUNE, true},
		},
		{
			{"Is Blocking", OptionPage.ENTRY_EMPTY, CodeReference.CODE_BLOCKING, true},
			{"State Exists", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_STATE_EXISTS, true},
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

}
