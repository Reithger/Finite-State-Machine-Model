package ui.page.imagepage;

public class FSMImage {
	
	private String reference;
	private String path;
	
	public FSMImage (String inRef, String inPath) {
		reference = inRef;
		path = inPath;
	}
	
	public void setReferenceName(String in) {
		reference = in;
	}
	
	public void setPath(String in) {
		path = in;
	}
	
	public String getReferenceName() {
		return reference;
	}
	
	public String getPath() {
		return path;
	}
	
}
