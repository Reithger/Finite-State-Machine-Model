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
		int posX = p.getWidth() + wid / 2;
		for(int k = 0; k < size; k++) {
			//Draws text entries and iterates to next position
			String star = getContentAt(k);
			p.handleRectangle("entry_text_" + getName() + "_" + k, false, 5, posX, y, wid / 3 / (5), lineHei, Color.white, Color.gray);
			p.handleTextEntry("entry_ext_entry_" + getName() + "_" + k, false, posX, y, wid / 3 / (5), lineHei, subSystemCode, DEFAULT_FONT, star);
			posX += wid;
			registerCode(subSystemCode--);
		}
		return y;
	}
	
}
