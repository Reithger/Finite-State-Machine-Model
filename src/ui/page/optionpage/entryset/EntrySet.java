package ui.page.optionpage.entryset;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

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
	public final static String ENTRY_AGENTS = "A";
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
		ElementPanel p = OptionPage.getElementPanel();
		OptionPage.handleText(getElementPrefix() + "label_text", p.getWidth() / 3 / 2, posY, p.getWidth() / 3, p.getHeight() / 20, DEFAULT_FONT, label);
		switch(type) {
			case ENTRY_AGENTS:
				
				break;
			case TEXT_DISPLAY:
				posX = p.getWidth() / 3;
				for(int i = 0; i < contents.length; i++) {
					String starr = contents[i];
					if(starr == null || starr.contentEquals("")) {
						break;
					}
					if(i != 0)
						posY += p.getHeight() / 18;
					OptionPage.handleRectangle(getElementPrefix() + "_text_display_rect_" + i, posX, posY, p.getWidth() / 3, p.getHeight() / 30, Color.white, Color.gray);
					OptionPage.handleText(getElementPrefix() + "_text_display_" + i, posX, posY, p.getWidth() / 3, p.getHeight() / 30, DEFAULT_FONT, starr);
					OptionPage.handleButton(getElementPrefix() + "_text_display_button_" + i, posX, posY, p.getWidth() / 3, p.getHeight() / 30, code + i);
				}
				break;
			case ENTRY_SELECT_FSM:
				posX = p.getWidth() / 3 + p.getWidth() / 3 ;
				String choze = contents[0] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[0];
		  		OptionPage.handleRectangle(getElementPrefix() + "_rect_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, Color.white, Color.gray);
		  		OptionPage.handleText(getElementPrefix() + "_text_entry_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, DEFAULT_FONT, choze);
		  		OptionPage.handleButton(getElementPrefix() + "_butt_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, code);
				break;
			case ENTRY_SELECT_FSMS:
				posX = p.getWidth() / 3 + p.getWidth() / 3;
				for(int a = 0; a < MultiFSMSelection.MAX_SELECT_FSMS; a++) {
					String chozes = contents[a] == null ? DEFAULT_TEXT_ENTRY_CONTENTS : contents[a];
					if((chozes == null || chozes.equals(""))) {
						OptionPage.handleRectangle(getElementPrefix() + "_rect_fsm_entry_" + a, posX, posY, p.getWidth() / 3, p.getHeight() / 30, Color.white, Color.gray);
						OptionPage.handleButton(getElementPrefix() + "_butt_fsm_entry", posX, posY, p.getWidth() / 3, p.getHeight() / 30, code);
						break;
					}
					posY += p.getHeight() / 18;
					OptionPage.handleRectangle(getElementPrefix() + "_rect_fsm_entry_" + a, posX, posY, p.getWidth() / 3, p.getHeight() / 30, Color.white, Color.gray);
					OptionPage.handleText(getElementPrefix() + "_text_entry_fsm_entry_" + a, posX, posY, p.getWidth() / 3, p.getHeight() / 30, DEFAULT_FONT, chozes);
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
			OptionPage.handleRectangle(getElementPrefix() + "_" + code + "_rect", posX, posY, p.getHeight() / 30, p.getHeight() / 30, Color.black, Color.gray);
			OptionPage.handleButton(getElementPrefix() + "_" + code + "_butt", posX, posY, p.getHeight() / 30, p.getHeight() / 30, code);
			OptionPage.handleLine(getElementPrefix() + "_" + code + "_line", p.getWidth() / 20, posY + p.getHeight() / 40, p.getWidth() * 19 / 20, posY + p.getHeight() / 40, 1, Color.black);
		}
		else {
			OptionPage.handleLine(getElementPrefix() + "_" + code + "_line", p.getWidth() / 20, posY + p.getHeight() / 40, p.getWidth() * 17 / 20, posY + p.getHeight() / 40, 1, Color.gray);
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
				new SingleFSMSelection(this);
				break;
			case ENTRY_SELECT_FSMS:
				new MultiFSMSelection(this);
				break;
			case ENTRY_AGENTS:
				new AgentSelection(this, contents[0]);
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
	
	public String getElementNameTextEntry(int ind) {
		return getElementPrefix() + "_text_entry_" + ind;
	}

	private int getEntryContentSize(String in) {
		switch(in) {
			case TEXT_DISPLAY:
				return 64;
			case ENTRY_AGENTS:
				return 1;	//TODO: More spots probably; need file path for Plant fsm
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
