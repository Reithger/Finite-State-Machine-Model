package ui.page.optionpage.entryset;

import java.awt.Color;
import java.awt.Font;

import visual.composite.HandlePanel;

public class EntryList extends EntryTextDisplay{
	
	private final static Font ADD_FONT = new Font("Serif", Font.BOLD, 18);
	
	private int addCode;
	
	public EntryList(String pref, String name, boolean butt, int code, int inCode) {
		super(pref, name, butt, code);
		addCode = inCode;
	}
	
	@Override
	public int draw(int y, int lineHei, HandlePanel p) {
		y = super.draw(y, lineHei, p);

		p.handleTextButton(prefix() + "_entry_list", false, p.getWidth() * 5 /8, y, p.getWidth() / 2, lineHei * 5 / 8, ADD_FONT, "+", addCode, Color.white, Color.black);
		
		registerCode(addCode, prefix() + "_entry_list");
		
		y += lineHei;
		
		return y;
	}
	
	@Override
	public String resetContent() {
		getContents().clear();
		return prefix();
	}
	
	@Override
	public boolean handleInput(int code, HandlePanel p) {
		String base = getCodeMapping(code);
		try {
			int val = Integer.parseInt(base.substring(base.lastIndexOf("_") + 1));
			deleteContentAt(val);
			p.removeElementPrefixed(prefix());
			return false;
		}
		catch(Exception e) {
			
		}
		return true;
	}
	
}
