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
import Common.Util;

public class Start {
	private Client client;
    Timer timer = new Timer();
    
	public Login login;
	public ChatRoom chat;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Start object = new Start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public Start() throws Exception {
		client = new Client(this);
		login = new Login();
		chat = new ChatRoom();
		
		chat.setVisible(false);
		JButton loginButton = login.loginButton;
		
		// 登录按钮的响应函数
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String pwd = login.pwdTextField.getText();
				String userName = login.userNameTextField.getText();
				if (!pwd.equals("") && !userName.equals("")) {
					try {
						boolean flag = client.login(userName, pwd);
						if (flag) { // 登录成功 关闭登录窗口 打开聊天窗口
							login.setVisible(false);
							chat.setVisible(true);
							// 开始每1秒检查一下在线列表
							timer.schedule(new CheckOnlineUserList(), 0, 1000);
							
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

		
		// 发送消息
		JButton sendButton = chat.sendButton;
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = chat.messageSendTextArea.getText();
				String userName = chat.titleLabel.getText();
				
				try {
					client.sendP2PMessage(userName, message);
					chat.addP2PTextMessage(chat.titleLabel.getText(), client.userName, "", message);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	
	class CheckOnlineUserList extends TimerTask {
		@Override
	    public void run() {
			// chat.setList(client.userList);
		}
	}
}


