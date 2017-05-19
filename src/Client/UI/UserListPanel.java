package Client.UI;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Common.Util;

public class UserListPanel extends JPanel {
	public String cunrrentUser = null;
	private ChatRoom chat;
	public UserListPanel(Map<String, String> userList) {
		init();
		for(String name : userList.keySet()) {
			this.addUser(name);
		}
	}
	
	public UserListPanel(ChatRoom c) {
		init();
		chat = c;
		this.setLayout(new GridLayout(10,  1));
	}
	
	public void init() {
		this.setBackground(Color.GRAY);
	}
	
	public void setUserList(Map<String, String> userList, String curName) {
		for(String name : userList.keySet()) {
			if (name.equals(curName)) {
				continue;
			}
			this.addUser(name);
		}
		this.revalidate();
	}
	
	public void removeUser(String name) {
		for (int i = 0; i < this.getComponentCount(); i++) {
			User tempUser = (User) this.getComponent(i);
			if (name != null && name.equals(tempUser.name)) {
				// tempUser.setBackground(Color.GREEN);
				this.remove(i);
			}
		}
		
		this.revalidate();
	}
	
	// 增加一个用户
	public void addUser(String name) {
		User u = new User(name);
		this.add(u);
		u.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				setCurrentUser(((User)e.getSource()).name);
				/*
				UserListPanel listPanel = (UserListPanel)((User)e.getSource()).getParent();
				
				String cur = listPanel.cunrrentUser;
				for (int i = 0; i < listPanel.getComponentCount(); i++) {
					User tempUser = (User) listPanel.getComponent(i);
					if (cur != null && cur == tempUser.name) { //将之前的item设置为灰色
						tempUser.setBackground(Color.GRAY);
					}
					//将当前点击的设置为红色
					((User)e.getSource()).setBackground(Color.RED);
					listPanel.cunrrentUser = ((User)e.getSource()).name;
			
					chat.showCurrentUserMessage(listPanel.cunrrentUser);
					if (tempUser.haveNewMessage == true) {
						tempUser.setText(tempUser.name);
						tempUser.haveNewMessage = false;
					}
				}
				*/
			}
		});
		this.revalidate();
	}

	public void setCurrentUser(String user) {
		UserListPanel listPanel = this;
		
		String cur = listPanel.cunrrentUser;
		for (int i = 0; i < listPanel.getComponentCount(); i++) {
			User tempUser = (User) listPanel.getComponent(i);
			if (cur != null && cur.equals(tempUser.name)) { //将之前的item设置为灰色
				tempUser.setBackground(Color.GRAY);
				listPanel.cunrrentUser = user;
			}
			else if (cur == null) {
				tempUser.setBackground(Color.GRAY);
				listPanel.cunrrentUser = user;
			}
			//将当前点击的设置为红色
			if (user.equals(tempUser.name)) {
				tempUser.setBackground(Color.RED);
			}
			if (tempUser.haveNewMessage == true) {
				tempUser.setText(tempUser.name);
				tempUser.haveNewMessage = false;
			}
		}
		Util.log(user + "----");
		chat.showCurrentUserMessage(listPanel.cunrrentUser);
	}
	public void setCurrentUser(int index) {
		UserListPanel listPanel = this;
		
		String user = ((User) listPanel.getComponent(index)).name;
		String cur = listPanel.cunrrentUser;
		for (int i = 0; i < listPanel.getComponentCount(); i++) {
			User tempUser = (User) listPanel.getComponent(i);
			if (cur != null && cur == tempUser.name) { //将之前的item设置为灰色
				tempUser.setBackground(Color.GRAY);
				listPanel.cunrrentUser = user;
			}
			//将当前点击的设置为红色
			if (user.equals(tempUser.name)) {
				tempUser.setBackground(Color.RED);
			}
			chat.showCurrentUserMessage(listPanel.cunrrentUser);
			if (tempUser.haveNewMessage == true) {
				tempUser.setText(tempUser.name);
				tempUser.haveNewMessage = false;
			}
		}
	}
	
	public void setHaveNewMessage(String from) {
		for (int i = 0; i < this.getComponentCount(); i++) {
			User tempUser = (User) this.getComponent(i);
			if (from != null && from.equals(tempUser.name) && tempUser.haveNewMessage == false) {
				tempUser.setText(from + " (new message)");
				tempUser.haveNewMessage = true;
				this.revalidate();
			}
		}
	}
	
	class User extends JPanel {
		JLabel userNameLabel;
		String name;
		boolean haveNewMessage = false;
		public User(String n) {
			name = n;
			this.setBackground(Color.GRAY);
			userNameLabel = new JLabel(n);
			this.add(userNameLabel);
		}
		
		public void setText(String s) {
			userNameLabel.setText(s);
		}
		public String getText() {
			return userNameLabel.getText();
		}
	}
}
