package ui.page.optionpage;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

import ui.FSMUI;
import visual.panel.ElementPanel;

public abstract class OptionPage {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	private final static Font OPTIONS_FONT = new Font("Serif", Font.BOLD, 12);
	public final static int OPTIONS_CODE_BUFFER = 50;
	public final static String ENTRY_TEXT_SINGLE = "S";
	public final static String ENTRY_TEXT_DOUBLE = "D";
	public final static String ENTRY_TEXT_TRIPLE = "T";
	public final static String ENTRY_TEXT_QUARTET = "Q";
	public final static String ENTRY_CHECKBOX = "C";
	public final static String ENTRY_EMPTY = "E";
	private final static String DEFAULT_TEXT_ENTRY_CONTENTS = "";
	private final static String CHECKBOX_TRUE = "t";
	private final static String CHECKBOX_FALSE = "f";
	
//---  Instance Variables   -------------------------------------------------------------------

	private String header;
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
	
//---  Constructors   -------------------------------------------------------------------------
	
	public OptionPage(String head, String[] categ, String[][] lab, String[][] typ, int[][] cod) {
		header = head;
		categories = categ;
		openCategories = new boolean[categories.length];
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
		int startY = p.getHeight() / 20;
		int codeStart = categories.length;
		for(int i = 0; i < categories.length; i++) {
			//Category header
			int posX = p.getWidth() / 3 / 2;
			int posY = startY;
			handleText(header + "_option_header_text_" + i, posX, posY, p.getHeight() * 9/10, p.getHeight() / 20, categories[i]);
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
				handleText(header + "_option_" + i + "_" + j + "_text", p.getWidth() / 3 / 2, startY, p.getWidth() / 3, p.getHeight() / 20, labels[i][j]);
				handleLine(header + "_option_"  + i + "_" + j + "_line", p.getWidth() / 20, startY + p.getHeight() / 40, p.getWidth() * 19 / 20, startY + p.getHeight() / 40, 1, Color.black);
				switch(types[i][j]) {
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
				}
				startY += p.getHeight() / 18;
			}
		}
	}
	
	public void handleMouseInput(int code, int x, int y) {
		int[] pos = getCodeIndices(code);
		if(pos != null && types[pos[0]][pos[1]].equals(ENTRY_CHECKBOX)) {
			contents[pos[0]][pos[1]][0] = (contents[pos[0]][pos[1]][0].equals(CHECKBOX_TRUE) ? CHECKBOX_FALSE : CHECKBOX_TRUE);
			p.removeElement(header + "_option_" + pos[0] + "_" + pos[1] + "_checkbox_rect");
			drawPage();
		}
		applyCode(code);
	}
	
	public abstract void applyCode(int code);

	public boolean toggleCategory(int index) {
		if(index >= 0 && index < openCategories.length) {
			openCategories[index] = !openCategories[index];
			return true;
		}
		return false;
	}

	public static void assignElementPanel(ElementPanel inP) {
		p = inP;
		p.setScrollBarHorizontal(false);
	}
	
	public static void assignFSMUI(FSMUI fsm) {
		reference = fsm;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getHeader() {
		return header;
	}

	public String[] getTextEntry(String label) {
		int len = 0;
		while(p.getElementStoredText(header + "_option_" + label + "_" + len++) != null) {
			
		}
		String[] out = new String[len-1];
		for(int i = 0; i < out.length; i++) {
			out[i] = p.getElementStoredText(header + "_option_" + label + "_" + i);
		}
		return out;
	}
	
	public String[] getTextEntry(int i, int j) {
		int len = 0;
		while(p.getElementStoredText(getTextEntryName(i, j, len++)) != null) {
			
		}
		String[] out = new String[len-1];
		for(int k = 0; k < out.length; k++) {
			out[k] = p.getElementStoredText(getTextEntryName(i, j, k));
		}
		return out;
	}
	
	public String getTextFromCode(int code, int posit) {
		int[] indic = getCodeIndices(code);
		int numPosit = getEntryTextSize(types[indic[0]][indic[1]]);
		if(posit >= numPosit) {
			return null;
		}
		return getTextEntry(indic[0], indic[1])[posit];
	}
	
	public Integer getIntegerFromCode(int code, int posit) {
		int[] indic = getCodeIndices(code);
		int numPosit = getEntryTextSize(types[indic[0]][indic[1]]);
		if(posit >= numPosit) {
			return null;
		}
		String num = getTextEntry(indic[0], indic[1])[posit];
		try {
			return Integer.parseInt(num);
		}
		catch(Exception e) {
			System.out.println("Failure to parse '" + num + "' as an Integer value");
			return null;
		}
	}
	
	public int[] getCodeIndices(int code) {
		for(int i = 0; i < codes.length; i++) {
			for(int j = 0; j < codes[i].length; j++) {
				if(codes[i][j] == code) {
					return new int[] {i, j};
				}
			}
		}
		return null;
	}
		
	public Boolean getCheckboxContents(int code) {
		int[] pos = getCodeIndices(code);
		if(pos != null && types[pos[0]][pos[1]].equals(ENTRY_CHECKBOX)) {
			return contents[pos[0]][pos[1]][0].contentEquals(CHECKBOX_TRUE);
		}
		return null;
	}
	
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
			case ENTRY_EMPTY:
				return 0;
			default:
				return 0;
		}
	}
	
	private String getTextEntryName(int i, int j, int k) {
		return header + "_option_"  + i + "_" + j + "_" + k + "_" + categories[i] + "_" + labels[i][j] + "_" + k;
	}

	public static FSMUI getFSMUI() {
		return reference;
	}
	
//---  Composites   ---------------------------------------------------------------------------

	private void handleText(String nom, int x, int y, int wid, int hei, String phr) {
		if(!p.moveElement(nom, x, y)){
			p.addText(nom, 15, x, y, wid, hei, phr, OPTIONS_FONT, true, true, true);
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
	
	private void handleRectangle(String nom, int x, int y, int wid, int hei, Color col, Color col2) {
		if(!p.moveElement(nom, x, y)) {
			p.addRectangle(nom, 5, x, y, wid, hei, true, col, col2);
		}
	}
		
}
