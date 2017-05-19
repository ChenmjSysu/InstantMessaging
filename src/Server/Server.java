package Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
	
	private String databaseDriven = "com.mysql.jdbc.Driver";
	private String databaseURL = "jdbc:mysql://localhost:3306/instantMessage?useSSL=false";
	private String databaseUserName = "root";
	private String databasePassword = "mysql";
	private Connection dbConnection = null;
	
	
	private void initDatabase() throws ClassNotFoundException, SQLException {
		Class.forName(databaseDriven);
		dbConnection = DriverManager.getConnection(databaseURL, databaseUserName, databasePassword);
	}
	
//	private static void startTCPSocket() throws Exception {
//		int serverPort = 6789;
//		String clientSentence;
//		String modifiedSentence;
//		ServerSocket welcomeSocket = new ServerSocket(serverPort);
//		System.out.println("start Server(TCP) in port: " + serverPort);
//		while(true) {
//			Socket connectionSocket = welcomeSocket.accept();
//			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
//			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
//			
//			clientSentence = inFromClient.readLine();
//			modifiedSentence = clientSentence.toUpperCase() + "\n";
//			int a;
//			outToClient.writeBytes(modifiedSentence);
//		}
//	}
//	
//	private static void startUDPServer() throws Exception {
//		int serverPort = 9876;
//		DatagramSocket serverSocket = new DatagramSocket(serverPort);
//		byte[] receiveData = new byte[1024];
//		byte[] sendData = new byte[1024];
//		while(true) {
//			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//			serverSocket.receive(receivePacket);
//			String sentence = new String(receivePacket.getData());
//			InetAddress IPAddress = receivePacket.getAddress();
//			int clientPort = receivePacket.getPort();
//			String modifiedSentence = sentence.toUpperCase();
//			sendData = modifiedSentence.getBytes();
//			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientPort);
//			serverSocket.send(sendPacket);
//		}
//	}
//	


	private int checkUser(String name, String pwd) {
		String sql = "SELECT * FROM users WHERE username='" + name + "' AND password='" + pwd + "'";
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
	
	private int addUser(String name, String pwd) {
		if (checkUser(name, pwd) > 0) return -2;
		String sql = "INSERT INTO users (username, password) VALUES ('" + name + "', '" + pwd + "')";
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
	
	private String userLogin(String inFromClient, Socket connectionSocket) throws IOException {
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
	
	private String userRegist(String inFromClient, Socket connectionSocket) throws IOException {
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
							HelloProtocol hello = new HelloProtocol(Util.SERVER_IP, Util.SERVER_PORT);
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
					Util.log(userName + " offline");
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
		// startTCPSocket();
		// startUDPServer();
		Util.log("start Server(TCP) in port: " + Util.SERVER_PORT);
	    Server server = new Server();
	    server.initDatabase();
	    ServerSocket welcomeSocket = new ServerSocket(Util.SERVER_PORT);
	    while(true){
	        Socket connectionSocket = welcomeSocket.accept();
	        server.process(connectionSocket);
	    }
		
	}
}
