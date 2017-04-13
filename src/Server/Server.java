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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Common.ACTION_TYPE;
import Common.PROTOCOL_MESSAGE_TYPE;
import Common.Util;
import Protocol.GetUserListProtocol;
import Protocol.HelloProtocol;
import Protocol.LoginProtocol;

public class Server {
	// key: userName val: socket
	static Map<String, Socket> onlineUserList = new HashMap<String, Socket>();
	// key: userName, val: IP&TCP_PORT&UDP_PORT
	static Map<String, String> userList = new HashMap<String, String>();
	// key: userName, val: whether user is online
	static Map<String, String> beatTime = new HashMap<String, String>();
	
	Timer timer = new Timer();
	
	private static void startTCPSocket() throws Exception {
		int serverPort = 6789;
		String clientSentence;
		String modifiedSentence;
		ServerSocket welcomeSocket = new ServerSocket(serverPort);
		System.out.println("start Server(TCP) in port: " + serverPort);
		while(true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			
			clientSentence = inFromClient.readLine();
			modifiedSentence = clientSentence.toUpperCase() + "\n";
			int a;
			outToClient.writeBytes(modifiedSentence);
		}
	}
	
	private static void startUDPServer() throws Exception {
		int serverPort = 9876;
		DatagramSocket serverSocket = new DatagramSocket(serverPort);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		while(true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			String sentence = new String(receivePacket.getData());
			InetAddress IPAddress = receivePacket.getAddress();
			int clientPort = receivePacket.getPort();
			String modifiedSentence = sentence.toUpperCase();
			sendData = modifiedSentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientPort);
			serverSocket.send(sendPacket);
		}
	}
	


	private int checkUser(String name, String pwd) {
		return 0;
	}
	
	private String userLogin(String inFromClient, Socket connectionSocket) throws IOException {
		String status = "";
		String options[] = inFromClient.split(Util.PROTOCAL_LINEEND);
		String userName = options[1];
		String pwd = options[2];
		String port = options[3];
		String IP = connectionSocket.getInetAddress().getHostAddress();
		String userIPPort = IP + ":" + port;
		int ret = checkUser(userName, pwd);
		// userName and pwd are valid
		if (ret == 0) {
			userList.put(userName, userIPPort);
			LoginProtocol p = new LoginProtocol(PROTOCOL_MESSAGE_TYPE.LOGIN_SUCCESS);
			status = p.getContent();
			beatTime.put(userName, "NO");
			updateOnlineList(updateUserList(userName, 1, port, connectionSocket));
			onlineUserList.put(userName, connectionSocket);
			for(String key : onlineUserList.keySet()) {
				System.out.println(key);
			}
			timer.schedule(new CheckBeat(userName), 1000, 10000);
			return status;
		}
		
		return status;
	}
	
	// 从客户端发过来的消息获取用户名
	private String getUserName(String message){
        
        String []options = message.split("[\\n\\r]+")[0].split(" ");
        return options[2];
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
							Util.log("--waiting--");
						}
						Util.log("-----\n" + clientSentence + "-----");
						PROTOCOL_MESSAGE_TYPE state = Util.getAction(clientSentence);
						if (state == PROTOCOL_MESSAGE_TYPE.LOGIN_REQUEST) {
							userName = getUserName(clientSentence);
						}
						switch (state) {
						case HELLO:
							//handshake  say hello
							Util.log("Get HELLO from " + connectionSocket.getInetAddress().getHostAddress());
							HelloProtocol hello = new HelloProtocol(Util.SERVER_IP, Util.SERVER_PORT);
							outToClient.writeUTF(hello.getContent());
							outToClient.flush();
							break;
						case LOGIN_REQUEST:
							String content = userLogin(clientSentence, connectionSocket);
							outToClient.writeUTF(content);
                            outToClient.flush();
							break;
						case GET_USERLIST_REQUEST:
							GetUserListProtocol protocol = new GetUserListProtocol(userList);
							outToClient.writeUTF(protocol.getContent());
                            outToClient.flush();
						case BEAT:
							//keep beat
							break;
							
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private String updateUserList(String Name, int i, String Port_Num,  Socket connectionSocket){
        String Status = "";

        String Request_Line = "";


        Date Current_Date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String DateStr = formatter.format(Current_Date);
        String IP_Num;
        if (connectionSocket != null)
            IP_Num = connectionSocket.getInetAddress().getHostAddress();

        else
            IP_Num = "8888";
        String Header_Line = "Date" + " " + DateStr + "Content-Length" + " " + "0" + "\r\n";

        String Entity_Body = "\r\n";
        if (i == 0) 
            Request_Line = "5c1.0" + " " + "UPDATE" + " " + "0" + " " + Name + " " +  IP_Num + "," + Port_Num + "\r\n";
        else
            Request_Line = "5c1.0" + " " + "UPDATE" + " " + "1" + " " + Name + " "  + IP_Num + "," + Port_Num + "\r\n";

        Status += Request_Line + Header_Line + Entity_Body;
        return Status;
	}
	
	private void updateOnlineList(String status) throws  IOException{
        
        try {
            for(String key : onlineUserList.keySet()) {
                System.out.println("---------------------------------");
                Socket _socket = onlineUserList.get(key);
                DataOutputStream outToClient = new DataOutputStream(_socket.getOutputStream());
                outToClient.writeUTF(status + '\n');
                outToClient.flush();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
	
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
					userList.remove(userName);
					Socket failSocket = onlineUserList.get(userName);
					beatTime.remove(userName);
					String status = updateUserList(userName, 0, "8888", failSocket);
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
				userList.remove(userName);
				Socket failSocket = onlineUserList.get(userName);
				onlineUserList.remove(userName);
				beatTime.remove(userName);
				String status = updateUserList(userName, 0, "8888", failSocket);
				try {
					updateOnlineList(status);
				} catch (Exception ee) {
					ee.printStackTrace();
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
	    ServerSocket welcomeSocket = new ServerSocket(Util.SERVER_PORT);
	    while(true){
	        Socket connectionSocket = welcomeSocket.accept();
	        server.process(connectionSocket);
	    }
		
	}
}
