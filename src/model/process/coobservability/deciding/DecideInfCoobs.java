package model.process.coobservability.deciding;

import java.util.ArrayList;

import model.fsm.TransitionSystem;
import model.process.coobservability.support.Agent;

public class DecideInfCoobs extends DecideCoobs{

	public DecideInfCoobs(ArrayList<TransitionSystem> inPlan, ArrayList<TransitionSystem> inSpe, ArrayList<String> attrIn, ArrayList<Agent> agentsIn) {
		super(inPlan, inSpe, attrIn, agentsIn);
	}
	
	public DecideInfCoobs(ArrayList<String> events, TransitionSystem specStart, ArrayList<String> attrIn, ArrayList<Agent> agentsIn) {
		super(events, specStart, attrIn, agentsIn);
	}
	
	public DecideInfCoobs(TransitionSystem root, ArrayList<String> attr, ArrayList<Agent> in) {
		super(root, attr, in);
	}
	
	public DecideInfCoobs() {
		
	}
	
	@Override
	public boolean decideCondition() throws Exception{
		return super.decideCondition() ? true : ustruct.getFilteredIllegalConfigStates().isEmpty();
	}

	@Override
	public DecideCondition constructDeciderCoobs(ArrayList<String> events, TransitionSystem specStart, ArrayList<String> attr, ArrayList<Agent> agents) {
		return new DecideInfCoobs(events, specStart, attr, agents);
	}

}
