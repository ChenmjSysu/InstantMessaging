package Protocol;

import java.util.Map;

import Common.PROTOCOL_MESSAGE_TYPE;
import Common.Util;

import Common.UserList;

/*
 * Protocol Format
 * 
 * ---------------------------
 * |  version  |   datetime  |
 * |-------------------------|
 * |  protocol message type  |  
 * |-------------------------|
 * |userName| IP | TCP | UDP |
 * |-------------------------|
 * |         ....            |
 * |-------------------------|
 * |userName| IP | TCP | UDP |
 * |-------------------------|
 * 
 */

public class GetUserListProtocol extends Protocol {
	UserList userList;
	public GetUserListProtocol(Map<String, String> list) {
		userList = new UserList(list);
		super.ver = "5C";
		super.dateStr = Util.getFormattedCurrentDateTime();
		super.messageType = PROTOCOL_MESSAGE_TYPE.GET_USERLIST_SUCCESS;
		
		super.contents = super.ver + super.sp + super.dateStr + super.crlf
				+ super.messageType + super.crlf
				+ userList.toString() + super.crlf;
	}
	
	public GetUserListProtocol() {
		super.ver = "5C";
		super.dateStr = Util.getFormattedCurrentDateTime();
		super.messageType = PROTOCOL_MESSAGE_TYPE.GET_USERLIST_REQUEST;
		
		super.contents = super.ver + super.sp + super.dateStr + super.crlf
				+ super.messageType + super.crlf;
	}
}
