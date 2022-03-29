package model.process.coobservability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import model.fsm.TransitionSystem;
import model.process.coobservability.support.IllegalConfig;

public class Incremental {
	
//---  Constants   ----------------------------------------------------------------------------
	
	public final static int INCREMENTAL_A_PLANTS = 0;
	public final static int INCREMENTAL_A_SPECS = 1;
	public final static int INCREMENTAL_A_BOTH = 2;
	
	public final static int INCREMENTAL_B_SOONEST = 0;
	public final static int INCREMENTAL_B_LATEST = 1;
	public final static int INCREMENTAL_B_LOW_STATE = 2;
	public final static int INCREMENTAL_B_LOW_TRANS = 3;
	public final static int INCREMENTAL_B_LOW_EVENTS = 4;
	public final static int INCREMENTAL_B_SHARE_EVENTS = 5;
	public final static int INCREMENTAL_B_RANDOM = 6;
	
	public final static int COUNTEREXAMPLE_SHORT = 0;
	public final static int COUNTEREXAMPLE_LONG = 1;
	public final static int COUNTEREXAMPLE_FEWEST_EVENTS = 2;
	public final static int COUNTEREXAMPLE_MOST_EVENTS = 3;
	public final static int COUNTEREXAMPLE_RANDOM = 4;

//---  Instance Variables   -------------------------------------------------------------------
	
	private static int incrementalOptionA;
	private static int incrementalOptionB;
	private static int counterexampleChoice;
	
	private static String observableRef;
	private static String initialRef;
	
//---  Static Assignments   -------------------------------------------------------------------
	
	public static void assignIncrementalOptions(int a, int b, int c) {
		incrementalOptionA = a;
		incrementalOptionB = b;
		counterexampleChoice = c;
	}
	
	public static void assignAttributeReference(String obs, String init) {
		observableRef = obs;
		initialRef = init;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static TransitionSystem generateSigmaStarion(ArrayList<TransitionSystem> plants) {
		TransitionSystem sigmaStar = new TransitionSystem("sigmaStarion");
		sigmaStar.copyAttributes(plants.get(0));
		String init = "0";
		sigmaStar.addState(init);
		sigmaStar.setStateAttribute(init, initialRef, true);
		for(String e : getAllEvents(plants)) {
			for(TransitionSystem t : plants) {
				if(t.eventExists(e)) {
					sigmaStar.addEvent(e, t);
					sigmaStar.addTransition(init, e, init);
					break;
				}
			}
		}
		return sigmaStar;
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
	
	public static IllegalConfig pickCounterExample(HashSet<IllegalConfig> counters) {
		IllegalConfig out = null;
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
				while(pos != 0) {
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
	
	public static TransitionSystem pickComponent(ArrayList<TransitionSystem> plants, ArrayList<TransitionSystem> specs, IllegalConfig counterexample) {
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
		
		use = counterexample == null ? INCREMENTAL_B_RANDOM : incrementalOptionB;
		
		switch(use) {
			case INCREMENTAL_B_SOONEST:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || observablePath(ts, counterexample.getEventPath()).length() < observablePath(out, counterexample.getEventPath()).length()){
							out = ts;
						}
					}
				}
				break;
			case INCREMENTAL_B_LATEST:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || observablePath(ts, counterexample.getEventPath()).length() > observablePath(out, counterexample.getEventPath()).length()){
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
			case INCREMENTAL_B_LOW_TRANS:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || countTransitions(ts) < countTransitions(out)){
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
			case INCREMENTAL_B_SHARE_EVENTS:
				for(TransitionSystem ts : selectionPool) {
					if(canReject(ts, specs.contains(ts), counterexample)) {
						if(out == null || sharedEvents(ts, counterexample.getEventPath()) < sharedEvents(out, counterexample.getEventPath())){
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
				out = pool.get(rand.nextInt(pool.size()));
				break;
		}
		
		return out;
	}

//---  Support Methods   ----------------------------------------------------------------------
	
	private static ArrayList<String> getAllEvents(ArrayList<TransitionSystem> plants){
		HashSet<String> hold = new HashSet<String>();
		
		for(TransitionSystem t : plants) {
			hold.addAll(t.getEventNames());
		}
		
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(hold);
		return out;
	}

	private static int countTransitions(TransitionSystem plant) {
		int out = 0;
		for(String s : plant.getStateNames()) {
			for(String e : plant.getStateTransitionEvents(s)) {
				out += plant.getStateEventTransitionStates(s, e).size();
			}
		}
		return out;
	}
	
	private static int sharedEvents(TransitionSystem plant, String eventPath) {
		HashSet<String> events = new HashSet<String>();
		for(String s : eventPath.split("")) {
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
	
	private static boolean canReject(TransitionSystem plant, boolean spec, IllegalConfig ic) {
		if(ic == null) {
			return true;
		}
		String reachedState = navigateTransitionSystem(plant, observablePath(plant, ic.getEventPath() + (spec ? "" : ic.getEvent())));
		if(reachedState != null) {
			for(String s : ic.getObservedPaths()) {
				if(navigateTransitionSystem(plant, observablePath(plant, s + ic.getEvent())) == null) {
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
	
	private static String navigateTransitionSystem(TransitionSystem plant, String eventPath) {
		String curr = plant.getStatesWithAttribute(initialRef).get(0);
		for(String s : eventPath.split("")) {
			if(plant.getEventNames().contains(s) && plant.getEventsWithAttribute(observableRef).contains(s)) {
				ArrayList<String> next = plant.getStateEventTransitionStates(curr, s);
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
	
	private static String observablePath(TransitionSystem plant, String eventPath) {
		String out = "";
		for(char s : eventPath.toCharArray()) {
			if(plant.getEventNames().contains(s+"") && plant.getEventAttribute(s+"", observableRef)) {
				out += s;
			}
		}
		return out;
	}

}
