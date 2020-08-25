package ui.page.popups;

import java.awt.Color;
import java.awt.Font;

import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public abstract class PopoutWindow {
	
	protected final static int DEFAULT_POPUP_WIDTH = 400;
	protected final static int DEFAULT_POPUP_HEIGHT = 250;
	protected final static Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);
	protected final static Font ENTRY_FONT = new Font("Serif", Font.BOLD, 12);
	
	private WindowFrame frame;
	private ElementPanel panel;
	
	public PopoutWindow() {
		frame = new WindowFrame(DEFAULT_POPUP_WIDTH, DEFAULT_POPUP_HEIGHT) {
			@Override
			public void reactToResize() {
				
			}
		};
		frame.setName("Popup Window");
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
			
		};
		panel.setScrollBarVertical(false);
		frame.reserveWindow("default");
		frame.reservePanel("default", "pan", panel);
	}
	
	public void setTitle(String in) {
		frame.setName(in);
	}
	
	public void dispose() {
		frame.disposeFrame();
	}
	
	public abstract void clickAction(int code, int x, int y);
	
	public abstract void keyAction(char code);
	
	public String getStoredText(String ref) {
		return panel.getElementStoredText(ref);
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
