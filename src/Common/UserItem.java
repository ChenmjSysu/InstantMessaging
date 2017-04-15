package Common;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UserItem {
	public String userName;
	public InetAddress ip;
	public int tcp;
	public int udp;
	
	public UserItem(String name, String info) throws UnknownHostException {
		userName = name;
		String[] parts = info.split(Util.PROTOCAL_SP);
		ip = InetAddress.getByName(parts[0]);
		
		tcp = Integer.parseInt(parts[1]);
		udp = Integer.parseInt(parts[2]);
	}
	
	public UserItem(String name, InetAddress i, int t, int u) {
		userName = name;
		ip = i;
		tcp = t;
		udp = u;
	}
	
	public String toString() {
		return ip.getHostAddress() + Util.PROTOCAL_SP + tcp + Util.PROTOCAL_SP + udp;
	}
}
