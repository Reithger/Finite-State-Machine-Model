package model.process.coobservability.deciding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import model.fsm.TransitionSystem;
import model.process.ProcessDES;
import model.process.coobservability.UStructure;
import model.process.coobservability.support.Agent;
import model.process.coobservability.support.IllegalConfig;
import model.process.coobservability.support.StateSet;
import model.process.memory.MemoryMeasure;

public class DecideCoobs implements DecideCondition{
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static String initialRef;
	private static String controllableRef;
	private static String badTransRef;

	private ArrayList<TransitionSystem> plants;
	private ArrayList<TransitionSystem> specs;	
	private ArrayList<String> attr;
	private ArrayList<Agent> agents;
	
	private ArrayList<String> events;
	
	protected UStructure ustruct;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public DecideCoobs(ArrayList<TransitionSystem> inPlan, ArrayList<TransitionSystem> inSpe, ArrayList<String> attrIn, ArrayList<Agent> agentsIn) {
		plants = inPlan;
		specs = inSpe;
		attr = attrIn;
		agents = agentsIn;
	}

	public DecideCoobs(ArrayList<String> eventsIn, TransitionSystem specStart, ArrayList<String> attrIn, ArrayList<Agent> agentsIn) {
		events = eventsIn;
		specs = new ArrayList<TransitionSystem>();
		specs.add(specStart);
		attr = attrIn;
		agents = agentsIn;
	}
	
	public DecideCoobs(TransitionSystem root, ArrayList<String> attrIn, ArrayList<Agent> agentsIn) {
		plants = new ArrayList<TransitionSystem>();
		plants.add(root);
		attr = attrIn;
		agents = agentsIn;
	}
	
	public DecideCoobs() {

	}
	
//---  Static Assignments   -------------------------------------------------------------------
	
	public static void assignAttributeReferences(String init, String cont, String bad) {
		initialRef = init;
		controllableRef = cont;
		badTransRef = bad;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public boolean decideCondition() {
		System.out.println("--- Deciding Coobservability ---");
		System.out.println("--- Using: " + plants + ", " + specs + " ---");
		TransitionSystem use = specs == null ? plants.get(0) : deriveTruePlant();
		if(use.getTransitionsWithAttribute(badTransRef).size() == 0) {
			System.out.println("Immediate bail");
			return true;
		}
		ustruct = new UStructure(use, attr, agents);
		ustruct.reserveTransitionSystem(use);
		boolean out = ustruct.getIllegalConfigOneStates().isEmpty() && ustruct.getIllegalConfigTwoStates().isEmpty();
		if(!out)
			System.out.println("--- " + use.getId() + " - Counterexamples: " + getCounterExamples().iterator().next());
		return out;
	}

	@Override
	public DecideCondition constructDeciderCoobs(ArrayList<String> eventsIn, TransitionSystem specStart, ArrayList<String> attrIn, ArrayList<Agent> agentsIn) {
		return new DecideCoobs(eventsIn, specStart, attrIn, agentsIn);
	}

	@Override
	public HashSet<IllegalConfig> getCounterExamples() {
		HashSet<IllegalConfig> out = new HashSet<IllegalConfig>();
		out.addAll(ustruct.getIllegalConfigOneStates());
		out.addAll(ustruct.getIllegalConfigTwoStates());
		return out;
	}

	@Override
	public void addComponent(TransitionSystem next, boolean plant) {
		if(plant) {
			if(plants == null) {
				plants = new ArrayList<TransitionSystem>();
				//TODO: This fixes some cases and breaks others. Figure out how this needs to be configured?
				plants.add(generatePlantSigmaStarion(next));
			}
			plants.add(next);
		}
		else {
			specs.add(next);
		}
	}

	@Override
	public void replaceSigma(ArrayList<String> events) {
		plants.remove(0);
		plants.add(0, generateSigmaStarion(parallelComp(specs)));
	}
	
	@Override
	public MemoryMeasure produceMemoryMeasure() {
		return ustruct != null ? ustruct : UStructure.produceBlank();
	}
	
	public UStructure getUStructure() {
		return ustruct;
	}
	
	private TransitionSystem generatePlantSigmaStarion(TransitionSystem template) {
		TransitionSystem out = new TransitionSystem("sigma_starion_plant");
		out.copyAttributes(template);
		
		String st = "0";
		
		out.addState(st);
		out.setStateAttribute(st, initialRef, true);
		
		for(String t : events) {
			out.addTransition(st, t, st);
		}
		
		return out;
	}
	
	private TransitionSystem generateSigmaStarion(TransitionSystem spec) {
		TransitionSystem sigmaStar = spec.copy();
		sigmaStar.setId("sigma_starion_" + spec.getId());
		
		for(String s : sigmaStar.getStateNames()) {
			for(String t : events) {
				if(!sigmaStar.getStateTransitionEvents(s).contains(t)) {
					sigmaStar.addTransition(s, t, s);
				}
			}
		}
		
		return sigmaStar;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private TransitionSystem deriveTruePlant() {
		//System.out.println("Deriving with: " + plants + ", " + specs);
		TransitionSystem ultPlant = plants == null ? generateSigmaStarion(parallelComp(specs)) : parallelComp(plants);
		TransitionSystem ultSpec = parallelComp(specs);
		
		LinkedList<StateSet> queue = new LinkedList<StateSet>();
		HashSet<StateSet> visited = new HashSet<StateSet>();
		
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
				//if(e.equals("b_{0}"))
				  //System.out.println(plantState + ", " + specState + " -> " + e + " " + ultPlant.getStateEventTransitionStates(plantState, e) + " " + ultSpec.getStateEventTransitionStates(specState, e));
				if(specState == null || (ultSpec.eventExists(e) && (ultSpec.getStateEventTransitionStates(specState, e) == null || ultSpec.getStateEventTransitionStates(specState, e).size() == 0))) {	//Do we need to check for bad transitions behind this?
					ultPlant.setTransitionAttribute(plantState, e, badTransRef, true);
					//if(e.equals("b_{0}"))
					 // System.out.println("Bad: " + plantState + " " + e);
					//TODO: Anything behind a bad transition here *has* to also be bad
					use = new String[] {ultPlant.getStateEventTransitionStates(plantState, e).get(0), null};
					queue.add(new StateSet(use));
				}
				else {
					use = new String[] {ultPlant.getStateEventTransitionStates(plantState, e).get(0), ultSpec.eventExists(e) ? ultSpec.getStateEventTransitionStates(specState, e).get(0) : specState};
					queue.add(new StateSet(use));
				}
			}
		}
		return ultPlant;
	}

	private TransitionSystem parallelComp(ArrayList<TransitionSystem> in) {
		return ProcessDES.parallelComposition(in);
	}

}
