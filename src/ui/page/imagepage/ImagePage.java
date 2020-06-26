package ui.page.imagepage;

import java.awt.Color;
import java.util.ArrayList;

import visual.panel.ElementPanel;

public class ImagePage {

//---  Instance Variables   -------------------------------------------------------------------
	
	private ElementPanel p;
	private ArrayList<String> images;
	private int currentImageIndex;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ImagePage() {
		images = new ArrayList<String>();
		currentImageIndex = 0;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	//-- Drawing  ---------------------------------------------
	
	public void drawPage() {
		if(images.size() == 0) {
			addFraming(p);
		}
		else {
			p.removeElementPrefixed("frame");
			for(int i = 0; i < images.size(); i++) {
				int posX = (i == currentImageIndex ? 0 : 5000);
				int posY = (i == currentImageIndex ? 0 : 5000);
				String imageName = "image_" + images.get(i).substring(images.get(i).lastIndexOf("/") + 1);
				if(!p.moveElement(imageName, posX, posY)) {
					p.addImage(imageName, 10, posX, posY, false, images.get(i));
				}
			}
		}
	}

	private void addFraming(ElementPanel p) {
		int width = p.getWidth();
		int height = p.getHeight();
		p.addLine("frame_line_1", 15, 0, 0, width, height, 5, Color.BLACK);
		p.addLine("frame_line_2", 15, width, 0, 0, height, 5, Color.BLACK);
		p.addLine("frame_line_3", 15, 0, 0, 0, height, 5, Color.BLACK);
		p.addLine("frame_line_4", 15, 0, 0, width, 0, 5, Color.BLACK);
		p.addLine("frame_line_5", 15, width, height, width, 0, 5, Color.BLACK);
		p.addLine("frame_line_6", 15, width, height, 0, height, 5, Color.BLACK);
	}
		
	public void allotImage(String path) {
		images.add(path);
	}

	public void removeImage(String path) {
		images.remove(path);
		//TODO: Adjust index to maintain position
	}

	public void setCurrentImageIndex(int in) {
		currentImageIndex = in;
	}
	
	public ElementPanel generateElementPanel(int x, int y, int width, int height) {
		p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				System.out.println(getFocusElement() + " " + code);
			}
			
			public void clickBehaviour(int code, int x, int y) {
				
			}
		};
		return p;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public ArrayList<String> getImages(){
		return images;
	}
	
	public int getCurrentImageHeader() {
		return currentImageIndex;
	}
	
}
