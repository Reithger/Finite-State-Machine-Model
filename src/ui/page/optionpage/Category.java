package ui.page.optionpage;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import ui.page.optionpage.entryset.EntrySet;
import visual.composite.HandlePanel;

public class Category {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	private final static Font CATEGORY_FONT = new Font("Serif", Font.BOLD, 12);

//---  Instance Variables   -------------------------------------------------------------------
	
	private String name;
	private ArrayList<EntrySet> sets;
	private boolean open;
	private int catCode;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Category(String nom) {
		name = nom;
		sets = new ArrayList<EntrySet>();
		open = false;
	}

//---  Operations   ---------------------------------------------------------------------------

	public void draw(int y, int heiLine, HandlePanel p) {
		y = drawCategoryHeader(y, heiLine, p);
		
		if(open) {
			for(EntrySet e : sets) {
				e.drawEntrySet(y, heiLine, p);
			}
		}
		else {
			hideContents();
		}
	}
	
	public int drawCategoryHeader(int y, int lineHei, HandlePanel p) {
		int posX = p.getWidth() / 3 / 2;
		int wid =  p.getWidth() * 9/10;
		p.handleText("top_category_" + name + "_header_text", false, posX, y, wid, lineHei, CATEGORY_FONT, name);
		p.handleButton("top_category_" + name + "_header_butt_" + name, false, posX, y, wid, lineHei, catCode);
		p.handleLine("top_category_" + name + "_header_line_" + name, false, 5, p.getWidth() / 20, y + p.getHeight() / 40, p.getWidth() * 11 / 20, y + p.getHeight() / 40, 3, Color.black);
		return y + lineHei;
	}

	public void toggleOpen() {
		open = !open;
	}

	public void hideContents() {
		OptionPage.getHandlePanel().moveElementPrefixed("category_" + name, -100, -100);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCode(int in) {
		catCode = in;
	}
	
	public void addEntrySet(EntrySet in) {
		sets.add(in);
	}
	
	public void setEntrySetContents(int code, ArrayList<String> ref) {
		EntrySet e = getEntrySet(code);
		if(e != null) {
			e.setContents(ref);
		}
	}
	
	public void setEntrySetContent(int code, int index, String ref) {
		EntrySet e = getEntrySet(code);
		if(e != null) {
			e.setContent(ref, index);
		}
	}
	
	public void removeEntrySetContent(int code, int index) {
		EntrySet e = getEntrySet(code);
		if(e != null) {
			e.removeContentAt(index);
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getTitle() {
		return name;
	}
	
	public boolean isOpen() {
		return open;
	}

	public int getCode() {
		return catCode;
	}
	
	//-- EntrySet  --------------------------------------------
	
	protected EntrySet getEntrySet(int code) {
		for(EntrySet e : sets) {
			if(e.containsCode(code)) {
				return e;
			}
		}
		return null;
	}

	public ArrayList<String> getContents(int code){
		EntrySet e = getEntrySet(code);
		if(e != null) {
			return e.getContents();
		}
		return null;
	}
	
	public String getContent(int code, int posit) {
		EntrySet e = getEntrySet(code);
		if(e != null) {
			return e.getContentAt(posit);
		}
		return null;
	}

	public boolean contains(int code) {
		for(EntrySet e : sets) {
			if(e.containsCode(code)) {
				return true;
			}
		}
		return false;
	}

}
