package model.process.coobservability.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class CrushIdentityGroup implements Comparator<CrushIdentityGroup>, Comparable<CrushIdentityGroup> {

	private CrushIdentityGroup parentGroup;
	
	private String event;
	
	private HashSet<String> thisGroup;
	
	public CrushIdentityGroup(CrushIdentityGroup parent, String ev, HashSet<String> group) {
		parentGroup = parent;
		event = ev == null ? "" : ev;
		thisGroup = parent != null && parent.search(group) ? null : group;
	}
	
	public HashSet<String> getGroup(){
		return thisGroup;
	}
	
	public int getSize() {
		return thisGroup.size();
	}
	
	private boolean search(HashSet<String> check) {
		return thisGroup.equals(check) ? true : parentGroup != null ? parentGroup.search(check) : false;
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	@Override
	public String toString() {
		if(thisGroup == null) {
			return parentGroup.toString();
		}
		ArrayList<String> b = new ArrayList<String>();
		b.addAll(thisGroup);
		Collections.sort(b);
		return (parentGroup == null ? "Null" : parentGroup.toString()) + "\n by " + event + " to \n" + b.toString();
	}

	@Override
	public int compare(CrushIdentityGroup o1, CrushIdentityGroup o2) {
		return o1.toString().compareTo(o2.toString());
	}

	@Override
	public int compareTo(CrushIdentityGroup o) {
		return this.toString().compareTo(o.toString());
	}
	
	@Override
	public boolean equals(Object o) {
		return this.toString().equals(o.toString());
	}
	
}
