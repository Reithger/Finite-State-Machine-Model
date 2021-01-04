package ui.popups;

import java.awt.Color;
import java.util.ArrayList;

import ui.page.optionpage.entryset.EntrySet;
import visual.frame.WindowFrame;

public class SingleFSMSelection extends WindowFrame{

	protected final static int WIDTH_SELECT_FSM_WINDOW = 300;
	protected final static int HEIGHT_SELECT_FSM_WINDOW = 200;
	protected final static Color BACK_COLOR_GRAY = new Color(192, 192, 192);;
	protected final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);

	private SingleFSMPanel pan;
	
	public SingleFSMSelection(EntrySet ref, ArrayList<String> names) {
		super(WIDTH_SELECT_FSM_WINDOW, HEIGHT_SELECT_FSM_WINDOW);
		setName("Select FSM for Operation");
		setResizable(true);
		setExitOnClose(false);
		pan = new SingleFSMPanel(WIDTH_SELECT_FSM_WINDOW, HEIGHT_SELECT_FSM_WINDOW, ref, names);
		addPanelToWindow("default", "pan", pan);
	}
	
	@Override
	public void reactToResize() {
		pan.resize(WIDTH_SELECT_FSM_WINDOW, this.getHeight());
	}

}
