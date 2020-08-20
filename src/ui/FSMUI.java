package ui;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

import fsm.DetObsContFSM;
import fsm.ModalSpecification;
import fsm.NonDetObsContFSM;
import fsm.TransitionSystem;
import graphviz.FSMToDot;
import graphviz.GraphViz;
import ui.page.headers.ImageHeader;
import ui.page.headers.OptionsHeader;
import ui.page.imagepage.ImagePage;
import ui.page.optionpage.OptionPageManager;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

/**
 * 
 * TODO: Display type of TransitionSystem, as much as possible anyways, backend needs some rearranging but type is important right now
 * TODO: Factory for reading in files and generating the correct type of TransitionSystem
 * TODO: Add States (can add like 15 really quickly)
 * TODO: Convert to .tkz
 * TODO: Rename states
 * TODO: Adding a 'new' fsm of the same name should replace the previous one to avoid collision issue (especially image display)
 * 
 * @author Borinor
 *
 */

public class FSMUI {

//---  Constants   ----------------------------------------------------------------------------
	
	public final static int WINDOW_WIDTH = 1000;
	public final static int WINDOW_HEIGHT = 600;
	public final static double PANEL_RATIO_VERTICAL = 33 / 35.0;
	private final static Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);
	private final static Font IMAGE_HEADER_FONT = new Font("Serif", Font.BOLD, 12);
	private final static Font ENTRY_FONT = new Font("Serif", Font.BOLD, 12);
	private final static Font ERROR_FONT = new Font("Serif", Font.BOLD, 12);
	private final static int DEFAULT_POPUP_WIDTH = 300;
	private final static int DEFAULT_POPUP_HEIGHT = 200;
	private final static int ROTATION_MULTIPLIER = 6;	//TODO: Extract some of this into its own class, lots of stuff going on here
	private final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
	
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
	
	private ArrayList<TransitionSystem> fsms;
	
	private volatile String dotAddress;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FSMUI() {
		fileConfiguration();
		frame = new WindowFrame(WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setName("Finite State Machine Model");
		imagePage = new ImagePage(this);
		optionPageManager = new OptionPageManager(this);
		fsmPaths = new ArrayList<String>();
		fsms = new ArrayList<TransitionSystem>();
		createPages();
		updateDisplay();
	}
	
	//-- Support  ---------------------------------------------

	private void createPages() {
		frame.reserveWindow("Home");
		optionHeader = new OptionsHeader(0, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)));
		imageHeader = new ImageHeader(WINDOW_WIDTH / 2, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL))); 
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
						dotAddress = null;
						
						WindowFrame entry = new WindowFrame(400, 150);
						ElementPanel p = new ElementPanel(0, 0, 400, 150) {
							public void clickBehaviour(int event, int x, int y) {
								if(event == 25) {
									String addr = this.getElementStoredText("entry");
									if(verifyDotAddress(addr)) {
										dotAddress = addr;
										entry.disposeFrame();
									}
									else {
										handleText(this, "error", getWidth() / 5, getHeight() * 4 / 5, getWidth() / 3, getHeight() / 5, DEFAULT_FONT, "NOT FOUND");
										handleText(this, "error2", getWidth() * 4 / 5, getHeight() * 4 / 5, getWidth() / 3, getHeight() / 5, DEFAULT_FONT, "TRY AGAIN");
									}
								}
							}
						};
						entry.reserveWindow("han");
						entry.reservePanel("han", "pan", p);
						handleText(p, "text", p.getWidth() / 2, p.getHeight() / 5, p.getWidth() * 2 / 3, p.getHeight() / 2, DEFAULT_FONT, "Please submit the system path for the GraphViz dot.exe");
						
						handleRectangle(p, "rec", 5, p.getWidth() / 2, p.getHeight() /2, p.getWidth() * 4 / 5, p.getHeight() / 5, Color.white, Color.black);
						handleTextEntry(p, "entry", p.getWidth() / 2, p.getHeight() /2, p.getWidth() * 4 / 5, p.getHeight() / 5, 20, "C://");
						
						handleRectangle(p, "rec2", 5, p.getWidth() / 2, p.getHeight() * 4 / 5, p.getWidth() / 4, p.getHeight() / 5, Color.white, Color.black);
						handleText(p, "text2", p.getWidth() / 2, p.getHeight() * 4 / 5, p.getWidth() / 4, p.getHeight() / 5, DEFAULT_FONT, "Submit");
						handleButton(p, "but", p.getWidth() / 2, p.getHeight() * 4 / 5, p.getWidth() / 4, p.getHeight() / 5, 25);
						
						p.setScrollBarVertical(false);
						
						while(dotAddress == null || !(new File(dotAddress).exists())) {}
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
		TransitionSystem cre = new ModalSpecification();;
		try {
			cre = new ModalSpecification(new File(in), name);
		}
		catch(Exception e) {
			try {
				cre = new NonDetObsContFSM(new File(in), name);
			}
			catch(Exception e1) {
				try {
					cre = new DetObsContFSM(new File(in), name);
				}
				catch(Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		cre.toTextFile(FSMUI.ADDRESS_SOURCES, name);
		fsmPaths.add(in);
		fsms.add(cre);
		String imgPath = FSMToDot.createImgFromFSM(cre, ADDRESS_IMAGES + cre.getId(), ADDRESS_IMAGES, ADDRESS_CONFIG);
		allotImage(imgPath);
	}
	
	public void allotTransitionSystem(TransitionSystem in, String name) {
		in.setId(name);
		fsmPaths.add(ADDRESS_SOURCES + name);
		fsms.add(in);
		in.toTextFile(FSMUI.ADDRESS_SOURCES, name);
		String imgPath = FSMToDot.createImgFromFSM(in, ADDRESS_IMAGES + in.getId(), ADDRESS_IMAGES, ADDRESS_CONFIG);
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
	
	public void saveActiveFSMSource() {
		getActiveFSM().toTextFile(FSMUI.ADDRESS_SOURCES, getActiveFSM().getId());
	}
	
	public void saveActiveFSMImage() {
		FSMToDot.createImgFromFSM(getActiveFSM(), FSMUI.ADDRESS_IMAGES + getActiveFSM().getId(), FSMUI.ADDRESS_IMAGES, FSMUI.ADDRESS_CONFIG);
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
	
	public void renameActiveFSM(String newName) {
		deleteFSMFromMemory(newName);
		getActiveFSM().setId(newName);
		getActiveFSM().toTextFile(ADDRESS_SOURCES, newName);
		FSMToDot.createImgFromFSM(getActiveFSM(), ADDRESS_IMAGES + getActiveFSM().getId(), ADDRESS_IMAGES, ADDRESS_CONFIG);
		imagePage.replaceActiveImage(newName);
		updateImageHeader();
	}
	
	public void refreshActiveImage() {
		TransitionSystem tS = getActiveFSM();
		if(tS != null) {
			FSMToDot.createImgFromFSM(tS, ADDRESS_IMAGES + tS.getId(), ADDRESS_IMAGES, ADDRESS_CONFIG);
		}
		imagePage.refreshActiveImage();
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
	
	public TransitionSystem getActiveFSM() {
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
	
	public TransitionSystem getFSM(int index) {
		return fsms.get(index);
	}
	
	public int getCurrentActiveFSM() {
		return imagePage.getCurrentImageIndex();
	}
	
//---  Composites   ---------------------------------------------------------------------------

	private void handleText(ElementPanel p, String nom, int x, int y, int wid, int hei, Font font, String phr) {
		if(!p.moveElement(nom, x, y)){
			p.addText(nom, 15, false, x, y, wid, hei, phr, font, true, true, true);
		}
	}

	private void handleTextEntry(ElementPanel p, String nom, int x, int y, int wid, int hei, int cod, String phr) {
		if(!p.moveElement(nom, x, y)){
			p.addTextEntry(nom, 15, false, x, y, wid, hei, cod, phr, ENTRY_FONT, true, true, true);
		}
	}
	
	private void handleButton(ElementPanel p, String nom, int x, int y, int wid, int hei, int code) {
		if(!p.moveElement(nom, x, y)) {
			p.addButton(nom, 10, false, x, y, wid, hei, code, true);
		}
	}
	
	private void handleLine(ElementPanel p, String nom, int x, int y, int x2, int y2, int thck, Color col) {
		if(!p.moveElement(nom, x, y)) {
			p.addLine(nom, 5, false, x, y, x2, y2, thck, col);
		}
	}
	
	private void handleRectangle(ElementPanel p, String nom, int prior, int x, int y, int wid, int hei, Color col, Color col2) {
		if(!p.moveElement(nom, x, y)) {
			p.addRectangle(nom, prior, false, x, y, wid, hei, true, col, col2);
		}
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
		WindowFrame popup = new WindowFrame(DEFAULT_POPUP_WIDTH, DEFAULT_POPUP_HEIGHT);
		popup.setName("Popup Window");
		popup.setExitOnClose(false);
		ElementPanel p = new ElementPanel(0, 0, DEFAULT_POPUP_WIDTH, DEFAULT_POPUP_HEIGHT) {
			@Override
			public void clickBehaviour(int code, int x, int y){
				popup.disposeFrame();
			}
		};
		p.addText("tex", 5, false, p.getWidth() /2, p.getHeight() / 2, p.getWidth() * 2 / 3, p.getHeight() * 3 / 4, text, DEFAULT_FONT, true, true, true);
		popup.reservePanel("default",  "pan", p);
	}
	
}
