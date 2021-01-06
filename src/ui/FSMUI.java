package ui;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import controller.InputReceiver;
import ui.headers.HeaderSelect;
import ui.page.imagepage.ImagePage;
import ui.page.optionpage.OptionPageManager;
import visual.frame.WindowFrame;

/**
 * 
 * TODO: Auto-load some FSMs on start up (settings menu?) (as an option to the user)
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
		frame = new WindowFrame(wid, hei);
		frame.setName("Finite State Machine Model");
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
		imagePage = new ImagePage(wid / 2, headerHeight, wid / 2, hei - headerHeight);
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
	
	public void receiveCode(int code, int mouseType) {
		if(code - CODE_BASE_OPTIONS_HEADER >= 0 && code - CODE_BASE_OPTIONS_HEADER < optionPageManager.getOptionPageList().length) {
			optionPageManager.setCurrentOptionPageIndex(code - CODE_BASE_OPTIONS_HEADER);
			updateOptionHeader();
		}
		else if(code - CODE_BASE_IMAGE_HEADER >= 0 && code - CODE_BASE_IMAGE_HEADER < imagePage.getImageNames().size()){
			imagePage.setCurrentImageIndex(code - CODE_BASE_IMAGE_HEADER);
			updateImageHeader();
		}
		else {
			reference.receiveCode(code, mouseType);
		}
	}
	
	public void receiveKeyInput(char code, int keyType) {
		reference.receiveKeyInput(code, keyType);
	}
	
	public void addFSM(String ref, String img) {
		imagePage.allotFSM(ref, img);
	}
	
	public void removeFSM(String ref) {
		imagePage.removeFSM(ref);
	}

	public void updateFSMImage(String ref, String img) {
		imagePage.updateFSM(ref, img);
	}
	
	public void clearTextContents(int code) {
		optionPageManager.clearTextContents(code);
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
		updateOptionHeader();
	}

	public void updateImagePanel() {
		if(imagePage == null) {
			return;
		}
		imagePage.drawPage();
		updateImageHeader();
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
		return Integer.parseInt(getTextContent(code));
	}
	
	public Integer getIntegerContent(int code, int posit) {
		return Integer.parseInt(getTextContent(code, posit));
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
