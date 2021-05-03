package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import controller.InputReceiver;
import filemeta.FileChooser;
import ui.headers.HeaderSelect;
import ui.page.imagepage.ImagePage;
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

public class FSMUI implements InputReceiver{

//---  Constants   ----------------------------------------------------------------------------
	
	private final static double PANEL_RATIO_VERTICAL = 33 / 35.0;
	private final static String WINDOW_NAME = "Home";
	private final static int CODE_BASE_OPTIONS_HEADER = 3500;
	private final static int CODE_BASE_IMAGE_HEADER = 4000;
	
	private final static int DEFAULT_POPUP_WIDTH = 400;
	private final static int DEFAULT_POPUP_HEIGHT = 250;

//---  Instance Variables   -------------------------------------------------------------------
	
	//-- UI  --------------------------------------------------
	
	/** WindowFrame object containing several ElementPanels that provide different services to the user, manages repainting*/
	private WindowFrame frame;
	/** ElementPanel object handling the presentation of images to the user relating to the work they are doing*/
	private ImagePage imagePage;
	/** */
	private OptionPageManager optionPageManager;
	/** ElementPanel object handling the organization and accessing of all currently available images*/
	private HeaderSelect imageHeader;
	/** ElementPaenl object handling the organization and accessing of all categories of user tools in optionSpace*/
	private HeaderSelect optionHeader;
	
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
				if(imageHeader != null) {
					int topHei = (int)(newHei * (1 - PANEL_RATIO_VERTICAL));
					int genWid = newWid / 2;
					imageHeader.updateSize(genWid, 0, genWid, topHei);
					optionHeader.updateSize(0, 0, genWid, topHei);

					imagePage.updateSize(genWid, topHei, genWid, newHei - topHei);
					optionPageManager.updateSize(0, topHei, genWid, newHei - topHei);
					
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
		int headerHeight = (int)(hei * (1 - PANEL_RATIO_VERTICAL));
		imagePage = new ImagePage(wid / 2, headerHeight, wid / 2, hei - headerHeight, this);
		optionPageManager = new OptionPageManager(this, 0, headerHeight, wid / 2, hei - headerHeight);
		optionHeader = new HeaderSelect(0, 0, wid / 2, headerHeight, CODE_BASE_OPTIONS_HEADER);
		imageHeader = new HeaderSelect(wid / 2, 0, wid / 2, headerHeight, CODE_BASE_IMAGE_HEADER); 
		
		imageHeader.setInputReceiver(this);
		optionHeader.setInputReceiver(this);
		
		frame.addPanelToWindow(WINDOW_NAME, "optionHeader", optionHeader);
		frame.addPanelToWindow(WINDOW_NAME, "imageHeader", imageHeader);
		frame.addPanelToWindow(WINDOW_NAME, "optionSpace", optionPageManager.getPanel());
		frame.addPanelToWindow(WINDOW_NAME, "imageSpace", imagePage.getPanel());
	}

//---  Operations   ---------------------------------------------------------------------------
	
	//-- Input Handling  --------------------------------------
	
	public void receiveCode(int code, int mouseType) {
		if(code - CODE_BASE_OPTIONS_HEADER >= 0 && code - CODE_BASE_OPTIONS_HEADER < optionPageManager.getOptionPageList().length) {
			optionPageManager.setCurrentOptionPageIndex(code - CODE_BASE_OPTIONS_HEADER);
			updateOptionHeader();
			updateActiveOptionPage();
		}
		else if(code - CODE_BASE_IMAGE_HEADER >= 0 && code - CODE_BASE_IMAGE_HEADER < imagePage.getImageNames().size()){
			imagePage.setCurrentImageIndex(code - CODE_BASE_IMAGE_HEADER);
			updateImageHeader();
			updateImagePanel();
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
	
	public void addFSM(String ref, String img) {
		imagePage.allotFSM(ref, img);
		updateImageHeader();
		updateImagePanel();
	}
	
	public void removeFSM(String ref) {
		imagePage.removeFSM(ref);
		updateImageHeader();
		updateImagePanel();
	}

	public void updateFSMImage(String ref, String img) {
		imagePage.updateFSM(ref, img);
		updateImageHeader();
		updateImagePanel();
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
		updateOptionHeader();
		updateImageHeader();
		updateActiveOptionPage();
		updateImagePanel();
	}
	
	public void updateOptionHeader() {
		if(optionHeader != null)
			optionHeader.update(optionPageManager.getOptionPageNames(), optionPageManager.getCurrentOptionPageIndex());
	}

	public void updateImageHeader() {
		if(imageHeader != null) 
			imageHeader.update(imagePage.getImageNames(), imagePage.getCurrentImageIndex());
	}
	
	public void updateActiveOptionPage() {
		if(optionPageManager == null) {
			return;
		}
		optionPageManager.drawPage();
	}

	public void updateImagePanel() {
		if(imagePage == null) {
			return;
		}
		imagePage.drawPage();
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
		return imagePage.getCurrentFSM();
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
