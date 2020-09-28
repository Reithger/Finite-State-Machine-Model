package ui.page.imagepage;

import java.awt.Color;
import java.awt.Image;

import ui.page.popups.PopoutImageDisplay;
import visual.panel.ElementPanel;

public class ImageDisplay {

//---  Constant Values   ----------------------------------------------------------------------
	
	private final static double DEFAULT_ZOOM = 1.0;
	private static final double MOVEMENT_FACTOR = .1;
	private static final double ZOOM_FACTOR = 1.1;
	
	private static final int DEFAULT_POPOUT_WIDTH = 800;
	private static final int DEFAULT_POPOUT_HEIGHT = 800;
	
	private static final double UI_BOX_RATIO_Y = 3 / 4.0;
	private static final double UI_BOX_RATIO_X = 4 / 5.0;
	public static final String IMAGE_NAME = "img";
	
	//-- Codes  -----------------------------------------------
	private static final int CODE_MOVE_RIGHT = 10;
	private static final int CODE_MOVE_DOWN = 11;
	private static final int CODE_MOVE_LEFT = 12;
	private static final int CODE_MOVE_UP = 13;
	private static final int CODE_ZOOM_IN = 14;
	private static final int CODE_ZOOM_OUT = 15;
	private static final int CODE_RESET_POSITION = 17;
	private static final int CODE_POPOUT = 18;
	private static final int CODE_HIDE_UI = 19;
	private static final char KEY_MOVE_RIGHT = 'd';
	private static final char KEY_MOVE_DOWN = 's';
	private static final char KEY_MOVE_LEFT = 'a';
	private static final char KEY_MOVE_UP = 'w';
	private static final char KEY_ZOOM_IN = 'q';
	private static final char KEY_ZOOM_OUT = 'e';
	private static final char KEY_RESET_POSITION = 'h';
	private static final char KEY_POPOUT = 'p';
	private static final char KEY_HIDE_UI = 't';
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private String imageName;
	private String imagePath;
	private Image reference;
	private double zoom;
	private ElementPanel p;
	private int originUIX;
	private int originUIY;
	private boolean popout;
	private boolean hideUI;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ImageDisplay(String path, ElementPanel in) {
		imagePath = path;
		p = in;
		zoom = 1;
		originUIX = (int)(in.getWidth() * UI_BOX_RATIO_X);
		originUIY = (int)(in.getHeight()  * UI_BOX_RATIO_Y);
		refresh();
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void processClickInput(int code) {
		switch(code) {
			case CODE_MOVE_RIGHT:
				decreaseOriginX();
				break;
			case CODE_MOVE_LEFT:
				increaseOriginX();
				break;
			case CODE_MOVE_UP:
				increaseOriginY();
				break;
			case CODE_MOVE_DOWN:
				decreaseOriginY();
				break;
			case CODE_ZOOM_IN:
				increaseZoom();
				break;
			case CODE_ZOOM_OUT:
				decreaseZoom();
				break;
			case CODE_RESET_POSITION:
				resetPosition();
				break;
			case CODE_POPOUT:
				generatePopout();
				break;
			case CODE_HIDE_UI:
				hideUI = !hideUI;
				p.removeElementPrefixed("");
				drawPage();
				break;
			}
	}
	
	public void processKeyInput(char code) {
		switch(code) {
			case KEY_MOVE_RIGHT:
				decreaseOriginX();
				break;
			case KEY_MOVE_LEFT:
				increaseOriginX();
				break;
			case KEY_MOVE_UP:
				increaseOriginY();
				break;
			case KEY_MOVE_DOWN:
				decreaseOriginY();
				break;
			case KEY_ZOOM_IN:
				increaseZoom();
				break;
			case KEY_ZOOM_OUT:
				decreaseZoom();
				break;
			case KEY_RESET_POSITION:
				resetPosition();
				break;
			case KEY_POPOUT:
				generatePopout();
				break;
			case KEY_HIDE_UI:
				hideUI = !hideUI;
				p.removeElementPrefixed("");
				drawPage();
				break;
			}
	}
	
	public void generatePopout() {
		PopoutImageDisplay pop = new PopoutImageDisplay(this.getImagePath());
		pop.resize(DEFAULT_POPOUT_WIDTH, DEFAULT_POPOUT_HEIGHT);
	}
	
	public void drawPage() {
		p.addImage(IMAGE_NAME, 10, false, 0, 0, false, getImage(), getZoom());

		int usedWidth = popout ? DEFAULT_POPOUT_WIDTH : p.getWidth();
		int imageSize = usedWidth / 20;
		imageSize = imageSize > 30 ? 30 : imageSize;
		
		if(!hideUI) {
			int usedHeight = popout ? DEFAULT_POPOUT_HEIGHT : p.getHeight();
			int spacing = imageSize * 4 / 3;
			int posX = originUIX + (int)(usedWidth * (1 - UI_BOX_RATIO_X)) / 2;
			int posY = originUIY + spacing * 3 / 4;
	
			p.addRectangle("rect_ui", 13, true,  originUIX, originUIY, (int)(usedWidth * (1 - UI_BOX_RATIO_X)), (int)(usedHeight * (1 - UI_BOX_RATIO_Y)), false, Color.white, Color.black);
			
			drawImageButton("ui_box_zoom_in", true, posX - spacing, posY, imageSize, imageSize, "/assets/ui/zoom_in.png", CODE_ZOOM_IN);
			drawImageButton("ui_box_zoom_out", true, posX + spacing, posY, imageSize, imageSize, "/assets/ui/zoom_out.png", CODE_ZOOM_OUT);
			posY += spacing;
			drawImageButton("ui_box_move_up", true, posX, posY, imageSize, imageSize, "/assets/ui/up_arrow.png", CODE_MOVE_UP);
			posY += spacing;
			drawImageButton("ui_box_move_left", true, posX - spacing, posY, imageSize, imageSize, "/assets/ui/left_arrow.png", CODE_MOVE_LEFT);
			drawImageButton("ui_box_move_right", true, posX + spacing, posY, imageSize, imageSize, "/assets/ui/right_arrow.png", CODE_MOVE_RIGHT);
			drawImageButton("ui_box_UI_ring", true, posX, posY, imageSize, imageSize, "/assets/ui/UI_ring.png", CODE_RESET_POSITION);
			posY += spacing;
			drawImageButton("ui_box_move_down", true, posX, posY, imageSize, imageSize, "/assets/ui/down_arrow.png", CODE_MOVE_DOWN);
			
			if(!popout) {
				drawImageButton("ui_box_popout", true, p.getWidth() - imageSize, imageSize, imageSize * 3 / 2, imageSize * 3 / 2, "/assets/ui/popout.png", CODE_POPOUT);
				p.addRectangle("rect_ui_popout", 13, true, p.getWidth() - imageSize, imageSize, imageSize * 3 / 2, imageSize * 3 / 2, true, Color.white, Color.black);
			}
		}
		drawImageButton("ui_hide_ui", true, p.getWidth() - (popout ? 1 : 3) * imageSize, imageSize, imageSize * 3 / 2, imageSize * 3 / 2, hideUI ? "/assets/ui/eye_open-2.png" : "/assets/ui/eye_closed-2.png", CODE_HIDE_UI);
		p.addRectangle("rect_hide_ui", 13, true, p.getWidth() - (popout ? 1 : 3) * imageSize, imageSize, imageSize * 3 / 2, imageSize * 3 / 2, true, Color.white, Color.black);
	}

	public void refresh() {
		clear();
		reference = p.retrieveImage(imagePath);
		imageName = formatImageName(imagePath);
	}
	
	public void clear() {
		p.removeCachedImage(imagePath);
		p.removeElement(IMAGE_NAME);
		p.removeElementPrefixed("");
	}
	
	public void resetPosition() {
		p.setOffsetX(0);
		p.setOffsetY(0);
		zoom = DEFAULT_ZOOM;
		p.removeElement(IMAGE_NAME);
		drawPage();
	}
	
	//---  Composite   ----------------------------------------------------------------------------
		
	private void drawImageButton(String name, boolean frame, int x, int y, int wid, int hei, String path, int code) {
		String imageName = name + "_image";
		if(!p.moveElement(imageName, x, y)) {
			double imgWid = p.retrieveImage(path).getWidth(null);
			double zoom = 1.0;
			if(imgWid != wid) {
				zoom = wid / imgWid;
			}
			p.addImage(imageName,15, frame, x, y, true, path, zoom);
		}
		String buttonName = name + "_button";
		if(!p.moveElement(buttonName, x, y)) {
			p.addButton(buttonName, 15, frame,  x, y, wid, hei, code, true);
		}
	}
		
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void designatePopout() {
		popout = true;
		originUIX = 0;
		originUIY = 0;
	}
	
	public void setImagePath(String in) {
		p.removeCachedImage(imagePath);
		imagePath = in;
		refresh();
	}
	
	public void increaseOriginX() {
		p.setOffsetX((int)(p.getOffsetX() + reference.getWidth(null) * zoom * MOVEMENT_FACTOR));
	}

	public void increaseOriginY() {
		p.setOffsetY((int)(p.getOffsetY() + reference.getHeight(null) * zoom * MOVEMENT_FACTOR));
	}
	
	public void decreaseOriginX() {
		p.setOffsetX((int)(p.getOffsetX() - reference.getWidth(null) * zoom * MOVEMENT_FACTOR));
	}
	
	public void decreaseOriginY() {
		p.setOffsetY((int)(p.getOffsetY() - reference.getHeight(null) * zoom * MOVEMENT_FACTOR));
	}
	
	public void dragOriginX(int amount) {
		p.setOffsetX(p.getOffsetX() + amount);
	}
	
	public void dragOriginY(int amount) {
		p.setOffsetY(p.getOffsetY() + amount);
	}
	
	public void increaseZoom() {
		zoom *= ZOOM_FACTOR;
		p.removeElement(IMAGE_NAME);
	}
	
	public void decreaseZoom() {
		zoom /= ZOOM_FACTOR;
		p.removeElement(IMAGE_NAME);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getImageName() {
		return imageName;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public Image getImage() {
		return reference;
	}
	
	public double getZoom() {
		return zoom;
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	private String formatImageName(String in) {
		return in.substring(in.lastIndexOf("\\") + 1).substring(in.lastIndexOf("/") + 1);
	}
	
}
