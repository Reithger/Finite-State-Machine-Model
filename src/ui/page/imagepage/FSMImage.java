package ui.page.imagepage;

import java.awt.Image;

public class FSMImage {
	
	private String reference;
	private Image path;
	private double zoom;
	private int x;
	private int y;
	
	public FSMImage (String inRef, Image inPath) {
		reference = inRef;
		path = inPath;
		zoom = 1;
		x = 0;
		y = 0;
	}
	
	public double getZoom() {
		return zoom;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
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
	
	public String getReferenceName() {
		return reference;
	}
	
	public Image getImage() {
		return path;
	}
	
}
