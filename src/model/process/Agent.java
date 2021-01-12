package model.process;

import java.util.ArrayList;

import model.fsm.component.EventMap;

public class Agent {

//---  Constants   ----------------------------------------------------------------------------
	
	public static String attributeObservableRef;
	public static String attributeControllableRef;
	
	private static String[] attributeList;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private EventMap events;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Agent(ArrayList<String> inEvents) {
		ArrayList<String> attrib = new ArrayList<String>();
		for(String s : attributeList) {
			attrib.add(s);
		}
		events = new EventMap(attrib);
		for(String e : inEvents)
			events.addEvent(e);
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static void assignAttributeReferences(String obs, String cont) {
		attributeObservableRef = obs;
		attributeControllableRef = cont;
		attributeList = new String[] {attributeObservableRef, attributeControllableRef};
	}
	
	public void addUnknownEvent(String in) {
		events.addEvent(in);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setAttributeTrue(String attrib, ArrayList<String> names) {
		for(String s : names) {
			events.setEventAttribute(s, attrib, true);
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	public Boolean getEventAttribute(String event, String attrib) {
		return events.getEventAttribute(event, attrib);
	}

	public ArrayList<String> getEvents(){
		return events.getEventNames();
	}

	public ArrayList<String> getEventsAttributeSet(String attrib, boolean val){
		ArrayList<String> out = new ArrayList<String>();
		for(String s : events.getEventNames()) {
			Boolean b = events.getEventAttribute(s, attrib);
			if(b != null && b == val) {
				out.add(s);
			}
		}
		return out;
	}

	public boolean contains(String eventName) {
		return events.contains(eventName);
	}
	
}