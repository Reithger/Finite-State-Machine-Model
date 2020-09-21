package ui.page.popups;

public class PopoutAlert extends PopoutWindow{

	public PopoutAlert(String text) {
		super();
		handleText("tex", DEFAULT_POPUP_WIDTH / 2, DEFAULT_POPUP_HEIGHT / 2, DEFAULT_POPUP_WIDTH * 9 / 10, DEFAULT_POPUP_HEIGHT * 9 / 10, DEFAULT_FONT, text);
	}

	@Override
	public void clickAction(int code, int x, int y) {
		dispose();	
	}

	@Override
	public void keyAction(char code) {
		dispose();	
	}

	@Override
	public void scrollAction(int scroll) {
		// TODO Auto-generated method stub
		
	}
	
}
