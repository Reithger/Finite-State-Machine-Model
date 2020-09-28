package ui.page.popups;

import java.awt.Color;
import java.awt.Font;

import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public abstract class PopoutWindow {

//---  Constant Values   ----------------------------------------------------------------------
	
	protected final static int DEFAULT_POPUP_WIDTH = 400;
	protected final static int DEFAULT_POPUP_HEIGHT = 250;
	protected final static Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);
	protected final static Font ENTRY_FONT = new Font("Serif", Font.BOLD, 12);
	private final static int ROTATION_MULTIPLIER = 10;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private WindowFrame frame;
	private ElementPanel panel;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public PopoutWindow() {
		panel = null;
		frame = new WindowFrame(DEFAULT_POPUP_WIDTH, DEFAULT_POPUP_HEIGHT) {
			@Override
			public void reactToResize() {
				panel.resize(frame.getWidth(), frame.getHeight());
			}
		};
		frame.setName("Popup Window");
		frame.setResizable(true);
		frame.setExitOnClose(false);
		panel = new ElementPanel(0, 0, DEFAULT_POPUP_WIDTH, DEFAULT_POPUP_HEIGHT) {
			@Override
			public void clickBehaviour(int code, int x, int y){
				clickAction(code, x, y);
			}
			
			@Override
			public void keyBehaviour(char code) {
				keyAction(code);
			}
			
			@Override
			public void mouseWheelBehaviour(int scroll) {
				this.setOffsetYBounded(this.getOffsetY() - scroll * ROTATION_MULTIPLIER);
				scrollAction(scroll);
			}
			
			public void clickPressBehaviour(int code, int x, int y) {
				clickPressAction(code, x, y);
			}
			
			public void clickReleasedBehaviour(int code, int x, int y) {
				clickReleaseAction(code, x, y);
			}
			
			public void dragBehaviour(int code, int x, int y) {
				dragAction(code, x, y);
			}
			
		};
		frame.reserveWindow("default");
		frame.reservePanel("default", "pan", panel);
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void resize(int wid, int hei) {
		frame.resize(wid, hei);
		panel.resize(wid, hei);
		frame.repaint();
		panel.repaint();
	}

	public void dispose() {
		frame.disposeFrame();
	}
	
	protected void allowScrollbars(boolean set) {
		panel.setScrollBarVertical(set);
		panel.setScrollBarHorizontal(set);
	}
	
	public abstract void clickAction(int code, int x, int y);
	
	public abstract void clickPressAction(int code, int x, int y);
	
	public abstract void clickReleaseAction(int code, int x, int y);
	
	public abstract void keyAction(char code);
	
	public abstract void scrollAction(int scroll);
	
	public abstract void dragAction(int code, int x, int y);
	
	protected void removeElementPrefixed(String in) {
		panel.removeElementPrefixed(in);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setTitle(String in) {
		frame.setName(in);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getStoredText(String ref) {
		return panel.getElementStoredText(ref);
	}
	
	protected ElementPanel getElementPanel() {
		return panel;
	}
	
//---  Drawing Support   ----------------------------------------------------------------------
	
	public void handleTextButton(String nom, int x, int y, int wid, int hei, Font font, String phr, int code, Color col, Color col2) {
		handleRectangle(nom + "_rect", 10, x, y, wid, hei, col, col2);
		handleText(nom + "_text", x, y, wid, hei, font, phr);
		handleButton(nom + "_butt", x, y, wid, hei, code);
	}
	
	public void handleText(String nom, int x, int y, int wid, int hei, Font font, String phr) {
		if(!panel.moveElement(nom, x, y)){
			panel.addText(nom, 15, false, x, y, wid, hei, phr, font, true, true, true);
		}
	}

	public void handleTextEntry(String nom, int x, int y, int wid, int hei, int cod, String phr) {
		if(!panel.moveElement(nom, x, y)){
			panel.addTextEntry(nom, 15, false, x, y, wid, hei, cod, phr, ENTRY_FONT, true, true, true);	//TODO: Smaller font for entry?
		}
	}
	
	public void handleButton(String nom, int x, int y, int wid, int hei, int code) {
		if(!panel.moveElement(nom, x, y)) {
			panel.addButton(nom, 10, false, x, y, wid, hei, code, true);
		}
	}
	
	public void handleLine(String nom, int x, int y, int x2, int y2, int thck, Color col) {
		if(!panel.moveElement(nom, x, y)) {
			panel.addLine(nom, 5, false, x, y, x2, y2, thck, col);
		}
	}
	
	public void handleRectangle(String nom, int prior, int x, int y, int wid, int hei, Color col, Color col2) {
		if(!panel.moveElement(nom, x, y)) {
			panel.addRectangle(nom, prior, false, x, y, wid, hei, true, col, col2);
		}
	}

}
