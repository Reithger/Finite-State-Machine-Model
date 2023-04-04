package model.process.coobservability.deciding;

import java.util.ArrayList;
import java.util.HashSet;

import model.fsm.TransitionSystem;
import model.process.ProcessDES;
import model.process.coobservability.StateBased;
import model.process.coobservability.support.Agent;
import model.process.coobservability.support.AgentStates;
import model.process.coobservability.support.IllegalConfig;
import model.process.coobservability.support.StateSet;
import model.process.coobservability.support.StateSetPath;
import model.process.memory.MemoryMeasure;

public class DecideSBCoobs implements DecideCondition{
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static String attributeObservableRef;
	
	private ArrayList<TransitionSystem> plants;
	
	private ArrayList<TransitionSystem> specs;
	
	private ArrayList<String> attributes;
	
	private ArrayList<Agent> agents;
	
	private HashSet<String> events;
	
	private StateBased sbStructure;
	
	private boolean pathKnowledge;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public DecideSBCoobs(boolean pathIn) {
		assignPathKnowledge(pathIn);
	}
	
	public DecideSBCoobs(ArrayList<String> eventsIn, TransitionSystem specStart, ArrayList<String> attr, ArrayList<Agent> agentsIn) {
		events = new HashSet<String>();
		events.addAll(eventsIn);
		plants = new ArrayList<TransitionSystem>();
		plants.add(generateSigmaStarion(specStart));
		specs = new ArrayList<TransitionSystem>();
		specs.add(specStart);
		attributes = attr;
		agents = agentsIn;
	}
	
	public DecideSBCoobs(ArrayList<TransitionSystem> inPlants, ArrayList<TransitionSystem> inSpecs, ArrayList<String> attrIn, ArrayList<Agent> agentsIn) {
		plants = inPlants;
		specs = inSpecs;
		attributes = attrIn;
		agents = agentsIn;
	}
	
//---  Static Assignments   -------------------------------------------------------------------
	
	public static void assignAttributeReferences(String obs) {
		attributeObservableRef = obs;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public boolean decideCondition() throws Exception{
		sbStructure = new StateBased(plants, specs, attributes, agents, getPathKnowledge());
		boolean out = sbStructure.isSBCoobservable();
		sbStructure.assignTestResult(out);
		return out;
	}

	@Override
	public DecideCondition constructDeciderCoobs(ArrayList<String> events, TransitionSystem specStart, ArrayList<String> attr, ArrayList<Agent> agentsIn) {
		DecideSBCoobs out = new DecideSBCoobs(events, specStart, attr, agentsIn);
		out.assignPathKnowledge(getPathKnowledge());
		return out;
	}

	@Override
	public void addComponent(TransitionSystem next, boolean plant) {
		if(plant) {
			plants.add(next);
		}
		else {
			specs.add(next);
		}
	}
	
	@Override
	public MemoryMeasure produceMemoryMeasure() {
		return sbStructure == null ? StateBased.produceBlank() : sbStructure;
	}

	private TransitionSystem generateSigmaStarion(TransitionSystem spec) {
		TransitionSystem sigmaStar = spec.copy();
		sigmaStar.setId("sigma_starion_" + spec.getId());
		
		for(String s : sigmaStar.getStateNames()) {
			for(String t : events) {
				sigmaStar.setEventAttribute(t, attributeObservableRef, true);
				if(!sigmaStar.getStateTransitionEvents(s).contains(t)) {
					sigmaStar.addTransition(s, t, s);
				}
			}
		}

		return sigmaStar;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	protected void assignPathKnowledge(boolean in) {
		pathKnowledge = in;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	private boolean getPathKnowledge() {
		return pathKnowledge;
	}
	
	@Override
	public HashSet<IllegalConfig> getCounterExamples() {
		
		return null;
		/*
		HashSet<IllegalConfig> out = new HashSet<IllegalConfig>();
		if(sbStructure == null) {
			return out;
		}
		for(StateSet s : sbStructure.getRemainingDisableStates()) {
			AgentStates aS = new AgentStates(s.getStates(), sbStructure.getStateSetPath(s));
			for(String t : sbStructure.getStateSetPathEvents(s)) {
				getSequences(0, aS, sbStructure.getEquivalentPaths(s), new ArrayList<ArrayList<String>>(), t, out);
			}
		}
		System.out.println("~~~\n~~~\n" + out);
		return out;
		*/
	}

//---  Support Methods   ----------------------------------------------------------------------
	
	private void getSequences(int index, AgentStates aS, ArrayList<ArrayList<StateSet>> paths, ArrayList<ArrayList<String>> use, String s, HashSet<IllegalConfig> out){
		if(index >= paths.size()) {
			out.add(new IllegalConfig(aS, copy(use), s));
		}
		else {
			for(StateSet st : paths.get(index)) {
				//use.add(st.getEventPath());
				getSequences(index + 1, aS, paths, use, s, out);
				use.remove(use.size() - 1);
			}
		}
	}
	
	private ArrayList<ArrayList<String>> copy(ArrayList<ArrayList<String>> in){
		ArrayList<ArrayList<String>> out = new ArrayList<ArrayList<String>>();
		for(ArrayList<String> t : in) {
			ArrayList<String> use = new ArrayList<String>();
			for(String s : t) {
				use.add(s);
			}
			out.add(use);
		}
		return out;
	}

}
