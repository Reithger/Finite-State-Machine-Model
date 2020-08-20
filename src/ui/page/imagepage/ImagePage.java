package ui.page.imagepage;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;

import ui.FSMUI;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

//TODO: There's something screwy with the zoom in and zoom out in keeping the image centered, fix it

public class ImagePage {

//---  Constants   ----------------------------------------------------------------------------

	private static final double UI_BOX_RATIO_Y = 3 / 4.0;
	private static final double UI_BOX_RATIO_X = 4 / 5.0;
	
	private static final int DEFAULT_POPOUT_WIDTH = 800;
	private static final int DEFAULT_POPOUT_HEIGHT = 800;
	
	//-- Codes  -----------------------------------------------
	private static final int CODE_MOVE_RIGHT = 10;
	private static final int CODE_MOVE_DOWN = 11;
	private static final int CODE_MOVE_LEFT = 12;
	private static final int CODE_MOVE_UP = 13;
	private static final int CODE_ZOOM_IN = 14;
	private static final int CODE_ZOOM_OUT = 15;
	private static final int CODE_REMOVE_IMAGE = 16;
	private static final int CODE_RESET_POSITION = 17;
	private static final int CODE_POPOUT = 18;
	private static final char KEY_MOVE_RIGHT = 'd';
	private static final char KEY_MOVE_DOWN = 's';
	private static final char KEY_MOVE_LEFT = 'a';
	private static final char KEY_MOVE_UP = 'w';
	private static final char KEY_ZOOM_IN = 'q';
	private static final char KEY_ZOOM_OUT = 'e';
	private static final char KEY_REMOVE_IMAGE = 'f';
	private static final char KEY_RESET_POSITION = 'h';
	private static final char KEY_POPOUT = 'p';
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private ElementPanel p;
	private ArrayList<ImageDisplay> images;
	private int currentImageIndex;
	private int originUIX;
	private int originUIY;
	private FSMUI reference;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ImagePage(FSMUI ref) {
		images = new ArrayList<ImageDisplay>();
		reference = ref;
		currentImageIndex = 0;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public ElementPanel generateElementPanel(int x, int y, int width, int height) {
		p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
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
					case KEY_REMOVE_IMAGE:
						removeImage(currentImageIndex);
						break;
					case KEY_RESET_POSITION:
						resetPosition();
						break;
					case KEY_POPOUT:
						generatePopout();
						break;
				}
				drawPage();
			}
			
			public void clickBehaviour(int code, int x, int y) {
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
					case CODE_REMOVE_IMAGE:
						removeImage(currentImageIndex);
						break;
					case CODE_RESET_POSITION:
						resetPosition();
						break;
					case CODE_POPOUT:
						generatePopout();
						break;
				}
				drawPage();
			}
		};
		originUIX = (int)(width * UI_BOX_RATIO_X);
		originUIY = (int)(height  * UI_BOX_RATIO_Y);
		addFraming();
		p.setScrollBarHorizontal(false);
		p.setScrollBarVertical(false);
		return p;
	}

	public void generatePopout() {
		PopoutDisplay pp = new PopoutDisplay(0, 0, DEFAULT_POPOUT_WIDTH, DEFAULT_POPOUT_HEIGHT, images.get(getCurrentImageIndex()).getImagePath());
		pp.setScrollBarVertical(false);
		pp.setScrollBarHorizontal(false);
		WindowFrame fra = new WindowFrame(DEFAULT_POPOUT_WIDTH, DEFAULT_POPOUT_HEIGHT) {
			@Override
			public void reactToResize() {
				pp.removeElementPrefixed("");
				pp.resize(getWidth(), getHeight());
				pp.drawPopout();
			}
		};
		fra.setExitOnClose(false);
		fra.setResizable(true);
		fra.reservePanel("default", "pan", pp);
	}

	public void replaceActiveImage(String newName) {
		p.removeElement(getCurrentImageDisplay().getImagePath());
		getCurrentImageDisplay().setImagePath(FSMUI.ADDRESS_IMAGES + newName + ".jpg");
	}
	
	public void refreshActiveImage() {
		getCurrentImageDisplay().refresh();
	}
	
	public void allotImage(String path) {
		images.add(new ImageDisplay(path, p));
		reference.updateImageHeader();
	}
	
	public void removeImage(int ind){
		if(ind < 0 || ind >= images.size()) {
			return;
		}
		images.get(ind).clear();
		images.remove(ind);
	}
	
	//-- Change Values  ---------------------------------------
	
	public void resetPosition() {
		if(currentImageIndex < images.size()) {
			getCurrentImageDisplay().resetPosition();
		}
	}

	public void increaseOriginX() {
		if(currentImageIndex < images.size()) {
			getCurrentImageDisplay().increaseOriginX();
		}
	}
	
	public void increaseOriginY() {
		if(currentImageIndex < images.size()) {
			getCurrentImageDisplay().increaseOriginY();
		}
	}
	
	public void increaseZoom() {
		if(currentImageIndex < images.size()) {
			getCurrentImageDisplay().increaseZoom();
		}
	}
	
	public void decreaseOriginX() {
		if(currentImageIndex < images.size()) {
			getCurrentImageDisplay().decreaseOriginX();
		}
	}
	
	public void decreaseOriginY() {
		if(currentImageIndex < images.size()) {
			getCurrentImageDisplay().decreaseOriginY();
		}
	}

	public void decreaseZoom() {
		if(currentImageIndex < images.size()) {
			getCurrentImageDisplay().decreaseZoom();
		}
	}
	
	//-- Drawing  ---------------------------------------------
	
	public void drawPage() {
		if(images.size() > 0) {
			ImageDisplay img = images.get(currentImageIndex);
			p.addImage("img", 10, false, 0, 0, false, img.getImage(), img.getZoom());
		}
		addFraming();
		int imageSize = p.getWidth() / 20;
		int spacing = imageSize * 4 / 3;
		int posX = originUIX + (int)(p.getWidth() * (1 - UI_BOX_RATIO_X)) / 2;
		int posY = originUIY + spacing * 3 / 4;
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
		
		drawImageButton("ui_box_popout", true, p.getWidth() - imageSize, imageSize, imageSize * 3 / 2, imageSize * 3 / 2, "/assets/ui/popout.png", CODE_POPOUT);
		p.addRectangle("rect_ui_popout", 13, true, p.getWidth() - imageSize, imageSize, imageSize * 3 / 2, imageSize * 3 / 2, true, Color.white, Color.black);
	}

	private void addFraming() {
		int width = p.getWidth();
		int height = p.getHeight();
		p.addLine("frame_line_3", 15, true, 0, 0, 0, height, 5, Color.BLACK);
		p.addLine("frame_line_4", 15, true,  0, 1, width, 1, 2, Color.BLACK);
		p.addLine("frame_line_5", 15, true,  width, height, width, 0, 5, Color.BLACK);
		p.addLine("frame_line_6", 15, true,  width, height, 0, height, 5, Color.BLACK);
		p.addRectangle("rect_ui", 13, true,  originUIX, originUIY, (int)(width * (1 - UI_BOX_RATIO_X)), (int)(height * (1 - UI_BOX_RATIO_Y)), false, Color.white, Color.black);
	}

//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCurrentImageIndex(int in) {
		currentImageIndex = in;
		p.removeElement("img");
		refreshActiveImage();
	}

	public void increaseCurrentImageIndex() {
		if(currentImageIndex + 1 < images.size()) {
			setCurrentImageIndex(currentImageIndex + 1);
		}
	}
	
	public void decreaseCurrentImageIndex() {
		if(currentImageIndex - 1 >= 0) {
			setCurrentImageIndex(currentImageIndex - 1);
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public ArrayList<String> getImages(){
		ArrayList<String> out = new ArrayList<String>();
		for(int i = 0; i < images.size(); i++) {
			out.add(images.get(i).getImagePath());
		}
		return out;
	}
	
	public int getCurrentImageIndex() {
		return currentImageIndex;
	}
	
	public ImageDisplay getCurrentImageDisplay() {
		if(currentImageIndex < images.size())
			return images.get(currentImageIndex);
		return null;
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
	
//---  Support Classes   ----------------------------------------------------------------------
	
	public class PopoutDisplay extends ElementPanel{
		
	//---  Constants   ------------------------------------------------------------------------
		
		private static final int CODE_MOVE_RIGHT = 10;
		private static final int CODE_MOVE_DOWN = 11;
		private static final int CODE_MOVE_LEFT = 12;
		private static final int CODE_MOVE_UP = 13;
		private static final int CODE_ZOOM_IN = 14;
		private static final int CODE_ZOOM_OUT = 15;
		private static final int CODE_RESET_POSITION = 17;
		private static final char KEY_MOVE_RIGHT = 'd';
		private static final char KEY_MOVE_DOWN = 's';
		private static final char KEY_MOVE_LEFT = 'a';
		private static final char KEY_MOVE_UP = 'w';
		private static final char KEY_ZOOM_IN = 'q';
		private static final char KEY_ZOOM_OUT = 'e';
		private static final char KEY_RESET_POSITION = 'h';

		private static final double MOVEMENT_FACTOR = .1;
		private static final double ZOOM_FACTOR = 1.1;
		
		private static final int MAX_UI_SIZE = 30;
		
	//---  Instance Variables   ---------------------------------------------------------------
		
		private Image display;
		private double zoom;
		
		public PopoutDisplay(int x, int y, int width, int height, String img) {
			super(x, y, width, height);
			display = retrieveImage(img);
			zoom = 1;
		}
	
		public void keyBehaviour(char code) {
			int offset = 0;
			switch(code) {
				case KEY_MOVE_RIGHT:
					offset = (int)(getOffsetX() - getWidth() * zoom * MOVEMENT_FACTOR);
					setOffsetX(offset);
					break;
				case KEY_MOVE_LEFT:
					offset = (int)(getOffsetX() + getWidth() * zoom * MOVEMENT_FACTOR);
					setOffsetX(offset);
					break;
				case KEY_MOVE_UP:
					offset = (int)(getOffsetY() + getHeight() * zoom * MOVEMENT_FACTOR);
					setOffsetY(offset);
					break;
				case KEY_MOVE_DOWN:
					offset = (int)(getOffsetY() - getHeight() * zoom * MOVEMENT_FACTOR);
					setOffsetY(offset);
					break;
				case KEY_ZOOM_IN:
					zoom *= ZOOM_FACTOR;
					break;
				case KEY_ZOOM_OUT:
					zoom /= ZOOM_FACTOR;
					break;
				case KEY_RESET_POSITION:
					setOffsetX(0);
					setOffsetY(0);
					zoom = 1;
					break;
			}
			drawPopout();
		}
		
		public void clickBehaviour(int code, int x, int y) {
			switch(code) {
				case CODE_MOVE_RIGHT:
					setOffsetX((int)(getOffsetX() - getWidth() * zoom * MOVEMENT_FACTOR));
					break;
				case CODE_MOVE_LEFT:
					setOffsetX((int)(getOffsetX() + getWidth() * zoom * MOVEMENT_FACTOR));
					break;
				case CODE_MOVE_UP:
					setOffsetY((int)(getOffsetY() + getHeight() * zoom * MOVEMENT_FACTOR));
					break;
				case CODE_MOVE_DOWN:
					setOffsetY((int)(getOffsetY() - getHeight() * zoom * MOVEMENT_FACTOR));
					break;
				case CODE_ZOOM_IN:
					zoom *= ZOOM_FACTOR;
					break;
				case CODE_ZOOM_OUT:
					zoom /= ZOOM_FACTOR;
					break;
				case CODE_RESET_POSITION:
					setOffsetX(0);
					setOffsetY(0);
					zoom = 1;
					break;
			}
			drawPopout();
		}

		public void resetView() {
			removeElementPrefixed("");
			drawPopout();
		}
		
		public void drawPopout() {
			addImage("img", 5, false,  0, 0, false, display, zoom);
			int imageSize = getWidth() / 20 > MAX_UI_SIZE ? MAX_UI_SIZE : getWidth() / 20;
			int spacing = imageSize * 4 / 3;
			int posX = spacing * 5 / 3;// + (int)(getWidth() * (1 - UI_BOX_RATIO_X)) / 2;
			int posY = spacing * 3 / 4;
			addRectangle("rect_ui", 13, true,  posX - spacing * 3 / 2, posY - spacing * 2 / 3, spacing * 3, spacing * 9 / 2, false, Color.white, Color.black);
			drawImageButton("ui_box_zoom_in", true, posX - spacing, posY, imageSize, imageSize, "/assets/ui/zoom_in.png", CODE_ZOOM_IN);
			drawImageButton("ui_box_zoom_out", true,posX + spacing, posY, imageSize, imageSize, "/assets/ui/zoom_out.png", CODE_ZOOM_OUT);
			posY += spacing;
			drawImageButton("ui_box_move_up", true,posX, posY, imageSize, imageSize, "/assets/ui/up_arrow.png", CODE_MOVE_UP);
			posY += spacing;
			drawImageButton("ui_box_move_left", true, posX - spacing, posY, imageSize, imageSize, "/assets/ui/left_arrow.png", CODE_MOVE_LEFT);
			drawImageButton("ui_box_move_right", true, posX + spacing, posY, imageSize, imageSize, "/assets/ui/right_arrow.png", CODE_MOVE_RIGHT);
			drawImageButton("ui_box_UI_ring", true, posX, posY, imageSize, imageSize, "/assets/ui/UI_ring.png", CODE_RESET_POSITION);
			posY += spacing;
			drawImageButton("ui_box_move_down", true, posX, posY, imageSize, imageSize, "/assets/ui/down_arrow.png", CODE_MOVE_DOWN);
		}
		
		private void drawImageButton(String name, boolean frame, int x, int y, int wid, int hei, String path, int code) {
			String imageName = name + "_image";
			if(!moveElement(imageName, x, y)) {
				double imgWid = retrieveImage(path).getWidth(null);
				double zoom = 1.0;
				if(imgWid != wid) {
					zoom = wid / imgWid;
				}
				addImage(imageName, 15, frame,  x, y, true, path, zoom);
			}
			String buttonName = name + "_button";
			if(!moveElement(buttonName, x, y)) {
				addButton(buttonName, 15, frame,  x, y, wid, hei, code, true);
			}
		}
		
	}
}
