package model.process.coobservability;

import java.util.ArrayList;
import java.util.Collection;

import model.fsm.component.EventMap;

public class Agent {

//---  Instance Variables   -------------------------------------------------------------------
	
	/** EventMap object to track how this Agent considers the controllability/visibility of a plant's events*/
	private EventMap events;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * 
	 * Constructed with a list of Attributes to consider for each of the also provided list
	 * of Events. Assignment of attribute condition for each event occurs through other functions.
	 * 
	 * @param attr
	 * @param inEvents
	 */
	
	public Agent(ArrayList<String> attr, ArrayList<String> inEvents) {
		events = new EventMap(attr);
		if(inEvents != null) {
			for(String e : inEvents)
				events.addEvent(e);
		}
	}
	
	public Agent(EventMap in) {
		events = in;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * 
	 * Adds an additional event to this Agent's EventMap without any attribute info.
	 * 
	 * @param in
	 */
	
	public void addUnknownEvent(String in) {
		if(!events.contains(in)) {
			events.addEvent(in);
		}
	}
	
	public void addUnknownEvents(Collection<String> in) {
		for(String s : in) {
			addUnknownEvent(s);
		}
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * 
	 * Function to set a particular attribute value as true for the entire list of events as
	 * denoted by an ArrayList<<>String>.
	 * 
	 * @param attrib
	 * @param names
	 */
	
	public void setAttributeTrue(String attrib, ArrayList<String> names) {
		for(String s : names) {
			events.setEventAttribute(s, attrib, true);
		}
	}
	
	/**
	 * 
	 * Function to set a specific boolean value for the attribute status of a particular
	 * event.
	 * 
	 * @param attrib - Attribute to assign the value of
	 * @param name - Name of the event that's attribute value will change
	 * @param set - New boolean value to assign to the attribute of the designated event
	 */
	
	public void setAttribute(String attrib, String name, boolean set) {
		events.setEventAttribute(name, attrib, set);
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