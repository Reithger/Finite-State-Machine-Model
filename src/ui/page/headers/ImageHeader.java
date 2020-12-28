package ui.page.headers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;

import ui.FSMUI;
import ui.page.imagepage.ImagePage;

public class ImageHeader extends HeaderBase{

	private final static Font IMAGE_HEADER_FONT = new Font("Serif", Font.BOLD, 12);
	private final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
	
	private ImagePage imagePage;
	
	public ImageHeader(int x, int y, int wid, int hei, ImagePage refer) {
		super(x, y, wid, hei);
		imagePage = refer;
		setScrollBarVertical(false);
		setScrollBarHorizontal(false);
		addLine("line_3", 15, true, 0, 0, 0, hei, 2, Color.BLACK);
		addLine("line_6", 15, true, wid, hei, 0, hei, 5, Color.BLACK);
		
	}

	public void update() {
		ArrayList<Image> images = imagePage.getImages();
		removeElementPrefixed("header");
		if(images.size() == 0) {
			int posX = getWidth()/ 2;
			int posY = getHeight() / 2;
			int wid = getWidth() / 3;
			int hei = getHeight() * 2 / 3;
			handleRectangle("stand_in_header_rect", false, 10, posX, posY, wid, hei, Color.gray, Color.black);
			handleText("stand_in_header_text", false, posX, posY, wid, hei, IMAGE_HEADER_FONT, "No images currently available");
		}
		else {
			removeElementPrefixed("stand_in");
			for(int i = 0 ; i < images.size() + 1; i++) {
				int posX = FSMUI.WINDOW_WIDTH / 2/ 10 + i * (FSMUI.WINDOW_WIDTH / 2 / 5);
				int posY = (int)(FSMUI.WINDOW_HEIGHT * (1.0 - FSMUI.PANEL_RATIO_VERTICAL) / 2);
				int wid = FSMUI.WINDOW_WIDTH / 2 / 6;
				int hei = (int)(FSMUI.WINDOW_HEIGHT * (1 - FSMUI.PANEL_RATIO_VERTICAL) * 2 / 3);
				if(i == images.size()) {
					handleRectangle("header_rect_" + i, false, 10, posX, posY, 1, hei, COLOR_TRANSPARENT, COLOR_TRANSPARENT);
					break;
				}
				if(i == imagePage.getCurrentImageIndex()) {
					handleRectangle("header_rect_active", false, 12, posX, posY, wid, hei, Color.green, Color.black);
				}
				handleRectangle("header_rect_" + i, false, 10, posX, posY, wid, hei, Color.gray, Color.black);
				handleButton("header_butt_" + i, false, posX, posY, wid, hei, i);
				//String nom = images.get(i).substring(images.get(i).lastIndexOf("\\") + 1).substring(images.get(i).lastIndexOf("/") + 1);
				//handleText("header_text_" + i, false, posX, posY, wid, hei, IMAGE_HEADER_FONT, nom);
			}
		}
	}
	
	public void clickBehaviour(int code, int x, int y) {
		if(code < imagePage.getImages().size() && code >= 0) {
			imagePage.setCurrentImageIndex(code);
			imagePage.drawPage();
		}
		update();
	}
	


}
