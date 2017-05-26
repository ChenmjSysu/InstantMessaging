package Client.UI;

import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Common.Util;

public class Login extends JFrame {
	private JLabel userNameLabel;
	public JTextField userNameTextField;
	private JLabel pwdLabel;
	public JPasswordField pwdTextField;
	
	private JLabel bkg;
	
	public JButton loginButton;
	public JButton registButton;
	
	public Start start;
	
	public Login(Start s) {
		start = s;
		this.setTitle(Util.LOGIN_WIN_TITLE);
		// 初始化窗体组建
		init();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 绝对定位
		this.setLayout(null);
		this.setBounds(0, 0, Util.LOGIN_WIN_WIDTH, Util.LOGIN_WIN_HEIGHT);
		this.setResizable(false);
		// 居中显示
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void init() {
		Container container = this.getContentPane();
		
		bkg = new JLabel();
		bkg.setBounds(0, 0, Util.LOGIN_WIN_WIDTH, Util.LOGIN_WIN_HEIGHT);
		// 用户名
		userNameLabel = new JLabel("User name");
		userNameLabel.setBounds(10, 50, 70, 20);
		userNameTextField = new JTextField();
		userNameTextField.setBounds(100, 50, 150, 20);
		// 密码
		pwdLabel = new JLabel("Password");
		pwdLabel.setBounds(10, 100, 70, 20);
		pwdTextField = new JPasswordField();
		pwdTextField.setBounds(100, 100, 150, 20);
		
		// 登陆按钮
		loginButton = new JButton("Login");
		loginButton.setBounds(20, 150, 65, 20);
		
		// 注册按钮
		registButton = new JButton("Regist");
		registButton.setBounds(120, 150, 65, 20);
		
		container.add(userNameLabel);
		container.add(userNameTextField);
		container.add(pwdLabel);
		container.add(pwdTextField);
		container.add(loginButton);
		container.add(registButton);
	}
	
}
