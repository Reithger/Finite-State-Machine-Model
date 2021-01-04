package ui.popups;

import java.awt.Color;
import java.awt.Font;

import input.CustomEventReceiver;
import ui.page.optionpage.OptionPage;
import visual.panel.ElementPanel;

public class MultiFSMSelectedPanel extends ElementPanel{

	private final Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);
	private final static int CODE_CLOSE_SELECT_FSMS = -15;
	protected final static double BOX_HEIGHT_RATIO = 1.0 / 6;
	protected final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
	
	private MultiFSMSelection context;
	private int maxSelectFSMs;

	public MultiFSMSelectedPanel(int wid, int hei, int maxFSMs, MultiFSMSelection ref) {
		super(0, 0, wid, hei);
		context = ref;
		maxSelectFSMs = maxFSMs;
		draw();
		this.setEventReceiver(new CustomEventReceiver() {
			@Override
			public void clickEvent(int codeB, int x, int y, int mouseType) {
				draw();
				if(codeB >= 0 && codeB < maxSelectFSMs) {
					context.getContext().removeItem(codeB);
					draw();
					OptionPage.getElementPanel().removeElementPrefixed("");
					OptionPage.getFSMUI().updateActiveOptionPage();
				}
				else if(codeB == CODE_CLOSE_SELECT_FSMS) {
					OptionPage.getElementPanel().removeElementPrefixed("");
					OptionPage.getFSMUI().updateActiveOptionPage();
					getParentFrame().disposeFrame();
				}
			}
		});
	}
	

	
	public void draw() {
		removeElementPrefixed("");
		int bottomI = maxSelectFSMs;
		int wid = getWidth() * 2 / 3;
		int hei = (int)(getHeight() * BOX_HEIGHT_RATIO);
		int heiChange = hei * 3 / 2;
		for(int i = 0; i < maxSelectFSMs; i++) {	
			String nom = context.getContext().getContentAtIndex(i);
			if(nom == null || nom.equals("")) {
				bottomI = i;
				break;
			}
			addRectangle("select_fsm_" + i + "_rect", 5, false, getWidth() / 2, hei + heiChange * i, wid, hei, true, Color.gray, Color.black);
			addText("select_fsm_" + i + "_text", 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, nom, DEFAULT_FONT, true, true, true);
			addButton("select_fsm_" + i + "_butt", 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, i, true);
		}
		addRectangle("select_fsm_" + bottomI + "_rect", 5, false, getWidth() / 2, hei + heiChange * bottomI, wid, hei, true, Color.gray, Color.black);
		addText("select_fsm_" + bottomI + "_text", 10, false, getWidth() / 2, hei + heiChange * bottomI, wid, hei, "Close Selection Screen", DEFAULT_FONT, true, true, true);
		addButton("select_fsm_" + bottomI + "_butt", 10, false, getWidth() / 2, hei + heiChange * bottomI, wid, hei, CODE_CLOSE_SELECT_FSMS, true);
		bottomI++;
		addRectangle("select_fsm_" + bottomI + "_rect", 5, false, getWidth() / 2, hei + heiChange * bottomI, wid, hei, true, COLOR_TRANSPARENT);
	}
}
