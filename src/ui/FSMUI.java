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

import fsm.TransitionSystem;
import ui.page.imagepage.ImagePage;
import ui.page.optionpage.AdjustFSM;
import ui.page.optionpage.OptionPage;
import ui.page.optionpage.OptionPageManager;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class FSMUI {

//---  Constants   ----------------------------------------------------------------------------
	
	public final static int WINDOW_WIDTH = 1000;
	public final static int WINDOW_HEIGHT = 600;
	public final static double PANEL_RATIO_VERTICAL = 33 / 35.0;
	private final static Font HEADER_FONT = new Font("Serif", Font.BOLD, 12);
	private final static int CODE_START_OPTIONS_HEADER = 150;
	private final static int CODE_START_IMAGES_HEADER = 150;
	private final static String OS = System.getProperty("os.name");
	private final static String[] CONFIG_PHRASES = new String[] {
			"tempDirForLinux", "dorForLinux", "tempDirForWindows",
			"dotForWindows", "tempDirForMacOSX", "dorForMacOSX",
	};
	
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
	
	private ArrayList<TransitionSystem> fsms;
	
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FSMUI() {
		frame = new WindowFrame(WINDOW_WIDTH, WINDOW_HEIGHT);
		imagePage = new ImagePage();
		optionPageManager = new OptionPageManager();
		fsms = new ArrayList<TransitionSystem>();
		createPages();
		updateDisplay();
		fileConfiguration();
		allotImage("/assets/test_image.jpg");
	}
	
	//-- Support  ---------------------------------------------
	
	private void fileConfiguration() {
		File settings = new File("./settings/");
		settings.mkdir();
		File config = new File(settings.getAbsolutePath() + "\\config.txt");
		if(!config.exists() || verifyConfigFile(config)){
			try {
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
		//TODO: Existence of a config file (if none, write from stored default path)
		//TODO: Retrieve and assign file directories
		//TODO: Folder for images, text representation of FSM objects
	}
	
	private boolean verifyConfigFile(File f) {
		try {
			Scanner sc = new Scanner(f);
			String line = sc.nextLine();
			while(line != null) {
				if(!line.matches("#.*")) {
					
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			//TODO: Error window popup
		}
		return true;
	}
	
	private void createPages() {
		frame.reserveWindow("Home");
		optionHeader = generateOptionHeader(0, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)));
		imageHeader = generateImageHeader(WINDOW_WIDTH / 2, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)));
		frame.reservePanel("Home", "optionHeader", optionHeader);
		frame.reservePanel("Home", "imageHeader", imageHeader);
		frame.reservePanel("Home", "optionSpace", optionPageManager.generateElementPanel(0, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * PANEL_RATIO_VERTICAL)));
		frame.reservePanel("Home", "imageSpace", imagePage.generateElementPanel(WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * PANEL_RATIO_VERTICAL)));
	}
		
//---  Operations   ---------------------------------------------------------------------------

	public void allotImage(String path) {
		imagePage.allotImage(path);
		updateImageHeader();
		updateImagePanel();
	}

	public void removeImage(int index) {
		fsms.remove(index);
	}
	
	public void removeImage(String path) {
		imagePage.removeImage(path);
		updateImageHeader();
		updateImagePanel();
	}
	
	public void allotTransitionSystem(TransitionSystem in) {
		fsms.add(in);
		//TODO: Write FSM to .png, use file location to allotImage
	}
	
	public void removeTransitionSystem(int index) {
		fsms.remove(index);
	}
	
	//-- Generate ElementPanels  ------------------------------

	private ElementPanel generateOptionHeader(int x, int y, int width, int height) {
		ElementPanel p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				
			}
			
			public void clickBehaviour(int code, int x, int y) {
				System.out.println(code);
				int cod = code - CODE_START_OPTIONS_HEADER;
				if(cod < optionPageManager.getOptionPageList().length && cod >= 0) {
					optionPageManager.setCurrentOptionPageIndex(cod);
					updateActiveOptionPage();
				}
				updateOptionHeader();
			}
		};

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

		p.addLine("line_3", 15, 0, 0, 0, height, 2, Color.BLACK);
		p.addLine("line_6", 15, width, height, 0, height, 5, Color.BLACK);
		return p;
	}
		
	//-- Update ElementPanels  --------------------------------
	
	private void updateDisplay() {
		updateOptionHeader();
		updateImageHeader();
		updateActiveOptionPage();
		updateImagePanel();
	}
	
	private void updateOptionHeader() {
		ElementPanel p = optionHeader;
		if(p == null) {
			return;
		}
		for(int i = 0 ; i < optionPageManager.getOptionPageList().length; i++) {
			int posX = WINDOW_WIDTH / 2/ 10 + i * (WINDOW_WIDTH / 2 / 5);
			int posY = (int)(WINDOW_HEIGHT * (1.0 - PANEL_RATIO_VERTICAL) / 2);
			int wid = WINDOW_WIDTH / 2 / 6;
			int hei = (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL) * 2 / 3);
			if(i == optionPageManager.getCurrentOptionPageIndex()) {
				handleRectangle(p, "header_rect_active", 12, posX, posY, wid, hei, Color.green, Color.black);
			}
			handleRectangle(p, "header_rect_" + i, 10, posX, posY, wid, hei, Color.gray, Color.black);
			handleButton(p, "header_butt_" + i, posX, posY, wid, hei, CODE_START_OPTIONS_HEADER + i);
			handleText(p, "header_text_" + i, posX, posY, wid, hei, optionPageManager.getOptionPageList()[i].getHeader());
		}
	}

	private void updateImageHeader() {
		ElementPanel p = imageHeader;
		if(p == null) {
			return;
		}
		ArrayList<String> images = imagePage.getImages();
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
				String nom = images.get(i).substring(images.get(i).lastIndexOf("/") + 1);
				handleText(p, "header_text_" + i, posX, posY, wid, hei, nom);
			}
		}
	}
	
	private void updateActiveOptionPage() {
		if(optionPageManager == null) {
			return;
		}
		optionPageManager.drawPage();
		updateOptionHeader();
	}

	private void updateImagePanel() {
		if(imagePage == null) {
			return;
		}
		imagePage.drawPage();
		updateImageHeader();
	}

//---  Composites   ---------------------------------------------------------------------------

	private void handleText(ElementPanel p, String nom, int x, int y, int wid, int hei, String phr) {
		if(!p.moveElement(nom, x, y)){
			p.addText(nom, 15, x, y, wid, hei, phr, HEADER_FONT, true, true, true);
		}
	}

	private void handleTextEntry(ElementPanel p, String nom, int x, int y, int wid, int hei, int cod, String phr) {
		if(!p.moveElement(nom, x, y)){
			p.addTextEntry(nom, 15, x, y, wid, hei, cod, phr, HEADER_FONT, true, true, true);
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
