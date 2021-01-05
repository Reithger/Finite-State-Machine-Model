package ui.page.optionpage.entryset;

import visual.composite.HandlePanel;

public class EntryEmpty extends EntrySet{

	public EntryEmpty(String prefix, String label, boolean button, int code) {
		super(prefix, label, button, code);
	}
	
	@Override
	public int draw(int y, int lineHei, HandlePanel p) {
		return y;
	}
	
}
