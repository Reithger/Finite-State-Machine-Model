package ui.page.optionpage.entryset;

import java.awt.Color;

import visual.composite.HandlePanel;

public class EntryList extends EntryTextDisplay{
	
	private int addCode;
	
	public EntryList(String pref, String name, boolean butt, int code, int inCode) {
		super(pref, name, butt, code);
		addCode = inCode;
	}
	
	@Override
	public int draw(int y, int lineHei, HandlePanel p) {
		y = super.draw(y, lineHei, p);
		
		y += lineHei;
		
		p.handleTextButton(prefix() + "_entry_list", false, p.getWidth() * 5 /8, y, p.getWidth() / 3, lineHei, DEFAULT_FONT, "+", addCode, Color.white, Color.black);
		
		return y;
	}
	
}
