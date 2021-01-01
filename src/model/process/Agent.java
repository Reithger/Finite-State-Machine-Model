package model.process;

import java.util.ArrayList;

import model.AttributeList;
import model.fsm.component.EventMap;

public class Agent {

//---  Constants   ----------------------------------------------------------------------------
	
	public final static String ATTRIBUTE_OBSERVABLE = AttributeList.ATTRIBUTE_OBSERVABLE;
	public final static String ATTRIBUTE_CONTROLLABLE = AttributeList.ATTRIBUTE_CONTROLLABLE;
	
	private final static String[] ATTRIBUTES = new String[] {ATTRIBUTE_OBSERVABLE, ATTRIBUTE_CONTROLLABLE};
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private EventMap events;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Agent(ArrayList<String> inEvents) {
		ArrayList<String> attrib = new ArrayList<String>();
		for(String s : ATTRIBUTES) {
			attrib.add(s);
		}
		events = new EventMap(attrib);
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