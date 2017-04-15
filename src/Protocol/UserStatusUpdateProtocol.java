package Protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;

import Common.PROTOCOL_MESSAGE_TYPE;
import Common.UserItem;
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
 * |      newStatus          |
 * |-------------------------|
 * |          ip             |
 * |-------------------------|
 * |       tcpPort           |
 * |-------------------------|
 * |       udpPort           |
 * |-------------------------|
 * 
 */

public class UserStatusUpdateProtocol extends Protocol {
	public String userName;
	public USER_STATUS_TYPE status;
	public InetAddress ip;
	public int tcpPort;
	public int udpPort;
	
	public UserStatusUpdateProtocol(UserItem info, USER_STATUS_TYPE s) {
		userName = info.userName;
		ip = info.ip;
		tcpPort = info.tcp;
		udpPort = info.udp;
		status = s;
		super.ver = "5C";
		super.dateStr = Util.getFormattedCurrentDateTime();
		super.messageType = PROTOCOL_MESSAGE_TYPE.USER_STATUS_UPDATE;
		
		super.contents = super.ver + super.sp + super.dateStr + super.crlf
				+ super.messageType + super.crlf
				+ userName + super.crlf
				+ status + super.crlf
				+ ip.getHostAddress() + super.crlf
				+ tcpPort + super.crlf
				+ udpPort + super.crlf;
	}
	
	public UserStatusUpdateProtocol(String s) {
		String opt[] = s.split(Util.PROTOCAL_LINEEND);
		PROTOCOL_MESSAGE_TYPE type = PROTOCOL_MESSAGE_TYPE.valueOf(opt[1]);
		ver = opt[0].split(Util.PROTOCAL_SP)[0];
		dateStr = opt[0].split(Util.PROTOCAL_SP)[0];
		
		userName = opt[2];
		status = USER_STATUS_TYPE.valueOf(opt[3]);
		try {
			ip = InetAddress.getByName(opt[4]);
			tcpPort = Integer.parseInt(opt[5]);
			udpPort = Integer.parseInt(opt[6]);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public enum USER_STATUS_TYPE {
		ONLINE,
		OFFLINE,
	}
}
