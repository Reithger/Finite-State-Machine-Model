package ui.page.optionpage.implementation;

import controller.CodeReference;
import ui.page.optionpage.OptionPage;

public class UStructurePage extends OptionPage{

//---  Constants   ----------------------------------------------------------------------------
	
	//-- Codes  -----------------------------------------------
	
	//-- Scripts  ---------------------------------------------
	
	private final static String HEADER = "UStructure";
	private final static String[] CATEGORIES = new String[] {"Build U-Structure"};
	private final static Object[][][] DATA = new Object[][][] {
		{
			{"Plant", OptionPage.ENTRY_BUTTON_LIST, CodeReference.CODE_SELECT_PLANT, true},
			{"Bad Transitions", OptionPage.ENTRY_TEXT_TRIPLE, CodeReference.CODE_ADD_BAD_TRANS, true},		//Need ENTRY_TEXT_TRIPLE but allows repeated entry
			{"", OptionPage.TEXT_DISPLAY, CodeReference.CODE_DISPLAY_BAD_TRANS_START, false},
			{"Agents", OptionPage.TEXT_DISPLAY, CodeReference.CODE_BUILD_AGENTS, true},			//Pop-up window for defining visible/controllable events for n agents
			{"Display UStructure?", OptionPage.ENTRY_CHECKBOX, CodeReference.CODE_TOGGLE_USTRUCT, false},
			{"Build U-Structure", OptionPage.ENTRY_EMPTY, CodeReference.CODE_BUILD_USTRUCT, true},
		},

	};
	private final static String HELP = 
			"Some words\n"
			+ "And more\n" 
			+ "And more\n";

	private final static String SEPARATOR = "-_-~-_-";
	
//---  Constructors   -------------------------------------------------------------------------
	
	public UStructurePage() {
		super(HEADER, HELP);
	}

}
