package ui.page.displaypage;

import java.util.ArrayList;

import controller.InputReceiver;
import input.CustomEventReceiver;
import input.NestedEventReceiver;
import ui.headers.HeaderSelect;
import visual.composite.HandlePanel;

public class DisplayPageManager implements InputReceiver{

//---  Constants   ----------------------------------------------------------------------------

	private final static int CODE_BASE_DISPLAY_HEADER = 4000;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private HeaderSelect imageHeader;
	
	private DisplayPage display;
	
	private HandlePanel p;
	
	private ArrayList<FSMInfo> fsms;
	
	private int index;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public DisplayPageManager(InputReceiver reference, int xIn, int yIn, int wid, int hei, double vertProp) {
		generateElementPanel(wid, (int)(hei * (1 - vertProp)), wid, (int)(hei * vertProp));

		imageHeader = new HeaderSelect(wid, 0, wid, (int)(hei * (1 - vertProp)), CODE_BASE_DISPLAY_HEADER); 
		
		imageHeader.setInputReceiver(reference);

		display = new DisplayPage(this);
		NestedEventReceiver ner = new NestedEventReceiver(display.getEventHandling(p));
		fsms = new ArrayList<FSMInfo>();
		p.setEventReceiver(ner);
		index = 0;
	}

//---  Operations   ---------------------------------------------------------------------------

	@Override
	public void receiveCode(int code, int mouseType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveKeyInput(char code, int keyType) {
		// TODO Auto-generated method stub
		
	}
	
	public void generateElementPanel(int x, int y, int width, int height) {
		p = new HandlePanel(x, y, width, height);
	}

	public void updateSizeLoc(int x, int y, int wid, int hei, double vertProp) {
		p.resize(wid, (int)(hei * vertProp));
		p.setLocation(x, y + (int)(hei * (1 - vertProp)));
		p.removeAllElements();
		imageHeader.updateSizeLoc(x, y, wid, (int)(hei * (1 - vertProp)));
		drawPage();
	}
	
	public void drawPage() {
		display.draw(p);
		imageHeader.update(getDisplayNames(), index);
	}
	
	public void toggleDisplayImageMode() {
		p.removeAllElements();
		display.toggleDisplayMode();
		drawPage();
	}
	
	public void allotFSM(String ref, String img) {
		
	}
	
	public void removeFSM(String ref) {
		
	}
	
	public void updateFSM(String ref, String img) {
		
	}

//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCurrentDisplayIndex(int ind) {
		index = ind;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getCurrentFSM() {
		return fsms.size() == 0 ? null : fsms.get(index).getName();
	}
	
	private FSMInfo getCurrentFSMInfo() {
		if(index < 0 || index >= fsms.size()) {
			return null;
		}
		return fsms.get(index);
	}
	
	private ArrayList<String> getDisplayNames(){
		ArrayList<String> out = new ArrayList<String>();
		for(FSMInfo f : fsms) {
			out.add(f.getName());
		}
		return out;
	}
	
	public int getCodeReferenceBase() {
		return CODE_BASE_DISPLAY_HEADER;
	}
	
	public int getSizeDisplayList() {
		return fsms.size();
	}
	
	public HandlePanel getBodyPanel() {
		return p;
	}
	
	public HeaderSelect getHeaderPanel() {
		return imageHeader;
	}


}
