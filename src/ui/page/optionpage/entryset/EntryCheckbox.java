package ui.page.optionpage.entryset;

import java.awt.Color;
import java.util.ArrayList;

import visual.composite.HandlePanel;

public class EntryCheckbox extends EntrySet{
	
	public EntryCheckbox(String pref, String name, boolean butt, int code) {
		super(pref, name, butt, code);
		ArrayList<String> use = new ArrayList<String>();
		use.add("f");
		setContents(use);
	}
	
	@Override
	public int draw(int y, int lineHei, HandlePanel p) {
		int posX = p.getWidth() * 3 / 8;
		int size = lineHei * 5 / 6;
		p.handleRectangle(prefix() + "_entry_checkbox_rect", false, 5, posX, y, size, size, (!getContentAt(0).equals(SIGNIFIER_TRUE) ? Color.white : Color.gray), Color.black);
		p.handleButton(prefix() + "_entry_checkbox_butt", false, posX, y, size, size, subSystemCode);
		registerCode(subSystemCode--);
		return y;
	}

}
