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
 * |       serverIP          |
 * |-------------------------|
 * |       serverPort        |
 * |-------------------------|
 * 
 */

public class HelloProtocol extends Protocol {
	String serverIP;
	int serverPort;
	public HelloProtocol(String ip, int port) {
		serverIP = ip;
		serverPort = port;
		
		super.ver = "5C";
		super.dateStr = Util.getFormattedCurrentDateTime();
		super.messageType = PROTOCOL_MESSAGE_TYPE.HELLO;
		
		super.contents = super.ver + super.sp + super.dateStr + super.crlf
				+ super.messageType + super.crlf
				+ serverIP + super.crlf
				+ serverPort + super.crlf;
	}
	
	public HelloProtocol(String str) {
		String opt[] = str.split(Util.PROTOCAL_LINEEND);
		PROTOCOL_MESSAGE_TYPE type = PROTOCOL_MESSAGE_TYPE.valueOf(opt[1]);
		ver = opt[0].split(Util.PROTOCAL_SP)[0];
		dateStr = opt[0].split(Util.PROTOCAL_SP)[0];
		
		if (type == PROTOCOL_MESSAGE_TYPE.HELLO) {
			serverIP = opt[2];
			serverPort = Integer.parseInt(opt[3]);
		}
		
	}
}
