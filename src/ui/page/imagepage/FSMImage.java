package ui.page.imagepage;

import java.awt.Image;

public class FSMImage {
	
	private String reference;
	private Image path;
	
	public FSMImage (String inRef, Image inPath) {
		reference = inRef;
		path = inPath;
	}
	
	public void setReferenceName(String in) {
		reference = in;
	}
	
	public void setPath(Image in) {
		path = in;
	}
	
	public String getReferenceName() {
		return reference;
	}
	
	public Image getPath() {
		return path;
	}
	
}
