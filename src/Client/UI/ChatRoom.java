package Client.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Client.Client;
import Common.MessageItem;
import Common.Util;

public class ChatRoom extends JFrame {
    private JTextArea messageShowTextArea;
    public JTextField messageSendTextArea;
    public JButton sendButton;
    
    public Map<String, String> user2EditMessageList;
    public Map<String, List<MessageItem>> user2SendMessageList;
    
    public UserListPane userListPane;
    public JLabel titleLabel;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatRoom frame = new ChatRoom();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	});
	}
	
	public ChatRoom() {
		this.setTitle("Chat");
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 绝对定位
		this.setBounds(0, 0, 2*Util.LOGIN_WIN_WIDTH, 2*Util.LOGIN_WIN_HEIGHT);
		this.setResizable(true);
		// 居中显示
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		// 初始化窗体组建
		init();
		
		user2EditMessageList = new HashMap<String, String>();
		user2SendMessageList = new HashMap<String, List<MessageItem>>(); 
	}
	
	private void init() {
		Container container = this.getContentPane();
		
		userListPane = new UserListPane(this);
		
		titleLabel = new JLabel("title");
		
		messageShowTextArea = new JTextArea();
		messageShowTextArea.setEditable(false);
		messageSendTextArea = new JTextField();
        sendButton = new JButton("Send");
        
        messageShowTextArea.setText("");
        messageSendTextArea.setText("");
        
        container.setLayout(new GridBagLayout());
        
        container.add(userListPane, new MyGridBagConstraints(0, 0, 1, 4).setIpad(70, 0).setWeight(20, 100));
        container.add(titleLabel, new MyGridBagConstraints(1, 0, 1, 1).setInsets(5, 20, 5, 20).setWeight(80, 20).setAnchor(MyGridBagConstraints.CENTER));
        container.add(messageShowTextArea, new MyGridBagConstraints(1, 1).setIpad(0, 50).setWeight(80, 600));
        container.add(messageSendTextArea, new MyGridBagConstraints(1, 2).setIpad(0, 20).setWeight(80, 260));
        container.add(sendButton, new MyGridBagConstraints(1, 3).setIpad(0, 20).setWeight(80, 20));
	}
	
	public void setList(Map<String, String> map) {
		userListPane.setUserList(map);
		
		for (String name : map.keySet()) {
			user2SendMessageList.put(name, new ArrayList<MessageItem>());
			user2EditMessageList.put(name, new String());
		}
	}
	
	public void removeUser(String name) {
		
	}
	
	public void addUser(String name) {
		userListPane.addUser(name);
		
		user2SendMessageList.put(name, new ArrayList<MessageItem>());
		user2EditMessageList.put(name, new String());
	}
	
	public void addP2PTextMessage(String user, String from, String dateStr, String message) {
		MessageItem m = new MessageItem(message, dateStr, from);
		((List<MessageItem>)user2SendMessageList.get(user)).add(m);
		// 如果当前打开的对话框就是该用户的 那么直接在显示框显示
		if (from.equals(titleLabel.getText())) {
			addMessage2TextArea(m);
		}
		messageShowTextArea.revalidate();
	}
	
	public void showCurrentUserMessage(String name) {
		// 如果当前的对话框与点击的不一样，也就是要切换
		if (!name.equals(titleLabel.getText())) {
			// 先清空
			messageShowTextArea.setText("");
			titleLabel.setText(name);
			for (MessageItem item : user2SendMessageList.get(name)) {
				addMessage2TextArea(item);
			}
			messageShowTextArea.revalidate();
		}
	}
	
	public void addMessage2TextArea(MessageItem item) {
		messageShowTextArea.append(item.fromUser + " : " + item.message + "\n");		
	}
	
}
