package model.fsm.component;

import java.util.LinkedList;

public class Attribute {

//---  Instance Variables   -------------------------------------------------------------------
	
	private String id;
	private boolean value;
	private Attribute wrap;

//---  Constructors   -------------------------------------------------------------------------
	
	public Attribute(String inId) {
		id = inId;
		value = false;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void addWrapper(String ref) {
		if(id.equals(ref)) {
			return;
		}
		if(wrap != null) {
			wrap.addWrapper(ref);
		}
		else {
			wrap = new Attribute(ref);
		}
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setAttributes(LinkedList<String> in) {
		if(in != null && in.size() != 0) {
			wrap = new Attribute(in.poll());
			wrap.setAttributes(in);
		}
	}
	
	public void setValue(String ref, boolean in) {
		if(id.equals(ref)) {
			value = in;
		}
		if(wrap != null) {
			wrap.setValue(ref, in);
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	public Boolean getValue(String ref) {
		if(id.equals(ref)) {
			return value;
		}
		if(wrap != null) {
			return wrap.getValue(ref);
		}
		return null;
	}
	
	public LinkedList<String> getAttributes(){
		LinkedList<String> out = new LinkedList<String>();
		out.add(id);
		if(wrap != null) {
			out.addAll(wrap.getAttributes());
		}
		return out;
	}

}
