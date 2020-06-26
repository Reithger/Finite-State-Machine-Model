package ui.page.optionpage;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

import visual.panel.ElementPanel;

public abstract class OptionPage {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	private final static Font OPTIONS_FONT = new Font("Serif", Font.BOLD, 12);
	public final static int OPTIONS_CODE_BUFFER = 50;
	public final static String ENTRY_TEXT_SINGLE = "S";
	public final static String ENTRY_TEXT_DOUBLE = "D";
	public final static String ENTRY_TEXT_TRIPLE = "T";
	public final static String ENTRY_TEXT_QUARTET = "Q";
	public final static String ENTRY_CHECKMARK = "C";
	public final static String ENTRY_EMPTY = "E";
	private final static String DEFAULT_TEXT_ENTRY_CONTENTS = "";
	
//---  Instance Variables   -------------------------------------------------------------------

	private String header;
	private String[] categories;
	private boolean[] openCategories;
	private String[][] labels;
	private String[][] types;
	private String[][][] contents;
	/** Code system is a bit awkward: negative value makes a button not appear but be referenced, don't double up any values*/
	private int[][] codes;
	private static ElementPanel p;
	private static int currentOptionPageIndex;
	
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
					
				}
			}
		}
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void drawPage() {
		updateTextEntryContents();
		int startY = p.getHeight() / 20;
		int codeStart = categories.length;
		for(int i = 0; i < categories.length; i++) {
			//Category header
			int posX1 = p.getWidth() / 3 / 3;
			int posY1 = startY;
			String categHeaderName = header + "_option_header_text_" + i;
			if(!p.moveElement(categHeaderName, posX1, posY1)){
				p.addText(categHeaderName, 15, posX1, posY1, p.getHeight() * 9 / 10, p.getHeight() / 20, categories[i], OPTIONS_FONT, true, true, true);
			}
			String categButtonName = header + "_option_header_butt_" + i;
			if(!p.moveElement(categButtonName, posX1, posY1)) {
				p.addButton(categButtonName, 10, posX1, posY1, p.getHeight() * 9 / 10, p.getHeight() / 20, i, true);
			}
			int posX2 = p.getWidth() / 20;
			int posY2 = startY + p.getHeight() / 40;
			String categLineName = header + "_option_line_" + i;
			if(!p.moveElement(categLineName, posX2, posY2)) {
				p.addLine(categLineName, 5,  posX2, posY2, p.getWidth() * 11 / 20, startY + p.getHeight() / 40, 3, Color.black);
			}
			startY += p.getHeight() / 18;
			//If category closed, iterate codeStart to maintain distance and move Elements away from view
			if(openCategories[i] == false) {
				for(int j = 0; j < types[i].length; j++) {
					codeStart += getEntryTextSize(types[i][j]);
					String labelTextName = header + "_option_text_" + i + "_" + j;
					String labelLineName = header + "_option_line_" + i + "_" + j;
					String submitRectangleName = header + "_option_rect_" + i + "_" + j;
					String submitButtonName = header + "_option_butt_" + i + "_" + j;
					int disX = -100;
					int disY = -100;
					p.moveElement(labelTextName, disX, disY);
					p.moveElement(labelLineName, disX, disY);
					p.moveElement(submitRectangleName, disX, disY);
					p.moveElement(submitButtonName, disX, disY);
					int num = getEntryTextSize(types[i][j]);
					for(int k = 0; k < num; k++) {
						String entryRectangleName = header + "_option_rect_" + i + "_" + j + "_" + k;
						String entryTextEntryName = getTextEntryName(i, j, k);
						p.moveElement(entryRectangleName, disX, disY);
						p.moveElement(entryTextEntryName, disX, disY);
					}
				}
				continue;
			}
			for(int j = 0; j < labels[i].length; j++) {
				//Label
				int posX3 = p.getWidth() / 3 / 2;
				int posY3 = startY;
				String labelTextName = header + "_option_text_" + i + "_" + j;
				if(!p.moveElement(labelTextName, posX3, posY3)){
					p.addText(labelTextName, 10, posX3, posY3, p.getWidth() / 3, p.getHeight() / 20, labels[i][j], OPTIONS_FONT, true, true, true);
				}
				int posX4 = p.getWidth() / 20;
				int posY4 = startY + p.getHeight() / 40;
				String labelLineName = header + "_option_line_" + i + "_" + j;
				if(!p.moveElement(labelLineName, posX4, posY4)) {
					p.addLine(labelLineName, 5, posX4, posY4, p.getWidth() * 19 / 20, startY + p.getHeight() / 40, 1, Color.black);
				}
				//Get number of text entries; need to make flexible for checkbox
				int num = getEntryTextSize(types[i][j]);
				for(int k = 0; k < num; k++) {
					//Draws text entries and iterates to next position
					String star = contents[i][j][k] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[i][j][k];
					int posX5 = p.getWidth() / 3 + p.getWidth() / 3 / 4 * (k + 1);
					int posY5 = startY;
					String entryRectangleName = header + "_option_rect_" + i + "_" + j + "_" + k;
					if(!p.moveElement(entryRectangleName, posX5, posY5)) {
						p.addRectangle(entryRectangleName, 5, posX5, posY5, p.getWidth() / 3 / (5), p.getHeight() / 30, true, Color.white, Color.gray);
					}
					String entryTextEntryName = getTextEntryName(i, j, k);
					if(!p.moveElement(entryTextEntryName, posX5, posY5)) {
						p.addTextEntry(entryTextEntryName, 10, posX5, posY5, p.getWidth() / 3 / (5), p.getHeight() / 30, codeStart++, star, OPTIONS_FONT, true, true, true);
					}
				}
				//Submission button
				int posX6 = p.getWidth() - p.getWidth() / 3 / 4;
				int posY6 = startY;
				if(codes[i][j] >= 0) {
					String submitRectangleName = header + "_option_rect_" + i + "_" + j;
					if(!p.moveElement(submitRectangleName, posX6, posY6)) {
						p.addRectangle(submitRectangleName, 10, posX6, posY6, p.getHeight() / 30, p.getHeight() / 30, true, Color.black, Color.gray);
					}
					String submitButtonName = header + "_option_butt_" + i + "_" + j;
					if(!p.moveElement(submitButtonName, posX6, posY6)) {
						p.addButton(submitButtonName, 10, posX6, posY6, p.getHeight() / 30, p.getHeight() / 30, codes[i][j], true);
					}
				}
				startY += p.getHeight() / 18;
			}
		}
	}
	
	private void updateTextEntryContents() {
		for(int i = 0; i < categories.length; i++) {
			for(int j = 0; j < labels[i].length; j++) {
				for(int k = 0; k < getEntryTextSize(types[i][j]); k++) {
					String textEntryName = getTextEntryName(i, j, k);
					String text = p.getElementStoredText(textEntryName);
					contents[i][j][k] = new String(text == null ? DEFAULT_TEXT_ENTRY_CONTENTS : text);
				}
			}
		}
	}
	
	public abstract void applyCode(int code);

	public boolean toggleCategory(int index) {
		if(index >= 0 && index < openCategories.length) {
			openCategories[index] = !openCategories[index];
			return true;
		}
		return false;
	}

	public static ElementPanel generateElementPanel(int x, int y, int width, int height, OptionPage[] optionPages) {
		p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				System.out.println(getFocusElement() + " " + code);
			}
			
			public void clickBehaviour(int code, int x, int y) {
				optionPages[getCurrentOptionPageIndex()].applyCode(code);
			}
		};
		return p;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public static void setCurrentOptionPageIndex(int in) {
		currentOptionPageIndex = in;
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
	
	public boolean getCheckboxEntry(String label) {
		for(int i = 0; i < labels.length; i++) {
			for(int j = 0; j < labels[i].length; j++) {
				
			}
		}
		return false;
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
			case ENTRY_EMPTY:
				return 0;
			default:
				return 0;
		}
	}
	
	private String getTextEntryName(int i, int j, int k) {
		return header + "_option_" + categories[i] + "_" + labels[i][j] + "_" + k;
	}

	public static int getCurrentOptionPageIndex() {
		return currentOptionPageIndex;
	}
	
}
