package Protocol;

import Common.PROTOCOL_MESSAGE_TYPE;
import Common.Util;

/*
 * Protocol Format
 * 
 * ---------------------------
 * |  version  |   datetime  |
 * |-------------------------|
 * |  protocol message type  |  
 * |-------------------------|
 * |       userName          |
 * |-------------------------|
 * |       message           |
 * |-------------------------|
 * 
 */


public class P2PMessageProtocol extends Protocol {
	String userName;
	String message;
	
	public P2PMessageProtocol(String name, String m) {
		userName = name;
		message = m;
		super.ver = "5C";
		super.dateStr = Util.getFormattedCurrentDateTime();
		super.messageType = PROTOCOL_MESSAGE_TYPE.P2P_Message;
		
		super.contents = super.ver + super.sp + super.dateStr + super.crlf
				+ super.messageType + super.crlf
				+ userName + super.crlf
				+ message + super.crlf;
	}
}
