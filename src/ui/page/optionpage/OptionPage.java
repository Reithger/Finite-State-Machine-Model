package ui.page.optionpage;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import ui.FSMUI;
import ui.page.optionpage.entryset.EntrySet;
import visual.panel.ElementPanel;

/**
 * 
 * DO NOT USE NEGATIVE CODE VALUES WE HAD TO MAKE A COMPROMISE AND THAT'S THE RESULT
 * 
 * @author Reithger
 *
 */

public abstract class OptionPage {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	protected final static Font OPTIONS_FONT = new Font("Serif", Font.BOLD, 12);
	
	public final static String ENTRY_TEXT_SINGLE = "S";	//TODO: Probably distinct functions to add each of these, like ElementPanel
	public final static String ENTRY_TEXT_DOUBLE = "D";	//TODO: Need EntrySet factory and subclasses of an abstract class, need that structure
	public final static String ENTRY_TEXT_TRIPLE = "T";
	public final static String ENTRY_TEXT_QUARTET = "Q";
	public final static String ENTRY_TEXT_LONG = "L";
	public final static String ENTRY_CHECKBOX = "C";
	public final static String TEXT_DISPLAY = "ET";
	public final static String ENTRY_EMPTY = "E";
	
	public final static String ENTRY_BUTTON_LIST = "CSB";
		//TODO: Should be generic button for high-level interpretation, allows custom work
		//TODO: Allow register 'x' number of vertical spaces to display associated text
	
	
//---  Instance Variables   -------------------------------------------------------------------

	private String header;
	private String help;
	private ArrayList<Category> categories;	
	
	/** Administrative code value used by subsystems (codes to refer to textEntry objects mostly) so they have unique identifiers*/
	public static int SUBSYSTEM_CODE = -50;
	
	private static ElementPanel p;
	private static FSMUI reference;
	private boolean showHelp;		//TODO actually implement help pages
	private int helpKey;
	private boolean showSettings; //TODO: Settings menu
	private int settingsKey;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public OptionPage(String head, String inHelp, String[] categoriesIn, Object[][][] data) {
		header = head;
		help = inHelp;
		categories = new ArrayList<Category>();
		for(int i = 0; i < categoriesIn.length; i++) {
			String cat = categoriesIn[i];
			addCategory(cat);
			for(int j = 0; j < data[i].length; j++) {
				String lab = (String)(data[i][j][0]);
				String type = (String)(data[i][j][1]);	//TODO: ugh
				int code = (Integer)(data[i][j][2]);
				boolean butt = (Boolean)(data[i][j][3]);
				addEntrySet(cat, lab, type, code, butt);
			}
		}
	}

	//---  Operations   ---------------------------------------------------------------------------
	
	public void drawPage() {
		if(showHelp) {
			drawHelpPage();
		}
		else {
			drawNormalPage();
		}
	}
	
	public void drawNormalPage() {
		int wid = p.getWidth();
		int hei = p.getHeight();
		int startY = hei / 20;
		int codeStart = categories.size();
		helpKey = codeStart;
		settingsKey = ++codeStart;
		codeStart++;
		handleRectangle("help_rect", wid - wid / 15, wid / 20, wid / 20, wid / 20, Color.gray, Color.black);
		handleButton("help_button", wid - wid / 15,  wid / 20, wid / 20, wid / 20, helpKey);
		handleImage("help_img", wid - wid / 15, wid / 20, "/assets/ui/question_mark.png", 3);
		for(int i = 0; i < categories.size(); i++) {
			Category cat = categories.get(i);
			startY = cat.drawCategoryHeader(startY);
			if(cat.isOpen()) {
				for(EntrySet e : cat.getEntrySets()) {
					startY = e.drawEntrySet(startY, wid, hei);
				}
			}
			else {
				cat.hideContents();
			}
		}
	}
	
	public void drawHelpPage() {
		handleText("help", p.getWidth() / 2, p.getHeight() / 2, p.getWidth(), p.getHeight(), OPTIONS_FONT, help);
	}
	
	public void handleMouseInput(int code, int x, int y) {
		if(code == helpKey || showHelp == true) {
			showHelp = !showHelp;
			p.removeElementPrefixed("");
			drawPage();
			return;
		}
		EntrySet e = getEntrySetFromCode(code);
		if(e != null) {
			e.processEvent();
		}
		if(!toggleCategory(code)) {
			applyCode(code);
		}
		drawPage();
	}

	public abstract void applyCode(int code);

//---  Setter Methods   -----------------------------------------------------------------------

	public static void assignElementPanel(ElementPanel inP) {
		p = inP;
		p.setScrollBarHorizontal(false);
	}
	
	public static void assignFSMUI(FSMUI fsm) {
		reference = fsm;
	}

	public void addCategory(String title) {
		Category out = new Category(title);
		out.setCode(categories.size());
		categories.add(out);
	}

	public void addEntrySet(String category, String label, String type, int code, boolean activeButton) {
		EntrySet set = new EntrySet(label, type, activeButton, code);
		getCategory(category).addEntrySet(set);
	}
	
	public void resetCodeEntries(int code) {
		for(Category c : categories) {
			if(c.contains(code)) {
				c.getEntrySet(code).resetContents();
			}
		}
		p.repaint();
	}

	public boolean toggleCategory(int code) { 
		if(code >= 0 && code < categories.size()) {
			categories.get(code).toggleOpen();
			return true;
		}
		return false;
	}

	public void appendContentToCode(int code, String reference) {
		getEntrySetFromCode(code).appendItem(reference);
	}
	
	public void removeContentsFromCode(int code, int index) {
		getEntrySetFromCode(code).removeItem(index);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	//-- Meta  ------------------------------------------------
	
	public String getHeader() {
		return header;
	}

	public static FSMUI getFSMUI() {
		return reference;
	}
	
	public static ElementPanel getElementPanel() {
		return p;
	}
	
	public Category getCategory(String title) {
		for(Category c : categories) {
			if(c.getTitle().equals(title)) {
				return c;
			}
		}
		return null;
	}
	
	public Category getCategoryFromCode(int code) {
		for(Category c : categories) {
			if(c.contains(code)) {
				return c;
			}
		}
		return null;
	}
	
	public EntrySet getEntrySetFromCode(int code) {
		Category c = getCategoryFromCode(code);
		if(c != null)
			return c.getEntrySet(code);
		return null;
	}
	
	//-- Access Contents  -------------------------------------
	
	public String getTextFromCode(int code, int posit){
		Category c = getCategoryFromCode(code);
		if(c != null)
			return c.getContents(code, posit);
		return null;
	}
			
	public Integer getIntegerFromCode(int code, int posit) {
		return Integer.parseInt(getTextFromCode(code, posit));
	}
	
	public Boolean getCheckboxContentsFromCode(int code) {
		return getTextFromCode(code, 0).contentEquals(EntrySet.CHECKBOX_TRUE);
	}
	
	public String getTypeFromCode(int code) {
		return getEntrySetFromCode(code).getType();
	}

	public String[] getContentFromCode(int code) {
		return getEntrySetFromCode(code).getContents();
	}
		
//---  Composites   ---------------------------------------------------------------------------

	public static void handleText(String nom, int x, int y, int wid, int hei, Font inF, String phr) {
		if(!p.moveElement(nom, x, y)){
			p.addText(nom, 15, false, x, y, wid, hei, phr, inF, true, true, true);
		}
	}
	
	public static void handleImage(String nom, int x, int y, String path, double scale) {
		if(!p.moveElement(nom, x, y)){
			p.addImage(nom, 15, false,  x, y, true, path, scale);
		}
	}

	public static void handleTextEntry(String nom, int x, int y, int wid, int hei, int cod, String phr) {
		if(!p.moveElement(nom, x, y)){
			p.addTextEntry(nom, 15, false, x, y, wid, hei, cod, phr, OPTIONS_FONT, true, true, true);
		}
	}
	
	public static void handleButton(String nom, int x, int y, int wid, int hei, int code) {
		if(!p.moveElement(nom, x, y)) {
			p.addButton(nom, 10, false, x, y, wid, hei, code, true);
		}
	}
	
	public static void handleLine(String nom, int x, int y, int x2, int y2, int thck, Color col) {
		if(!p.moveElement(nom, x, y)) {
			p.addLine(nom, 5, false, x, y, x2, y2, thck, col);
		}
	}
	
	public static void handleRectangle(String nom, int x, int y, int wid, int hei, Color inside, Color border) {
		if(!p.moveElement(nom, x, y)) {
			p.addRectangle(nom, 5, false, x, y, wid, hei, true, inside, border);
		}
	}
		
}
