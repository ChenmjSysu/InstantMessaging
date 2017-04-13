package Client.UI;

import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.*;
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

public class Start {
	private Client client;
	
	private Login login;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Start object = new Start();
			}
		});
	}
	
	public Start() {
		login = new Login();
		JButton loginButton = login.loginButton; 
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String pwd = login.pwdTextField.getText();
				String userName = login.userNameTextField.getText();
				if (!pwd.equals("") && !userName.equals("")) {
					try {
						boolean flag = client.login(userName, pwd);
						if (flag) { // 登录成功 关闭登录窗口 打开列表窗口
							
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
	}
	
}
