package Client.UI;

import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Common.Util;

public class Regist extends JFrame {
	private JLabel userNameLabel;
	public JTextField userNameTextField;
	private JLabel pwdLabel;
	public JTextField pwdTextField;
	private JLabel pwdLabelAgain;
	public JTextField pwdTextFieldAgain;
	
	private JLabel bkg;
	
	public JButton loginButton;
	public JButton registButton;
	
	public Start start;
	
	public Regist(Start s) {
		start = s;
		this.setTitle(Util.REGIST_WIN_TITLE);
		// 初始化窗体组建
		init();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 绝对定位
		this.setLayout(null);
		this.setBounds(0, 0, Util.REGIST_WIN_WIDTH, Util.REGIST_WIN_HEIGHT);
		this.setResizable(false);
		// 居中显示
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void init() {
		Container container = this.getContentPane();
		
		bkg = new JLabel();
		bkg.setBounds(0, 0, Util.REGIST_WIN_WIDTH, Util.REGIST_WIN_HEIGHT);
		// 用户名
		userNameLabel = new JLabel("User name");
		userNameLabel.setBounds(10, 50, 70, 20);
		userNameTextField = new JTextField();
		userNameTextField.setBounds(100, 50, 150, 20);
		// 密码
		pwdLabel = new JLabel("Password");
		pwdLabel.setBounds(10, 100, 70, 20);
		pwdTextField = new JTextField();
		pwdTextField.setBounds(100, 100, 150, 20);
		pwdLabelAgain = new JLabel("PasswordAgain");
		pwdLabelAgain.setBounds(10, 150, 70, 20);
		pwdTextFieldAgain = new JTextField();
		pwdTextFieldAgain.setBounds(100, 150, 150, 20);
		
		// 登陆按钮
		loginButton = new JButton("Login");
		loginButton.setBounds(20, 200, 65, 20);
		// 注册按钮
		registButton = new JButton("Regist");
		registButton.setBounds(120, 200, 65, 20);
		
		container.add(userNameLabel);
		container.add(userNameTextField);
		container.add(pwdLabel);
		container.add(pwdTextField);
		container.add(pwdLabelAgain);
		container.add(pwdTextFieldAgain);
		container.add(loginButton);
		container.add(registButton);
	}
	
}
