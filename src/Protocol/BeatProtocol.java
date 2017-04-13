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
 * 
 */

public class BeatProtocol extends Protocol {
	String userName;
	public BeatProtocol(String str) {
		String opt[] = str.split(Util.PROTOCAL_LINEEND);
		if (opt.length > 1) {
			PROTOCOL_MESSAGE_TYPE type = PROTOCOL_MESSAGE_TYPE.valueOf(opt[1]);
			ver = opt[0].split(Util.PROTOCAL_SP)[0];
			dateStr = opt[0].split(Util.PROTOCAL_SP)[0];
			
			if (type == PROTOCOL_MESSAGE_TYPE.HELLO) {
				userName = opt[2];
			}
		}
		else {
			String name = str;
			userName = name;
			
			super.ver = "5C";
			super.dateStr = Util.getFormattedCurrentDateTime();
			super.messageType = PROTOCOL_MESSAGE_TYPE.HELLO;
			
			super.contents = super.ver + super.sp + super.dateStr + super.crlf
					+ super.messageType + super.crlf
					+ userName + super.crlf;
		}
	}
}
