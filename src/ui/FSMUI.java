package ui;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import ui.optionpage.AdjustFSM;
import ui.optionpage.OptionPage;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class FSMUI {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static int WINDOW_WIDTH = 1000;
	private final static int WINDOW_HEIGHT = 600;
	private final static double PANEL_RATIO_VERTICAL = 33 / 35.0;
	private final static Font OPTIONS_FONT = new Font("Serif", Font.BOLD, 12);
	private final static int CODE_START_OPTIONS_HEADER = 150;
	private final static int CODE_START_IMAGES_HEADER = 150;
	private final static int HEADER_EDIT = 0;
	public final static OptionPage[] OPTION_PAGES = new OptionPage[] {
			new AdjustFSM(0, 0, WINDOW_WIDTH/2, (int)(WINDOW_HEIGHT * PANEL_RATIO_VERTICAL)),
			new AdjustFSM(0, 0, WINDOW_WIDTH/2, (int)(WINDOW_HEIGHT * PANEL_RATIO_VERTICAL)),
	};
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** Window needs to have tabs for options menus, multiple images*/
	private WindowFrame frame;
	
	private ElementPanel imageSpace;
	
	private ElementPanel imageHeader;
	
	private ElementPanel optionSpace;
	
	private ElementPanel optionHeader;

	private ArrayList<String> images;
	
	private int currentOptionHeader;
	
	private int currentImageHeader;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FSMUI() {
		frame = new WindowFrame(WINDOW_WIDTH, WINDOW_HEIGHT);
		currentOptionHeader = this.HEADER_EDIT;
		images = new ArrayList<String>();
		createElementPanels();
		assignPanels();
		assignOptionPages();
		initializePanels();
	}
	
	//-- Support  ---------------------------------------------
	
	private void createElementPanels() {
		optionHeader = generateOptionHeader(0, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)));
		imageHeader = generateImageHeader(WINDOW_WIDTH / 2, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)));
		optionSpace = generateOptionPanel(0, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * PANEL_RATIO_VERTICAL));
		imageSpace = generateImagePanel(WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * PANEL_RATIO_VERTICAL));
	}
	
	private void assignPanels() {
		frame.reserveWindow("Home");
		frame.reservePanel("Home", "optionHeader", optionHeader);
		frame.reservePanel("Home", "imageHeader", imageHeader);
		frame.reservePanel("Home", "optionSpace", optionSpace);
		frame.reservePanel("Home", "imageSpace", imageSpace);
	}
	
	private void assignOptionPages() {
		for(OptionPage oP : OPTION_PAGES) {
			oP.assignElementPanel(optionSpace);
		}
	}
	
	private void initializePanels() {
		updateOptionHeader();
		updateActiveOptionPage();
		updateImageHeader();
		updateImagePanel();
	}
	
//---  Operations   ---------------------------------------------------------------------------

	private void allotImage(String path) {
		images.add(path);
		updateImageHeader();
	}

	private void removeImage(String path) {
		images.remove(path);
		updateImageHeader();
	}
	
	//-- Generate ElementPanels  ------------------------------
	
	/**
	 * Able to navigate the image: zoom in/out, move around, etc.
	 * 
	 * @param path
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */

	private ElementPanel generateOptionHeader(int x, int y, int width, int height) {
		ElementPanel p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				
			}
			
			public void clickBehaviour(int code, int x, int y) {
				int cod = code - CODE_START_OPTIONS_HEADER;
				if(cod < OPTION_PAGES.length && cod >= 0) {
					currentOptionHeader = cod;
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
				if(cod < images.size() && cod >= 0) {
					currentImageHeader = cod;
					updateImagePanel();
				}
				updateImageHeader();
			}
		};

		p.addLine("line_3", 15, 0, 0, 0, height, 2, Color.BLACK);
		p.addLine("line_6", 15, width, height, 0, height, 5, Color.BLACK);
		return p;
	}
	
	private ElementPanel generateOptionPanel(int x, int y, int width, int height) {
		ElementPanel p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				System.out.println(getFocusElement() + " " + code);
			}
			
			public void clickBehaviour(int code, int x, int y) {
				OPTION_PAGES[currentOptionHeader].applyCode(code);
			}
		};
		return p;
	}
	
	private ElementPanel generateImagePanel(int x, int y, int width, int height) {
		ElementPanel p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				
			}
			
			public void clickBehaviour(int code, int x, int y) {
				
			}
		};
		addFraming(p);
		
		return p;
	}

	//-- Update ElementPanels  --------------------------------
	
	private void updateOptionHeader() {
		ElementPanel p = optionHeader;
		p.removeElementPrefixed("header");
		for(int i = 0 ; i < OPTION_PAGES.length; i++) {
			int posX = WINDOW_WIDTH / 2/ 10 + i * (WINDOW_WIDTH / 2 / 5);
			int posY = (int)(WINDOW_HEIGHT * (1.0 - PANEL_RATIO_VERTICAL) / 2);
			int wid = WINDOW_WIDTH / 2 / 6;
			int hei = (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL) * 2 / 3);
			p.addRectangle("header_rect_" + i, 10, posX, posY, wid, hei, true, i == currentOptionHeader ? Color.green : Color.gray);
			p.addButton("header_butt_" + i, 10, posX, posY, wid, hei, CODE_START_OPTIONS_HEADER + i, true);
			p.addText("header_text_" + i, 15, posX, posY, wid, hei, OPTION_PAGES[i].getHeader(), OPTIONS_FONT, true, true, true);
		}
	}
	
	private void updateImageHeader() {
		ElementPanel p = imageHeader;
		p.removeElementPrefixed("header");
		if(images.size() == 0) {
			int posX = WINDOW_WIDTH / 2/ 2;
			int posY = (int)(WINDOW_HEIGHT * (1.0 - PANEL_RATIO_VERTICAL) / 2);
			int wid = WINDOW_WIDTH / 2 / 3;
			int hei = (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL) * 2 / 3);
			p.addRectangle("header_rect", 10, posX, posY, wid, hei, true, Color.gray);
			p.addText("header_text", 15, posX, posY, wid, hei, "No images currently available", OPTIONS_FONT, true, true, true);
		}
		else {
			for(int i = 0 ; i < images.size(); i++) {
				int posX = WINDOW_WIDTH / 2/ 10 + i * (WINDOW_WIDTH / 2 / 5);
				int posY = (int)(WINDOW_HEIGHT * (1.0 - PANEL_RATIO_VERTICAL) / 2);
				int wid = WINDOW_WIDTH / 2 / 6;
				int hei = (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL) * 2 / 3);
				p.addRectangle("header_rect_" + i, 10, posX, posY, wid, hei, true, i == currentImageHeader ? Color.green : Color.gray);
				p.addButton("header_butt_" + i, 10, posX, posY, wid, hei, CODE_START_OPTIONS_HEADER + i, true);
				p.addText("header_text_" + i, 15, posX, posY, wid, hei, images.get(i), OPTIONS_FONT, true, true, true);
			}
		}
	}
	
	private void updateImagePanel() {

	}
	
	private void updateActiveOptionPage() {
		OPTION_PAGES[currentOptionHeader].drawPage();
	}

	//-- Composite  -------------------------------------------

	private void addFraming(ElementPanel p) {
		int width = p.getWidth();
		int height = p.getHeight();
		p.addLine("line_1", 15, 0, 0, width, height, 5, Color.BLACK);
		p.addLine("line_2", 15, width, 0, 0, height, 5, Color.BLACK);
		p.addLine("line_3", 15, 0, 0, 0, height, 5, Color.BLACK);
		p.addLine("line_4", 15, 0, 0, width, 0, 5, Color.BLACK);
		p.addLine("line_5", 15, width, height, width, 0, 5, Color.BLACK);
		p.addLine("line_6", 15, width, height, 0, height, 5, Color.BLACK);
	}
	
}
