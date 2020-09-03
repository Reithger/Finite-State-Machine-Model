package test;

import java.io.File;

import fsm.FSM;
import graphviz.FSMToDot;
import support.Agent;
import support.UStructure;
import support.component.Event;
import support.component.State;
import support.component.map.TransitionFunction;
import ui.FSMUI;

public class test {
	
	private final static String SOURCE = "./Finite State Machine Model/sources/";
	private final static String SOURCEB = "./Finite State Machine Model/images/";
	private final static String SOURCEC = "./Finite State Machine Model/settings/config.txt";

	public static void main(String[] args) {
		FSMUI screen = new FSMUI();
		//testB();
	}
	
	public static void testA() {
		File f = new File(SOURCE + "A.fsm");
		File g = new File(SOURCE + "B.fsm");
		FSM a = null;
		FSM b = null;
		try {
			a = new FSM(f, "a");
			b = new FSM(g, "b");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		FSM c = a.product(b);
		FSMToDot.createImgFromFSM(c, SOURCEB + "/test", SOURCEB, SOURCEC);
	}

	public static void testB() {
		FSM fsm = new FSM();
		
		fsm.addTransition("1", "a", "2");
		fsm.addTransition("1", "b", "3");
		fsm.addTransition("2", "b", "4");
		fsm.addTransition("3", "a", "5");
		fsm.addTransition("4", "c", "6");
		fsm.addTransition("5", "c", "7");
		fsm.addInitialState("1");
		
		fsm.toTextFile(SOURCE, "_test_initial");
		try {
		fsm = new FSM(new File(SOURCE + "_test_initial.fsm"), "test_initial");
		}
		catch(Exception e) {}
		
		FSMToDot.createImgFromFSM(fsm, SOURCEB + "_in1", SOURCEB, SOURCEC);
		
		TransitionFunction bad = new TransitionFunction();
		bad.addTransitionState(new State("5"), new Event("c"), new State("7"));
		
		Event a1 = new Event("a");
		Event a2 = new Event("a");
		a2.setEventObservability(false);
		Event b1 = new Event("b");
		Event b2 = new Event("b");
		b1.setEventObservability(false);
		Event c = new Event("c");
		
		Agent ag1 = new Agent(a1, b1, c);
		Agent ag2 = new Agent(a2, b2, c);
		
		UStructure uStruc = new UStructure(fsm, bad, ag1, ag2);
		
		FSMToDot.createImgFromFSM(uStruc.getPlantFSM(), SOURCEB + "_out1", SOURCEB, SOURCEC);
		
		FSMToDot.createImgFromFSM(uStruc.getUStructure(), SOURCEB + "_out2", SOURCEB, SOURCEC);
		
		System.out.println(uStruc.getIllegalConfigOneStates());
		System.out.println(uStruc.getIllegalConfigTwoStates());
	}

	public static void testC() {
		FSM fsm = new FSM();
		
		fsm.addTransition("0", "c1", "1");
		fsm.addTransition("0", "b1", "2");
		//fsm.addTransition("0", "b2", "3");
		//fsm.addTransition("0", "a1", "4");
		fsm.addTransition("1", "a2", "5");
		fsm.addTransition("1", "b2", "6");
		//fsm.addTransition("3", "c2", "8");
		fsm.addTransition("2", "a1", "7");
		//fsm.addTransition("4", "b1", "9");
		fsm.addTransition("5", "b1", "10");
		fsm.addTransition("6", "a1", "11");
		fsm.addTransition("7", "c2", "12");
		//fsm.addTransition("8", "a2", "13");
		//fsm.addTransition("9", "c1", "14");
		fsm.addTransition("10", "w", "15");
		fsm.addTransition("11", "w", "16");
		fsm.addTransition("12", "w", "17");
		//fsm.addTransition("13", "w", "18");
		//fsm.addTransition("14", "w", "19");
		fsm.addInitialState("0");

		FSMToDot.createImgFromFSM(fsm, SOURCEB + "_in1", SOURCEB, SOURCEC);

		TransitionFunction bad = new TransitionFunction();
		bad.addTransitionState(new State("13"), new Event("w"), new State("18"));
		bad.addTransitionState(new State("14"), new Event("w"), new State("19"));
		
		Event a11 = new Event("a");	//event name 'a1' then agent number '1'
		Event a12 = new Event("a");
		Event a13 = new Event("a");
		Event a21 = new Event("a");
		Event a22 = new Event("a");
		Event a23 = new Event("a");
		
		a12.setEventObservability(false);
		a13.setEventObservability(false);
		a22.setEventObservability(false);
		a23.setEventObservability(false);
		
		a11.setEventControllability(false);
		a12.setEventControllability(false);
		a13.setEventControllability(false);
		a21.setEventControllability(false);
		a22.setEventControllability(false);
		a23.setEventControllability(false);
		
		Event b11 = new Event("a");	//event name 'a1' then bgent number '1'
		Event b12 = new Event("a");
		Event b13 = new Event("a");
		Event b21 = new Event("a");
		Event b22 = new Event("a");
		Event b23 = new Event("a");
		
		b11.setEventObservability(false);
		b13.setEventObservability(false);
		b21.setEventObservability(false);
		b23.setEventObservability(false);
		
		b11.setEventControllability(false);
		b12.setEventControllability(false);
		b13.setEventControllability(false);
		b21.setEventControllability(false);
		b22.setEventControllability(false);
		b23.setEventControllability(false);
		
		Event c11 = new Event("a");	//event name 'a1' then cgent number '1'
		Event c12 = new Event("a");
		Event c13 = new Event("a");
		Event c21 = new Event("a");
		Event c22 = new Event("a");
		Event c23 = new Event("a");
		
		c11.setEventObservability(false);
		c12.setEventObservability(false);
		c21.setEventObservability(false);
		c22.setEventObservability(false);
		
		c11.setEventControllability(false);
		c12.setEventControllability(false);
		c13.setEventControllability(false);
		c21.setEventControllability(false);
		c22.setEventControllability(false);
		c23.setEventControllability(false);
		
		Event w = new Event("w");
		
		Agent a = new Agent(a11, a21, b11, b21, c11, c21, w);
		Agent b = new Agent(a12, a22, b12, b22, c12, c22, w);
		Agent c = new Agent(a13, a23, b13, b23, c13, c23, w);
		
		UStructure uStruc = new UStructure(fsm, bad, a, b, c);
		
		FSMToDot.createImgFromFSM(uStruc.getPlantFSM(), SOURCEB + "_out1", SOURCEB, SOURCEC);
		
		System.out.println(uStruc.getIllegalConfigOneStates());
		System.out.println(uStruc.getIllegalConfigTwoStates());
		
		FSMToDot.createImgFromFSM(uStruc.getUStructure(), SOURCEB + "_out2", SOURCEB, SOURCEC);
	}
	
}
