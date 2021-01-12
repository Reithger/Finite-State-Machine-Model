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
			p.handleTextButton(formTextButtonName(i), false, posX, y, p.getWidth() / 2, lineHei * 5 / 8, DEFAULT_FONT, starr, subSystemCode, Color.white, Color.gray);
			registerCode(subSystemCode--, formTextButtonName(i));
			y += lineHei;
		}
		return y;
	}
	
	protected String formTextButtonName(int index) {
		return prefix() + "_entry_text_" + index;
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
