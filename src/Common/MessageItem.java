package Common;

import java.sql.Date;

public class MessageItem {
	public String message;
	public String dateStr;
	public String fromUser;
	public ALIGN_TYPE algin;
	
	public MessageItem(String m, String date, String from, ALIGN_TYPE l) {
		message = m;
		dateStr = date;
		fromUser = from;
		algin = l;
	}
	
	public enum ALIGN_TYPE {
		left,
		right,
	}
}
