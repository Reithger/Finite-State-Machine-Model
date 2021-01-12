package controller;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

import controller.convert.FormatConversion;
import filemeta.FileChooser;
import filemeta.config.Config;
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
		codeHandlingAdjustFSM(code, mouseType);
		codeHandlingOperations(code, mouseType);
		codeHandlingUStructure(code, mouseType);

		updateViewFSM(view.getCurrentFSM());
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
	
	private void codeHandlingAdjustFSM(int code, int mouseType) {
		String currFSM = view.getCurrentFSM();
		switch(code) {
		
		//-- Generate FSM  ------------------------------------
				
			case CodeReference.CODE_GENERATE_FSM:
				generateRandomFSM(code);
				break;
			case CodeReference.CODE_ADD_STATE_ATTRIBUTE:
				requestAttributeChoice(code, model.getStateAttributeList(), "How many states of this type do you want?");
				break;
			case CodeReference.CODE_ADD_EVENT_ATTRIBUTE:
				requestAttributeChoice(code, model.getEventAttributeList(), "How many events of this type do you want?");
				break;
			case CodeReference.CODE_ADD_TRANS_ATTRIBUTE:
				requestAttributeChoice(code, model.getTransitionAttributeList(), "How many transitions of this type do you want?");
				break;
				
		//-- FSM Properties  ----------------------------------

			case CodeReference.CODE_RENAME_FSM:
				String newFSM = view.getTextContent(code);
				model.renameFSM(currFSM, newFSM);
				view.removeFSM(currFSM);
				allotFSMToView(newFSM);
				break;
			case CodeReference.CODE_FSM_ADD_STATE_ATTRIBUTE:
				model.setFSMStateAttributes(currFSM, addAttributeLists(view.getContent(code), model.getFSMStateAttributes(currFSM)));
				break;
			case CodeReference.CODE_FSM_ACCESS_ADD_STATE_ATTRIBUTE:
				appendSingleChosenAttribute(model.getStateAttributeList(), code);
				break;
			case CodeReference.CODE_FSM_ADD_EVENT_ATTRIBUTE:
				model.setFSMEventAttributes(currFSM, addAttributeLists(view.getContent(code), model.getFSMEventAttributes(currFSM)));
				break;
			case CodeReference.CODE_FSM_ACCESS_ADD_EVENT_ATTRIBUTE:
				appendSingleChosenAttribute(model.getEventAttributeList(), code);
				break;
			case CodeReference.CODE_FSM_ADD_TRANS_ATTRIBUTE:
				model.setFSMTransitionAttributes(currFSM, addAttributeLists(view.getContent(code), model.getFSMTransitionAttributes(currFSM)));
				break;
			case CodeReference.CODE_FSM_ACCESS_ADD_TRANS_ATTRIBUTE:
				appendSingleChosenAttribute(model.getTransitionAttributeList(), code);
				break;
			case CodeReference.CODE_FSM_REMOVE_STATE_ATTRIBUTE:
				model.setFSMStateAttributes(currFSM, subtractAttributeLists(view.getContent(code), model.getFSMStateAttributes(currFSM)));
				break;
			case CodeReference.CODE_FSM_ACCESS_REMOVE_STATE_ATTRIBUTE:
				appendSingleChosenAttribute(model.getFSMStateAttributes(currFSM), code);
				break;
			case CodeReference.CODE_FSM_REMOVE_EVENT_ATTRIBUTE:
				model.setFSMEventAttributes(currFSM, subtractAttributeLists(view.getContent(code), model.getFSMEventAttributes(currFSM)));
				break;
			case CodeReference.CODE_FSM_ACCESS_REMOVE_EVENT_ATTRIBUTE:
				appendSingleChosenAttribute(model.getFSMEventAttributes(currFSM), code);
				break;
			case CodeReference.CODE_FSM_REMOVE_TRANS_ATTRIBUTE:
				model.setFSMTransitionAttributes(currFSM, subtractAttributeLists(view.getContent(code), model.getFSMTransitionAttributes(currFSM)));
				break;
			case CodeReference.CODE_FSM_ACCESS_REMOVE_TRANS_ATTRIBUTE:
				appendSingleChosenAttribute(model.getFSMTransitionAttributes(currFSM), code);
				break;
				
		//-- States  ------------------------------------------
		
			case CodeReference.CODE_ADD_STATE:
				model.addState(currFSM, view.getTextContent(code));
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_ADD_STATES:
				model.addStates(currFSM, view.getIntegerContent(code));
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_REMOVE_STATE:
				model.removeState(currFSM, view.getTextContent(code));
				view.clearTextContents(code);
			case CodeReference.CODE_RENAME_STATE:
				model.renameState(currFSM, view.getTextContent(code, 0), view.getTextContent(code, 1));
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_ADD_EDIT_STATE_ATTRIBUTE:
				appendSingleChosenAttribute(model.getFSMStateAttributes(currFSM), code);
				break;
			case CodeReference.CODE_EDIT_STATE_ATTRIBUTE:
				ArrayList<String> grab = view.getContent(code);
				for(String s : model.getFSMStateAttributes(currFSM)) {
					model.setStateAttribute(currFSM, view.getTextContent(CodeReference.CODE_ACCESS_EDIT_STATE), s, grab.contains(s));
				}
				break;
				

		//-- Events  ------------------------------------------
				
			case CodeReference.CODE_ADD_EVENT:
				model.addEvent(currFSM, view.getTextContent(code));
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_ADD_EVENTS:
				model.addEvents(currFSM, view.getIntegerContent(code));
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_REMOVE_EVENT:
				model.removeEvent(currFSM, view.getTextContent(code));
				view.clearTextContents(code);
			case CodeReference.CODE_RENAME_EVENT:
				model.renameEvent(currFSM, view.getTextContent(code, 0), view.getTextContent(code, 1));
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_ADD_EDIT_EVENT_ATTRIBUTE:
				appendSingleChosenAttribute(model.getFSMEventAttributes(currFSM), code);
				break;
			case CodeReference.CODE_EDIT_EVENT_ATTRIBUTE:
				ArrayList<String> grab2 = view.getContent(code);
				for(String s : model.getFSMEventAttributes(currFSM)) {
					model.setEventAttribute(currFSM, view.getTextContent(CodeReference.CODE_ACCESS_EDIT_EVENT), s, grab2.contains(s));
				}
				break;
				
		//-- Transitions  -------------------------------------

			case CodeReference.CODE_ADD_TRANSITION:
				addTransition(currFSM, code);
				break;
			case CodeReference.CODE_REMOVE_TRANSITION:
				removeTransition(currFSM, code);
				break;
			case CodeReference.CODE_ADD_EDIT_TRANS_ATTRIBUTE:
				appendSingleChosenAttribute(model.getFSMTransitionAttributes(currFSM), code);
				break;
			case CodeReference.CODE_EDIT_TRANS_ATTRIBUTE:
				ArrayList<String> grab3 = view.getContent(code);
				for(String s : model.getFSMTransitionAttributes(currFSM)) {
					model.setEventAttribute(currFSM, view.getTextContent(CodeReference.CODE_ACCESS_EDIT_TRANS), s, grab3.contains(s));
				}
				break;
				
		//-- Admin  -------------------------------------------
				
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
			case CodeReference.CODE_DUPLICATE_FSM:
				allotFSMToView(model.duplicate(currFSM));
				break;
			case CodeReference.CODE_CLOSE_FSM:
				view.removeFSM(currFSM);
				break;
			default:
				break;
		}
	}
	
	private void codeHandlingOperations(int code, int mouseType) {
		String currFSM = view.getCurrentFSM();
		switch(code) {
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
				allotFSMToView(model.performProduct(noms));
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_PRODUCT_SELECT:
				requestFSMChoice(code);
				break;
			case CodeReference.CODE_PARALLEL_COMPOSITION:
				ArrayList<String> noms2 = view.getContent(code);
				allotFSMToView(model.performParallelComposition(noms2));
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
			default:
				break;
			}
		}
	
	private void codeHandlingUStructure(int code, int mouseType) {
		switch(code) {
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
	}
	
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
	
	private ArrayList<String> addAttributeLists(ArrayList<String> newStuff, ArrayList<String> oldStuff){
		ArrayList<String> use = new ArrayList<String>();
		use.addAll(oldStuff);
		for(String s : newStuff) {
			if(!use.contains(s)) {
				use.add(s);
			}
		}
		return use;
	}
	
	private ArrayList<String> subtractAttributeLists(ArrayList<String> remv, ArrayList<String> oldStuff){
		ArrayList<String> use = new ArrayList<String>();
		for(String s : oldStuff) {
			if(!remv.contains(s)) {
				use.add(s);
			}
		}
		return use;
	}
	
	//-- User Request  ----------------------------------------

	private void appendSingleChosenAttribute(String[] in, int code) {
		String select = view.requestUserInputList(in, true);
		ArrayList<String> use = view.getContent(code);
		if(!use.contains(select)) {
			view.setTextContent(code, use.size(), select);
		}
	}
	
	private void appendSingleChosenAttribute(ArrayList<String> in, int code) {
		String select = view.requestUserInputList(in, true);
		ArrayList<String> use = view.getContent(code);
		if(!use.contains(select)) {
			view.setTextContent(code, use.size(), select);
		}
	}
	
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
					Config.setConfigFileEntry("Diagram/settings/config.txt", DOT_ADDRESS_VAR, FileChooser.promptSelectFile("C:/", true, true).getAbsolutePath());
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
