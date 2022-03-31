package model.process.coobservability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;
import model.process.ProcessDES;
import model.process.coobservability.support.Agent;
import model.process.coobservability.support.IllegalConfig;
import model.process.coobservability.support.StateSet;

public class ProcessCoobservability {

//---  Instance Variables   -------------------------------------------------------------------
	
	private static String controllableRef;
	private static String observableRef;
	private static String initialRef;
	private static String badTransRef;
	
	private static boolean showCrushInfo;
	private static boolean showImportantCrushInfo;
	private static boolean showUStructureInfo;
	
//---  Meta   ---------------------------------------------------------------------------------
	
	public static void assignReferences(String cont, String obs, String init, String badTrans) {
		controllableRef = cont;
		observableRef = obs;
		initialRef = init;
		badTransRef = badTrans;
		StateBased.assignAttributeReference(init, obs, cont);
		Incremental.assignAttributeReference(obs, init);
	}
	
	public static void assignAdditionalInfo(boolean in, boolean important, boolean ustru) {
		showCrushInfo = in;
		showImportantCrushInfo = important;
		showUStructureInfo = ustru;
	}
	
	public static void assignIncrementalOptions(int a, int b, int c) {
		Incremental.assignIncrementalOptions(a, b, c);
	}

//---  Operations   ---------------------------------------------------------------------------
	
	//-- Coobservable  ----------------------------------------
		
	public static boolean isCoobservableUStruct(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		UStructure ustr = constructUStruct(plant, attr, agents);
		return isCoobservableUStruct(ustr);
	}
	
	private static boolean isCoobservableUStructRaw(TransitionSystem plant, ArrayList<String> attr, ArrayList<Agent> agents) {
		UStructure ustr = constructUStructRaw(plant, attr, agents);
		return isCoobservableUStruct(ustr);
	}

	public static boolean isCoobservableUStruct(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		ArrayList<Agent> age = constructAgents(getAllEvents(plants, specs), attr, agents);
		
		return isCoobservableUStructRaw(deriveTruePlant(plants, specs, attr), attr, age);
	}
	
	private static TransitionSystem deriveTruePlant(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr) {
		TransitionSystem ultPlant = parallelComp(plants);
		TransitionSystem ultSpec = parallelComp(specs);
		
		LinkedList<StateSet> queue = new LinkedList<StateSet>();
		HashSet<StateSet> visited = new HashSet<StateSet>();
		ArrayList<String> stAttr = ultPlant.getStateAttributes();
		stAttr.add(badTransRef);
		ultPlant.setStateAttributes(stAttr);
		
		StateSet.assignSizes(1, 1);
		String[] use = new String[] {ultPlant.getStatesWithAttribute(initialRef).get(0), ultSpec.getStatesWithAttribute(initialRef).get(0)};
		
		queue.add(new StateSet(use));
		
		while(!queue.isEmpty()) {
			StateSet curr = queue.poll();
			if(visited.contains(curr)) {
				continue;
			}
			visited.add(curr);
			
			String plantState = curr.getPlantState(0);
			String specState = curr.getSpecState(0);
			
			for(String e : ultPlant.getStateTransitionEvents(plantState)) {
				if(ultSpec.getStateEventTransitionStates(specState, e) == null) {	//Do we need to check for bad transitions behind this?
					if(ultPlant.getEventAttribute(e, controllableRef)) {
						ultPlant.setTransitionAttribute(plantState, e, badTransRef, true);
					}
				}
				else {
					use = new String[] {ultPlant.getStateEventTransitionStates(plantState, e).get(0), ultSpec.getStateEventTransitionStates(specState, e).get(0)};
					queue.add(new StateSet(use));
				}
			}
		}
		return ultPlant;
	}

	private static boolean isCoobservableUStruct(UStructure ustr) {
		return ustr.getFilteredIllegalConfigStates().isEmpty();
	}
	
	//-- SB Coobservable  -------------------------------------
	
	public static boolean isSBCoobservableUrvashi(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		HashSet<String> events = new HashSet<String>();
		
		for(TransitionSystem t : plants) {
			events.addAll(t.getEventNames());
		}
		
		for(TransitionSystem t : specs) {
			events.addAll(t.getEventNames());
		}
		
		ArrayList<String> eventUse = new ArrayList<String>();
		eventUse.addAll(events);
		
		StateBased use = new StateBased(plants, specs, attr, constructAgents(eventUse, attr, agents));

		printMemoryUsage(use);
		
		return use.isSBCoobservable();
	}

	//-- Incremental  -----------------------------------------

	public static boolean isCoobservableLiu(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		ConcreteMemoryMeasure cmm = new ConcreteMemoryMeasure();
		
		ArrayList<TransitionSystem> copyPlants = new ArrayList<TransitionSystem>();
		ArrayList<TransitionSystem> copySpecs = new ArrayList<TransitionSystem>();
		copyPlants.addAll(plants);
		copySpecs.addAll(specs);
		
		ArrayList<Agent> age = constructAgents(getAllEvents(plants, specs), attr, agents);
		
		while(!copySpecs.isEmpty()) {
			TransitionSystem pick = Incremental.pickComponent(null, copySpecs, null);								//Get initial spec to use (heuristics choose here)
			ArrayList<TransitionSystem> hold = new ArrayList<TransitionSystem>();		//List to hold all the plants/specs used in the current iteration
			copySpecs.remove(pick);
			hold.add(pick);
			pick = parallelComp(Incremental.generateSigmaStarion(plants), pick);			//Immediately merge our sigmaStarion plant with the spec we chose
			UStructure uStruct = constructUStructQuiet(pick, attr, age);
			cmm.logMemoryUsage();
			while(!isCoobservableUStruct(uStruct)) {
				if(copyPlants.isEmpty() && copySpecs.isEmpty()) {
					printMemoryUsage(cmm);
					return false;
				}
				cmm.logMemoryUsage();
				IllegalConfig counterexample = Incremental.pickCounterExample(uStruct.getFilteredIllegalConfigStates());	//Get a single bad state, probably, maybe write something so UStruct can trace it
				TransitionSystem use = Incremental.pickComponent(copyPlants, copySpecs, counterexample);	//Heuristics go here
				pick = parallelComp(pick, use);
				uStruct = constructUStructQuiet(pick, attr, age);
				if(copySpecs.contains(use)) {
					hold.add(use);
					copySpecs.remove(use);
				}
				else {
					hold.add(use);
					copyPlants.remove(use);
				}
				//Get counterexample - here this should mean one of our problem states (badGood/goodBad states)
				//Find plant or spec that rejects the counterexample - that can make the correct control decision/removes it as a problem
				// NOTE: Have to just do full coobservability again via parallel comp.
				// Should also email Dr. Mallik (?) about some stuff and get some context/help; his 2004 paper has the heuristics!
				//if none exists, return false
				//otherwise add it to progress and continue on
			}
			copyPlants.addAll(hold);
		}
		printMemoryUsage(cmm);
		return true;
	}
	
	public static boolean isSBCoobservableLiu(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {

		
		return false;
	}
	
	//-- Support  ---------------------------------------------
	
	public static TransitionSystem convertSoloPlantSpec(TransitionSystem plant) {
		TransitionSystem out = plant.copy();
		
		for(String s : plant.getStateNames()) {
			for(String e : plant.getStateTransitionEvents(s)) {
				if(plant.getTransitionAttribute(s, e, badTransRef)) {
					for(String t : plant.getStateEventTransitionStates(s, e)) {
						out.removeTransition(s, e, t);
					}
				}
			}
		}
		return out;
	}

	public static UStructure constructUStruct(TransitionSystem plant, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		UStructure u = new UStructure(plant, attr, constructAgents(plant.getEventNames(), attr, agents));
		presentAdditionalInfo(u);
		return u;
	}
	
	public static UStructure constructUStructRaw(TransitionSystem plant, ArrayList<String> attr, ArrayList<Agent> agents) {
		UStructure u = new UStructure(plant, attr, agents);
		presentAdditionalInfo(u);
		return u;
	}
	
	private static UStructure constructUStructQuiet(TransitionSystem plant, ArrayList<String> attr, ArrayList<Agent> agents) {
		return new UStructure(plant, attr, agents);
	}
	
	//-- Helper  ----------------------------------------------

	private static TransitionSystem parallelComp(TransitionSystem ... in) {
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		for(TransitionSystem t : in) {
			use.add(t);
		}
		return parallelComp(use);
	}
	
	private static TransitionSystem parallelComp(ArrayList<TransitionSystem> in) {
		TransitionSystem out = ProcessDES.parallelComposition(in);
		return out;
	}

	private static ArrayList<String> getAllEvents(ArrayList<TransitionSystem> ... in){
		ArrayList<String> out = new ArrayList<String>();
		HashSet<String> hold = new HashSet<String>();
		for(ArrayList<TransitionSystem> aT : in) {
			for(TransitionSystem t : aT) {
				hold.addAll(t.getEventNames());
			}
		}
		out.addAll(hold);
		return out;
	}
	
	private static void presentAdditionalInfo(UStructure u) {
		printMemoryUsage(u);
		if(showCrushInfo) {
			System.out.println(u.printOutCrushMaps(showImportantCrushInfo));
		}
		if(showUStructureInfo) {
			System.out.println("\t\t\t\tState Size: " + u.getUStructure().getStateNames().size() + ", Transition Size: " + u.getUStructure().getNumberTransitions());
		}
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private static ArrayList<Agent> constructAgents(ArrayList<String> event, ArrayList<String> attr, ArrayList<HashMap<String, ArrayList<Boolean>>> agents){
		ArrayList<Agent> agen = new ArrayList<Agent>();
		
		for(HashMap<String, ArrayList<Boolean>> h : agents) {
			Agent a = new Agent(attr, event);
			for(String s : event) {
				if(h.get(s) == null) {
					continue;
				}
				for(int i = 0; i < attr.size(); i++) {
					Boolean b = h.get(s).get(i);
					if(b)
						a.setAttribute(attr.get(i), s, true);
				}
			}
			agen.add(a);
		}
		return agen;
	}
	
	private static void printMemoryUsage(MemoryMeasure m) {
		System.out.println("\t\t\t\tAverage Memory: " + m.getAverageMemoryUsage() + " Mb, Max Memory: " + m.getMaximumMemoryUsage() + " Mb");
	}
	
}
