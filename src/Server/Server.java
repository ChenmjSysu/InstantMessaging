package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Common.ACTION_TYPE;
import Common.PROTOCOL_MESSAGE_TYPE;
import Common.UserItem;
import Common.Util;
import Protocol.BeatProtocol;
import Protocol.GetUserListProtocol;
import Protocol.HelloProtocol;
import Protocol.LoginProtocol;
import Protocol.RegistProtocol;
import Protocol.UserStatusUpdateProtocol;
import Protocol.UserStatusUpdateProtocol.USER_STATUS_TYPE;

public class Server {
	// key: userName val: socket
	static Map<String, Socket> onlineUserList = new HashMap<String, Socket>();
	// key: userName, val: IP&TCP_PORT&UDP_PORT
	static Map<String, String> userList = new HashMap<String, String>();
	// key: userName, val: whether user is online
	static Map<String, String> beatTime = new HashMap<String, String>();
	
	Timer timer = new Timer();
	
	private int databasePort = 3306;
	private String databaseDriven = "com.mysql.jdbc.Driver";
	private String databaseURL = "jdbc:mysql://localhost:3306/instantMessage?useSSL=false";
	private String databaseUserName = "root";
	private String databasePassword = "mysql";
	private Connection dbConnection = null;
	
	private int tcpPort;
	private String tcpIp;
	
	
	private void initDatabase() {
		try {
			Class.forName(databaseDriven); 
			dbConnection = DriverManager.getConnection(databaseURL, databaseUserName, databasePassword);
		} catch (SQLException e) {
			Util.log("Connect Database Fail.");
			System.exit(1);
		}
		catch (ClassNotFoundException e) {
			Util.log(databaseDriven + " ClassNotFoundException");
			System.exit(1);
		}
	}
	
	private int checkUser(String name, String pwd) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(pwd.getBytes());
		String pwdMd5 = new BigInteger(1, md.digest()).toString(16);
		String sql = "SELECT * FROM users WHERE username='" + name + "' AND password='" + pwdMd5 + "'";
		Statement statement;
		try {
			statement = dbConnection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			result.last();
			int count = result.getRow();
			return count;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	private int addUser(String name, String pwd) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(pwd.getBytes());
		String pwdMd5 = new BigInteger(1, md.digest()).toString(16);
		if (checkUser(name, pwd) > 0) return -2;
		String sql = "INSERT INTO users (username, password) VALUES ('" + name + "', '" + pwdMd5 + "')";
		Statement statement;
		try {
			statement = dbConnection.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
	private String userLogin(String inFromClient, Socket connectionSocket) throws IOException, NoSuchAlgorithmException {
		String status = "";
		LoginProtocol protocol = new LoginProtocol(inFromClient);
		InetAddress IP = connectionSocket.getInetAddress();
		String userInfo = new UserItem(protocol.userName, IP, protocol.tcpPort, protocol.udpPort).toString();

		int ret = checkUser(protocol.userName, protocol.pwd);
		// userName and pwd are valid
		if (ret == 1) {
			// 保存用户的信息
			userList.put(protocol.userName, userInfo);
			LoginProtocol p = new LoginProtocol(PROTOCOL_MESSAGE_TYPE.LOGIN_SUCCESS);
			status = p.getContent();
			beatTime.put(protocol.userName, "YES");
			// 向其他用户发送新用户登录消息
			updateOnlineList(genUserUpdateMessage(protocol.userName, USER_STATUS_TYPE.ONLINE));
			// 保存新用户的socket，以后续向该用户发送消息
			onlineUserList.put(protocol.userName, connectionSocket);
			//for(String key : onlineUserList.keySet()) {
			//	System.out.println(key);
			//}
			// 开始监测该用户的beat 每10s监测一次
			timer.schedule(new CheckBeat(protocol.userName), 1000, 10000);
			return status;
		}
		else {
			LoginProtocol p = new LoginProtocol(PROTOCOL_MESSAGE_TYPE.LOGIN_FAIL_NO_USERNAME);
			return p.getContent();
		}
	}
	
	private String userRegist(String inFromClient, Socket connectionSocket) throws IOException, NoSuchAlgorithmException {
		RegistProtocol protocol = new RegistProtocol(inFromClient);
		InetAddress IP = connectionSocket.getInetAddress();
		String userName = protocol.userName;
		String pwd = protocol.pwd;
		String pwdAgain = protocol.pwdAgain;
		RegistProtocol returnProtocol;
		if (!pwd.equals(pwdAgain))  {
			returnProtocol = new RegistProtocol(PROTOCOL_MESSAGE_TYPE.REGIST_FAIL_PASSWORD_MISMATCH);
			return returnProtocol.getContent();
		}

		int ret = checkUser(protocol.userName, protocol.pwd);
		// userName and pwd are valid, no username exist
		if (ret == 0) {
			int r = addUser(userName, pwd);
			if (r == 0) {
				returnProtocol = new RegistProtocol(PROTOCOL_MESSAGE_TYPE.REGIST_SUCCESS);
				Util.log("Regist Success " + userName);
				return returnProtocol.getContent();
			}
			else {
				Util.log("Regist Fail " + userName);
				returnProtocol = new RegistProtocol(PROTOCOL_MESSAGE_TYPE.REGIST_FAIL);
				return returnProtocol.getContent();
			}
		}
		else {
			Util.log("username: " + userName + " already existed.");
			returnProtocol = new RegistProtocol(PROTOCOL_MESSAGE_TYPE.REGIST_FAIL_EXIST_USERNAME);
			return returnProtocol.getContent();
		}
	}
	

	// 处理单个TCP连接的线程
	private void process(final Socket connectionSocket) throws IOException {
		new Thread(new Runnable() {
			public void run() {
				boolean flag_ = true;
				String userName = "";
				try {
					timer.purge();
					DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());
					DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
					while(flag_) {
						String clientSentence;
						while((clientSentence = inFromClient.readUTF()) == null
								&& clientSentence.length() <= 0) {
						}
						// Util.log("-----\n" + clientSentence + "-----");
						PROTOCOL_MESSAGE_TYPE state = Util.getAction(clientSentence);
//						if (state == PROTOCOL_MESSAGE_TYPE.LOGIN_REQUEST) {
//							LoginProtocol protocol = new LoginProtocol(clientSentence);
//							userName = protocol.userName;
//						}
						switch (state) {
						case HELLO:
							//handshake  say hello
							Util.log("Get HELLO from " + connectionSocket.getInetAddress().getHostAddress());
							HelloProtocol hello = new HelloProtocol(tcpIp, tcpPort);
							outToClient.writeUTF(hello.getContent());
							outToClient.flush();
							break;
						case LOGIN_REQUEST:
							String loginContent = userLogin(clientSentence, connectionSocket);
							outToClient.writeUTF(loginContent);
                            outToClient.flush();
							break;
						case REGIST_REQUEST:
							String registContent = userRegist(clientSentence, connectionSocket);
							outToClient.writeUTF(registContent);
							outToClient.flush();
							break;
						case GET_USERLIST_REQUEST:
							GetUserListProtocol protocol = new GetUserListProtocol(userList);
							outToClient.writeUTF(protocol.getContent());
                            outToClient.flush();
						case BEAT:
							//keep beat
							BeatProtocol beatProtocol = new BeatProtocol(clientSentence);
				            userName = beatProtocol.userName;
				            beatTime.put(userName, "YES");
							break;
						default:
							break;
							
						}
					}
				} catch(Exception e) {
					beatTime.put(userName, "NO");
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	// 生成用户状态更新的消息
	private String genUserUpdateMessage(String user, USER_STATUS_TYPE type) throws UnknownHostException{
        UserItem info = new UserItem(user, userList.get(user));
        UserStatusUpdateProtocol protocol = new UserStatusUpdateProtocol(info, type);
        return protocol.getContent();
	}
	
	// 发送消息给全部在线用户，来更新用户的在线列表
	private void updateOnlineList(String message) throws  IOException{
        try {
            for(String key : onlineUserList.keySet()) {
                Socket _socket = onlineUserList.get(key);
                DataOutputStream outToClient = new DataOutputStream(_socket.getOutputStream());
                outToClient.writeUTF(message + '\n');
                outToClient.flush();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
	
	// 每当有新用户登录 就开始不断监测新用户的beat
	// 每个用户一个线程，如果监测到这个用户的beat为no，则向其他用户发送下线消息，结束线程
	class CheckBeat extends TimerTask {
		private String userName;
		public CheckBeat(String userName) {
			this.userName = userName;
		}
		@Override
		public void run() {
			try {
				if (beatTime.get(userName) == null) this.cancel();
				else if (beatTime.get(userName).equals("NO")) {
					String status = genUserUpdateMessage(userName, USER_STATUS_TYPE.OFFLINE);
					userList.remove(userName);
					//Socket failSocket = onlineUserList.get(userName);
					beatTime.remove(userName);
					Util.log(userName + "logout");
					try {
						updateOnlineList(status);
					} catch(Exception e) {
						e.printStackTrace();
					}
					this.cancel();
				}
				else {
					beatTime.put(userName, "NO");
				}
			} catch(Exception e) {
				e.printStackTrace();
				userList.remove(userName);
				//Socket failSocket = onlineUserList.get(userName);
				onlineUserList.remove(userName);
				beatTime.remove(userName);
				String status;
				try {
					status = genUserUpdateMessage(userName, USER_STATUS_TYPE.OFFLINE);
					updateOnlineList(status);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				this.cancel();
			}
		}
	}


	public static void main(String args[]) throws Exception {
		if (args[0].equals("-h")) {
			System.out.println("Usage:");
			System.out.println("\tTCPPort MysqlUsername MysqlPassword MysqlPort");
			System.exit(0);
		}
	    Server server = new Server();
	    server.tcpPort = Util.SERVER_PORT;
	    server.tcpIp = InetAddress.getLocalHost().getHostAddress();
	    if (args.length >= 4) {
	    	// server.tcpIp = args[0];
	    	server.tcpPort = Integer.parseInt(args[0]);
	    	server.databaseUserName = args[1];
	    	server.databasePassword = args[2];
	    	server.databasePort = Integer.parseInt(args[3]);
	    	server.databaseURL = "jdbc:mysql://localhost:" + args[3] + "/instantMessage?useSSL=false";
	    }
	    Util.log("start Server(TCP) in [ip:port]: " + server.tcpIp + ":" + server.tcpPort);
	    Util.log("Mysql username: " + server.databaseUserName);
	    Util.log("Mysql password: " + server.databasePassword);
	    Util.log("Mysql port: " + server.databasePort);
	    server.initDatabase();
	    ServerSocket welcomeSocket = new ServerSocket(server.tcpPort);
	    while(true){
	        Socket connectionSocket = welcomeSocket.accept();
	        server.process(connectionSocket);
	    }
		
	}
}
