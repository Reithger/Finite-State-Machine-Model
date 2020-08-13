package ui.page.imagepage;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;

import ui.FSMUI;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

//TODO: There's something screwy with the zoom in and zoom out in keeping the image centered, fix it
//TODO: Pop-out window so you can look at a larger copy of the image (identical ElementPanel, just bigger in a new frame)

public class ImagePage {

//---  Constants   ----------------------------------------------------------------------------

	private static final double MOVEMENT_FACTOR = .1;
	private static final double ZOOM_FACTOR = 1.1;
	private static final int DEFAULT_ORIGIN_X = 0;
	private static final int DEFAULT_ORIGIN_Y = 0;
	private static final double DEFAULT_ZOOM = 1.0;
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
	private static final char KEY_MOVE_RIGHT = 'd';
	private static final char KEY_MOVE_DOWN = 'w';
	private static final char KEY_MOVE_LEFT = 'a';
	private static final char KEY_MOVE_UP = 's';
	private static final char KEY_ZOOM_IN = 'q';
	private static final char KEY_ZOOM_OUT = 'e';
	private static final char KEY_REMOVE_IMAGE = 'f';
	private static final char KEY_RESET_POSITION = 'h';
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private ElementPanel p;
	/** Contains path for each image*/
	private ArrayList<String> images;
	private ArrayList<Image> imageReferences;
	private int currentImageIndex;
	private ArrayList<Double> zoom;
	private ArrayList<Integer> originX;
	private ArrayList<Integer> originY;
	//TODO: Change this to use the new SVI Panel offsetX/Y capabilities
	private int originUIX;
	private int originUIY;
	private boolean dragging;
	private FSMUI reference;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ImagePage(FSMUI ref) {
		images = new ArrayList<String>();
		imageReferences = new ArrayList<Image>();
		currentImageIndex = 0;
		zoom = new ArrayList<Double>();
		originX = new ArrayList<Integer>();
		originY = new ArrayList<Integer>();
		dragging = false;
		reference = ref;
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
		WindowFrame fra = new WindowFrame(DEFAULT_POPOUT_WIDTH, DEFAULT_POPOUT_HEIGHT) {
			@Override
			public void reactToResize() {
				
			}
		};
		PopoutDisplay pp = new PopoutDisplay(0, 0, DEFAULT_POPOUT_WIDTH, DEFAULT_POPOUT_HEIGHT, getCurrentImage());
		fra.reservePanel("default", "pan", pp);
	}

	
	private String formImageName(int index) {
		if(index < images.size())
			return images.get(index).substring(images.get(index).lastIndexOf("\\") + 1).substring(images.get(index).lastIndexOf("/") + 1);
		return null;
	}
	
	public void updateActiveImage(String newName) {
		p.removeElement(formImageName(currentImageIndex));
		images.set(currentImageIndex, FSMUI.ADDRESS_IMAGES + newName + ".jpg");
		imageReferences.set(currentImageIndex, p.retrieveImage(images.get(currentImageIndex)));
		refreshImage();
	}
	
	public void refreshImage() {
		if(images.size() != 0) {
			String imgName = formImageName(currentImageIndex);
			p.removeCachedImage(images.get(currentImageIndex));
			imageReferences.set(currentImageIndex, p.retrieveImage(images.get(currentImageIndex)));
			p.removeElement(imgName);
		}
		drawPage();
	}
	
	private void updateCurrentImage() {
		String imgName = formImageName(currentImageIndex);
		if(imgName != null)
			p.addImage(imgName, 10, originX.get(currentImageIndex), originY.get(currentImageIndex), false, images.get(currentImageIndex), zoom.get(currentImageIndex));
	}
	
	public void allotImage(String path) {
		images.add(path);
		imageReferences.add(p.retrieveImage(path));
		zoom.add(DEFAULT_ZOOM);
		originX.add(DEFAULT_ORIGIN_X);
		originY.add(DEFAULT_ORIGIN_Y);
		reference.updateImageHeader();
	}
	
	public void removeImage(String path) {
		if(images.size() == 0 || path == null) {
			return;
		}
		int ind = images.indexOf(path);
		p.removeElement(formImageName(ind));
		if(ind < currentImageIndex) {
			currentImageIndex--;
		}
		imageReferences.remove(ind);
		images.remove(ind);
		zoom.remove(ind);
		originX.remove(ind);
		originY.remove(ind);
		reference.updateImageHeader();
	}

	public void removeImage(int ind){
		if(ind >= images.size()) {
			return;
		}
		removeImage(images.get(ind));
	}
	
	//-- Change Values  ---------------------------------------
	
	public void resetPosition() {
		if(currentImageIndex < zoom.size()) {
			originY.set(currentImageIndex, 0);
			originX.set(currentImageIndex, 0);
			zoom.set(currentImageIndex, 1.0);
			updateCurrentImage();
		}
	}

	public void increaseOriginX() {
		Image img = getCurrentImage();
		if(img == null) {
			return;
		}
		int wid = (int)(img.getWidth(null) * zoom.get(currentImageIndex));
		originX.set(currentImageIndex, (int)(originX.get(currentImageIndex) + wid * MOVEMENT_FACTOR));
	}
	
	public void increaseOriginY() {
		Image img = getCurrentImage();
		if(img == null) {
			return;
		}
		int hei = (int)(img.getHeight(null) * zoom.get(currentImageIndex));
		originY.set(currentImageIndex, (int)(originY.get(currentImageIndex) + hei * MOVEMENT_FACTOR));
	}
	
	public void increaseZoom() {
		int preWid = (int)(p.getWidth() * zoom.get(currentImageIndex));
		int preHei = (int)(p.getHeight() * zoom.get(currentImageIndex));
		if(currentImageIndex < zoom.size())
			zoom.set(currentImageIndex, zoom.get(currentImageIndex) * ZOOM_FACTOR);
		int postWid = (int)(p.getWidth() * zoom.get(currentImageIndex));
		int postHei = (int)(p.getHeight() * zoom.get(currentImageIndex));
		int offsetX = (preWid - postWid) / 2;
		int offsetY = (preHei - postHei) / 2;
		originX.set(currentImageIndex, originX.get(currentImageIndex) - Math.abs(offsetX));
		originY.set(currentImageIndex, originY.get(currentImageIndex) - Math.abs(offsetY));
		updateCurrentImage();
	}
	
	public void decreaseOriginX() {
		Image img = getCurrentImage();
		if(img == null) {
			return;
		}
		int wid = (int)(img.getWidth(null) * zoom.get(currentImageIndex));
		originX.set(currentImageIndex, (int)(originX.get(currentImageIndex) - wid * MOVEMENT_FACTOR));
	}
	
	public void decreaseOriginY() {
		Image img = getCurrentImage();
		if(img == null) {
			return;
		}
		int hei = (int)(img.getHeight(null) * zoom.get(currentImageIndex));
		originY.set(currentImageIndex, (int)(originY.get(currentImageIndex) - hei * MOVEMENT_FACTOR));
	}

	public void decreaseZoom() {
		int preWid = (int)(p.getWidth() * zoom.get(currentImageIndex));
		int preHei = (int)(p.getHeight() * zoom.get(currentImageIndex));
		if(currentImageIndex < zoom.size())
			zoom.set(currentImageIndex, zoom.get(currentImageIndex) / ZOOM_FACTOR);
		int postWid = (int)(p.getWidth() * zoom.get(currentImageIndex));
		int postHei = (int)(p.getHeight() * zoom.get(currentImageIndex));
		int offsetX = (preWid - postWid) / 2;
		int offsetY =(preHei - postHei) / 2;
		originX.set(currentImageIndex, originX.get(currentImageIndex) + Math.abs(offsetX));
		originY.set(currentImageIndex, originY.get(currentImageIndex) + Math.abs(offsetY));
		updateCurrentImage();
	}
	
	//-- Drawing  ---------------------------------------------
	
	public void drawPage() {
		for(int i = 0; i < images.size(); i++) {
			int posX = (i == currentImageIndex ? originX.get(i) : 5000);
			int posY = (i == currentImageIndex ? originY.get(i) : 5000);
			String imageName = formImageName(i);
			if(!p.moveElement(imageName, posX, posY)) {
				p.addImage(imageName, 10, posX, posY, false, images.get(i), zoom.get(i));
			}
		}
		int imageSize = p.getWidth() / 20;
		int spacing = imageSize * 4 / 3;
		int posX = originUIX + (int)(p.getWidth() * (1 - UI_BOX_RATIO_X)) / 2;
		int posY = originUIY + spacing * 3 / 4;
		drawImageButton("ui_box_zoom_in", posX - spacing, posY, imageSize, imageSize, "/assets/ui/zoom_in.png", CODE_ZOOM_IN);
		drawImageButton("ui_box_zoom_out", posX + spacing, posY, imageSize, imageSize, "/assets/ui/zoom_out.png", CODE_ZOOM_OUT);
		posY += spacing;
		drawImageButton("ui_box_move_up", posX, posY, imageSize, imageSize, "/assets/ui/up_arrow.png", CODE_MOVE_UP);
		posY += spacing;
		drawImageButton("ui_box_move_left", posX - spacing, posY, imageSize, imageSize, "/assets/ui/left_arrow.png", CODE_MOVE_LEFT);
		drawImageButton("ui_box_move_right", posX + spacing, posY, imageSize, imageSize, "/assets/ui/right_arrow.png", CODE_MOVE_RIGHT);
		drawImageButton("ui_box_UI_ring", posX, posY, imageSize, imageSize, "/assets/ui/UI_ring.png", CODE_RESET_POSITION);
		posY += spacing;
		drawImageButton("ui_box_move_down", posX, posY, imageSize, imageSize, "/assets/ui/down_arrow.png", CODE_MOVE_DOWN);
	}

	private void addFraming() {
		int width = p.getWidth();
		int height = p.getHeight();
		p.addLine("frame_line_3", 15, 0, 0, 0, height, 5, Color.BLACK);
		p.addLine("frame_line_4", 15, 0, 0, width, 0, 2, Color.BLACK);
		p.addLine("frame_line_5", 15, width, height, width, 0, 5, Color.BLACK);
		p.addLine("frame_line_6", 15, width, height, 0, height, 5, Color.BLACK);
		p.addRectangle("rect_ui", 13, originUIX, originUIY, (int)(width * (1 - UI_BOX_RATIO_X)), (int)(height * (1 - UI_BOX_RATIO_Y)), false, Color.white, Color.black);
	}

//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCurrentImageIndex(int in) {
		currentImageIndex = in;
	}

	public void increaseCurrentImageIndex() {
		if(currentImageIndex + 1 < images.size()) {
			currentImageIndex++;
		}
	}
	
	public void decreaseCurrentImageIndex() {
		if(currentImageIndex - 1 >= 0) {
			currentImageIndex--;
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public ArrayList<String> getImages(){
		return images;
	}
	
	public int getCurrentImageIndex() {
		return currentImageIndex;
	}
	
	public Image getCurrentImage() {
		if(currentImageIndex < imageReferences.size())
			return imageReferences.get(currentImageIndex);
		return null;
	}
	
//---  Composite   ----------------------------------------------------------------------------
	
	private void drawImageButton(String name, int x, int y, int wid, int hei, String path, int code) {
		String imageName = name + "_image";
		if(!p.moveElement(imageName, x, y)) {
			double imgWid = p.retrieveImage(path).getWidth(null);
			double zoom = 1.0;
			if(imgWid != wid) {
				zoom = wid / imgWid;
			}
			p.addImage(imageName, 15, x, y, true, path, zoom);
		}
		String buttonName = name + "_button";
		if(!p.moveElement(buttonName, x, y)) {
			p.addButton(buttonName, 15, x, y, wid, hei, code, true);
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
		private static final char KEY_MOVE_DOWN = 'w';
		private static final char KEY_MOVE_LEFT = 'a';
		private static final char KEY_MOVE_UP = 's';
		private static final char KEY_ZOOM_IN = 'q';
		private static final char KEY_ZOOM_OUT = 'e';
		private static final char KEY_RESET_POSITION = 'h';

		private static final double MOVEMENT_FACTOR = .1;
		private static final double ZOOM_FACTOR = 1.1;
		
	//---  Instance Variables   ---------------------------------------------------------------
		
		private Image display;
		private double zoom;
		
		public PopoutDisplay(int x, int y, int width, int height, Image img) {
			super(x, y, width, height);
			display = img;
			zoom = 1;
		}
	
		public void keyBehaviour(char code) {
			switch(code) {
				case KEY_MOVE_RIGHT:
					setOffsetX((int)(getOffsetX() - getWidth() * zoom * MOVEMENT_FACTOR));
					break;
				case KEY_MOVE_LEFT:
					setOffsetX((int)(getOffsetX() + getWidth() * zoom * MOVEMENT_FACTOR));
					break;
				case KEY_MOVE_UP:
					setOffsetY((int)(getOffsetY() + getHeight() * zoom * MOVEMENT_FACTOR));
					break;
				case KEY_MOVE_DOWN:
					setOffsetY((int)(getOffsetY() - getHeight() * zoom * MOVEMENT_FACTOR));
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

		public void drawPopout() {
			addImage("img", 5, 0, 0, true, display);
			int imageSize = getWidth() / 20;
			int spacing = imageSize * 4 / 3;
			int posX = originUIX + (int)(getWidth() * (1 - UI_BOX_RATIO_X)) / 2;
			int posY = originUIY + spacing * 3 / 4;
			drawImageButton("ui_box_zoom_in", posX - spacing, posY, imageSize, imageSize, "/assets/ui/zoom_in.png", CODE_ZOOM_IN);
			drawImageButton("ui_box_zoom_out", posX + spacing, posY, imageSize, imageSize, "/assets/ui/zoom_out.png", CODE_ZOOM_OUT);
			posY += spacing;
			drawImageButton("ui_box_move_up", posX, posY, imageSize, imageSize, "/assets/ui/up_arrow.png", CODE_MOVE_UP);
			posY += spacing;
			drawImageButton("ui_box_move_left", posX - spacing, posY, imageSize, imageSize, "/assets/ui/left_arrow.png", CODE_MOVE_LEFT);
			drawImageButton("ui_box_move_right", posX + spacing, posY, imageSize, imageSize, "/assets/ui/right_arrow.png", CODE_MOVE_RIGHT);
			drawImageButton("ui_box_UI_ring", posX, posY, imageSize, imageSize, "/assets/ui/UI_ring.png", CODE_RESET_POSITION);
			posY += spacing;
			drawImageButton("ui_box_move_down", posX, posY, imageSize, imageSize, "/assets/ui/down_arrow.png", CODE_MOVE_DOWN);
		}

	}
}
