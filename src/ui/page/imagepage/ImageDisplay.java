package ui.page.imagepage;

import java.awt.Image;

import visual.panel.ElementPanel;

public class ImageDisplay {

//---  Constant Values   ----------------------------------------------------------------------
	
	private final static double DEFAULT_ZOOM = 1.0;
	private static final double MOVEMENT_FACTOR = .1;
	private static final double ZOOM_FACTOR = 1.1;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private String imageName;
	private String imagePath;
	private Image reference;
	private double zoom;
	private ElementPanel p;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ImageDisplay(String path, ElementPanel in) {
		imagePath = path;
		p = in;
		zoom = 1;
		refresh();
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void refresh() {
		p.removeCachedImage(imagePath);
		reference = p.retrieveImage(imagePath);
		imageName = formatImageName(imagePath);
	}
	
	public void clear() {
		p.removeCachedImage(imagePath);
	}
	
	public void resetPosition() {
		p.setOffsetX(0);
		p.setOffsetY(0);
		zoom = DEFAULT_ZOOM;
		p.removeElement(ImagePage.IMAGE_NAME);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
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
	
	public void increaseZoom() {
		zoom *= ZOOM_FACTOR;
		p.removeElement(ImagePage.IMAGE_NAME);
	}
	
	public void decreaseZoom() {
		zoom /= ZOOM_FACTOR;
		p.removeElement(ImagePage.IMAGE_NAME);
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
