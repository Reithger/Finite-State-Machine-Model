package controller;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import controller.convert.FormatConversion;
import filemeta.FileChooser;
import filemeta.config.Config;
import model.AttributeList;
import model.Manager;
import ui.FSMUI;
import visual.composite.popout.PopoutAlert;

/*
 * TODO: Auto-load some FSMs on start up (settings menu?) (as an option to the user)
 * TODO: ImagePage is a headache to look at
 * TODO: U-Structure needs re-integrating
 * 
 * TODO: Tooltip popup when hovering over Categories/Entry Sets
 * TODO: Loading Icon should be fancier
 * TODO: Means of having FSM in scope without generating graphviz image (they get too big!)
 * TODO: 0 transition input breaks it for random generation
 * TODO: Transition removal did not work... ugh, need to fix some stuff, make robust, it's iffy
 * 
 * TODO: Manager needs way to export list of Transitions for a FSM, review output formats for that
 * TODO: Manual request by code value for updating the graphviz image, regular update is just info view
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
	
	private final static String SEPARATOR = " - ";
	private final static String SYMBOL_FALSE = "x";
	private final static String SYMBOL_TRUE = "o";
	
	private final static String REGEX_NEWLINE_REPLACE = ",;,";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private FSMUI view;
	private Manager model;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FiniteStateMachine() {
		Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		view = new FSMUI((int)(r.getWidth()), (int)(r.getHeight()), this);
		view.assignSymbols(SEPARATOR, SYMBOL_TRUE, SYMBOL_FALSE);
		model = new Manager();
		FormatConversion.assignPaths(ADDRESS_IMAGES, ADDRESS_CONFIG);
		fileConfiguration();
	}

//---  Operations   ---------------------------------------------------------------------------
	
	//-- Input Handling  --------------------------------------
	
	public void receiveCode(int code, int mouseType) {
		if(code == -1) {
			return;
		}
		view.startLoading();
		codeHandlingAdjustFSM(code, mouseType);
		codeHandlingOperations(code, mouseType);
		codeHandlingUStructure(code, mouseType);
		codeHandlingDisplay(code, mouseType);
		view.endLoading();
	}

	public void receiveKeyInput(char code, int keyType) {
		
	}
	
	public void updateViewFSM(String ref) {
		if(ref == null || !model.hasFSM(ref)) {
			return;
		}
		view.updateFSMInfo(ref, model.getFSMStateAttributes(ref), model.getFSMEventAttributes(ref), model.getFSMTransitionAttributes(ref),
				model.getFSMStateAttributeMap(ref), model.getFSMEventAttributeMap(ref), model.getFSMTransitionAttributeMap(ref));
	}
	
	//-- Input Handling Separation  ---------------------------
	
	private void codeHandlingAdjustFSM(int code, int mouseType) {
		String currFSM = view.getCurrentFSM();
		switch(code) {
		
		//-- Generate FSM  ------------------------------------
				
			case CodeReference.CODE_GENERATE_FSM:
				generateRandomFSM(code);
				updateViewFSM(view.getCurrentFSM());
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
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_FSM_ACCESS_ADD_STATE_ATTRIBUTE:
				appendSingleChosenAttribute(model.getStateAttributeList(), code);
				break;
			case CodeReference.CODE_FSM_ADD_EVENT_ATTRIBUTE:
				model.setFSMEventAttributes(currFSM, addAttributeLists(view.getContent(code), model.getFSMEventAttributes(currFSM)));
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_FSM_ACCESS_ADD_EVENT_ATTRIBUTE:
				appendSingleChosenAttribute(model.getEventAttributeList(), code);
				break;
			case CodeReference.CODE_FSM_ADD_TRANS_ATTRIBUTE:
				model.setFSMTransitionAttributes(currFSM, addAttributeLists(view.getContent(code), model.getFSMTransitionAttributes(currFSM)));
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_FSM_ACCESS_ADD_TRANS_ATTRIBUTE:
				appendSingleChosenAttribute(model.getTransitionAttributeList(), code);
				break;
			case CodeReference.CODE_FSM_REMOVE_STATE_ATTRIBUTE:
				model.setFSMStateAttributes(currFSM, subtractAttributeLists(view.getContent(code), model.getFSMStateAttributes(currFSM)));
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_FSM_ACCESS_REMOVE_STATE_ATTRIBUTE:
				appendSingleChosenAttribute(model.getFSMStateAttributes(currFSM), code);
				break;
			case CodeReference.CODE_FSM_REMOVE_EVENT_ATTRIBUTE:
				model.setFSMEventAttributes(currFSM, subtractAttributeLists(view.getContent(code), model.getFSMEventAttributes(currFSM)));
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_FSM_ACCESS_REMOVE_EVENT_ATTRIBUTE:
				appendSingleChosenAttribute(model.getFSMEventAttributes(currFSM), code);
				break;
			case CodeReference.CODE_FSM_REMOVE_TRANS_ATTRIBUTE:
				model.setFSMTransitionAttributes(currFSM, subtractAttributeLists(view.getContent(code), model.getFSMTransitionAttributes(currFSM)));
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_FSM_ACCESS_REMOVE_TRANS_ATTRIBUTE:
				appendSingleChosenAttribute(model.getFSMTransitionAttributes(currFSM), code);
				break;
				
		//-- States  ------------------------------------------
		
			case CodeReference.CODE_ADD_STATE:
				model.addState(currFSM, view.getTextContent(code));
				view.clearTextContents(code);
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_ADD_STATES:
				Integer numAdd = view.getIntegerContent(code);
				if(numAdd != null) {
					model.addStates(currFSM, numAdd);
					view.clearTextContents(code);
					updateViewFSM(view.getCurrentFSM());
				}
				break;
			case CodeReference.CODE_REMOVE_STATE:
				model.removeState(currFSM, view.getTextContent(code));
				view.clearTextContents(code);
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_RENAME_STATE:
				model.renameState(currFSM, view.getTextContent(code, 0), view.getTextContent(code, 1));
				view.clearTextContents(code);
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_ADD_EDIT_STATE_ATTRIBUTE:
				appendSingleChosenAttribute(model.getFSMStateAttributes(currFSM), code);
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_EDIT_STATE_ATTRIBUTE:
				ArrayList<String> grab = view.getContent(code);
				for(String s : model.getFSMStateAttributes(currFSM)) {
					model.setStateAttribute(currFSM, view.getTextContent(CodeReference.CODE_ACCESS_EDIT_STATE), s, grab.contains(s));
				}
				updateViewFSM(view.getCurrentFSM());
				break;
				

		//-- Events  ------------------------------------------
				
			case CodeReference.CODE_ADD_EVENT:
				model.addEvent(currFSM, view.getTextContent(code));
				view.clearTextContents(code);
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_ADD_EVENTS:
				Integer evenAdd = view.getIntegerContent(code);
				if(evenAdd != null) {
					model.addEvents(currFSM, evenAdd);
					view.clearTextContents(code);
					updateViewFSM(view.getCurrentFSM());
				}
				break;
			case CodeReference.CODE_REMOVE_EVENT:
				model.removeEvent(currFSM, view.getTextContent(code));
				view.clearTextContents(code);
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_RENAME_EVENT:
				model.renameEvent(currFSM, view.getTextContent(code, 0), view.getTextContent(code, 1));
				view.clearTextContents(code);
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_ADD_EDIT_EVENT_ATTRIBUTE:
				appendSingleChosenAttribute(model.getFSMEventAttributes(currFSM), code);
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_EDIT_EVENT_ATTRIBUTE:
				ArrayList<String> grab2 = view.getContent(code);
				for(String s : model.getFSMEventAttributes(currFSM)) {
					model.setEventAttribute(currFSM, view.getTextContent(CodeReference.CODE_ACCESS_EDIT_EVENT), s, grab2.contains(s));
				}
				updateViewFSM(view.getCurrentFSM());
				break;
				
		//-- Transitions  -------------------------------------

			case CodeReference.CODE_ADD_TRANSITION:
				addTransition(currFSM, code);
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_REMOVE_TRANSITION:
				removeTransition(currFSM, code);
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_ADD_EDIT_TRANS_ATTRIBUTE:
				appendSingleChosenAttribute(model.getFSMTransitionAttributes(currFSM), code);
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_EDIT_TRANS_ATTRIBUTE:
				ArrayList<String> grab3 = view.getContent(code);
				for(String s : model.getFSMTransitionAttributes(currFSM)) {
					model.setEventAttribute(currFSM, view.getTextContent(CodeReference.CODE_ACCESS_EDIT_TRANS), s, grab3.contains(s));
				}
				updateViewFSM(view.getCurrentFSM());
				break;
				
		//-- Admin  -------------------------------------------
				
			case CodeReference.CODE_SAVE_FSM:
				saveFSM(currFSM);
				break;
			case CodeReference.CODE_SAVE_IMG:
				view.displayAlert((currFSM == null) ? "Error: No selected FSM" : "Image file saved to: " + generateDotImage(currFSM));
				break;
			case CodeReference.CODE_SAVE_TKZ:
				view.displayAlert((currFSM == null) ? "Error: No selected FSM" : ".tkz file saved to: " + FormatConversion.createTikZFromFSM(model.generateFSMDot(currFSM), currFSM));
				break;
			case CodeReference.CODE_SAVE_SVG:
				view.displayAlert((currFSM == null) ? "Error: No selected FSM" : ".svg file saved to: " + FormatConversion.createSVGFromFSM(model.generateFSMDot(currFSM), currFSM));
				break;
			case CodeReference.CODE_LOAD_SOURCE:
				loadSource();
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_DELETE_SOURCE:
				File remv = new File(ADDRESS_SOURCES + "/" + currFSM + ".fsm");
				remv.delete();
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_DUPLICATE_FSM:
				allotFSMToView(model.duplicate(currFSM));
				updateViewFSM(view.getCurrentFSM());
				break;
			case CodeReference.CODE_CLOSE_FSM:
				model.removeFSM(currFSM);
				view.removeFSM(currFSM);
				updateViewFSM(view.getCurrentFSM());
				break;
			default:
				break;
		}
	}
	
	private void codeHandlingOperations(int code, int mouseType) {
		String currFSM = view.getCurrentFSM();
		String ret = "";
		switch(code) {
			case CodeReference.CODE_TRIM:
				ret = model.trim(currFSM);
				if(ret == null) {
					view.displayAlert((currFSM == null) ? "Error: No selected FSM" : "Error: FSM given to trim did not possess attributes: State - Initial, Marked");
				}
				else {
					allotFSMToView(ret);
				}
				break;
			case CodeReference.CODE_ACCESSIBLE:
				ret = model.makeAccessible(currFSM);
				if(ret == null) {
					view.displayAlert((currFSM == null) ? "Error: No selected FSM" : "Error: FSM given to make accessible did not possess attributes: State - Initial");
				}
				else {
					allotFSMToView(ret);
				}
				break;
			case CodeReference.CODE_CO_ACCESSIBLE:
				ret = model.makeCoAccessible(currFSM);
				if(ret == null) {
					view.displayAlert((currFSM == null) ? "Error: No selected FSM" : "Error: FSM given to make coaccessible did not possess attributes: State - Initial, Marked");
				}
				else {
					allotFSMToView(ret);
				}
				break;
			case CodeReference.CODE_OBSERVER:
				ret = model.buildObserver(currFSM);
				if(ret == null) {
					view.displayAlert((currFSM == null) ? "Error: No selected FSM" : "Error: FSM given to build an Observer did not possess attributes: State - Initial, Event - Observable");
				}
				else {
					allotFSMToView(ret);
				}
				break;
			case CodeReference.CODE_PRODUCT:
				ArrayList<String> noms = view.getContent(code);
				ret = model.performProduct(noms);
				if(ret == null) {
					view.displayAlert((currFSM == null) ? "Error: No selected FSM" : "Error: An FSM given to make Product did not possess attributes: State - Initial");
				}
				else {
					allotFSMToView(ret);
				}
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_PRODUCT_SELECT:
				requestFSMChoice(code);
				break;
			case CodeReference.CODE_PARALLEL_COMPOSITION:
				ArrayList<String> noms2 = view.getContent(code);
				ret = model.performParallelComposition(noms2);
				if(ret == null) {
					view.displayAlert((currFSM == null) ? "Error: No selected FSM" : "Error: An FSM given to make Parallel Composition did not possess attributes: State - Initial");
				}
				else {
					allotFSMToView(ret);
				}
				view.clearTextContents(code);
				break;
			case CodeReference.CODE_PARALLEL_COMPOSITION_SELECT:
				requestFSMChoice(code);
				break;
			case CodeReference.CODE_BLOCKING:
				Boolean res = model.isBlocking(currFSM);
				if(res == null) {
					view.displayAlert((currFSM == null) ? "Error: No selected FSM" : "Error: FSM given to query Blocking did not possess attributes: State - Initial");
				}
				else {
					view.displayAlert((currFSM == null) ? "Error: No selected FSM" : "FSM is " + (res ? "" : "not") + " blocking");
				}
				break;
			case CodeReference.CODE_STATE_EXISTS:
				String chkSt = view.getTextContent(code);
				Boolean res2 = model.stateExists(currFSM, chkSt);
				if(res2 == null) {
					view.displayAlert((currFSM == null) ? "Error: No selected FSM" : "Requisite FSM to query presence of State does not exist");
				}
				else {
					view.displayAlert((currFSM == null) ? "Error: No selected FSM" : "State " + chkSt + " is " + (res2 ? "" : "not") + " in the FSM");
				}
				break;
			default:
				return;
			}
		updateViewFSM(view.getCurrentFSM());
	}
	
	private void codeHandlingUStructure(int code, int mouseType) {
		switch(code) {
			case CodeReference.CODE_SELECT_PLANT:
				requestFSMChoice(code);
				break;
			case CodeReference.CODE_DISPLAY_BAD_TRANS_START:
				ArrayList<String> res = view.requestUserInput("Please provide the bad transition in format (state 1), (event), (state 2)", 3);
				view.setTextContent(CodeReference.CODE_DISPLAY_BAD_TRANS_START, view.getContent(CodeReference.CODE_DISPLAY_BAD_TRANS_START).size(), res.get(0) + SEPARATOR + res.get(1) + SEPARATOR + res.get(2));
				break;
			case CodeReference.CODE_BUILD_AGENTS:
				ArrayList<String> attrib = new ArrayList<String>();
				attrib.add(AttributeList.ATTRIBUTE_OBSERVABLE);
				attrib.add(AttributeList.ATTRIBUTE_CONTROLLABLE);
				ArrayList<String> content = view.getContent(CodeReference.CODE_BUILD_AGENTS);
				for(int i = 0; i < content.size(); i++) {
					content.set(i, content.get(i).replaceAll(REGEX_NEWLINE_REPLACE, "\n"));
				}
				ArrayList<String> agents = view.requestAgentInput(content, model.getFSMEventList(view.getCurrentFSM()), attrib);
				view.clearTextContents(CodeReference.CODE_BUILD_AGENTS);
				for(int i = 0; i < agents.size(); i++) {
					view.setTextContent(CodeReference.CODE_BUILD_AGENTS, i, agents.get(i).replaceAll("\n", REGEX_NEWLINE_REPLACE));
				}
				break;
			case CodeReference.CODE_BUILD_USTRUCT:
				String plant = view.getTextContent(CodeReference.CODE_SELECT_PLANT);
				ArrayList<String> badTrans = view.getContent(CodeReference.CODE_ADD_BAD_TRANS);
				ArrayList<String> agentInfo = view.getContent(CodeReference.CODE_BUILD_AGENTS);
				
				HashMap<String, HashSet<String>> badMap = new HashMap<String, HashSet<String>>();
				
				for(String s : badTrans) {
					String[] line = s.split(SEPARATOR);
					String root = line[0];
					String eve = line[1];
					if(badMap.get(root) == null) {
						badMap.put(root, new HashSet<String>());
					}
					badMap.get(root).add(eve);
				}
				
				boolean[][][] age = new boolean[agentInfo.size()][][];
				for(int i = 0; i < agentInfo.size(); i++) {
					String[] lines = agentInfo.get(i).split(REGEX_NEWLINE_REPLACE);
					age[i] = new boolean[lines.length - 1][lines[1].split(SEPARATOR).length - 1];
					for(int j = 1; j < lines.length; j++) {
						String[] line = lines[j].split(SEPARATOR);
						for(int k = 1; k < line.length; k++) {
							age[i][j-1][k-1] = line[k].contentEquals(SYMBOL_TRUE);
						}
					}
					
				}

				//TODO: Make this dynamically defined, not manually here and in Agent popout screen
				ArrayList<String> attr = new ArrayList<String>();
				attr.add(AttributeList.ATTRIBUTE_OBSERVABLE);
				attr.add(AttributeList.ATTRIBUTE_CONTROLLABLE);
				//TODO: Reintegrate this after getting UI stuff figured out
				//String nom = model.buildUStructure(plant, attr, badMap, age);
				
				boolean display = view.getCheckboxContent(CodeReference.CODE_TOGGLE_USTRUCT);
				break;
			default:
				break;
		}
	}
	
	private void codeHandlingDisplay(int code, int mouseType) {
		switch(code) {
			case CodeReference.CODE_GENERATE_IMAGE:
				String path = generateDotImage(view.getCurrentFSM());
				if(path != null) {
					view.updateFSMImage(view.getCurrentFSM(), path);
				}
				break;
		    default:
			    break;
		}
	}
	
	private void loadSource() {
		view.endLoading();
		String path = view.requestFilePath(ADDRESS_SOURCES, "");
		view.startLoading();
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
		File f = new File(ADDRESS_SOURCES + "/" + currFSM + ".fsm");
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(f, "rw");
			raf.writeBytes(src);
			raf.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println(f.getPath());
		view.displayAlert((currFSM == null) ? "Error: No selected FSM" : "Source file saved to: " + f.getPath());
	}
	
	private void generateRandomFSM(int code) {
		Integer st = view.getIntegerContent(CodeReference.CODE_ACCESS_NUM_STATES);
		Integer ev = view.getIntegerContent(CodeReference.CODE_ACCESS_NUM_EVENTS);
		Integer tr = view.getIntegerContent(CodeReference.CODE_ACCESS_NUM_TRANS);
		if(st != null && ev != null && tr != null) {
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
		view.endLoading();
		String select = view.requestUserInputList(in, true);
		view.startLoading();
		ArrayList<String> use = view.getContent(code);
		if(!use.contains(select)) {
			view.setTextContent(code, use.size(), select);
		}
	}
	
	private void appendSingleChosenAttribute(ArrayList<String> in, int code) {
		view.endLoading();
		String select = view.requestUserInputList(in, true);
		view.startLoading();
		ArrayList<String> use = view.getContent(code);
		if(!use.contains(select)) {
			view.setTextContent(code, use.size(), select);
		}
	}
	
	private void requestAttributeChoice(int code, String[] attributes, String phrase) {
		ArrayList<String> statAttr = view.getContent(code);	//remove existing Attributes from current list
		view.endLoading();
		String use = view.requestUserInputList(attributes, true);
		int num = view.requestUserIntegerInput(phrase);
		view.startLoading();
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
		view.endLoading();
		String choice = view.requestUserInputList(use, true);
		view.startLoading();
		view.setTextContent(code, content.size(), choice);
	}
	
	//-- File Configuration  ----------------------------------
	
	public static void fileConfiguration() {
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
		if(ref == null) {
			return null;
		}
		return FormatConversion.createImgFromFSM(model.generateFSMDot(ref), ref);
	}
	
	private void allotFSMToView(String fsm) {
		if(fsm == null) {
			return;
		}
		updateViewFSM(fsm);
	}
	
}
