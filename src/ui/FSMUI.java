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
import filemeta.FileChooser;
import filemeta.config.Config;
import ui.page.headers.ImageHeader;
import ui.page.headers.OptionsHeader;
import ui.page.imagepage.ImagePage;
import ui.page.optionpage.OptionPageManager;
import visual.composite.popout.PopoutAlert;
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
	
	public final static double PANEL_RATIO_VERTICAL = 33 / 35.0;

//---  Instance Variables   -------------------------------------------------------------------
	
	//-- UI  --------------------------------------------------
	
	/** WindowFrame object containing several ElementPanels that provide different services to the user, manages repainting*/
	private WindowFrame frame;
	/** ElementPanel object handling the presentation of images to the user relating to the work they are doing*/
	private ImagePage imagePage;
	/** */
	private OptionPageManager optionPageManager;
	/** ElementPanel object handling the organization and accessing of all currently available images*/
	private ImageHeader imageHeader;
	/** ElementPaenl object handling the organization and accessing of all categories of user tools in optionSpace*/
	private OptionsHeader optionHeader;
	
	//-- System Information  ----------------------------------
	
	private HashMap<String, Image> fsmImgs;
	
	private InputReceiver reference;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FSMUI(int wid, int hei, InputReceiver ref) {
		reference = ref;
		frame = new WindowFrame(wid, hei);
		frame.setName("Finite State Machine Model");
		fsmImgs = new HashMap<String, Image>();
		createPages();
		updateDisplay();
	}
	
	//-- Support  ---------------------------------------------

	private void createPages() {
		frame.reserveWindow("Home");
		frame.showActiveWindow("Home");
		imagePage = new ImagePage();	//TODO: Need to have headers refresh automatically
		optionPageManager = new OptionPageManager(this);
		optionHeader = new OptionsHeader(0, 0, frame.getWidth() / 2, (int)(frame.getHeight() * (1 - PANEL_RATIO_VERTICAL)), optionPageManager);
		imageHeader = new ImageHeader(frame.getWidth() / 2, 0, frame.getWidth() / 2, (int)(frame.getHeight() * (1 - PANEL_RATIO_VERTICAL)), imagePage); 
		frame.addPanelToWindow("Home", "optionHeader", optionHeader);
		frame.addPanelToWindow("Home", "imageHeader", imageHeader);
		frame.addPanelToWindow("Home", "optionSpace", optionPageManager.generateElementPanel(0, (int)(frame.getHeight() * (1 - PANEL_RATIO_VERTICAL)), frame.getWidth() / 2, (int)(frame.getHeight() * PANEL_RATIO_VERTICAL)));
		frame.addPanelToWindow("Home", "imageSpace", imagePage.generateElementPanel(frame.getWidth() / 2, (int)(frame.getHeight() * (1 - PANEL_RATIO_VERTICAL)), frame.getWidth() / 2, (int)(frame.getHeight() * PANEL_RATIO_VERTICAL)));
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void receiveCode(int code, String ref, int mouseType) {
		reference.receiveCode(code, ref, mouseType);
	}
	
	public void receiveKeyInput(char code, String ref, int keyType) {
		reference.receiveKeyInput(code, ref, keyType);
	}
	
	public void addFSM(String ref, Image img) {
		fsmImgs.put(ref, img);
	}
	
	public void removeFSM(String ref) {
		fsmImgs.remove(ref);
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
			optionHeader.update();
	}

	public void updateImageHeader() {
		if(imageHeader != null) 
			imageHeader.update();
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
