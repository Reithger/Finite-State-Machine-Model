package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
		ProcessDES.assignAttributeReferences(AttributeList.ATTRIBUTE_INITIAL, AttributeList.ATTRIBUTE_MARKED, AttributeList.ATTRIBUTE_PRIVATE, AttributeList.ATTRIBUTE_OBSERVABLE, AttributeList.ATTRIBUTE_CONTROLLABLE);
	}
	
	//-- File Meta  -------------------------------------------
	
	public String generateFSMDot(String ref) {
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		return GenerateDot.generateDot(fsms.get(ref));
	}
	
	public String readInFSM(String fileContents) {
		TransitionSystem in = ReadWrite.readFile(fileContents);
		appendFSM(in.getId(), in, false);
		return in.getId();
	}
	
	public String exportFSM(String ref) {
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		return ReadWrite.generateFile(fsms.get(ref));
	}
	
	public boolean hasFSM(String ref) {
		return fsms.containsKey(ref);
	}

	public String duplicate(String fsm) {
		if(fsm == null || fsms.get(fsm) != null) {
			TransitionSystem out = fsms.get(fsm).copy();
			out.setId(out.getId() + "_copy");
			appendFSM(out.getId(), out, false);
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
		if(roots.size() == 0 || roots.get(0) == null || fsms.get(roots.get(0)) == null || roots.size() < 2) {
			return null;
		}
		TransitionSystem in = fsms.get(roots.get(0));
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		for(int i = 1; i < roots.size(); i++) {
			use.add(fsms.get(roots.get(i)));
		}
		TransitionSystem out = ProcessDES.product(in, use);
		if(out == null) {
			return null;
		}
		appendFSM(out.getId(), out, false);
		return out.getId();
	}
	
	public String performParallelComposition(ArrayList<String> roots) {
		if(roots.get(0) == null || fsms.get(roots.get(0)) == null || roots.size() < 2) {
			return null;
		}
		TransitionSystem in = fsms.get(roots.get(0));
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		for(int i = 1; i < roots.size(); i++) {
			use.add(fsms.get(roots.get(i)));
		}
		TransitionSystem out = ProcessDES.parallelComposition(in, use);
		if(out == null) {
			return null;
		}
		appendFSM(out.getId(), out, false);
		return out.getId();
	}
	
	public String buildObserver(String ref) {
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		TransitionSystem out = ProcessDES.buildObserver(fsms.get(ref));
		if(out == null) {
			return null;
		}
		appendFSM(out.getId(), out, false);
		return out.getId();
	}
	
	//-- Clean  -----------------------------------------------
	
	public String trim(String ref) {
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		TransitionSystem out = ProcessDES.trim(fsms.get(ref));
		if(out == null) {
			return null;
		}
		appendFSM(out.getId(), out, false);
		return out.getId();
	}
	
	public String makeAccessible(String ref) {
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		TransitionSystem out = ProcessDES.makeAccessible(fsms.get(ref));
		if(out == null) {
			return null;
		}
		appendFSM(out.getId(), out, false);
		return out.getId();
	}
	
	public String makeCoAccessible(String ref) {
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		TransitionSystem out = ProcessDES.makeCoAccessible(fsms.get(ref));
		if(out == null) {
			return null;
		}
		appendFSM(out.getId(), out, false);
		return out.getId();
	}
	
	//-- Analysis  --------------------------------------------
	
	public Boolean stateExists(String ref, String nom) {
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		return fsms.get(ref).stateExists(nom);
	}
	
	public Boolean eventExists(String ref, String nom) {
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		return fsms.get(ref).eventExists(nom);
	}
	
	public Boolean isBlocking(String ref) {
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		return ProcessDES.isBlocking(fsms.get(ref));
	}
	
	public Boolean testOpacity(String ref) {
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		return ProcessDES.testOpacity(fsms.get(ref));
	}
	
	public ArrayList<String> findPrivateStates(String ref){
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		return ProcessDES.findPrivateStates(fsms.get(ref));
	}
	
	//-- U-Structure  -----------------------------------------
	
	public String buildUStructure(String ref, ArrayList<String> attr, HashMap<String, HashSet<String>> badTrans, boolean[][][] agents) {
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		//TODO: Other failure checks to do ahead of time?
		TransitionSystem tS = ProcessDES.buildUStructure(fsms.get(ref), attr, badTrans, agents);
		if(tS == null) {
			return null;
		}
		appendFSM(tS.getId(), tS, false);
		return tS.getId();
	}
	
	//-- Manipulate  ------------------------------------------
	
		//-- FSM  ---------------------------------------------
	
	public void addFSM(String id, ArrayList<String> stateAttr, ArrayList<String> eventAttr, ArrayList<String> tranAttr) {
		appendFSM(id, new TransitionSystem(id, stateAttr, eventAttr, tranAttr), false);
	}
	
	public void removeFSM(String id) {
		fsms.remove(id);
	}
	
	public void renameFSM(String old, String newFSM) {
		if(old != null && fsms.get(old) != null) {
			TransitionSystem oldFS = fsms.get(old).copy();
			oldFS.setId(newFSM);
			fsms.remove(old);
			appendFSM(newFSM, oldFS, true);
		}
	}
	
	public void assignStateAttributes(String ref, ArrayList<String> stateAttr) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).setStateAttributes(stateAttr);
	}
	
	public void assignEventAttributes(String ref, ArrayList<String> eventAttr) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).setEventAttributes(eventAttr);
	}
	
	public void assignTransitionAttributes(String ref, ArrayList<String> tranAttr) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).setTransitionAttributes(tranAttr);
	}
	
		//-- State  -------------------------------------------
	
	public void addState(String ref, String stateName) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).addState(stateName);
	}
	
	public void addStates(String ref, int num) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		String alph = "0123456789";
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
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).removeState(stateName);
	}
	
	public void renameState(String ref, String old, String newNom) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		if(fsms.get(ref) != null) {
			fsms.get(ref).renameState(old, newNom);
		}
	}
	
	public void setStateAttribute(String ref, String stateName, String attrib, boolean inValue) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).setStateAttribute(stateName, attrib, inValue);
	}
		
		//-- Event  -------------------------------------------
	
	public void addEvent(String ref, String eventName) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).addEvent(eventName);
	}
	
	public void addEvents(String ref, int num) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		String alph = "abcdefghijklmnopqrstuvwxyz";
		int used = 0;
		int curr = 0;
		while(used < num) {
			String nom = "";
			int cop = curr;
			do {
				nom += alph.charAt(cop % alph.length());
				cop /= alph.length();
			}while(cop != 0);
			if(!eventExists(ref, nom)) {
				addEvent(ref, nom);
				used++;
			}
			curr++;
		}
	}
	
	public void removeEvent(String ref, String eventName) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).removeEvent(eventName);
	}
	
	public void renameEvent(String ref, String old, String newNom) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		if(fsms.get(ref) != null) {
			fsms.get(ref).renameEvent(old, newNom);
		}
	}
	
	public void setEventAttribute(String ref, String eventName, String attrib, boolean inValue) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).setEventAttribute(eventName, attrib, inValue);
	}

		//-- Transition  --------------------------------------
	
	public void addTransition(String ref, String star, String even, String targ) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).addTransition(star, even, targ);
	}
	
	public void removeTransition(String ref, String star, String even, String targ) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).removeTransition(star, even, targ);
	}
	
	public void setTransitionAttribute(String ref, String star, String even, String attrib, boolean inValue) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).setTransitionAttribute(star, even, attrib, inValue);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setFSMStateAttributes(String ref, ArrayList<String> attri) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).setStateAttributes(attri);
	}
	
	public void setFSMEventAttributes(String ref, ArrayList<String> attri) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).setEventAttributes(attri);
	}
	
	public void setFSMTransitionAttributes(String ref, ArrayList<String> attri) {
		if(ref == null || fsms.get(ref) == null) {
			return;
		}
		fsms.get(ref).setTransitionAttributes(attri);
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
	
	public ArrayList<String> getFSMStateList(String ref){
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		return fsms.get(ref).getStateNames();
	}
	
	public ArrayList<String> getFSMEventList(String ref){
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		return fsms.get(ref).getEventNames();
	}
	
	public ArrayList<String> getFSMStateAttributes(String ref){
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		return fsms.get(ref).getStateAttributes();
	}
	
	public ArrayList<String> getFSMEventAttributes(String ref){
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		return fsms.get(ref).getEventAttributes();
	}
	
	public ArrayList<String> getFSMTransitionAttributes(String ref){
		if(ref == null || fsms.get(ref) == null) {
			return null;
		}
		return fsms.get(ref).getTransitionAttributes();
	}
	
	public ArrayList<String> getReferences(){
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(fsms.keySet());
		return out;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private String appendFSM(String nom, TransitionSystem fsm, boolean overwrite) {
		if(!overwrite && fsms.get(nom) != null) {
			int counter = 1;
			while(fsms.get(nom + " (" + counter + ")") != null) {
				counter++;
			}
			nom = nom + " (" + counter + ")";
		}
		fsms.put(nom, fsm);
		fsm.setId(nom);
		return nom;
	}
	
}
