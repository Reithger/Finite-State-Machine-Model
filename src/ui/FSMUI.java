package ui;

import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class FSMUI {

	private final static int WINDOW_WIDTH = 800;
	private final static int WINDOW_HEIGHT = 800;
	
	/** Window needs to have tabs for options menus, multiple images*/
	private WindowFrame frame;
	
	private int image_panel_count;
	
	public FSMUI() {
		frame = new WindowFrame(WINDOW_WIDTH, WINDOW_HEIGHT);
		image_panel_count = 0;
	}
	
	/**
	 * Able to navigate the image: zoom in/out, move around, etc.
	 * 
	 * @param path
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	
	private ElementPanel generateImagePanel(String path, int x, int y, int width, int height) {
		ElementPanel p = new ElementPanel(x, y, width, height) {
			public void keyBehaviour(char code) {
				
			}
			
			public void clickBehaviour(int code, int x, int y) {
				
			}
		};
		String name = "img_panel_" + image_panel_count++;
		p.addImage(name, 10, x, y, true, path);
		
		   
		
		return p;
	}
	
	/**
	 * Program needs to be able to add states/transitions, select existing ones, intuitive interface from
	 * generic function to mass produce different iterations.
	 * 
	 * @param labels
	 * @param types
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	
	private ElementPanel generateOptionsPanel(String[] labels, String[] types, int x, int y, int width, int height) {
		
		return null;
	}
	
}
