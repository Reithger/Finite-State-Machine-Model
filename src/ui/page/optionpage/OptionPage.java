package ui.page.optionpage;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;

import ui.FSMUI;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public abstract class OptionPage {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	private final static Font OPTIONS_FONT = new Font("Serif", Font.BOLD, 12);
	public final static int OPTIONS_CODE_BUFFER = 50;
	public final static String ENTRY_TEXT_SINGLE = "S";
	public final static String ENTRY_TEXT_DOUBLE = "D";
	public final static String ENTRY_TEXT_TRIPLE = "T";
	public final static String ENTRY_TEXT_QUARTET = "Q";
	public final static String ENTRY_TEXT_LONG = "L";
	public final static String ENTRY_CHECKBOX = "C";
	public final static String ENTRY_EMPTY = "E";
	public final static String ENTRY_SELECT_FSM = "F";
	public final static String ENTRY_SELECT_FSMS = "FS";
	private final static String DEFAULT_TEXT_ENTRY_CONTENTS = "";
	private final static String CHECKBOX_TRUE = "t";
	private final static String CHECKBOX_FALSE = "f";
	
	private final static int MAX_SELECT_FSMS = 8;
	
	private final static int WIDTH_SELECT_FSM_WINDOW = 300;
	private final static int HEIGHT_SELECT_FSM_WINDOW = 200;
	private final static int WIDTH_SELECT_FSMS_WINDOW = 500;
	private final static int WIDTH_SELECT_FSMS_PANEL = 250;
	private final static int HEIGHT_SELECT_FSMS_WINDOW = 300;
	private final static int CODE_CLOSE_SELECT_FSMS = -15;
	private final static Color BACK_COLOR_GRAY = new Color(192, 192, 192);;
	private final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
	
//---  Instance Variables   -------------------------------------------------------------------

	private String header;
	private String help;
	private String[] categories;
	private boolean[] openCategories;
	private String[][] labels;
	private String[][] types;
	/** Contents can be string phrases or string representations of boolean decision making*/
	private String[][][] contents;
	/** Code system is a bit awkward: negative value makes a button not appear but be referenced, don't double up any values*/
	private int[][] codes;
	private static ElementPanel p;
	private static FSMUI reference;
	private boolean showHelp;
	private int helpKey;
	private boolean showSettings; //TODO: Settings menu
	private int settingsKey;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public OptionPage(String head, String[] categ, String[][] lab, String[][] typ, int[][] cod, String inHelp) {
		header = head;
		categories = categ;
		openCategories = new boolean[categories.length];
		help = inHelp;
		labels = lab;
		types = typ;
		codes = cod;
		contents = new String[categ.length][][];
		for(int i = 0; i < categ.length; i++) {
			contents[i] = new String[lab[i].length][];
			for(int j = 0; j < lab[i].length; j++) {
				contents[i][j] = new String[getEntryTextSize(types[i][j])];
				for(int k = 0; k < getEntryTextSize(types[i][j]); k++) {
					contents[i][j][k] =	types[i][j].equals(ENTRY_CHECKBOX) ? "f" : "";
				}
			}
		}
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void drawPage() {
		if(showHelp) {
			drawHelpPage();
		}
		else {
			drawNormalPage();
		}
	}
	
	public void drawNormalPage() {
		int startY = p.getHeight() / 20;
		int codeStart = categories.length;
		helpKey = codeStart;
		codeStart++;
		handleRectangle("help_rect", p.getWidth() - p.getWidth() / 15, p.getWidth() / 20, p.getWidth() / 20, p.getWidth() / 20, Color.gray, Color.black);
		handleButton("help_button", p.getWidth() - p.getWidth() / 15,  p.getWidth() / 20, p.getWidth() / 20, p.getWidth() / 20, helpKey);
		handleImage("help_img", p.getWidth() - p.getWidth() / 15, p.getWidth() / 20, "/assets/ui/question_mark.png", 3);
		for(int i = 0; i < categories.length; i++) {
			//Category header
			int posX = p.getWidth() / 3 / 2;
			int posY = startY;
			handleText(header + "_option_header_text_" + i, posX, posY, p.getHeight() * 9/10, p.getHeight() / 20, OPTIONS_FONT, categories[i]);
			handleButton(header + "_option_header_butt_" + i, posX, posY, p.getHeight() * 9 / 10, p.getHeight() / 20, i);
			posX = p.getWidth() / 20;
			posY = startY + p.getHeight() / 40;
			handleLine(header + "_option_line_" + i, posX, posY, p.getWidth() * 11 / 20, startY + p.getHeight() / 40, 3, Color.black);
			startY += p.getHeight() / 18;
			//If category closed, iterate codeStart to maintain distance and move Elements away from view
			if(openCategories[i] == false) {
				for(int j = 0; j < types[i].length; j++) {
					codeStart += getEntryTextSize(types[i][j]);
					p.moveElementPrefixed(header + "_option_" + i + "_" + j, -100, -100);
				}
				continue;
			}
			for(int j = 0; j < labels[i].length; j++) {
				//Label
				handleText(header + "_option_" + i + "_" + j + "_text", p.getWidth() / 3 / 2, startY, p.getWidth() / 3, p.getHeight() / 20, OPTIONS_FONT, labels[i][j]);
				switch(types[i][j]) {
					case ENTRY_SELECT_FSM:
						posX = p.getWidth() / 3 + p.getWidth() / 3 ;
						posY = startY;
						String choze = contents[i][j][0] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[i][j][0];
				  		handleRectangle(header + "_option_" + i + "_" + j + "_rect_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, Color.white, Color.gray);
						handleText(getTextEntryName(i, j, 0), posX, posY, p.getWidth() / 3, p.getHeight() / 30, OPTIONS_FONT, choze);
						handleButton(header + "_option_" + i + "_" + j + "_butt_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, codes[i][j]);
						break;
					case ENTRY_SELECT_FSMS:
						posX = p.getWidth() / 3 + p.getWidth() / 3;
						for(int a = 0; a < MAX_SELECT_FSMS; a++) {
							posY = startY;
							String chozes = contents[i][j][a] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[i][j][a];
							if((chozes == null || chozes.equals(""))) {
						  		handleRectangle(header + "_option_" + i + "_" + j + "_" + a + "_rect_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, Color.white, Color.gray);
								handleButton(header + "_option_" + i + "_" + j + "_" + a + "_butt_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, codes[i][j]);
								break;
							}
							startY += p.getHeight() / 18;
					  		handleRectangle(header + "_option_" + i + "_" + j + "_" + a + "_rect_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, Color.white, Color.gray);
							handleText(getTextEntryName(i, j, a), posX, posY, p.getWidth() / 3, p.getHeight() / 30, OPTIONS_FONT, chozes);
							handleButton(header + "_option_" + i + "_" + j + "_" + a + "_butt_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, codes[i][j]);
						}
						break;
				  	case ENTRY_TEXT_LONG:
						posX = p.getWidth() / 3 + p.getWidth() / 3 ;
						posY = startY;
						String starr = contents[i][j][0] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[i][j][0];
				  		handleRectangle(header + "_option_" + i + "_" + j + "_rect_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, Color.white, Color.gray);
						handleTextEntry(getTextEntryName(i, j, 0), posX, posY, p.getWidth() / 3, p.getHeight() / 30, codeStart++, starr);
						break;
					case ENTRY_CHECKBOX:
						posX = p.getWidth() / 3 + p.getWidth() / 3 / 4;
						handleRectangle(header + "_option_"  + i + "_" + j + "_checkbox_rect", posX, startY, p.getHeight() / 30, p.getHeight() / 30, (contents[i][j][0].equals(CHECKBOX_FALSE) ? Color.white : Color.gray), Color.black);
						handleButton(header + "_option_" + i + "_" + j + "_checkbox_button", posX, startY, p.getHeight() / 30, p.getHeight() / 30, codes[i][j]);
						break;
					default:
						int num = getEntryTextSize(types[i][j]);
						for(int k = 0; k < num; k++) {
							//Draws text entries and iterates to next position
							String star = contents[i][j][k] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[i][j][k];
							posX = p.getWidth() / 3 + p.getWidth() / 3 / 4 * (k + 1);
							posY = startY;
							handleRectangle(header + "_option_" + i + "_" + j + "_" + k + "_rect", posX, posY, p.getWidth() / 3 / (5), p.getHeight() / 30, Color.white, Color.gray);
							handleTextEntry(getTextEntryName(i, j, k), posX, posY, p.getWidth() / 3 / (5), p.getHeight() / 30, codeStart++, star);
						}
				}
				//Submission button
				posX = p.getWidth() - p.getWidth() / 3 / 4;
				posY = startY;
				if(codes[i][j] >= 0) {
					handleRectangle(header + "_option_" + i + "_" + j + "_rect", posX, posY, p.getHeight() / 30, p.getHeight() / 30, Color.black, Color.gray);
					handleButton(header + "_option_"  + i + "_" + j + "_butt", posX, posY, p.getHeight() / 30, p.getHeight() / 30, codes[i][j]);
					handleLine(header + "_option_"  + i + "_" + j + "_line", p.getWidth() / 20, startY + p.getHeight() / 40, p.getWidth() * 19 / 20, startY + p.getHeight() / 40, 1, Color.black);
				}
				else {
					handleLine(header + "_option_"  + i + "_" + j + "_line", p.getWidth() / 20, startY + p.getHeight() / 40, p.getWidth() * 17 / 20, startY + p.getHeight() / 40, 1, Color.gray);
				}
				startY += p.getHeight() / 18;
			}
		}
	}
	
	public void drawHelpPage() {
		handleText("help", p.getWidth() / 2, p.getHeight() / 2, p.getWidth(), p.getHeight(), OPTIONS_FONT, help);
	}
	
	public void handleMouseInput(int code, int x, int y) {
		if(code == helpKey || showHelp == true) {
			showHelp = ! showHelp;
			p.removeElementPrefixed("");
			drawPage();
			return;
		}
		int[] pos = getCodeIndices(code);
		if(pos != null) {
			switch(getTypeFromCode(code)) {
				case ENTRY_CHECKBOX:
					contents[pos[0]][pos[1]][0] = (contents[pos[0]][pos[1]][0].equals(CHECKBOX_TRUE) ? CHECKBOX_FALSE : CHECKBOX_TRUE);
					p.removeElement(header + "_option_" + pos[0] + "_" + pos[1] + "_checkbox_rect");
					break;
				case ENTRY_SELECT_FSM:
					WindowFrame fra = new WindowFrame(WIDTH_SELECT_FSM_WINDOW, HEIGHT_SELECT_FSM_WINDOW) {
						@Override
						public void reactToResize() {
							this.getPanel("pan").resize(WIDTH_SELECT_FSMS_PANEL, this.getHeight());
						}
					};
					fra.setName("Select FSM for Operation");
					ArrayList<String> fsms = getFSMUI().getFSMList();
					ElementPanel eP = new ElementPanel(0, 0, WIDTH_SELECT_FSM_WINDOW, HEIGHT_SELECT_FSM_WINDOW) {
						@Override
						public void clickBehaviour(int code, int x, int y) {
							if(code >= 0 && code < fsms.size()) {
								contents[pos[0]][pos[1]][0] = FSMUI.stripPath(fsms.get(code));
								p.removeElement(getTextEntryName(pos[0], pos[1], 0));
								drawPage();
								this.getParentFrame().disposeFrame();
							}
						}
					};
					fra.reservePanel("default", "pan", eP);
					fra.setResizable(true);
					fra.setExitOnClose(false);
					int wid = WIDTH_SELECT_FSMS_PANEL * 2 / 3;
					int hei = HEIGHT_SELECT_FSM_WINDOW / 5;
					int heiChange = hei * 3 / 2;
					for(int i = 0; i < fsms.size(); i++) {
						String nom = FSMUI.stripPath(fsms.get(i));
						eP.addRectangle("back_" + i, 5, eP.getWidth() / 2,hei + heiChange * i, wid, hei, true, BACK_COLOR_GRAY, Color.black);
						eP.addText("text_" + i, 10, eP.getWidth() / 2, hei + heiChange * i, wid, hei, nom, OPTIONS_FONT, true, true, true);
						eP.addButton("butt_" + i, 10, eP.getWidth() / 2, hei + heiChange * i, wid, hei, i, true);
					}
					break;
				case ENTRY_SELECT_FSMS:
					WindowFrame fra2 = new WindowFrame(WIDTH_SELECT_FSMS_WINDOW, HEIGHT_SELECT_FSMS_WINDOW) {
						@Override
						public void reactToResize() {
							this.getPanel("pan").resize(WIDTH_SELECT_FSMS_PANEL, this.getHeight());
							this.getPanel("pan2").resize(WIDTH_SELECT_FSMS_PANEL, this.getHeight());
							drawSelectedFSMsPanel((ElementPanel)(this.getPanel("pan")), code);
							drawSelectableFSMsPanel((ElementPanel)(this.getPanel("pan2")));
						}
					};
					fra2.setExitOnClose(false);
					fra2.setResizable(true);
					fra2.setName("Select FSMs For Operation");
					
					ElementPanel eP2 = new ElementPanel(0, 0, WIDTH_SELECT_FSMS_PANEL, HEIGHT_SELECT_FSMS_WINDOW) {
						@Override
						public void clickBehaviour(int codeB, int x, int y) {
							drawSelectedFSMsPanel(this, code);
							if(codeB >= 0 && codeB < MAX_SELECT_FSMS) {
								removeFSM(code, codeB);
								drawSelectedFSMsPanel(this, code);
								p.removeElementPrefixed("");
								drawPage();
							}
							else if(codeB == CODE_CLOSE_SELECT_FSMS) {
								fra2.disposeFrame();
								p.removeElementPrefixed("");
								drawPage();
							}
						}
					};
					drawSelectedFSMsPanel(eP2, code);
					
					ElementPanel eP3 = new ElementPanel(WIDTH_SELECT_FSMS_WINDOW - WIDTH_SELECT_FSMS_PANEL, 0, WIDTH_SELECT_FSMS_PANEL, HEIGHT_SELECT_FSMS_WINDOW) {
						@Override
						public void clickBehaviour(int codeB, int x, int y) {
							ArrayList<String> fsms2 = getFSMUI().getFSMList();
							if(codeB >= 0 && codeB < fsms2.size()) {
								appendFSM(code, FSMUI.stripPath(fsms2.get(codeB)));
								drawSelectedFSMsPanel(eP2, code);
								drawPage();
							}
						}
					};
					drawSelectableFSMsPanel(eP3);
					
					fra2.reservePanel("default", "pan", eP2);
					fra2.reservePanel("default", "pan2", eP3);
					break;
				default:
					break;
			}
			drawPage();
		}
		applyCode(code);
	}
	
	private void drawSelectedFSMsPanel(ElementPanel eP2, int code) {
		eP2.removeElementPrefixed("");
		int bottomI = MAX_SELECT_FSMS;
		int wid = WIDTH_SELECT_FSMS_PANEL * 2 / 3;
		int hei = HEIGHT_SELECT_FSM_WINDOW / 5;
		int heiChange = hei * 3 / 2;
		for(int i = 0; i < MAX_SELECT_FSMS; i++) {	
			String nom = getTextFromCode(code, i);
			System.out.println(nom + " " + i);
			if(nom == null || nom.equals("")) {
				bottomI = i;
				break;
			}
			eP2.addRectangle("select_fsm_" + i + "_rect", 5, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, true, BACK_COLOR_GRAY, Color.black);
			eP2.addText("select_fsm_" + i + "_text", 10, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, nom, OPTIONS_FONT, true, true, true);
			eP2.addButton("select_fsm_" + i + "_butt", 10, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, i, true);
		}
		eP2.addRectangle("select_fsm_" + bottomI + "_rect", 5, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * bottomI, wid, hei, true, BACK_COLOR_GRAY, Color.black);
		eP2.addText("select_fsm_" + bottomI + "_text", 10, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * bottomI, wid, hei, "Close Selection Screen", OPTIONS_FONT, true, true, true);
		eP2.addButton("select_fsm_" + bottomI + "_butt", 10, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * bottomI, wid, hei, CODE_CLOSE_SELECT_FSMS, true);
		bottomI++;
		eP2.addRectangle("select_fsm_" + bottomI + "_rect", 5, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * bottomI, wid, hei, true, COLOR_TRANSPARENT);
	}
	
	private void drawSelectableFSMsPanel(ElementPanel eP2) {
		ArrayList<String> fsms2 = getFSMUI().getFSMList();
		eP2.removeElementPrefixed("");
		int wid = WIDTH_SELECT_FSM_WINDOW * 2 / 3;
		int hei = HEIGHT_SELECT_FSM_WINDOW / 5;
		int heiChange = hei * 3 / 2;
		for(int i = 0; i < fsms2.size(); i++) {	
			String nom = FSMUI.stripPath(fsms2.get(i));
			eP2.addRectangle("back_" + i, 5, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, true, BACK_COLOR_GRAY, Color.black);
			eP2.addText("text_" + i, 10, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, nom, OPTIONS_FONT, true, true, true);
			eP2.addButton("butt_" + i, 10, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, i, true);
		}
	}
	
	public abstract void applyCode(int code);

//---  Setter Methods   -----------------------------------------------------------------------

	public static void assignElementPanel(ElementPanel inP) {
		p = inP;
		p.setScrollBarHorizontal(false);
	}
	
	public static void assignFSMUI(FSMUI fsm) {
		reference = fsm;
	}
	
	public void resetCodeEntries(int code) {
		int[] indic = getCodeIndices(code);
		for(int i = 0; i < contents[indic[0]][indic[1]].length; i++) {
			contents[indic[0]][indic[1]][i] = "";
			p.setElementStoredText(getTextEntryName(indic[0], indic[1], i), "");
		}
		p.repaint();
	}

	public boolean toggleCategory(int index) {
		if(index >= 0 && index < openCategories.length) {
			openCategories[index] = !openCategories[index];
			return true;
		}
		return false;
	}

	public void appendFSM(int code, String reference) {
		if(getTypeFromCode(code).contentEquals(ENTRY_SELECT_FSMS)) {
			int[] indic = getCodeIndices(code);
			int index = 0;
			while(!contents[indic[0]][indic[1]][index].equals("") && index + 1 < contents[indic[0]][indic[1]].length) {
				index++;
			}
			if(index == contents[indic[0]][indic[1]].length) {
				return;
			}
			contents[indic[0]][indic[1]][index] = reference;
		}
	}
	
	public void removeFSM(int code, int index) {
		if(getTypeFromCode(code).contentEquals(ENTRY_SELECT_FSMS)) {
			int[] indic = getCodeIndices(code);
			contents[indic[0]][indic[1]][index] = contents[indic[0]][indic[1]][++index];
			while(!contents[indic[0]][indic[1]][index].equals("") && index + 1 < contents[indic[0]][indic[1]].length) {
				contents[indic[0]][indic[1]][index] = contents[indic[0]][indic[1]][++index];
			}
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	//-- Meta  ------------------------------------------------
	
	public String getHeader() {
		return header;
	}

	public static FSMUI getFSMUI() {
		return reference;
	}
	
	//-- Access Contents  -------------------------------------
	
	public String getTextFromCode(int code, int posit){
		int[] indic = getCodeIndices(code);
		int numPosit = getEntryTextSize(types[indic[0]][indic[1]]);
		if(posit >= numPosit) {
			return null;
		}
		return contents[indic[0]][indic[1]][posit];
	}
	
	public String getTextEntryFromCode(int code, int posit) {
		int[] indic = getCodeIndices(code);
		int numPosit = getEntryTextSize(types[indic[0]][indic[1]]);
		if(posit >= numPosit) {
			return null;
		}
		return getTextEntryElementContents(code)[posit];
	}
	
	public Integer getIntegerFromCode(int code, int posit) {
		System.out.println(code + " " + posit);
		int[] indic = getCodeIndices(code);
		int numPosit = getEntryTextSize(types[indic[0]][indic[1]]);
		if(posit >= numPosit) {
			return null;
		}
		String num = getTextEntryElementContents(code)[posit];
		try {
			return Integer.parseInt(num);
		}
		catch(Exception e) {
			System.out.println("Failure to parse '" + num + "' as an Integer value");
			return null;
		}
	}
	
	public Boolean getCheckboxContentsFromCode(int code) {
		int[] pos = getCodeIndices(code);
		if(pos != null && types[pos[0]][pos[1]].equals(ENTRY_CHECKBOX)) {
			return contents[pos[0]][pos[1]][0].contentEquals(CHECKBOX_TRUE);
		}
		return null;
	}
	
	public String getTypeFromCode(int code) {
		int[] indic = getCodeIndices(code);
		return types[indic[0]][indic[1]];
	}

	public String[] getFSMsFromCode(int code) {
		if(getTypeFromCode(code).contentEquals(ENTRY_SELECT_FSMS)) {
			int[] indic = getCodeIndices(code);
			String[] proc = contents[indic[0]][indic[1]];
			int len = 0;
			while(!proc[len++].equals("")) {
			}
			String[] out = new String[len-1];
			for(int i = 0; i < out.length; i++) {
				out[i] = proc[i];
			}
			return out;
		}
		return null;
	}
	
	//-- Mechanic Reference  ----------------------------------
	
	private int getEntryTextSize(String in) {
		switch(in) {
			case ENTRY_TEXT_QUARTET:
				return 4;
			case ENTRY_TEXT_TRIPLE:
				return 3;
			case ENTRY_TEXT_DOUBLE:
				return 2;
			case ENTRY_TEXT_SINGLE:
				return 1;
			case ENTRY_CHECKBOX:
				return 1;
			case ENTRY_TEXT_LONG:
				return 1;
			case ENTRY_SELECT_FSM:
				return 1;
			case ENTRY_SELECT_FSMS:
				return MAX_SELECT_FSMS;
			case ENTRY_EMPTY:
				return 0;
			default:
				return 0;
		}
	}
	
	private String getTextEntryName(int i, int j, int k) {
		return header + "_option_"  + i + "_" + j + "_" + k + "_" + categories[i] + "_" + labels[i][j] + "_" + k;
	}

	private int[] getCodeIndices(int code) {
		for(int i = 0; i < codes.length; i++) {
			for(int j = 0; j < codes[i].length; j++) {
				if(codes[i][j] == code) {
					return new int[] {i, j};
				}
			}
		}
		return null;
	}
	
	private String[] getTextEntryElementContents(int code) {
		int[] ref = getCodeIndices(code);
		int len = 0;
		while(p.getElementStoredText(getTextEntryName(ref[0], ref[1], len++)) != null) {
			
		}
		String[] out = new String[len-1];
		for(int k = 0; k < out.length; k++) {
			out[k] = p.getElementStoredText(getTextEntryName(ref[0], ref[1], k));
		}
		return out;
	}
	
//---  Composites   ---------------------------------------------------------------------------

	private void handleText(String nom, int x, int y, int wid, int hei, Font inF, String phr) {
		if(!p.moveElement(nom, x, y)){
			p.addText(nom, 15, x, y, wid, hei, phr, inF, true, true, true);
		}
	}
	
	private void handleImage(String nom, int x, int y, String path, double scale) {
		if(!p.moveElement(nom, x, y)){
			p.addImage(nom, 15, x, y, true, path, scale);
		}
	}

	private void handleTextEntry(String nom, int x, int y, int wid, int hei, int cod, String phr) {
		if(!p.moveElement(nom, x, y)){
			p.addTextEntry(nom, 15, x, y, wid, hei, cod, phr, OPTIONS_FONT, true, true, true);
		}
	}
	
	private void handleButton(String nom, int x, int y, int wid, int hei, int code) {
		if(!p.moveElement(nom, x, y)) {
			p.addButton(nom, 10, x, y, wid, hei, code, true);
		}
	}
	
	private void handleLine(String nom, int x, int y, int x2, int y2, int thck, Color col) {
		if(!p.moveElement(nom, x, y)) {
			p.addLine(nom, 5, x, y, x2, y2, thck, col);
		}
	}
	
	private void handleRectangle(String nom, int x, int y, int wid, int hei, Color inside, Color border) {
		if(!p.moveElement(nom, x, y)) {
			p.addRectangle(nom, 5, x, y, wid, hei, true, inside, border);
		}
	}
		
}
