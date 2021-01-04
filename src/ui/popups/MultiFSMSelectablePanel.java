package ui.popups;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import input.CustomEventReceiver;
import visual.panel.ElementPanel;

public class MultiFSMSelectablePanel extends ElementPanel{

	private final Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);
	protected final static double BOX_HEIGHT_RATIO = 1.0 / 6;

	private ArrayList<String> fsms;
	private MultiFSMSelection context;
	
	public MultiFSMSelectablePanel(int x, int wid, int hei, ArrayList<String> inFSMs,  MultiFSMSelection fra) {
		super(x, 0, wid, hei);
		context = fra;
		fsms = inFSMs;
		draw();
		this.setEventReceiver(new CustomEventReceiver() {
			@Override
			public void clickEvent(int codeB, int x, int y, int mouseType) {
				if(codeB >= 0 && codeB < fsms.size()) {
					context.getContext().appendItem(stripPath(fsms.get(codeB)));
					context.redraw();
				}
			}
		});
	}

	
	public void draw() {
		removeElementPrefixed("");
		int wid = getWidth() * 2 / 3;
		int hei = (int)(getHeight() * BOX_HEIGHT_RATIO);
		int heiChange = hei * 3 / 2;
		for(int i = 0; i < fsms.size(); i++) {	
			String nom = stripPath(fsms.get(i));
			addRectangle("back_" + i, 5, false, getWidth() / 2, hei + heiChange * i, wid, hei, true, Color.gray, Color.black);
			addText("text_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, nom, DEFAULT_FONT, true, true, true);
			addButton("butt_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, i, true);
		}
		if(fsms.size() == 0) {
			int i = 0;
			addRectangle("back_" + i, 5, false, getWidth() / 2, hei + heiChange * i, wid, hei, true, Color.gray, Color.black);
			addText("text_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, "No FSMs to select", DEFAULT_FONT, true, true, true);
			addButton("butt_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, i, true);
		}
	}
	
	public static String stripPath(String in) {
		String out = in.substring(in.lastIndexOf("/") + 1);
		return out.substring(in.lastIndexOf("\\") + 1);
	}
	
}
