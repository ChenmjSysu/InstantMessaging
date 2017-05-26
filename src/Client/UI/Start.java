package Client.UI;

import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.WindowConstants;
import java.awt.Toolkit;

import Client.Client;
import Client.UI.*;
import Common.MessageItem.ALIGN_TYPE;
import Common.PROTOCOL_MESSAGE_TYPE;
import Common.Util;
import javafx.scene.control.Alert.AlertType;

public class Start {
	public Client client;
    Timer timer = new Timer();
    
	public Login login;
	public Regist regist;
	public ChatRoom chat;
	
	public static void main(String[] args) {
		if (args[0].equals("-h")) {
			System.out.println("Usage:");
			System.out.println("\tServerPort ServerIP");
			System.exit(0);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Start object = new Start(args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public Start(String[] args) throws Exception {
		Util.log("init Client");
		if (args.length >= 2) {
			client = new Client(this, args[0], Integer.parseInt(args[1]));
		}
		else {
			client = new Client(this);
		}
		Util.log("init Login UI");
		login = new Login(this);
		Util.log("init Regist UI");
		regist = new Regist(this);
		Util.log("init ChatRoom UI");
		chat = new ChatRoom(this);
		
		chat.setVisible(false);
		regist.setVisible(false);

		
		// 登录按钮的响应函数
		login.loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String pwd = login.pwdTextField.getText();
				String userName = login.userNameTextField.getText();
				if (!pwd.equals("") && !userName.equals("")) {
					try {
						PROTOCOL_MESSAGE_TYPE flag = client.login(userName, pwd);
						if (flag == PROTOCOL_MESSAGE_TYPE.LOGIN_SUCCESS) { // 登录成功 关闭登录窗口 打开聊天窗口
							login.setVisible(false);
							chat.setVisible(true);
							chat.setTitle(client.userName + chat.getTitle());
							if (chat.user2EditMessageList.size() <= 1) {
								// chat.sendButton.setEnabled(false);
							}
							else {
								chat.sendButton.setEnabled(true);
								chat.userListPane.setCurrentUser(0);
							}
							// 开始每1秒检查一下在线列表
							timer.schedule(new CheckOnlineUserList(), 0, 1000);
							
						}
						else if (flag == PROTOCOL_MESSAGE_TYPE.LOGIN_FAIL_NO_USERNAME) {
							JOptionPane.showMessageDialog(null, "Login Fail\nUserName not exist", "Error", 0);
						}
						else if (flag == PROTOCOL_MESSAGE_TYPE.LOGIN_FAIL_ERROR_PASSWORD) {
							JOptionPane.showMessageDialog(null, "Login Fail\nPassword is incorrect", "Error", 0);
						}
						else { // 登录失败 弹出提示框
							JOptionPane.showMessageDialog(null, "Login Fail", "Error", 0);
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		login.registButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				login.setVisible(false);
				regist.setVisible(true);
			}
			
		});

		// 登录按钮的响应函数
		regist.loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				regist.setVisible(false);
				login.setVisible(true);
			}
		});
		//  注册按钮
		regist.registButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String pwd = regist.pwdTextField.getText();
				String pwdAgain = regist.pwdTextFieldAgain.getText();
				String userName = regist.userNameTextField.getText();
				if (!pwd.equals("") && pwd.equals(pwdAgain) && !userName.equals("")) {
					try {
						PROTOCOL_MESSAGE_TYPE flag = client.regist(userName, pwd, pwdAgain);
						if (flag == PROTOCOL_MESSAGE_TYPE.REGIST_SUCCESS) { //  注册成功 关闭登录窗口 打开聊天窗口
							JOptionPane.showMessageDialog(null, "Regist Success", "Success", JOptionPane. PLAIN_MESSAGE);
							login.setVisible(true);
							regist.setVisible(false);
						}
						else if (flag == PROTOCOL_MESSAGE_TYPE.REGIST_FAIL_EXIST_USERNAME) {
							JOptionPane.showMessageDialog(null, "Regist Fail\nUsername Exist", "Error", 0);
						}
						else if (flag == PROTOCOL_MESSAGE_TYPE.REGIST_FAIL_PASSWORD_MISMATCH) { // 登录失败 弹出提示框
							JOptionPane.showMessageDialog(null, "Regist Fail\nTwo Password is noy the same", "Error", 0);
						}
						else {
							JOptionPane.showMessageDialog(null, "Regist Fail", "Error", 0);
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});

		
		// 发送消息
		JButton sendButton = chat.sendButton;
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});

	}
	
	public void sendMessage() {
		String message = chat.messageSendTextArea.getText();
		String userName = chat.titleLabel.getText();
		
		try {
			client.sendP2PMessage(userName, message);
			chat.addP2PTextMessage(chat.titleLabel.getText(), client.userName, Util.getFormattedCurrentDateTime(), message, ALIGN_TYPE.right);
			chat.messageSendTextArea.setText("");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	class CheckOnlineUserList extends TimerTask {
		@Override
	    public void run() {
			// chat.setList(client.userList);
		}
	}
}


