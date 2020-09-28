package ui.page.popups;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import input.Communication;
import ui.FSMUI;

public class PopoutSelectList extends PopoutWindow{

	private static final int DEFAULT_WIDTH = 300;
	private static final int DEFAULT_HEIGHT = 400;
	private static final int CODE_FILTER_ENTRY_ACCESS = -17;
	private static final int CODE_FILTER_ENTRY_SUBMIT = -16;
	
	public static final String STATIC_ACCESS = "list";
	private static final String TEXT_ENTRY_FILTER_ACCESS = "filter";
	
	private String[] ref;
	private String[] used;
	private String searchTerm;
	private boolean filtered;
	
	public PopoutSelectList(String[] list, boolean filter) {
		super();
		allowScrollbars(true);
		Communication.set(STATIC_ACCESS, null);
		resize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		ref = list;
		used = ref;
		filtered = filter;
		searchTerm = "";
		drawPage();
	}
	
	private void drawPage() {
		int heiChng = DEFAULT_HEIGHT / 4;
		if(filtered) {
			this.handleTextEntry(TEXT_ENTRY_FILTER_ACCESS, DEFAULT_WIDTH / 3, heiChng / 4, DEFAULT_WIDTH * 7 / 12, heiChng / 4, CODE_FILTER_ENTRY_ACCESS, "");
			this.handleRectangle("rect_filter_entry", 5, DEFAULT_WIDTH / 3, heiChng / 4, DEFAULT_WIDTH * 7 / 12, heiChng / 4, Color.WHITE, Color.BLACK);
			
			this.handleRectangle("rect_submit", 5, DEFAULT_WIDTH * 3 /4, heiChng / 4, heiChng / 5, heiChng / 5, Color.GRAY, Color.BLACK);
			this.handleButton("butt_submit", DEFAULT_WIDTH * 3 / 4, heiChng / 4, heiChng / 5, heiChng / 5, CODE_FILTER_ENTRY_SUBMIT);
		}
		removeElementPrefixed("han");
		for(int i = 0; i < used.length; i++) {
			handleTextButton("han_" + i, DEFAULT_WIDTH / 2, (filtered ? heiChng : heiChng / 2) + i * heiChng, DEFAULT_WIDTH * 2 / 4, DEFAULT_HEIGHT / 5, DEFAULT_FONT, used[i], i, Color.gray, Color.black);
		}
		handleText("placeholder", DEFAULT_WIDTH / 2, (filtered ? heiChng : heiChng / 2) + (used.length)* heiChng , 10, 10, DEFAULT_FONT, "");
	}
	
	private void filterList() {
		int newSize = 0;
		for(String s : ref) {
			if(s.matches(searchTerm + ".*")) {
				newSize++;
			}
		}
		used = new String[newSize];
		int posit = 0;
		for(int i = 0; i < ref.length; i++) {
			if(ref[i].matches(searchTerm + ".*")) {
				used[posit++] = ref[i];
			}
		}
	}
	
	@Override
	public void clickAction(int code, int x, int y) {
		if(code == CODE_FILTER_ENTRY_SUBMIT) {
			searchTerm = this.getStoredText(TEXT_ENTRY_FILTER_ACCESS);
			filterList();
			drawPage();
		}
		for(int i = 0; i < used.length; i++) {
			if(i == code) {
				Communication.set(STATIC_ACCESS, used[i]);
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
