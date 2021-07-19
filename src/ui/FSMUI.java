package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import controller.InputReceiver;
import filemeta.FileChooser;
import ui.page.displaypage.DisplayPageManager;
import ui.page.optionpage.OptionPageManager;
import ui.popups.PopoutAgentSelection;
import ui.popups.PopoutInputRequest;
import visual.composite.popout.PopoutAlert;
import visual.composite.popout.PopoutSelectList;
import visual.frame.WindowFrame;

/**
 * 
 * @author Ada Clevinger
 *
 */

public class FSMUI implements InputHandler{

//---  Constants   ----------------------------------------------------------------------------
	
	//TODO: Let the ratio change for resizing by user if they wanna change the proportion, piggyback reactToResize prolly
	private final static double PANEL_RATIO_VERTICAL = 33 / 35.0;
	private final static String WINDOW_NAME = "Home";
	
	private final static int DEFAULT_POPUP_WIDTH = 400;
	private final static int DEFAULT_POPUP_HEIGHT = 250;

//---  Instance Variables   -------------------------------------------------------------------
	
	//-- UI  --------------------------------------------------
	
	/** WindowFrame object containing several ElementPanels that provide different services to the user, manages repainting*/
	private WindowFrame frame;
	/** ElementPanel object handling the presentation of images to the user relating to the work they are doing*/
	private DisplayPageManager displayPageManager;
	/** */
	private OptionPageManager optionPageManager;
	
	//-- System Information  ----------------------------------
	
	private InputReceiver reference;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FSMUI(int wid, int hei, InputReceiver ref) {
		reference = ref;
		frame = new WindowFrame(wid, hei) {
			@Override
			public void reactToResize() {
				int newWid = getWidth();
				int newHei = getHeight();
				if(displayPageManager != null) {
					int genWid = newWid / 2;
					displayPageManager.updateSizeLoc(genWid, 0, genWid, newHei, PANEL_RATIO_VERTICAL);	

					optionPageManager.updateSizeLoc(0, 0, genWid, newHei, PANEL_RATIO_VERTICAL);
					
					updateDisplay();
				}
			}
		};
		frame.setName("Finite State Machine Model");
		frame.setResizable(true);
		frame.getFrame().setLocation(-8, -3);
		createPages();
		updateDisplay();
	}
	
	//-- Support  ---------------------------------------------

	private void createPages() {
		frame.reserveWindow(WINDOW_NAME);
		frame.showActiveWindow(WINDOW_NAME);
		int wid = frame.getWidth();
		int hei = frame.getHeight();
		
		displayPageManager = new DisplayPageManager(this, wid / 2, 0, wid / 2, hei, PANEL_RATIO_VERTICAL);
		
		
		optionPageManager = new OptionPageManager(this, 0, 0, wid / 2, hei, PANEL_RATIO_VERTICAL);

		
		frame.addPanelToWindow(WINDOW_NAME, "optionHeader", optionPageManager.getHeaderPanel());
		frame.addPanelToWindow(WINDOW_NAME, "imageHeader", displayPageManager.getHeaderPanel());
		frame.addPanelToWindow(WINDOW_NAME, "optionSpace", optionPageManager.getBodyPanel());
		frame.addPanelToWindow(WINDOW_NAME, "imageSpace", displayPageManager.getBodyPanel());
	}

//---  Operations   ---------------------------------------------------------------------------
	
	//-- Input Handling  --------------------------------------
	
	public void receiveCode(int code, int mouseType) {
		int opt = optionPageManager.getCodeReferenceBase();
		int disp = displayPageManager.getCodeReferenceBase();
		if(code - opt >= 0 && code - opt < optionPageManager.getSizePageList()) {
			optionPageManager.setCurrentOptionPageIndex(code - opt);
			updateActiveOptionPage();
		}
		else if(code - disp >= 0 && code - disp < displayPageManager.getSizeDisplayList()){
			displayPageManager.setCurrentDisplayIndex(code - disp);
			updateDisplayPanel();
		}
		else {
			reference.receiveCode(code, mouseType);
		}
	}
	
	public void receiveKeyInput(char code, int keyType) {
		reference.receiveKeyInput(code, keyType);
	}
	
	//-- User Interaction Popups  -----------------------------
	
	public void displayAlert(String text) {
		new PopoutAlert(DEFAULT_POPUP_WIDTH, DEFAULT_POPUP_HEIGHT, text);
	}
	
	public String requestFolderPath(String defDir, String display) {
		return FileChooser.promptSelectFile(defDir, true, false).toString();
	}
	
	public String requestFilePath(String defDir, String display) {
		return FileChooser.promptSelectFile(defDir, true, true).toString();
	}

	public String requestUserInputList(String[] refs, boolean search) {
		PopoutSelectList pL = new PopoutSelectList(250, 200, refs, search);
		String out = pL.getSelected();
		pL.dispose();
		return out;
	}
	
	public String requestUserInputList(ArrayList<String> refs, boolean search) {
		String[] use = new String[refs.size()];
		for(int i = 0; i < use.length; i++) {
			use[i] = refs.get(i);
		}
		return requestUserInputList(use, search);
	}
	
	public String requestUserInput(String phrase) {
		PopoutInputRequest pIR = new PopoutInputRequest(phrase, 1);
		String result = pIR.getSubmitted().get(0);
		pIR.dispose();
		return result;
	}
	
	public ArrayList<String> requestUserInput(String phrase, int size) {
		PopoutInputRequest pIR = new PopoutInputRequest(phrase, size);
		ArrayList<String> result = pIR.getSubmitted();
		pIR.dispose();
		return result;
	}
	
	public Integer requestUserIntegerInput(String phrase) {
		try {
			return Integer.parseInt(requestUserInput(phrase));
		}
		catch(Exception e) {
			return null;
		}
	}

	public ArrayList<String> requestAgentInput(ArrayList<String> inAgents, ArrayList<String> events, ArrayList<String> attrib){
		PopoutAgentSelection pAS = new PopoutAgentSelection(inAgents, events, attrib);
		ArrayList<String> res = pAS.getResult();
		pAS.dispose();
		return res;
	}
	
	//-- Image Page Manipulation  -----------------------------
	
	public void removeFSM(String ref) {
		displayPageManager.removeFSM(ref);
		updateDisplayPanel();
	}

	public void updateFSMInfo(String ref, ArrayList<String> stateAttrib, ArrayList<String> eventAttrib, ArrayList<String> tranAttrib,
				HashMap<String, ArrayList<Boolean>> stateMap, HashMap<String, ArrayList<Boolean>> eventMap, HashMap<String, ArrayList<Boolean>> transMap) {
		displayPageManager.updateFSMInfo(ref, stateAttrib, eventAttrib, tranAttrib, stateMap, eventMap, transMap);
		updateDisplayPanel();
	}
	
	public void updateFSMImage(String ref, String img) {
		displayPageManager.updateFSMImage(ref, img);
	}
	
	//-- Option Page Manipulation  ----------------------------
	
	public void clearTextContents(int code) {
		optionPageManager.clearTextContents(code);
	}
	
	public void startLoading() {
		optionPageManager.startLoading();
	}
	
	public void endLoading() {
		optionPageManager.endLoading();
	}
	
	//-- Update ElementPanels  --------------------------------
	
	public void updateDisplay() {
		updateActiveOptionPage();
		updateDisplayPanel();
	}

	public void updateActiveOptionPage() {
		if(optionPageManager == null) {
			return;
		}
		optionPageManager.drawPage();
	}

	public void updateDisplayPanel() {
		if(displayPageManager == null) {
			return;
		}
		displayPageManager.drawPage();
	}

//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setTextContent(int code, int posit, String ref) {
		optionPageManager.setEntrySetContent(code, posit, ref);
	}
	
	public void assignSymbols(String separator, String tr, String fal) {
		PopoutAgentSelection.assignSymbols(separator, tr, fal);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getCurrentFSM() {
		return displayPageManager.getCurrentFSM();
	}
	
	public String getTextContent(int code) {
		return optionPageManager.getTextContent(code, 0);
	}
	
	public String getTextContent(int code, int posit) {
		return optionPageManager.getTextContent(code, posit);
	}
	
	public Integer getIntegerContent(int code) {
		try {
			return Integer.parseInt(getTextContent(code));
		}
		catch(Exception e) {
			e.printStackTrace();
			displayAlert("Illegal numeric entry for code: " + code + ". Check that an input expecting a number was not given a non-numeric value.");
			return null;
		}
	}
	
	public Integer getIntegerContent(int code, int posit) {
		try {
			return Integer.parseInt(getTextContent(code, posit));
		}
		catch(Exception e) {
			e.printStackTrace();
			displayAlert("Illegal numeric entry for code: " + code + ". Check that an input expecting a number was not given a non-numeric value.");
			return null;
		}
	}
	
	public Boolean getCheckboxContent(int code) {
		return optionPageManager.getCheckboxContent(code);
	}
	
	public ArrayList<String> getContent(int code){
		return optionPageManager.getContent(code);
	}
	
//---  Mechanical   ---------------------------------------------------------------------------
	
	public BufferedReader retrieveFileReader(String pathIn) {
		String path = pathIn.replace("\\", "/");
		InputStream is = FSMUI.class.getResourceAsStream(path); 
		if(is == null) {
			try {
				is = new FileInputStream(new File(path));
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return new BufferedReader(new InputStreamReader(is));
	}
	
	public static String stripPath(String in) {
		String out = in.substring(in.lastIndexOf("/") + 1);
		return out.substring(in.lastIndexOf("\\") + 1);
	}

}
