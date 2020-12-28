package ui.page.headers;

import input.CustomEventReceiver;
import visual.composite.HandlePanel;

public abstract class HeaderBase extends HandlePanel{
	
	private final static int ROTATION_MULTIPLIER = 10;

	public HeaderBase(int x, int y, int wid, int hei) {
		super(x, y, wid, hei);
		this.setEventReceiver(new CustomEventReceiver() {
			@Override
			public void mouseWheelEvent(int rotation) {
				if(getMaximumScreenX() < getWidth()) {
					return;
				}
				setOffsetXBounded(getOffsetX() + rotation * ROTATION_MULTIPLIER);
			}
			
			@Override
			public void clickEvent(int code, int x, int y, int mouseType) {
				clickBehaviour(code, x, y);
			}
		});
	}
	
	public abstract void clickBehaviour(int code, int x, int y);
	
	@Override
	public int getMinimumScreenX() {
		return 0;
	}
	
}
