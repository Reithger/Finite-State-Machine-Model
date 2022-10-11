package model.process.coobservability.support;

import java.util.ArrayList;
import java.util.Comparator;

public class StateSetPath extends StateSet implements Comparator<StateSetPath>, Comparable<StateSetPath>{

	private StateSetPath parent;
	
	private ArrayList<String> eventPath;
	
	private String problemEvent;
	
	public StateSetPath(String[] in, StateSetPath inParent) {
		super(in);
		parent = inParent;
		if(!parent.search(new StateSet(in))) {
			eventPath = new ArrayList<String>();
			for(String s : parent.getEventPath()) {
				eventPath.add(s);
			}
		}
	}
	
	public void setProblemEvent(String in) {
		problemEvent = in;
	}
	
	protected boolean search(StateSet check) {
		return getPairName().equals(check.getPairName()) ? true : parent == null ? false : parent.search(check);
	}
	
	public boolean isNew() {
		return eventPath != null;
	}
	
	public StateSetPath(String[] in) {
		super(in);
		eventPath = new ArrayList<String>();
		parent = null;
	}

	public void addEvent(String s) {
		eventPath.add(s);
	}
	
	
	public ArrayList<String> getEventPath(){
		ArrayList<String> out = new ArrayList<String>();
		for(String s : eventPath == null ? new ArrayList<String>() : eventPath) {
			out.add(s);
		}
		return out;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return toString().equals(other.toString());
	}
	
	@Override
	public String toString() {
		return eventPath == null ? parent.toString() : (super.toString() + ", " + eventPath);
	}

	@Override
	public int compareTo(StateSetPath o) {
		return toString().compareTo(o.toString());
	}

	@Override
	public int compare(StateSetPath o1, StateSetPath o2) {
		return o1.compareTo(o2);
	}
	
}
