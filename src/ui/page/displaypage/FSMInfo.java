package ui.page.displaypage;

import java.util.ArrayList;
import java.util.HashMap;

public class FSMInfo {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private String fsmName;
	
	private String image;
	
	private ArrayList<String> fsmStateAttributes;
	
	private HashMap<String, ArrayList<Boolean>> fsmStateDetails;
	
	private ArrayList<String> fsmEventAttributes;

	private HashMap<String, ArrayList<Boolean>> fsmEventDetails;
	
	private ArrayList<String> fsmTransitionAttributes;

	private HashMap<String, ArrayList<Boolean>> fsmTransitionDetails;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public FSMInfo(String name) {
		fsmName = name;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void updateStateAttributes(ArrayList<String> statAttr) {
		fsmStateAttributes = statAttr;
	}
	
	public void updateStateDetails(HashMap<String, ArrayList<Boolean>> statDeta){
		fsmStateDetails = statDeta;
	}

	public void updateEventAttributes(ArrayList<String> evenAttr) {
		fsmEventAttributes = evenAttr;
	}
	
	public void updateEventDetails(HashMap<String, ArrayList<Boolean>> evenDeta){
		fsmEventDetails = evenDeta;
	}
	
	public void updateTransitionAttributes(ArrayList<String> transAttr) {
		fsmTransitionAttributes = transAttr;
	}
	
	public void updateTransitionDetails(HashMap<String, ArrayList<Boolean>> transDeta){
		fsmTransitionDetails = transDeta;
	}
	
	public void updateImage(String img) {
		image = img;
	}

//---  Getter Methods   -----------------------------------------------------------------------

	public ArrayList<String> getStates(){
		return new ArrayList<String>(fsmStateDetails.keySet());
	}

	public ArrayList<String> getEvents(){
		return new ArrayList<String>(fsmEventDetails.keySet());
	}
	
	public ArrayList<String> getTransitions(){
		return new ArrayList<String>(fsmTransitionDetails.keySet());
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
	
	public String getImage() {
		return image;
	}

}
