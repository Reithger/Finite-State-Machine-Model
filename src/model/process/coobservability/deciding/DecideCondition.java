package model.process.coobservability.deciding;

import java.util.ArrayList;
import java.util.HashSet;

import model.fsm.TransitionSystem;
import model.process.coobservability.support.Agent;
import model.process.coobservability.support.IllegalConfig;
import model.process.memory.MemoryMeasure;

public interface DecideCondition {
	
	public abstract boolean decideCondition() throws Exception;
	
	public abstract DecideCondition constructDeciderCoobs(ArrayList<String> events, TransitionSystem specStart, ArrayList<String> attr, ArrayList<Agent> agents);
	
	public abstract HashSet<IllegalConfig> getCounterExamples();
	
	public abstract void addComponent(TransitionSystem next, boolean plant);
	
	public abstract MemoryMeasure produceMemoryMeasure();
	
}
