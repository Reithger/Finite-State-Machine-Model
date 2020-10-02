package ui.page.optionpage.entryset;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import input.Communication;
import ui.FSMUI;
import ui.page.optionpage.OptionPage;
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
	
	private final static Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 12);
	
	public final static String ENTRY_TEXT_SINGLE = "S";
	public final static String ENTRY_TEXT_DOUBLE = "D";
	public final static String ENTRY_TEXT_TRIPLE = "T";
	public final static String ENTRY_TEXT_QUARTET = "Q";
	public final static String ENTRY_TEXT_LONG = "L";
	public final static String ENTRY_CHECKBOX = "C";
	public final static String ENTRY_EMPTY = "E";
	public final static String ENTRY_SELECT_FSM = "F";
	public final static String ENTRY_SELECT_FSMS = "FS";
	public final static String TEXT_DISPLAY = "ET";
	
	private final static String DEFAULT_TEXT_ENTRY_CONTENTS = "";
	public final static String CHECKBOX_TRUE = "t";
	public final static String CHECKBOX_FALSE = "f";
	
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
		int wid = OptionPage.getElementPanel().getWidth();
		int hei = OptionPage.getElementPanel().getHeight();
		String pref = getElementPrefix();
		OptionPage.handleText(pref + "label_text", wid / 3 / 2, posY, wid / 3, hei / 20, DEFAULT_FONT, label);
		switch(type) {
			case TEXT_DISPLAY:
				posX = wid / 3;
				for(int i = 0; i < contents.length; i++) {
					String starr = contents[i];
					if(starr == null || starr.contentEquals("")) {
						break;
					}
					if(i != 0)
						posY += hei / 18;
					OptionPage.handleRectangle(pref + "_text_display_rect_" + i, posX, posY, wid / 3, hei / 30, Color.white, Color.gray);
					OptionPage.handleText(pref + "_text_display_" + i, posX, posY, wid / 3, hei / 30, DEFAULT_FONT, starr);
					OptionPage.handleButton(pref + "_text_display_button_" + i, posX, posY, wid / 3, hei / 30, code + i);
				}
				break;
			case ENTRY_SELECT_FSM:
				posX = wid / 3 + wid / 3 ;
				String choze = contents[0] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[0];
		  		OptionPage.handleRectangle(pref + "_rect_fsm_entry", posX, posY, wid / 3, hei / 30, Color.white, Color.gray);
		  		OptionPage.handleText(pref + "_text_entry_fsm_entry", posX, posY, wid / 3, hei / 30, DEFAULT_FONT, choze);
		  		OptionPage.handleButton(pref + "_butt_fsm_entry", posX, posY, wid / 3, hei / 30, code);
				break;
			case ENTRY_SELECT_FSMS:
				posX = wid / 3 + wid / 3;
				for(int a = 0; a < MultiFSMSelection.MAX_SELECT_FSMS; a++) {
					String chozes = contents[a] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[a];
					if((chozes == null || chozes.equals(""))) {
						OptionPage.handleRectangle(pref + "_rect_fsm_entry_" + a, posX, posY, wid / 3, hei / 30, Color.white, Color.gray);
						OptionPage.handleButton(pref + "_butt_fsm_entry", posX, posY, wid / 3, hei / 30, code);
						break;
					}
					posY += hei / 18;
					OptionPage.handleRectangle(pref + "_rect_fsm_entry_" + a, posX, posY, wid / 3, hei / 30, Color.white, Color.gray);
					OptionPage.handleText(pref + "_text_entry_fsm_entry_" + a, posX, posY, wid / 3, hei / 30, DEFAULT_FONT, chozes);
					OptionPage.handleButton(pref + "_butt_fsm_entry_" + a, posX, posY, wid / 3, hei / 30, code);
				}
				break;
		  	case ENTRY_TEXT_LONG:
				posX = wid / 3 + wid / 3 ;
				String starr = contents[0] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[0];
				OptionPage.handleRectangle(pref + "_rect_entry", posX, posY, wid / 3, hei / 30, Color.white, Color.gray);
				OptionPage.handleTextEntry(getElementNameTextEntry(0), posX, posY, wid / 3, hei / 30, subsystemCode, starr);
				break;
			case ENTRY_CHECKBOX:
				posX = wid / 3 + wid / 3 / 4;
				OptionPage.handleRectangle(pref + "_checkbox_rect", posX, posY, hei / 30, hei / 30, (contents[0].equals(CHECKBOX_FALSE) ? Color.white : Color.gray), Color.black);
				OptionPage.handleButton(pref + "_checkbox_button", posX, posY, hei / 30, hei / 30, code);
				break;
			default:
				int num = getEntryContentSize(type);
				for(int k = 0; k < num; k++) {
					//Draws text entries and iterates to next position
					String star = contents[k] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[k];
					posX = wid / 3 + wid / 3 / 4 * (k + 1);
					OptionPage.handleRectangle(pref + "_rect_" + k, posX, posY, wid / 3 / (5), hei / 30, Color.white, Color.gray);
					OptionPage.handleTextEntry(getElementNameTextEntry(k), posX, posY, wid / 3 / (5), hei / 30, subsystemCode - k, star);
				}
		}
		//Submission button
		posX = wid - wid / 3 / 4;
		if(button) {
			OptionPage.handleRectangle(pref + "_" + code + "_rect", posX, posY, hei / 30, hei / 30, Color.black, Color.gray);
			OptionPage.handleButton(pref + "_" + code + "_butt", posX, posY, hei / 30, hei / 30, code);
			OptionPage.handleLine(pref + "_" + code + "_line", wid / 20, posY + hei / 40, wid * 19 / 20, posY + hei / 40, 1, Color.black);
		}
		else {
			OptionPage.handleLine(pref + "_" + code + "_line", wid / 20, posY + hei / 40, wid * 17 / 20, posY + hei / 40, 1, Color.gray);
		}
		posY += hei / 18;
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
				new SingleFSMSelection(this);
				break;
			case ENTRY_SELECT_FSMS:
				new MultiFSMSelection(this);
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
	
	public String getElementPrefix() {
		return "category_" + category + "_" + label + "_" + type;
	}
	
	public String getElementNameTextEntry(int ind) {
		return getElementPrefix() + "_text_entry_" + ind;
	}

	private int getEntryContentSize(String in) {
		switch(in) {
			case TEXT_DISPLAY:
				return 64;
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
				return MultiFSMSelection.MAX_SELECT_FSMS;
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

	//TODO: Display small .jpg of the fsm?
	
	//-- Frames  ----------------------------------------------

}
