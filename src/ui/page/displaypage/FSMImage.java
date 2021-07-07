package ui.page.displaypage;

import java.awt.Image;

import input.NestedEventReceiver;
import visual.composite.HandlePanel;
import visual.composite.ImageDisplay;

public class FSMImage {
	
	private static final String DEFAULT_IMAGE_PATH = "src/assets/default.png";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private String reference;
	
	private Image path;
	
	private double zoom;
	
	private int x;
	
	private int y;
	
	private static ImageDisplay iD;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FSMImage (String inRef, Image inPath) {
		reference = inRef;
		path = inPath;
		zoom = 1;
		x = 0;
		y = 0;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static void attachPanel(HandlePanel p) {
		if(iD == null)
			iD = new ImageDisplay(DEFAULT_IMAGE_PATH, p);
		NestedEventReceiver neR = new NestedEventReceiver(p.getEventReceiver());
		neR.addNested(iD.generateEventReceiver());
		p.setEventReceiver(neR);
		iD.autofitImage();
		iD.toggleUI();
		iD.toggleDisableToggleUI();
		iD.refresh();
	}
	
	public void activate() {
		iD.setOffsetX(getX());
		iD.setOffsetY(getY());
		iD.setZoom(getZoom());
		iD.setImage(getImage());
		iD.refreshImage();
	}
	
	public void deactivate() {
		setX(iD.getOffsetX());
		setY(iD.getOffsetY());
		setZoom(iD.getZoom());
	}
	
	public void drawPage() {
		iD.drawPage();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setZoom(double in) {
		zoom = in;
	}
	
	public void setX(int in) {
		x = in;
	}
	
	public void setY(int in) {
		y = in;
	}
	
	public void setReferenceName(String in) {
		reference = in;
	}
	
	public void setImage(Image in) {
		path = in;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public double getZoom() {
		return zoom;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public String getReferenceName() {
		return reference;
	}
	
	public Image getImage() {
		return path;
	}
	
}
