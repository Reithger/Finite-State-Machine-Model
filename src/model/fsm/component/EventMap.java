package model.fsm.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * This class is a wrapper for a HashMap allowing the user to search for an event object using the corresponding name.
 * It also holds nice functions for working with events.
 * 
 * This class is a part of the support package.
 * 
 * @author Ada Clevinger and Graeme Zinck
 */

public class EventMap {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** HashMap<<r>String, Entity> mapping String object names of events to their corresponding Entity objects. */
	private HashMap<String, Entity> events;
	
	private ArrayList<String> attributes;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * Constructor for an EventMap object that initializes the events HashMap<<r>String, Entity>.
	 */
	
	public EventMap(ArrayList<String> defAttrib) {
		events = new HashMap<String, Entity>();
		attributes = defAttrib == null ? new ArrayList<String>() : defAttrib;
	}

//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * Renames the Entity corresponding to the oldName String with the newName String.
	 * 
	 * @param oldName - String object representing the name of the Entity.
	 * @param newName - String object representing the desired new name of the Entity.
	 */
	
	public void renameEvent(String oldName, String newName) {
		Entity event = events.get(oldName);
		event.setName(newName);
		events.remove(oldName);
		events.put(newName, event);
	}

	public void mergeEvents(EventMap in) {
		events.putAll(in.getEvents());
	}
	
//---  Adder Methods   ------------------------------------------------------------------------
	
	/**
	 * Adds an event to the map which is mapped to the name indicated. The new event initializes with
	 * the default settings for the given Entity class. If the event already existed, no new object is
	 * created.
	 * 
	 * @param eventName - String object representing the name of the Entity extending object to add to this EventMap object.
	 * @return - Returns an Entity extending object representing the Entity in three cases: the Entity already existed,
	 * the Entity was newly made, or null if there was an error instantiating the event.
	 */
	
	public void addEvent(String eventName) {
		if(events.get(eventName) != null) {
			return;
		}
		events.put(eventName, new Entity(eventName));
		LinkedList<String> use = new LinkedList<String>();
		use.addAll(attributes);
		events.get(eventName).setAttributes(use);
	}

	public void addEvent(String eventName, EventMap context) {
		events.put(eventName, context.getEvent(eventName).copy());
	}
	
//---  Remover Methods   ----------------------------------------------------------------------

	/**
	 * Removes the Entity corresponding to the provided String from the mapping if present.
	 * 
	 * @param eventName - String object representing the name of the Entity to remove from this Entity Map.
	 */
	
	public void removeEvent(String eventName) {
		events.remove(eventName);
	}

//---  Setter Methods   -----------------------------------------------------------------------

	public void setAttributes(ArrayList<String> attrib) {
		attributes = attrib;
		for(Entity e : events.values()) {
			LinkedList<String> use = new LinkedList<String>();
			use.addAll(attributes);
			e.wipeAttributes();
			e.setAttributes(use);
		}
	}
	
	public void setEventAttribute(String nom, String ref, boolean val) {
		events.get(nom).setAttributeValue(ref, val);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	protected Entity getEvent(String eventName) {
		return events.get(eventName);
	}
	
	public ArrayList<String> getAttributes(){
		return attributes;
	}
	
	public ArrayList<String> getEventNames(){
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(events.keySet());
		return out;
	}

	public ArrayList<String> getEventsWithAttribute(String attrib){
		ArrayList<String> out = new ArrayList<String>();
		for(String s : events.keySet()) {
			if(getEventAttribute(s, attrib)) {
				out.add(s);
			}
		}
		return out;
	}
	
	public Boolean getEventAttribute(String nom, String ref) {
		return events.get(nom).getAttributeValue(ref);
	}
	
	/**
	 * Getter method that returns a boolean value describing whether or not a given Entity extending
	 * object exists within this EventMap as denoted by a provided String object.
	 * 
	 * @param eventName - String object representing the Entity extending object to look for.
	 * @return - Returns a boolean value; true if the Entity extending object exists in the map, false otherwise.
	 */
	
	public boolean eventExists(String eventName) {
		return events.containsKey(eventName);
	}

	protected HashMap<String, Entity> getEvents(){
		return events;
	}

//---  Manipulations   ------------------------------------------------------------------------

	public boolean contains(String in) {
		for(Entity e : this.getEvents().values()) {
			if((e.getName()).equals(in))
				return true;
		}
		return false;
	}
	
}