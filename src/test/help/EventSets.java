package test.help;

import model.AttributeList;

public class EventSets {
	
//---  Constants   ----------------------------------------------------------------------------

	public final static String[] EVENT_ATTR_LIST = new String[] {AttributeList.ATTRIBUTE_OBSERVABLE, AttributeList.ATTRIBUTE_CONTROLLABLE};
	
	public final static String[] EVENT_LIST_A = new String[] {"a_{1}", "a_{2}", "b_{1}", "b_{2}", "c"};
	
	public final static String[] EVENT_LIST_B = new String[] {"a", "b", "c", "d", "s"};
	
	public final static String[] EVENT_LIST_C = new String[] {"a_{1}", "a_{2}", "b_{1}", "b_{2}", "c", "d"};
	
	public final static String[] EVENT_LIST_D = new String[] {"a", "b", "c"};
	
	public final static String[] EVENT_LIST_E = new String[] {"a_{1}", "a_{2}", "b_{1}", "b_{2}", "c1", "c2", "c3", "d", "s"};

	public final static String[] EVENT_LIST_FINN5 = new String[] {"a_{1}", "a_{2}", "a_{3}","a_{4}","a_{5}","a_{6}","b_{1}", "b_{2}", "b_{3}","b_{4}","b_{5}","b_{6}", "s"};
	
	public final static String[] EVENT_LIST_LIU_ONE = new String[] {"a", "b", "g"};
	
	public final static String[] EVENT_LIST_DTP = new String[] {"getFrame", "send_0", "send_1", "rcvAck_0", "rcvAck_1", "loss", "sendAck_0", "sendAck_1", "rcv_0", "rcv_1", "passToHost"};
	
	public final static String[] EVENT_LIST_DTP_SENDER = new String[] {"getFrame", "send_0", "send_1", "rcvAck_0", "rcvAck_1", "loss"};
	
	public final static String[] EVENT_LIST_DTP_RECEIVER = new String[] {"rcv_0", "rcv_1", "passToHost", "sendAck_0", "sendAck_1"};
	
	public final static String[] EVENT_LIST_DTP_CHANNEL = new String[] {"rcv_0", "rcv_1", "send_0", "send_1", "loss", "sendAck_0", "sendAck_1", "rcvAck_0", "rcvAck_1"};
	
	public final static String[] EVENT_LIST_DTP_SPEC_ONE = EVENT_LIST_DTP;
	
	public final static String[] EVENT_LIST_DTP_SPEC_TWO = new String[] {"getFrame", "loss", "send_0", "send_1", "rcvAck_0", "rcvAck_1"};
	
	public final static String[] EVENT_LIST_DTP_SPEC_THREE = new String[] {"rcv_0", "rcv_1", "sendAck_0", "sendAck_1", "passToHost"};
	
	public final static String[] EVENT_LIST_SPEC_PRIME = new String[] {"a", "b", "g"};
	
	
	
	public final static String[] EVENT_LIST_HISC = new String[] {"take_item", "package", "allow_exit", "new_part", "part_f_obuff", "part_passes",
																 "part_fails", "ret_inbuff", "deposit_part", "part_ent-I", "part_ent-II", "part_ent-III",
																 "fin_exit-I", "fin_exit-II", "fin_exit-III"};
	
	public final static String[] EVENT_LIST_HISC_J = new String[] {"part_arr1-", "start_pol-", "compl_pol-", "part_lv1-",
															     "part_arr2-", "partLvExit-", "recog_A-", "recog_B-", "attch_ptA-", "attch_ptB-",
															     "finA_attch-", "finB_attch-", "part_lv2-", "part_arr3-", "start_case-", "compl_case-",
															     "part_lv3-", "str_exit-", "take_pt-", "str_ptA-", "str_ptB-", "cmpl_A-", "cmpl_B-",
															     "ret_pt-", "dip_acid-", "polish-", "str_rlse-", "attch_case-"};
	
	public final static String[] EVENT_LIST_HISC_J2 = new String[] {"start_pol-", "attch_ptA-", "attch_ptB-", "start_case-", "finA_attch-", "finB_attch-", "part_lv1-",
																    "partLvExit-", "str_exit-", "part_lv2-", "part_lv3-", "take_pt-", "str_ptA-", "str_ptB-", 
																    "dip_acid-", "polish-", "str_rlse-", "part_ent-"};
	
	public final static String[] EVENT_LIST_HISC_PACK_SYS = new String[] {"take_item", "package", "allow_exit"};
	
	public final static String[] EVENT_LIST_HISC_SOURCE = new String[] {"new_part"};
	
	public final static String[] EVENT_LIST_HISC_SINK = new String[] {"allow_exit"};
	
	public final static String[] EVENT_LIST_HISC_TEST = new String[] {"part_f_obuff", "part_passes", "part_fails", "ret_inbuff", "deposit_part"};
	
	
	public final static String[] EVENT_LIST_HISC_PATH = new String[] {"part_ent-", "part_arr1-", "part_lv1-", "str_exit-", "fin_exit-", "partLvExit-",
																	  "part_arr2-", "recog_A-", "recog_B-", "part_lv2-", "part_arr3-", "part_lv3-"};
	
	public final static String[] EVENT_LIST_HISC_DEFINE = new String[] {"attch_ptA-", "attch_ptB-", "finA_attch-", "finB_attch-"};
	
	public final static String[] EVENT_LIST_HISC_ATTACH_PART = new String[] {"take_pt-", "str_ptA-", "cmpl_A-", "str_ptB-", "cmpl_B-", "ret_pt-"};
	
	public final static String[] EVENT_LIST_HISC_POLISH_PART = new String[] {"start_pol-", "dip_acid-", "polish-", "str_rlse-", "compl_pol-"};
	
	public final static String[] EVENT_LIST_HISC_ATTACH_CASE = new String[] {"start_case-", "attch_case-", "compl_case-"};
	
	public final static String[] EVENT_LIST_HISC_INBUFF = new String[] {"ret_inbuff", "new_part", "part_ent-I", "part_ent-II", "part_ent-III"};
	
	public final static String[] EVENT_LIST_HISC_OUTBUFF = new String[] {"part_f_obuff", "part_ent-I", "part_ent-II", "part_ent-III", "fin_exit-I", "fin_exit-II", "fin_exit-III"};
	
	public final static String[] EVENT_LIST_HISC_PACKBUFF = new String[] {"deposit_part", "take_item"};
	
	public final static String[] EVENT_LIST_HISC_ENSURE = new String[] {"new_part", "part_passes"};
	
	public final static String[] EVENT_LIST_HISC_MOVE = new String[] {"part_ent-", "fin_exit-"};
	
	public final static String[] EVENT_LIST_HISC_POLISH_SEQUENCE = new String[] {"start_pol-", "dip_acid-", "polish-", "str_rlse-"};
	
	public final static String[] EVENT_LIST_HISC_SEQUENCE = new String[] {"fin_exit-", "part_ent-", "part_arr1-", "start_pol-", "compl_pol-", "part_lv1-",
																		  "part_arr2-", "partLvExit-", "recog_A-", "recog_B-", "attch_ptA-", "attch_ptB-",
																		  "finA_attch-", "finB_attch-", "part_lv2-", "part_arr3-", "start_case-", "compl_case-",
																		  "part_lv3-", "str_exit-"};
	
	public final static String[] EVENT_LIST_HISC_AFFIX = new String[] {"str_ptA-", "attch_ptB-", "take_pt-", "str_ptB-", "cmpl_B-", "ret_pt-", "finB_attch-", "attch_ptA-",
																	   "cmpl_A-", "finA_attch-"};
	
	public final static String[] EVENT_LIST_HISC_G = new String[] {"start_pol-", "compl_pol-", "attch_ptA-", "finA_attch-", "attch_ptB-", "finB_attch-", "start_case-", "compl_case-"};
}
