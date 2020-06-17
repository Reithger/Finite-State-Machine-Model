package ui;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class FSMUI {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static int WINDOW_WIDTH = 1200;
	private final static int WINDOW_HEIGHT = 800;
	private final static double PANEL_RATIO_VERTICAL = 33 / 35.0;
	private final static Font OPTIONS_FONT = new Font("Serif", Font.BOLD, 12);
	private final static int CODE_START_OPTIONS_HEADER = 100;
	private final static int HEADER_EDIT = 0;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** Window needs to have tabs for options menus, multiple images*/
	private WindowFrame frame;
	
	private int image_panel_count;
	
	private ArrayList<String> images;
	
	private ElementPanel imageSpace;
	
	private ElementPanel imageHeader;
	
	private ElementPanel optionSpace;
	
	private ElementPanel optionHeader;
	
	private int currentOptionHeader;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FSMUI() {
		frame = new WindowFrame(WINDOW_WIDTH, WINDOW_HEIGHT);
		image_panel_count = 0;
		images = new ArrayList<String>();
		imageSpace = generateImagePanel(WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * PANEL_RATIO_VERTICAL));
		optionSpace = generateOptionsPanel(0, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)), WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * PANEL_RATIO_VERTICAL));
		imageHeader = generateImageHeader(WINDOW_WIDTH / 2, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)));
		optionHeader = generateOptionHeader(0, 0, WINDOW_WIDTH / 2, (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL)));
		frame.reserveWindow("Home");
		frame.reservePanel("Home", "optionHeader", optionHeader);
		frame.reservePanel("Home", "imageHeader", imageHeader);
		frame.reservePanel("Home", "optionSpace", optionSpace);
		frame.reservePanel("Home", "imageSpace", imageSpace);
		updateOptionsPanel();
		updateOptionHeader();
		currentOptionHeader = this.HEADER_EDIT;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
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
	
	private void updateImagePanel(ElementPanel p) {

	}
	
	private ElementPanel generateImageHeader(int x, int y, int width, int height) {
		ElementPanel p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				
			}
			
			public void clickBehaviour(int code, int x, int y) {
				
			}
		};
		addFraming(p);
		return p;
	}
	
	private void updateImageHeader() {
		
	}
	
	private void allotImage(String path) {
		images.add(path);
		updateImageHeader();
	}
	
	private void updateActiveOptionPage() {
		
	}

	private ElementPanel generateOptionHeader(int x, int y, int width, int height) {
		ElementPanel p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				
			}
			
			public void clickBehaviour(int code, int x, int y) {
				int cod = code - CODE_START_OPTIONS_HEADER;
				System.out.println(code + " " + cod);
				if(cod < OPTIONS_HEADERS.length && cod >= 0) {
					currentOptionHeader = cod;
					updateOptionsPanel();
					updateOptionHeader();
				}
			}
		};
		//addFraming(p);
		return p;
	}
	
	private void updateOptionHeader() {
		ElementPanel p = optionHeader;
		p.removeElementPrefixed("header");
		for(int i = 0 ; i < OPTIONS_HEADERS.length; i++) {
			int posX = WINDOW_WIDTH / 2/ 10 + i * (WINDOW_WIDTH / 2 / 5);
			int posY = (int)(WINDOW_HEIGHT * (1.0 - PANEL_RATIO_VERTICAL) / 2);
			int wid = WINDOW_WIDTH / 2 / 6;
			int hei = (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL) * 2 / 3);
			p.addRectangle("header_rect_" + i, 10, posX, posY, wid, hei, true, i == currentOptionHeader ? Color.green : Color.gray);
			p.addButton("header_butt_" + i, 10, posX, posY, wid, hei, CODE_START_OPTIONS_HEADER + i, true);
			p.addText("header_text_" + i, 15, posX, posY, wid, hei, OPTIONS_HEADERS[i], OPTIONS_FONT, true, true, true);
		}
	}
	
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
