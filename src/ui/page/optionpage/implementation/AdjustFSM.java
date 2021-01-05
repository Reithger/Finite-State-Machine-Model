package ui.page.optionpage.implementation;

import java.io.File;
import java.util.Random;

import controller.CodeReference;
import ui.FSMUI;
import ui.page.optionpage.OptionPage;
import visual.composite.popout.PopoutAlert;
import visual.composite.popout.PopoutSelectList;

/**
 * TODO: SVG is not displaying correctly, despite the produced TikZ functioning correctly
 * TODO: Allow editing a transition to become a Must transition, other types?
 * TODO: Crashes if Make FSM and Make Complicated FSM are open at the same time
 * TODO: Correction: It freezes if those two are open, naming collision? Infinite loop?
 * @author Reithger
 *
 */

public class AdjustFSM extends OptionPage{
	
//---  Constant Values   ----------------------------------------------------------------------
	
	//-- Scripts  ---------------------------------------------
	
	private final static String HEADER = "Adjust FSM";
	private final static String[] CATEGORIES = new String[] {"Generate FSM Simple", "Generate FSM Complicated", "Edit FSM States", "Edit FSM Transitions", "Admin"};
	private final static Object[][][] DATA = new Object[][][] {
		{
			{"Number of States", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ACCESS_NUM_STATES, false},
			{"Number of Events", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ACCESS_NUM_EVENTS, false},
			{"Number of Transitions", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ACCESS_NUM_TRANS, false},
			{"Non-Deterministic", OptionPage.ENTRY_CHECKBOX, CodeReference.CODE_ACCESS_NON_DETERMINISTIC, false},
			{"Name", OptionPage.ENTRY_TEXT_LONG, CodeReference.CODE_ACCESS_FSM_NAME, false},
			{"Generate", OptionPage.ENTRY_EMPTY,  CodeReference.CODE_GENERATE_FSM, true},
		},
		{
			{"Number of States", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ACCESS_COMPLEX_NUM_STATES, false},
			{"Number of Events", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ACCESS_COMPLEX_NUM_EVENTS, false},
			{"Number of Transitions", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ACCESS_COMPLEX_NUM_TRANS, false},
			
			{"Number of Initial States", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ACCESS_COMPLEX_INITIAL, false},
			{"Number of Marked States", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ACCESS_COMPLEX_MARKED, false},
			{"Number of Secret States", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ACCESS_COMPLEX_SECRET, false},
			{"Number of Controlled Events", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ACCESS_COMPLEX_CONTROLLED, false},
			{"Number of Unobservable Events", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ACCESS_COMPLEX_UNOBSERVED, false},
			{"Number of Attacker Invisible Events", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ACCESS_COMPLEX_ATTACKER, false},
			
			{"Non-Deterministic", OptionPage.ENTRY_CHECKBOX, CodeReference.CODE_ACCESS_COMPLEX_NON_DETERMINISTIC, false},
			{"Name", OptionPage.ENTRY_TEXT_LONG, CodeReference.CODE_ACCESS_COMPLEX_FSM_NAME, false},
			{"Generate", OptionPage.ENTRY_EMPTY, CodeReference.CODE_GENERATE_COMPLEX_FSM, true},
		},
		{
			{"Add State", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ADD_STATE, true},
			{"Add Many States", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_ADD_STATES, true},
			{"Remove State", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_REMOVE_STATE, true},
			{"Rename State", OptionPage.ENTRY_TEXT_DOUBLE, CodeReference.CODE_RENAME_STATE, true},
			{"Add Initial State", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_INITIAL_STATE, true},
			{"Set State Marked", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_MARKED_STATE, true},
			{"Set State Secret", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_SECRET_STATE, true},
			{"Set State Bad", OptionPage.ENTRY_TEXT_SINGLE, CodeReference.CODE_BAD_STATE, true},
		},
		{
			{"Add Transition", OptionPage.ENTRY_TEXT_TRIPLE, CodeReference.CODE_ADD_TRANSITION, true},
			//{"Mark Must Transition", OptionPage.ENTRY_CHECKBOX, CodeReference.CODE_TOGGLE_MUST, false},
			{"Remove Transition", OptionPage.ENTRY_TEXT_TRIPLE, CodeReference.CODE_REMOVE_TRANSITION, true},
		},
		{
			{"Save Source", OptionPage.ENTRY_EMPTY, CodeReference.CODE_SAVE_FSM, true},
			{"Save Image", OptionPage.ENTRY_EMPTY, CodeReference.CODE_SAVE_IMG, true},
			{"Generate Tikz", OptionPage.ENTRY_EMPTY, CodeReference.CODE_SAVE_TKZ, true},
			{"Generate SVG", OptionPage.ENTRY_EMPTY, CodeReference.CODE_SAVE_SVG, true},
			{"Load Source", OptionPage.ENTRY_EMPTY, CodeReference.CODE_LOAD_SOURCE, true},
			{"Rename FSM", OptionPage.ENTRY_TEXT_LONG, CodeReference.CODE_RENAME_FSM, true},
			{"Generate Copy", OptionPage.ENTRY_TEXT_LONG, CodeReference.CODE_DUPLICATE_FSM, true},
			{"Close FSM", OptionPage.ENTRY_EMPTY, CodeReference.CODE_CLOSE_FSM, true},
			{"Delete Source", OptionPage.ENTRY_EMPTY, CodeReference.CODE_DELETE_SOURCE, true},
		},
	};
	private final static String HELP = 
			"Some line\n"
			+ "Another line\n"
			+ "This will be the help page";
	
//---  Constructors   -------------------------------------------------------------------------

	public AdjustFSM() {
		super(HEADER, HELP);
	}

}
