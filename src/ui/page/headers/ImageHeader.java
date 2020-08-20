package ui.page.headers;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import ui.FSMUI;
import visual.panel.ElementPanel;

public class ImageHeader extends ElementPanel{

	private final static int CODE_START_IMAGES_HEADER = 150;
	private final static Font IMAGE_HEADER_FONT = new Font("Serif", Font.BOLD, 12);
	private final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
	
	public ImageHeader(int x, int y, int wid, int hei) {
		super(x, y, wid, hei);
		setScrollBarVertical(false);
		setScrollBarHorizontal(false);
		addLine("line_3", 15, false, 0, 0, 0, hei, 2, Color.BLACK);
		addLine("line_6", 15, false, wid, hei, 0, hei, 5, Color.BLACK);
		
	}

	public void update() {
		ArrayList<String> images = imagePage.getImages();
		removeElementPrefixed("header");
		if(images.size() == 0) {
			int posX = getWidth()/ 2;
			int posY = getHeight() / 2;
			int wid = getWidth() / 3;
			int hei = getHeight() * 2 / 3;
			handleRectangle("stand_in_header_rect", 10, posX, posY, wid, hei, Color.gray, Color.black);
			handleText("stand_in_header_text", posX, posY, wid, hei, IMAGE_HEADER_FONT, "No images currently available");
		}
		else {
			removeElementPrefixed("stand_in");
			for(int i = 0 ; i < images.size() + 1; i++) {
				int posX = FSMUI.WINDOW_WIDTH / 2/ 10 + i * (FSMUI.WINDOW_WIDTH / 2 / 5);
				int posY = (int)(FSMUI.WINDOW_HEIGHT * (1.0 - FSMUI.PANEL_RATIO_VERTICAL) / 2);
				int wid = FSMUI.WINDOW_WIDTH / 2 / 6;
				int hei = (int)(FSMUI.WINDOW_HEIGHT * (1 - FSMUI.PANEL_RATIO_VERTICAL) * 2 / 3);
				if(i == images.size()) {
					handleRectangle("header_rect_" + i, 10, posX, posY, 1, hei, COLOR_TRANSPARENT, COLOR_TRANSPARENT);
					break;
				}
				if(i == imagePage.getCurrentImageIndex()) {
					handleRectangle("header_rect_active", 12, posX, posY, wid, hei, Color.green, Color.black);
				}
				handleRectangle("header_rect_" + i, 10, posX, posY, wid, hei, Color.gray, Color.black);
				handleButton("header_butt_" + i, posX, posY, wid, hei, i);
				String nom = images.get(i).substring(images.get(i).lastIndexOf("\\") + 1).substring(images.get(i).lastIndexOf("/") + 1);
				handleText("header_text_" + i, posX, posY, wid, hei, IMAGE_HEADER_FONT, nom);
			}
		}
	}
	
	@Override
	public void keyBehaviour(char code) {
		
	}

	@Override
	public void clickBehaviour(int code, int x, int y) {
		int cod = code - CODE_START_IMAGES_HEADER;
		if(cod < imagePage.getImages().size() && cod >= 0) {
			imagePage.setCurrentImageIndex(cod);
			updateImagePanel();
		}
		updateImageHeader();
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
