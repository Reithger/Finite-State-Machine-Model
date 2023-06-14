package test.help;

import java.util.ArrayList;

import model.AttributeList;
import model.Manager;
import model.fsm.TransitionSystem;

public class SystemGeneration {
	
//---  Instance Variables   -------------------------------------------------------------------

	private static Manager model;
	
//---  Static Assignments   -------------------------------------------------------------------
	
	public static void assignManager(Manager man) {
		model = man;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	//-- Solo System  -----------------------------------------
	
	public static void generateSystemExample1(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 7);

		initialState(name, "0");
		
		initiateEvents(name, EventSets.EVENT_LIST_A);
		
		model.addTransition(name, "0", "a_{1}", "1");
		model.addTransition(name, "0", "a_{2}", "2");
		
		model.addTransition(name, "1", "b_{1}", "3");
		model.addTransition(name, "1", "b_{2}", "4");
		model.addTransition(name, "2", "b_{1}", "5");
		model.addTransition(name, "2", "b_{2}", "6");
		
		model.addTransition(name, "3", "c", "3");
		model.addTransition(name, "4", "c", "4");
		model.addTransition(name, "5", "c", "5");
		model.addTransition(name, "6", "c", "6");
		
		
	}
	
	public static void generateSystemExample2(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 7);

		initialState(name, "0");
		
		initiateEvents(name, EventSets.EVENT_LIST_A);
		
		model.addTransition(name, "0", "a_{1}", "1");
		model.addTransition(name, "0", "a_{2}", "2");
		
		model.addTransition(name, "1", "b_{1}", "3");
		model.addTransition(name, "1", "b_{2}", "4");
		model.addTransition(name, "2", "b_{1}", "5");
		model.addTransition(name, "2", "b_{2}", "6");
		
		model.addTransition(name, "3", "c", "3");
	}
	
	public static void generateSystemExample3(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 7);

		initialState(name, "0");
		
		initiateEvents(name, EventSets.EVENT_LIST_A);
		
		model.addTransition(name, "0", "a_{1}", "1");
		model.addTransition(name, "0", "a_{2}", "2");
		
		model.addTransition(name, "1", "b_{1}", "3");
		model.addTransition(name, "1", "b_{2}", "4");
		model.addTransition(name, "2", "b_{1}", "5");
		model.addTransition(name, "2", "b_{2}", "6");
		
		model.addTransition(name, "3", "c", "3");
		model.addTransition(name, "4", "c", "4");
		model.addTransition(name, "5", "c", "5");
		model.addTransition(name, "6", "c", "6");
		
		setBadTransitions(name,"4","c","5","c","6","c");
	}
	
	public static void generateSystemExample4(String name) {
		generateSystemDefault(name);
		
		model.addState(name, "(0, 1, 2)");
		model.addState(name, "(3, 5)");
		model.addState(name, "(4, 6)");
		
		initialState(name, "(0, 1, 2)");
		
		initiateEvents(name, EventSets.EVENT_LIST_A);
		
		model.addTransition(name, "(0, 1, 2)", "b_{1}", "(3, 5)");
		model.addTransition(name, "(0, 1, 2)", "b_{2}", "(4, 6)");
		model.addTransition(name, "(4, 6)", "c", "(4, 6)");
		model.addTransition(name, "(3, 5)", "c", "(3, 5)");
		
	}
	
	public static void generateSystemA(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 8);
		model.removeState(name, "0");
		
		model.setStateAttribute(name, "1", AttributeList.ATTRIBUTE_INITIAL, true);
		
		initiateEvents(name, EventSets.EVENT_LIST_A, "c");
		
		model.addTransition(name, "1", "a_{1}", "2");
		model.addTransition(name, "1", "a_{2}", "3");
		model.addTransition(name, "2", "b_{1}", "4");
		model.addTransition(name, "2", "b_{2}", "5");
		model.addTransition(name, "3", "b_{1}", "6");
		model.addTransition(name, "3", "b_{2}", "7");
		
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
	
	public static void generateSystemBAlt(String name) {
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
		model.addTransition(name, "2", "a", "3");
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
		
		model.addTransition(name, "0", "a_{1}", "1");
		model.addTransition(name, "0", "b_{1}", "1");
		model.addTransition(name, "0", "a_{2}", "2");
		model.addTransition(name, "0", "b_{2}", "2");
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
		model.addTransition(name, "0", "a_{1}", "2");
		model.addTransition(name, "0", "c2", "3");
		model.addTransition(name, "0", "a_{2}", "4");
		model.addTransition(name, "0", "c3", "5");

		model.addTransition(name, "1", "a_{2}", "6");
		model.addTransition(name, "1", "b_{2}", "6");

		model.addTransition(name, "2", "b_{1}", "6");
		model.addTransition(name, "2", "b_{2}", "7");

		model.addTransition(name, "3", "a_{2}", "7");
		model.addTransition(name, "3", "b_{1}", "7");

		model.addTransition(name, "4", "b_{1}", "8");
		
		model.addTransition(name, "5", "a_{1}", "8");
		model.addTransition(name, "5", "b_{2}", "8");

		model.addTransition(name, "6", "d", "9");
		model.addTransition(name, "7", "d", "10");
		model.addTransition(name, "8", "d", "11");

		model.addTransition(name, "9", "s", "9");
		model.addTransition(name, "10", "s", "10");
		model.addTransition(name, "11", "s", "11");
		
		setBadTransitions(name, "10", "s", "11", "s");
	}
	
	public static void generateSystemFinn(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 19);
		//model.removeState(name, "0");
		
		model.setStateAttribute(name, "0", AttributeList.ATTRIBUTE_INITIAL, true);

		initiateEvents(name, EventSets.EVENT_LIST_FINN5, "s");
		
		model.addTransition(name, "0", "a_{1}", "1");
		model.addTransition(name, "0", "a_{2}", "2");
		model.addTransition(name, "0", "a_{3}", "3");
		model.addTransition(name, "0", "a_{4}", "4");
		model.addTransition(name, "0", "a_{5}", "5");
		model.addTransition(name, "0", "a_{6}", "6");
		model.addTransition(name, "1", "b_{1}", "7");
		model.addTransition(name, "1", "b_{2}", "8");
		model.addTransition(name, "2", "b_{2}", "9");
		model.addTransition(name, "2", "b_{3}", "10");
		model.addTransition(name, "3", "b_{3}", "11");
		model.addTransition(name, "3", "b_{4}", "12");
		model.addTransition(name, "4", "b_{4}", "13");
		model.addTransition(name, "4", "b_{5}", "14");
		model.addTransition(name, "5", "b_{5}", "15");
		model.addTransition(name, "5", "b_{6}", "16");
		model.addTransition(name, "6", "b_{6}", "17");
		model.addTransition(name, "6", "b_{1}", "18");
		
		model.addTransition(name, "7", "s", "7");
		model.addTransition(name, "8", "s", "8");
		model.addTransition(name, "9", "s", "9");
		model.addTransition(name, "10", "s", "10");
		model.addTransition(name, "11", "s", "11");
		model.addTransition(name, "12", "s", "12");
		model.addTransition(name, "13", "s", "13");
		model.addTransition(name, "14", "s", "14");
		model.addTransition(name, "15", "s", "15");
		model.addTransition(name, "16", "s", "16");
		model.addTransition(name, "17", "s", "17");
		model.addTransition(name, "18", "s", "18");
		
		setBadTransitions(name, "8", "s", "10", "s","12", "s","14", "s","16", "s","17", "s");
	}
	
	public static void generateSystemSigmaStarion(String name, ArrayList<String> events) {
		generateSystemDefault(name);
		
		model.addStates(name, 1);

		initialState(name, "0");
		
		String[] use = new String[events.size()];
		for(int i = 0; i < events.size(); i++) {
			use[i] = events.get(i);
		}
		
		initiateEvents(name, use);
		
		for(String e : events) {
			model.addTransition(name, "0", e, "0");
		}
	}
	
	public static void generateSystemSpecPrimeTestPlant(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 5);

		initialState(name, "0");
		
		initiateEvents(name, EventSets.EVENT_LIST_SPEC_PRIME);
		
		addTransitions(name, "a", "0", "1");
		addTransitions(name, "b", "0", "1", "1", "3");
		addTransitions(name, "g", "1", "2", "3", "4");
	}
	
	public static void generateSystemSpecPrimeTestSpec(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 5);

		initialState(name, "0");
		
		initiateEvents(name, EventSets.EVENT_LIST_SPEC_PRIME);
		
		addTransitions(name, "a", "0", "2");
		addTransitions(name, "b", "0", "1", "2", "4");
		addTransitions(name, "g", "1", "3");
	}
	
	//-- Poly System  -----------------------------------------
	
	public static void generateSystemSetA(ArrayList<String> name) {
		generateLiuG1(name.get(0));
		generateLiuH1(name.get(1));
	}
	
	public static void generateSystemSetB(ArrayList<String> name) {
		generateLiuG3(name.get(0));
		generateLiuG4(name.get(1));
		generateLiuH1(name.get(2));
	}
	
	private static void generateLiuG1(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 5);
		model.removeState(name, "0");
		
		model.setStateAttribute(name, "1", AttributeList.ATTRIBUTE_INITIAL, true);
		
		initiateEvents(name, EventSets.EVENT_LIST_LIU_ONE, "g");
		
		model.addTransition(name, "1", "a", "2");
		model.addTransition(name, "1", "b", "2");
		model.addTransition(name, "2", "a", "3");
		model.addTransition(name, "2", "b", "3");
		model.addTransition(name, "3", "g", "4");
	}

	private static void generateLiuG2(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 7);
		model.removeState(name, "0");
		
		model.setStateAttribute(name, "1", AttributeList.ATTRIBUTE_INITIAL, true);
		
		initiateEvents(name, EventSets.EVENT_LIST_LIU_ONE, "g");
		
		addTransitions(name, "a", "1", "2", "2", "4", "3", "5");
		addTransitions(name, "b", "1", "3", "3", "4", "2", "5");
		addTransitions(name, "g", "5", "6");
	}
	
	private static void generateLiuH1(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 6);
		model.removeState(name, "0");

		initialState(name, "1");
		
		initiateEvents(name, EventSets.EVENT_LIST_LIU_ONE, "g");
		
		addTransitions(name, "a", "1", "2", "3", "4");
		addTransitions(name, "b", "1", "3", "2", "4");
		addTransitions(name, "g", "4", "5");
	}
	
	private static void generateLiuG3(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 9);
		model.removeState(name, "0");
		
		initialState(name, "1");
		
		initiateEvents(name, EventSets.EVENT_LIST_LIU_ONE, "g");
		
		addTransitions(name, "a", "1", "2", "2", "4", "3", "5");
		addTransitions(name, "b", "1", "3", "3", "6", "2", "5");
		addTransitions(name, "g", "4", "7", "5", "8");
	}
	
	private static void generateLiuG4(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 9);
		model.removeState(name, "0");
		
		initialState(name, "1");
		
		initiateEvents(name, EventSets.EVENT_LIST_LIU_ONE, "g");
		
		addTransitions(name, "a", "1", "2", "2", "4", "3", "5");
		addTransitions(name, "b", "1", "3", "3", "6", "2", "5");
		addTransitions(name, "g", "6", "7", "5", "8");
	}
	
	private static void generateLiuH2(String name) {
		generateSystemDefault(name);
	}

	public static void generateSystemSetUrvashi(String plant, String spec) {
		generateUrvashiPlant(plant);
		generateUrvashiSpec(spec);
	}
	
	private static void generateUrvashiPlant(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 7);
		
		
		initialState(name, "0");
		
		initiateEvents(name, EventSets.EVENT_LIST_D, "c");
		
		addTransitions(name, "a", "0", "1", "2", "4");
		addTransitions(name, "b", "0", "2", "1", "3");
		addTransitions(name, "c", "3", "5", "4", "6");
	}
	
	private static void generateUrvashiSpec(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 7);

		model.removeState(name, "5");
		
		initialState(name, "0");
		
		initiateEvents(name, EventSets.EVENT_LIST_D, "c");
		
		addTransitions(name, "a", "0", "1", "2", "4");
		addTransitions(name, "b", "0", "2", "1", "3");
		addTransitions(name, "c", "4", "6");
	}
	
	public static ArrayList<String> generateSystemSetDTP() {
		ArrayList<String> use = new ArrayList<String>();
		use.add("Sender");
		use.add("Receiver");
		use.add("Channel");
		use.add("SpecOne");
		use.add("SpecTwo");
		use.add("SpecThree");
		generateDTPSender(use.get(0));
		generateDTPReceiver(use.get(1));
		generateDTPChannel(use.get(2));
		generateDTPSpecOne(use.get(3));
		generateDTPSpecTwo(use.get(4));
		generateDTPSpecThree(use.get(5));
		return use;
	}
 	
	private static void generateDTPSender(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 5);
		
		model.removeState(name,  "0");
		
		initialState(name, "1");
		
		initiateEvents(name, EventSets.EVENT_LIST_DTP_SENDER);
		
		addTransitions(name, "getFrame", "1", "2", "4", "2");
		addTransitions(name, "loss", "3", "2");
		addTransitions(name, "send_0", "2", "3", "4", "3");
		addTransitions(name, "send_1", "2", "3", "4", "3");
		addTransitions(name, "rcvAck_0", "3", "4");
		addTransitions(name, "rcvAck_1", "3", "4");
	}
	
	private static void generateDTPReceiver(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 6);
		
		model.removeState(name,  "0");
		
		initialState(name, "1");
		
		initiateEvents(name, EventSets.EVENT_LIST_DTP_RECEIVER);
		
		addTransitions(name, "rcv_0", "1", "2", "4", "5");
		addTransitions(name, "rcv_1", "1", "2", "4", "5");
		addTransitions(name, "passToHost", "2", "3", "5", "3");
		addTransitions(name, "sendAck_0", "3", "4", "5", "4");
		addTransitions(name, "sendAck_1", "3", "4", "5", "4");
	}
	
	private static void generateDTPChannel(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 5);
		
		model.removeState(name,  "0");
		
		initialState(name, "1");
		
		initiateEvents(name, EventSets.EVENT_LIST_DTP_CHANNEL);
		
		addTransitions(name, "rcv_0", "2", "1");
		addTransitions(name, "rcvAck_1", "3", "1");
		addTransitions(name, "rcv_1", "4", "1");
		addTransitions(name, "rcvAck_0", "5", "1");
		
		addTransitions(name, "send_0", "1", "2");
		addTransitions(name, "send_1", "1", "4");
		addTransitions(name, "sendAck_0", "1", "5");
		addTransitions(name, "sendAck_1", "1", "3");
		
		addTransitions(name, "loss", "2", "1", "4", "1", "5", "1", "3", "1");
	}
	
	private static void generateDTPSpecOne(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 3);
		
		model.removeState(name,  "0");
		
		initialState(name, "1");
		
		initiateEvents(name, EventSets.EVENT_LIST_DTP_SPEC_ONE);

		addTransitions(name, "getFrame", "1", "2");
		addTransitions(name, "passToHost", "2", "1");
		
		for(String s : EventSets.EVENT_LIST_DTP) {
			if(!(s.equals("getFrame") || s.equals("passToHost"))) {
				addTransitions(name, s, "1", "1", "2", "2");
			}
		}
	}
	
	private static void generateDTPSpecTwo(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 7);
		
		model.removeState(name,  "0");
		
		initialState(name, "1");
		
		initiateEvents(name, EventSets.EVENT_LIST_DTP_SPEC_TWO);
		
		addTransitions(name, "getFrame", "1", "2", "4", "5");
		addTransitions(name, "loss", "3", "2", "6", "5");
		
		addTransitions(name, "rcvAck_0", "3", "4", "6", "5");
		addTransitions(name, "rcvAck_1", "3", "2", "6", "1");
		
		addTransitions(name, "send_0", "2", "3");
		addTransitions(name, "send_1", "5", "6");
	}
	
	private static void generateDTPSpecThree(String name) {
		generateSystemDefault(name);
		
		model.addStates(name, 7);
		
		model.removeState(name,  "0");
		
		initialState(name, "1");
		
		initiateEvents(name, EventSets.EVENT_LIST_DTP_SPEC_THREE);
		
		addTransitions(name, "rcv_0", "1", "2", "4", "3");
		addTransitions(name, "rcv_1", "4", "5", "1", "6");
		
		addTransitions(name, "passToHost", "2", "3", "5", "6");
		
		addTransitions(name, "sendAck_0", "3", "4");
		addTransitions(name, "sendAck_1", "6", "1");
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

	private static void addTransitions(String name, String event, String ... statePairs) {
		for(int i = 0; i < statePairs.length; i += 2) {
			model.addTransition(name, statePairs[i], event, statePairs[i+1]);
		}
	}
	
	private static void initialState(String name, String state) {
		model.setStateAttribute(name, state, AttributeList.ATTRIBUTE_INITIAL, true);
	}
	
}
