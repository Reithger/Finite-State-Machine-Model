package test.help;

import java.util.ArrayList;

import model.AttributeList;
import model.Manager;

public class SystemGeneration {
	
//---  Instance Variables   -------------------------------------------------------------------

	private static Manager model;
	
//---  Static Assignments   -------------------------------------------------------------------
	
	public static void assignManager(Manager man) {
		model = man;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	//-- Solo System  -----------------------------------------
	
	public static void generateSystemA(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 8);
		model.removeState(name, "0");
		
		model.setStateAttribute(name, "1", AttributeList.ATTRIBUTE_INITIAL, true);
		
		initiateEvents(name, EventSets.EVENT_LIST_A, "c");
		
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

	public static void generateSystemB(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 6);
		
		model.setStateAttribute(name, "0", AttributeList.ATTRIBUTE_INITIAL, true);
		
		initiateEvents(name, EventSets.EVENT_LIST_B, "s");
		
		model.setEventAttribute(name, "c", AttributeList.ATTRIBUTE_OBSERVABLE, false);
		
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
	
	public static void generateSystemC(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 5);
		
		model.setStateAttribute(name, "0", AttributeList.ATTRIBUTE_INITIAL, true);
		
		initiateEvents(name, EventSets.EVENT_LIST_C, "c");
		
		model.addTransition(name, "0", "a1", "1");
		model.addTransition(name, "0", "b1", "1");
		model.addTransition(name, "0", "a2", "2");
		model.addTransition(name, "0", "b2", "2");
		model.addTransition(name, "1", "c", "3");
		model.addTransition(name, "2", "d", "3");
		model.addTransition(name, "2", "c", "4");
		
		setBadTransitions(name, "2", "c");
	}

	public static void generateSystemD(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 7);
		model.removeState(name, "0");
		
		model.setStateAttribute(name, "1", AttributeList.ATTRIBUTE_INITIAL, true);

		initiateEvents(name, EventSets.EVENT_LIST_D, "b", "c");
		
		model.addTransition(name, "1", "a", "3");
		model.addTransition(name, "1", "b", "2");
		model.addTransition(name, "2", "c", "4");
		model.addTransition(name, "2", "b", "5");
		model.addTransition(name, "3", "b", "5");
		model.addTransition(name, "5", "c", "6");
		
		setBadTransitions(name, "2", "b", "5", "c");
	}

	public static void generateSystemE(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 12);
		model.setStateAttribute(name, "0", AttributeList.ATTRIBUTE_INITIAL, true);
		
		initiateEvents(name, EventSets.EVENT_LIST_E, "s");
		
		model.addTransition(name, "0", "c1", "1");
		model.addTransition(name, "0", "a1", "2");
		model.addTransition(name, "0", "c2", "3");
		model.addTransition(name, "0", "a2", "4");
		model.addTransition(name, "0", "c3", "5");

		model.addTransition(name, "1", "a2", "6");
		model.addTransition(name, "1", "b2", "6");

		model.addTransition(name, "2", "b1", "6");
		model.addTransition(name, "2", "b2", "7");

		model.addTransition(name, "3", "a2", "7");
		model.addTransition(name, "3", "b1", "7");

		model.addTransition(name, "4", "b1", "8");
		
		model.addTransition(name, "5", "a1", "8");
		model.addTransition(name, "5", "b2", "8");

		model.addTransition(name, "6", "d", "9");
		model.addTransition(name, "7", "d", "10");
		model.addTransition(name, "8", "d", "11");

		model.addTransition(name, "9", "s", "9");
		model.addTransition(name, "10", "s", "10");
		model.addTransition(name, "11", "s", "11");
		
		setBadTransitions(name, "10", "s", "11", "s");
	}
	
	//-- Poly System  -----------------------------------------
	
	public static void generateSystemSetA(ArrayList<String> name) {
		
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private static void generateSystemDefault(String name) {
		model.generateEmptyFSM(name);
		
		ArrayList<String> stateAtt = new ArrayList<String>();
		ArrayList<String> eventAtt = new ArrayList<String>();
		ArrayList<String> transAtt = new ArrayList<String>();
		
		stateAtt.add(AttributeList.ATTRIBUTE_INITIAL);
		for(String s : EventSets.EVENT_ATTR_LIST) {
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

}
