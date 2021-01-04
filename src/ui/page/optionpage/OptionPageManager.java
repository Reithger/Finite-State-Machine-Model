package ui.page.optionpage;

import input.CustomEventReceiver;
import ui.FSMUI;
import ui.page.optionpage.implementation.AdjustFSM;
import ui.page.optionpage.implementation.Operations;
import ui.page.optionpage.implementation.UStructurePage;
import visual.composite.HandlePanel;

public class OptionPageManager {
	
//---  Instance Variables   -------------------------------------------------------------------

	private OptionPage[] optionPages;
	private static int currentOptionPageIndex;
	private HandlePanel p;
	private final static int ROTATION_MULTIPLIER = 15;
	private int width;
	private int height;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public OptionPageManager(FSMUI reference, int wid, int hei) {
		OptionPage.assignFSMUI(reference);
		OptionPage.assignInputReceiver(reference);
		width = wid;
		height = hei;
		optionPages = new OptionPage[] {
				new AdjustFSM(0, 0, width/2, (int)(height * FSMUI.PANEL_RATIO_VERTICAL)),
				new Operations(0, 0, width/2, (int)(height * FSMUI.PANEL_RATIO_VERTICAL)),
				new UStructurePage(0, 0, width/2, (int)(height * FSMUI.PANEL_RATIO_VERTICAL)),
		};
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public HandlePanel generateElementPanel(int x, int y, int width, int height) {
		p = new HandlePanel(x, y, width, height) {
		
			@Override
			public int getMinimumScreenX() {
				return 0;
			}
			
			@Override
			public int getMinimumScreenY() {
				return 0;
			}
			
			@Override
			public int getMaximumScreenY() {
				int max = super.getMaximumScreenY();
				return max + (max > getHeight() ? 15 : 0);
			}
		};
		p.setEventReceiver(new CustomEventReceiver() {
			
			@Override
			public void keyEvent(char code) {
				
			}

			@Override
			public void clickEvent(int code, int x, int y, int mouseType) {
				optionPages[currentOptionPageIndex].handleMouseInput(code, x, y, mouseType);
			}

			
			@Override
			public void mouseWheelEvent(int rotation) {
				if(p.getMaximumScreenY() < p.getHeight()) {
					return;
				}
				p.setOffsetYBounded(p.getOffsetY() - rotation * ROTATION_MULTIPLIER);
			}

		});
		OptionPage.assignHandlePanel(p);
		return p;
	}

	public void drawPage() {
		optionPages[currentOptionPageIndex].drawPage();
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
		return optionPages;
	}
	
}
