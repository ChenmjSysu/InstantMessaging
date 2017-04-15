package Common;

import java.sql.Date;

public class MessageItem {
	public String message;
	public String dateStr;
	public String fromUser;
	
	public MessageItem(String m, String date, String from) {
		message = m;
		dateStr = date;
		fromUser = from;
	}
}
