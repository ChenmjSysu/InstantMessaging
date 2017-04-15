package Common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class UserList {
	public Map<String, String> userList = new HashMap<String, String>();
	
	public UserList(Map<String, String> l) {
		userList = l; 
	}
	
	public UserList(String str) {
		String[] opts = str.split(Util.PROTOCAL_LINEEND);
		for (int i = 2; i < opts.length; i++) {
			String s = opts[i];
			String[] parts = s.split(Util.PROTOCAL_SP);
			String key = parts[0];
			String val = parts[1] + Util.PROTOCAL_SP + parts[2] + Util.PROTOCAL_SP + parts[3];
			userList.put(key, val);
		}
	}
	
	public String toString() {
		String str = "";
		Set<Map.Entry<String, String>> userListSet = userList.entrySet();
		Iterator<Map.Entry<String,String>> iter= userListSet.iterator();
		while(iter.hasNext()) {
			Map.Entry<String, String> user = iter.next();
			String[] opts = user.getValue().split(Util.PROTOCAL_SP);
			str += user.getKey() + Util.PROTOCAL_SP + opts[0] + Util.PROTOCAL_SP + opts[1] + Util.PROTOCAL_SP + opts[2] + Util.PROTOCAL_LINEEND;
		}
		return str;
	}
}
