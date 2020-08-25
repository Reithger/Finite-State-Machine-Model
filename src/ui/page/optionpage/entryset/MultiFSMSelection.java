package ui.page.optionpage.entryset;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import ui.FSMUI;
import ui.page.optionpage.OptionPage;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;


public class MultiFSMSelection extends WindowFrame{
	
	protected final static int WIDTH_SELECT_FSMS_WINDOW = 500;
	protected final static int HEIGHT_SELECT_FSMS_WINDOW = 300;
	protected final static int WIDTH_SELECT_FSMS_PANEL = 250;
	protected final static int HEIGHT_SELECT_FSMS_PANEL = 300;
	protected final static Color BACK_COLOR_GRAY = new Color(192, 192, 192);
	protected final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
	protected final static double BOX_HEIGHT_RATIO = 1.0 / 6;
	public final static int MAX_SELECT_FSMS = 8;
	
	private MultiFSMSelectedPanel selected;
	private MultiFSMSelectablePanel choice;
	private EntrySet context;
	
	public MultiFSMSelection(EntrySet ref) {
		super(WIDTH_SELECT_FSMS_WINDOW, HEIGHT_SELECT_FSMS_WINDOW);
		context =ref;
		setExitOnClose(false);
		setResizable(true);
		setName("Select FSMs For Operation");
		selected = new MultiFSMSelectedPanel(this);
		choice = new MultiFSMSelectablePanel(this);
		reservePanel("default", "pan", selected);
		reservePanel("default", "pan2", choice);
	}
	
	@Override
	public void reactToResize() {
		selected.resize(WIDTH_SELECT_FSMS_PANEL, this.getHeight());
		choice.resize(WIDTH_SELECT_FSMS_PANEL, this.getHeight());
		redraw();
	}
	
	public void redraw() {
		selected.draw();
		choice.draw();
	}
	
	public EntrySet getContext() {
		return context;
	}
	
	public class MultiFSMSelectedPanel extends ElementPanel{

		private final Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);
		private final static int CODE_CLOSE_SELECT_FSMS = -15;
		
		private MultiFSMSelection context;

		public MultiFSMSelectedPanel(MultiFSMSelection ref) {
			super(0, 0, WIDTH_SELECT_FSMS_PANEL, HEIGHT_SELECT_FSMS_PANEL);
			context = ref;
			draw();
		}
		
		@Override
		public void clickBehaviour(int codeB, int x, int y) {
			draw();
			if(codeB >= 0 && codeB < MAX_SELECT_FSMS) {
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
		
		public void draw() {
			removeElementPrefixed("");
			int bottomI = MAX_SELECT_FSMS;
			int wid = WIDTH_SELECT_FSMS_PANEL * 2 / 3;
			int hei = (int)(HEIGHT_SELECT_FSMS_WINDOW * BOX_HEIGHT_RATIO);
			int heiChange = hei * 3 / 2;
			for(int i = 0; i < MAX_SELECT_FSMS; i++) {	
				String nom = context.getContext().getContentAtIndex(i);
				if(nom == null || nom.equals("")) {
					bottomI = i;
					break;
				}
				addRectangle("select_fsm_" + i + "_rect", 5, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, true, BACK_COLOR_GRAY, Color.black);
				addText("select_fsm_" + i + "_text", 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, nom, DEFAULT_FONT, true, true, true);
				addButton("select_fsm_" + i + "_butt", 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, i, true);
			}
			addRectangle("select_fsm_" + bottomI + "_rect", 5, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * bottomI, wid, hei, true, BACK_COLOR_GRAY, Color.black);
			addText("select_fsm_" + bottomI + "_text", 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * bottomI, wid, hei, "Close Selection Screen", DEFAULT_FONT, true, true, true);
			addButton("select_fsm_" + bottomI + "_butt", 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * bottomI, wid, hei, CODE_CLOSE_SELECT_FSMS, true);
			bottomI++;
			addRectangle("select_fsm_" + bottomI + "_rect", 5, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * bottomI, wid, hei, true, COLOR_TRANSPARENT);
		}
	}
	
	public class MultiFSMSelectablePanel extends ElementPanel{

		private final Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);

		private ArrayList<String> fsms = OptionPage.getFSMUI().getFSMList();
		private MultiFSMSelection context;
		
		public MultiFSMSelectablePanel(MultiFSMSelection fra) {
			super(WIDTH_SELECT_FSMS_WINDOW - WIDTH_SELECT_FSMS_PANEL, 0, WIDTH_SELECT_FSMS_PANEL, HEIGHT_SELECT_FSMS_PANEL);
			context = fra;
			draw();
		}
		
		@Override
		public void clickBehaviour(int codeB, int x, int y) {
			if(codeB >= 0 && codeB < fsms.size()) {
				context.getContext().appendItem(FSMUI.stripPath(fsms.get(codeB)));
				OptionPage.getFSMUI().updateActiveOptionPage();
				context.redraw();
			}
		}
		
		private void draw() {
			removeElementPrefixed("");
			int wid = WIDTH_SELECT_FSMS_PANEL * 2 / 3;
			int hei = (int)(HEIGHT_SELECT_FSMS_WINDOW * BOX_HEIGHT_RATIO);
			int heiChange = hei * 3 / 2;
			for(int i = 0; i < fsms.size(); i++) {	
				String nom = FSMUI.stripPath(fsms.get(i));
				addRectangle("back_" + i, 5, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, true, BACK_COLOR_GRAY, Color.black);
				addText("text_" + i, 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, nom, DEFAULT_FONT, true, true, true);
				addButton("butt_" + i, 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, i, true);
			}
			if(fsms.size() == 0) {
				int i = 0;
				addRectangle("back_" + i, 5, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, true, BACK_COLOR_GRAY, Color.black);
				addText("text_" + i, 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, "No FSMs to select", DEFAULT_FONT, true, true, true);
				addButton("butt_" + i, 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, i, true);
			}
		}
		
	}
	
}
