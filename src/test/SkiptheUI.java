package test;

import java.util.ArrayList;
import java.util.HashMap;

import controller.FiniteStateMachine;
import controller.convert.FormatConversion;
import input.CustomEventReceiver;
import model.AttributeList;
import model.Manager;
import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

/**
 * 
 *  - Hi! Ada here, letting you know how to use this so you can easily make your UMLs and whatnot; if anything is weird or you'd like
 *  something added, feel free to let me know! For the folks this is intended for, they oughta be able to get in contact with me so
 *  I'm not gonna put my contact info here.
 *  
 *  - This is thrown together for a specific expected use case and is not representative of best practices for programming.
 *  
 *  - Creating the U-Structure involves a few things; you need to make the Plant UML and define your agents' observability/controllability.
 *  This kind of involves a lot, but for the most part you do the set-up once and most of the changes you need to make aren't a part of that.
 * 
 *  - A lot of stuff in here is template, feel free to look at the "TestFunctionality" file in the same test package for a real use-case; the
 *  format there is different cause I wanted to make your environment for this a bit smoother/nicer, feel free to change anything you like for
 *  how you like to work.
 *  
 *  - If something goes wrong, check the consistency between added Events and the Agent boolean[][][], that usually messes something up.
 * 
 *  - Gonna be adding the auto-marked type-1/type-2 bad state visualization at some point, wanted to get it as-is to you ASAP
 * 
 * @author Ada Clevinger
 *
 */

public class SkiptheUI {

//---  Constants   ----------------------------------------------------------------------------
	
	private static final String PLANT_NAME = "Plant";
	
	private static final String[] EVENT_LIST = new String[] {"a", "b", "c"};
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static Manager model;
	
	private static ArrayList<HashMap<String, ArrayList<Boolean>>> agents;
	
	private static ArrayList<String> eventAtt;
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * 
	 * Besides maybe not wanting to make the image of our FSM due to inefficiency reasons, probably don't
	 * need to mess with anything in here.
	 * 
	 * Just manages the construction flow to get things running.
	 * 
	 * @param args
	 */
	
	public static void main(String[] args) { 
		//Backend stuff to let us generate our FSM Images, should prompt to set up GraphViz location as that makes our graphs.
		FormatConversion.assignPaths(FiniteStateMachine.ADDRESS_IMAGES, FiniteStateMachine.ADDRESS_CONFIG);
		FiniteStateMachine.fileConfiguration();
		
		//Instantiate our Model so we can use all its automated fun stuff to make it easier on us
		model = new Manager();
		
		//Gives us a new FSM to manipulate
		model.generateEmptyFSM(PLANT_NAME);
		
		//Basic setup of the base Plant FSM
		setupStates();
		setupEvents();
		setupTransitions();
		
		//Makes a visual display of our FSM that is interactable (zooming in/out is kinda inefficient, I am trying to fix it)
		makeImageDisplay(PLANT_NAME);
		
		//For the U-Structure, we need our agents!
		setupAgents();
		
		//Makes the visual display of the U-Structure built from our Plant FSM
		//NOTE: Making the view of the U-Structure can take a lot of time, becomes impossible so maybe comment this out.
		//They can also be pretty big, may need to scroll around a bit to "find" it as it draws the image at the (0, 0) origin in the corner
		makeImageDisplay(model.buildUStructure(PLANT_NAME, eventAtt, agents));
		
		//There's information we can get from the U-Structure that's a bit bugged at the moment, will do my best to get it working soon.
		//At the very least, oughta give you a nice image to work with and speed up image construction (the image is saved to your system btw)
	}
	
//---  FSM Setup   ----------------------------------------------------------------------------
	
	/**
	 * 
	 *  - Function to put all State manipulations in one place for convenience.
	 *    - This involves adjusting the relevant Attributes for the FSM in question
	 *    and assigning particular boolean values for those attributes to each state
	 *    
	 *  - Must assign an Attribute to be able to assign it to one of the states
	 *    - There is a file "AttributeList" in the project that has the full list,
	 *    make sure to reference by the constant for consistency with the backend.
	 *    Relevant Attributes should be present by default, but feel free to add any
	 *    if you want to.
	 *    - model.assignStateAttributes(String::FSM_NAME, ArrayList<String>::ATTRIBUTE_LIST);
	 *    
	 *  - Can add states by specific name one at a time 
	 *    - model.addState(String::FSM_NAME, String::STATE_NAME);
	 *    
	 *  - Can add some number of states at a time using an ascending
	 * numeric pattern (add 5, will make states (0, 1, 2, 3, 4)).
	 * If a state in that list already exists, it will skip it but still add the
	 * desired number of states (if there were already state "3", then it would also
	 * add a state "5" in the prior example).
	 *    - model.addStates(String::FSM_NAME, int::NUMBER_STATES_ADD);
	 * 
	 *  - Can modify an attribute that has been assigned to the FSM for any state
	 *    - model.setStateAttribute(String::FSM_NAME, String::STATE_NAME, String::ATTRIBUTE_NAME, boolean::ASSIGNED_VALUE);
	 */
	
	private static void setupStates() {
		ArrayList<String> stateAtt = new ArrayList<String>();
		stateAtt.add(AttributeList.ATTRIBUTE_INITIAL);
		model.assignStateAttributes(PLANT_NAME, stateAtt);
		
		//Add a single state with a given name
		model.addState(PLANT_NAME, "state_name");
		
		
		//Add a bundle of states in ascending order numerically (0, 1, 2, 3, 4, ...)
		model.addStates(PLANT_NAME, 5);
		
		//Assign to a specific state a set boolean value for its status regarding a particular attribute
		//By default, without any assignment, all attributes are false for a new State
		model.setStateAttribute(PLANT_NAME, "state_name", AttributeList.ATTRIBUTE_INITIAL, true);
		model.setStateAttribute(PLANT_NAME, "0", AttributeList.ATTRIBUTE_INITIAL, true);
		
		//Strictly speaking you can also remove them but why
		model.removeState(PLANT_NAME, "state_name");
	}
	
	/**
	 * 
	 *  - Function to put all Event manipulations in one place for convenience.
	 *    - This involves adjusting the relevant Attributes for the FSM in question
	 *    and assigning particular boolean values for those attributes to each event
	 *    
	 *  - Must assign an Attribute to be able to assign it to one of the events
	 *    - There is a file "AttributeList" in the project that has the full list,
	 *    make sure to reference by the constant for consistency with the backend.
	 *    Relevant Attributes should be present by default, but feel free to add any
	 *    if you want to.
	 *    - model.assignEventAttributes(String::FSM_NAME, ArrayList<String>::ATTRIBUTE_LIST);
	 *    
	 *   - It's conceptually similar to States so should make sense, yell at me if it doesn't
	 * 
	 */
	
	private static void setupEvents() {
		eventAtt = new ArrayList<String>();
		eventAtt.add(AttributeList.ATTRIBUTE_OBSERVABLE);
		eventAtt.add(AttributeList.ATTRIBUTE_CONTROLLABLE);
		model.assignEventAttributes(PLANT_NAME, eventAtt);
		
		//Add a single event to the FSM
		model.addEvent(PLANT_NAME, "event_name");
		
		//Assign a boolean value for an attribute for a chosen event
		model.setEventAttribute(PLANT_NAME, "event_name", AttributeList.ATTRIBUTE_OBSERVABLE, true);
		
		//And can remove for some reason
		model.removeEvent(PLANT_NAME, "event_name");
	
		/* 
		 * May be convenient to list information as an array to loop through for some default options,
		 * I've made a constant String array you can mess with for consistency in later parts of the workings.
		 */
		
		for(String s : EVENT_LIST) {
			model.addEvent(PLANT_NAME, s);
			model.setEventAttribute(PLANT_NAME, s, AttributeList.ATTRIBUTE_OBSERVABLE, true);
		}
	}
	
	/**
	 * 
	 * - Same deal as before for Attributes
	 * 
	 * - Transitions are a bit more funky, it's defined as "Start State", "Event", "End State"
	 * in that order
	 *   - model.addTransitions(STRING::FSM_NAME, STRING::START_STATE_NAME, STRING::EVENT_NAME, STRING::END_STATE_NAME);
	 * 
	 * - Assigning attributes doesn't require the "End State" because in non-determinism you can't really distinguish
	 * a property for a transition that leads to a different state than another transition by the same event; it's weird
	 *   - model.setTransitionAttribute(String::FSM_NAME, String::START_STATE_NAME, String::EVENT_NAME, String::ATTRIBUTE_NAME, boolean::ASSIGNMENT_VALUE);
	 * 
	 */
	
	private static void setupTransitions() {
		ArrayList<String> transAtt = new ArrayList<String>();
		transAtt.add(AttributeList.ATTRIBUTE_BAD);
		model.assignTransitionAttributes(PLANT_NAME, transAtt);
		
		//Add a transition, format for entry is "Start State Name", "Event Name", "End State Name"
		model.addTransition(PLANT_NAME, "0", "a", "1");
		model.addTransition(PLANT_NAME, "1", "a", "2");
		model.addTransition(PLANT_NAME, "1", "b", "3");
		model.addTransition(PLANT_NAME, "2", "c", "4");
		
		//Modify an attribute value for a transition as defined only by "Start State Name" and "Event Name"
		model.setTransitionAttribute(PLANT_NAME, "2", "c", AttributeList.ATTRIBUTE_BAD, true);

		
	}
	
//---  U-Structure Setup   --------------------------------------------------------------------
	
	/**
	 * 
	 * - This one's weird, it's a lot of complicated data I had to move around
	 * - I'm so sorry for an ArrayList<HashMap<String, ArrayList<Boolean>>
	 * - But
	 * - Here we are
	 * - So
	 * - You need to make a list of your agents, for each of which you are mapping an Event to
	 * a list of Booleans whose placement corresponds to the relevant Attribute in the order
	 * added to the FSM earlier; it could've also been a mapping of Attribute to Boolean instead
	 * of a Boolean list but I didn't have the heart to do it.
	 * - I'm just gonna give an example of a few agents and show a shorthand format I'm using for
	 * my own stuff; it's awkward, but in the UI it would prefill in info and be nicer to use...
	 * once the UI is done for that. It's a big project. Anyways.
	 * 
	 * 
	 */
	
	private static void setupAgents() {
		
		/**
		 * 
		 * Most of what you will need to do is adjust agentInfo for adding/removing agents and adjusting
		 * what their values are for each Event's Attributes.
		 * 
		 * The boolean[][][], if considered as a 3D thing in a [x][y][z] format, is that x = which agent,
		 * y = which Event, and z = which Attribute; the order within these are decided by the constant
		 * String[] EVENT_LIST and the instance variables eventAtt; eventAtt is set up in its ordering in
		 * setupEvents() and you can manually change the EVENT_LIST constant for the events you want in your
		 * FSM.
		 * 
		 */
		
		boolean[][][] agentInfo = new boolean[][][] {	//Agent 1
														{	//Observability, Controllability
															{true, false},	//a
															{true, false},	//b
															{false, false},	//c
														 },
														//Agent 2
														 {	//Observability, Controllability
															{false, false},	//a
															{false, false},	//b
															{true, false},	//c
														 }
														};
		
		agents = new ArrayList<HashMap<String, ArrayList<Boolean>>>();
		
		/* 
		 * So, ideally this should be in the right order but the reason I did the awkward data format for
		 * this is because it wasn't guaranteed to pull the Event List back out from the FSM in the same
		 * order it was added, so use the String array constant for consistency or manually assign everything.
		 * 
		 * For each agent, we construct a HashMap<String, ArrayList<Boolean>> that maps each Event name to that
		 * agent's relationship to it re: the relevant attributes, which in our case are primarily Observability
		 * and Controllability. That order of Attributes added to the "eventAtt" list of Attributes is done in
		 * the setupEvents() function.
		 * 
		 * Down here, this should all be automated, just mess with the boolean[][][] agentInfo above.
		 * 
		 */
		
		for(int i = 0; i < agentInfo.length; i++) {
			HashMap<String, ArrayList<Boolean>> agen = new HashMap<String, ArrayList<Boolean>>();
			for(int j = 0; j < EVENT_LIST.length; j++) {
				String e = EVENT_LIST[j];
				ArrayList<Boolean> att = new ArrayList<Boolean>();
				for(int k = 0; k < eventAtt.size(); k++) {
					att.add(agentInfo[i][j][k]);
				}
				agen.put(e, att);
			}
			agents.add(agen);
		}
	}
	
//---  Helper Methods   -----------------------------------------------------------------------
	
	private static String generateRandomFSM(String nom, int numStates, int numEvents, int numTransition, boolean det, ArrayList<String> strAtt, ArrayList<String> eveAtt, ArrayList<String> tranAtt, ArrayList<Integer> numbers) {
		return model.readInFSM(model.generateRandomFSM(nom, numStates, numEvents, numTransition, det, strAtt, eveAtt, tranAtt, numbers));
	}
	
	
	private static void makeImageDisplay(String in) {
		String path = FormatConversion.createImgFromFSM(model.generateFSMDot(in), in);
		System.out.println(path);
		WindowFrame fram = new WindowFrame(800, 800);
		fram.reserveWindow("Main");
		fram.setName("Displaying: " + in);
		fram.showActiveWindow("Main");
		ElementPanel p = new ElementPanel(0, 0, 800, 800);
		ImageDisplay iD = new ImageDisplay(path, p);
		p.setEventReceiver(iD.generateEventReceiver());
		fram.addPanelToWindow("Main", "pan", p);
		iD.refresh();
	}
	
	
}
