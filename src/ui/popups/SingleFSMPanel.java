package ui.popups;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import input.CustomEventReceiver;
import ui.page.optionpage.OptionPage;
import ui.page.optionpage.entryset.EntrySet;
import visual.panel.ElementPanel;

public class SingleFSMPanel extends ElementPanel{

	private final Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);
	
	private ArrayList<String> fsms;
	private EntrySet context; 
	
	public SingleFSMPanel(int wid, int hei, EntrySet ref, ArrayList<String> entries) {
		super(0, 0, wid, hei);
		fsms = entries;
		context = ref;
		draw();
		this.setEventReceiver(new CustomEventReceiver() {
			@Override
			public void clickEvent(int code, int x, int y, int mouseType) {
				if(code >= 0 && code < fsms.size()) {
					context.removeItem(0);
					context.appendItem(stripPath(fsms.get(code)));
					OptionPage.getElementPanel().removeElementPrefixed("");
					OptionPage.getFSMUI().updateActiveOptionPage();
					getParentFrame().disposeFrame();
				}
				if(fsms.size() == 0) {
					getParentFrame().disposeFrame();
				}
				draw();
			}
		});
	}
	

	
	public void draw() {
		int wid = getWidth() * 2 / 3;
		int hei = getHeight() / 5;
		int heiChange = hei * 3 / 2;
		for(int i = 0; i < fsms.size(); i++) {
			String nom = stripPath(fsms.get(i));
			addRectangle("back_" + i, 5, false, getWidth() / 2,hei + heiChange * i, wid, hei, true, Color.gray, Color.black);
			addText("text_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, nom, DEFAULT_FONT, true, true, true);
			addButton("butt_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, i, true);
		}
		if(fsms.size() == 0) {
			int i = 0;
			addRectangle("back_" + i, 5, false, getWidth() / 2,hei + heiChange * i, wid, hei, true, Color.gray, Color.black);
			addText("text_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, "No FSMs to select", DEFAULT_FONT, true, true, true);
			addButton("butt_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, i, true);
		}
	}
	
	public static String stripPath(String in) {
		String out = in.substring(in.lastIndexOf("/") + 1);
		return out.substring(in.lastIndexOf("\\") + 1);
	}
	
}

