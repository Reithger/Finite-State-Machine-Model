package controller;

import java.awt.Image;

import controller.convert.FormatConversion;
import filemeta.FileChooser;
import filemeta.config.Config;
import model.Manager;
import ui.FSMUI;
import visual.composite.popout.PopoutAlert;

public class FiniteStateMachine implements InputReceiver{
	
//---  Constants   ----------------------------------------------------------------------------
	
	//-- Config  ----------------------------------------------
	
	private final static String OS = System.getProperty("os.name");
	public final static String DOT_ADDRESS_VAR = "dotAddress";
	public final static String ADDRESS_SETTINGS = "./Finite State Machine Model/settings/";
	public final static String ADDRESS_IMAGES = "./Finite State Machine Model/images/";
	public final static String ADDRESS_SOURCES = "./Finite State Machine Model/sources/";
	public final static String ADDRESS_CONFIG = ADDRESS_SETTINGS + "/config.txt";
	
	private final static String DEFAULT_CONFIG_COMMENT = "##############################################################\r\n" + 
			"#                       Configurations                       #\r\n" + 
			"##############################################################\r\n" + 
			"# Format as 'name = address', the \" = \" spacing is necessary\r\n" + 
			"# It's awkward but it makes the file reading easier and I'm telling you this directly";

	public final static int WINDOW_WIDTH = 1000;
	public final static int WINDOW_HEIGHT = 600;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private FSMUI view;
	private Manager model;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FiniteStateMachine() {
		view = new FSMUI(WINDOW_WIDTH, WINDOW_HEIGHT, this);
		model = new Manager();
		FormatConversion.assignPaths(ADDRESS_SOURCES, ADDRESS_CONFIG);
		fileConfiguration();
	}

//---  Operations   ---------------------------------------------------------------------------
	
	//-- Input Handling  --------------------------------------
	
	public void receiveCode(int code, int mouseType) {
		if(code == -1) {
			return;
		}
		String currFSM = view.getCurrentFSM();
		switch(code) {
			case CodeReference.CODE_ADD_STATE:
				String newState = view.getTextContent(code);
				model.addState(currFSM, newState);
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_REMOVE_STATE:
				String removeState = view.getTextContent(code);
				model.removeState(currFSM, removeState);
				view.clearTextContents(code);
			case CodeReference.CODE_GENERATE_FSM:
				int st = view.getIntegerContent(CodeReference.CODE_ACCESS_NUM_STATES);
				int ev = view.getIntegerContent(CodeReference.CODE_ACCESS_NUM_EVENTS);
				int tr = view.getIntegerContent(CodeReference.CODE_ACCESS_NUM_TRANS);
				String nom = view.getTextContent(CodeReference.CODE_ACCESS_FSM_NAME);
				boolean det = view.getCheckboxContent(CodeReference.CODE_ACCESS_NON_DETERMINISTIC);
				model.readInFSM(model.generateRandomFSM(nom, st, ev, tr, det));
				view.addFSM(nom, generateDotImage(nom));
				break;
			case CodeReference.CODE_RENAME_STATE:
				break;
			case CodeReference.CODE_ADD_STATES:
				break;
			case CodeReference.CODE_ADD_TRANSITION:
				break;
			case CodeReference.CODE_REMOVE_TRANSITION:
				break;
			case CodeReference.CODE_SAVE_FSM:
				break;
			case CodeReference.CODE_SAVE_IMG:
				break;
			case CodeReference.CODE_SAVE_TKZ:
				break;
			case CodeReference.CODE_SAVE_SVG:
				break;
			case CodeReference.CODE_LOAD_SOURCE:
				break;
			case CodeReference.CODE_DELETE_SOURCE:
				break;
			case CodeReference.CODE_RENAME_FSM:
				break;
			case CodeReference.CODE_DUPLICATE_FSM:
				break;
			case CodeReference.CODE_CLOSE_FSM:
				break;
			case CodeReference.CODE_TRIM:
				break;
			case CodeReference.CODE_ACCESSIBLE:
				break;
			case CodeReference.CODE_CO_ACCESSIBLE:
				break;
			case CodeReference.CODE_OBSERVER:
				break;
			case CodeReference.CODE_PRODUCT:
				break;
			case CodeReference.CODE_PARALLEL_COMPOSITION:
				break;
			case CodeReference.CODE_SUP_CNT_SBL:
				break;
			case CodeReference.CODE_BLOCKING:
				break;
			case CodeReference.CODE_STATE_EXISTS:
				break;
			case CodeReference.CODE_SELECT_PLANT:
				break;
			case CodeReference.CODE_ADD_BAD_TRANS:
				break;
			case CodeReference.CODE_BUILD_AGENTS:
				break;
			case CodeReference.CODE_BUILD_USTRUCT:
				break;
			case CodeReference.CODE_TOGGLE_USTRUCT:
				break;
			case CodeReference.CODE_DISPLAY_BAD_TRANS_START:
				break;
				
			default:
				break;
		}
		updateViewFSM(currFSM);
	}
	
	public void receiveKeyInput(char code, int keyType) {
		
	}
	
	public void updateViewFSM(String ref) {
		view.updateFSMImage(ref, generateDotImage(ref));
	}
	
	private String generateDotImage(String ref) {
		return FormatConversion.createImgFromFSM(model.generateFSMDot(ref), ref);
	}
	
	//-- File Configuration  ----------------------------------
	
	private void fileConfiguration() {
		Config c = new Config("", new UMLConfigValidation());
		c.addFilePath("Diagram");
		c.addFilePath("Diagram/settings");
		c.addFilePath("Diagram/images");
		c.addFilePath("Diagram/sources");
		c.addFile("Diagram/settings", "config.txt", DEFAULT_CONFIG_COMMENT);
		c.addFileEntry("Diagram/settings", "config.txt", DOT_ADDRESS_VAR, "Where is your dot program located? It will be called externally.", "?");
		
		c.softWriteConfig();
		
		while(!c.verifyConfig()) {
			switch(c.getErrorCode()) {
				case UMLConfigValidation.CODE_FAILURE_DOT_ADDRESS:
					PopoutAlert pA = new PopoutAlert(400, 250, "Please navigate to and select the path for your graphviz/bin/dot.exe file in the following navigation tool");
					c.setConfigFileEntry("Diagram/settings/config.txt", DOT_ADDRESS_VAR, FileChooser.promptSelectFile("C:/", true, true).getAbsolutePath());
					pA.dispose();
					break;
				case UMLConfigValidation.CODE_FAILURE_FILE_MISSING:
					c.initializeDefaultConfig();
					break;
				default:
					break;
			}
		}
	}
	
}
