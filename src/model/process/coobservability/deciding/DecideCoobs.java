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

/**
 * 
 * Also needs enable-by-default and disable-by-default options
 * 
 * 
 * 
 * @author SirBo
 *
 */

public class DecideCoobs implements DecideCondition{
	
//---  Constants   ----------------------------------------------------------------------------
	
	public static final int DECISION_MODE_ENABLE = 1;
	public static final int DECISION_MODE_DISABLE = 0;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static String initialRef;
	private static String controllableRef;
	private static String badTransRef;
	private static String observableRef;

	private ArrayList<TransitionSystem> plants;
	private ArrayList<TransitionSystem> specs;	
	private ArrayList<String> attr;
	private ArrayList<Agent> agents;
	
	private HashSet<String> events;
	
	protected UStructure ustruct;
	
	private int enableDisableMode;
	
	private TransitionSystem hold;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public DecideCoobs() {
		enableDisableMode = DECISION_MODE_ENABLE;
	}
	
	public DecideCoobs(ArrayList<TransitionSystem> inPlan, ArrayList<TransitionSystem> inSpe, ArrayList<String> attrIn, ArrayList<Agent> agentsIn) {
		plants = inPlan;
		specs = inSpe;
		attr = attrIn;
		agents = agentsIn;
		enableDisableMode = DECISION_MODE_ENABLE;
	}

	public DecideCoobs(ArrayList<String> eventsIn, TransitionSystem specStart, ArrayList<String> attrIn, ArrayList<Agent> agentsIn) {
		specs = new ArrayList<TransitionSystem>();
		specs.add(specStart);
		attr = attrIn;
		agents = agentsIn;
		events = new HashSet<String>();
		events.addAll(eventsIn);
		enableDisableMode = DECISION_MODE_ENABLE;
	}
	
	public DecideCoobs(TransitionSystem root, ArrayList<String> attrIn, ArrayList<Agent> agentsIn) {
		plants = new ArrayList<TransitionSystem>();
		plants.add(root);
		attr = attrIn;
		agents = agentsIn;
		enableDisableMode = DECISION_MODE_ENABLE;
	}

//---  Static Assignments   -------------------------------------------------------------------
	
	public static void assignAttributeReferences(String init, String cont, String obs, String bad) {
		initialRef = init;
		controllableRef = cont;
		badTransRef = bad;
		observableRef = obs;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public boolean decideCondition() throws Exception{
		//System.out.println("--- Deciding Coobservability ---");
		//System.out.println("--- Using: " + plants + ", " + specs + " ---");
		TransitionSystem use = specs == null ? plants.get(0) : deriveTruePlant();
		
		/*if(use.getTransitionsWithAttribute(badTransRef).size() == 0) {
			//System.out.println("Immediate bail");
			return true;
		}*/
		
		ustruct = new UStructure(use, attr, agents);
		ustruct.reserveTransitionSystem(hold);
		//ustruct.reserveTransitionSystem(ustruct.getUStructure());
		boolean out = decideResult();
		ustruct.assignTestResult(out);
		//if(!out)
			//System.out.println("--- " + use.getId() + " - Counterexamples: " + getCounterExamples().iterator().next());
		return out;
	}
	
	private boolean decideResult() {
		switch(enableDisableMode) {
			case 0: 
				return ustruct.getIllegalConfigOneStates().isEmpty();
			case 1: 
				return ustruct.getIllegalConfigTwoStates().isEmpty();
			default: 
				return ustruct.getIllegalConfigOneStates().isEmpty() && ustruct.getIllegalConfigTwoStates().isEmpty();
		}
	}

	@Override
	public DecideCondition constructDeciderCoobs(ArrayList<String> eventsIn, TransitionSystem specStart, ArrayList<String> attrIn, ArrayList<Agent> agentsIn) {
		return new DecideCoobs(eventsIn, specStart, attrIn, agentsIn);
	}

	@Override
	public HashSet<IllegalConfig> getCounterExamples() {
		return getEnableDisableModeCounterExamples();
	}
	
	private HashSet<IllegalConfig> getEnableDisableModeCounterExamples(){
		switch(enableDisableMode) {
		case 0:
			return ustruct.getIllegalConfigOneStates();
		case 1: 
			return ustruct.getIllegalConfigTwoStates();
		default: 
			HashSet<IllegalConfig> out = new HashSet<IllegalConfig>();
			out.addAll(ustruct.getIllegalConfigOneStates());
			out.addAll(ustruct.getIllegalConfigTwoStates());
			return out;
		}
	}

	@Override
	public void addComponent(TransitionSystem next, boolean plant) {
		if(plant) {
			if(plants == null) {
				plants = new ArrayList<TransitionSystem>();
			}
			plants.add(next);
		}
		else {
			specs.add(next);
		}
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
		
		for(String t : getRelevantEvents()) {
			out.addTransition(st, t, st);
		}
		
		return out;
	}
	
	private TransitionSystem generateSigmaStarion(TransitionSystem spec) {
		TransitionSystem sigmaStar = spec.copy();
		sigmaStar.setId("sigma_starion_" + spec.getId());
		
		for(String s : sigmaStar.getStateNames()) {
			for(String t : events) {
				sigmaStar.setEventAttribute(t, observableRef, true);
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

		TransitionSystem ultSpec = parallelCompSpecs();
		TransitionSystem ultPlant;
		
		if(plants == null) {
			ultPlant = generatePlantSigmaStarion(ultSpec);
		}
		else {
			ultPlant = parallelCompPlants();
		}
		
		//System.out.println(ultPlant.getStateNames().size() + " " + ultSpec.getStateNames().size());
		
		//System.out.println(parallelCompAutomata(ultPlant, ultSpec).getStateNames().size());
		
		ultPlant = parallelCompAutomata(ultPlant, makeSpecPrime(ultSpec));
		
		//System.out.println(ultPlant.getStateNames());
		
		//System.out.println(ultPlant.getStateNames().size());
		
		//hold = ultPlant.copy();
		
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
				//System.out.println(plantState + ", " + specState + " -> " + e + " " + ultPlant.getStateEventTransitionStates(plantState, e) + " " + ultSpec.getStateEventTransitionStates(specState, e));
				if(specState == null || (ultSpec.eventExists(e) && cantPerform(ultSpec, specState, e))) {	//Do we need to check for bad transitions behind this?
					ultPlant.setTransitionAttribute(plantState, e, badTransRef, true);
					//System.out.println("Bad: " + plantState + " " + e);
					
					// --- Experimental Solution ---
					String next = ultPlant.getStateEventTransitionStates(plantState, e).get(0);
					ultPlant.addTransition(plantState, e, "trash");
					ultPlant.removeTransition(plantState, e, next);
					//--- */
					
				}
				else {
					use = new String[] {ultPlant.getStateEventTransitionStates(plantState, e).get(0), ultSpec.eventExists(e) ? ultSpec.getStateEventTransitionStates(specState, e).get(0) : specState};
					//ultPlant.setTransitionAttribute(plantState, e, badTransRef, false);
					queue.add(new StateSet(use));
				}
			}
		}

		// --- Experimental Solution ---
		try {
			ultPlant = ProcessDES.makeAccessible(ultPlant);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// --- */

		//hold = ultPlant;

		//System.out.println(ultPlant.getStateNames());
		//System.out.println(ultPlant.getStateNames().size());
		
		ultSpec = null;
		
		return ultPlant;
	}
	
	private TransitionSystem makeSpecPrime(TransitionSystem in) {
		TransitionSystem out = in.copy();
		
		ArrayList<String> contr = new ArrayList<String>();
		
		for(String e : getRelevantEvents()) {
			for(Agent a : agents) {
				if(a.getEventAttribute(e, controllableRef)) {
					contr.add(e);
					break;
				}
			}
		}

		
		for(String s : out.getStateNames()) {
			for(String e : out.getEventNames()) {
				if(!out.hasTransition(s, e) && contr.contains(e))
					out.addTransition(s, e, "trash");
			}
		}
		
		return out;
	}
	
	private boolean cantPerform(TransitionSystem ultSpec, String specState, String e) {
		return ultSpec.getStateEventTransitionStates(specState, e) == null || ultSpec.getStateEventTransitionStates(specState, e).size() == 0;
	}

	private HashSet<String> getRelevantEvents(){
		HashSet<String> out = new HashSet<String>();
		
		if(plants != null)
			for(TransitionSystem t : plants) {
				out.addAll(t.getEventNames());
			}
		
		if(specs != null)
			for(TransitionSystem t : specs) {
				out.addAll(t.getEventNames());
			}
		
		return out;
	}
	
	/**
	 * 
	 * Checks if specifications share no events with the plants
	 * 
	 * @return
	 */
	
	private boolean isSpecEventNoPlant() {
		HashSet<String> use = new HashSet<String>();
		
		if(plants != null)
			for(TransitionSystem t : plants) {
				use.addAll(t.getEventNames());
			}
		
		if(specs != null)
			for(TransitionSystem t : specs) {
				for(String e : t.getEventNames()) {
					if(!use.contains(e)) {
						return true;
					}
				}
			}
		return false;
	}

	private TransitionSystem parallelCompSpecs() {
		return specs.size() == 1 ? specs.get(0) : ProcessDES.parallelComposition(specs);
	}
	
	private TransitionSystem parallelCompPlants() {
		TransitionSystem use = null;
		if(events != null && !isSpecEventNoPlant()) {
			//System.out.println("HERE: " + getRelevantEvents() + ", " + events);
			use = generatePlantSigmaStarion(plants.get(0));
			plants.add(use);
		}
		TransitionSystem hold = ProcessDES.parallelComposition(plants);
		if(use != null) {
			plants.remove(plants.size() - 1);
		}
		return hold;
	}
	
	private TransitionSystem parallelCompAutomata(TransitionSystem p1, TransitionSystem p2) {
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		use.add(p1);
		use.add(p2);
		return ProcessDES.parallelComposition(use);
	}
	
	private TransitionSystem performPermissiveUnion(TransitionSystem pl, TransitionSystem sp) {
		ArrayList<TransitionSystem> use = new ArrayList<TransitionSystem>();
		use.add(pl);
		use.add(sp);
		return ProcessDES.permissiveUnion(use);
	}

}
