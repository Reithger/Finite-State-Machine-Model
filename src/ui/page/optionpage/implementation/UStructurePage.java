package ui.page.optionpage.implementation;

import java.io.File;
import java.util.ArrayList;

import fsm.FSM;
import support.Agent;
import support.UStructure;
import support.component.map.TransitionFunction;
import ui.FSMUI;
import ui.page.optionpage.OptionPage;
import ui.page.optionpage.entryset.EntrySet;
import ui.page.popups.PopoutAgentSelection;

public class UStructurePage extends OptionPage{

//---  Constants   ----------------------------------------------------------------------------
	
	//-- Codes  -----------------------------------------------
	
	private final static int CODE_SELECT_PLANT = 100;
	private final static int CODE_ADD_BAD_TRANS = 101;
	private final static int CODE_BUILD_AGENTS = 102;
	private final static int CODE_BUILD_USTRUCT = 103;
	private final static int CODE_TOGGLE_USTRUCT = 104;
	private final static int CODE_DISPLAY_BAD_TRANS_START = 500;
	
	//-- Scripts  ---------------------------------------------
	
	private final static String HEADER = "UStructure";
	private final static String[] CATEGORIES = new String[] {"Build U-Structure"};
	private final static Object[][][] DATA = new Object[][][] {
		{
			{"Plant", EntrySet.ENTRY_SELECT_FSM, CODE_SELECT_PLANT, false},
			{"Bad Transitions", EntrySet.ENTRY_TEXT_TRIPLE, CODE_ADD_BAD_TRANS, true},		//Need ENTRY_TEXT_TRIPLE but allows repeated entry
			{"", EntrySet.TEXT_DISPLAY, CODE_DISPLAY_BAD_TRANS_START, false},
			{"Agents", EntrySet.TEXT_DISPLAY, CODE_BUILD_AGENTS, true},			//Pop-up window for defining visible/controllable events for n agents
			{"Display UStructure?", EntrySet.ENTRY_CHECKBOX, CODE_TOGGLE_USTRUCT, false},
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
	ArrayList<Agent> agents;
	UStructure built;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public UStructurePage(int x, int y, int wid, int hei) {
		super(HEADER, HELP, CATEGORIES, DATA);
		badTransitions = new ArrayList<String>();
	}
	
//---  Operations   ---------------------------------------------------------------------------

	@Override
	public void applyCode(int code) {
		if(code >= CODE_DISPLAY_BAD_TRANS_START) {
			int ind = code - CODE_DISPLAY_BAD_TRANS_START;
			badTransitions.remove(ind);
			getEntrySetFromCode(CODE_DISPLAY_BAD_TRANS_START).removeItem(ind);
			getElementPanel().removeElementPrefixed("");
			drawPage();
		}
		switch(code) {
			case CODE_ADD_BAD_TRANS:
				String a = this.getTextFromCode(code, 0);
				String b = this.getTextFromCode(code, 1);
				String c = this.getTextFromCode(code, 2);
				badTransitions.add(a + SEPARATOR + b + SEPARATOR + c);
				getEntrySetFromCode(CODE_DISPLAY_BAD_TRANS_START).appendItem(a + " -- " + b + " --> " + c);
				resetCodeEntries(code);
				break;
			case CODE_BUILD_AGENTS:
				new PopoutAgentSelection(this, FSMUI.ADDRESS_SOURCES + getTextFromCode(CODE_SELECT_PLANT, 0));
				break;
			case CODE_BUILD_USTRUCT:
				TransitionFunction tF = new TransitionFunction();
				for(String s : badTransitions) {
					String[] dat = s.split(SEPARATOR);
					tF.addTransition(dat[0], dat[1], dat[2]);
				}
				FSM fs = null;
				try {
					fs = new FSM(new File(FSMUI.ADDRESS_SOURCES + getTextFromCode(CODE_SELECT_PLANT, 0)), getTextFromCode(CODE_SELECT_PLANT, 0) + "_ustruct");
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				built = new UStructure(fs, tF, agents);
				if(this.getCheckboxContentsFromCode(CODE_TOGGLE_USTRUCT)) {
					getFSMUI().allotTransitionSystem(built.getUStructure(), fs.getId());
				}
				break;
		}
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setAgents(ArrayList<Agent> in) {
		agents = in;
	}

}
