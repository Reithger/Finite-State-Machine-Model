package ui.page.headers;

import java.awt.Color;

import visual.panel.ElementPanel;

public class OptionsHeader extends ElementPanel{

	private final static int CODE_START_OPTIONS_HEADER = 150;
	
	public OptionsHeader(int x, int y, int wid, int hei) {
		super(x, y, wid, hei);

		setScrollBarVertical(false);
		setScrollBarHorizontal(false);
		addLine("line_5", 15, false, wid, hei, wid, 0, 2, Color.BLACK);
		addLine("line_6", 15, false, wid, hei, 0, hei, 5, Color.BLACK);
	}

	public void update() {
		for(int i = 0 ; i < optionPageManager.getOptionPageList().length + 1; i++) {
			int posX = WINDOW_WIDTH / 2/ 8 + i * (WINDOW_WIDTH / 2 / 4);
			int posY = (int)(WINDOW_HEIGHT * (1.0 - PANEL_RATIO_VERTICAL) / 2);
			int wid = WINDOW_WIDTH / 2 / 5;
			int hei = (int)(WINDOW_HEIGHT * (1 - PANEL_RATIO_VERTICAL) * 2 / 3);
			if(i == optionPageManager.getOptionPageList().length) {
				handleRectangle(p, "header_rect_" + i, 10, posX, posY, 1, hei, COLOR_TRANSPARENT, COLOR_TRANSPARENT);
				break;
			}
			if(i == optionPageManager.getCurrentOptionPageIndex()) {
				handleRectangle(p, "header_rect_active", 12, posX, posY, wid, hei, Color.green, Color.black);
			}
			handleRectangle(p, "header_rect_" + i, 10, posX, posY, wid, hei, Color.gray, Color.black);
			handleButton(p, "header_butt_" + i, posX, posY, wid, hei, CODE_START_OPTIONS_HEADER + i);
			handleText(p, "header_text_" + i, posX, posY, wid, hei, DEFAULT_FONT, optionPageManager.getOptionPageList()[i].getHeader());
		
	}
	
	@Override
	public void keyBehaviour(char code) {
		
	}

	@Override
	public void clickBehaviour(int code, int x, int y) {
		int cod = code - CODE_START_OPTIONS_HEADER;
		if(cod < optionPageManager.getOptionPageList().length && cod >= 0) {
			optionPageManager.setCurrentOptionPageIndex(cod);
			updateActiveOptionPage();
		}
		updateOptionHeader();
	}

	@Override
	public void mouseWheelBehaviour(int rotation) {
		if(getMaximumScreenX() < getWidth()) {
			return;
		}
		setOffsetXBounded(getOffsetX() + rotation * ROTATION_MULTIPLIER);
	}
	
}
