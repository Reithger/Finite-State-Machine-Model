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

import fsm.NonDetObsContFSM;
import fsm.TransitionSystem;
import graphviz.FSMToDot;
import graphviz.GraphViz;
import ui.page.imagepage.ImagePage;
import ui.page.optionpage.OptionPageManager;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class FSMUI {

//---  Constants   ----------------------------------------------------------------------------
	
	public final static int WINDOW_WIDTH = 1000;
	public final static int WINDOW_HEIGHT = 600;
	public final static double PANEL_RATIO_VERTICAL = 33 / 35.0;
	private final static Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);
	private final static Font ENTRY_FONT = new Font("Serif", Font.BOLD, 12);
	private final static Font ERROR_FONT = new Font("Serif", Font.BOLD, 12);
	private final static int CODE_START_OPTIONS_HEADER = 150;
	private final static int CODE_START_IMAGES_HEADER = 150;
	
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
	private ElementPanel imageHeader;
	/** ElementPaenl object handling the organization and accessing of all categories of user tools in optionSpace*/
	private ElementPanel optionHeader;
	
	//-- System Information  ----------------------------------
	
	private ArrayList<String> fsmPaths;
	
	private ArrayList<TransitionSystem> fsms;
	
	private volatile String dotAddress;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FSMUI() {
		fileConfiguration();
		frame = new WindowFrame(WINDOW_WIDTH, WINDOW_HEIGHT);
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
		optionHeader = generateOptionHeader(0, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)));
		imageHeader = generateImageHeader(WINDOW_WIDTH / 2, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)));
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
										handleText(this, "error", getWidth() / 5, getHeight() * 4 / 5, getWidth() / 3, getHeight() / 5, "NOT FOUND");
										handleText(this, "error2", getWidth() * 4 / 5, getHeight() * 4 / 5, getWidth() / 3, getHeight() / 5, "TRY AGAIN");
									}
								}
							}
						};
						entry.reserveWindow("han");
						entry.reservePanel("han", "pan", p);
						handleText(p, "text", p.getWidth() / 2, p.getHeight() / 5, p.getWidth() * 2 / 3, p.getHeight() / 2, "Please submit the system path for the GraphViz dot.exe");
						
						handleRectangle(p, "rec", 5, p.getWidth() / 2, p.getHeight() /2, p.getWidth() * 4 / 5, p.getHeight() / 5, Color.white, Color.black);
						handleTextEntry(p, "entry", p.getWidth() / 2, p.getHeight() /2, p.getWidth() * 4 / 5, p.getHeight() / 5, 20, "C://");
						
						handleRectangle(p, "rec2", 5, p.getWidth() / 2, p.getHeight() * 4 / 5, p.getWidth() / 4, p.getHeight() / 5, Color.white, Color.black);
						handleText(p, "text2", p.getWidth() / 2, p.getHeight() * 4 / 5, p.getWidth() / 4, p.getHeight() / 5, "Submit");
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

	public void removeImage(int index) {
		fsmPaths.remove(index);
		imagePage.removeImage(index);
		imagePage.decreaseCurrentImageIndex();
		updateImageHeader();
		updateImagePanel();
	}
	
	public void removeImage(String path) {
		imagePage.removeImage(path);
		imagePage.decreaseCurrentImageIndex();
		updateImageHeader();
		updateImagePanel();
	}
	
	public void allotTransitionSystem(String in, String name) {
		fsmPaths.add(in);
		NonDetObsContFSM cre = new NonDetObsContFSM(new File(in), name);
		fsms.add(cre);
		String imgPath = FSMToDot.createImgFromFSM(cre, ADDRESS_IMAGES + cre.getId(), ADDRESS_IMAGES, ADDRESS_CONFIG);
		allotImage(imgPath);
	}
	
	public void allotTransitionSystem(TransitionSystem in, String name) {
		in.setId(name);
		fsmPaths.add(ADDRESS_SOURCES + "/" + name);
		fsms.add(in);
		String imgPath = FSMToDot.createImgFromFSM(in, ADDRESS_IMAGES + in.getId(), ADDRESS_IMAGES, ADDRESS_CONFIG);
		allotImage(imgPath);
	}
	
	public void removeTransitionSystem(int index) {
		fsms.remove(index);
		removeImage(index);
	}
	
	public void removeActiveTransitionSystem() {
		removeTransitionSystem(imagePage.getCurrentImageIndex());
	}
	
	public void refreshActiveImage() {
		TransitionSystem tS = getActiveFSM();
		if(tS != null) {
			FSMToDot.createImgFromFSM(tS, ADDRESS_IMAGES + tS.getId(), ADDRESS_IMAGES, ADDRESS_CONFIG);
		}
		imagePage.refreshImage();
	}
	
	//-- Generate ElementPanels  ------------------------------

	private ElementPanel generateOptionHeader(int x, int y, int width, int height) {
		ElementPanel p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				
			}
			
			public void clickBehaviour(int code, int x, int y) {
				int cod = code - CODE_START_OPTIONS_HEADER;
				if(cod < optionPageManager.getOptionPageList().length && cod >= 0) {
					optionPageManager.setCurrentOptionPageIndex(cod);
					updateActiveOptionPage();
				}
				updateOptionHeader();
			}
		};
		p.setScrollBarVertical(false);
		p.addLine("line_5", 15, width, height, width, 0, 2, Color.BLACK);
		p.addLine("line_6", 15, width, height, 0, height, 5, Color.BLACK);
		return p;
	}
	
	private ElementPanel generateImageHeader(int x, int y, int width, int height) {
		ElementPanel p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				
			}
			
			public void clickBehaviour(int code, int x, int y) {
				int cod = code - CODE_START_IMAGES_HEADER;
				if(cod < imagePage.getImages().size() && cod >= 0) {
					imagePage.setCurrentImageIndex(cod);
					updateImagePanel();
				}
				updateImageHeader();
			}
		};
		p.setScrollBarVertical(false);
		p.addLine("line_3", 15, 0, 0, 0, height, 2, Color.BLACK);
		p.addLine("line_6", 15, width, height, 0, height, 5, Color.BLACK);
		return p;
	}
		
	//-- Update ElementPanels  --------------------------------
	
	public void updateDisplay() {
		updateOptionHeader();
		updateImageHeader();
		updateActiveOptionPage();
		updateImagePanel();
	}
	
	public void updateOptionHeader() {
		ElementPanel p = optionHeader;
		if(p == null) {
			return;
		}
		for(int i = 0 ; i < optionPageManager.getOptionPageList().length; i++) {
			int posX = WINDOW_WIDTH / 2/ 8 + i * (WINDOW_WIDTH / 2 / 4);
			int posY = (int)(WINDOW_HEIGHT * (1.0 - PANEL_RATIO_VERTICAL) / 2);
			int wid = WINDOW_WIDTH / 2 / 5;
			int hei = (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL) * 2 / 3);
			if(i == optionPageManager.getCurrentOptionPageIndex()) {
				handleRectangle(p, "header_rect_active", 12, posX, posY, wid, hei, Color.green, Color.black);
			}
			handleRectangle(p, "header_rect_" + i, 10, posX, posY, wid, hei, Color.gray, Color.black);
			handleButton(p, "header_butt_" + i, posX, posY, wid, hei, CODE_START_OPTIONS_HEADER + i);
			handleText(p, "header_text_" + i, posX, posY, wid, hei, optionPageManager.getOptionPageList()[i].getHeader());
		}
	}

	public void updateImageHeader() {
		ElementPanel p = imageHeader;
		if(p == null) {
			return;
		}
		ArrayList<String> images = imagePage.getImages();
		p.removeElementPrefixed("header");
		if(images.size() == 0) {
			int posX = p.getWidth()/ 2;
			int posY = p.getHeight() / 2;
			int wid = p.getWidth() / 3;
			int hei = p.getHeight() * 2 / 3;
			handleRectangle(p, "stand_in_header_rect", 10, posX, posY, wid, hei, Color.gray, Color.black);
			handleText(p, "stand_in_header_text", posX, posY, wid, hei, "No images currently available");
		}
		else {
			p.removeElementPrefixed("stand_in");
			for(int i = 0 ; i < images.size(); i++) {
				int posX = WINDOW_WIDTH / 2/ 10 + i * (WINDOW_WIDTH / 2 / 5);
				int posY = (int)(WINDOW_HEIGHT * (1.0 - PANEL_RATIO_VERTICAL) / 2);
				int wid = WINDOW_WIDTH / 2 / 6;
				int hei = (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL) * 2 / 3);
				if(i == imagePage.getCurrentImageIndex()) {
					handleRectangle(p, "header_rect_active", 12, posX, posY, wid, hei, Color.green, Color.black);
				}
				handleRectangle(p, "header_rect_" + i, 10, posX, posY, wid, hei, Color.gray, Color.black);
				handleButton(p, "header_butt_" + i, posX, posY, wid, hei, CODE_START_OPTIONS_HEADER + i);
				String nom = images.get(i).substring(images.get(i).lastIndexOf("\\") + 1);
				handleText(p, "header_text_" + i, posX, posY, wid, hei, nom);
			}
		}
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

	private void handleText(ElementPanel p, String nom, int x, int y, int wid, int hei, String phr) {
		if(!p.moveElement(nom, x, y)){
			p.addText(nom, 15, x, y, wid, hei, phr, DEFAULT_FONT, true, true, true);
		}
	}

	private void handleTextEntry(ElementPanel p, String nom, int x, int y, int wid, int hei, int cod, String phr) {
		if(!p.moveElement(nom, x, y)){
			p.addTextEntry(nom, 15, x, y, wid, hei, cod, phr, ENTRY_FONT, true, true, true);
		}
	}
	
	private void handleButton(ElementPanel p, String nom, int x, int y, int wid, int hei, int code) {
		if(!p.moveElement(nom, x, y)) {
			p.addButton(nom, 10, x, y, wid, hei, code, true);
		}
	}
	
	private void handleLine(ElementPanel p, String nom, int x, int y, int x2, int y2, int thck, Color col) {
		if(!p.moveElement(nom, x, y)) {
			p.addLine(nom, 5, x, y, x2, y2, thck, col);
		}
	}
	
	private void handleRectangle(ElementPanel p, String nom, int prior, int x, int y, int wid, int hei, Color col, Color col2) {
		if(!p.moveElement(nom, x, y)) {
			p.addRectangle(nom, prior, x, y, wid, hei, true, col, col2);
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
	
}
