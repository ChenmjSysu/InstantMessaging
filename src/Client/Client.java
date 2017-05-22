package Client;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Client.UI.Start;
import Common.ACTION_TYPE;
import Common.PROTOCOL_MESSAGE_TYPE;
import Common.UserItem;
import Common.UserList;
import Common.Util;
import Common.MessageItem.ALIGN_TYPE;
import Protocol.*;
import Protocol.UserStatusUpdateProtocol.USER_STATUS_TYPE;

public class Client {
	Start startUI;
	
	String fromServer;
	String toServer;
	String fromUser;
	Socket clientSocket;
	Socket p2pSocket;
	ServerSocket welcomeSocket;
	DatagramSocket udpSocket;
	DataOutputStream outToServer;
	DataOutputStream outToP2PServer;
	DataInputStream inFromServer;
	DataInputStream inFromUser;
	DataInputStream inFromP2PUser;
	String localIP;
	int localTCPPort;
	int localUDPPort;

	Socket connectionSocket;
	List onlineList = new LinkedList();
	
	
	public static String userName = new String();
	public static boolean connecting = false;
	public static Map<String, String> userList = new HashMap<String, String>();
	static Map<String, String> userListChating = new HashMap<String, String>();
	static Map<String, String> beatTime = new HashMap<String, String>();
	
	Timer timer = new Timer();
	Timer p2pTimer = new Timer();
	
	// 向服务器发送hello消息，创建一个ServerSocket接受其他终端的消息
	public boolean hello() throws Exception {
		// 与服务器的交互
		clientSocket = new Socket(Util.SERVER_IP, Util.SERVER_PORT);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new DataInputStream(clientSocket.getInputStream());
		InetAddress localAddress = InetAddress.getLocalHost();
		localIP = localAddress.getHostAddress();
		HelloProtocol hello = new HelloProtocol(Util.SERVER_IP, Util.SERVER_PORT);
		welcomeSocket = new ServerSocket(0);
		localTCPPort = welcomeSocket.getLocalPort();
		outToServer.writeUTF(hello.getContent());
		outToServer.flush();
		while(true) {
			fromServer = inFromServer.readUTF();
			if (fromServer != null && fromServer.length() >= 0) {
				PROTOCOL_MESSAGE_TYPE action = Util.getAction(fromServer);
				if (action == PROTOCOL_MESSAGE_TYPE.HELLO) {
					return true;
				}
				else {
					Util.log("Connect Fail");
					return false;
				}
			}
		}
	}
	
	// 监听hello时候建立的socket收到的消息，然后送给process函数处理
	public void StartTCPListener() throws IOException {
		Util.log("Start TCPListener");
		new Thread(new Runnable(){
            public void run(){
                try{
                    while (connecting){
                        connectionSocket = welcomeSocket.accept();
                        tcpProcess(connectionSocket);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
	}
	
	public void StartUDPListener() throws IOException {
		Util.log("Start UDPListener");
		new Thread(new Runnable(){
            public void run(){
                try{
                	udpSocket = new DatagramSocket(0);
                	localUDPPort = udpSocket.getLocalPort();
                    byte[] buf = new byte[1000];
                	DatagramPacket dp = new DatagramPacket(buf, buf.length);
                    while (connecting){
	                    udpSocket.receive(dp);
	    
	                    String udpContent = new String(dp.getData(), 0, dp.getLength());
	                    // Util.log(udpContent);
                        udpProcess(udpSocket, udpContent);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
	}
	
	// 接受服务器消息
	private void serverListen() throws IOException{
        new Thread(new Runnable(){
        	public void run(){
        		try {
        			while(true) {
        				while((fromServer = inFromServer.readUTF())!=null && fromServer.length()>0) {
        					// Util.log(fromServer);
        					PROTOCOL_MESSAGE_TYPE state = Util.getAction(fromServer);
    						switch (state) {
    						case LOGIN_SUCCESS:
								break;
    						case USER_STATUS_UPDATE:
    							updateUser(fromServer);
    							break;
    						case GET_USERLIST_SUCCESS:
    							UserList l = new UserList(fromServer);
    							userList = l.userList;
    							startUI.chat.setList(userList, userName);
    							break;
    						default:
    							Util.log("unknow protocol");
    							break;
    						}
        				}
        			}
        		} catch(IOException e){
                    e.printStackTrace();
        		}
        	}
                
        }).start();
	}
	
	// 发送登录消息 成功则返回true，返则false
	public boolean login(String name, String password) throws Exception {
		if(connecting) {
			
			StartTCPListener();
			StartUDPListener();
			try {
				Util.log(name + " try to login in");
				userName = name;
				while (localTCPPort == 0 || localUDPPort == 0) {}
				LoginProtocol login = new LoginProtocol(name, password, localTCPPort, localUDPPort);
				outToServer = new DataOutputStream(clientSocket.getOutputStream());
				inFromServer = new DataInputStream(clientSocket.getInputStream());
				outToServer.writeUTF(login.getContent());
				outToServer.flush();
				
				// wait for response
				while((fromServer = inFromServer.readUTF()) == null && fromServer.length() <= 0) {
						// do nothing
					
				}
				if (fromServer != null) {
					PROTOCOL_MESSAGE_TYPE action = Util.getAction(fromServer);
					if (action == PROTOCOL_MESSAGE_TYPE.LOGIN_SUCCESS) {
						serverListen();
						heartBeat(clientSocket);
						getUserList();
						return true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	// 发送注册消息 成功则返回true，返则false
	public boolean regist(String name, String password, String passwordAgain) throws Exception {
		if(connecting) {
			// StartTCPListener();
			// StartUDPListener();
			try {
				Util.log(name + " try to regist");
				userName = name;
				// while (localTCPPort == 0 || localUDPPort == 0) {}
				RegistProtocol regist = new RegistProtocol(name, password, passwordAgain);
				outToServer = new DataOutputStream(clientSocket.getOutputStream());
				inFromServer = new DataInputStream(clientSocket.getInputStream());
				outToServer.writeUTF(regist.getContent());
				outToServer.flush();
				
				// wait for response
				while((fromServer = inFromServer.readUTF()) == null && fromServer.length() <= 0) {
						// do nothing
				}
				if (fromServer != null) {
					PROTOCOL_MESSAGE_TYPE action = Util.getAction(fromServer);
					if (action == PROTOCOL_MESSAGE_TYPE.REGIST_SUCCESS) {
						return true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void getUserList() throws IOException {
		GetUserListProtocol protocol = new GetUserListProtocol();
		outToServer.writeUTF(protocol.getContent());
		outToServer.flush();
    }
	
	// 每隔9秒发一个beat
	public void heartBeat(Socket dstSocket) throws IOException {
		timer = new Timer(true);
		BeatProtocol beat = new BeatProtocol(userName);
		DataOutputStream outTo = new DataOutputStream(dstSocket.getOutputStream());
		timer.schedule(
				new TimerTask() {
					public void run() {
						try {
							outTo.writeUTF(beat.getContent());
							outTo.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
		, 0, 9 * 1000);
	}
	
	public void sendP2PMessage(String toUser, String message) throws IOException {
		String info = userList.get(toUser);
		UserItem user = new UserItem(toUser, info);
		
		P2PTextMessageProtocol protocol = new P2PTextMessageProtocol(userName, message);
		byte[] buf = protocol.getContent().getBytes();
		
		DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, user.ip, user.udp);

		udpSocket.send(sendPacket);
		
	}
	
	public void updateUser(String s) {
		UserStatusUpdateProtocol protocol = new UserStatusUpdateProtocol(s);
		if (protocol.status == USER_STATUS_TYPE.ONLINE) {
			UserItem item = new UserItem(protocol.userName, protocol.ip, protocol.tcpPort, protocol.udpPort);
			userList.put(protocol.userName, item.toString());
			startUI.chat.addUser(protocol.userName);
		}
		else if (protocol.status == USER_STATUS_TYPE.OFFLINE) {
			userList.remove(protocol.userName);
			startUI.chat.removeUser(protocol.userName);
			Util.log(protocol.userName + "  logout");
		}
		else {
			Util.log("UserStatusUpdateProtocol type error");
		}
	}
	
	// this process work for TCP connection
	public void tcpProcess(final Socket connectionSocket) throws IOException {
		new Thread(new Runnable() {
			public void run() {
				boolean flag = true;
				
				try {
					timer.purge();
					DataInputStream inFromStream = new DataInputStream(connectionSocket.getInputStream());
					DataOutputStream outToStream = new DataOutputStream(connectionSocket.getOutputStream());
					while(flag) {
						String sentence;
						
						// wait for response
						while ((sentence = inFromStream.readUTF())==null & sentence.length()<=0){
							//do nothing
                        }
						
						//process the response
						PROTOCOL_MESSAGE_TYPE state = Util.getAction(sentence);
						switch (state) {
						
						default:
							break;
						}
					}
				} catch(IOException e){
                    e.printStackTrace();
                } 
			}
		}).start();
	}
	
	// this process work for UDP connection
		public void udpProcess(final DatagramSocket socket, String sentence) throws IOException {
			new Thread(new Runnable() {
				public void run() {
					try {
						timer.purge();
						
						//process the response
						PROTOCOL_MESSAGE_TYPE state = Util.getAction(sentence);
						switch (state) {
						case P2P_TEXT_MESSAGE:
							P2PTextMessageProtocol protocol = new P2PTextMessageProtocol(sentence);
							startUI.chat.addP2PTextMessage(protocol.userName, protocol.userName, protocol.dateStr, protocol.message, ALIGN_TYPE.left);
							break;
						}
						
					} catch(Exception e){
	                    e.printStackTrace();
	                } 
				}
			}).start();
		}
	
	public Client(Start s) throws Exception {
		Util.log("Say hello to Server");
		startUI = s;
		connecting = hello();
	}
	
	public static void main(String args[]) throws Exception {
		//UDPSend();
		Client client = new Client(null);
		
		if(connecting) {
			Util.log("Connect Success");
			client.StartTCPListener();
			userName = "chen";
			if (client.login(userName, "pwd")) {
//				client.serverListen();
//				client.heartBeat(client.clientSocket);
			}
		}
		else {
			Util.log("Connect Fail");
		}
	}
	
	private static void TCPSend() throws Exception {
		String sentence;
		String modifiedSetence;
		int port = 6789;
		String serverIp = "localhost";
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		Socket clientSocket = new Socket(serverIp, port);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		sentence = inFromUser.readLine();
		outToServer.writeBytes(sentence + "\n");
		modifiedSetence = inFromServer.readLine();
		
		System.out.println("From Server: " + modifiedSetence);
		
		clientSocket.close();
	}
	
	
	private static void UDPSend() throws Exception {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		String serverIp  = "localhost";
		int serverPort = 9876;
		InetAddress IPAddress = InetAddress.getByName(serverIp);
		
		byte[] sendData = new byte[2014];
		byte[] receiveData = new byte[1024];
		
		String sentence = inFromUser.readLine();
		sendData = sentence.getBytes();
		
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);
		
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		
		clientSocket.receive(receivePacket);
		
		String modifiedSentence = new String(receivePacket.getData());
		
		System.out.println("From Server: " + modifiedSentence);
		clientSocket.close();
	}
}
