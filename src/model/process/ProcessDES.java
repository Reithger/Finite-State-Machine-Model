package model.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
	
	//-- UStructure  ------------------------------------------
	
	public static TransitionSystem buildUStructure(TransitionSystem plant, ArrayList<String> attr, HashMap<String, HashSet<String>> badTrans, boolean[][][] agents) {
		ArrayList<Agent> agen = new ArrayList<Agent>();
		
		ArrayList<String> event = plant.getEventNames();
		
		for(int i = 0; i < agents.length; i++) {
			Agent a = new Agent(attr, event);
			for(int j = 0; j < event.size(); j++) {
				for(int k = 0; k < attr.size(); k++) {
					if(agents[i][j][k])
						a.setAttributeTrue(attr.get(k), event.get(j));
				}
			}
			agen.add(a);
		}
		
		UStructure ustr = new UStructure(plant, attr, badTrans, agen);
		return ustr.getUStructure();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public static void assignAttributeReferences(String init, String mark, String priv, String obs, String cont) {
		ProcessAnalysis.assignAttributeReferences(priv, init);
		ProcessOperation.assignAttributeReferences(init, obs);
		ProcessClean.assignAttributeReferences(init, mark);
		UStructure.assignAttributeReferences(init, obs, cont);
	}
	
}
