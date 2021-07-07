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
	private final static int CODE_REMOVE_RANGE = 1000;
	private final static double BLOCK_SIZE_RATIO = 1.0 / 6;
	
	private Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 32);
	private Font SMALLER_FONT = new Font("Serif", Font.BOLD, 10);
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private ArrayList<AgentRep> agents;
	private ArrayList<String> refEvents;
	private ArrayList<String> attributes;
	private volatile boolean ready;

//---  Constructors   -------------------------------------------------------------------------
	
	public PopoutAgentSelection(ArrayList<String> inAge, ArrayList<String> inEven, ArrayList<String> inAttrib) {
		super(WIDTH, HEIGHT);
		setTitle("Configure Agents");
		refEvents = inEven;
		agents = new ArrayList<AgentRep>();
		for(String s : inAge) {
			if(!s.equals(""))
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

		//Drawing: Key for true/false submission data
		
		int posY = vertSpacing / 3;
		int posX = horizSpacing / 3;
		
		int size = horizSpacing / 5;
		
		handleRectangle("key_rect_false", "move", 5, posX, posY, size, size, Color.red, Color.black);
		handleText("key_text_false", "move", 15, posX + horizSpacing / 4, posY, size, size, SMALLER_FONT, "= False");
		
		posY += vertSpacing / 3;
		
		handleRectangle("key_rect_true", "move", 5, posX, posY, size, size, Color.green, Color.black);
		handleText("key_text_true", "move", 15, posX + horizSpacing / 4, posY, size, size, SMALLER_FONT, "= True");
		
		//Drawing: Event name labels with subscript for attributes
		
		posY = vertSpacing / 2;
		posX = horizSpacing;
		
		for(int i = 0; i < refEvents.size(); i++) {
			String nom = refEvents.get(i);
			handleText("event_name_" + i, "move", 15, posX + horizSpacing / 2, posY, horizSpacing, height, DEFAULT_FONT, nom);

			//Drawing: Attributes subscript
			for(int j = 0; j < attributes.size(); j++) {
				int texWid = getHandlePanel().getTextWidth(" " + attributes.get(j) + " ", SMALLER_FONT);
				handleText("attr_name_tag_" + attributes.get(j) + "_" + i, "move", 15, posX + texWid / 2, posY + vertSpacing / 4, texWid, height, SMALLER_FONT, attributes.get(j));
				posX += texWid;
			}
			
			handleRectangle("event_border_top_" + i, "move", 5, posX - horizSpacing / 2, posY, horizSpacing, height, Color.white, Color.black);
		}
		posY += height * 3 / 2;
		int toggleEvents = CODE_TOGGLE_EVENT;
		
		//Drawing: Each agent with composite events w/ attribute info
		
		for(int i = 0; i < agents.size(); i++) {
			AgentRep ag = agents.get(i);
			posX = horizSpacing / 2;
			//Draw name (plant or i'th agent)
			handleText("agent_name_" + i, "move", 15, posX, posY, horizSpacing, height, DEFAULT_FONT, "Agent " + i);
			handleButton("agent_name_remove_" + i, "move", 15, posX, posY, horizSpacing, height, CODE_REMOVE_RANGE + i);
			posX += horizSpacing / 2;
			removeElementPrefixed("event_attr_rect_tag_" + i);
			for(int j = 0; j < refEvents.size(); j++) {
				EventRep e = ag.getEvent(j);
				posX += horizSpacing / attributes.size() / 2;
				size = horizSpacing / attributes.size() / 3;
				for(int k = 0; k < attributes.size(); k++) {
					handleRectangle("event_attr_rect_tag_" + i + "_" + j + "_" + attributes.get(k), "move", 8, posX, posY, size, size, e.getValue(k) ? Color.green : Color.red, Color.black);
					handleButton("event_attr_butt_tag_" + attributes.get(k) + "_" + i + "_" + j, "move", 15, posX, posY, size, size, toggleEvents++);
					posX += horizSpacing / attributes.size();
				}
				posX -= horizSpacing / 2 - horizSpacing / attributes.size() / 2;
				handleRectangle("event_border_agent_" + i + "_" + j, "move",  5, posX - horizSpacing / 2, posY, horizSpacing, height, Color.white, Color.black);
			}
			posY += vertSpacing;
		}
		
		//Drawing: Add agent button and submission of the totality
		
		posX = horizSpacing / 2;
		handleText("agent_name_add", "move", 15, posX, posY, horizSpacing, height, DEFAULT_FONT, "+");
		handleButton("agent_add_button", "move", 15, posX, posY, horizSpacing, height, CODE_ADD_AGENT);
		posY += height;
		posX += refEvents.size() * height;
		handleText("submit", "move", 15, posX, posY, horizSpacing, height, DEFAULT_FONT, "Submit");
		handleButton("submit_button", "move", 15, posX, posY, horizSpacing, height, CODE_SUBMIT);
		posY += height;
		handleText("buffer", "move", 15, 0, posY, 1, 1, DEFAULT_FONT, "");
	}
	
	@Override
	public void clickAction(int code, int x, int y) {
		if(code >= CODE_TOGGLE_EVENT && code < CODE_REMOVE_RANGE) {
			int pos = code - CODE_TOGGLE_EVENT;
			int age = pos / (refEvents.size() * attributes.size());
			int eve = (pos - age * (refEvents.size() * attributes.size())) / attributes.size();
			int attr = (pos - age * (refEvents.size() * attributes.size())) % attributes.size();
			agents.get(age).getEvents().get(eve).toggle(attr);
		}
		if(code >= CODE_REMOVE_RANGE) {
			int pos = code - CODE_REMOVE_RANGE;
			if(pos >= 0 && pos < agents.size()) {
				for(int i = pos; i < agents.size() - 1; i++) {
					agents.set(i, agents.get(i + 1));
					agents.get(i).setName((i+1)+"");
				}
				agents.remove(agents.size() - 1);
				removeElementPrefixed("");
			}
		}
		switch(code) {
		//TODO: Allow removal of an agent
			case CODE_ADD_AGENT:
				agents.add(new AgentRep(""+(agents.size() + 1), refEvents, attributes.size()));
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
			System.out.println(a.toString());
			out.add(a.toString());
		}
		return out;
	}

}
