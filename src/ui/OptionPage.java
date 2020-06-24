package ui;

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
	private final static String DEFAULT_TEXT_ENTRY_CONTENTS = "tes";
	
//---  Instance Variables   -------------------------------------------------------------------

	private String header;
	private String[] categories;
	private boolean[] openCategories;
	private String[][] labels;
	private String[][] types;
	private String[][][] contents;
	private int[][] codes;
	private ElementPanel p;
	
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
		p.removeElementPrefixed("option");
		for(int i = 0; i < categories.length; i++) {
			//Category header
			p.addText("option_header_text_" + i, 15, p.getWidth() / 3 / 3, startY, p.getHeight() * 9 / 10, p.getHeight() / 20, categories[i], OPTIONS_FONT, true, true, true);
			p.addButton("option_header_butt_" + i, 10, p.getWidth() / 3 / 3, startY, p.getHeight() * 9 / 10, p.getHeight() / 20, i, true);
			p.addLine("option_line_" + i, 5,  p.getWidth() / 20, startY + p.getHeight() / 40, p.getWidth() * 11 / 20, startY + p.getHeight() / 40, 3, Color.black);
			startY += p.getHeight() / 18;
			//If category closed, iterate codeStart to maintain distance
			if(openCategories[i] == false) {
				for(int j = 0; j < types[i].length; j++) {
					codeStart += getEntryTextSize(types[i][j]);
				}
				continue;
			}
			for(int j = 0; j < labels[i].length; j++) {
				//Label
				p.addText("option_text_" + i + "_" + j, 10, p.getWidth() / 3 / 2, startY, p.getWidth() / 3, p.getHeight() / 20, labels[i][j], OPTIONS_FONT, true, true, true);
				p.addLine("option_line_" + i + "_" + j, 5, p.getWidth() / 20, startY + p.getHeight() / 40, p.getWidth() * 19 / 20, startY + p.getHeight() / 40, 1, Color.black);
				//Get number of text entries; need to make flexible for checkbox
				int num = getEntryTextSize(types[i][j]);
				for(int k = 0; k < num; k++) {
					//Draws text entries and iterates to next position
					int posX = p.getWidth() / 3 + p.getWidth() / 3 / 4 * (k + 1);
					String star = contents[i][j][k] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[i][j][k];
					p.addRectangle("option_rect_" + i + "_" + j + "_" + k, 5, posX, startY, p.getWidth() / 3 / (5), p.getHeight() / 30, true, Color.white, Color.gray);
					p.addTextEntry(getTextEntryName(i, j, k), 10, posX, startY, p.getWidth() / 3 / (5), p.getHeight() / 30, codeStart++, star, OPTIONS_FONT, true, true, true);
				}
				//Submission button
				p.addRectangle("option_rect_" + i + "_" + j, 10, p.getWidth() - p.getWidth() / 3 / 4, startY, p.getHeight() / 30, p.getHeight() / 30, true, Color.black, Color.gray);
				p.addButton("option_butt_" + i + "_" + j, 10, p.getWidth() - p.getWidth() / 3 / 4, startY, p.getHeight() / 30, p.getHeight() / 30, codes[i][j], true);
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
	
	private String getTextEntryName(int i, int j, int k) {
		return "option_" + categories[i] + "_" + labels[i][j] + "_" + k;
	}

	public void assignElementPanel(ElementPanel pIn) {
		p = pIn;
	}
	
	public abstract void applyCode(int code);

	public boolean toggleCategory(int index) {
		if(index >= 0 && index < openCategories.length) {
			openCategories[index] = !openCategories[index];
			return true;
		}
		return false;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getHeader() {
		return header;
	}

	public String[] getTextEntry(String label) {
		int len = 0;
		while(p.getElementStoredText("option_" + label + "_" + len++) != null) {
			
		}
		String[] out = new String[len-1];
		for(int i = 0; i < out.length; i++) {
			out[i] = p.getElementStoredText("option_" + label + "_" + i);
		}
		return out;
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
			default:
				return 0;
		}
	}
	
}