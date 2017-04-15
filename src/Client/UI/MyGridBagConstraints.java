package Client.UI;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class MyGridBagConstraints extends GridBagConstraints {
	public MyGridBagConstraints(int x, int y) {
		this.gridx = x;
		this.gridy = y;
		this.fill = this.BOTH;
	}
	
	public MyGridBagConstraints(int x, int y, int width, int height) {
		this.gridx = x;
		this.gridy = y;
		this.gridwidth = width;  // 设置组件水平方向所占的格子数，如果为0，说明该组件是改行的最后一个
		this.gridheight = height; 
		
		this.fill = this.BOTH;
	}
	
	public MyGridBagConstraints setWeight(double weightx, double weighty) {  
		this.weightx = weightx;  // 设置组件水平的拉伸幅度，如果为0说明不拉伸，不为0就随着窗口增大进行拉伸，0～1
		this.weighty = weighty;  
		return this;  
	}  
	
	public MyGridBagConstraints setIpad(int ipadx, int ipady) {  
		this.ipadx = ipadx;  
		this.ipady = ipady;  
		return this;  
	}  
	
	public MyGridBagConstraints setInsets(int d) {
		this.insets = new Insets(d, d, d, d);
		return this;
	}
	
	public MyGridBagConstraints setInsets(int top, int left, int bottom, int right) {
		this.insets = new Insets(top, left, bottom, right);
		return this;
	}
	
	public MyGridBagConstraints setAnchor(int anchor) {
		this.anchor = anchor;
		return this;
	}
	
	
}
