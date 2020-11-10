package ui.page.imagepage;

import java.awt.Color;
import java.util.ArrayList;

import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

//TODO: There's something screwy with the zoom in and zoom out in keeping the image centered, fix it
//TODO: Default zoomout range on creation so you can actually see the thing
public class ImagePage {

//---  Instance Variables   -------------------------------------------------------------------
	
	private ElementPanel p;
	private ArrayList<ImageDisplay> images;
	private int currentImageIndex;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ImagePage() {
		images = new ArrayList<ImageDisplay>();
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
				getCurrentImageDisplay().processMouseWheelInput(scroll);
				drawPage();
			}
			
			public void clickPressBehaviour(int code, int x, int y) {
				getCurrentImageDisplay().processPressInput(code, x, y);
				drawPage();
			}
			
			public void clickReleaseBehaviour(int code, int x, int y) {
				getCurrentImageDisplay().processReleaseInput(code, x, y);
				drawPage();
			}
			
			@Override
			public void dragBehaviour(int code, int x, int y) {
				getCurrentImageDisplay().processDragInput(code, x, y);
				drawPage();
			}
		};

		addFraming();
		p.setScrollBarHorizontal(false);
		p.setScrollBarVertical(false);
		return p;
	}

	public void generatePopout() {
		WindowFrame newF = new WindowFrame(800, 800);
		ElementPanel p2 = new ElementPanel(0, 0, 800, 800);
		new ImageDisplay(getCurrentImageDisplay().getImagePath(), p2);
		newF.addPanel("p2", p2);
	}

	public void replaceActiveImage(String newPath) {
		p.removeElement(getCurrentImageDisplay().getImagePath());
		getCurrentImageDisplay().setImagePath(newPath + (newPath.contains(".jpg") ? "" : ".jpg"));
	}
	
	public void refreshActiveImage() {
		getCurrentImageDisplay().refresh();
		drawPage();
	}
	
	public void allotImage(String path) {
		images.add(new ImageDisplay(path, p));
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
