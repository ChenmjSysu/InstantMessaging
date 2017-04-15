package Protocol;

import Common.Util;
import Common.PROTOCOL_MESSAGE_TYPE;

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
 * |       password          |
 * |-------------------------|
 * |       tcpPort           |
 * |-------------------------|
 * |       udpPort           |
 * |-------------------------|
 * 
 */

public class LoginProtocol extends Protocol {
	public String userName;
	public String pwd;
	public int tcpPort;
	public int udpPort;

	public LoginProtocol(String name, String password, int tcp, int udp) {
		userName = name;
		pwd = password;
		tcpPort = tcp;
		udpPort = udp;
		super.ver = "5C";
		super.dateStr = Util.getFormattedCurrentDateTime();
		super.messageType = PROTOCOL_MESSAGE_TYPE.LOGIN_REQUEST;
		
		super.contents = super.ver + super.sp + super.dateStr + super.crlf
				+ super.messageType + super.crlf
				+ userName + super.crlf
				+ pwd + super.crlf
				+ tcpPort + super.crlf
				+ udpPort + super.crlf;
	}
	
	public LoginProtocol(PROTOCOL_MESSAGE_TYPE type) {
		super.ver = "5C";
		super.dateStr = Util.getFormattedCurrentDateTime();
		super.messageType = type;
		
		super.contents = super.ver + super.sp + super.dateStr + super.crlf
				+ super.messageType + super.crlf;
	}
	
	public LoginProtocol(String str) {
		String opt[] = str.split(Util.PROTOCAL_LINEEND);
		PROTOCOL_MESSAGE_TYPE type = PROTOCOL_MESSAGE_TYPE.valueOf(opt[1]);
		ver = opt[0].split(Util.PROTOCAL_SP)[0];
		dateStr = opt[0].split(Util.PROTOCAL_SP)[0];
		
		if (type == PROTOCOL_MESSAGE_TYPE.LOGIN_SUCCESS 
				|| type == PROTOCOL_MESSAGE_TYPE.LOGIN_FAIL_ERROR_PASSWORD
				|| type == PROTOCOL_MESSAGE_TYPE.LOGIN_FAIL_NO_USERNAME) {
		}
		else if (type == PROTOCOL_MESSAGE_TYPE.LOGIN_REQUEST) {
			userName = opt[2];
			pwd = opt[3];
			tcpPort = Integer.parseInt(opt[4]);
			udpPort = Integer.parseInt(opt[5]);
		}
		
	}
}
