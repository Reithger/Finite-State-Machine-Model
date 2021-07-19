package ui.page.displaypage;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CodeReference;
import input.CustomEventReceiver;
import input.NestedEventReceiver;
import ui.InputHandler;
import ui.headers.HeaderSelect;
import visual.composite.HandlePanel;

public class DisplayPageManager implements InputHandler{

//---  Constants   ----------------------------------------------------------------------------

	private final static int CODE_BASE_DISPLAY_HEADER = 4000;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private HeaderSelect imageHeader;
	
	private DisplayPage display;
	
	private HandlePanel p;
	
	private ArrayList<FSMInfo> fsms;
	
	private int index;
	
	private InputHandler reference;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public DisplayPageManager(InputHandler ref, int xIn, int yIn, int wid, int hei, double vertProp) {
		generateElementPanel(wid, (int)(hei * (1 - vertProp)), wid, (int)(hei * vertProp));
		reference = ref;

		imageHeader = new HeaderSelect(wid, 0, wid, (int)(hei * (1 - vertProp)), CODE_BASE_DISPLAY_HEADER); 
		
		imageHeader.setInputHandler(reference);

		display = new DisplayPage(this, p);
		p.addEventReceiver(new CustomEventReceiver() {
			
			@Override
			public void clickEvent(int code, int x, int y, int mouseType) {
				receiveCode(code, mouseType);
			}
			
		});
		fsms = new ArrayList<FSMInfo>();
		index = 0;
	}

//---  Operations   ---------------------------------------------------------------------------

	@Override
	public void receiveCode(int code, int mouseType) {
		if(code == CodeReference.CODE_DISPLAY_CYCLE_VIEW) {
			toggleDisplayImageMode();
		}
		else {
			reference.receiveCode(code, mouseType);
		}
	}

	@Override
	public void receiveKeyInput(char code, int keyType) {
		reference.receiveKeyInput(code, keyType);
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
		display.draw();
		imageHeader.update(getDisplayNames(), index);
	}
	
	public void toggleDisplayImageMode() {
		p.removeElementPrefixed("");
		display.toggleDisplayMode();
		drawPage();
	}

	public void removeFSM(String ref) {
		
	}
	
	public void updateFSMInfo(String ref, ArrayList<String> stateAttrib, ArrayList<String> eventAttrib, ArrayList<String> tranAttrib,
			HashMap<String, ArrayList<Boolean>> stateMap, HashMap<String, ArrayList<Boolean>> eventMap, HashMap<String, ArrayList<Boolean>> transMap) {
		FSMInfo use = getFSMInfo(ref);
		if(use == null) {
			use = new FSMInfo(ref);
			fsms.add(use);
		}
		use.updateStateAttributes(stateAttrib);
		use.updateEventAttributes(eventAttrib);
		use.updateTransitionAttributes(tranAttrib);
		use.updateStateDetails(stateMap);
		use.updateEventDetails(eventMap);
		use.updateTransitionDetails(transMap);
		if(!display.hasDisplay()) {
			setCurrentDisplayIndex(index);
		}
	}

	public void updateFSMImage(String ref, String img) {
		FSMInfo use = getFSMInfo(ref);
		if(use != null) {
			use.updateImage(img);
			display.updateImage();
		}
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setCurrentDisplayIndex(int ind) {
		if(ind != index || !display.hasDisplay()) {
			index = ind;
			display.updateFSMInfo(getCurrentFSMInfo());
			display.updateImage();
			display.adjustOffsets();
		}
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
	
	private FSMInfo getFSMInfo(String ref) {
		for(FSMInfo fsm : fsms) {
			if(fsm.getName().equals(ref)) {
				return fsm;
			}
		}
		return null;
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
