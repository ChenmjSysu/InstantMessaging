package Protocol;

import Common.Util;
import Common.PROTOCOL_MESSAGE_TYPE;

/*
 * Protocol Format
 * 
 * ---------------------------
 * |  version  |   datetime  |
 * |-------------------------|
 * |  protocal message type  |  
 * |-------------------------|
 * |          val 0          |
 * |-------------------------|
 * |          ....           |
 * |-------------------------|
 * |          val n          |
 * |-------------------------|
 * 
 */


public class Protocol {
	String contents;
	String sp = Util.PROTOCAL_SP;
	String crlf = Util.PROTOCAL_LINEEND;
	String ver = "";
	public String dateStr;
	PROTOCOL_MESSAGE_TYPE messageType;
	
	public String getContent() { return contents; }
	public String getDateStr() { return dateStr; }
	public PROTOCOL_MESSAGE_TYPE getMessageType() { return messageType; }
	
	public static String Combine(String request, String header, String body) {
		return request + header + body;
	}
}
