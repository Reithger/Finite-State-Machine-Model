package ui.popups;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import visual.composite.popout.PopoutWindow;

public class PopoutAgentSelection extends PopoutWindow{

//---  Constants   ----------------------------------------------------------------------------
	
	private final static int WIDTH = 800;
	private final static int HEIGHT = 600;
	
	private final static int CODE_ADD_AGENT = 10;
	private final static int CODE_SUBMIT = 15;
	private final static int CODE_TOGGLE_EVENT = 500;
	private final static double BLOCK_SIZE_RATIO = 1.0 / 6;
	
	private Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 32);
	private Font SMALLER_FONT = new Font("Serif", Font.BOLD, 10);
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private ArrayList<AgentRep> agents;
	private ArrayList<String> refEvents;
	private ArrayList<String> attributes;
	private boolean ready;

//---  Constructors   -------------------------------------------------------------------------
	
	public PopoutAgentSelection(ArrayList<String> inAge, ArrayList<String> inEven, ArrayList<String> inAttrib) {
		super(WIDTH, HEIGHT);
		setTitle("Configure Agents");
		refEvents = inEven;
		agents = new ArrayList<AgentRep>();
		for(String s : inAge) {
			agents.add(new AgentRep(s));
		}
		attributes = inAttrib;
		
		drawPage();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void drawPage() {
		int horizSpacing = 0;
		for(String s : attributes) {
			horizSpacing += this.getHandlePanel().getTextWidth(" " + s + " ", SMALLER_FONT);
		}
		int vertSpacing = (int)(WIDTH * BLOCK_SIZE_RATIO);
		int height = vertSpacing * 2 / 3;
		int posY = vertSpacing / 2;
		int posX = horizSpacing;
		removeElementPrefixed("");
		ArrayList<EventRep> events = agents.get(0).getEvents();
		for(int i = 0; i < events.size(); i++) {
			String nom = events.get(i).getName();
			handleText("event_name_" + i, false, posX - horizSpacing / 2, posY, horizSpacing, height, DEFAULT_FONT, nom);
			
			for(int j = 0; j < attributes.size(); j++) {
				int texWid = getHandlePanel().getTextWidth(" " + attributes.get(j) + " ", SMALLER_FONT);
				handleText("attr_name_tag_" + attributes.get(j) + "_" + i, false, posX + texWid / 2, posY + vertSpacing / 3, texWid, height, SMALLER_FONT, attributes.get(j));
				posX += texWid;
			}
			
			handleRectangle("event_border_" + i, false,  5, posX - horizSpacing / 2, posY, horizSpacing, height, Color.white, Color.black);
		}
		posY += height * 3 / 2;
		int toggleEvents = CODE_TOGGLE_EVENT;
		for(int i = 0; i < agents.size(); i++) {
			posX = 0;
			//Draw name (plant or i'th agent)
			handleText("agent_name_" + i, false, posX + horizSpacing / 2, posY, horizSpacing, height, DEFAULT_FONT, i == 0 ? "Plant" : ("Agent " + i));
			posX += horizSpacing / attributes.size() / 2;
			for(int j = 0; j < events.size(); j++) {
				EventRep e = events.get(j);
				posX += horizSpacing / attributes.size();
				for(int k = 0; k < attributes.size(); k++) {
					handleRectangle("event_attr_rect_tag_" + attributes.get(k) + "_" + i + "_" + j, false, 5, posX, posY + vertSpacing / 3, horizSpacing / attributes.size(), height, e.getValue(k) ? Color.DARK_GRAY : Color.white, Color.black);
					handleButton("event_attr_butt_tag_" + attributes.get(k) + "_" + i + "_" + j, false, posX, posY + vertSpacing / 3, horizSpacing / attributes.size(), height, toggleEvents++);
					posX += horizSpacing / attributes.size();
				}
				posX -= horizSpacing / attributes.size() / 2;
				handleRectangle("event_border_" + i, false,  5, posX - horizSpacing / 2, posY, horizSpacing, height, Color.white, Color.black);
			}
			posY += vertSpacing;
		}
		posX = horizSpacing / 2;
		handleText("agent_name_add", false, posX, posY, horizSpacing, height, DEFAULT_FONT, "+");
		handleButton("agent_add_button", false, posX, posY, horizSpacing, height, CODE_ADD_AGENT);
		posX += events.size() * height;
		handleText("submit", false,  posX, posY, horizSpacing, height, DEFAULT_FONT, "Submit");
		handleButton("submit_button", false,  posX, posY, horizSpacing, height, CODE_SUBMIT);
		posY += height;
		handleText("buffer", false,  0, posY, 1, 1, DEFAULT_FONT, "");
	}
	
	@Override
	public void clickAction(int code, int x, int y) {
		if(code >= CODE_TOGGLE_EVENT + agents.size() * refEvents.size() * attributes.size()) {
			int pos = code - CODE_TOGGLE_EVENT;
			int age = pos / (refEvents.size() * attributes.size());
			int eve = (pos - age * (refEvents.size() * attributes.size())) / attributes.size();
			int attr = (pos - age * (refEvents.size() * attributes.size())) % attributes.size();
			agents.get(age).getEvents().get(eve).toggle(attr);
		}
		switch(code) {
			case CODE_ADD_AGENT:
				agents.add(new AgentRep(""+(agents.size() - 1), refEvents, attributes.size()));
				break;
			case CODE_SUBMIT:
				ready = true;
				break;
			default:
				break;
		}
		drawPage();
	}

//---  Setter Methods   -----------------------------------------------------------------------
	
	public static void assignSymbols(String separator, String tr, String fa) {
		AgentRep.assignSymbols(separator, tr, fa);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public ArrayList<String> getResult(){
		while(!ready) {	}
		ArrayList<String> out = new ArrayList<String>();
		for(AgentRep a : agents) {
			out.add(a.toString());
		}
		return out;
	}

}
