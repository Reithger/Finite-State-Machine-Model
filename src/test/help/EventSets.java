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
	
	
}
