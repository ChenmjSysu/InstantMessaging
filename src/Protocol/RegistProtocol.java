package Protocol;

import Common.PROTOCOL_MESSAGE_TYPE;
import Common.Util;

public class RegistProtocol extends Protocol {
	public String userName;
	public String pwd;
	public String pwdAgain;


	public RegistProtocol(String name, String password, String passwordAgain) {
		userName = name;
		pwd = password;
		pwdAgain = passwordAgain;
		super.ver = "5C";
		super.dateStr = Util.getFormattedCurrentDateTime();
		super.messageType = PROTOCOL_MESSAGE_TYPE.REGIST_REQUEST;
		
		super.contents = super.ver + super.sp + super.dateStr + super.crlf
				+ super.messageType + super.crlf
				+ userName + super.crlf
				+ pwd + super.crlf
				+ pwdAgain + super.crlf;
	}
	
	public RegistProtocol(PROTOCOL_MESSAGE_TYPE type) {
		super.ver = "5C";
		super.dateStr = Util.getFormattedCurrentDateTime();
		super.messageType = type;
		
		super.contents = super.ver + super.sp + super.dateStr + super.crlf
				+ super.messageType + super.crlf;
	}
	
	public RegistProtocol(String str) {
		String opt[] = str.split(Util.PROTOCAL_LINEEND);
		PROTOCOL_MESSAGE_TYPE type = PROTOCOL_MESSAGE_TYPE.valueOf(opt[1]);
		ver = opt[0].split(Util.PROTOCAL_SP)[0];
		dateStr = opt[0].split(Util.PROTOCAL_SP)[0];
		
		if (type == PROTOCOL_MESSAGE_TYPE.REGIST_SUCCESS) {
		}
		else if (type == PROTOCOL_MESSAGE_TYPE.REGIST_REQUEST) {
			userName = opt[2];
			pwd = opt[3];
			pwdAgain = opt[4];
		}
		
	}
}

