package ui.page.popups;

import java.awt.Color;

import input.Communication;

public class PopoutConfig extends PopoutWindow{

	private final static int CODE_SUBMIT_ADDRESS = 25;
	private final static String TEXT_FAIL_LEFT = "NOT FOUND";
	private final static String TEXT_FAIL_RIGHT = "TRY AGAIN";
	
	public PopoutConfig() {
		super();
		drawPage();
	}
	
	private void drawPage() {
		int posX = DEFAULT_POPUP_WIDTH / 2;
		int posY = DEFAULT_POPUP_HEIGHT / 5;
		int heightSmall = DEFAULT_POPUP_HEIGHT / 6;
		int heightTall = DEFAULT_POPUP_HEIGHT / 2;
		handleText("text", posX, posY, DEFAULT_POPUP_WIDTH * 2 / 3, heightTall, DEFAULT_FONT, "Please submit the system path for the GraphViz dot.exe");
		posY += DEFAULT_POPUP_HEIGHT * 3 / 10;
		handleRectangle("rec", 5, posX, posY, DEFAULT_POPUP_WIDTH * 4 / 5, heightSmall, Color.white, Color.black);
		handleTextEntry("entry", posX, posY, DEFAULT_POPUP_WIDTH * 4 / 5, heightSmall, 20, "C://");
		posY += DEFAULT_POPUP_HEIGHT * 3 / 10;
		handleRectangle("rec2", 5, posX, posY, DEFAULT_POPUP_WIDTH / 4, heightSmall, Color.white, Color.black);
		handleText("text2", posX, posY, DEFAULT_POPUP_WIDTH / 4, heightSmall, DEFAULT_FONT, "Submit");
		handleButton("but", posX, posY, DEFAULT_POPUP_WIDTH / 4, heightSmall, CODE_SUBMIT_ADDRESS);
	}

	@Override
	public void clickAction(int code, int x, int y) {
		if(code == CODE_SUBMIT_ADDRESS) {
			Communication.set("dot", getStoredText("entry"));
		}
	}

	@Override
	public void keyAction(char code) {
		// TODO Auto-generated method stub
		
	}
	
	public void failure() {
		handleText("error", DEFAULT_POPUP_WIDTH / 5, DEFAULT_POPUP_HEIGHT * 4 / 5, DEFAULT_POPUP_WIDTH / 3, DEFAULT_POPUP_HEIGHT / 5, DEFAULT_FONT, TEXT_FAIL_LEFT);
		handleText("error2", DEFAULT_POPUP_WIDTH * 4 / 5, DEFAULT_POPUP_HEIGHT * 4 / 5, DEFAULT_POPUP_WIDTH / 3, DEFAULT_POPUP_HEIGHT / 5, DEFAULT_FONT, TEXT_FAIL_RIGHT);
	}
	
	public void success() {
		dispose();
	}

	@Override
	public void scrollAction(int scroll) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void clickPressAction(int code, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clickReleaseAction(int code, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragAction(int code, int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
