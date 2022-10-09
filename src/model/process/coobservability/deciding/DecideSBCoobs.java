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
import model.process.memory.MemoryMeasure;

public class DecideSBCoobs implements DecideCondition{
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static String attributeObservableRef;

	private ArrayList<TransitionSystem> plants;
	
	private ArrayList<TransitionSystem> specs;
	
	private ArrayList<String> attributes;
	
	private ArrayList<Agent> agents;
	
	private StateBased sbStructure;
	
	private boolean pathKnowledge;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public DecideSBCoobs(boolean pathIn) {
		assignPathKnowledge(pathIn);
	}
	
	public DecideSBCoobs(ArrayList<String> events, TransitionSystem specStart, ArrayList<String> attr, ArrayList<Agent> agentsIn) {
		plants = new ArrayList<TransitionSystem>();
		plants.add(generateSigmaStarion(events, specStart));
		specs = new ArrayList<TransitionSystem>();
		specs.add(specStart);
		attributes = attr;
		agents = agentsIn;
		pathKnowledge = true;
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
	public boolean decideCondition() {
		sbStructure = new StateBased(plants, specs, attributes, agents, getPathKnowledge());
		return sbStructure.isSBCoobservable();
	}

	@Override
	public DecideCondition constructDeciderCoobs(ArrayList<String> events, TransitionSystem specStart, ArrayList<String> attr, ArrayList<Agent> agentsIn) {
		DecideSBCoobs out = new DecideSBCoobs(events, specStart, attr, agentsIn);
		assignPathKnowledge(getPathKnowledge());
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

	@Override
	public void replaceSigma(ArrayList<String> events) {
		plants.remove(0);
		plants.add(0, generateSigmaStarion(events, parallelComp(specs)));
	}
	
	private TransitionSystem generateSigmaStarion(ArrayList<String> events, TransitionSystem spec) {
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
		HashSet<IllegalConfig> out = new HashSet<IllegalConfig>();
		if(sbStructure == null) {
			return out;
		}
		for(StateSet s : sbStructure.getRemainingDisableStates()) {
			AgentStates aS = new AgentStates(s.getStates(), sbStructure.getStateSetPathMinusEvent(s));
			for(String t : sbStructure.getStateSetPathEvent(s)) {
				ArrayList<ArrayList<String>> agentViews = new ArrayList<ArrayList<String>>();
				for(int i = 1; i < agents.size(); i++) {
					agentViews.add(filterEventPath(aS.getEventPath(), t, agents.get(i)));
				}
				out.add(new IllegalConfig(aS, agentViews, t, false));		//TODO: the false here is PLACEHOLDER need to assign correct plant choice for behaviour
			}
		}
		return out;
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private ArrayList<String> filterEventPath(ArrayList<String> events, String contr, Agent age) {
		ArrayList<String> out = new ArrayList<String>();
		for(String s : events) {
			if(age.contains(s) && age.getEventAttribute(s, attributeObservableRef)) {
				out.add(s);
			}
		}
		if(age.contains(contr) && age.getEventAttribute(contr, attributeObservableRef)) {
			out.add(contr);
		}
		return out;
	}

	private TransitionSystem parallelComp(ArrayList<TransitionSystem> in) {
		return ProcessDES.parallelComposition(in);
	}

}
