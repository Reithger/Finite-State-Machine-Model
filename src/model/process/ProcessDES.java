package model.process;

import java.util.ArrayList;
import java.util.HashMap;

import model.fsm.TransitionSystem;
import model.process.coobservability.ProcessCoobservability;
import model.process.coobservability.UStructure;

public class ProcessDES {

//---  Operations   ---------------------------------------------------------------------------

	//-- Operation  -------------------------------------------
	
	public static TransitionSystem buildObserver(TransitionSystem in) {
		return ProcessOperation.buildObserver(in);
	}
	
	public static TransitionSystem product(ArrayList<TransitionSystem> fsms) {
		return ProcessOperation.product(fsms);
	}
	
	public static TransitionSystem parallelComposition(ArrayList<TransitionSystem> fsms) {
		return ProcessOperation.parallelComposition(fsms);
	}
	
	public static TransitionSystem convertSoloPlantSpec(TransitionSystem in) {
		return ProcessCoobservability.convertSoloPlantSpec(in);
	}
	
	//-- Clean  -----------------------------------------------
	
	public static TransitionSystem trim(TransitionSystem in) throws Exception {
		return ProcessClean.trim(in);
	}
	
	public static TransitionSystem makeAccessible(TransitionSystem in) {
		return ProcessClean.makeAccessible(in);
	}
	
	public static TransitionSystem makeCoAccessible(TransitionSystem in) throws Exception {
		return ProcessClean.makeCoAccessible(in);
	}
	
	//-- Analysis  --------------------------------------------
	
	public static Boolean isBlocking(TransitionSystem in) throws Exception {
		return ProcessAnalysis.isBlocking(in);
	}
	
	public static Boolean isAccessible(TransitionSystem in) throws Exception {
		return ProcessAnalysis.isAccessible(in);
	}
	
	public static ArrayList<String> findPrivateStates(TransitionSystem in){
		return ProcessAnalysis.findPrivateStates(in);
	}
	
	public static Boolean testOpacity(TransitionSystem in) {
		return ProcessAnalysis.testOpacity(in);
	}
	
	public static Boolean isCoobservableUStruct(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		return ProcessCoobservability.isCoobservableUStruct(plant, attr, agents);
	}
	
	public static Boolean isCoobservableUStruct(ArrayList<TransitionSystem> plant, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		return ProcessCoobservability.isCoobservableUStruct(plant, specs, attr, agents);
	}
	
	public static Boolean isSBCoobservableUrvashi(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		return ProcessCoobservability.isSBCoobservableUrvashi(plants, specs, attr, agents);
	}
	
	public static Boolean isIncrementalCoobservable(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		return ProcessCoobservability.isCoobservableLiu(plants, specs, attr, agents);
	}
	
	//-- UStructure  ------------------------------------------
	
	public static TransitionSystem buildUStructure(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		return ProcessCoobservability.constructUStruct(plant, attr, agents).getUStructure();
	}
	
	public static ArrayList<TransitionSystem> buildUStructureCrush(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		return ProcessCoobservability.constructUStruct(plant, attr, agents).getCrushUStructures();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public static void assignAttributeReferences(String init, String mark, String priv, String obs, String cont, String bad, String good) {
		ProcessAnalysis.assignAttributeReferences(priv, init);
		ProcessOperation.assignAttributeReferences(init, obs);
		ProcessClean.assignAttributeReferences(init, mark);
		ProcessCoobservability.assignReferences(cont, obs, init, bad);
		UStructure.assignAttributeReferences(init, obs, cont, bad, good);
	}
	
	public static void assignCoobservableCrushPrintOut(boolean print, boolean important, boolean ustruct) {
		ProcessCoobservability.assignAdditionalInfo(print, important, ustruct);
	}
	
}
