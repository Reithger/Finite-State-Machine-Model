package ui.page.imagepage;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;

import ui.FSMUI;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

//TODO: There's something screwy with the zoom in and zoom out in keeping the image centered, fix it
//TODO: Default zoomout range on creation so you can actually see the thing
public class ImagePage {

//---  Instance Variables   -------------------------------------------------------------------
	
	private ElementPanel p;
	private ArrayList<ImageDisplay> images;
	private int currentImageIndex;
	private FSMUI reference;
	
	private int dragStartX;
	private int dragStartY;
	private boolean dragState;
	
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
				getCurrentImageDisplay().processKeyInput(code);
				drawPage();
			}
			
			public void clickBehaviour(int code, int x, int y) {
				getCurrentImageDisplay().processClickInput(code);
				drawPage();
			}

			@Override
			public void mouseWheelBehaviour(int scroll) {
				if(scroll < 0) {
					getCurrentImageDisplay().increaseZoom();
				}
				else {
					getCurrentImageDisplay().decreaseZoom();
				}
				drawPage();
			}
			
			public void clickPressBehaviour(int code, int x, int y) {
				dragStartX = x;
				dragStartY = y;
				dragState = true;
			}
			
			public void clickReleaseBehaviour(int code, int x, int y) {
				dragState = false;
			}
			
			@Override
			public void dragBehaviour(int code, int x, int y) {
				if(dragState) {
					getCurrentImageDisplay().dragOriginX(x - dragStartX);
					getCurrentImageDisplay().dragOriginY(y - dragStartY);
					dragStartX = x;
					dragStartY = y;
				}
			}
		};

		addFraming();
		p.setScrollBarHorizontal(false);
		p.setScrollBarVertical(false);
		return p;
	}

	public void generatePopout() {
		getCurrentImageDisplay().generatePopout();
	}

	public void replaceActiveImage(String newName) {
		p.removeElement(getCurrentImageDisplay().getImagePath());
		getCurrentImageDisplay().setImagePath(FSMUI.ADDRESS_IMAGES + newName + ".jpg");
	}
	
	public void refreshActiveImage() {
		getCurrentImageDisplay().refresh();
		drawPage();
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
	
	//-- Drawing  ---------------------------------------------
	
	public void drawPage() {
		if(images.size() > 0 && p.getElement(ImageDisplay.IMAGE_NAME) == null) {
			getCurrentImageDisplay().drawPage();
		}
		addFraming();
	}

	private void addFraming() {
		int width = p.getWidth();
		int height = p.getHeight();
		p.addLine("frame_line_3", 15, true, 0, 0, 0, height, 5, Color.BLACK);
		p.addLine("frame_line_4", 15, true,  0, 1, width, 1, 2, Color.BLACK);
		p.addLine("frame_line_5", 15, true,  width, height, width, 0, 5, Color.BLACK);
		p.addLine("frame_line_6", 15, true,  width, height, 0, height, 5, Color.BLACK);
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
	
}
