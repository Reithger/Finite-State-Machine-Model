package ui.popups;

import java.awt.Color;
import java.util.ArrayList;

import visual.composite.popout.PopoutWindow;

public class PopoutInputRequest extends PopoutWindow{

//---  Constants   ----------------------------------------------------------------------------
	
	private final static int POPUP_WIDTH = 300;
	private final static int POPUP_HEIGHT = 200;
	private final static int CODE_SUBMIT = 5;
	private final static String ELEMENT_NAME_ENTRY = "entry";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private int entryNum;
	private volatile boolean ready;
	private ArrayList<String> out;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public PopoutInputRequest(String text, int num) {
		super(POPUP_WIDTH, POPUP_HEIGHT);
		out = new ArrayList<String>();
		entryNum = num;
		ready = false;
		int posX = POPUP_WIDTH / 2;
		int posY = POPUP_HEIGHT / 6;
		
		int labelWidth = POPUP_WIDTH * 3 / 4;
		int labelHeight = POPUP_HEIGHT / 3;
		handleText("tex", "move", 10, posX, posY, labelWidth, labelHeight, null, text);
		handleRectangle("rect_phrase", "move", 3, posX, posY, labelWidth * 6 / 5, labelHeight, Color.white, Color.black);
		
		/*
		 * 
		posY += POPUP_HEIGHT / 3;
		int submitWidth = POPUP_WIDTH / (num + 2);
		int distBet = POPUP_WIDTH / (num + 1);
		posX = distBet * 3 / 2;
		int submitHeight = POPUP_HEIGHT / 4;
		for(int i = 0; i < num; i++) {
			handleTextButton("subm_" + i, false, posX, posY, submitWidth, submitHeight, null, "Submit", CODE_SUBMIT + i, Color.white, Color.black);
			posX += distBet;
		}
		 */
		
		posY += POPUP_HEIGHT / 3;
		int entryWidth = POPUP_WIDTH / (num + 2);
		int distBet = POPUP_WIDTH / (num + 1);
		posX = distBet;
		int entryHeight = POPUP_HEIGHT / 5;
		for(int i = 0; i < num; i++) {
			handleTextEntry(compileEntryName(i), "move", 10, posX, posY, entryWidth, entryHeight, -55 - i, null, "");
			handleRectangle("rect_" + i, "move", 5, posX, posY, entryWidth, entryHeight, Color.white, Color.black);
			posX += distBet;
		}
		
		posX = POPUP_WIDTH / 2;
		posY += POPUP_HEIGHT / 3;
		int submitWidth = POPUP_WIDTH / 2;
		int submitHeight = POPUP_HEIGHT / 4;
		handleTextButton("subm", "move", 15, posX, posY, submitWidth, submitHeight, null, "Submit", CODE_SUBMIT, Color.white, Color.black);
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	public ArrayList<String> getSubmitted() {
		while(!ready) {
			Thread.onSpinWait();
		};
		return out;
	}
	
	private String compileEntryName(int in) {
		return ELEMENT_NAME_ENTRY + "_" + in;
	}

//---  Input Handling   -----------------------------------------------------------------------
	
	@Override
	public void clickAction(int code, int arg1, int arg2) {
		if(code == CODE_SUBMIT) {
			for(int i = 0; i < entryNum; i++) {
				out.add(getStoredText(compileEntryName(i)));
			}
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
