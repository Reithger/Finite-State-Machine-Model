package ui.page.optionpage.implementation;

import controller.CodeReference;
import ui.page.optionpage.OptionPage;

public class UStructurePage extends OptionPage{

//---  Constants   ----------------------------------------------------------------------------
	
	//-- Scripts  ---------------------------------------------
	
	private final static String HEADER = "UStructure";
	private final static String CATEGORY_USTRUC = "Build U-Structure";

	private final static String HELP = 
			"Some words\n"
			+ "And more\n" 
			+ "And more\n";

//---  Constructors   -------------------------------------------------------------------------
	
	public UStructurePage() {
		super(HEADER, HELP);
		
		addCategory(CATEGORY_USTRUC);
		
		addEntryList(CATEGORY_USTRUC, "Plant", false, -1, CodeReference.CODE_SELECT_PLANT);
		addEntryList(CATEGORY_USTRUC, "Bad Transitions", false, CodeReference.CODE_ADD_BAD_TRANS, CodeReference.CODE_DISPLAY_BAD_TRANS_START);
		addEntryTextDisplay(CATEGORY_USTRUC, "Agents", true, CodeReference.CODE_BUILD_AGENTS);
		addEntryCheckbox(CATEGORY_USTRUC, "Display U-Structure?", false, CodeReference.CODE_TOGGLE_USTRUCT);
		addEntryEmpty(CATEGORY_USTRUC, "Build U-Structure", true, CodeReference.CODE_BUILD_USTRUCT);
		
	}

}
