package ui.page.headers;

import java.awt.Color;
import java.awt.Font;
import visual.panel.ElementPanel;

public abstract class HeaderBase extends ElementPanel{
	
	private final static Font ENTRY_FONT = new Font("Serif", Font.BOLD, 12);
	private final static int ROTATION_MULTIPLIER = 10;

	public HeaderBase(int x, int y, int wid, int hei) {
		super(x, y, wid, hei);
	}
	
	public void handleText(String nom, int x, int y, int wid, int hei, Font font, String phr) {
		if(!moveElement(nom, x, y)){
			addText(nom, 15, false, x, y, wid, hei, phr, font, true, true, true);
		}
	}

	public void handleTextEntry(String nom, int x, int y, int wid, int hei, int cod, String phr) {
		if(!moveElement(nom, x, y)){
			addTextEntry(nom, 15, false, x, y, wid, hei, cod, phr, ENTRY_FONT, true, true, true);
		}
	}
	
	public void handleButton(String nom, int x, int y, int wid, int hei, int code) {
		if(!moveElement(nom, x, y)) {
			addButton(nom, 10, false, x, y, wid, hei, code, true);
		}
	}
	
	public void handleLine(String nom, int x, int y, int x2, int y2, int thck, Color col) {
		if(!moveElement(nom, x, y)) {
			addLine(nom, 5, false, x, y, x2, y2, thck, col);
		}
	}
	
	public void handleRectangle(String nom, int prior, int x, int y, int wid, int hei, Color col, Color col2) {
		if(!moveElement(nom, x, y)) {
			addRectangle(nom, prior, false, x, y, wid, hei, true, col, col2);
		}
	}
	@Override
	public int getMinimumScreenX() {
		return 0;
	}
	
	@Override
	public void mouseWheelBehaviour(int rotation) {
		if(getMaximumScreenX() < getWidth()) {
			return;
		}
		setOffsetXBounded(getOffsetX() + rotation * ROTATION_MULTIPLIER);
	}
	
}
