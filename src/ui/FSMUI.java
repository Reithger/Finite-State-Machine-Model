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
	private final static int CODE_START_OPTIONS = 50;
	private final static String[] OPTIONS_HEADERS = new String[] {"Edit", "Option 2", "Option 3"};
	private final static int HEADER_EDIT = 0;
	private final static String[][] OPTIONS_LABELS = new String[][]{
		{"Option 1", "Option 2", "Option 3"},
		{"Option 1", "Option 2", "Option 3"},
		{"Option 1", "Option 2", "Option 3"},
		};
	private final static String[][] OPTIONS_TYPES = new String[][] {
		{"S", "D", "T",},
		{},
		{},
	};
	
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
	
	private void allotImage(String path) {
		images.add(path);
		updateImageHeader();
	}
	
	private void updateImageHeader() {
		
	}
	
	/**
	 * Program needs to be able to add states/transitions, select existing ones, intuitive interface from
	 * generic function to mass produce different iterations.
	 * 
	 * @param labels
	 * @param types
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	
	private ElementPanel generateOptionsPanel(int x, int y, int width, int height) {
		ElementPanel p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				
			}
			
			public void clickBehaviour(int code, int x, int y) {
				
			}
		};
		addFraming(p);
		return p;
	}
	
	private void updateOptionsPanel() {
		String[] options = OPTIONS_LABELS[currentOptionHeader];
		String[] types = OPTIONS_TYPES[currentOptionHeader];
		ElementPanel p = optionSpace;
		int startY = WINDOW_HEIGHT / 20;
		for(int i = 0; i < options.length; i++) {
			p.addText("option_text_" + i, 10, WINDOW_WIDTH / 2 / 3 / 2, startY, WINDOW_WIDTH / 2 / 3, WINDOW_HEIGHT / 20, options[i], OPTIONS_FONT, true, true, true);
			p.addLine("option_line_" + i, 5, WINDOW_WIDTH / 2 / 20, startY + WINDOW_HEIGHT / 40, WINDOW_WIDTH / 2 * 19 / 20, startY + WINDOW_HEIGHT / 40, 3, Color.black);
			int num = 0;
			switch(types[i]) {
				case "T":
					num = num > 3 ? num : 3;
				case "D":
					num = num > 2 ? num : 2;
				case "S":
					num = num > 1 ? num : 1;
					for(int j = 0; j < num; j++) {
						int posX = WINDOW_WIDTH / 2 / 3 + WINDOW_WIDTH / 3 / 8 * (j + 1);
						p.addRectangle("option_rect_" + i + "_" + j, 5, posX, startY, WINDOW_WIDTH / 3 / (10), WINDOW_HEIGHT / 30, true, Color.gray);
						p.addTextEntry("option_entry_" + i + "_" + j, 10, posX, startY, WINDOW_WIDTH / 3 / (10), WINDOW_HEIGHT / 30, CODE_START_OPTIONS + i * num + j, "test", OPTIONS_FONT, true, true, true);
					}
					break;
			}
			startY += WINDOW_HEIGHT / 18;
		}
	}
	
	private ElementPanel generateOptionHeader(int x, int y, int width, int height) {
		ElementPanel p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				
			}
			
			public void clickBehaviour(int code, int x, int y) {
				
			}
		};
		addFraming(p);
		return p;
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
