package ui.page.optionpage;

import java.awt.Color;
import java.util.ArrayList;

import controller.InputReceiver;
import input.CustomEventReceiver;
import ui.page.optionpage.implementation.AdjustFSM;
import ui.page.optionpage.implementation.Operations;
import ui.page.optionpage.implementation.UStructurePage;
import visual.composite.HandlePanel;

public class OptionPageManager {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static int ROTATION_MULTIPLIER = 15;
	
//---  Instance Variables   -------------------------------------------------------------------

	private OptionPage[] optionPages;
	private static int currentOptionPageIndex;
	private HandlePanel p;
	private boolean loading;
	private int lastX;
	private int lastY;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public OptionPageManager(InputReceiver reference, int xIn, int yIn, int wid, int hei) {
		OptionPage.assignInputReceiver(reference);
		generateHandlePanel(xIn, yIn, wid, hei);
		OptionPage.assignHandlePanel(p);
		optionPages = new OptionPage[] {
				new AdjustFSM(),
				new Operations(),
				new UStructurePage(),
		};
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void updateSize(int x, int y, int wid, int hei) {
		p.setLocation(x, y);
		p.resize(wid, hei);
		p.removeAllElements();
		getCurrentPage().drawPage();
	}
	
	private void generateHandlePanel(int x, int y, int width, int height) {
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
			public void mouseMoveEvent(int code, int x, int y) {
				lastX = x;
				lastY = y;
				p.moveElement("loading_rect", lastX, lastY);
			}
			
			@Override
			public void mouseWheelEvent(int rotation) {
				if(p.getMaximumScreenY() < p.getHeight()) {
					return;
				}
				p.setOffsetYBounded(p.getOffsetY() - rotation * ROTATION_MULTIPLIER);
			}

			@Override
			public void focusEventReaction(int code) {
				getCurrentPage().handleInput(code);
			}
			
		});
	}

	public void drawPage() {
		getCurrentPage().drawPage();
		if(loading) {
			int size = 32;
			p.addRectangle("loading_rect", 30, true, lastX - size / 2, lastY - size / 2, size, size, true, Color.yellow, Color.black);
		}
		else {
			p.removeElementPrefixed("loading");
		}
	}
	
	public void startLoading() {
		loading = true;
		drawPage();
	}
	
	public void endLoading() {
		loading = false;
		drawPage();
	}
	
	public void clearTextContents(int code) {
		getCurrentPage().resetContents(code);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCurrentOptionPageIndex(int in) {
		if(currentOptionPageIndex != in) {
			currentOptionPageIndex = in;
			p.setOffsetX(0);
			p.setOffsetY(0);
			p.removeElementPrefixed("");
		}
	}

	public void setEntrySetContent(int code, int posit, String ref) {
		getCurrentPage().setEntrySetContent(code, posit, ref);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getTextContent(int code, int posit) {
		return getCurrentPage().getTextFromCode(code, posit);
	}
	
	public Boolean getCheckboxContent(int code) {
		return getCurrentPage().getCheckboxContentsFromCode(code);
	}
	
	public ArrayList<String> getContent(int code){
		return getCurrentPage().getContentFromCode(code);
	}
	
	public ArrayList<String> getOptionPageNames(){
		ArrayList<String> out = new ArrayList<String>();
		for(OptionPage p : optionPages) {
			out.add(p.getHeader());
		}
		return out;
	}
	
	private OptionPage getCurrentPage() {
		return optionPages[currentOptionPageIndex];
	}
	
	public HandlePanel getPanel() {
		return p;
	}
	
	public int getCurrentOptionPageIndex() {
		return currentOptionPageIndex;
	}
	
	public OptionPage[] getOptionPageList() {
		return optionPages;
	}
	
}
