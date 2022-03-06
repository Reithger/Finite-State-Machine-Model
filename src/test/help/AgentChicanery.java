package test.help;

import java.util.ArrayList;
import java.util.HashMap;

public class AgentChicanery {
	
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
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private static ArrayList<HashMap<String, ArrayList<Boolean>>> generateAgentSet(boolean[][][] agentInfo, String[] eventList){
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
	
}
