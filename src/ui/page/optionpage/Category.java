package ui.page.optionpage;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;

import ui.page.optionpage.entryset.EntrySet;
import visual.panel.ElementPanel;

public class Category {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	private final static Font CATEGORY_FONT = new Font("Serif", Font.BOLD, 12);

//---  Instance Variables   -------------------------------------------------------------------
	
	private String name;
	private ArrayList<EntrySet> sets;
	private HashSet<Integer> contained;
	private boolean open;
	private int code;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Category(String nom) {
		name = nom;
		sets = new ArrayList<EntrySet>();
		contained = new HashSet<Integer>();
		open = false;
	}

//---  Operations   ---------------------------------------------------------------------------

	public int drawCategoryHeader(int y) {
		ElementPanel p = OptionPage.getElementPanel();
		int posX = p.getWidth() / 3 / 2;
		int wid =  p.getWidth() * 9/10;
		int hei = p.getHeight() / 20;
		OptionPage.handleText("top_category_" + name + "_header_text", posX, y, wid, hei, CATEGORY_FONT, name);
		OptionPage.handleButton("top_category_" + name + "_header_butt_" + name, posX, y, wid, hei, code);
		OptionPage.handleLine("top_category_" + name + "_header_line_" + name, p.getWidth() / 20, y + p.getHeight() / 40, p.getWidth() * 11 / 20, y + p.getHeight() / 40, 3, Color.black);
		return y + p.getHeight() / 18;
	}

	public void toggleOpen() {
		open = !open;
	}

	public void hideContents() {
		OptionPage.getElementPanel().moveElementPrefixed("category_" + name, -100, -100);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCode(int in) {
		code = in;
	}
	
	public void addEntrySet(EntrySet in) {
		in.setCategory(name);
		sets.add(in);
		contained.add(in.getCode());
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public EntrySet getEntrySet(int code) {
		for(EntrySet e : sets) {
			if(e.getCode() == code) {
				return e;
			}
		}
		return null;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public String getContents(int code, int posit) {
		for(EntrySet e : sets) {
			if(e.getCode() == code) {
				return e.getContentAtIndex(posit);
			}
		}
		return null;
	}

	public int getCode() {
		return code;
	}
	
	public ArrayList<EntrySet> getEntrySets(){
		return sets;
	}
		
	public String getTitle() {
		return name;
	}
	
	public boolean contains(int code) {
		return contained.contains(code);
	}

}
