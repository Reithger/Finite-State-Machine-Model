package ui.page.optionpage.implementation;

import controller.CodeReference;
import ui.page.optionpage.OptionPage;

/**
 * TODO: SVG is not displaying correctly, despite the produced TikZ functioning
 * correctly TODO: Allow editing a transition to become a Must transition, other
 * types? TODO: Crashes if Make FSM and Make Complicated FSM are open at the
 * same time TODO: Correction: It freezes if those two are open, naming
 * collision? Infinite loop?
 * 
 * @author Reithger
 *
 */

public class AdjustFSM extends OptionPage {

//---  Constant Values   ----------------------------------------------------------------------

	// -- Scripts ---------------------------------------------

	private final static String HEADER = "Adjust FSM";
	private final static String CATEGORY_GENERATE = "Generate FSM Simple";
	private final static String CATEGORY_EDIT_FSM = "Edit FSM Properties";
	private final static String CATEGORY_EDIT_STATES = "Edit FSM States";
	private final static String CATEGORY_EDIT_EVENTS = "Edit FSM Events";
	private final static String CATEGORY_EDIT_TRANSITIONS = "Edit FSM Transitions";
	private final static String CATEGORY_ADMIN = "Admin";
	private final static String[] CATEGORIES = new String[] { CATEGORY_GENERATE,
			CATEGORY_EDIT_FSM, CATEGORY_EDIT_STATES, CATEGORY_EDIT_EVENTS, CATEGORY_EDIT_TRANSITIONS, CATEGORY_ADMIN };
	private final static String HELP = "Some line\n" + "Another line\n" + "This will be the help page";

//---  Constructors   -------------------------------------------------------------------------

	public AdjustFSM() {
		super(HEADER, HELP);
		for(String s : CATEGORIES) {
			addCategory(s);
		}
		
		addEntryText(CATEGORY_GENERATE, "Number of States", false, CodeReference.CODE_ACCESS_NUM_STATES, 1, false);
		addEntryText(CATEGORY_GENERATE, "Number of Events", false, CodeReference.CODE_ACCESS_NUM_EVENTS, 1, false);
		addEntryText(CATEGORY_GENERATE, "Number of Transitions", false, CodeReference.CODE_ACCESS_NUM_TRANS, 1, false);
		addEntryCheckbox(CATEGORY_GENERATE, "Deterministic?", false, CodeReference.CODE_ACCESS_NON_DETERMINISTIC);
		addEntryText(CATEGORY_GENERATE, "Name", false, CodeReference.CODE_ACCESS_FSM_NAME, 1, true);
		addEntryList(CATEGORY_GENERATE, "State Attributes", false, CodeReference.CODE_ACCESS_STATE_ATTRIBUTES, CodeReference.CODE_ADD_STATE_ATTRIBUTE);
		addEntryList(CATEGORY_GENERATE, "Event Attributes", false, CodeReference.CODE_ACCESS_EVENT_ATTRIBUTES, CodeReference.CODE_ADD_EVENT_ATTRIBUTE);
		addEntryList(CATEGORY_GENERATE, "Transition Attributes", false, CodeReference.CODE_ACCESS_TRANS_ATTRIBUTES, CodeReference.CODE_ADD_TRANS_ATTRIBUTE);
		addEntryEmpty(CATEGORY_GENERATE, "Generate", true, CodeReference.CODE_GENERATE_FSM);
		
		addEntryText(CATEGORY_EDIT_FSM, "Rename FSM", true, CodeReference.CODE_RENAME_FSM, 1, true);
		addEntryList(CATEGORY_EDIT_FSM, "Add State Attributes", true, CodeReference.CODE_FSM_ADD_STATE_ATTRIBUTE, CodeReference.CODE_FSM_ACCESS_ADD_STATE_ATTRIBUTE);
		addEntryList(CATEGORY_EDIT_FSM, "Remove State Attributes", true, CodeReference.CODE_FSM_REMOVE_STATE_ATTRIBUTE, CodeReference.CODE_FSM_ACCESS_REMOVE_STATE_ATTRIBUTE);
		addEntryList(CATEGORY_EDIT_FSM, "Add Event Attributes", true, CodeReference.CODE_FSM_ADD_EVENT_ATTRIBUTE, CodeReference.CODE_FSM_ACCESS_ADD_EVENT_ATTRIBUTE);
		addEntryList(CATEGORY_EDIT_FSM, "Remove Event Attributes", true, CodeReference.CODE_FSM_REMOVE_EVENT_ATTRIBUTE, CodeReference.CODE_FSM_ACCESS_REMOVE_EVENT_ATTRIBUTE);
		addEntryList(CATEGORY_EDIT_FSM, "Add Transition Attributes", true, CodeReference.CODE_FSM_ADD_TRANS_ATTRIBUTE, CodeReference.CODE_FSM_ACCESS_ADD_TRANS_ATTRIBUTE);
		addEntryList(CATEGORY_EDIT_FSM, "Remove Transition Attributes", true, CodeReference.CODE_FSM_REMOVE_TRANS_ATTRIBUTE, CodeReference.CODE_FSM_ACCESS_REMOVE_TRANS_ATTRIBUTE);

		addEntryText(CATEGORY_EDIT_STATES, "Add State", true, CodeReference.CODE_ADD_STATE, 1, false);
		addEntryText(CATEGORY_EDIT_STATES, "Add Many States", true, CodeReference.CODE_ADD_STATES, 1, false);
		addEntryText(CATEGORY_EDIT_STATES, "Remove State", true, CodeReference.CODE_REMOVE_STATE, 1, false);
		addEntryText(CATEGORY_EDIT_STATES, "Rename State", true, CodeReference.CODE_RENAME_STATE, 2, false);
		addEntryText(CATEGORY_EDIT_STATES, "State to Edit", false, CodeReference.CODE_ACCESS_EDIT_STATE, 1, false);
		addEntryList(CATEGORY_EDIT_STATES, "Edit Attribute", true, CodeReference.CODE_EDIT_STATE_ATTRIBUTE, CodeReference.CODE_ADD_EDIT_STATE_ATTRIBUTE);

		addEntryText(CATEGORY_EDIT_EVENTS, "Add Event", true, CodeReference.CODE_ADD_EVENT, 1, false);
		addEntryText(CATEGORY_EDIT_EVENTS, "Add Many Events", true, CodeReference.CODE_ADD_EVENTS, 1, false);
		addEntryText(CATEGORY_EDIT_EVENTS, "Remove Event", true, CodeReference.CODE_REMOVE_EVENT, 1, false);
		addEntryText(CATEGORY_EDIT_EVENTS, "Rename Event", true, CodeReference.CODE_RENAME_EVENT, 2, false);
		addEntryText(CATEGORY_EDIT_EVENTS, "Event to Edit", false, CodeReference.CODE_ACCESS_EDIT_EVENT, 1, false);
		addEntryList(CATEGORY_EDIT_EVENTS, "Edit Attribute", true, CodeReference.CODE_EDIT_EVENT_ATTRIBUTE, CodeReference.CODE_ADD_EDIT_EVENT_ATTRIBUTE);

		addEntryText(CATEGORY_EDIT_TRANSITIONS, "Add Transition", true, CodeReference.CODE_ADD_TRANSITION, 3, false);
		addEntryText(CATEGORY_EDIT_TRANSITIONS, "Remove Transition", true, CodeReference.CODE_REMOVE_TRANSITION, 3, false);
		addEntryText(CATEGORY_EDIT_STATES, "Transition to Edit", false, CodeReference.CODE_ACCESS_EDIT_TRANS, 1, false);
		addEntryList(CATEGORY_EDIT_STATES, "Edit Attribute", true, CodeReference.CODE_EDIT_TRANS_ATTRIBUTE, CodeReference.CODE_ADD_EDIT_TRANS_ATTRIBUTE);
		
		addEntryEmpty(CATEGORY_ADMIN, "Save Source", true, CodeReference.CODE_SAVE_FSM);
		addEntryEmpty(CATEGORY_ADMIN, "Save Image", true, CodeReference.CODE_SAVE_IMG);
		addEntryEmpty(CATEGORY_ADMIN, "Generate TikZ", true, CodeReference.CODE_SAVE_TKZ);
		addEntryEmpty(CATEGORY_ADMIN, "Generate SVG", true, CodeReference.CODE_SAVE_SVG);
		addEntryEmpty(CATEGORY_ADMIN, "Load Source", true, CodeReference.CODE_LOAD_SOURCE);
		addEntryText(CATEGORY_ADMIN, "Duplicate", true, CodeReference.CODE_DUPLICATE_FSM, 1, true);
		addEntryEmpty(CATEGORY_ADMIN, "Close FSM", true, CodeReference.CODE_CLOSE_FSM);
		addEntryEmpty(CATEGORY_ADMIN, "Delete Source", true, CodeReference.CODE_DELETE_SOURCE);
	}

}
