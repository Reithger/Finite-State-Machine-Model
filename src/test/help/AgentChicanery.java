package test.help;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * Notation used is 3D boolean array; first level is for agent data represented as 2D boolean array, second level is each event's
 * observability/controllability data in a length 2 boolean array (so total size is # agents * # events * 2)
 * 
 * Each event is {[is_observable], [is_controllable]} in the order of events described by the EventSets.EVENT_LIST_... in the generateAgentSet function call.
 * 
 * Easy way to initialize and set exactly what values you want can be seen in generateAgentsDTP where I added some automation to configuring the boolean[][][].
 * 
 * @author SirBo
 *
 */

public class AgentChicanery {
	
	private static final int OBS = 0;
	private static final int CTR = 1;
	
//---  Operations   ---------------------------------------------------------------------------

	public static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsA() {
		boolean[][][] agentInfo = new boolean[][][] {	{	//Agent 1
			  {true, false},	//a1
			  {true, false},	//a2
			  {false, false},	//b1
			  {false, false},	//b2
			  {true, true}		//c
			},
		 	{	//Agent 2
			  {false, false},
			  {false, false},
			  {true, false},
			  {true, false},
			  {true, true}
			}
		};
		return generateAgentSet(agentInfo, EventSets.EVENT_LIST_A);
	}
	
	public static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsB(){
		boolean[][][] agentInfo = new boolean[][][] {	{	//Agent 1
			  {true, false},	//a
			  {false, false},	//b
			  {false, false},	//c
			  {true, false},	//d
			  {false, true}		//s
			},
		 	{	//Agent 2
			  {false, false},
			  {true, false},
			  {false, false},
			  {true, false},
			  {false, true}
			},
		 	{	//Agent 3
			  {false, false},
			  {false, false},
			  {true, false},
			  {true, false},
			  {false, true}
			},
		};

		return generateAgentSet(agentInfo, EventSets.EVENT_LIST_B);
	}
	
	public static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsB2(){
		boolean[][][] agentInfo = new boolean[][][] {	{	//Agent 1
			  {true, false},	//a
			  {false, false},	//b
			  {false, false},	//c
			  {true, false},	//d
			  {false, true}		//s
			},
		 	{	//Agent 2
			  {false, false},
			  {true, false},
			  {false, false},
			  {true, false},
			  {false, true}
			},
		};

		return generateAgentSet(agentInfo, EventSets.EVENT_LIST_B);
	}
	
	public static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsC() {
		boolean[][][] agentInfo = new boolean[][][] {	{	//Agent 1
			  {true, false},	//a1
			  {true, false},	//a2
			  {false, false},	//b1
			  {false, false},	//b2
			  {false, true},		//c
			  {false, false}		//d
			},
		 	{	//Agent 2
			  {false, false},
			  {false, false},
			  {true, false},
			  {true, false},
			  {false, true},
			  {false, false}
			}
		};
		return generateAgentSet(agentInfo, EventSets.EVENT_LIST_C);
	}
	
	public static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsD() {
		boolean[][][] agentInfo = new boolean[][][] {	{	//Agent 1
			  {true, false},	//a
			  {false, false},	//b
			  {false, true},	//c
			},
		 	{	//Agent 2
			  {false, false},
			  {true, true},
			  {false, true},
			}
		};
		return generateAgentSet(agentInfo, EventSets.EVENT_LIST_D);
	}
	
	public static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsE(){
		boolean[][][] agentInfo = new boolean[][][] {	{	//Agent 1
			  {true, false},	//a1
			  {true, false},	//a2
			  {false, false},	//b1
			  {false, false},	//b2
			  {false, false},	//c1
			  {false, false},	//c2
			  {false, false},	//c3
			  {true, false},	//d
			  {true, true},	//s
			},
		 	{	//Agent 2
			  {false, false},	//a1
			  {false, false},	//a2
			  {true, false},	//b1
			  {true, false},	//b2
			  {false, false},	//c1
			  {false, false},	//c2
			  {false, false},	//c3
			  {true, false},	//d
			  {true, true},	//s
			},
		 	{	//Agent 3
			  {false, false},	//a1
			  {false, false},	//a2
			  {false, false},	//b1
			  {false, false},	//b2
			  {true, false},	//c1
			  {true, false},	//c2
			  {true, false},	//c3
			  {true, false},	//d
			  {true, true},	//s
			}
		};
		return generateAgentSet(agentInfo, EventSets.EVENT_LIST_E);
	}

	public static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsFinn5(){
		boolean[][][] agentInfo = new boolean[][][] {	{	
			//Agent 1
			  {true, false},	//a1
			  {true, false},	//a2
			  {true, false},	//a3
			  {true, false},	//a4
			  {true, false},    //a5
			  {true, false},	//a6
			  {false, false},	//b1
			  {false, false},	//b2
			  {false, false},	//b3
			  {false, false},	//b4
			  {false, false},   //b5
			  {false, false},	//b6
			  {false, true}	//s
			},
		 	{	//Agent 2
				  {false, false},	//a1
				  {false, false},	//a2
				  {false, false},	//a3
				  {false, false},	//a4
				  {false, false},   //a5
				  {false, false},	//a6
				  {true, false},	//b1
				  {true, false},	//b2
				  {true, false},	//b3
				  {true, false},	//b4
				  {true, false},    //b5
				  {true, false},	//b6
				  {false, true}	//s
			},
		};

		return generateAgentSet(agentInfo, EventSets.EVENT_LIST_FINN5);
	}
	
	public static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsLiuOne() {
		boolean[][][] agentInfo = new boolean[][][] {	{	//Agent 1
			  {true, true},		//a
			  {false, false},	//b
			  {false, true},	//g
			},
		 	{	//Agent 2
			  {false, false},	//a
			  {true, true},		//b
			  {false, true},	//g
			},
		};
		return generateAgentSet(agentInfo, EventSets.EVENT_LIST_LIU_ONE);
	}
	
	public static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsUrvashi(){
		boolean[][][] agentInfo = new boolean[][][] { {
				{true, false},
				{false, false},
				{true, true},
			},
			{
				{false, false},
				{true, false},
				{true, false},
			},
		};
		return generateAgentSet(agentInfo, EventSets.EVENT_LIST_D);
	}
	
	public static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentsDTP(){
		String[] events = EventSets.EVENT_LIST_DTP;
		boolean[][][] agentInfo = makeDefaultAgentArray(events, 2);
		assignEventAgentInfo(agentInfo, events, 0, OBS, "getFrame", "send_0", "send_1", "rcvAck_0", "rcvAck_1", "loss");
		assignEventAgentInfo(agentInfo, events, 1, OBS, "sendAck_0", "sendAck_1", "rcv_0", "rcv_1", "passToHost");

		assignEventAgentInfo(agentInfo, events, 0, CTR, "getFrame", "send_0", "send_1");
		assignEventAgentInfo(agentInfo, events, 1, CTR, "sendAck_0", "sendAck_1", "passToHost");
		
		return generateAgentSet(agentInfo, EventSets.EVENT_LIST_DTP);
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	protected static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentSet(boolean[][][] agentInfo, String[] eventList){
		ArrayList<HashMap<String, ArrayList<Boolean>>> use = new ArrayList<HashMap<String, ArrayList<Boolean>>>();
		
		for(int i = 0; i < agentInfo.length; i++) {
			HashMap<String, ArrayList<Boolean>> agen = new HashMap<String, ArrayList<Boolean>>();
			for(int j = 0; j < eventList.length; j++) {
				String e = eventList[j];
				ArrayList<Boolean> att = new ArrayList<Boolean>();
				for(int k = 0; k < EventSets.EVENT_ATTR_LIST.length; k++) {
					att.add(agentInfo[i][j][k]);
				}
				agen.put(e, att);
			}
			use.add(agen);
		}
		return use;
	}
	
	private static boolean[][][] makeDefaultAgentArray(String[] eventSets, int numAgents){
		boolean[][][] out = new boolean[numAgents][eventSets.length][2];
		for(int i = 0; i < numAgents; i++) {
			for(int j = 0; j < eventSets.length; j++) {
				out[i][j][0] = false;
				out[i][j][1] = false;
			}
		}
		return out;
	}
	
	private static int indexOf(String[] arr, String key) {
		for(int i = 0; i < arr.length; i++) {
			if(arr[i].equals(key)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 
	 * int pos: 0 = obs, 1 = ctr
	 * 
	 * @param array
	 * @param eventSets
	 * @param agentPref
	 * @param event
	 * @param pos
	 * @param choice
	 */
	
	private static void assignEventAgentInfo(boolean[][][] array, String[] eventSets, int agentPref, int pos, String ... events) {
		for(String s : events)
			array[agentPref][indexOf(eventSets, s)][pos] = true;
	}
	
}
