package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import controller.FiniteStateMachine;
import controller.convert.FormatConversion;
import model.AttributeList;
import model.Manager;
import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class TestFunctionality {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static String[] EVENT_LIST_A = new String[] {"a1", "a2", "b1", "b2", "c"};
	private final static String[] EVENT_LIST_B = new String[] {"a", "b", "c", "d", "s"};
	private final static String[] EVENT_LIST_C = new String[] {"a1", "a2", "b1", "b2", "c", "d"};
	private final static String[] EVENT_LIST_D = new String[] {"a", "b", "c"};
	
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
		//basicUStructCheck();
		System.out.println("System A Coobservability:");
		checkSystemACoobservable();
		System.out.println("System A SB Coobservability:");
		checkSystemASBCoobservable();
		System.out.println("System B Coobservability:");
		checkSystemBCoobservable();
		System.out.println("System B SB Coobservability:");
		checkSystemBSBCoobservable();
		System.out.println("System C Coobservability:");
		checkSystemCCoobservable();
		System.out.println("System C SB Coobservability:");
		checkSystemCSBCoobservable();
		System.out.println("System D Coobservability:");
		checkSystemDCoobservable();
		System.out.println("System D SB Coobservability:");
		checkSystemDSBCoobservable();
	}
	
//---  Automated Testing   --------------------------------------------------------------------
	
	private static void basicUStructCheck() {
		String SystemA = "Example 1";
		generateSystemA(SystemA);
		makeImageDisplay(SystemA, "Example 1");

		String ustruct = model.buildUStructure(SystemA, eventAtt, generateAgentsA());
		
		makeImageDisplay(ustruct, "Example 1 UStruct");
	}
	
	private static void checkCoobservable(String name, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		System.out.println(model.isCoobservableUStruct(name, eventAtt, agents, false));
	}
	
	private static void checkSBCoobservable(String name, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		String b = name + "_spec";
		generateSoloSpecPlant(name, b);
		ArrayList<String> plants = new ArrayList<String>();
		ArrayList<String> specs = new ArrayList<String>();
		plants.add(name);
		specs.add(b);
		System.out.println(model.isSBCoobservableUrvashi(plants, specs, eventAtt, agents));
	}
	
	private static void checkSystemACoobservable() {
		String SystemA = "Example 1";
		generateSystemA(SystemA);
		System.out.println(model.isCoobservableUStruct(SystemA, eventAtt, generateAgentsA(), false));
	}
	
	private static void checkSystemASBCoobservable() {
		String a = "Ex1";
		generateSystemA(a);
		checkSBCoobservable(a, generateAgentsA());
	}
	
	private static void checkSystemBCoobservable() {
		String SystemB = "Example B 1";
		generateSystemB(SystemB);
		System.out.println(model.isCoobservableUStruct(SystemB, eventAtt, generateAgentsB(), false));
	}
	
	private static void checkSystemBSBCoobservable() {
		String ex1 = "Ex B 1";
		generateSystemB(ex1);
		checkSBCoobservable(ex1, generateAgentsB());
	}
	
	private static void checkSystemCCoobservable() {
		String SystemC = "Example C 1";
		generateSystemC(SystemC);
		System.out.println(model.isCoobservableUStruct(SystemC, eventAtt, generateAgentsC(), false));
	}
	
	private static void checkSystemCSBCoobservable() {
		String ex1 = "Ex C 1";
		generateSystemC(ex1);
		checkSBCoobservable(ex1, generateAgentsC());
	}
	
	private static void checkSystemDCoobservable() {
		String SystemD = "Example D 1";
		generateSystemD(SystemD);
		System.out.println(model.isCoobservableUStruct(SystemD, eventAtt, generateAgentsD(), false));
	}
	
	private static void checkSystemDSBCoobservable() {
		String ex1 = "Ex D 1";
		generateSystemD(ex1);
		checkSBCoobservable(ex1, generateAgentsD());
	}
	
//---  Prefabs   ------------------------------------------------------------------------------
	
	private static void generateSystemDefault(String name) {
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
		
	}
	
	private static void initiateEvents(String name, String[] eventList, String ... controllables) {
		for(String s : eventList) {
			model.addEvent(name, s);
			model.setEventAttribute(name, s, AttributeList.ATTRIBUTE_OBSERVABLE, true);
		}
		for(String s : controllables) {
			model.setEventAttribute(name, s, AttributeList.ATTRIBUTE_CONTROLLABLE, true);
		}
	}
	
	private static void setBadTransitions(String name, String ... pairTrans) {
		for(int i = 0; i < pairTrans.length; i += 2) {
			model.setTransitionAttribute(name, pairTrans[i], pairTrans[i+1], AttributeList.ATTRIBUTE_BAD, true);
		}
	}
	
	private static void generateSystemA(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 8);
		model.removeState(name, "0");
		
		model.setStateAttribute(name, "1", AttributeList.ATTRIBUTE_INITIAL, true);
		
		initiateEvents(name, EVENT_LIST_A, "c");
		
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
		
		setBadTransitions(name, "5", "c", "6", "c");
	}

	private static void generateSystemB(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 6);
		
		model.setStateAttribute(name, "0", AttributeList.ATTRIBUTE_INITIAL, true);
		
		initiateEvents(name, EVENT_LIST_B, "s");
		
		model.addTransition(name, "0", "a", "1");
		model.addTransition(name, "0", "b", "2");
		model.addTransition(name, "1", "b", "3");
		model.addTransition(name, "1", "c", "2");
		model.addTransition(name, "2", "b", "3");
		model.addTransition(name, "2", "d", "5");
		model.addTransition(name, "3", "d", "4");
		model.addTransition(name, "4", "s", "4");
		model.addTransition(name, "5", "s", "5");
		
		setBadTransitions(name, "5", "s");
	}
	
	private static void generateSystemC(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 5);
		
		model.setStateAttribute(name, "0", AttributeList.ATTRIBUTE_INITIAL, true);
		
		initiateEvents(name, EVENT_LIST_C, "c");
		
		model.addTransition(name, "0", "a1", "1");
		model.addTransition(name, "0", "b1", "1");
		model.addTransition(name, "0", "a2", "2");
		model.addTransition(name, "0", "b2", "2");
		model.addTransition(name, "1", "c", "3");
		model.addTransition(name, "2", "d", "3");
		model.addTransition(name, "2", "c", "4");
		
		setBadTransitions(name, "2", "c");
	}

	private static void generateSystemD(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 7);
		model.removeState(name, "0");
		
		model.setStateAttribute(name, "1", AttributeList.ATTRIBUTE_INITIAL, true);

		initiateEvents(name, EVENT_LIST_D, "b", "c");
		
		model.addTransition(name, "1", "a", "3");
		model.addTransition(name, "1", "b", "2");
		model.addTransition(name, "2", "c", "4");
		model.addTransition(name, "2", "b", "5");
		model.addTransition(name, "3", "b", "5");
		model.addTransition(name, "5", "c", "6");
		
		setBadTransitions(name, "2", "b", "5", "c");
	}

	private static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsA() {
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
		return generateAgentSet(agentInfo, EVENT_LIST_A);
	}
	
	private static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsB(){
		boolean[][][] agentInfo = new boolean[][][] {	{	//Agent 1
			  {true, false},	//a
			  {false, false},	//b
			  {false, false},	//c
			  {true, false},	//d
			  {false, true}		//s
			},
		 	{	//Agent 2
			  {false, false},
			  {true, false},
			  {false, false},
			  {true, false},
			  {false, true}
			},
		 	{	//Agent 3
			  {false, false},
			  {false, false},
			  {true, false},
			  {true, false},
			  {false, true}
			},
		};

		return generateAgentSet(agentInfo, EVENT_LIST_B);
	}
	
	private static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsC() {
		boolean[][][] agentInfo = new boolean[][][] {	{	//Agent 1
			  {true, false},	//a1
			  {true, false},	//a2
			  {false, false},	//b1
			  {false, false},	//b2
			  {false, true},		//c
			  {false, false}		//d
			},
		 	{	//Agent 2
			  {false, false},
			  {false, false},
			  {true, false},
			  {true, false},
			  {false, true},
			  {false, false}
			}
		};
		return generateAgentSet(agentInfo, EVENT_LIST_C);
	}
	
	private static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsD() {
		boolean[][][] agentInfo = new boolean[][][] {	{	//Agent 1
			  {true, false},	//a
			  {false, false},	//b
			  {false, true},	//c
			},
		 	{	//Agent 2
			  {false, false},
			  {true, true},
			  {false, true},
			}
		};
		return generateAgentSet(agentInfo, EVENT_LIST_D);
	}
	
	private static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentSet(boolean[][][] agentInfo, String[] eventList){
		ArrayList<HashMap<String, ArrayList<Boolean>>> use = new ArrayList<HashMap<String, ArrayList<Boolean>>>();
		
		for(int i = 0; i < agentInfo.length; i++) {
			HashMap<String, ArrayList<Boolean>> agen = new HashMap<String, ArrayList<Boolean>>();
			for(int j = 0; j < eventList.length; j++) {
				String e = eventList[j];
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
	
	private static void generateSoloSpecPlant(String plant, String spec) {
		model.convertSoloPlantSpec(plant, spec);
	}
	
	private static String generateRandomFSM(String nom, Manager model, int numStates, int numEvents, int numTransition, boolean det, ArrayList<String> strAtt, ArrayList<String> eveAtt, ArrayList<String> tranAtt, ArrayList<Integer> numbers) {
		return model.readInFSM(model.generateRandomFSM(nom, numStates, numEvents, numTransition, det, strAtt, eveAtt, tranAtt, numbers));
	}
	
	private static void makeImageDisplay(String in, String nom) {
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
