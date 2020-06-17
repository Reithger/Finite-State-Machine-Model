package ui;

import java.awt.Color;
import java.awt.Font;

import visual.panel.ElementPanel;

public abstract class OptionPage {
	
	private final static Font OPTIONS_FONT = new Font("Serif", Font.BOLD, 12);
	public final static int OPTIONS_CODE_BUFFER = 50;

	private String header;
	private String[] categories;
	private boolean[] openCategories;
	private String[][] labels;
	private String[][] types;
	private ElementPanel p;
	private int codeBuffer;
	
	public OptionPage(String head, String[] categ, String[][] lab, String[][] typ) {
		header = head;
		categories = categ;
		openCategories = new boolean[categories.length];
		labels = lab;
		types = typ;
	}
	
	public void update() {
		String[][] options = labels;
		p.removeElementPrefixed("option");
		int startY = p.getHeight() / 20;
		for(int i = 0; i < categories.length; i++) {
			p.addText("header_text_" + i, 10, p.getWidth() / 3 / 2, startY, p.getHeight() * 9 / 10, p.getHeight() / 20, categories[i], OPTIONS_FONT, true, true, true);
			startY += p.getHeight() / 18;
			for(int j = 0; j < labels[i].length; j++) {
				p.addText("option_text_" + i, 10, p.getWidth() / 3 / 2, startY, p.getWidth() / 3, p.getHeight() / 20, options[i][j], OPTIONS_FONT, true, true, true);
				p.addLine("option_line_" + i, 5, p.getWidth() / 20, startY + p.getHeight() / 40, p.getWidth() * 19 / 20, startY + p.getHeight() / 40, 3, Color.black);
				int num = 0;
				switch(types[i][j]) {
					case "T":
						num = num > 3 ? num : 3;
					case "D":
						num = num > 2 ? num : 2;
					case "S":
						num = num > 1 ? num : 1;
						for(int k = 0; k < num; k++) {
							int posX = p.getWidth() / 3 + p.getWidth() / 3 / 8 * (k + 1);
							p.addRectangle("option_rect_" + i + "_" + k, 5, posX, startY, p.getWidth() / 3 / (10), p.getHeight() / 30, true, Color.gray);
							p.addTextEntry("option_entry_" + i + "_" + k, 10, posX, startY, p.getWidth() / 3 / (10), p.getHeight() / 30, codeBuffer + i * OPTIONS_CODE_BUFFER + num * j + k, "test", OPTIONS_FONT, true, true, true);
						}
						break;
				}
				startY += p.getHeight() / 18;
			}
		}
	}

	public void assignElementPanel(ElementPanel pIn) {
		p = pIn;
	}
	
	public String getHeader() {
		return header;
	}
	
	public void assignCodeRange(int base) {
		codeBuffer = base;
	}
	
	public abstract void applyCode(int code);
}
