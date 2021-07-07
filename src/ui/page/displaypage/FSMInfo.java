package ui.page.displaypage;

import java.util.ArrayList;
import java.util.HashMap;

public class FSMInfo {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private String fsmName;
	
	private ArrayList<String> fsmStates;
	
	private ArrayList<String> fsmStateAttributes;
	
	private HashMap<String, ArrayList<Boolean>> fsmStateDetails;
	
	private ArrayList<String> fsmEvents;

	private ArrayList<String> fsmEventAttributes;

	private HashMap<String, ArrayList<Boolean>> fsmEventDetails;
	
	private ArrayList<String> fsmTransitions;

	private ArrayList<String> fsmTransitionAttributes;

	private HashMap<String, ArrayList<Boolean>> fsmTransitionDetails;
	
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FSMInfo(String name, ArrayList<String> statAttr, ArrayList<String> evenAttr, ArrayList<String> tranAttr) {
		fsmName = name;
		fsmStateAttributes = statAttr;
		fsmEventAttributes = evenAttr;
		fsmTransitionAttributes = tranAttr;
		fsmStates = new ArrayList<String>();
		fsmEvents = new ArrayList<String>();
		fsmTransitions = new ArrayList<String>();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void updateStates(ArrayList<String> stat) {
		fsmStates = stat;
	}
	
	public void updateStateAttributes(ArrayList<String> statAttr) {
		fsmStateAttributes = statAttr;
	}
	
	public void updateStateDetails(HashMap<String, ArrayList<Boolean>> statDeta){
		fsmStateDetails = statDeta;
	}
	
	public void updateEvents(ArrayList<String> even) {
		fsmEvents = even;
	}

	public void updateEventAttributes(ArrayList<String> evenAttr) {
		fsmEventAttributes = evenAttr;
	}
	
	public void updateEventDetails(HashMap<String, ArrayList<Boolean>> evenDeta){
		fsmEventDetails = evenDeta;
	}
	
	public void updateTransitions(ArrayList<String> trans) {
		fsmTransitions = trans;
	}

	public void updateTransitionAttributes(ArrayList<String> transAttr) {
		fsmTransitionAttributes = transAttr;
	}
	
	public void updateTransitionDetails(HashMap<String, ArrayList<Boolean>> transDeta){
		fsmTransitionDetails = transDeta;
	}

//---  Getter Methods   -----------------------------------------------------------------------

	public ArrayList<String> getStates(){
		return fsmStates;
	}

	public ArrayList<String> getEvents(){
		return fsmEvents;
	}
	
	public ArrayList<String> getTransitions(){
		return fsmTransitions;
	}
	
	public ArrayList<String> getStateAttributes(){
		return fsmStateAttributes;
	}
	
	public ArrayList<String> getEventAttributes(){
		return fsmEventAttributes;
	}
	
	public ArrayList<String> getTransitionAttributes(){
		return fsmTransitionAttributes;
	}
	
	public HashMap<String, ArrayList<Boolean>> getStateDetails(){
		return fsmStateDetails;
	}
	
	public HashMap<String, ArrayList<Boolean>> getEventDetails(){
		return fsmEventDetails;
	}
	
	public HashMap<String, ArrayList<Boolean>> getTransitionDetails(){
		return fsmTransitionDetails;
	}
	
	public String getName() {
		return fsmName;
	}

}
