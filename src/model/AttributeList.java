package model;

import java.util.HashMap;

public class AttributeList {

//---  Constants   ----------------------------------------------------------------------------
	
	//false means you only need one, true means you need all (AON = "all or nothing")
	
	//-- State  -----------------------------------------------
	
	public final static String[] STATE_ATTRIBUTES = new String[] {AttributeList.ATTRIBUTE_INITIAL, AttributeList.ATTRIBUTE_MARKED, AttributeList.ATTRIBUTE_PRIVATE, AttributeList.ATTRIBUTE_BAD, AttributeList.ATTRIBUTE_GOOD};
	public final static String ATTRIBUTE_INITIAL = "Initial";
	public final static boolean ATTRIBUTE_AON_INITIAL = true;
	public final static String ATTRIBUTE_MARKED = "Marked";
	public final static boolean ATTRIBUTE_AON_MARKED = true;
	public final static String ATTRIBUTE_PRIVATE = "Private";
	public final static boolean ATTRIBUTE_AON_PRIVATE = true;
	public final static String ATTRIBUTE_BAD = "Bad";
	public final static boolean ATTRIBUTE_AON_BAD = false;
	public final static String ATTRIBUTE_GOOD = "Good";
	public final static boolean ATTRIBUTE_AON_GOOD = false;
	
	//-- Event  -----------------------------------------------
	
	public final static String[] EVENT_ATTRIBUTES = new String[] {AttributeList.ATTRIBUTE_OBSERVABLE, AttributeList.ATTRIBUTE_CONTROLLABLE, AttributeList.ATTRIBUTE_ATTACKER_OBSERVABLE};
	public final static String ATTRIBUTE_OBSERVABLE = "Observable";
	public final static boolean ATTRIBUTE_AON_OBSERVABLE = false;
	public final static String ATTRIBUTE_CONTROLLABLE = "Controllable";
	public final static boolean ATTRIBUTE_AON_CONTROLLABLE = false;
	public final static String ATTRIBUTE_ATTACKER_OBSERVABLE = "AttackerObservable";
	public final static boolean ATTRIBUTE_AON_ATTACKER_OBSERVABLE = true;
	
	//-- Transitions  -----------------------------------------
	
	public final static String[] TRANSITION_ATTRIBUTES = new String[] {AttributeList.ATTRIBUTE_BAD};
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static HashMap<String, Boolean> map;
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public static boolean getAON(String ref) {
		if(map == null) {
			setupMap();
		}
		return map.get(ref);
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private static void setupMap() {
		map = new HashMap<String, Boolean>();
		map.put(ATTRIBUTE_INITIAL, ATTRIBUTE_AON_INITIAL);
		map.put(ATTRIBUTE_MARKED, ATTRIBUTE_AON_MARKED);
		map.put(ATTRIBUTE_PRIVATE, ATTRIBUTE_AON_PRIVATE);
		map.put(ATTRIBUTE_OBSERVABLE, ATTRIBUTE_AON_OBSERVABLE);
		map.put(ATTRIBUTE_CONTROLLABLE, ATTRIBUTE_AON_CONTROLLABLE);
		map.put(ATTRIBUTE_BAD, ATTRIBUTE_AON_BAD);
		map.put(ATTRIBUTE_ATTACKER_OBSERVABLE, ATTRIBUTE_AON_ATTACKER_OBSERVABLE);
		map.put(ATTRIBUTE_GOOD, ATTRIBUTE_AON_GOOD);
	}
	
}
