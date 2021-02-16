package ui.popups;

import java.util.ArrayList;

public class AgentRep {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private String nom;
	private ArrayList<EventRep> events;
	
	private static String SEPARATOR;
	private static String SYMBOL_TRUE;
	private static String SYMBOL_FALSE;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public AgentRep(String inNom, ArrayList<String> eves, int attr) {
		nom = inNom;
		events = new ArrayList<EventRep>();
		for(String s : eves) {
			events.add(new EventRep(s, attr));
		}
	}
	
	public AgentRep(String readIn) {
		String[] lines = readIn.split("\n");
		nom = lines[0];
		events = new ArrayList<EventRep>();
		for(int i = 1; i < lines.length; i++) {
			String[] line = lines[i].split(SEPARATOR);
			EventRep e = new EventRep(line[0], line.length - 1);
			for(int j = 1; j < line.length; j++) {
				if(line[j].equals(SYMBOL_TRUE)) {
					e.toggle(j - 1);
				}
			}
			events.add(e);
		}
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public static void assignSymbols(String sep, String tr, String fa) {
		SEPARATOR = sep;
		SYMBOL_TRUE = tr;
		SYMBOL_FALSE = fa;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getName() {
		return nom;
	}
	
	public EventRep getEvent(int i) {
		return events.get(i);
	}
	
	public ArrayList<EventRep> getEvents(){
		return events;
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(nom + "\n");
		for(EventRep e : events) {
			sb.append(e.getName());
			for(Boolean b : e.getValues()) {
				sb.append(SEPARATOR + (b ? SYMBOL_TRUE : SYMBOL_FALSE));
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
}