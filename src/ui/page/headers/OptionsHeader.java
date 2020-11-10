package ui.page.headers;

import java.awt.Color;
import java.awt.Font;

import ui.page.optionpage.OptionPageManager;

public class OptionsHeader extends HeaderBase{

	private final static Font OPTIONS_HEADER_FONT = new Font("Serif", Font.BOLD, 12);
	private final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
	
	private final static int CODE_START_OPTIONS_HEADER = 150;
	private OptionPageManager optionPageManager;
	
	public OptionsHeader(int x, int y, int wid, int hei, OptionPageManager refer) {
		super(x, y, wid, hei);
		optionPageManager = refer;
		setScrollBarVertical(false);
		setScrollBarHorizontal(false);
		addLine("line_5", 15, true, wid, hei, wid, 0, 2, Color.BLACK);
		addLine("line_6", 15, true, wid, hei, 0, hei, 5, Color.BLACK);
	}

	public void update() {
		for(int i = 0 ; i < optionPageManager.getOptionPageList().length + 1; i++) {
			int posX = getWidth() / 8 + i * (getWidth() / 4);
			int posY = (int)(getHeight() / 2);
			int wid = getWidth() /  5;
			int hei = (int)(getHeight() * 2 / 3);
			if(i == optionPageManager.getOptionPageList().length) {
				handleRectangle("header_rect_" + i, 10, posX, posY, 1, hei, COLOR_TRANSPARENT, COLOR_TRANSPARENT);
				break;
			}
			if(i == optionPageManager.getCurrentOptionPageIndex()) {
				handleRectangle("header_rect_active", 12, posX, posY, wid, hei, Color.green, Color.black);
			}
			handleRectangle("header_rect_" + i, 10, posX, posY, wid, hei, Color.gray, Color.black);
			handleButton("header_butt_" + i, posX, posY, wid, hei, CODE_START_OPTIONS_HEADER + i);
			handleText("header_text_" + i, posX, posY, wid, hei, OPTIONS_HEADER_FONT, optionPageManager.getOptionPageList()[i].getHeader());
		}
	}
	
	@Override
	public void keyBehaviour(char code) {
		
	}

	@Override
	public void clickBehaviour(int code, int x, int y) {
		int cod = code - CODE_START_OPTIONS_HEADER;
		if(cod < optionPageManager.getOptionPageList().length && cod >= 0) {
			optionPageManager.setCurrentOptionPageIndex(cod);
			optionPageManager.drawPage();
		}
		update();
	}
	
}
