package controller;

public class CodeReference {
	
//---  Constants   ----------------------------------------------------------------------------
	
	//-- Display Page  ----------------------------------------
	
	public final static int CODE_GENERATE_IMAGE = 154;
	public final static int CODE_DISPLAY_CYCLE_VIEW = 155;
	
	//-- Generate FSM  ----------------------------------------
	public final static int CODE_ACCESS_NUM_STATES = 100;
	public final static int CODE_ACCESS_NUM_EVENTS = 101;
	public final static int CODE_ACCESS_NUM_TRANS = 102;
	public final static int CODE_ACCESS_FSM_NAME = 103;
	public final static int CODE_ACCESS_NON_DETERMINISTIC = 104;
	public final static int CODE_ACCESS_STATE_ATTRIBUTES = 106;
	public final static int CODE_ACCESS_EVENT_ATTRIBUTES = 107;
	public final static int CODE_ACCESS_TRANS_ATTRIBUTES = 108;
	public final static int CODE_ADD_STATE_ATTRIBUTE = 109;
	public final static int CODE_ADD_EVENT_ATTRIBUTE = 110;
	public final static int CODE_ADD_TRANS_ATTRIBUTE = 111;
	public final static int CODE_GENERATE_FSM = 105;
	//-- FSM Properties  --------------------------------------
	public final static int CODE_RENAME_FSM = 135;
	public final static int CODE_FSM_ADD_STATE_ATTRIBUTE = 112;
	public final static int CODE_FSM_ACCESS_ADD_STATE_ATTRIBUTE = 147;
	public final static int CODE_FSM_REMOVE_STATE_ATTRIBUTE = 113;
	public final static int CODE_FSM_ACCESS_REMOVE_STATE_ATTRIBUTE = 148;
	public final static int CODE_FSM_ADD_EVENT_ATTRIBUTE = 114;
	public final static int CODE_FSM_ACCESS_ADD_EVENT_ATTRIBUTE = 149;
	public final static int CODE_FSM_REMOVE_EVENT_ATTRIBUTE = 115;
	public final static int CODE_FSM_ACCESS_REMOVE_EVENT_ATTRIBUTE = 150;
	public final static int CODE_FSM_ADD_TRANS_ATTRIBUTE = 116;
	public final static int CODE_FSM_ACCESS_ADD_TRANS_ATTRIBUTE = 151;
	public final static int CODE_FSM_REMOVE_TRANS_ATTRIBUTE = 117;
	public final static int CODE_FSM_ACCESS_REMOVE_TRANS_ATTRIBUTE = 152;
	//-- States  ----------------------------------------------
	public final static int CODE_ADD_STATE = 118;
	public final static int CODE_REMOVE_STATE = 119;	
	public final static int CODE_RENAME_STATE = 120;
	public final static int CODE_ADD_STATES = 121;
	public final static int CODE_EDIT_STATE_ATTRIBUTE = 122;
	public final static int CODE_ADD_EDIT_STATE_ATTRIBUTE = 123;
	public final static int CODE_ACCESS_EDIT_STATE = 124;
	//-- Events  ----------------------------------------------
	public final static int CODE_ADD_EVENT = 140;
	public final static int CODE_REMOVE_EVENT = 141;	
	public final static int CODE_RENAME_EVENT = 142;
	public final static int CODE_ADD_EVENTS = 143;
	public final static int CODE_EDIT_EVENT_ATTRIBUTE = 144;
	public final static int CODE_ADD_EDIT_EVENT_ATTRIBUTE = 145;
	public final static int CODE_ACCESS_EDIT_EVENT = 146;
	//-- Transitions  -----------------------------------------
	public final static int CODE_ADD_TRANSITION = 126;
	public final static int CODE_REMOVE_TRANSITION = 127;	
	public final static int CODE_EDIT_TRANS_ATTRIBUTE = 153;
	public final static int CODE_ADD_EDIT_TRANS_ATTRIBUTE = 138;
	public final static int CODE_ACCESS_EDIT_TRANS = 139;
	//-- Admin  -----------------------------------------------
	public final static int CODE_SAVE_FSM = 129;
	public final static int CODE_SAVE_IMG = 130;
	public final static int CODE_SAVE_TKZ = 131;
	public final static int CODE_SAVE_SVG = 132;
	public final static int CODE_LOAD_SOURCE = 133;
	public final static int CODE_DELETE_SOURCE = 134;
	public final static int CODE_DUPLICATE_FSM = 136;
	public final static int CODE_CLOSE_FSM = 137;
	
	//-- Operations  ------------------------------------------
	
	public final static int CODE_TRIM = 200;
	public final static int CODE_ACCESSIBLE = 201;
	public final static int CODE_CO_ACCESSIBLE = 202;
	public final static int CODE_OBSERVER = 203;
	public final static int CODE_PRODUCT_SELECT = 204;
	public final static int CODE_PRODUCT = 205;
	public final static int CODE_PARALLEL_COMPOSITION_SELECT = 206;
	public final static int CODE_PARALLEL_COMPOSITION = 207;
	public final static int CODE_SUP_CNT_SBL_SELECT = 208;
	public final static int CODE_SUP_CNT_SBL = 209;
	public final static int CODE_UNDER_FSM = 210;
	public final static int CODE_OPT_OPQ_CONTROLLER =111;
	public final static int CODE_OPT_SPVR_SELECT = 212;
	public final static int CODE_OPT_SPVR = 213;
	public final static int CODE_GRT_LWR_BND_SELECT = 214;
	public final static int CODE_GRT_LWR_BND = 215;
	public final static int CODE_PRUNE = 216;
	public final static int CODE_BLOCKING = 217;
	public final static int CODE_STATE_EXISTS = 218;
	
	//-- UStructure  ------------------------------------------
	
	public final static int CODE_SELECT_PLANT = 300;
	public final static int CODE_ADD_BAD_TRANS = 301;
	public final static int CODE_BUILD_AGENTS = 302;
	public final static int CODE_BUILD_USTRUCT = 303;
	public final static int CODE_TOGGLE_USTRUCT = 304;
	public final static int CODE_DISPLAY_BAD_TRANS_START = 500;
}
