package controller;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

import controller.convert.FormatConversion;
import filemeta.FileChooser;
import filemeta.config.Config;
import model.AttributeList;
import model.Manager;
import ui.FSMUI;
import visual.composite.popout.PopoutAlert;

/*
 * TODO: Add multiple states renames some of them but maintains the overall structure? What?
 * 
 * 
 */

public class FiniteStateMachine implements InputReceiver{
	
//---  Constants   ----------------------------------------------------------------------------
	
	//-- Config  ----------------------------------------------
	
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
				generateRandomFSM(code);
				break;
			case CodeReference.CODE_ADD_STATE_ATTRIBUTE:
				requestAttributeChoice(code, AttributeList.STATE_ATTRIBUTES, "How many states of this type do you want?");
				break;
			case CodeReference.CODE_ADD_EVENT_ATTRIBUTE:
				requestAttributeChoice(code, AttributeList.EVENT_ATTRIBUTES, "How many events of this type do you want?");
				break;
			case CodeReference.CODE_ADD_TRANS_ATTRIBUTE:
				requestAttributeChoice(code, AttributeList.TRANS_ATTRIBUTES, "How many transitions of this type do you want?");
				break;
			case CodeReference.CODE_RENAME_STATE:
				String old = view.getTextContent(code, 0);
				String newName = view.getTextContent(code, 1);
				model.renameState(currFSM, old, newName);
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_ADD_STATES:
				int num = view.getIntegerContent(code);
				model.addStates(currFSM, num);
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_ADD_TRANSITION:
				addTransition(currFSM, code);
				break;
			case CodeReference.CODE_REMOVE_TRANSITION:
				removeTransition(currFSM, code);
				break;
			case CodeReference.CODE_SAVE_FSM:
				saveFSM(currFSM);
				break;
			case CodeReference.CODE_SAVE_IMG:
				new PopoutAlert(250, 200, "Image file saved to: " + generateDotImage(currFSM));
				break;
			case CodeReference.CODE_SAVE_TKZ:
				new PopoutAlert(250, 200, ".tkz file saved to: " + FormatConversion.createTikZFromFSM(model.generateFSMDot(currFSM), currFSM));
				break;
			case CodeReference.CODE_SAVE_SVG:
				new PopoutAlert(250, 200, ".svg file saved to: " + FormatConversion.createSVGFromFSM(model.generateFSMDot(currFSM), currFSM));
				break;
			case CodeReference.CODE_LOAD_SOURCE:
				loadSource();
				break;
			case CodeReference.CODE_DELETE_SOURCE:
				File remv = new File(ADDRESS_SOURCES + "/" + currFSM + ".fsm");
				remv.delete();
				break;
			case CodeReference.CODE_RENAME_FSM:
				String newFSM = view.getTextContent(code);
				model.renameFSM(currFSM, newFSM);
				view.removeFSM(currFSM);
				allotFSMToView(newFSM);
				break;
			case CodeReference.CODE_DUPLICATE_FSM:
				allotFSMToView(model.duplicate(currFSM));
				break;
			case CodeReference.CODE_CLOSE_FSM:
				view.removeFSM(currFSM);
				break;
			case CodeReference.CODE_TRIM:
				allotFSMToView(model.trim(currFSM));
				break;
			case CodeReference.CODE_ACCESSIBLE:
				allotFSMToView(model.makeAccessible(currFSM));
				break;
			case CodeReference.CODE_CO_ACCESSIBLE:
				allotFSMToView(model.makeCoAccessible(currFSM));
				break;
			case CodeReference.CODE_OBSERVER:
				allotFSMToView(model.buildObserver(currFSM));
				break;
			case CodeReference.CODE_PRODUCT:
				ArrayList<String> noms = view.getContent(code);
				model.performProduct(noms);
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_PRODUCT_SELECT:
				requestFSMChoice(code);
				break;
			case CodeReference.CODE_PARALLEL_COMPOSITION:
				ArrayList<String> noms2 = view.getContent(code);
				model.performParallelComposition(noms2);
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_PARALLEL_COMPOSITION_SELECT:
				requestFSMChoice(code);
				break;
			case CodeReference.CODE_BLOCKING:
				new PopoutAlert(250, 200, "FSM is " + (model.isBlocking(currFSM) ? "" : "not") + " blocking");
				break;
			case CodeReference.CODE_STATE_EXISTS:
				String chkSt = view.getTextContent(code);
				new PopoutAlert(250, 200, "FSM is " + (model.stateExists(currFSM, chkSt) ? "" : "not") + " blocking");
				break;
				
			//-- U-Structure  ---------------------------------
				
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
		if(ref == null || !model.hasFSM(ref)) {
			return;
		}
		view.updateFSMImage(ref, generateDotImage(ref));
	}
	
	//-- Input Handling Separation  ---------------------------
	
	private void loadSource() {
		String path = view.requestFilePath(ADDRESS_SOURCES, "");
		File f = new File(path);
		StringBuilder use = new StringBuilder();
		try {
			Scanner sc = new Scanner(f);
			while(sc.hasNextLine()) {
				use.append(sc.nextLine() + "\n");
			}
			sc.close();
			allotFSMToView(model.readInFSM(use.toString()));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveFSM(String currFSM) {
		String src = model.exportFSM(currFSM);
		File f = new File(ADDRESS_SOURCES + "/" + src + ".fsm");
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(f, "rw");
			raf.writeBytes(src);
			raf.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		new PopoutAlert(250, 200, "Source file saved to: " + f.getAbsolutePath());
	}
	
	private void generateRandomFSM(int code) {
		int st = view.getIntegerContent(CodeReference.CODE_ACCESS_NUM_STATES);
		int ev = view.getIntegerContent(CodeReference.CODE_ACCESS_NUM_EVENTS);
		int tr = view.getIntegerContent(CodeReference.CODE_ACCESS_NUM_TRANS);
		String nom = view.getTextContent(CodeReference.CODE_ACCESS_FSM_NAME);
		boolean det = view.getCheckboxContent(CodeReference.CODE_ACCESS_NON_DETERMINISTIC);
		
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		
		ArrayList<String> stateAttr = new ArrayList<String>();
		for(String s : view.getContent(CodeReference.CODE_ACCESS_STATE_ATTRIBUTES)) {
			String[] two = s.split((" - "));
			stateAttr.add(two[0]);
			numbers.add(Integer.parseInt(two[1]));
		}
		ArrayList<String> eventAttr = new ArrayList<String>();
		for(String s : view.getContent(CodeReference.CODE_ACCESS_EVENT_ATTRIBUTES)) {
			String[] two = s.split((" - "));
			eventAttr.add(two[0]);
			numbers.add(Integer.parseInt(two[1]));
		}
		ArrayList<String> transAttr = new ArrayList<String>();
		for(String s : view.getContent(CodeReference.CODE_ACCESS_TRANS_ATTRIBUTES)) {
			String[] two = s.split((" - "));
			transAttr.add(two[0]);
			numbers.add(Integer.parseInt(two[1]));
		}
		
		allotFSMToView(	model.readInFSM(model.generateRandomFSM(nom, st, ev, tr, det, stateAttr, eventAttr, transAttr, numbers)));
	}

	private void addTransition(String currFSM, int code) {
		String state1 = view.getTextContent(code, 0);
		String event = view.getTextContent(code, 1);
		String state2 = view.getTextContent(code, 2);
		model.addTransition(currFSM, state1, event, state2);
		view.clearTextContents(code);
	}
	
	private void removeTransition(String currFSM, int code) {
		String state1 = view.getTextContent(code, 0);
		String event = view.getTextContent(code, 1);
		String state2 = view.getTextContent(code, 2);
		model.removeTransition(currFSM, state1, event, state2);
		view.clearTextContents(code);
	}
	
	//-- User Request  ----------------------------------------

	private void requestAttributeChoice(int code, String[] attributes, String phrase) {
		ArrayList<String> statAttr = view.getContent(code);	//remove existing Attributes from current list
		String use = view.requestUserInputList(attributes, true);
		int num = view.requestUserIntegerInput(phrase);
		int ind = statAttr.size();
		for(int i = 0; i < statAttr.size(); i++) {
			if(statAttr.get(i).split(" - ")[0].equals(use)) {
				ind = i;
				break;
			}
		}
		view.setTextContent(code, ind, use + " - " + num);
	}
	
	private void requestFSMChoice(int code) {
		ArrayList<String> content = view.getContent(code);
		ArrayList<String> start = model.getReferences();
		String[] use = new String[start.size()];
		for(int i = 0; i < use.length; i++) {
			use[i] = start.get(i);
		}
		String choice = view.requestUserInputList(use, true);
		view.setTextContent(code, content.size(), choice);
	}
	
	//-- File Configuration  ----------------------------------
	
	private void fileConfiguration() {
		Config c = new Config("", new UMLConfigValidation());
		c.addFilePath("Finite State Machine Model");
		c.addFilePath("Finite State Machine Model/settings");
		c.addFilePath("Finite State Machine Model/images");
		c.addFilePath("Finite State Machine Model/sources");
		c.addFile("Finite State Machine Model/settings", "config.txt", DEFAULT_CONFIG_COMMENT);
		c.addFileEntry("Finite State Machine Model/settings", "config.txt", DOT_ADDRESS_VAR, "Where is your dot program located? It will be called externally.", "?");
		
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
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private String generateDotImage(String ref) {
		return FormatConversion.createImgFromFSM(model.generateFSMDot(ref), ref);
	}
	
	private void allotFSMToView(String fsm) {
		view.addFSM(fsm, generateDotImage(fsm));
	}
	
}
