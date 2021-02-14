package ui.page.optionpage;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import controller.InputReceiver;
import ui.page.optionpage.entryset.EntrySetFactory;
import visual.composite.HandlePanel;

/**
 * 
 * DO NOT USE NEGATIVE CODE VALUES WE HAD TO MAKE A COMPROMISE AND THAT'S THE RESULT
 * 
 * @author Ada Clevinger
 *
 */

public abstract class OptionPage {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	protected final static Font OPTIONS_FONT = new Font("Serif", Font.BOLD, 12);
	
//---  Instance Variables   -------------------------------------------------------------------

	private String header;
	private String help;
	private ArrayList<Category> categories;	
	
	private static HandlePanel p;
	private boolean showHelp;		//TODO actually implement help pages
	private int helpKey;
	private boolean showSettings; //TODO: Settings menu
	private int settingsKey;
	
	private static InputReceiver inputRef;
	
	private static int lineHeightFraction;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public OptionPage(String head, String inHelp) {
		header = head;
		help = inHelp;
		categories = new ArrayList<Category>();
		lineHeightFraction = 15;
	}

	//---  Operations   ---------------------------------------------------------------------------
	
	public void drawPage() {
		if(showHelp) {
			drawHelpPage();
		}
		else {
			drawNormalPage();
		}
		addFraming();
	}
	
	public void drawNormalPage() {
		int wid = p.getWidth();
		int hei = p.getHeight();
		int startY = hei / 20;
		int codeStart = categories.size();
		helpKey = codeStart;
		settingsKey = ++codeStart;
		codeStart++;
		p.handleRectangle("help_rect", false, 5, wid - wid / 15, wid / 20, wid / 20, wid / 20, Color.gray, Color.black);
		p.handleButton("help_button", false, wid - wid / 15,  wid / 20, wid / 20, wid / 20, helpKey);
		p.handleImage("help_img", false, wid - wid / 15, wid / 20, "/assets/ui/question_mark.png", 3);
		for(int i = 0; i < categories.size(); i++) {
			Category cat = categories.get(i);
			startY = cat.draw(startY, hei / lineHeightFraction, p);
		}
	}
	
	public void drawHelpPage() {
		p.handleText("help", false, p.getWidth() / 2, p.getHeight() / 2, p.getWidth(), p.getHeight(), OPTIONS_FONT, help);
	}
	
	public void handleMouseInput(int code, int x, int y, int mouseType) {
		if(code == helpKey || showHelp == true) {
			showHelp = !showHelp;
			p.removeElementPrefixed("");
			drawPage();
			return;
		}
		if(!toggleCategory(code) && handleInput(code)) {
			inputRef.receiveCode(code, mouseType);
		}
	}
	
	public boolean handleInput(int code) {
		boolean out = false;
		if(getCategoryFromCode(code) != null)
			out = getCategoryFromCode(code).handleInput(code, p);
		drawPage();
		return out;
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

	public static void assignInputReceiver(InputReceiver iR) {
		inputRef = iR;
	}
	
	public static void assignHandlePanel(HandlePanel inP) {
		p = inP;
		p.setScrollBarHorizontal(false);
	}

	public boolean toggleCategory(int code) { 
		if(code >= 0 && code < categories.size()) {
			categories.get(code).toggleOpen();
			drawPage();
			return true;
		}
		return false;
	}

	//-- EntrySet  --------------------------------------------
	
	public void addCategory(String title) {
		Category out = new Category(title);
		out.setCode(categories.size());
		categories.add(out);
	}

	public void resetCodeEntries(int code) {
		for(Category c : categories) {
			if(c.contains(code)) {
				c.getEntrySet(code).reset(p);
			}
		}
		drawPage();
	}

	public void setEntrySetContent(int code, int index, String reference) {
		getCategoryFromCode(code).setEntrySetContent(code, index, reference);
		drawPage();
	}
	
	public void removeContentsFromCode(int code, int index) {
		getCategoryFromCode(code).resetEntrySetContent(code, index);
		drawPage();
	}
	
	public void deleteContentsFromCode(int code, int index) {
		getCategoryFromCode(code).deleteEntrySetContent(code, index);
		drawPage();
	}
	
	public void resetContents(int code) {
		p.removeElementPrefixed(getCategoryFromCode(code).resetEntrySetContents(code));
		drawPage();
	}
	
		//-- Add Types  ---------------------------------------

	public void addEntryText(String category, String label, boolean button, int code, int size, boolean flex) {
		if(getCategory(category) == null) {
			addCategory(category);
		}
		Category c = getCategory(category);
		c.addEntrySet(EntrySetFactory.generateEntryText(c.prefix(), label, button, code, size, flex));
	}
	
	public void addEntryTextDisplay(String category, String label, boolean button, int code) {
		if(getCategory(category) == null) {
			addCategory(category);
		}
		Category c = getCategory(category);
		c.addEntrySet(EntrySetFactory.generateEntryTextDisplay(c.prefix(), label, button, code));
	}
	
	public void addEntryList(String category, String label, boolean button, int code, int newCode) {
		if(getCategory(category) == null) {
			addCategory(category);
		}
		Category c = getCategory(category);
		c.addEntrySet(EntrySetFactory.generateEntryList(c.prefix(), label, button, code, newCode));
	}
	
	public void addEntryCheckbox(String category, String label, boolean button, int code) {
		if(getCategory(category) == null) {
			addCategory(category);
		}
		Category c = getCategory(category);
		c.addEntrySet(EntrySetFactory.generateEntryCheckbox(c.prefix(), label, button, code));
	}
	
	public void addEntryEmpty(String category, String label, boolean button, int code) {
		if(getCategory(category) == null) {
			addCategory(category);
		}
		Category c = getCategory(category);
		c.addEntrySet(EntrySetFactory.generateEntryEmpty(c.prefix(), label, button, code));
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	//-- Meta  ------------------------------------------------
	
	public String getHeader() {
		return header;
	}

	public static HandlePanel getHandlePanel() {
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

	//-- Access Contents  -------------------------------------
	
	public String getTextFromCode(int code, int posit){
		Category c = getCategoryFromCode(code);
		if(c != null)
			return c.getContent(code, posit);
		return null;
	}
			
	public Integer getIntegerFromCode(int code, int posit) {
		return Integer.parseInt(getTextFromCode(code, posit));
	}
	
	public Boolean getCheckboxContentsFromCode(int code) {
		return getTextFromCode(code, 0).contentEquals(EntrySetFactory.SIGNIFIER_TRUE);
	}
	
	public ArrayList<String> getContentFromCode(int code) {
		return getCategoryFromCode(code).getContents(code);
	}
		
}
