package ui.page.imagepage;

import java.awt.Color;
import java.util.ArrayList;

import input.CustomEventReceiver;
import visual.composite.HandlePanel;
import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

//TODO: There's something screwy with the zoom in and zoom out in keeping the image centered, fix it
//TODO: Default zoomout range on creation so you can actually see the thing

public class ImagePage {

//---  Instance Variables   -------------------------------------------------------------------
	
	private HandlePanel p;
	private ArrayList<FSMImage> images;
	private ImageDisplay iD;
	private int index;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ImagePage(int x, int y, int width, int height) {
		images = new ArrayList<FSMImage>();
		generateElementPanel(x, y, width, height);
		iD = new ImageDisplay("./assets/default.png", p);
		iD.autofitImage();
		iD.toggleUI();
		iD.toggleDisableToggleUI();
		iD.refresh();
		index = 0;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void updateSize(int x, int y, int wid, int hei) {
		p.setLocation(x, y);
		p.resize(wid, hei);
		iD.refresh();
	}
	
	public void generateElementPanel(int x, int y, int width, int height) {
		p = new HandlePanel(x, y, width, height);
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
	
	public void allotFSM(String ref, String path) {
		logCurrentFSMImageProperties();
		FSMImage nIm = new FSMImage(ref, p.retrieveImage(path));
		images.add(nIm);
		iD.setImage(nIm.getImage());
		iD.autofitImage();
		nIm.setZoom(iD.getZoom());
		iD.refresh();
		index = images.size() - 1;
	}
	
	public void refreshActiveImage() {
		if(images.size() == 0) {
			return;
		}
		while(images.size() <= getCurrentImageIndex()) {
			index--;
		}
		iD.setImage(getCurrentFSMImage().getImage());
		iD.refresh();
	}

	private void logCurrentFSMImageProperties() {
		if(images.size() != 0) {
			getCurrentFSMImage().setZoom(iD.getZoom());
			getCurrentFSMImage().setX(iD.getOffsetX());
			getCurrentFSMImage().setY(iD.getOffsetY());
		}
	}
	
	private void updateDisplayWithFSMImageProperties() {
		iD.setZoom(getCurrentFSMImage().getZoom());
		iD.setOffsetX(getCurrentFSMImage().getX());
		iD.setOffsetY(getCurrentFSMImage().getY());
	}
	
	public void updateFSM(String ref, String path) {
		for(FSMImage fI : images) {
			if(fI.getReferenceName().equals(ref)) {
				p.removeCachedImage(path);
				fI.setImage(p.retrieveImage(path));
				refreshActiveImage();
				break;
			}
		}
	}
	
	public void removeFSM(String ref) {
		for(int i = 0; i < images.size(); i++) {
			if(images.get(i).getReferenceName().equals(ref)) {
				removeFSM(i);
				index -= index >= i ? 1 : 0;
				index = index < 0 ? 0 : index;
				setCurrentImageIndex(index);
				break;
			}
		}
	}
	
	public void removeFSM(int ind){
		if(ind < 0 || ind >= images.size()) {
			return;
		}
		if(ind <= index && ind - 1 >= 0) {
			index--;
			setCurrentImageIndex(index);
		}
		images.remove(ind);
		refreshActiveImage();
	}
	
	//-- Drawing  ---------------------------------------------
	
	public void drawPage() {
		iD.drawPage();
		addFraming();
	}

	private void addFraming() {
		int width = p.getWidth();
		int height = p.getHeight();
		int thick = 3;
		int buf = thick / 2;
		p.addLine("frame_line_3", 15, true, buf, buf, buf, height - buf, thick, Color.BLACK);
		p.addLine("frame_line_4", 15, true,  buf, buf, width - buf, buf, thick, Color.BLACK);
		p.addLine("frame_line_5", 15, true,  width - buf, height - buf, width - buf, buf, thick, Color.BLACK);
		p.addLine("frame_line_6", 15, true,  width - buf, height - buf, buf, height - buf, thick, Color.BLACK);
	}

//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCurrentImageIndex(int in) {
		if(index != in) {
			logCurrentFSMImageProperties();
			index = in;
			updateDisplayWithFSMImageProperties();
			refreshActiveImage();
		}
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getCurrentFSM() {
		if(images.size() == 0) {
			return null;
		}
		return images.get(getCurrentImageIndex()).getReferenceName();
	}
	
	public HandlePanel getPanel() {
		return p;
	}
	
	private FSMImage getCurrentFSMImage() {
		return images.get(getCurrentImageIndex());
	}
	
	public ArrayList<String> getImageNames(){
		ArrayList<String> out = new ArrayList<String>();
		for(FSMImage fsm : images) {
			out.add(fsm.getReferenceName());
		}
		return out;
	}
	
	public int getCurrentImageIndex() {
		return index;
	}
	
}
