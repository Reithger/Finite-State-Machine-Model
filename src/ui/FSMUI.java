package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

import fsm.FSM;
import graphviz.GraphViz;
import input.Communication;
import support.meta.FormatConversion;
import ui.page.headers.ImageHeader;
import ui.page.headers.OptionsHeader;
import ui.page.imagepage.ImagePage;
import ui.page.optionpage.OptionPageManager;
import ui.page.popups.PopoutAlert;
import ui.page.popups.PopoutConfig;
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
	private final static String DOT_ADDRESS_VAR = "dotAddress";
	public final static String ADDRESS_SETTINGS = "./Finite State Machine Model/settings/";
	public final static String ADDRESS_IMAGES = "./Finite State Machine Model/images/";
	public final static String ADDRESS_SOURCES = "./Finite State Machine Model/sources/";
	public final static String ADDRESS_CONFIG = ADDRESS_SETTINGS + "/config.txt";
	
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
	
	private volatile String dotAddress;
	
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
		imagePage = new ImagePage(this);
		optionPageManager = new OptionPageManager(this);
		optionHeader = new OptionsHeader(0, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), optionPageManager);
		imageHeader = new ImageHeader(WINDOW_WIDTH / 2, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), imagePage); 
		frame.reservePanel("Home", "optionHeader", optionHeader);
		frame.reservePanel("Home", "imageHeader", imageHeader);
		frame.reservePanel("Home", "optionSpace", optionPageManager.generateElementPanel(0, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * PANEL_RATIO_VERTICAL)));
		frame.reservePanel("Home", "imageSpace", imagePage.generateElementPanel(WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * PANEL_RATIO_VERTICAL)));
	}
	
	//-- File Configuration  ----------------------------------
	
	private void fileConfiguration() {
		File settings = new File(ADDRESS_SETTINGS);
		File images = new File(ADDRESS_IMAGES);
		File source = new File(ADDRESS_SOURCES);
		images.mkdirs();
		source.mkdirs();
		settings.mkdirs();
		File config = new File(ADDRESS_CONFIG);
		if(!config.exists() || !verifyConfigFile(config)){
			createConfigurationFile(config);
		}
		readDirectories(config);
	}
	
	private boolean verifyConfigFile(File f) {
		try {
			Scanner sc = new Scanner(f);
			String line;
			while(sc.hasNextLine()) {
				line = sc.nextLine();
				if(!line.matches("#.*")) { //TODO: This is a bad verification, doesn't extend
					if(!line.matches(DOT_ADDRESS_VAR + " = .*")) {
						sc.close();
						return false;
					}
				}
			}
			sc.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			//TODO: Error window popup
		}
		return true;
	}
	
	private void createConfigurationFile(File config) {
		try {
			if(config.exists()) {
				config.delete();
			}
			config.createNewFile();
			BufferedReader defaultConfig = retrieveFileReader("/assets/config/config.properties");
			RandomAccessFile write = new RandomAccessFile(config, "rw");
			int c = defaultConfig.read();
			while(c != -1) {
				write.write(c);
				c = defaultConfig.read();
			}
			write.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			//TODO: Error window popup
		}
	}
	
	private void readDirectories(File config) {
		try {
			Scanner sc = new Scanner(config);
			String line;
			while(sc.hasNextLine()) {
				line = sc.nextLine();
				if(line.matches(DOT_ADDRESS_VAR + " = .*")) {
					dotAddress = line.substring((DOT_ADDRESS_VAR + " = ").length());
					if(dotAddress.contentEquals("?") || !verifyDotAddress(dotAddress)) {
						PopoutConfig pop = new PopoutConfig();
						while(!verifyDotAddress(dotAddress)) {
							dotAddress = null;
							while(Communication.get("dot") == null) {}
							dotAddress = Communication.get("dot");
							Communication.set("dot", null);
							pop.failure();
						}
						pop.success();
						writeConfigEntry(DOT_ADDRESS_VAR, dotAddress);
					}
				}
			}
			sc.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			//TODO: Error window popup
		}
	}
	
	private void writeConfigEntry(String entry, String contents) {
		try {
			File config = new File(ADDRESS_CONFIG);
			Scanner sc = new Scanner(config);
			StringBuilder sb = new StringBuilder();
			String line;
			while(sc.hasNextLine()) {
				line = sc.nextLine();
				if(line.matches(entry + ".*")) {
					sb.append(entry + " = " + contents + "\n");
				}
				else {
					sb.append(line + "\n");
				}
			}
			sc.close();
			config.delete();
			config.createNewFile();
			RandomAccessFile write = new RandomAccessFile(config, "rw");
			write.writeBytes(sb.toString());
			write.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			//TODO: Error window popup
		}
	}
	
	private boolean verifyDotAddress(String path) {
		return GraphViz.verifyDotPath(path);
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

	public void popupDisplayText(String text) {
		new PopoutAlert(text);
	}
	
}
