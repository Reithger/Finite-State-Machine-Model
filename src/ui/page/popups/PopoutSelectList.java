package ui.page.popups;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import input.Communication;
import ui.FSMUI;

public class PopoutSelectList extends PopoutWindow{

	private static final int DEFAULT_WIDTH = 300;
	private static final int DEFAULT_HEIGHT = 400;
	
	public static final String STATIC_ACCESS = "list";
	
	private String[] ref;
	
	public PopoutSelectList(String[] list) {
		super();
		allowScrollbars(true);
		Communication.set(STATIC_ACCESS, null);
		resize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		ref = list;
		drawPage();
	}
	
	private void drawPage() {
		int heiChng = DEFAULT_HEIGHT / 4;
		for(int i = 0; i < ref.length; i++) {
			handleTextButton("han_" + i, DEFAULT_WIDTH / 2, heiChng / 2 + i * heiChng, DEFAULT_WIDTH * 2 / 4, DEFAULT_HEIGHT / 5, DEFAULT_FONT, ref[i], i, Color.gray, Color.black);
		}
	}
	
	@Override
	public void clickAction(int code, int x, int y) {
		for(int i = 0; i < ref.length; i++) {
			if(i == code) {
				Communication.set(STATIC_ACCESS, ref[i]);
				dispose();
			}
		}
		
	}
	@Override
	public void keyAction(char code) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void scrollAction(int scroll) {
		
	}
	
}
