package Common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	public static int SERVER_PORT = 9876;
	public static String SERVER_IP = "127.0.0.1";
	public static String PROTOCAL_SP = " ";
	public static String PROTOCAL_LINEEND = "\r\n";
	
	public static String LOGIN_WIN_TITLE = "Login[Instant Message]";
	public static String REGIST_WIN_TITLE = "Regist[Instant Message]";
	public static String CHAT_WIN_TITLE = "[Instant Message]";
	
	public static int LOGIN_WIN_WIDTH = 300;
	public static int LOGIN_WIN_HEIGHT = 200;
	public static int REGIST_WIN_WIDTH = 300;
	public static int REGIST_WIN_HEIGHT = 200;
	
	public static String getFormattedCurrentDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
		Date currentDate = new Date(System.currentTimeMillis());
		String dateStr = formatter.format(currentDate);
		return dateStr;
	}
	
	public static void log(String str) {
		System.out.println("[" + getFormattedCurrentDateTime() + "]  " + str);
	}
	
	// 获取消息类型，例如是登陆还是登出
	public static PROTOCOL_MESSAGE_TYPE getAction(String message) {
		String []options = message.split(Util.PROTOCAL_LINEEND);
		return PROTOCOL_MESSAGE_TYPE.valueOf(options[1]);
	}
}
