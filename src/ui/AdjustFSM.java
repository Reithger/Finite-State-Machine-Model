package ui;

import java.util.Arrays;

public class AdjustFSM extends OptionPage{
	
//---  Constant Values   ----------------------------------------------------------------------
	
	private final static String HEADER = "Head";
	private final static String[] CATEGORIES = new String[] {"Category 1", "Category 2"};
	private final static String[][] LABELS = new String[][] {
		{"Option 1a", "Option 2a", "Option 3a"},
		{"Option 1b", "Option 2b", "Option 3b"},
	};
	private final static String[][] TYPES = new String[][] {
		{ENTRY_TEXT_SINGLE, ENTRY_TEXT_DOUBLE, ENTRY_TEXT_TRIPLE,},
		{ENTRY_TEXT_TRIPLE, ENTRY_TEXT_DOUBLE, ENTRY_TEXT_QUARTET,},
	};
	/** Make sure codes are high values to give buffer for background behaviors*/
	private final static int[][] CODES = new int[][] {
		{100, 101, 102},
		{103, 104, 105},
	};
	
//---  Constructors   -------------------------------------------------------------------------

	public AdjustFSM(int x, int y, int width, int height) {
		super(HEADER, CATEGORIES, LABELS, TYPES, CODES);
	}
	
//---  Operations   ---------------------------------------------------------------------------

	@Override
	public void applyCode(int code) {
		if(!toggleCategory(code)) {
			
		}
		System.out.println(Arrays.toString(this.getTextEntry("Option 2")));
		drawPage();
	}

}
