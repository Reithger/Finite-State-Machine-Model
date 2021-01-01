package ui.page.popups;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import ui.page.optionpage.implementation.UStructurePage;
import visual.composite.popout.PopoutWindow;

public class PopoutAgentSelection extends PopoutWindow{

	private final static int WIDTH = 800;
	private final static int HEIGHT = 600;
	
	private final static int CODE_ADD_AGENT = 10;
	private final static int CODE_SUBMIT = 15;
	private final static int CODE_TOGGLE_EVENT = 500;
	private final static double BLOCK_SIZE_RATIO = 1.0 / 6;
	private Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 32);
	private Font SMALL_FONT = new Font("Serif", Font.BOLD, 12);
	private Font SMALLER_FONT = new Font("Serif", Font.BOLD, 10);
	
	ArrayList<Agent> agents;
	ArrayList<Entity> events;
	UStructurePage reference;
	
	public PopoutAgentSelection(UStructurePage context, String ref) {
		super(WIDTH, HEIGHT);
		setTitle("Configure Agents");
		FSM fs = null;
		ref = (ref.contains("\\.fsm") ? ref : (ref + ".fsm"));
		try {
			fs = new FSM(new File(ref), "");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		Collection<Entity> inE = fs.getEvents();
		events = new ArrayList<Entity>(inE);
		agents = new ArrayList<Agent>();
		agents.add(new Agent(inE));
		reference = context;
		drawPage();
	}
	
	public void drawPage() {
		int spacing = (int)(WIDTH * BLOCK_SIZE_RATIO);
		int height = spacing * 2 / 3;
		int posY = spacing / 2;
		int posX = spacing / 2;
		removeElementPrefixed("");
		for(int i = 0; i < events.size(); i++) {
			posX += spacing;
			handleText("event_name_" + i, false, posX, posY, spacing, height, DEFAULT_FONT, events.get(i).getEventName());
			handleText("event_name_tag_obs_" + i, false,  posX - spacing / 4, posY + spacing / 3, spacing / 2, height, SMALLER_FONT, "Observability");
			handleText("event_name_tag_cnt_" + i, false,  posX + spacing / 4, posY + spacing / 3, spacing / 2, height, SMALLER_FONT, "Controllable");
			handleRectangle("event_border_" + i, false,  5, posX, posY, spacing, height, Color.white, Color.black);
		}
		posY += height * 3 / 2;
		for(int i = 0; i < agents.size(); i++) {
			posX = spacing / 2;
			//Draw name (plant or i'th agent)
			handleText("agent_name_" + i, false,  posX, posY, spacing, height, DEFAULT_FONT, i == 0 ? "Plant" : ("Agent " + i));
			Agent a = agents.get(i);
			for(int j = 0; j < events.size(); j++) {
				posX += spacing;
				String e = events.get(j).getEventName();
				handleText("agent_event_obs_" + i + "_" + j, false,  posX - spacing / 4, posY, spacing / 2, height,  SMALL_FONT,""+a.getObservable(e));
				handleText("agent_event_con_" + i + "_" + j, false,  posX + spacing / 4, posY, spacing / 2, height, SMALL_FONT, ""+a.getControllable(e));
				handleButton("agent_event_toggle_" + i + "_" + j, false,  posX, posY, spacing, height, CODE_TOGGLE_EVENT + i * events.size() + j);
				handleRectangle("agent_event_border_" + i + "_" + j, false,  5, posX, posY, spacing, height, Color.white, Color.black);
			}
			posY += spacing;
		}
		posX = height / 2;
		handleText("agent_name_add", false,  posX, posY, spacing, height, DEFAULT_FONT, "+");
		handleButton("agent_add_button", false, posX, posY, spacing, height, CODE_ADD_AGENT);
		posX += events.size() * height;
		handleText("submit", false,  posX, posY, spacing, height, DEFAULT_FONT, "Submit");
		handleButton("submit_button", false,  posX, posY, spacing, height, CODE_SUBMIT);
		posY += height;
		handleText("buffer", false,  0, posY, 1, 1, DEFAULT_FONT, "");
	}
	
	@Override
	public void clickAction(int code, int x, int y) {
		if(code >= CODE_TOGGLE_EVENT + events.size()) {
			int pos = code - CODE_TOGGLE_EVENT;
			Agent a = agents.get(pos / events.size());
			String e = events.get(pos % events.size()).getEventName();
			if(a.getObservable(e)) {
				if(a.getControllable(e)) {
					a.setObservable(e, false);
					a.setControllable(e, false);
				}
				else {
					a.setObservable(e, false);
					a.setControllable(e, true);
				}
			}
			else if(a.getControllable(e)) {
				a.setObservable(e, true);
				a.setControllable(e, true);
			}
			else {
				a.setObservable(e, true);
				a.setControllable(e, false);
			}
			
		}
		switch(code) {
			case CODE_ADD_AGENT:
				agents.add(new Agent(events));
				break;
			case CODE_SUBMIT:
				reference.setAgents(new ArrayList<Agent>(agents.subList(1, agents.size())));
				dispose();
				break;
			default:
				break;
		}
		drawPage();
	}


	@Override
	public void clickPressAction(int code, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clickReleaseAction(int code, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyAction(char code) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scrollAction(int scroll) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragAction(int code, int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
