package ui.page.optionpage.entryset;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import visual.composite.HandlePanel;

/**
 * TODO: Probably wanna break this up with an interface or abstract class?
 * 
 * @author Ada Clevinger
 *
 */

public abstract class EntrySet {
	
//---  Constants   ----------------------------------------------------------------------------

	protected final static Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 12);
	private final static int SUBSYSTEM_CODE_DEFAULT = -1500;
	public final static String SIGNIFIER_TRUE = "t";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	protected static int subSystemCode = SUBSYSTEM_CODE_DEFAULT;
	
	private ArrayList<String> contents;
	private HashMap<Integer, String> codes;
	
	private String prefix;
	private String label;
	private boolean button;
	private int code;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public EntrySet(String inPref, String inName, boolean submit, int inCode) {
		contents = new ArrayList<String>();
		prefix = inPref;
		label = inName;
		button = submit;
		code = inCode;
		codes = new HashMap<Integer, String>();
		codes.put(code, "");
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public int drawEntrySet(int y, int lineHei, HandlePanel p) {
		p.handleText(prefix() + "_entry_set_" + label + "_label", false, p.getWidth() / 8, y, p.getWidth() / 4, lineHei, DEFAULT_FONT, label);
		
		y = draw(y, lineHei, p);
		
		if(button) {
			int posX = p.getWidth() * 11 / 12;
			p.handleRectangle(prefix() + "_button_rect", false, 5, posX, y, p.getHeight() / 30, p.getHeight() / 30, Color.black, Color.gray);
			p.handleButton(prefix() + "_button_butt", false, posX, y, p.getHeight() / 30, p.getHeight() / 30, code);
		}
		p.handleLine(prefix() + "_underscore_line", false, 5, p.getWidth() / 20, y + p.getHeight() / 40, p.getWidth() * (button ? 17 : 19) / 20, y + p.getHeight() / 40, 1, Color.black);
		
		y += lineHei;
		return y;
	}
	
	protected abstract int draw(int y, int lineHei, HandlePanel p);
	
	public boolean handleInput(int code, HandlePanel p) {
		return getCodeMapping(code).equals("");
	}
	
	public void resetContent() {
		int i = contents.size();
		contents.clear();
		for(int a = 0; a < i; a++) {
			contents.add("");
		}
	}
	
	public void reset(HandlePanel p) {
		for(int i = 0; i < contents.size(); i++) {
			contents.set(i, "");
		}
		deregisterCodes();
		registerCode(code, "");
		p.removeElementPrefixed(prefix());
	}
	
	protected void registerCode(int in, String ref) {
		codes.put(in, ref);
	}
	
	protected void deregisterCode(int in) {
		codes.remove(in);
	}
	
	protected void deregisterCodes() {
		codes.clear();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setContents(ArrayList<String> in) {
		contents = in;
	}
	
	public void setContent(String in, int index) {
		if(index == contents.size()) {
			contents.add(in);
		}
		else if(index >= 0 && index < contents.size()) {
			contents.set(index, in);
		}
	}

	public void addContent(String in) {
		for(int i = 0; i < contents.size(); i++) {
			if(contents.get(i).equals("")) {
				contents.set(i, in);
				return;
			}
		}
		contents.add(in);
	}
	
	public void removeContentAt(int i) {
		contents.remove(i);
		contents.add("");
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	protected String getCodeMapping(int code) {
		return codes.get(code);
	}
	
	protected String prefix() {
		return prefix + "_entry_set_" + label + "_" + code;
	}
	
	public boolean containsCode(int in) {
		return codes.get(in) != null;
	}

	public String getName() {
		return label;
	}
	
	public int getCode() {
		return code;
	}

	public String getContentAt(int i) {
		return contents.get(i);
	}
	
	public ArrayList<String> getContents(){
		return contents;
	}

}
