package ui.page.displaypage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import controller.CodeReference;
import controller.InputReceiver;
import input.CustomEventReceiver;
import visual.composite.HandlePanel;

/**
 * TODO: Search Entries
 * TODO: Wire up Scrollwheel properly - backend is having issues with the click regions; multiple threads may be causing race conditions
 * (adding an element to an arraylist shouldn't be done at the wrong index)
 * 
 * @author Borinor
 *
 */

public class DisplayPage {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static Font TITLE_FONT = new Font("Serif", Font.BOLD, 22);
	private final static Font TEXT_FONT = new Font("Serif", Font.BOLD, 16);
	private final static Font ATTRIBUTE_FONT = new Font("Serif", Font.BOLD, 14);
	private final static int STAND_IN_CODE = -1;
	private final static int DEFAULT_ENTRY_SIZE = 25;
	
	private final static String TITLE_STATES = "States";
	private final static String TITLE_EVENTS = "Events";
	private final static String TITLE_TRANS = "Transitions";
	
	private final static double VERT_PROP = 7 / (double)12;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private FSMInfo info;
	
	private FSMImage imageDisplay;
	
	private boolean displayImageMode;
	
	private InputReceiver reference;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public DisplayPage(InputReceiver give) {
		reference = give;
	}
	
	public CustomEventReceiver getEventHandling(HandlePanel p) {
		CustomEventReceiver out = new CustomEventReceiver() {
			
			@Override
			public void clickEvent(int event, int x, int y, int mouseType) {

			}
			
			@Override
			public void mouseWheelEvent(int rot) {
				
			}
			
		};
		return out;
	}

//---  Operations   ---------------------------------------------------------------------------

	public void draw(HandlePanel p) {
		if(info == null) {
			drawDefault(p);
		}
		else if(displayImageMode) {
			drawImage(p);
		}
		else {
			drawInfo(p);
		}
		addFraming(p);
	}
	
	public void updateFSMInfo(FSMInfo in) {
		info = in;
	}
	
	public void drawInfo(HandlePanel p) {
		int vertP = (int)(p.getHeight() * VERT_PROP);
		
		drawDisplayThing(0, 0, p.getWidth() / 2, vertP, p, TITLE_STATES, info.getStates(), info.getStateAttributes(), info.getStateDetails());
		
		drawDisplayThing(p.getWidth() / 2, 0, p.getWidth() / 2, vertP, p, TITLE_EVENTS, info.getEvents(), info.getEventAttributes(), info.getEventDetails());
		
		drawDisplayThing(0, vertP, p.getWidth(), p.getHeight() - vertP, p, TITLE_TRANS, info.getTransitions(), info.getTransitionAttributes(), info.getTransitionDetails());
		
		//TODO: Set up buttons for adding/removing/renaming events/states/transitions and toggling qualities thereof
	}
	
	private void drawDisplayThing(int x, int y, int wid, int hei, HandlePanel p, String title, ArrayList<String> attributes, ArrayList<String> entries, HashMap<String, ArrayList<Boolean>> factors) {
		int sideBuffer = getSideBuffer(p);
		int vertBuffer = getVertBuffer(p);
		
		int currY = y + vertBuffer;
		
		p.handleText(title + "_list_title_", "move", 50, x + wid / 8, currY, wid / 3, hei / 4, TITLE_FONT, title);
		
		currY += vertBuffer;
		
		p.handleThickRectangle(title + "_list_rect_", "move", 35, x + sideBuffer, currY, x + wid - sideBuffer, y + hei - vertBuffer, Color.BLACK, 3);

		int boxHei = hei - 3 * vertBuffer;
		
		p.handleLine(title + "_list_line_ver_", "move", 35, x + wid / 2, currY, x + wid / 2, currY + boxHei, 2, Color.black);
		
		int entryHei = vertBuffer;
		
		int runX = x + wid / 2;
		int blockWid = sideBuffer;
		int attrBlockHeight = vertBuffer * 3 / 4;
		
		for(int i = 0; i < attributes.size(); i++) {
			int desX = runX + blockWid / 2 + blockWid * i;
			if(desX + blockWid < runX || desX > x + wid - sideBuffer) {
				continue;
			}
			p.handleLine(title + "_list_attr_line_ver_" + "_" + i, "move", 5, desX + blockWid / 2, currY, desX + blockWid / 2, currY + boxHei, 1, Color.black);
			p.handleText(title + "_list_attr_text_" + "_" + i, "move", 15, desX, currY + attrBlockHeight / 2, blockWid, attrBlockHeight, ATTRIBUTE_FONT, attributes.get(i));
		}
		
		Color back = p.getPanel().getBackground();
		
		p.handleRectangle(title + "_rect_cover_" + "_" + 1, "move", 25, x + wid / 2, currY + boxHei + entryHei / 2, wid, entryHei, back, back);
		
		p.handleRectangle(title + "_rect_cover_" + "_" + 2, "move", 25, x + wid / 2, currY + attrBlockHeight / 2, wid - sideBuffer * 2, attrBlockHeight, back, back);
		
		p.handleRectangle(title + "_rect_cover_" + "_" + 3, "move", 25, x + wid / 2, y + vertBuffer, wid, 2 * vertBuffer, back, back);
		
		currY += attrBlockHeight;

		p.handleLine(title + "_list_attr_line_bott_", "move", 30, x + sideBuffer, currY, x + wid - sideBuffer, currY, 2, Color.black);
		
		for(int i = 0; i < entries.size(); i++) {
			int desY = currY + entryHei * i;
			
			p.handleThickRectangle(title + "_list_entry_" + i + "_rect", title, 15, x + sideBuffer, desY, x + wid - sideBuffer, desY + entryHei, Color.BLACK, 1);
			p.handleText(title + "_list_entry_" + i + "_text", title, 15, x + sideBuffer * 2, desY + entryHei / 2, wid / 4, entryHei, TEXT_FONT, entries.get(i));

			ArrayList<Boolean> att = factors.get(entries.get(i));
			
			for(int j = 0; j < att.size(); j++) {
				int desX = runX + (j * blockWid);
				if(desX + blockWid < runX || desX > x + wid - sideBuffer) {
					continue;
				}
				desX += blockWid / 2;
				p.handleRectangle(title + "_list_entry_" + i + "_attribute_signifier_rect_" + "_" + j, title, 10, desX, desY + entryHei / 2, entryHei / 2, entryHei / 2, att.get(j) ? Color.green : Color.red, Color.black);
				p.handleButton(title + "_list_entry_" + i + "_attribute_signifier_button_" + "_" + j, title, 10, desX, desY + entryHei / 2, entryHei / 2, entryHei / 2, STAND_IN_CODE);
				//TODO : Replace STAND_IN_CODE with actual value to communicate with model
			}
		}
		p.addScrollbar(title + "_scrollbar_vert", 45, "no_move", x + wid - sideBuffer, y + 2 * vertBuffer, sideBuffer / 3, boxHei, currY, boxHei - attrBlockHeight, title, true);
		p.setGroupDrawOutsideWindow(title, false);
	}
	
	public void drawImage(HandlePanel p) {
		if(imageDisplay == null) {
			reference.receiveCode(CodeReference.CODE_GENERATE_IMAGE, 0);
		}
		imageDisplay.drawPage();
	}

	public void drawDefault(HandlePanel p) {
		int vertP = (int)(p.getHeight() * VERT_PROP);
		p.handleLine("vertline", "move", 35, p.getWidth() / 2, 0, p.getWidth() / 2, vertP, 3, Color.black);
		
		p.handleLine("horizline", "move", 35, 0, vertP, p.getWidth(), vertP, 3, Color.black);
		
		ArrayList<String> attr = new ArrayList<String>();
		for(int i = 0; i < 3; i++) {
			attr.add("Attr" + i);
		}
		HashMap<String, ArrayList<Boolean>> fact = new HashMap<String, ArrayList<Boolean>>();
		
		ArrayList<String> stat = new ArrayList<String>();
		Random rand = new Random();
		for(int i = 0; i < DEFAULT_ENTRY_SIZE; i++) {
			String nom = "Entry " + i;
			stat.add(nom);
			ArrayList<Boolean> bol = new ArrayList<Boolean>();
			for(int j = 0; j < 3; j++) {
				bol.add(rand.nextInt(2) == 0 ? true : false);
			}
			fact.put(nom, bol);
		}

		drawDisplayThing(0, 0, p.getWidth() / 2, vertP, p, TITLE_STATES, attr, stat, fact);
		
		drawDisplayThing(p.getWidth() / 2, 0, p.getWidth() / 2, vertP, p, TITLE_EVENTS, attr, stat, fact);
		
		drawDisplayThing(0, vertP, p.getWidth(), p.getHeight() - vertP, p, TITLE_TRANS, attr, stat, fact);
		
		
		addFraming(p);
	}
	
	private void addFraming(HandlePanel p) {
		int width = p.getWidth();
		int height = p.getHeight();
		int thick = 3;
		int buf = thick / 2;
		p.addLine("frame_line_3", 25, "no_move", buf, buf, buf, height - buf, thick, Color.BLACK);
		p.addLine("frame_line_4", 25, "no_move",  buf, buf, width - buf, buf, thick, Color.BLACK);
		p.addLine("frame_line_5", 25, "no_move",  width - buf, height - buf, width - buf, buf, thick, Color.BLACK);
		p.addLine("frame_line_6", 25, "no_move",  width - buf, height - buf, buf, height - buf, thick, Color.BLACK);
	}
	
	public void toggleDisplayMode() {
		displayImageMode = !displayImageMode;
	}
	
	public void updateImage(Image in) {
		if(imageDisplay == null) {
			imageDisplay = new FSMImage(info.getName(), in);
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	private int getVertBuffer(HandlePanel p) {
		return p.getHeight() / 25;
	}
	
	private int getSideBuffer(HandlePanel p) {
		return p.getWidth() / 16;
	}
	
	public boolean hasDisplay() {
		return imageDisplay == null;
	}
	
}
