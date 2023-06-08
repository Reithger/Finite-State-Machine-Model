package model.process.coobservability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import model.fsm.TransitionSystem;
import model.process.coobservability.deciding.DecideCondition;
import model.process.coobservability.support.Agent;
import model.process.coobservability.support.IllegalConfig;
import model.process.memory.IncrementalMemoryMeasure;

/**
 * 
 * TODO: Second-round selection for multiple contenders should be deterministic not random selection
 * 
 * @author SirBo
 *
 */

public class Incremental extends IncrementalMemoryMeasure {
	
//---  Constants   ----------------------------------------------------------------------------

	public final static int INCREMENTAL_A_BOTH = 0;
	public final static int INCREMENTAL_A_PLANTS = 1;
	public final static int INCREMENTAL_A_SPECS = 2;

	public final static int INCREMENTAL_B_RANDOM = 0;
	public final static int INCREMENTAL_B_SOONEST = 1;
	public final static int INCREMENTAL_B_LATEST = 2;
	public final static int INCREMENTAL_B_LOW_STATE = 3;
	public final static int INCREMENTAL_B_HIGH_STATE = 4;
	public final static int INCREMENTAL_B_LOW_EVENTS = 5;
	public final static int INCREMENTAL_B_HIGH_EVENTS = 6;
	public final static int INCREMENTAL_B_LOW_TRANS = 7;
	public final static int INCREMENTAL_B_HIGH_TRANS = 8;
	public final static int INCREMENTAL_B_SHARE_EVENTS = 9;
	public final static int INCREMENTAL_B_DIFF_EVENTS = 10;
	
	public final static int[] INCREMENTAL_B_NO_REJECT = new int[] {INCREMENTAL_B_RANDOM,
																   INCREMENTAL_B_LOW_STATE,
																   INCREMENTAL_B_HIGH_STATE,
																   INCREMENTAL_B_LOW_EVENTS,
																   INCREMENTAL_B_HIGH_EVENTS,
																   INCREMENTAL_B_LOW_TRANS,
																   INCREMENTAL_B_HIGH_TRANS};

	public final static int COUNTEREXAMPLE_RANDOM = 0;
	public final static int COUNTEREXAMPLE_SHORT = 1;
	public final static int COUNTEREXAMPLE_LONG = 2;
	public final static int COUNTEREXAMPLE_FEWEST_EVENTS = 3;
	public final static int COUNTEREXAMPLE_MOST_EVENTS = 4;

	public final static int NUM_A_HEURISTICS = 3;
	public final static int NUM_B_HEURISTICS = 11;
	public final static int NUM_C_HEURISTICS = 5;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static int incrementalOptionA;
	private static int incrementalOptionB;
	private static int counterexampleChoice;
	
	private static String observableRef;
	private static String initialRef;
	private static String badRef;
	
	private DecideCondition decider;
	
//---  Static Assignments   -------------------------------------------------------------------
	
	public static void assignIncrementalOptions(int a, int b, int c) {
		incrementalOptionA = a;
		incrementalOptionB = b;
		counterexampleChoice = c;
	}
	
	public static void assignAttributeReference(String obs, String init, String bad) {
		observableRef = obs;
		initialRef = init;
		badRef = bad;
	}
	
	public static int[] retrieveIncrementalOptions() {
		return new int[] {incrementalOptionA, incrementalOptionB, counterexampleChoice};
	}
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Incremental(DecideCondition dC) {
		decider = dC;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public boolean decideIncrementalCondition(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, ArrayList<String> attr, ArrayList<Agent> agents) throws Exception{
		ArrayList<TransitionSystem> copyPlants = new ArrayList<TransitionSystem>();
		ArrayList<TransitionSystem> copySpecs = new ArrayList<TransitionSystem>();
		copyPlants.addAll(plants);
		copySpecs.addAll(specs);

		logHeuristics(retrieveIncrementalOptions());
		
		while(!copySpecs.isEmpty()) {
			TransitionSystem pick = pickComponent(null, copySpecs, null);								//Get initial spec to use (heuristics choose here)
			ArrayList<TransitionSystem> hold = new ArrayList<TransitionSystem>();		//List to hold all the plants/specs used in the current iteration
			copySpecs.remove(pick);
			hold.add(pick);
			decider = decider.constructDeciderCoobs(getAllEvents(plants), pick, attr, agents);
			logMemoryUsage();
			while(!decider.decideCondition()) {
				//System.out.println("Loop start");
				if(copyPlants.isEmpty() && copySpecs.isEmpty()) {
					logData(decider, hold);
					reserveTransitionSystem(decider.produceMemoryMeasure().getReserveSystem());
					return false;
				}
				logMemoryUsage();
				IllegalConfig counterexample = pickCounterExample(decider.getCounterExamples());	//Get a single bad state, probably, maybe write something so UStruct can trace it
				//System.out.println(hold);
				//System.out.println(counterexample);
				pick = pickComponent(copyPlants, copySpecs, counterexample);	//Heuristics go here
				//System.out.println(pick);
				if(pick == null) {
					logData(decider, hold);
					reserveTransitionSystem(decider.produceMemoryMeasure().getReserveSystem());
					return false;
				}
				if(copySpecs.contains(pick)) {
					hold.add(pick);
					copySpecs.remove(pick);
					decider.addComponent(pick, false);
				}
				else {
					hold.add(pick);
					copyPlants.remove(pick);
					decider.addComponent(pick, true);
				}
				//System.out.println("Loop End");
			}
			//System.out.println("Subsystem terminated");
			copyPlants.addAll(hold);
			logData(decider, hold);
		}
		reserveTransitionSystem(decider.produceMemoryMeasure().getReserveSystem());
		assignTestResult(true);
		return true;
	}
	
	private void logData(DecideCondition dec, ArrayList<TransitionSystem> hold) {
		logFinishedProcess(decider.produceMemoryMeasure());
		logFinishedComponents(hold.size());
		logComponentNames(getComponentNames(hold));
	}
	
	/**
	 * 
	 * Can probably introduce heuristics for choosing the counterexample? Shortest length, fewest unique events, etc.?
	 * 
	 * Counterexample only counts if it's in both lists of illegalconfig
	 * 
	 * @param ustruct
	 * @param enableByDefault
	 * @return
	 */
	
	private IllegalConfig pickCounterExample(HashSet<IllegalConfig> counters) {
		if(counters == null) {
			return null;
		}
		IllegalConfig out = null;
		//System.out.println("---COUNTEREXAMPLES: " + counters);
		switch(counterexampleChoice) {
			case COUNTEREXAMPLE_SHORT:
				for(IllegalConfig c : counters) {
					if(out == null || c.getEventPathLength() < out.getEventPathLength()) {
						out = c;
					}
				}
				return out;
			case COUNTEREXAMPLE_LONG:
				for(IllegalConfig c : counters) {
					if(out == null || c.getEventPathLength() > out.getEventPathLength()) {
						out = c;
					}
				}
				return out;
			case COUNTEREXAMPLE_FEWEST_EVENTS:
				for(IllegalConfig c : counters) {
					if(out == null || c.getNumberDistinctEvents() > out.getNumberDistinctEvents()) {
						out = c;
					}
				}
				return out;
			case COUNTEREXAMPLE_MOST_EVENTS:
				for(IllegalConfig c : counters) {
					if(out == null || c.getNumberDistinctEvents() < out.getNumberDistinctEvents()) {
						out = c;
					}
				}
				return out;
			case COUNTEREXAMPLE_RANDOM:
				Random rand = new Random();
				int pos = rand.nextInt(counters.size());
				Iterator<IllegalConfig> i = counters.iterator();
				while(--pos != -1) {
					out = i.next();
				}
				return out;
			default:
				return null;
		}
	}
	
	/**
	 * 
	 * Heuristics:
	 *  - Choice A
	 *    - Always choose plant over spec
	 *    - Always choose spec over plant
	 *    - Randomly choose
	 *  - Choice B
	 *    - Choose a component that rejects the counterexample the 'soonest'
	 *    - Choose a component that rejects the counterexample the 'latest'
	 *    - Choose a component with the fewest states
	 *    - Choose a component with the fewest transitions
	 *    - Choose a component that shares the most events with the current plant
	 *    - Choose a component with the fewest events 
	 * 
	 * TODO: Needs some way to know that a component can even reject the counterexample in the first place, how to do quickly?
	 *     - Can get the true event path that led to a problem scenario, now how to handle guessing and populating our agents?
	 * 
	 * @param plants
	 * @param specs
	 * @param counterexample
	 * @return
	 */
	
	private TransitionSystem pickComponent(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, IllegalConfig counterexample) {
		ArrayList<TransitionSystem> selectionPool = new ArrayList<TransitionSystem>();
		
		int use = plants == null ? INCREMENTAL_A_SPECS : incrementalOptionA;
		
		switch(use) {
			case INCREMENTAL_A_PLANTS:
				if(plants.size() != 0)
					selectionPool.addAll(plants);
				else 
					selectionPool.addAll(specs);
				break;
			case INCREMENTAL_A_SPECS:
				if(specs.size() != 0)
					selectionPool.addAll(specs);
				else
					selectionPool.addAll(plants);
				break;
			case INCREMENTAL_A_BOTH:
				selectionPool.addAll(plants);
				selectionPool.addAll(specs);
				break;
			default:
				break;
		}
		
		if(selectionPool.size() == 0) {
			return null;
		}
		
		TransitionSystem out = null;
		
		use = counterexample == null ? pickComponentHeuristicNoReject(incrementalOptionB) : incrementalOptionB;
		
		switch(use) {
			case INCREMENTAL_B_SOONEST:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || observablePath(ts, counterexample.getEventPath()).size() < observablePath(out, counterexample.getEventPath()).size()){
							out = ts;
						}
					}
				}
				break;
			case INCREMENTAL_B_LATEST:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || observablePath(ts, counterexample.getEventPath()).size() > observablePath(out, counterexample.getEventPath()).size()){
							out = ts;
						}
					}
				}
				break;
			case INCREMENTAL_B_LOW_STATE:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || ts.getStateNames().size() < out.getStateNames().size()){
							out = ts;
						}
					}
				}
				break;
			case INCREMENTAL_B_HIGH_STATE:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || ts.getStateNames().size() > out.getStateNames().size()){
							out = ts;
						}
					}
				}
				break;
			case INCREMENTAL_B_LOW_TRANS:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || countTransitions(ts) < countTransitions(out)){
							out = ts;
						}
					}
				}
				break;
			case INCREMENTAL_B_HIGH_TRANS:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || countTransitions(ts) > countTransitions(out)){
							out = ts;
						}
					}
				}
				break;
			case INCREMENTAL_B_LOW_EVENTS:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || ts.getEventNames().size() < out.getEventNames().size()){
							out = ts;
						}
					}
				}
				break;
			case INCREMENTAL_B_HIGH_EVENTS:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || ts.getEventNames().size() > out.getEventNames().size()){
							out = ts;
						}
					}
				}
				break;
			case INCREMENTAL_B_SHARE_EVENTS:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || sharedEvents(ts, counterexample.getEventPath()) < sharedEvents(out, counterexample.getEventPath())){
							out = ts;
						}
					}
				}
				break;
			case INCREMENTAL_B_DIFF_EVENTS:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || sharedEvents(ts, counterexample.getEventPath()) > sharedEvents(out, counterexample.getEventPath())){
							out = ts;
						}
					}
				}
				break;
			case INCREMENTAL_B_RANDOM:
				ArrayList<TransitionSystem> pool = new ArrayList<TransitionSystem>();
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						pool.add(ts);
					}
				}
				Random rand = new Random();
				if(pool.size() != 0) {
					out = pool.get(rand.nextInt(pool.size()));
				}
				break;
		}
		
		if (out == null && incrementalOptionA != 0){
			int[] hold = getIncrementalSettings();
			assignIncrementalOptions(0, incrementalOptionB, counterexampleChoice);
			out = pickComponent(plants, specs, counterexample);
			assignIncrementalOptions(hold[0], hold[1], hold[2]);
			return out;
		}
		
		return out;
	}

	private int pickComponentHeuristicNoReject(int in) {
		HashSet<Integer> check = new HashSet<Integer>();
		for (int i : INCREMENTAL_B_NO_REJECT)
			check.add(i);
		return check.contains(in) ? in : INCREMENTAL_B_RANDOM;
	}
	
//---  Support Methods   ----------------------------------------------------------------------

	private int[] getIncrementalSettings() {
		int[] out = new int[3];
		out[0] = incrementalOptionA;
		out[1] = incrementalOptionB;
		out[2] = counterexampleChoice;
		return out;
	}
	
	private ArrayList<String> getComponentNames(ArrayList<TransitionSystem> components){
		ArrayList<String> out = new ArrayList<String>();
		for(TransitionSystem t : components) {
			out.add(t.getId());
		}
		return out;
	}
	
	private ArrayList<String> getAllEvents(ArrayList<TransitionSystem> plants){
		HashSet<String> hold = new HashSet<String>();
		
		for(TransitionSystem t : plants) {
			hold.addAll(t.getEventNames());
		}
		
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(hold);
		return out;
	}

	private int countTransitions(TransitionSystem plant) {
		int out = 0;
		for(String s : plant.getStateNames()) {
			for(String e : plant.getStateTransitionEvents(s)) {
				out += plant.getStateEventTransitionStates(s, e).size();
			}
		}
		return out;
	}
	
	private int sharedEvents(TransitionSystem plant, ArrayList<String> eventPath) {
		HashSet<String> events = new HashSet<String>();
		for(String s : eventPath) {
			events.add(s);
		}
		int out = 0;
		for(String s : events) {
			if(plant.getEventNames().contains(s)) {
				out++;
			}
		}
		return out;
	}
	
	private boolean canReject(TransitionSystem plant, boolean spec, IllegalConfig ic) {
		if(ic == null) {
			return true;
		}
		ArrayList<String> use = ic.getEventPath();
		if(!spec) {
			use.add(ic.getEvent());
		}
		String reachedState = navigateTransitionSystem(plant, observablePath(plant, use));
		//System.out.println("States: " + ic.getStateSet());
		//System.out.println("For: " + ic.getEventPath() + " " +  ic.getEvent() + ", " + plant.getId() + " reached: " + reachedState + " from start: " + plant.getStatesWithAttribute(initialRef).get(0) + ", knowing: " + plant.getEventNames());
		if(reachedState != null) {
			for(ArrayList<String> s : ic.getObservedPaths()) {
				use = s;
				use.add(ic.getEvent());
				//System.out.println("Agent View: " + use);
				reachedState = navigateTransitionSystem(plant, observablePath(plant, use));
				if(reachedState == null) {
					return true;
				}
			}
		}
		else {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * How to model guessing? How do we define rejection if we have to include it to know that it removed it properly, and
	 * how can we check that the counterexample is gone when the means by which we identify it will change once a new component
	 * is added?
	 * 
	 * And if there is an event in the eventPath that this plant/spec knows about and can see which it cannot perform while tracing
	 * the eventPath, does that mean it rejects the counterexample by writ of blocking the progression that would have led to that
	 * error?
	 * 
	 * Are making the right control decision and stopping the eventPath from happening both examples of rejecting the counterexample?
	 * 
	 * @param plant
	 * @param eventPath
	 * @return
	 */
	
	private String navigateTransitionSystem(TransitionSystem plant, ArrayList<String> eventPath) {
		String curr = plant.getStatesWithAttribute(initialRef).get(0);
		//System.out.println("Path: " + eventPath);
		for(String s : eventPath) {
			//System.out.println("Knows event " + s + "? " + plant.getEventNames().contains(s) + ", Sees it: " + plant.getEventsWithAttribute(observableRef).contains(s));
			if(plant.getEventNames().contains(s)) {
				ArrayList<String> next = plant.getStateEventTransitionStates(curr, s);
				//System.out.println("H: " + curr + ", " + s + ", " + next);
				if(next != null && next.size() > 0) {
					curr = next.get(0);
				}
				else {
					return null;
				}
			}
		}
		return curr;
	}
	
	/**
	 * 
	 * Function to filter an eventPath to only the events that are relevant to the transition system
	 * 
	 * @param plant
	 * @param eventPath
	 * @return
	 */
	
	private ArrayList<String> observablePath(TransitionSystem plant, ArrayList<String> eventPath) {
		ArrayList<String> out = new ArrayList<String>();
		for(String s : eventPath) {
			if(plant.getEventNames().contains(s) && plant.getEventAttribute(s, observableRef)) {
				out.add(s);
			}
		}
		return out;
	}

}
