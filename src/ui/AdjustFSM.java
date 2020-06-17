package ui;

public class AdjustFSM extends OptionPage{
	
	private final static String HEADER = "Head";
	private final static String[] CATEGORIES = new String[] {"Category 1", "Category 2"};
	private final static String[][] LABELS = new String[][] {
		{"Option 1", "Option 2", "Option 3"},
		{"Option 1", "Option 2", "Option 3"},
	};
	private final static String[][] TYPES = new String[][] {
		{"S", "D", "T",},
		{"T", "D", "T",},
	};
	private final static int CODE_BASE = 50;

	public AdjustFSM(int x, int y, int width, int height) {
		super(HEADER, CATEGORIES, LABELS, TYPES);
		this.assignCodeRange(CODE_BASE);
	}

}
