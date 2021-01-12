package model;

import java.util.ArrayList;
import java.util.HashMap;

import model.convert.GenerateDot;
import model.convert.GenerateFSM;
import model.convert.ReadWrite;
import model.fsm.TransitionSystem;
import model.process.ProcessDES;

public class Manager {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static String SEPARATOR = ";,;;,;";
	private final static String REGION_SEPARATOR = "---";
	private final static String TRUE_SYMBOL = "o";
	private final static String FALSE_SYMBOL = "x";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private HashMap<String, TransitionSystem> fsms;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Manager() {
		fsms = new HashMap<String, TransitionSystem>();
		ReadWrite.assignConstants(SEPARATOR, REGION_SEPARATOR, TRUE_SYMBOL, FALSE_SYMBOL);
		GenerateFSM.assignConstants(SEPARATOR, REGION_SEPARATOR, TRUE_SYMBOL, FALSE_SYMBOL);
		assignAttributeReferences();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	//-- Assign Attribute Data  -------------------------------
	
	private void assignAttributeReferences() {
		ProcessDES.assignAttributeReferences(AttributeList.ATTRIBUTE_INITIAL, AttributeList.ATTRIBUTE_MARKED, AttributeList.ATTRIBUTE_PRIVATE, AttributeList.ATTRIBUTE_OBSERVABLE);
	}
	
	//-- File Meta  -------------------------------------------
	
	public String generateFSMDot(String ref) {
		return GenerateDot.generateDot(fsms.get(ref));
	}
	
	public String readInFSM(String fileContents) {
		TransitionSystem in = ReadWrite.readFile(fileContents);
		fsms.put(in.getId(), in);
		return in.getId();
	}
	
	public String exportFSM(String ref) {
		return ReadWrite.generateFile(fsms.get(ref));
	}
	
	public boolean hasFSM(String ref) {
		return fsms.containsKey(ref);
	}
	
	public void renameFSM(String old, String newFSM) {
		if(fsms.get(old) != null) {
			TransitionSystem oldFS = fsms.get(old).copy();
			oldFS.setId(newFSM);
			fsms.remove(old);
			fsms.put(newFSM, oldFS);
		}
	}
	
	public String duplicate(String fsm) {
		if(fsms.get(fsm) != null) {
			TransitionSystem out = fsms.get(fsm).copy();
			out.setId(out.getId() + "_copy");
			fsms.put(out.getId(), out);
			return out.getId();
		}
		return null;
	}
	
	//-- FSM Generation  --------------------------------------
	
	public String generateRandomFSM(String nom, int numStates, int numEvents, int numTrans, boolean det, ArrayList<String> stateAttr, ArrayList<String> eventAttr, ArrayList<String> transAttr, ArrayList<Integer> numbers) {
		return GenerateFSM.createNewFSM(nom, numStates, numEvents, numTrans, det, stateAttr, eventAttr, transAttr, numbers);
	}
	
	//-- Processes  -------------------------------------------
	
	public String performProduct(ArrayList<String> roots) {
		TransitionSystem in = fsms.get(roots.get(0));
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		for(int i = 1; i < roots.size(); i++) {
			use.add(fsms.get(roots.get(i)));
		}
		TransitionSystem out = ProcessDES.product(in, use);
		fsms.put(out.getId(), out);
		return out.getId();
	}
	
	public String performParallelComposition(ArrayList<String> roots) {
		TransitionSystem in = fsms.get(roots.get(0));
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		for(int i = 1; i < roots.size(); i++) {
			use.add(fsms.get(roots.get(i)));
		}
		TransitionSystem out = ProcessDES.parallelComposition(in, use);
		fsms.put(out.getId(), out);
		return out.getId();
	}
	
	public String buildObserver(String ref) {
		TransitionSystem out = ProcessDES.buildObserver(fsms.get(ref));
		fsms.put(out.getId(), out);
		return out.getId();
	}
	
	//-- Clean  -----------------------------------------------
	
	public String trim(String ref) {
		TransitionSystem out = ProcessDES.trim(fsms.get(ref));
		fsms.put(out.getId(), out);
		return out.getId();
	}
	
	public String makeAccessible(String ref) {
		TransitionSystem out = ProcessDES.makeAccessible(fsms.get(ref));
		fsms.put(out.getId(), out);
		return out.getId();
	}
	
	public String makeCoAccessible(String ref) {
		TransitionSystem out = ProcessDES.makeCoAccessible(fsms.get(ref));
		fsms.put(out.getId(), out);
		return out.getId();
	}
	
	//-- Analysis  --------------------------------------------
	
	public Boolean stateExists(String ref, String nom) {
		if(fsms.get(ref) == null) {
			return null;
		}
		return fsms.get(ref).stateExists(nom);
	}
	
	public Boolean isBlocking(String ref) {
		if(fsms.get(ref) == null) {
			return null;
		}
		return ProcessDES.isBlocking(fsms.get(ref));
	}
	
	public Boolean testOpacity(String ref) {
		if(fsms.get(ref) == null) {
			return null;
		}
		return ProcessDES.testOpacity(fsms.get(ref));
	}
	
	public ArrayList<String> findPrivateStates(String ref){
		return ProcessDES.findPrivateStates(fsms.get(ref));
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
	
	public void addStates(String ref, int num) {
		String alph = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int used = 0;
		int curr = 0;
		while(used < num) {
			String nom = "";
			int cop = curr;
			do {
				nom += alph.charAt(cop % alph.length());
				cop /= alph.length();
			}while(cop != 0);
			if(!stateExists(ref, nom)) {
				addState(ref, nom);
				used++;
			}
			curr++;
		}
	}
	
	public void removeState(String ref, String stateName) {
		fsms.get(ref).removeState(stateName);
	}
	
	public void renameState(String ref, String old, String newNom) {
		if(fsms.get(ref) != null) {
			fsms.get(ref).renameState(old, newNom);
		}
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
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String[] getStateAttributeList() {
		return AttributeList.STATE_ATTRIBUTES;
	}
	
	public String[] getEventAttributeList() {
		return AttributeList.EVENT_ATTRIBUTES;
	}
	
	public String[] getTransitionAttributeList() {
		return AttributeList.TRANSITION_ATTRIBUTES;
	}
	
	public ArrayList<String> getReferences(){
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(fsms.keySet());
		return out;
	}
	
}
