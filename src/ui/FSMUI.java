package ui;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

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
	private final static Font OPTIONS_FONT = new Font("Serif", Font.BOLD, 12);
	private final static int CODE_START_OPTIONS_HEADER = 150;
	private final static int CODE_START_IMAGES_HEADER = 150;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** WindowFrame object containing several ElementPanels that provide different services to the user, manages repainting*/
	private WindowFrame frame;
	/** ElementPanel object handling the presentation of images to the user relating to the work they are doing*/
	private ImagePage imagePage;
	
	private OptionPageManager optionPageManager;
	/** ElementPanel object handling the organization and accessing of all currently available images*/
	private ElementPanel imageHeader;
	/** ElementPaenl object handling the organization and accessing of all categories of user tools in optionSpace*/
	private ElementPanel optionHeader;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FSMUI() {
		frame = new WindowFrame(WINDOW_WIDTH, WINDOW_HEIGHT);
		imagePage = new ImagePage();
		optionPageManager = new OptionPageManager();
		createPages();
		initializePanels();
		allotImage("/assets/test_image.jpg");
		allotImage("/assets/test_image2.jpg");
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
	
	private void initializePanels() {
		updateOptionHeader();
		updateActiveOptionPage();
		updateImageHeader();
		updateImagePanel();
	}
	
//---  Operations   ---------------------------------------------------------------------------

	private void allotImage(String path) {
		imagePage.allotImage(path);
		updateImageHeader();
		updateImagePanel();
	}

	private void removeImage(String path) {
		imagePage.removeImage(path);
		updateImageHeader();
		updateImagePanel();
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
	
	private void updateOptionHeader() {
		ElementPanel p = optionHeader;
		p.removeElementPrefixed("header");
		for(int i = 0 ; i < optionPageManager.getOptionPageList().length; i++) {
			int posX = WINDOW_WIDTH / 2/ 10 + i * (WINDOW_WIDTH / 2 / 5);
			int posY = (int)(WINDOW_HEIGHT * (1.0 - PANEL_RATIO_VERTICAL) / 2);
			int wid = WINDOW_WIDTH / 2 / 6;
			int hei = (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL) * 2 / 3);
			p.addRectangle("header_rect_" + i, 10, posX, posY, wid, hei, true, i == optionPageManager.getCurrentOptionPageIndex() ? Color.green : Color.gray);
			p.addButton("header_butt_" + i, 10, posX, posY, wid, hei, CODE_START_OPTIONS_HEADER + i, true);
			p.addText("header_text_" + i, 15, posX, posY, wid, hei, optionPageManager.getOptionPageList()[i].getHeader(), OPTIONS_FONT, true, true, true);
		}
	}

	private void updateImageHeader() {
		ElementPanel p = imageHeader;
		p.removeElementPrefixed("header");
		ArrayList<String> images = imagePage.getImages();
		if(images.size() == 0) {
			int posX = p.getWidth()/ 2;
			int posY = p.getHeight() / 2;
			int wid = p.getWidth() / 3;
			int hei = p.getHeight() * 2 / 3;
			String noHeaderRectName = "header_rect";
			p.addRectangle(noHeaderRectName, 10, posX, posY, wid, hei, true, Color.gray);
			String noHeaderTextName = "header_text";
			p.addText(noHeaderTextName, 15, posX, posY, wid, hei, "No images currently available", OPTIONS_FONT, true, true, true);
		}
		else {
			for(int i = 0 ; i < images.size(); i++) {
				int posX = p.getWidth()/ 10 + i * (p.getWidth() / 5);
				int posY = p.getHeight() / 2;
				int wid = p.getWidth() / 6;
				int hei = p.getHeight() * 2 / 3;
				String headerRectName = "header_rect_" + i;
				p.addRectangle(headerRectName, 10, posX, posY, wid, hei, true, i == imagePage.getCurrentImageHeader() ? Color.green : Color.gray);
				String headerButtName = "header_butt_" + i;
				p.addButton(headerButtName, 10, posX, posY, wid, hei, CODE_START_OPTIONS_HEADER + i, true);
				String headerTextName = "header_text_" + i;
				String nom = images.get(i).substring(images.get(i).lastIndexOf("/") + 1);
				p.addText(headerTextName, 15, posX, posY, wid, hei, nom, OPTIONS_FONT, true, true, true);
			}
		}
	}
	
	private void updateActiveOptionPage() {
		optionPageManager.drawPage();
		updateOptionHeader();
	}

	private void updateImagePanel() {
		imagePage.drawPage();
		updateImageHeader();
	}
	
}
