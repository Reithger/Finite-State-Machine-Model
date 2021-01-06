package model;

import java.util.ArrayList;
import java.util.HashMap;

import model.convert.GenerateDot;
import model.convert.GenerateFSM;
import model.convert.ReadWrite;
import model.fsm.TransitionSystem;
import model.process.ProcessAnalysis;
import model.process.ProcessClean;
import model.process.ProcessOperation;

public class Manager {

//---  Instance Variables   -------------------------------------------------------------------
	
	private HashMap<String, TransitionSystem> fsms;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Manager() {
		fsms = new HashMap<String, TransitionSystem>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	//-- File Meta  -------------------------------------------
	
	public String generateFSMDot(String ref) {
		return GenerateDot.generateDot(fsms.get(ref));
	}
	
	public void readInFSM(String fileContents) {
		TransitionSystem in = ReadWrite.readFile(fileContents);
		fsms.put(in.getId(), in);
	}
	
	public String exportFSM(String ref) {
		return ReadWrite.generateFile(fsms.get(ref));
	}
	
	//-- FSM Generation  --------------------------------------
	
	//TODO: Currently returns file path, should just return contents
	
	public String generateRandomFSM(String nom, int numStates, int numEvents, int numTrans, boolean det) {
		return GenerateFSM.createNewFSM(numStates, 0, numEvents, numTrans, 0, 0, 0, 0, 0, det, nom, "");
	}
	
	//-- Processes  -------------------------------------------
	
	public void performProduct(ArrayList<String> roots) {
		TransitionSystem in = fsms.get(roots.get(0));
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		for(int i = 1; i < roots.size(); i++) {
			use.add(fsms.get(roots.get(i)));
		}
		TransitionSystem out = ProcessOperation.product(in, use);
		fsms.put(out.getId(), out);
	}
	
	public void performParallelComposition(ArrayList<String> roots) {
		TransitionSystem in = fsms.get(roots.get(0));
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		for(int i = 1; i < roots.size(); i++) {
			use.add(fsms.get(roots.get(i)));
		}
		TransitionSystem out = ProcessOperation.parallelComposition(in, use);
		fsms.put(out.getId(), out);
	}
	
	public void buildObserver(String ref) {
		TransitionSystem out = ProcessOperation.buildObserver(fsms.get(ref));
		fsms.put(out.getId(), out);
	}
	
	//-- Clean  -----------------------------------------------
	
	public void trim(String ref) {
		TransitionSystem out = ProcessClean.trim(fsms.get(ref));
		fsms.put(out.getId(), out);
	}
	
	public void makeAccessible(String ref) {
		TransitionSystem out = ProcessClean.makeAccessible(fsms.get(ref));
		fsms.put(out.getId(), out);
	}
	
	public void makeCoAccessible(String ref) {
		TransitionSystem out = ProcessClean.makeCoAccessible(fsms.get(ref));
		fsms.put(out.getId(), out);
	}
	
	//-- Analysis  --------------------------------------------
	
	public Boolean isBlocking(String ref) {
		if(fsms.get(ref) == null) {
			return null;
		}
		return ProcessAnalysis.isBlocking(fsms.get(ref));
	}
	
	public Boolean testOpacity(String ref) {
		if(fsms.get(ref) == null) {
			return null;
		}
		return ProcessAnalysis.testOpacity(fsms.get(ref));
	}
	
	public ArrayList<String> findPrivateStates(String ref){
		return ProcessAnalysis.findPrivateStates(fsms.get(ref));
	}
	
	//-- Manipulate  ------------------------------------------
	
		//-- FSM  ---------------------------------------------
	
	public void addFSM(String id, ArrayList<String> stateAttr, ArrayList<String> eventAttr, ArrayList<String> tranAttr) {
		fsms.put(id, new TransitionSystem(id, stateAttr, eventAttr, tranAttr));
	}
	
	public void removeFSM(String id) {
		fsms.remove(id);
	}
	
	public void assignStateAttributes(String ref, ArrayList<String> stateAttr) {
		fsms.get(ref).setStateAttributes(stateAttr);
	}
	
	public void assignEventAttributes(String ref, ArrayList<String> eventAttr) {
		fsms.get(ref).setEventAttributes(eventAttr);
	}
	
	public void assignTransitionAttributes(String ref, ArrayList<String> tranAttr) {
		fsms.get(ref).setTransitionAttributes(tranAttr);
	}
	
		//-- State  -------------------------------------------
	
	public void addState(String ref, String stateName) {
		fsms.get(ref).addState(stateName);
	}
	
	public void removeState(String ref, String stateName) {
		fsms.get(ref).removeState(stateName);
	}
	
	public void setStateAttribute(String ref, String stateName, String attrib, boolean inValue) {
		fsms.get(ref).setStateAttribute(stateName, attrib, inValue);
	}
		
		//-- Event  -------------------------------------------
	
	public void addEvent(String ref, String eventName) {
		fsms.get(ref).addEvent(eventName);
	}
	
	public void removeEvent(String ref, String eventName) {
		fsms.get(ref).removeEvent(eventName);
	}
	
	public void setEventAttribute(String ref, String eventName, String attrib, boolean inValue) {
		fsms.get(ref).setEventAttribute(eventName, attrib, inValue);
	}

		//-- Transition  --------------------------------------
	
	public void addTransition(String ref, String star, String even, String targ) {
		fsms.get(ref).addTransition(star, even, targ);
	}
	
	public void removeTransition(String ref, String star, String even, String targ) {
		fsms.get(ref).removeTransition(star, even, targ);
	}
	
	public void setTransitionAttribute(String ref, String star, String even, String attrib, boolean inValue) {
		fsms.get(ref).setTransitionAttribute(star, even, attrib, inValue);
	}
	
}
