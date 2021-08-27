package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import controller.FiniteStateMachine;
import controller.convert.FormatConversion;
import input.CustomEventReceiver;
import model.AttributeList;
import model.Manager;
import model.process.coobservability.Agent;
import ui.popups.PopoutAgentSelection;
import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class TestFunctionality {

	public static void main(String[] args) {
		
		FormatConversion.assignPaths(FiniteStateMachine.ADDRESS_IMAGES, FiniteStateMachine.ADDRESS_CONFIG);
		FiniteStateMachine.fileConfiguration();
		
		String ex = "Example 1";
		Manager model = new Manager();
		model.generateEmptyFSM(ex);
		
		ArrayList<String> stateAtt = new ArrayList<String>();
		ArrayList<String> eventAtt = new ArrayList<String>();
		ArrayList<String> transAtt = new ArrayList<String>();
		
		stateAtt.add(AttributeList.ATTRIBUTE_INITIAL);
		eventAtt.add(AttributeList.ATTRIBUTE_OBSERVABLE);
		eventAtt.add(AttributeList.ATTRIBUTE_CONTROLLABLE);
		transAtt.add(AttributeList.ATTRIBUTE_BAD);
		
		model.assignStateAttributes(ex, stateAtt);
		model.assignEventAttributes(ex, eventAtt);
		model.assignTransitionAttributes(ex, transAtt);
		
		model.addStates(ex, 8);
		model.removeState(ex, "0");
		
		model.setStateAttribute(ex, "1", AttributeList.ATTRIBUTE_INITIAL, true);
		
		String[] events = new String[] {"a1", "a2", "b1", "b2", "c"};
		for(String s : events) {
			model.addEvent(ex, s);
			model.setEventAttribute(ex, s, AttributeList.ATTRIBUTE_OBSERVABLE, true);
		}
		
		model.setEventAttribute(ex, "c", AttributeList.ATTRIBUTE_CONTROLLABLE, true);
		
		model.addTransition(ex, "1", "a1", "2");
		model.addTransition(ex, "1", "a2", "3");
		model.addTransition(ex, "2", "b1", "4");
		model.addTransition(ex, "2", "b2", "5");
		model.addTransition(ex, "3", "b1", "6");
		model.addTransition(ex, "3", "b2", "7");
		
		model.addTransition(ex, "4", "c", "4");
		model.addTransition(ex, "5", "c", "5");
		model.addTransition(ex, "6", "c", "6");
		model.addTransition(ex, "7", "c", "7");
		
		model.setTransitionAttribute(ex, "5", "c", AttributeList.ATTRIBUTE_BAD, true);
		model.setTransitionAttribute(ex, "6", "c", AttributeList.ATTRIBUTE_BAD, true);
		
		
		HashMap<String, HashSet<String>> badTrans = new HashMap<String, HashSet<String>>();
		HashSet<String> ebe = new HashSet<String>();
		ebe.add("c");
		badTrans.put("5", ebe);
		badTrans.put("6", ebe);
		
		makeImageDisplay(ex, model, "Example 1");
		
		ArrayList<Agent> agents = new ArrayList<Agent>();
		agents.add(new Agent(eventAtt, null));
		agents.add(new Agent(eventAtt, null));
		
		for(String s : events) {
			for(Agent a : agents) {
				a.addUnknownEvent(s);
			}
		}
		
		agents.get(0).setAttribute(AttributeList.ATTRIBUTE_OBSERVABLE, "b1", false);
		agents.get(0).setAttribute(AttributeList.ATTRIBUTE_OBSERVABLE, "b2", false);
		agents.get(1).setAttribute(AttributeList.ATTRIBUTE_OBSERVABLE, "a1", false);
		agents.get(1).setAttribute(AttributeList.ATTRIBUTE_OBSERVABLE, "a2", false);
		agents.get(0).setAttribute(AttributeList.ATTRIBUTE_CONTROLLABLE, "c", true);
		agents.get(1).setAttribute(AttributeList.ATTRIBUTE_CONTROLLABLE, "c", true);
		
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
			for(int j = 0; j < events.length; j++) {
				String e = events[j];
				ArrayList<Boolean> att = new ArrayList<Boolean>();
				for(int k = 0; k < eventAtt.size(); k++) {
					att.add(agentInfo[i][j][k]);
				}
				agen.put(e, att);
			}
			use.add(agen);
		}
		
		
		String ustruct = model.buildUStructure(ex, eventAtt, use);
		
		makeImageDisplay(ustruct, model, "Example 1 UStruct");
		
		/*
		ArrayList<String> a = new ArrayList<String>();
		a.add("a");
		a.add("b");
		a.add("c");
		ArrayList<String> b = new ArrayList<String>();
		b.add("Observability");
		b.add("Controlability");
		
		PopoutAgentSelection.assignSymbols("---", "o", "x");
		PopoutAgentSelection pAS = new PopoutAgentSelection(new ArrayList<String>(), a, b);
		System.out.println(pAS.getResult());
		/*
		FormatConversion.assignPaths(FiniteStateMachine.ADDRESS_SOURCES, FiniteStateMachine.ADDRESS_CONFIG);
		ArrayList<String> strAtt = new ArrayList<String>();
		strAtt.add(AttributeList.ATTRIBUTE_INITIAL);
		strAtt.add(AttributeList.ATTRIBUTE_MARKED);
		ArrayList<String> eveAtt = new ArrayList<String>();
		eveAtt.add(AttributeList.ATTRIBUTE_OBSERVABLE);
		ArrayList<String> tranAtt = new ArrayList<String>();
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		numbers.add(1);
		numbers.add(1);
		numbers.add(2);
		String nom = generateRandomFSM("test", model, 5, 3, 2, true, strAtt, eveAtt, tranAtt, numbers);
		String nom2 = generateRandomFSM("test2", model, 5, 3, 2, true, strAtt, eveAtt, tranAtt, numbers);
		String nom3 = generateRandomFSM("test3", model, 5, 3, 2, true, strAtt, eveAtt, tranAtt, numbers);

		makeImageDisplay(nom, model, "Root 1");
		makeImageDisplay(nom2, model, "Root 2");
		makeImageDisplay(nom3, model, "Root 3");
		
		ArrayList<String> names = new ArrayList<String>();
		names.add(nom);
		names.add(nom2);
		
		String trim = model.performParallelComposition(names);
		
		makeImageDisplay(trim, model, "Product");
		makeImageDisplay(model.makeAccessible(trim), model, "Product");
		*/
	}
	
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
