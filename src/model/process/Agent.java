package model.process;

import java.util.ArrayList;

import model.fsm.component.EventMap;

public class Agent {

//---  Instance Variables   -------------------------------------------------------------------
	
	private EventMap events;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Agent(ArrayList<String> attr, ArrayList<String> inEvents) {
		events = new EventMap(attr);
		for(String e : inEvents)
			events.addEvent(e);
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void addUnknownEvent(String in) {
		events.addEvent(in);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setAttributeTrue(String attrib, ArrayList<String> names) {
		for(String s : names) {
			events.setEventAttribute(s, attrib, true);
		}
	}
	
	public void setAttributeTrue(String attrib, String name) {
		events.setEventAttribute(name, attrib, true);
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