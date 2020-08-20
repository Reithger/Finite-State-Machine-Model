package ui.page.optionpage;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import ui.FSMUI;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

/**
 * TODO: Probably wanna break this up with an interface or abstract class?
 * 
 * @author Borinor
 *
 */

public class EntrySet {

//---  Constant Values   ----------------------------------------------------------------------
	
	public final static String ENTRY_TEXT_SINGLE = "S";
	public final static String ENTRY_TEXT_DOUBLE = "D";
	public final static String ENTRY_TEXT_TRIPLE = "T";
	public final static String ENTRY_TEXT_QUARTET = "Q";
	public final static String ENTRY_TEXT_LONG = "L";
	public final static String ENTRY_CHECKBOX = "C";
	public final static String ENTRY_EMPTY = "E";
	public final static String ENTRY_SELECT_FSM = "F";
	public final static String ENTRY_SELECT_FSMS = "FS";
	private final static String DEFAULT_TEXT_ENTRY_CONTENTS = "";
	private final static String CHECKBOX_TRUE = "t";
	private final static String CHECKBOX_FALSE = "f";

	private final static int WIDTH_SELECT_FSMS_WINDOW = 500;
	private final static int HEIGHT_SELECT_FSMS_WINDOW = 300;
	private final static int WIDTH_SELECT_FSM_WINDOW = 300;
	private final static int HEIGHT_SELECT_FSM_WINDOW = 200;
	private final static int WIDTH_SELECT_FSMS_PANEL = 250;
	private final static int HEIGHT_SELECT_FSMS_PANEL = 300;
	
	private final static Color BACK_COLOR_GRAY = new Color(192, 192, 192);;
	private final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
	
	private final static int MAX_SELECT_FSMS = 8;
	
//---  Instance Variables   -------------------------------------------------------------------

	private String[] contents;
	private int subsystemCode;
	private String category;
	private String label;
	private String type;
	private int code;
	/** boolean value representing whether or not this Entry Set has a submission button */
	private boolean button;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public EntrySet(String lab, String ty, boolean butt, int co) {
		label = lab;
		type = ty;
		code = co;
		button = butt;
		contents = new String[getEntryContentSize(ty)];
		for(int i = 0; i < contents.length; i++) {
			contents[i] = type.contentEquals(ENTRY_CHECKBOX) ? CHECKBOX_FALSE : DEFAULT_TEXT_ENTRY_CONTENTS;
		}
		subsystemCode = OptionPage.SUBSYSTEM_CODE;
		OptionPage.SUBSYSTEM_CODE -= contents.length;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return - Returns an int value representing the change in y position after drawing this object
	 */
	
	public int drawEntrySet(int y) {
		int posX = 0;
		int posY = y;
		ElementPanel p = OptionPage.getElementPanel();
		OptionPage.handleText(getElementPrefix() + "label_text", p.getWidth() / 3 / 2, posY, p.getWidth() / 3, p.getHeight() / 20, OptionPage.OPTIONS_FONT, label);
		switch(type) {
			case ENTRY_SELECT_FSM:
				posX = p.getWidth() / 3 + p.getWidth() / 3 ;
				String choze = contents[0] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[0];
		  		OptionPage.handleRectangle(getElementPrefix() + "_rect_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, Color.white, Color.gray);
		  		OptionPage.handleText(getElementPrefix() + "_text_entry_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, OptionPage.OPTIONS_FONT, choze);
		  		OptionPage.handleButton(getElementPrefix() + "_butt_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, code);
				break;
			case ENTRY_SELECT_FSMS:
				posX = p.getWidth() / 3 + p.getWidth() / 3;
				for(int a = 0; a < MAX_SELECT_FSMS; a++) {
					String chozes = contents[a] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[a];
					if((chozes == null || chozes.equals(""))) {
						OptionPage.handleRectangle(getElementPrefix() + "_rect_fsm_entry_" + a, posX, posY, p.getWidth() / 3, p.getHeight() / 30, Color.white, Color.gray);
						OptionPage.handleButton(getElementPrefix() + "_butt_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, code);
						break;
					}
					posY += p.getHeight() / 18;
					OptionPage.handleRectangle(getElementPrefix() + "_rect_fsm_entry_" + a, posX, posY, p.getWidth() / 3, p.getHeight() / 30, Color.white, Color.gray);
					OptionPage.handleText(getElementPrefix() + "_text_entry_fsm_entry_" + a, posX, posY, p.getWidth() / 3, p.getHeight() / 30, OptionPage.OPTIONS_FONT, chozes);
					OptionPage.handleButton(getElementPrefix() + "_butt_fsm_entry_" + a, posX, posY, p.getWidth() / 3, p.getHeight() / 30, code);
				}
				break;
		  	case ENTRY_TEXT_LONG:
				posX = p.getWidth() / 3 + p.getWidth() / 3 ;
				String starr = contents[0] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[0];
				OptionPage.handleRectangle(getElementPrefix() + "_rect_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, Color.white, Color.gray);
				OptionPage.handleTextEntry(getElementNameTextEntry(0), posX, posY, p.getWidth() / 3, p.getHeight() / 30, subsystemCode, starr);
				break;
			case ENTRY_CHECKBOX:
				posX = p.getWidth() / 3 + p.getWidth() / 3 / 4;
				OptionPage.handleRectangle(getElementPrefix() + "_checkbox_rect", posX, posY, p.getHeight() / 30, p.getHeight() / 30, (contents[0].equals(CHECKBOX_FALSE) ? Color.white : Color.gray), Color.black);
				OptionPage.handleButton(getElementPrefix() + "_checkbox_button", posX, posY, p.getHeight() / 30, p.getHeight() / 30, code);
				break;
			default:
				int num = getEntryContentSize(type);
				for(int k = 0; k < num; k++) {
					//Draws text entries and iterates to next position
					String star = contents[k] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[k];
					posX = p.getWidth() / 3 + p.getWidth() / 3 / 4 * (k + 1);
					OptionPage.handleRectangle(getElementPrefix() + "_rect_" + k, posX, posY, p.getWidth() / 3 / (5), p.getHeight() / 30, Color.white, Color.gray);
					OptionPage.handleTextEntry(getElementNameTextEntry(k), posX, posY, p.getWidth() / 3 / (5), p.getHeight() / 30, subsystemCode - k, star);
				}
		}
		//Submission button
		posX = p.getWidth() - p.getWidth() / 3 / 4;
		if(button) {
			OptionPage.handleRectangle(getElementPrefix() + "_rect", posX, posY, p.getHeight() / 30, p.getHeight() / 30, Color.black, Color.gray);
			OptionPage.handleButton(getElementPrefix() + "_butt", posX, posY, p.getHeight() / 30, p.getHeight() / 30, code);
			OptionPage.handleLine(getElementPrefix() + "_line", p.getWidth() / 20, posY + p.getHeight() / 40, p.getWidth() * 19 / 20, posY + p.getHeight() / 40, 1, Color.black);
		}
		else {
			OptionPage.handleLine(getElementPrefix() + "_line", p.getWidth() / 20, posY + p.getHeight() / 40, p.getWidth() * 17 / 20, posY + p.getHeight() / 40, 1, Color.gray);
		}
		posY += p.getHeight() / 18;
		return posY;
	}
	
	public boolean appendItem(String ref) {
		for(int i = 0; i < contents.length; i++) {
			if(contents[i] == null || contents[i].equals("")) {
				contents[i] = ref;
				return true;
			}
		}
		return false;
	}
	
	public boolean removeItem(int ind) {
		if(ind < 0 || ind >= contents.length) {
			return false;
		}
		for(int i = ind; i + 1 < contents.length; i++) {
			contents[i] = contents[i + 1];
		}
		contents[contents.length - 1] = "";
		return true;
	}
	
	public void resetContents() {
		contents = new String[getEntryContentSize(type)];
		for(int i = 0; i < contents.length; i++) {
			OptionPage.getElementPanel().setElementStoredText(getElementNameTextEntry(i), "");
		}
	}
	
	public void processEvent() {
		switch(type) {
			case ENTRY_CHECKBOX:
				contents[0] = (contents[0].equals(CHECKBOX_TRUE) ? CHECKBOX_FALSE : CHECKBOX_TRUE);
				OptionPage.getElementPanel().removeElement(getElementPrefix() + "_checkbox_rect");
				break;
			case ENTRY_SELECT_FSM:
				SingleFSMFrame fra = new SingleFSMFrame();
				SingleFSMPanel eP = new SingleFSMPanel();
				fra.reservePanel("default", "pan", eP);
				break;
			case ENTRY_SELECT_FSMS:
				MultiFSMFrame fra2 = new MultiFSMFrame();
				MultiFSMSelectedPanel eP2 = new MultiFSMSelectedPanel();
				MultiFSMSelectablePanel eP3 = new MultiFSMSelectablePanel();
				fra2.reservePanel("default", "pan", eP2);
				fra2.reservePanel("default", "pan2", eP3);
				break;
			default:
				break;
		}
	}

//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCode(int in) {
		code = in;
	}

	public void setCategory(String in) {
		category = in;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	private String getElementPrefix() {
		return "category_" + category + "_" + label + "_" + type;
	}
	
	private String getElementNameTextEntry(int ind) {
		return getElementPrefix() + "_text_entry_" + ind;
	}

	private int getEntryContentSize(String in) {
		switch(in) {
			case ENTRY_TEXT_QUARTET:
				return 4;
			case ENTRY_TEXT_TRIPLE:
				return 3;
			case ENTRY_TEXT_DOUBLE:
				return 2;
			case ENTRY_TEXT_SINGLE:
				return 1;
			case ENTRY_CHECKBOX:
				return 1;
			case ENTRY_TEXT_LONG:
				return 1;
			case ENTRY_SELECT_FSM:
				return 1;
			case ENTRY_SELECT_FSMS:
				return MAX_SELECT_FSMS;
			case ENTRY_EMPTY:
				return 0;
			default:
				return 0;
		}
	}

	//-- Access Instance Variables  ---------------------------
	
	public int getCode() {
		return code;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getType() {
		return type;
	}
	
	public String[] getContents() {
		for(int i = 0; i < contents.length; i++) {
			try {
				String s = OptionPage.getElementPanel().getElementStoredText(getElementNameTextEntry(i));
				contents[i] = s == null ? contents[i] : s;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return contents;
	}

	public String getContentAtIndex(int index) {
		if(index < 0 || index >= contents.length) {
			return null;
		}
		return getContents()[index];
	}
	
//---  Support Classes   ----------------------------------------------------------------------
	
	//-- Frames  ----------------------------------------------
	
	public class SingleFSMFrame extends WindowFrame{
		
		
		public SingleFSMFrame() {
			super(WIDTH_SELECT_FSM_WINDOW, HEIGHT_SELECT_FSM_WINDOW);
			setName("Select FSM for Operation");
			setResizable(true);
			setExitOnClose(false);
		}
		
		@Override
		public void reactToResize() {
			this.getPanel("pan").resize(WIDTH_SELECT_FSM_WINDOW, this.getHeight());
		}
		
	}
	
	public class MultiFSMFrame extends WindowFrame{

		
		public MultiFSMFrame() {
			super(WIDTH_SELECT_FSMS_WINDOW, HEIGHT_SELECT_FSMS_WINDOW);
			setExitOnClose(false);
			setResizable(true);
			setName("Select FSMs For Operation");
		}
		
		@Override
		public void reactToResize() {
			this.getPanel("pan").resize(WIDTH_SELECT_FSMS_PANEL, this.getHeight());
			this.getPanel("pan2").resize(WIDTH_SELECT_FSMS_PANEL, this.getHeight());
			//TODO Make two contained ElementPanels redraw themselves
		}
		
	}
	
	//-- Panels  ----------------------------------------------
	
	public class SingleFSMPanel extends ElementPanel{


		private final Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);
		
		private ArrayList<String> fsms = OptionPage.getFSMUI().getFSMList();
		
		public SingleFSMPanel() {
			super(0, 0, WIDTH_SELECT_FSM_WINDOW, HEIGHT_SELECT_FSM_WINDOW);
			draw();
		}
		
		@Override
		public void clickBehaviour(int code, int x, int y) {
			if(code >= 0 && code < fsms.size()) {
				contents[0] = FSMUI.stripPath(fsms.get(code));
				OptionPage.getElementPanel().removeElement(getElementNameTextEntry(0));
				//TODO: Refresh OptionPage with drawPage?
				this.getParentFrame().disposeFrame();
			}
			draw();
		}
		
		public void draw() {
			int wid = WIDTH_SELECT_FSM_WINDOW * 2 / 3;
			int hei = HEIGHT_SELECT_FSM_WINDOW / 5;
			int heiChange = hei * 3 / 2;
			for(int i = 0; i < fsms.size(); i++) {
				String nom = FSMUI.stripPath(fsms.get(i));
				addRectangle("back_" + i, 5, false, getWidth() / 2,hei + heiChange * i, wid, hei, true, BACK_COLOR_GRAY, Color.black);
				addText("text_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, nom, DEFAULT_FONT, true, true, true);
				addButton("butt_" + i, 10, false, getWidth() / 2, hei + heiChange * i, wid, hei, i, true);
			}
		}
		
	}
	
	public class MultiFSMSelectedPanel extends ElementPanel{

		
		private final Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);
		private final static int CODE_CLOSE_SELECT_FSMS = -15;

		public MultiFSMSelectedPanel() {
			super(0, 0, WIDTH_SELECT_FSMS_PANEL, HEIGHT_SELECT_FSMS_PANEL);
			draw();
		}
		
		@Override
		public void clickBehaviour(int codeB, int x, int y) {
			draw();
			if(codeB >= 0 && codeB < MAX_SELECT_FSMS) {
				removeItem(codeB);
				draw();
				OptionPage.getElementPanel().removeElementPrefixed("");
			}
			else if(codeB == CODE_CLOSE_SELECT_FSMS) {
				OptionPage.getElementPanel().removeElementPrefixed("");
				getParentFrame().disposeFrame();
			}
		}
		
		public void draw() {
			removeElementPrefixed("");
			int bottomI = MAX_SELECT_FSMS;
			int wid = WIDTH_SELECT_FSMS_PANEL * 2 / 3;
			int hei = HEIGHT_SELECT_FSM_WINDOW / 5;
			int heiChange = hei * 3 / 2;
			for(int i = 0; i < MAX_SELECT_FSMS; i++) {	
				String nom = getContentAtIndex(i);
				if(nom == null || nom.equals("")) {
					bottomI = i;
					break;
				}
				addRectangle("select_fsm_" + i + "_rect", 5, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, true, BACK_COLOR_GRAY, Color.black);
				addText("select_fsm_" + i + "_text", 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, nom, DEFAULT_FONT, true, true, true);
				addButton("select_fsm_" + i + "_butt", 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, i, true);
			}
			addRectangle("select_fsm_" + bottomI + "_rect", 5, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * bottomI, wid, hei, true, BACK_COLOR_GRAY, Color.black);
			addText("select_fsm_" + bottomI + "_text", 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * bottomI, wid, hei, "Close Selection Screen", DEFAULT_FONT, true, true, true);
			addButton("select_fsm_" + bottomI + "_butt", 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * bottomI, wid, hei, CODE_CLOSE_SELECT_FSMS, true);
			bottomI++;
			addRectangle("select_fsm_" + bottomI + "_rect", 5, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * bottomI, wid, hei, true, COLOR_TRANSPARENT);
		}
	}
	
	public class MultiFSMSelectablePanel extends ElementPanel{

		private final Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 16);

		private ArrayList<String> fsms = OptionPage.getFSMUI().getFSMList();
		
		public MultiFSMSelectablePanel() {
			super(WIDTH_SELECT_FSMS_WINDOW - WIDTH_SELECT_FSMS_PANEL, 0, WIDTH_SELECT_FSMS_PANEL, HEIGHT_SELECT_FSMS_PANEL);
			draw();
		}
		
		@Override
		public void clickBehaviour(int codeB, int x, int y) {
			if(codeB >= 0 && codeB < fsms.size()) {
				appendItem(FSMUI.stripPath(fsms.get(codeB)));
				draw();
				//TODO optionPage drawPage
			}
		}
		
		private void draw() {
			removeElementPrefixed("");
			int wid = WIDTH_SELECT_FSM_WINDOW * 2 / 3;
			int hei = HEIGHT_SELECT_FSM_WINDOW / 5;
			int heiChange = hei * 3 / 2;
			for(int i = 0; i < fsms.size(); i++) {	
				String nom = FSMUI.stripPath(fsms.get(i));
				addRectangle("back_" + i, 5, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, true, BACK_COLOR_GRAY, Color.black);
				addText("text_" + i, 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, nom, DEFAULT_FONT, true, true, true);
				addButton("butt_" + i, 10, false, WIDTH_SELECT_FSMS_PANEL / 2, hei + heiChange * i, wid, hei, i, true);
			}
		}
		
	}
	
}
