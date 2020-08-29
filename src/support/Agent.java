package support;

import java.util.ArrayList;
import java.util.Collection;

import support.component.Event;
import support.component.map.EventMap;

public class Agent {

	EventMap events;
	
	public Agent(Event ... eventsProvided) {
		events = new EventMap();
		for(Event e : eventsProvided)
			events.addEvent(e);
	}
	
	public void addObservableEvents(String ... names) {
		for(String s : names) {
			if(events.contains(s)) {
				Event e = events.getEvent(s);
				e.setEventObservability(true);
			}
			else {
				Event e = new Event(s);
				e.setEventObservability(true);
				events.addEvent(e);
			}
			
		}
	}
	
	public void addNonObservableEvents(String ... names) {
		for(String s : names) {
			if(events.contains(s)) {
				Event e = events.getEvent(s);
				e.setEventObservability(false);
			}
			else {
				Event e = new Event(s);
				e.setEventObservability(false);
				events.addEvent(e);
			}
			
		}
	}
	
	public void addControllableEvents(String ... names) {
		for(String s : names) {
			if(events.contains(s)) {
				Event e = events.getEvent(s);
				e.setEventControllability(true);
			}
			else {
				Event e = new Event(s);
				e.setEventControllability(true);
				events.addEvent(e);
			}
			
		}
	}
	
	public void addNonControllableEvents(String ... names) {
		for(String s : names) {
			if(events.contains(s)) {
				Event e = events.getEvent(s);
				e.setEventControllability(false);
			}
			else {
				Event e = new Event(s);
				e.setEventControllability(false);
				events.addEvent(e);
			}
			
		}
	}
	
	public boolean getObservable(String eventName) {
		return events.getEvent(eventName).getEventObservability();
	}
	
	public ArrayList<String> getUnobservableEvents(){
		ArrayList<String> out = new ArrayList<String>();
		for(Event e : events.getEvents()) {
			if(!e.getEventObservability()) {
				out.add(e.getEventName());
			}
		}
		return out;
	}
	
	public boolean getControllable(String eventName) {
		return events.getEvent(eventName).getEventControllability();
	}
	
	public Collection<Event> getAgentEvents(){
		return events.getEvents();
	}
	
	public void addNonPresentEvent(String eventName) {
		Event e = events.addEvent(eventName);
		e.setEventControllability(false);
		e.setEventObservability(false);
	}
	
	public boolean contains(String eventName) {
		return events.contains(eventName);
	}
	
}
