package ui.popups;

import java.awt.Color;

import visual.composite.popout.PopoutWindow;

public class PopoutInputRequest extends PopoutWindow{

//---  Constants   ----------------------------------------------------------------------------
	
	private final static int POPUP_WIDTH = 300;
	private final static int POPUP_HEIGHT = 200;
	private final static int CODE_SUBMIT = 5;
	private final static String ELEMENT_NAME_ENTRY = "entry";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private volatile boolean ready;
	private String out;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public PopoutInputRequest(String text) {
		super(POPUP_WIDTH, POPUP_HEIGHT);
		ready = false;
		int posX = POPUP_WIDTH / 2;
		int posY = POPUP_HEIGHT / 6;
		
		int labelWidth = POPUP_WIDTH * 3 / 4;
		int labelHeight = POPUP_HEIGHT / 3;
		this.handleText("tex", false, posX, posY, labelWidth, labelHeight, null, text);
		
		posY += POPUP_HEIGHT / 3;
		int entryWidth = POPUP_WIDTH / 2;
		int entryHeight = POPUP_HEIGHT / 5;
		this.handleTextEntry(ELEMENT_NAME_ENTRY, false, posX, posY, entryWidth, entryHeight, -55, null, "");
		this.handleRectangle("rect", false, 5, posX, posY, entryWidth, entryHeight, Color.white, Color.black);
		
		posY += POPUP_HEIGHT / 3;
		int submitWidth = POPUP_WIDTH / 2;
		int submitHeight = POPUP_HEIGHT / 4;
		this.handleTextButton("subm", false, posX, posY, submitWidth, submitHeight, null, "Submit", CODE_SUBMIT, Color.white, Color.black);
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getSubmitted() {
		while(!ready) {
			Thread.onSpinWait();
		};
		return out;
	}

//---  Input Handling   -----------------------------------------------------------------------
	
	@Override
	public void clickAction(int arg0, int arg1, int arg2) {
		if(arg0 == CODE_SUBMIT) {
			out = this.getStoredText(ELEMENT_NAME_ENTRY);
			ready = true;
		}
	}

	@Override
	public void clickPressAction(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clickReleaseAction(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragAction(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyAction(char arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scrollAction(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
