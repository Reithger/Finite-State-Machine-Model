package ui.page.optionpage.entryset;

public class EntrySetFactory {

	public final static String SIGNIFIER_TRUE = "t";
	
	public static EntrySet generateEntryText(String pref, String label, boolean button, int code, int size, boolean flex) {
		return new EntryText(pref, label, button, code, size, flex);
	}
	
	public static EntrySet generateEntryTextDisplay(String pref, String label, boolean button, int code) {
		return new EntryTextDisplay(pref, label, button, code);
	}
	
	public static EntrySet generateEntryList(String pref, String label, boolean button, int code, int newCode) {
		return new EntryList(pref, label, button, code, newCode);
	}
	
	public static EntrySet generateEntryCheckbox(String pref, String label, boolean button, int code) {
		return new EntryCheckbox(pref, label, button, code);
	}
	
	public static EntrySet generateEntryEmpty(String pref, String label, boolean button, int code) {
		return new EntryEmpty(pref, label, button, code);
	}
	
}
