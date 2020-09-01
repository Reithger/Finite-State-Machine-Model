package ui.page.optionpage.entryset;

import java.io.File;
import java.util.ArrayList;

import fsm.FSM;
import support.component.Event;
import support.component.map.EventMap;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class AgentSelection extends WindowFrame{

	private final static int WIDTH = 800;
	private final static int HEIGHT = 600;
	
	private GridPanel gP;
	
	ArrayList<Event> events;
	
	public AgentSelection(EntrySet cont, String ref) {
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
		events = new ArrayList<Event>(fs.getEvents());
		gP = new GridPanel(0, 0, WIDTH, HEIGHT);
		reservePanel("default", "pan", gP);
	}
	
	public class GridPanel extends ElementPanel{

		public GridPanel(int x, int y, int width, int height) {
			super(x, y, width, height);
		}
		
	}

}
