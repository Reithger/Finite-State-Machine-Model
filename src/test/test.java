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
		//FSMUI screen = new FSMUI();
		testB();
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
	
}
