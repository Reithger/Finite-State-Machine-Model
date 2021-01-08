package ui.page.optionpage.entryset;

import java.awt.Color;
import java.util.ArrayList;

import visual.composite.HandlePanel;

public class EntryText extends EntrySet{

	private int size;
	private boolean expand;
	
	public EntryText(String pref, String ref, boolean button, int buttCode, int len, boolean fill) {
		super(pref, ref, button, buttCode);
		size = len;
		expand = fill;
		ArrayList<String> use = new ArrayList<String>();
		for(int i = 0; i < len; i++) {
			use.add("");
		}
		setContents(use);
	}
	
	@Override
	public int draw(int y, int lineHei, HandlePanel p) {
		int wid = p.getWidth() * 3 / 4 / (expand ? size : 5);
		int posX = p.getWidth() / 4 + wid / 2;
		int height = lineHei * 5 / 8;
		int width = wid * 7 / 10;
		for(int k = 0; k < size; k++) {
			//Draws text entries and iterates to next position
			String star = getContentAt(k);
			p.handleRectangle(prefix() + "_entry_text_" + k, false, 5, posX, y - lineHei / 10, width, height, Color.white, Color.gray);
			p.handleTextEntry(formTextEntryName(k), false, posX, y - lineHei / 10, width, height, subSystemCode, DEFAULT_FONT, star);
			posX += wid;
			registerCode(subSystemCode--, formTextEntryName(k));
		}
		return y;
	}
	
	public boolean handleInput(int code, HandlePanel p) {
		String elem = this.getCodeMapping(code);
		try {
			int ind = Integer.parseInt(elem.substring(elem.lastIndexOf("_") + 1));
			String tex = p.getElementStoredText(formTextEntryName(ind));
			setContent(tex, ind);
		}
		catch(Exception e) {
			
		}
		return getCodeMapping(code).equals("");
	}
	
	@Override
	public void setContent(String ref, int in) {
		super.setContent(ref, in);
	}
	
	private String formTextEntryName(int index) {
		return prefix() + "_entry_text_entry_" + index;
	}
	
}
