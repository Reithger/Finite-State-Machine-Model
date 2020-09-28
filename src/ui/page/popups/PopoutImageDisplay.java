package ui.page.popups;

import ui.page.imagepage.ImageDisplay;

public class PopoutImageDisplay extends PopoutWindow {

	private ImageDisplay imageDisplay;
	
	private int dragStartX;
	private int dragStartY;
	private boolean dragState;
	
	public PopoutImageDisplay(String ref) {
		super();
		this.allowScrollbars(false);
		imageDisplay = new ImageDisplay(ref, getElementPanel());
		imageDisplay.designatePopout();
		imageDisplay.drawPage();
	}
	
	@Override
	public void resize(int wid, int hei) {
		super.resize(wid, hei);
		imageDisplay.drawPage();
	}
	
	@Override
	public void clickAction(int code, int x, int y) {
		imageDisplay.processClickInput(code);
	}

	@Override
	public void keyAction(char code) {
		imageDisplay.processKeyInput(code);
	}

	@Override
	public void scrollAction(int scroll) {
		if(scroll < 0) {
			imageDisplay.increaseZoom();
		}
		else {
			imageDisplay.decreaseZoom();
		}
		imageDisplay.drawPage();
	}
	
	@Override
	public void clickPressAction(int code, int x, int y) {
		dragStartX = x;
		dragStartY = y;
		dragState = true;
	}
	
	@Override
	public void clickReleaseAction(int code, int x, int y) {
		dragState = false;
	}
	
	@Override
	public void dragAction(int code, int x, int y) {
		if(dragState) {
			imageDisplay.dragOriginX(x - dragStartX);
			imageDisplay.dragOriginY(y - dragStartY);
			dragStartX = x;
			dragStartY = y;
		}
	}

}
