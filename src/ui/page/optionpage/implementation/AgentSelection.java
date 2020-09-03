package ui.page.optionpage.implementation;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import fsm.FSM;
import support.Agent;
import support.component.Event;
import support.component.map.EventMap;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class AgentSelection extends WindowFrame{

	private final static int WIDTH = 800;
	private final static int HEIGHT = 600;
	
	private GridPanel gP;
	
	
	public AgentSelection(UStructurePage context, String ref) {
		super(WIDTH, HEIGHT);
		setExitOnClose(false);
		setResizable(true);
		setName("Configure Agents");
		FSM fs = null;
		try {
			fs = new FSM(new File(ref), "");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		gP = new GridPanel(0, 0, WIDTH, HEIGHT, context, fs.getEvents());
		reservePanel("default", "pan", gP);
	}
	
	public class GridPanel extends ElementPanel{

		private final static int CODE_ADD_AGENT = 10;
		private final static int CODE_SUBMIT = 15;
		private final static int CODE_TOGGLE_EVENT = 500;
		private final static double BLOCK_SIZE_RATIO = 1.0 / 6;
		private Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 32);
		private Font SMALL_FONT = new Font("Serif", Font.BOLD, 12);
		
		ArrayList<Agent> agents;
		ArrayList<Event> events;
		UStructurePage reference;
		
		public GridPanel(int x, int y, int width, int height, UStructurePage in, Collection<Event> inE) {
			super(x, y, width, height);
			events = new ArrayList<Event>(inE);
			agents = new ArrayList<Agent>();
			agents.add(new Agent(inE));
			reference = in;
			drawPage();
		}
		
		public void drawPage() {
			int spacing = (int)(getWidth() * BLOCK_SIZE_RATIO);
			int posY = spacing / 2;
			int posX = spacing / 2;
			removeElementPrefixed("");
			for(int i = 0; i < events.size(); i++) {
				posX += spacing;
				this.addText("event_name_" + i, 15, false, posX, posY, spacing, spacing, events.get(i).getEventName(), DEFAULT_FONT, true, true, true);
			}
			posY += spacing;
			for(int i = 0; i < agents.size(); i++) {
				posX = spacing / 2;
				//Draw name (plant or i'th agent)
				this.addText("agent_name_" + i, 15, false, posX, posY, spacing, spacing, i == 0 ? "Plant" : ("Agent " + i), DEFAULT_FONT, true, true, true);
				Agent a = agents.get(i);
				for(int j = 0; j < events.size(); j++) {
					posX += spacing;
					String e = events.get(j).getEventName();
					this.addText("agent_event_obs_" + i + "_" + j, 15, false, posX - spacing / 4, posY, spacing / 2, spacing, ""+a.getObservable(e), SMALL_FONT, true, true, true);
					this.addText("agent_event_con_" + i + "_" + j, 15, false, posX + spacing / 4, posY, spacing / 2, spacing, ""+a.getControllable(e), SMALL_FONT, true, true, true);
					this.addButton("agent_event_toggle_" + i + "_" + j, 15, false, posX, posY, spacing, spacing, null, CODE_TOGGLE_EVENT + i * events.size() + j, true);
				}
				posY += spacing;
			}
			posX = spacing / 2;
			this.addText("agent_name_add", 15, false, posX, posY, spacing, spacing, "+", DEFAULT_FONT, true, true, true);
			this.addButton("agent_add_button", 15, false, posX, posY, spacing, spacing, null, CODE_ADD_AGENT, true);
			posX += events.size() * spacing;
			this.addText("submit", 15, false, posX, posY, spacing, spacing, "Submit", DEFAULT_FONT, true, true, true);
			this.addButton("submit_button", 15, false, posX, posY, spacing, spacing, null, CODE_SUBMIT, true);
		}
		
		@Override
		public void clickBehaviour(int code, int x, int y) {
			if(code >= CODE_TOGGLE_EVENT) {
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
					this.getParentFrame().disposeFrame();
					break;
				default:
					break;
			}
			drawPage();
		}
		
	}

}
