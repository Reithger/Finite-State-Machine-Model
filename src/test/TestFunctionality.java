package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import controller.FiniteStateMachine;
import controller.convert.FormatConversion;
import model.AttributeList;
import model.Manager;
import model.process.coobservability.Agent;
import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class TestFunctionality {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static String[] EVENT_LIST_A = new String[] {"a1", "a2", "b1", "b2", "c"};
	private final static String[] EVENT_ATTR_LIST = new String[] {AttributeList.ATTRIBUTE_OBSERVABLE, AttributeList.ATTRIBUTE_CONTROLLABLE};
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static Manager model;
	
	private static ArrayList<String> eventAtt;
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static void main(String[] args) {
		FormatConversion.assignPaths(FiniteStateMachine.ADDRESS_IMAGES, FiniteStateMachine.ADDRESS_CONFIG);
		FiniteStateMachine.fileConfiguration();
		model = new Manager();
		
		eventAtt = new ArrayList<String>();
		for(String s : EVENT_ATTR_LIST) {
			eventAtt.add(s);
		}
		
		checkSystemASBCoobservable();
	}
	
//---  Automated Testing   --------------------------------------------------------------------
	
	private static void basicUStructCheck() {
		String SystemA = "Example 1";
		generateSystemA(SystemA);
		makeImageDisplay(SystemA, model, "Example 1");

		String ustruct = model.buildUStructure(SystemA, eventAtt, generateAgentsA(eventAtt));
		
		makeImageDisplay(ustruct, model, "Example 1 UStruct");
	}
	
	private static void checkSystemACoobservable() {
		String SystemA = "Example 1";
		generateSystemA(SystemA);
		System.out.println(model.isCoobservableUStruct(SystemA, eventAtt, generateAgentsA(eventAtt), false));
	}
	
	/**
	 * 
	 * Weird element here; the means by which enable/disable decisions are made on controllable events differ between
	 * schools of thought re: control theory; UStruct/Ricker label them as disable explicitly, whereas Leduc/Urvashi
	 * imply it via contradictions between plants and specifications.
	 * 
	 * Can't just plug-and-play, so how do we adapt it appropriately between these? Hmm. Just ask them.
	 * 
	 * I need my implementation to be dynamic so I can plug the same systems into different approaches, may need
	 * pre-processing? Don't want to change the system too much though and make it no longer the same thing...
	 * 
	 */
	
	private static void checkSystemASBCoobservable() {
		String a = "Ex1";
		String b = "Ex2";
		generateSystemA(a);
		generateSystemA(b);
		ArrayList<String> plants = new ArrayList<String>();
		ArrayList<String> specs = new ArrayList<String>();
		plants.add(a);
		specs.add(b);
		System.out.println(model.isSBCoobservableUrvashi(plants, specs, eventAtt, generateAgentsA(eventAtt)));
		
	}
	
//---  Prefabs   ------------------------------------------------------------------------------
	
	private static void generateSystemA(String name) {
		model.generateEmptyFSM(name);
		
		ArrayList<String> stateAtt = new ArrayList<String>();
		ArrayList<String> eventAtt = new ArrayList<String>();
		ArrayList<String> transAtt = new ArrayList<String>();
		
		stateAtt.add(AttributeList.ATTRIBUTE_INITIAL);
		for(String s : EVENT_ATTR_LIST) {
			eventAtt.add(s);
		}
		transAtt.add(AttributeList.ATTRIBUTE_BAD);
		
		model.assignStateAttributes(name, stateAtt);
		model.assignEventAttributes(name, eventAtt);
		model.assignTransitionAttributes(name, transAtt);
		
		model.addStates(name, 8);
		model.removeState(name, "0");
		
		model.setStateAttribute(name, "1", AttributeList.ATTRIBUTE_INITIAL, true);
		
		String[] events = EVENT_LIST_A;
		for(String s : events) {
			model.addEvent(name, s);
			model.setEventAttribute(name, s, AttributeList.ATTRIBUTE_OBSERVABLE, true);
		}
		
		model.setEventAttribute(name, "c", AttributeList.ATTRIBUTE_CONTROLLABLE, true);
		
		model.addTransition(name, "1", "a1", "2");
		model.addTransition(name, "1", "a2", "3");
		model.addTransition(name, "2", "b1", "4");
		model.addTransition(name, "2", "b2", "5");
		model.addTransition(name, "3", "b1", "6");
		model.addTransition(name, "3", "b2", "7");
		
		model.addTransition(name, "4", "c", "4");
		model.addTransition(name, "5", "c", "5");
		model.addTransition(name, "6", "c", "6");
		model.addTransition(name, "7", "c", "7");
		
		model.setTransitionAttribute(name, "5", "c", AttributeList.ATTRIBUTE_BAD, true);
		model.setTransitionAttribute(name, "6", "c", AttributeList.ATTRIBUTE_BAD, true);
		
		
		HashMap<String, HashSet<String>> badTrans = new HashMap<String, HashSet<String>>();
		HashSet<String> ebe = new HashSet<String>();
		ebe.add("c");
		badTrans.put("5", ebe);
		badTrans.put("6", ebe);
	}
	
	private static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsA(ArrayList<String> eventAtt) {
		boolean[][][] agentInfo = new boolean[][][] {	{	//Agent 1
			  {true, false},	//a1
			  {true, false},	//a2
			  {false, false},	//b1
			  {false, false},	//b2
			  {true, true}		//c
			},
		 	{	//Agent 2
			  {false, false},
			  {false, false},
			  {true, false},
			  {true, false},
			  {true, true}
			}
		};

		ArrayList<HashMap<String, ArrayList<Boolean>>> use = new ArrayList<HashMap<String, ArrayList<Boolean>>>();
				
		for(int i = 0; i < 2; i++) {
			HashMap<String, ArrayList<Boolean>> agen = new HashMap<String, ArrayList<Boolean>>();
			for(int j = 0; j < EVENT_LIST_A.length; j++) {
				String e = EVENT_LIST_A[j];
				ArrayList<Boolean> att = new ArrayList<Boolean>();
				for(int k = 0; k < eventAtt.size(); k++) {
					att.add(agentInfo[i][j][k]);
				}
				agen.put(e, att);
			}
			use.add(agen);
		}
		return use;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private static String generateRandomFSM(String nom, Manager model, int numStates, int numEvents, int numTransition, boolean det, ArrayList<String> strAtt, ArrayList<String> eveAtt, ArrayList<String> tranAtt, ArrayList<Integer> numbers) {
		return model.readInFSM(model.generateRandomFSM(nom, numStates, numEvents, numTransition, det, strAtt, eveAtt, tranAtt, numbers));
	}
	
	private static void makeImageDisplay(String in, Manager model, String nom) {
		String path = FormatConversion.createImgFromFSM(model.generateFSMDot(in), nom);
		System.out.println(path);
		WindowFrame fram = new WindowFrame(800, 800);
		fram.reserveWindow("Main");
		fram.setName("Test Functionality: " + nom);
		fram.showActiveWindow("Main");
		ElementPanel p = new ElementPanel(0, 0, 800, 800);
		ImageDisplay iD = new ImageDisplay(path, p);
		p.setEventReceiver(iD.generateEventReceiver());
		fram.addPanelToWindow("Main", "pan", p);
		iD.refresh();
	}
	
}
