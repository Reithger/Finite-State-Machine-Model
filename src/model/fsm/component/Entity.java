package model.fsm.component;

import java.util.LinkedList;

/**
 * This class models an Event in an FSM, storing information about the Event's name and
 * its status as Observable, Controllable, and whatever other features may be implemented
 * in the future.
 * 
 * This class is a part of the support.event package
 * 
 * @author Ada Clevinger and Graeme Zinck
 */

public class Entity {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** String instance variable object representing the name of the Event*/
	private String id;
	
	private Attribute wrap;
	
//---  Constructors   -------------------------------------------------------------------------

	/**
	 * Constructor for an Event object that assigns a defined String object to be its name,
	 * defaulting its status as Controllability and Observability to be true.
	 * 
	 * @param eventName - String object that represents the name of the Event object.
	 */
	
	public Entity(String name) {
		id = name;
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public Entity copy() {
		Entity out = new Entity(getName());
		out.setAttributes(getAttributes());
		for(String s : getAttributes()) {
			out.setAttributeValue(s, getAttributeValue(s));
		}
		return out;
	}
	
	public void wipeAttributes() {
		wrap = null;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * Setter method that assigns the provided String value to be the new name of this Event object
	 * 
	 * @param in - String object provided as the new value for this Event object's name
	 */
	
	public void setName(String in) {
		id = in;
	}
	
	public void setAttributeValue(String ref, boolean val) {
		if(wrap == null) {
			wrap = new Attribute(ref);
		}
		wrap.setValue(ref, val);
	}

	public void setAttributes(LinkedList<String> refs) {
		if(refs != null && refs.size() != 0) {
			wrap = new Attribute(refs.poll());
			wrap.setAttributes(refs);
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	/**
	 * Getter method that requests the current name of the Event object
	 * 
	 * @return - Returns a String object representing the name of this Event object
	 */
	
	public String getName() {
		return id;
	}
	
	public Boolean getAttributeValue(String ref) {
		if(wrap == null) {
			return null;
		}
		return wrap.getValue(ref);
	}

	public LinkedList<String> getAttributes(){
		return wrap == null ? new LinkedList<String>() : wrap.getAttributes();
	}
	
//---  Miscellaneous   ------------------------------------------------------------------------
	
	public boolean equals(Object other) {
		if(other == null)
			return false;
		try {
			Entity ot = (Entity)other;
			boolean result = getName().equals(ot.getName());
			result = result && (getAttributes().equals(ot.getAttributes()));
			for(String s : getAttributes()) {
				result = result && (getAttributeValue(s) == ot.getAttributeValue(s));
			}
			return result;
		}
		catch(Exception e) {
			return false;
		}
	}

}