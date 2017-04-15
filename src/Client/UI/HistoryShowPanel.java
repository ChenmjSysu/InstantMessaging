package Client.UI;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Common.MessageItem;
import Common.MessageItem.ALIGN_TYPE;

public class HistoryShowPanel extends JPanel {
	public HistoryShowPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(Color.WHITE);
		this.addContent(new MessageItem("message", "now", "chen", ALIGN_TYPE.left));
		this.addContent(new MessageItem("message", "now", "chen", ALIGN_TYPE.left));
		this.addContent(new MessageItem("MESSAGE", "now", "chen", ALIGN_TYPE.right));
		this.addContent(new MessageItem("我", "now", "chen", ALIGN_TYPE.left));
		this.addContent(new MessageItem("message", "now", "chen", ALIGN_TYPE.left));
		
	}
	
	public void setContent(List<MessageItem> userList) {
		this.removeAll();
		for (MessageItem item : userList) {
			ShowItem i = new ShowItem(item);
			this.add(i);
		}
	}
	
	public void addContent(MessageItem item) {
		ShowItem i = new ShowItem(item);
		this.add(i);
	}
	
	class ShowItem extends JPanel {
		MessageItem item;
		JTextField userName;
		JTextField message;

		
		public ShowItem(MessageItem i) {
			item = i;
			userName = new JTextField(i.fromUser + " " + i.dateStr + ":");
			message = new JTextField(i.message);
			
			
			userName.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			
			userName.setEditable(false);
			message.setEditable(false);
			
			if (item.algin == ALIGN_TYPE.right) {
				message.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
				userName.setHorizontalAlignment(JLabel.RIGHT);
				message.setHorizontalAlignment(JLabel.RIGHT);
			}
			else {
				message.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
				userName.setHorizontalAlignment(JLabel.LEFT);
				message.setHorizontalAlignment(JLabel.LEFT);
			}
			
			this.setBackground(Color.WHITE);
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(userName);
			this.add(message);
		}
		
	}
}
