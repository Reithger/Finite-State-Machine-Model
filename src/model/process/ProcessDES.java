package model.process;

import java.util.ArrayList;

import model.fsm.TransitionSystem;

public class ProcessDES {

//---  Operations   ---------------------------------------------------------------------------

	//-- Operation  -------------------------------------------
	
	public static TransitionSystem buildObserver(TransitionSystem in) {
		return ProcessOperation.buildObserver(in);
	}
	
	public static TransitionSystem product(TransitionSystem in, ArrayList<TransitionSystem> other) {
		return ProcessOperation.product(in, other);
	}
	
	public static TransitionSystem parallelComposition(TransitionSystem in, ArrayList<TransitionSystem> other) {
		return ProcessOperation.parallelComposition(in, other);
	}
	
	//-- Clean  -----------------------------------------------
	
	public static TransitionSystem trim(TransitionSystem in) {
		return ProcessClean.trim(in);
	}
	
	public static TransitionSystem makeAccessible(TransitionSystem in) {
		return ProcessClean.makeAccessible(in);
	}
	
	public static TransitionSystem makeCoAccessible(TransitionSystem in) {
		return ProcessClean.makeCoAccessible(in);
	}
	
	//-- Analysis  --------------------------------------------
	
	public static Boolean isBlocking(TransitionSystem in) {
		return ProcessAnalysis.isBlocking(in);
	}
	
	public static ArrayList<String> findPrivateStates(TransitionSystem in){
		return ProcessAnalysis.findPrivateStates(in);
	}
	
	public static Boolean testOpacity(TransitionSystem in) {
		return ProcessAnalysis.testOpacity(in);
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public static void assignAttributeReferences(String init, String mark, String priv, String obs) {
		ProcessAnalysis.assignAttributeReferences(priv, init);
		ProcessOperation.assignAttributeReferences(init, obs);
		ProcessClean.assignAttributeReferences(init, mark);
	}
	
}
