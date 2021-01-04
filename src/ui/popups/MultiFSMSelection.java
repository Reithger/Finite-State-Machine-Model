package ui.popups;

import java.awt.Color;
import java.util.ArrayList;

import ui.page.optionpage.entryset.EntrySet;
import visual.frame.WindowFrame;

public class MultiFSMSelection extends WindowFrame{
	
	protected final static int WIDTH_SELECT_FSMS_WINDOW = 500;
	protected final static int HEIGHT_SELECT_FSMS_WINDOW = 300;
	protected final static int WIDTH_SELECT_FSMS_PANEL = 250;
	protected final static int HEIGHT_SELECT_FSMS_PANEL = 300;
	protected final static Color BACK_COLOR_GRAY = new Color(192, 192, 192);
	protected final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
	protected final static double BOX_HEIGHT_RATIO = 1.0 / 6;
	public final static int MAX_SELECT_FSMS = 8;
	
	private MultiFSMSelectedPanel selected;
	private MultiFSMSelectablePanel choice;
	private EntrySet context;
	
	public MultiFSMSelection(ArrayList<String> fsms, EntrySet ref) {
		super(WIDTH_SELECT_FSMS_WINDOW, HEIGHT_SELECT_FSMS_WINDOW);
		context =ref;
		setExitOnClose(false);
		setResizable(true);
		setName("Select FSMs For Operation");
		selected = new MultiFSMSelectedPanel(WIDTH_SELECT_FSMS_PANEL, HEIGHT_SELECT_FSMS_PANEL, MAX_SELECT_FSMS, this);
		choice = new MultiFSMSelectablePanel(WIDTH_SELECT_FSMS_WINDOW - WIDTH_SELECT_FSMS_PANEL, WIDTH_SELECT_FSMS_PANEL, HEIGHT_SELECT_FSMS_PANEL, fsms, this);
		addPanelToWindow("default", "pan", selected);
		addPanelToWindow("default", "pan2", choice);
	}
	
	@Override
	public void reactToResize() {
		selected.resize(WIDTH_SELECT_FSMS_PANEL, this.getHeight());
		choice.resize(WIDTH_SELECT_FSMS_PANEL, this.getHeight());
		redraw();
	}
	
	public void redraw() {
		selected.draw();
		choice.draw();
	}
	
	public EntrySet getContext() {
		return context;
	}

}
