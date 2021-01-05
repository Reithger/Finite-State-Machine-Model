package ui.page.imagepage;

import java.awt.Color;
import java.util.ArrayList;

import input.CustomEventReceiver;
import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

//TODO: There's something screwy with the zoom in and zoom out in keeping the image centered, fix it
//TODO: Default zoomout range on creation so you can actually see the thing
public class ImagePage {

//---  Instance Variables   -------------------------------------------------------------------
	
	private ElementPanel p;
	private ArrayList<FSMImage> images;
	private ImageDisplay iD;
	private int currentImageIndex;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ImagePage(int x, int y, int width, int height) {
		images = new ArrayList<FSMImage>();
		generateElementPanel(x, y, width, height);
		iD = new ImageDisplay("./assets/default.png", p);
		currentImageIndex = 0;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void generateElementPanel(int x, int y, int width, int height) {
		p = new ElementPanel(x, y, width, height);
		p.setEventReceiver(new CustomEventReceiver() {
			@Override
			public void keyEvent(char code) {
				iD.processKeyInput(code);
				drawPage();
			}

			@Override
			public void clickEvent(int code, int x, int y, int mouseType) {
				iD.processClickInput(code);
				drawPage();
			}

			@Override
			public void mouseWheelEvent(int scroll) {
				iD.processMouseWheelInput(scroll);
				drawPage();
			}

			@Override
			public void clickPressEvent(int code, int x, int y, int mouseType) {
				iD.processPressInput(code, x, y);
				drawPage();
			}

			@Override
			public void clickReleaseEvent(int code, int x, int y, int mouseType) {
				iD.processReleaseInput(code, x, y);
				drawPage();
			}
			
			@Override
			public void dragEvent(int code, int x, int y, int mouseType) {
				iD.processDragInput(code, x, y);
				drawPage();
			}
		});

		addFraming();
		p.setScrollBarHorizontal(false);
		p.setScrollBarVertical(false);
	}

	public void generatePopout() {
		WindowFrame newF = new WindowFrame(800, 800);
		ElementPanel p2 = new ElementPanel(0, 0, 800, 800);
		new ImageDisplay(iD.getImage(), p2);
		newF.addPanel("p2", p2);
	}

	public void refreshActiveImage() {
		iD.refresh();
		drawPage();
	}
	
	public void allotImage(String ref, String path) {
		images.add(new FSMImage(ref, path));
	}
	
	public void removeImage(int ind){
		if(ind < 0 || ind >= images.size()) {
			return;
		}
		iD.clear();
		images.remove(ind);
	}
	
	//-- Drawing  ---------------------------------------------
	
	public void drawPage() {
		if(images.size() > 0) {
			iD.drawPage();
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
		iD.setImage(images.get(currentImageIndex).getPath());
		p.removeElement("img");
		refreshActiveImage();
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	public ArrayList<String> getImageNames(){
		ArrayList<String> out = new ArrayList<String>();
		for(FSMImage fsm : images) {
			out.add(fsm.getReferenceName());
		}
		return out;
	}
	
	public int getCurrentImageIndex() {
		return currentImageIndex;
	}
	
}
