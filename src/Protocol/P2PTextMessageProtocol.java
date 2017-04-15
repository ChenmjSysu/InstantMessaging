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


public class P2PTextMessageProtocol extends Protocol {
	public String userName;
	public String message;
	
	public P2PTextMessageProtocol(String name, String m) {
		userName = name;
		message = m;
		super.ver = "5C";
		super.dateStr = Util.getFormattedCurrentDateTime();
		super.messageType = PROTOCOL_MESSAGE_TYPE.P2P_TEXT_MESSAGE;
		
		super.contents = super.ver + super.sp + super.dateStr + super.crlf
				+ super.messageType + super.crlf
				+ userName + super.crlf
				+ message + super.crlf;
	}
	
	public P2PTextMessageProtocol(String str) {
		String opt[] = str.split(Util.PROTOCAL_LINEEND);
		PROTOCOL_MESSAGE_TYPE type = PROTOCOL_MESSAGE_TYPE.valueOf(opt[1]);
		ver = opt[0].split(Util.PROTOCAL_SP)[0];
		dateStr = opt[0].split(Util.PROTOCAL_SP)[0];
		
		if (type == PROTOCOL_MESSAGE_TYPE.P2P_TEXT_MESSAGE) {
			userName = opt[2];
			message = opt[3];
		}
	}
}
