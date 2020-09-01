package ui.page.optionpage.implementation;

import java.util.ArrayList;

import support.component.Transition;
import ui.page.optionpage.OptionPage;
import ui.page.optionpage.entryset.EntrySet;

public class UStructurePage extends OptionPage{

//---  Constants   ----------------------------------------------------------------------------
	
	//-- Codes  -----------------------------------------------
	
	private final static int CODE_SELECT_PLANT = 100;
	private final static int CODE_ADD_BAD_TRANS = 101;
	private final static int CODE_BUILD_AGENTS = 102;
	private final static int CODE_BUILD_USTRUCT = 103;
	private final static int CODE_DISPLAY_BAD_TRANS_START = 500;
	
	//-- Scripts  ---------------------------------------------
	
	private final static String HEADER = "UStructure";
	private final static String[] CATEGORIES = new String[] {"Build U-Structure"};
	private final static Object[][][] DATA = new Object[][][] {
		{
			{"Plant", EntrySet.ENTRY_SELECT_FSM, CODE_SELECT_PLANT, false},
			{"Bad Transitions", EntrySet.ENTRY_TEXT_TRIPLE, CODE_ADD_BAD_TRANS, true},		//Need ENTRY_TEXT_TRIPLE but allows repeated entry
			{"", EntrySet.TEXT_DISPLAY, CODE_DISPLAY_BAD_TRANS_START, false},
			{"Agents", EntrySet.ENTRY_AGENTS, CODE_BUILD_AGENTS, false},			//Pop-up window for defining visible/controllable events for n agents
			{"Build U-Structure", EntrySet.ENTRY_EMPTY, CODE_BUILD_USTRUCT, true},
		},

	};
	private final static String HELP = 
			"Some words\n"
			+ "And more\n" 
			+ "And more\n";

	private final static String SEPARATOR = "-_-~-_-";
	
	
//---  Instance Variables   -------------------------------------------------------------------
	
	ArrayList<String> badTransitions;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public UStructurePage(int x, int y, int wid, int hei) {
		super(HEADER, HELP, CATEGORIES, DATA);
		badTransitions = new ArrayList<String>();
	}
	
//---  Operations   ---------------------------------------------------------------------------

	@Override
	public void applyCode(int code) {
		if(!toggleCategory(code)) {
			if(code >= CODE_DISPLAY_BAD_TRANS_START) {
				int ind = code - CODE_DISPLAY_BAD_TRANS_START;
				badTransitions.remove(ind);
				getEntrySetFromCode(CODE_DISPLAY_BAD_TRANS_START).removeItem(ind);
				getElementPanel().removeElementPrefixed("");
				drawPage();
			}
			switch(code) {
				case CODE_SELECT_PLANT:
					//TODO: Make sure plant gets reset so 0'th index always contains desired url path
					//TODO: Remember to use your static Communication object
					getEntrySetFromCode(CODE_BUILD_AGENTS).appendItem(getTextFromCode(CODE_SELECT_PLANT, 0));
					break;
				case CODE_ADD_BAD_TRANS:
					String a = this.getTextFromCode(code, 0);
					String b = this.getTextFromCode(code, 1);
					String c = this.getTextFromCode(code, 2);
					badTransitions.add(a + SEPARATOR + b + SEPARATOR + c);
					getEntrySetFromCode(CODE_DISPLAY_BAD_TRANS_START).appendItem(a + " -- " + b + " --> " + c);
					resetCodeEntries(code);
					break;
				case CODE_BUILD_AGENTS:
					
					break;
				case CODE_BUILD_USTRUCT:
					
					break;
			}
		}
		drawPage();
	}

}
