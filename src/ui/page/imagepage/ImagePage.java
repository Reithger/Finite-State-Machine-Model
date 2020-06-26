package ui.page.imagepage;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;

import visual.panel.ElementPanel;

public class ImagePage {

//---  Constants   ----------------------------------------------------------------------------

	private static final double MOVEMENT_FACTOR = .1;
	private static final double ZOOM_FACTOR = 1.1;
	private static final int DEFAULT_ORIGIN_X = 0;
	private static final int DEFAULT_ORIGIN_Y = 0;
	private static final double DEFAULT_ZOOM = 1.0;
	
	//-- Codes  -----------------------------------------------
	private static final int CODE_MOVE_RIGHT = 10;
	private static final int CODE_MOVE_DOWN = 11;
	private static final int CODE_MOVE_LEFT = 12;
	private static final int CODE_MOVE_UP = 13;
	private static final int CODE_ZOOM_IN = 14;
	private static final int CODE_ZOOM_OUT = 15;
	private static final int CODE_REMOVE_IMAGE = 16;
	private static final char KEY_MOVE_RIGHT = 'd';
	private static final char KEY_MOVE_DOWN = 'w';
	private static final char KEY_MOVE_LEFT = 'a';
	private static final char KEY_MOVE_UP = 's';
	private static final char KEY_ZOOM_IN = 'q';
	private static final char KEY_ZOOM_OUT = 'e';
	private static final char KEY_REMOVE_IMAGE = 'f';
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private ElementPanel p;
	private ArrayList<String> images;
	private ArrayList<Image> imageReferences;
	private int currentImageIndex;
	private ArrayList<Double> zoom;
	private ArrayList<Integer> originX;
	private ArrayList<Integer> originY;
	private boolean dragging;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ImagePage() {
		images = new ArrayList<String>();
		imageReferences = new ArrayList<Image>();
		currentImageIndex = 0;
		zoom = new ArrayList<Double>();
		originX = new ArrayList<Integer>();
		originY = new ArrayList<Integer>();
		dragging = false;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public ElementPanel generateElementPanel(int x, int y, int width, int height) {
		p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				switch(code) {
					case KEY_MOVE_RIGHT:
						increaseOriginX();
						break;
					case KEY_MOVE_LEFT:
						decreaseOriginX();
						break;
					case KEY_MOVE_UP:
						increaseOriginY();
						break;
					case KEY_MOVE_DOWN:
						decreaseOriginY();
						break;
					case KEY_ZOOM_IN:
						zoom.set(currentImageIndex, zoom.get(currentImageIndex) * ZOOM_FACTOR);
						removeCurrentImage();
						break;
					case KEY_ZOOM_OUT:
						zoom.set(currentImageIndex, zoom.get(currentImageIndex) / ZOOM_FACTOR);
						removeCurrentImage();
						break;
					case KEY_REMOVE_IMAGE:
						removeImage(images.get(currentImageIndex));
						break;
				}
				drawPage();
			}
			
			public void clickBehaviour(int code, int x, int y) {
				switch(code) {
					case CODE_MOVE_RIGHT:
						increaseOriginX();
						break;
					case CODE_MOVE_LEFT:
						decreaseOriginX();
						break;
					case CODE_MOVE_UP:
						increaseOriginY();
						break;
					case CODE_MOVE_DOWN:
						decreaseOriginY();
						break;
					case CODE_ZOOM_IN:
						zoom.set(currentImageIndex, zoom.get(currentImageIndex) * ZOOM_FACTOR);
						removeCurrentImage();
						break;
					case CODE_ZOOM_OUT:
						zoom.set(currentImageIndex, zoom.get(currentImageIndex) / ZOOM_FACTOR);
						removeCurrentImage();
						break;
					case CODE_REMOVE_IMAGE:
						removeImage(images.get(currentImageIndex));
						break;
				}
				drawPage();
			}
		};
		addFraming();
		return p;
	}
	
	private String formImageName(int index) {
		return "image_" + images.get(index).substring(images.get(index).lastIndexOf("/") + 1);
	}
	
	private void removeCurrentImage() {
		p.removeElement(formImageName(currentImageIndex));
	}
	
	public void allotImage(String path) {
		images.add(path);
		imageReferences.add(p.retrieveImage(path));
		zoom.add(DEFAULT_ZOOM);
		originX.add(DEFAULT_ORIGIN_X);
		originY.add(DEFAULT_ORIGIN_Y);
	}
	
	public void removeImage(String path) {
		if(images.size() == 0) {
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
	}

	private void addFraming() {
		int width = p.getWidth();
		int height = p.getHeight();
		p.addLine("frame_line_3", 15, 0, 0, 0, height, 5, Color.BLACK);
		p.addLine("frame_line_4", 15, 0, 0, width, 0, 2, Color.BLACK);
		p.addLine("frame_line_5", 15, width, height, width, 0, 5, Color.BLACK);
		p.addLine("frame_line_6", 15, width, height, 0, height, 5, Color.BLACK);
	}

//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCurrentImageIndex(int in) {
		currentImageIndex = in;
	}

	public void increaseOriginX() {
		Image img = getCurrentImage();
		int wid = (int)(img.getWidth(null) * zoom.get(currentImageIndex));
		originX.set(currentImageIndex, (int)(originX.get(currentImageIndex) + wid * MOVEMENT_FACTOR));
	}
	
	public void increaseOriginY() {
		Image img = getCurrentImage();
		int hei = (int)(img.getHeight(null) * zoom.get(currentImageIndex));
		originY.set(currentImageIndex, (int)(originY.get(currentImageIndex) + hei * MOVEMENT_FACTOR));
	}
	
	public void decreaseOriginX() {
		Image img = getCurrentImage();
		int wid = (int)(img.getWidth(null) * zoom.get(currentImageIndex));
		originX.set(currentImageIndex, (int)(originX.get(currentImageIndex) - wid * MOVEMENT_FACTOR));
	}
	
	public void decreaseOriginY() {
		Image img = getCurrentImage();
		int hei = (int)(img.getHeight(null) * zoom.get(currentImageIndex));
		originY.set(currentImageIndex, (int)(originY.get(currentImageIndex) - hei * MOVEMENT_FACTOR));
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public ArrayList<String> getImages(){
		return images;
	}
	
	public int getCurrentImageHeader() {
		return currentImageIndex;
	}
	
	public Image getCurrentImage() {
		return imageReferences.get(currentImageIndex);
	}
	
}
