package ui.headers;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import controller.InputReceiver;
import input.CustomEventReceiver;
import visual.composite.HandlePanel;

public class HeaderSelect extends HandlePanel{
	
//---  Constants   ----------------------------------------------------------------------------
	
	private final static int ROTATION_MULTIPLIER = 10;
	private final static Font HEADER_FONT = new Font("Serif", Font.BOLD, 12);
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private InputReceiver reference;
	private int codeBase;

//---  Constructors   -------------------------------------------------------------------------
	
	public HeaderSelect(int x, int y, int wid, int hei, int inCode) {
		super(x, y, wid, hei);
		this.setScrollBarVertical(false);
		codeBase = inCode;
		setEventReceiver(new CustomEventReceiver() {
			@Override
			public void mouseWheelEvent(int rotation) {
				if(getMaximumScreenX() < getWidth()) {
					return;
				}
				setOffsetXBounded(getOffsetX() + rotation * ROTATION_MULTIPLIER);
			}
			
			@Override
			public void clickEvent(int code, int x, int y, int mouseType) {
				reference.receiveCode(code, mouseType);
			}
		});
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void updateSize(int x, int y, int wid, int hei) {
		setLocation(x, y);
		resize(wid, hei);
	}
	
	public void update(ArrayList<String> header, int select) {
		removeElementPrefixed("header");
		int wid = getWidth();
		int hei = getHeight();
		if(header.size() == 0) {
			int posX = wid/ 2;
			int posY = hei / 2;
			int widUse = wid / 3;
			int heiUse = hei * 2 / 3;
			handleRectangle("stand_in_header_rect", false, 10, posX, posY, widUse, heiUse, Color.gray, Color.black);
			handleText("stand_in_header_text", false, posX, posY, widUse, heiUse, HEADER_FONT, "No images currently available");
		}
		else {
			removeElementPrefixed("stand_in");
			for(int i = 0 ; i < header.size(); i++) {
				int posX = wid / 8 + i * (wid / 4);
				int posY = (int)(hei / 2);
				int widUse = wid /  5;
				int heiUse = (int)(hei * 2 / 3);
				if(i == select) {
					handleRectangle("header_rect_active", false, 12, posX, posY, widUse, heiUse, Color.green, Color.black);
				}
				handleRectangle("header_rect_" + i, false, 10, posX, posY, widUse, heiUse, Color.gray, Color.black);
				handleButton("header_butt_" + i, false, posX, posY, widUse, heiUse, codeBase + i);
				handleText("header_text_" + i, false, posX, posY, widUse, heiUse, HEADER_FONT, header.get(i));
			}
		}
		int thick = 2;
		this.handleLine("header_line_1", true, 15, thick / 2, thick / 2, this.getWidth() - thick / 2, thick / 2, thick, Color.black);
		this.handleLine("header_line_2", true, 15, thick / 2, thick / 2, thick / 2, this.getHeight() - thick / 2, thick, Color.black);
		this.handleLine("header_line_3", true, 15, this.getWidth() - thick / 2, this.getHeight() - thick / 2, thick / 2, this.getHeight() - thick / 2, thick, Color.black);
		this.handleLine("header_line_4", true, 15, this.getWidth() - thick / 2, this.getHeight() - thick / 2, this.getWidth() - thick / 2, thick / 2, thick, Color.black);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setInputReceiver(InputReceiver in) {
		reference = in;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public int getCodeBase() {
		return codeBase;
	}
	
	@Override
	public int getMinimumScreenX() {
		return 0;
	}
	
}
