package model.process;

import java.util.ArrayList;
import java.util.HashMap;

import model.fsm.TransitionSystem;
import model.process.memory.ReceiveMemoryMeasure;

public class ProcessDES {
	
//---  Static Assignments   -------------------------------------------------------------------
	
	public static void assignReferences(ReceiveMemoryMeasure rmm, String init, String mark, String priv, String obs, String cont, String bad, String good) {
		ProcessAnalysis.assignAttributeReferences(priv, init);
		ProcessOperation.assignAttributeReferences(init, obs);
		ProcessClean.assignAttributeReferences(init, mark);
		ProcessCoobservability.assignReferences(rmm, cont, obs, init, bad, good);
	}
	
	public static void assignEndAtFirstCounterexample(boolean in) {
		ProcessCoobservability.assignEndAtFirstCounterexample(in);
	}
		
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
	
	public static TransitionSystem permissiveUnion(ArrayList<TransitionSystem> fsms) {
		return ProcessOperation.permissiveUnion(fsms);
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
	
	public static Boolean isCoobservableUStruct(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) throws Exception{
		return ProcessCoobservability.isCoobservableUStruct(plant, attr, agents);
	}
	
	public static Boolean isInferenceCoobservableUStruct(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents)throws Exception {
		return ProcessCoobservability.isInferenceCoobservableUStruct(plant, attr, agents);
	}
	
	public static Boolean isCoobservableUStruct(ArrayList<TransitionSystem> plant, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents)throws Exception {
		return ProcessCoobservability.isCoobservableUStruct(plant, specs, attr, agents);
	}
	
	public static Boolean isInferenceCoobservableUStruct(ArrayList<TransitionSystem> plant, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) throws Exception{
		return ProcessCoobservability.isInferenceCoobservableUStruct(plant, specs, attr, agents);
	}
	
	public static Boolean isSBCoobservableUrvashi(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents)throws Exception {
		return ProcessCoobservability.isSBCoobservable(plants, specs, attr, agents);
	}
	
	public static Boolean isIncrementalCoobservable(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) throws Exception{
		return ProcessCoobservability.isIncrementalCoobservable(plants, specs, attr, agents);
	}
	
	public static Boolean isIncrementalInferenceCoobservable(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents)throws Exception {
		return ProcessCoobservability.isIncrementalInferenceCoobservable(plants, specs, attr, agents);
	}
	
	public static Boolean isIncrementalSBCoobservable(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents)throws Exception {
		return ProcessCoobservability.isIncrementalSBCoobservable(plants, specs, attr, agents);
	}
	
	//-- UStructure  ------------------------------------------
	
	public static TransitionSystem buildUStructure(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents)throws Exception {
		return ProcessCoobservability.constructUStruct(plant, attr, agents).getUStructure();
	}
	
	public static TransitionSystem buildUStructure(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents)throws Exception {
		return ProcessCoobservability.constructUStruct(plants, specs, attr, agents).getUStructure();
	}
	
	public static ArrayList<TransitionSystem> buildUStructureCrush(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents)throws Exception {
		return ProcessCoobservability.constructUStruct(plant, attr, agents).getCrushUStructures();
	}
	
	public static ArrayList<TransitionSystem> buildUStructureCrush(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) throws Exception{
		return ProcessCoobservability.constructUStruct(plants, specs, attr, agents).getCrushUStructures();
	}

}
