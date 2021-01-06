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
	
	//-- Scripts  ---------------------------------------------
	
	private final static String HEADER = "Operations";
	
	private final static String CATEGORY_TRANS_SYSTEMS = "Transition Systems";
	private final static String CATEGORY_FSM = "FSM";
	private final static String CATEGORY_QUERIES = "Queries";
	
	private final static String[] CATEGORIES = new String[] {CATEGORY_TRANS_SYSTEMS, CATEGORY_FSM, CATEGORY_QUERIES};

	private final static String HELP = 
			"Some words\n"
			+ "And more\n" 
			+ "And more\n";
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Operations() {
		super(HEADER, HELP);
		for(String s : CATEGORIES) {
			addCategory(s);
		}
		
		addEntryEmpty(CATEGORY_TRANS_SYSTEMS, "Trim", true, CodeReference.CODE_TRIM);
		addEntryEmpty(CATEGORY_TRANS_SYSTEMS, "Make Accessible", true, CodeReference.CODE_ACCESSIBLE);
		addEntryEmpty(CATEGORY_TRANS_SYSTEMS, "Make CoAccessible", true, CodeReference.CODE_CO_ACCESSIBLE);
		
		addEntryEmpty(CATEGORY_FSM, "Build Observer", true, CodeReference.CODE_OBSERVER);
		addEntryList(CATEGORY_FSM, "Product", true, CodeReference.CODE_PRODUCT, CodeReference.CODE_PRODUCT_SELECT);
		addEntryList(CATEGORY_FSM, "Parallel Composition", true, CodeReference.CODE_PARALLEL_COMPOSITION, CodeReference.CODE_PARALLEL_COMPOSITION_SELECT);
		addEntryList(CATEGORY_FSM, "Supremal Controllable Sublanguage", true, CodeReference.CODE_SUP_CNT_SBL, CodeReference.CODE_SUP_CNT_SBL_SELECT);
	
		addEntryEmpty(CATEGORY_QUERIES, "Is Blocking", true, CodeReference.CODE_BLOCKING);
		addEntryText(CATEGORY_QUERIES, "State Exists", true, CodeReference.CODE_STATE_EXISTS, 1, false);
	}

}
