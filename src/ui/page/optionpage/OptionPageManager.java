package ui.page.optionpage;

import java.awt.Color;
import java.util.ArrayList;

import controller.InputReceiver;
import input.CustomEventReceiver;
import ui.headers.HeaderSelect;
import ui.page.optionpage.implementation.AdjustFSM;
import ui.page.optionpage.implementation.Operations;
import ui.page.optionpage.implementation.UStructurePage;
import visual.composite.HandlePanel;

public class OptionPageManager {
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static int ROTATION_MULTIPLIER = 15;
	private final static int CODE_BASE_OPTIONS_HEADER = 3500;
	
//---  Instance Variables   -------------------------------------------------------------------

	private OptionPage[] optionPages;
	private static int currentOptionPageIndex;
	private HandlePanel bodyPanel;
	private boolean loading;
	private int lastX;
	private int lastY;
	/** ElementPanel object handling the organization and accessing of all categories of user tools in optionSpace*/
	private HeaderSelect optionHeader;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public OptionPageManager(InputReceiver reference, int xIn, int yIn, int wid, int hei, double vertProp) {
		OptionPage.assignInputReceiver(reference);
		generateHandlePanel(xIn, yIn + (int)(hei * (1 - vertProp)), wid, (int)(hei * vertProp));
		OptionPage.assignHandlePanel(bodyPanel);
		optionPages = new OptionPage[] {
				new AdjustFSM(),
				new Operations(),
				new UStructurePage(),
		};		
		optionHeader = new HeaderSelect(xIn, yIn, wid, (int)(hei * (1 - vertProp)), CODE_BASE_OPTIONS_HEADER);
		optionHeader.setInputReceiver(reference);
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void updateSizeLoc(int x, int y, int wid, int hei, double vertProp) {
		bodyPanel.setLocation(x, y + (int)(hei * (1 - vertProp)));
		bodyPanel.resize(wid, (int)(hei * vertProp));
		bodyPanel.removeAllElements();
		optionHeader.updateSizeLoc(x, y, wid, (int)(hei * (1 - vertProp)));
		getCurrentPage().drawPage();
	}
	
	private void generateHandlePanel(int x, int y, int width, int height) {
		bodyPanel = new HandlePanel(x, y, width, height) {
		
			@Override
			public int getMinimumScreenX(String move) {
				if(move.equals("move")) {
					return 0;
				}
				return super.getMinimumScreenX(move);
			}
			
			@Override
			public int getMinimumScreenY(String move) {
				if(move.equals("move")) {
					return 0;
				}
				return super.getMinimumScreenY(move);
			}
			
			@Override
			public int getMaximumScreenY(String move) {
				int max = super.getMaximumScreenY(move);
				return max + (max > getHeight() ? 15 : 0);
			}
		};
		bodyPanel.setEventReceiver(new CustomEventReceiver() {
			
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
				bodyPanel.moveElement("loading_rect", lastX, lastY);
			}
			
			@Override
			public void mouseWheelEvent(int rotation) {
				if(bodyPanel.getMaximumScreenY("move") < bodyPanel.getHeight()) {
					return;
				}
				int offset = bodyPanel.getOffsetY("move") - rotation * ROTATION_MULTIPLIER;
				offset = offset < 0 ? 0 : offset > bodyPanel.getMaximumScreenY("move") - height ? bodyPanel.getMaximumScreenY("move") - height : offset;
				bodyPanel.setOffsetY("move", offset);
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
			bodyPanel.addRectangle("loading_rect", 30, "no_move", lastX - size / 2, lastY - size / 2, size, size, true, Color.yellow, Color.black);
		}
		else {
			bodyPanel.removeElementPrefixed("loading");
		}
		optionHeader.update(getOptionPageNames(), getCurrentOptionPageIndex());
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
			bodyPanel.setOffsetX("move", 0);
			bodyPanel.setOffsetY("move", 0);
			bodyPanel.removeElementPrefixed("");
		}
	}

	public void setEntrySetContent(int code, int posit, String ref) {
		getCurrentPage().setEntrySetContent(code, posit, ref);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public int getCodeReferenceBase() {
		return CODE_BASE_OPTIONS_HEADER;
	}
	
	public int getSizePageList() {
		return getOptionPageNames().size();
	}
	
	public HandlePanel getHeaderPanel() {
		return optionHeader;
	}
	
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
		for(OptionPage bodyPanel : optionPages) {
			out.add(bodyPanel.getHeader());
		}
		return out;
	}
	
	private OptionPage getCurrentPage() {
		return optionPages[currentOptionPageIndex];
	}
	
	public HandlePanel getBodyPanel() {
		return bodyPanel;
	}
	
	public int getCurrentOptionPageIndex() {
		return currentOptionPageIndex;
	}
	
	public OptionPage[] getOptionPageList() {
		return optionPages;
	}
	
}
