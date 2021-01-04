package ui.page.optionpage.entryset;

import java.awt.Color;

import visual.composite.HandlePanel;

public class EntryTextDisplay extends EntrySet{
	
	public EntryTextDisplay(String pref, String name, boolean button, int code) {
		super(pref, name, button, code);
	}
	
	@Override
	public int draw(int y, int lineHei, HandlePanel p) {
		int posX = p.getWidth() * 5 / 8;
		for(int i = 0; i < getContents().size(); i++) {
			String starr = getContentAt(i);
			if(starr == null || starr.contentEquals("")) {
				break;
			}
			if(i != 0)
				y += lineHei;
			p.handleRectangle(prefix() + "_entry_text_rect_" + i, false, 5, posX, y, p.getWidth() / 3, lineHei, Color.white, Color.gray);
			p.handleText(prefix() + "_entry_text_text_" + i, false, posX, y, p.getWidth() / 3, lineHei, DEFAULT_FONT, starr);
			p.handleButton(prefix() + "_entry_text_butt_" + i, false, posX, y, p.getWidth() / 3, lineHei, subSystemCode);
			registerCode(subSystemCode--);
		}
		return y;
	}

	@Override
	public void setContent(String ref, int index) {
		if(index >= getContents().size()) {
			getContents().add(ref);
		}
		else {
			super.setContent(ref, index);
		}
	}
	
}
