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
	private final static String LANGUAGE = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private final static int RANDOM_NAME_LENGTH = 8;
	
//---  Constructors   -------------------------------------------------------------------------

	public AdjustFSM(int x, int y, int width, int height) {
		super(HEADER, HELP, CATEGORIES, DATA, ref);
	}
	
//---  Operations   ---------------------------------------------------------------------------

	@Override
	public void applyCode(int code) {
		switch(code) {
			case CodeReference.CODE_ADD_STATES:
				int id = 1;
				int added = 0;
				int toAdd = this.getIntegerFromCode(CodeReference.CODE_ADD_STATES, 0);
				while(added != toAdd) {
					while(getFSMUI().getActiveFSM().stateExists(""+id)) {
						id++;
					}
					getFSMUI().getActiveFSM().addState(""+id++);
					added++;
				}
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CodeReference.CODE_RENAME_STATE:
				String oldName = getTextFromCode(CodeReference.CODE_RENAME_STATE, 0);
				String newName = getTextFromCode(CodeReference.CODE_RENAME_STATE, 1);
				getFSMUI().getActiveFSM().getStateMap().renameState(oldName, newName);
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CodeReference.CODE_SAVE_SVG:
				new PopoutAlert(250, 150, "Saved at: " + getFSMUI().saveActiveSVG());
				break;
			case CodeReference.CODE_SAVE_TKZ:
				new PopoutAlert(250, 150, "Saved at: " + getFSMUI().saveActiveTikZ());
				break;
			case CodeReference.CODE_SAVE_FSM:
				getFSMUI().saveActiveFSMSource();
				break;
			case CodeReference.CODE_SAVE_IMG:
				getFSMUI().saveActiveFSMImage();
				break;
			case CodeReference.CODE_DUPLICATE_FSM:
				String tex = getTextFromCode(CodeReference.CODE_DUPLICATE_FSM, 0); 
				tex = tex.equals("") || tex == null ? getFSMUI().getActiveFSM().getId() + "(copy)" : tex;
				getFSMUI().allotTransitionSystem(getFSMUI().getActiveFSM().copy(), tex);
				break;
			case CodeReference.CODE_RENAME_FSM:
				getFSMUI().renameActiveFSM(getTextFromCode(CodeReference.CODE_RENAME_FSM, 0));
				resetCodeEntries(code);
				break;
			case CodeReference.CODE_LOAD_SOURCE:
				File f = new File(FSMUI.ADDRESS_SOURCES);
				PopoutSelectList fs = new PopoutSelectList(300, 600, f.list(), true);
				String outcome = fs.getSelected();
				getFSMUI().allotTransitionSystem(FSMUI.ADDRESS_SOURCES + outcome, outcome.replaceAll(".fsm", ""));
				getFSMUI().refreshActiveImage();
				fs.dispose();
				resetCodeEntries(code);
				break;
			case CodeReference.CODE_DELETE_SOURCE:
				getFSMUI().deleteActiveFSM();
				break;
			case CodeReference.CODE_ADD_STATE: 
				getFSMUI().getActiveFSM().addState(this.getTextFromCode(CodeReference.CODE_ADD_STATE, 0));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CodeReference.CODE_REMOVE_STATE: 
				getFSMUI().getActiveFSM().removeState(this.getTextFromCode(CodeReference.CODE_REMOVE_STATE, 0));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CodeReference.CODE_ADD_TRANSITION: 	//TODO: Integrate Must transition boolean at CodeReference.CODE_TOGGLE_MUST
				getFSMUI().getActiveFSM().addTransition(this.getTextFromCode(CodeReference.CODE_ADD_TRANSITION, 0), this.getTextFromCode(CodeReference.CODE_ADD_TRANSITION, 1), this.getTextFromCode(CodeReference.CODE_ADD_TRANSITION, 2));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);	//TODO: Let user decide whether or not to clear entries, in settings
				break;
			case CodeReference.CODE_REMOVE_TRANSITION: 
				getFSMUI().getActiveFSM().removeTransition(this.getTextFromCode(CodeReference.CODE_REMOVE_TRANSITION, 0), this.getTextFromCode(CodeReference.CODE_REMOVE_TRANSITION, 1), this.getTextFromCode(CodeReference.CODE_REMOVE_TRANSITION, 2));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CodeReference.CODE_SECRET_STATE:
				getFSMUI().getActiveFSM().toggleSecretState(this.getTextFromCode(CodeReference.CODE_SECRET_STATE, 0));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CodeReference.CODE_INITIAL_STATE:
				getFSMUI().getActiveFSM().addInitialState(this.getTextFromCode(CodeReference.CODE_INITIAL_STATE, 0));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CodeReference.CODE_MARKED_STATE:
				getFSMUI().getActiveFSM().toggleMarkedState(this.getTextFromCode(CodeReference.CODE_MARKED_STATE, 0));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CodeReference.CODE_BAD_STATE:
				getFSMUI().getActiveFSM().toggleBadState(this.getTextFromCode(CodeReference.CODE_BAD_STATE, 0));
				getFSMUI().refreshActiveImage();
				resetCodeEntries(code);
				break;
			case CodeReference.CODE_CLOSE_FSM:
				getFSMUI().removeActiveTransitionSystem();
				getFSMUI().refreshActiveImage();
				break;
			case CodeReference.CODE_GENERATE_FSM: 
				try {
					int numState = getIntegerFromCode(CodeReference.CODE_ACCESS_NUM_STATES, 0);
					int numEvent = getIntegerFromCode(CodeReference.CODE_ACCESS_NUM_EVENTS, 0);
					int numTrans = getIntegerFromCode(CodeReference.CODE_ACCESS_NUM_TRANS, 0);
					boolean nonDet = getCheckboxContentsFromCode(CodeReference.CODE_ACCESS_NON_DETERMINISTIC);
					String name = getTextFromCode(CodeReference.CODE_ACCESS_FSM_NAME, 0);
					if(name == null) {
						name = "";
						Random rand = new Random();
						for(int i = 0; i < RANDOM_NAME_LENGTH; i++) {
							name += LANGUAGE.charAt(rand.nextInt(LANGUAGE.length()));
						}
					}
					String path = GenerateFSM.createNewFSM(numState, 0, numEvent, numTrans, 1, 0, 0, 0, 0, nonDet, name, FSMUI.ADDRESS_SOURCES);
					getFSMUI().allotTransitionSystem(path, name);
					resetCodeEntries(code);
				}
				catch(Exception e) {
					e.printStackTrace();
					new PopoutAlert(250, 250, "Failed to generate FSM, please check your input again.");
				}
				break;
			case CodeReference.CODE_GENERATE_COMPLEX_FSM:
				try {
					int numStateC = getIntegerFromCode(CodeReference.CODE_ACCESS_COMPLEX_NUM_STATES, 0);
					int numEventC = getIntegerFromCode(CodeReference.CODE_ACCESS_COMPLEX_NUM_EVENTS, 0);
					int numTransC = getIntegerFromCode(CodeReference.CODE_ACCESS_COMPLEX_NUM_TRANS, 0);
					int numInitC = getIntegerFromCode(CodeReference.CODE_ACCESS_COMPLEX_INITIAL, 0);
					int numMarkedC = getIntegerFromCode(CodeReference.CODE_ACCESS_COMPLEX_MARKED, 0);
					int numSecretC = getIntegerFromCode(CodeReference.CODE_ACCESS_COMPLEX_SECRET, 0);
					int numControlledC = getIntegerFromCode(CodeReference.CODE_ACCESS_COMPLEX_CONTROLLED, 0);
					int numUnobservedC = getIntegerFromCode(CodeReference.CODE_ACCESS_COMPLEX_UNOBSERVED, 0);
					int numAttackerC = getIntegerFromCode(CodeReference.CODE_ACCESS_COMPLEX_ATTACKER, 0);
					boolean nonDetC = getCheckboxContentsFromCode(CodeReference.CODE_ACCESS_NON_DETERMINISTIC);
					String nameC = getTextFromCode(CodeReference.CODE_ACCESS_COMPLEX_FSM_NAME, 0);
					if(nameC == null) {
						nameC = "";
						Random rand = new Random();
						for(int i = 0; i < RANDOM_NAME_LENGTH; i++) {
							nameC += LANGUAGE.charAt(rand.nextInt(LANGUAGE.length()));
						}
					}
					String pathC = GenerateFSM.createNewFSM(numStateC, numMarkedC, numEventC, numTransC, numInitC, numSecretC, numUnobservedC, numAttackerC, numControlledC, nonDetC, nameC, FSMUI.ADDRESS_SOURCES);
					getFSMUI().allotTransitionSystem(pathC, nameC);
					resetCodeEntries(code);
				}
				catch(Exception e) {
					e.printStackTrace();
					new PopoutAlert(250, 250, "Failed to generate FSM, please check your input again.");
				}
				break;
			default:
				break;
			}
	}
	
}
