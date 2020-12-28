package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

public class FSMUI {

//---  Constants   ----------------------------------------------------------------------------
	
	public final static int WINDOW_WIDTH = 1000;
	public final static int WINDOW_HEIGHT = 600;
	public final static double PANEL_RATIO_VERTICAL = 33 / 35.0;
	
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
	
	private ArrayList<String> fsmPaths;
	
	private ArrayList<FSM> fsms;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FSMUI() {
		fileConfiguration();
		FormatConversion.assignPaths(ADDRESS_SOURCES, ADDRESS_CONFIG);
		frame = new WindowFrame(WINDOW_WIDTH, WINDOW_HEIGHT) {
			@Override
			public void reactToResize() {
				
			}
		};
		frame.setName("Finite State Machine Model");
		fsmPaths = new ArrayList<String>();
		fsms = new ArrayList<FSM>();
		createPages();
		updateDisplay();
	}
	
	//-- Support  ---------------------------------------------

	private void createPages() {
		frame.reserveWindow("Home");
		frame.showActiveWindow("Home");
		imagePage = new ImagePage();	//TODO: Need to have headers refresh automatically
		optionPageManager = new OptionPageManager(this);
		optionHeader = new OptionsHeader(0, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), optionPageManager);
		imageHeader = new ImageHeader(WINDOW_WIDTH / 2, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), imagePage); 
		frame.addPanelToWindow("Home", "optionHeader", optionHeader);
		frame.addPanelToWindow("Home", "imageHeader", imageHeader);
		frame.addPanelToWindow("Home", "optionSpace", optionPageManager.generateElementPanel(0, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * PANEL_RATIO_VERTICAL)));
		frame.addPanelToWindow("Home", "imageSpace", imagePage.generateElementPanel(WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * PANEL_RATIO_VERTICAL)));
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
	
//---  Operations   ---------------------------------------------------------------------------

	//-- In-program Manipulation  -----------------------------
	
	public void allotImage(String path) {
		imagePage.allotImage(path);
		imagePage.increaseCurrentImageIndex();
		updateImageHeader();
		updateImagePanel();
	}
	
	public void allotTransitionSystem(String in, String name) {
		if(in.contains(".fsm") == false) {
			in = in + ".fsm";
		}
		FSM cre = new FSM();
		try {
			cre = new FSM(new File(in), name);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		allotTransitionSystem(cre, name);
	}
	
	public void allotTransitionSystem(FSM in, String name) {
		if(fsmPaths.contains(ADDRESS_SOURCES + name)) {
			removeTransitionSystem(fsmPaths.indexOf(ADDRESS_SOURCES + name));
		}
		in.setId(name);
		fsmPaths.add(ADDRESS_SOURCES + name);
		fsms.add(in);
		in.toTextFile(FSMUI.ADDRESS_SOURCES, name);
		String imgPath = FormatConversion.createImgFromFSM(in, in.getId());
		allotImage(imgPath);
	}
	
	public void removeImage(int index) {
		fsmPaths.remove(index);
		imagePage.removeImage(index);
		imagePage.decreaseCurrentImageIndex();
		updateImageHeader();
		updateImagePanel();
	}
	
	public void removeTransitionSystem(int index) {
		fsms.remove(index);
		removeImage(index);
	}
	
	public void removeActiveTransitionSystem() {
		removeTransitionSystem(imagePage.getCurrentImageIndex());
	}
	
	public void refreshActiveImage() {
		FSM tS = getActiveFSM();
		if(tS != null) {
			FormatConversion.createImgFromFSM(tS, tS.getId());
		}
		imagePage.refreshActiveImage();
	}
		
	//-- File Manipulation  -----------------------------------
	
	public void saveActiveFSMSource() {
		getActiveFSM().toTextFile(FSMUI.ADDRESS_SOURCES, getActiveFSM().getId());
	}
	
	public String saveActiveFSMImage() {
		return FormatConversion.createImgFromFSM(getActiveFSM(), getActiveFSM().getId());
	}
	
	public String saveActiveTikZ() {
		return FormatConversion.createTikZFromFSM(getActiveFSM(), getActiveFSM().getId());
	}
	
	public String saveActiveSVG() {
		return FormatConversion.createSVGFromFSM(getActiveFSM(), getActiveFSM().getId());
	}
	
	public void renameActiveFSM(String newName) {
		deleteFSMFromMemory(newName);
		getActiveFSM().setId(newName);
		getActiveFSM().toTextFile(ADDRESS_SOURCES, newName);
		FormatConversion.createImgFromFSM(getActiveFSM(), getActiveFSM().getId());
		imagePage.replaceActiveImage(newName);
		updateImageHeader();
	}
	
	public void deleteActiveFSM() {
		deleteFSMFromMemory(getActiveFSM().getId());
		removeActiveTransitionSystem();
		refreshActiveImage();
	}
	
	private void deleteFSMFromMemory(String name) {
		File f = new File(ADDRESS_SOURCES + name + ".fsm");
		f.delete();
		f = new File(ADDRESS_IMAGES + name + ".jpg");
		f.delete();
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

//---  Getter Methods   -----------------------------------------------------------------------
	
	public FSM getActiveFSM() {
		if(fsms.size() == 0) {
			return null;
		}
		return fsms.get(imagePage.getCurrentImageIndex());
	}
	
	public ArrayList<String> getFSMList(){
		return fsmPaths;
	}
	public String getFSMPath(int index) {
		return fsmPaths.get(index);
	}
	
	public FSM getFSM(int index) {
		return fsms.get(index);
	}
	
	public int getCurrentActiveFSM() {
		return imagePage.getCurrentImageIndex();
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
