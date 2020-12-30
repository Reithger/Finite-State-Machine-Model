package model;

import java.util.HashMap;

import model.fsm.TransitionSystem;

public class Manager {

	private HashMap<String, TransitionSystem> fsms;
	
	public Manager() {
		fsms = new HashMap<String, TransitionSystem>();
	}
	
	public String generateFSMDot(String ref) {
		return GenerateDot.generateDot(fsms.get(ref));
	}
	
}
