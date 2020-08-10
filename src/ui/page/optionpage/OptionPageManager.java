package ui.page.optionpage;

import ui.FSMUI;
import visual.panel.ElementPanel;

public class OptionPageManager {
	
//---  Instance Variables   -------------------------------------------------------------------

	public final static OptionPage[] OPTION_PAGES = new OptionPage[] {
			new AdjustFSM(0, 0, FSMUI.WINDOW_WIDTH/2, (int)(FSMUI.WINDOW_HEIGHT * FSMUI.PANEL_RATIO_VERTICAL)),
			new Operations(0, 0, FSMUI.WINDOW_WIDTH/2, (int)(FSMUI.WINDOW_HEIGHT * FSMUI.PANEL_RATIO_VERTICAL)),
	};
	private static int currentOptionPageIndex;
	private ElementPanel p;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public OptionPageManager(FSMUI reference) {
		OptionPage.assignFSMUI(reference);
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public ElementPanel generateElementPanel(int x, int y, int width, int height) {
		p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				
			}
			
			public void clickBehaviour(int code, int x, int y) {
				OPTION_PAGES[currentOptionPageIndex].handleMouseInput(code, x, y);
			}
			
			public int getMinimumScreenY() {
				return 0;
			}
		};
		OptionPage.assignElementPanel(p);
		return p;
	}

	public void drawPage() {
		OPTION_PAGES[currentOptionPageIndex].drawPage();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCurrentOptionPageIndex(int in) {
		currentOptionPageIndex = in;
		p.removeElementPrefixed("");
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	public int getCurrentOptionPageIndex() {
		return currentOptionPageIndex;
	}
	
	public OptionPage[] getOptionPageList() {
		return OPTION_PAGES;
	}
	
}
