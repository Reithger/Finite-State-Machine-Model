package ui.page.optionpage.entryset;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import ui.FSMUI;
import ui.page.optionpage.OptionPage;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class SingleFSMSelection extends WindowFrame{

	protected final static int WIDTH_SELECT_FSM_WINDOW = 300;
	protected final static int HEIGHT_SELECT_FSM_WINDOW = 200;
	protected final static Color BACK_COLOR_GRAY = new Color(192, 192, 192);;
	protected final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);

	private SingleFSMPanel pan;
	
	public SingleFSMSelection(EntrySet ref) {
		super(WIDTH_SELECT_FSM_WINDOW, HEIGHT_SELECT_FSM_WINDOW);
		setName("Select FSM for Operation");
		setResizable(true);
		setExitOnClose(false);
		pan = new SingleFSMPanel(ref);
		reservePanel("default", "pan", pan);
	}
	
	@Override
	public void reactToResize() {
		pan.resize(WIDTH_SELECT_FSM_WINDOW, this.getHeight());
	}
	
	public class SingleFSMPanel extends ElementPanel{

		private final Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);
		
		private ArrayList<String> fsms = OptionPage.getFSMUI().getFSMList();
		private EntrySet context; 
		
		public SingleFSMPanel(EntrySet ref) {
			super(0, 0, WIDTH_SELECT_FSM_WINDOW, HEIGHT_SELECT_FSM_WINDOW);
			context = ref;
			draw();
		}
		
		@Override
		public void clickBehaviour(int code, int x, int y) {
			if(code >= 0 && code < fsms.size()) {
				context.removeItem(0);
				context.appendItem(FSMUI.stripPath(fsms.get(code)));
				OptionPage.getElementPanel().removeElementPrefixed("");
				OptionPage.getFSMUI().updateActiveOptionPage();
				this.getParentFrame().disposeFrame();
			}
			if(fsms.size() == 0) {
				this.getParentFrame().disposeFrame();
			}
			draw();
		}
		
		public void draw() {
			int wid = WIDTH_SELECT_FSM_WINDOW * 2 / 3;
			int hei = HEIGHT_SELECT_FSM_WINDOW / 5;
			int heiChange = hei * 3 / 2;
			for(int i = 0; i < fsms.size(); i++) {
				String nom = FSMUI.stripPath(fsms.get(i));
				addRectangle("back_" + i, 5, false, getWidth() / 2,hei + heiChange * i, wid, hei, true, BACK_COLOR_GRAY, Color.black);
				addText("text_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, nom, DEFAULT_FONT, true, true, true);
				addButton("butt_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, i, true);
			}
			if(fsms.size() == 0) {
				int i = 0;
				addRectangle("back_" + i, 5, false, getWidth() / 2,hei + heiChange * i, wid, hei, true, BACK_COLOR_GRAY, Color.black);
				addText("text_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, "No FSMs to select", DEFAULT_FONT, true, true, true);
				addButton("butt_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, i, true);
			}
		}
		
	}
	
}
