package gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class StackPanel extends JPanel {
	private int y;
	public StackPanel() {
		super();
		this.y=0;
	}
	public void addRow(Component left, Component right){
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = ++y;
		c.anchor = GridBagConstraints.EAST;
		add(left, c);
		c.gridx = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(right, c);
	}
	public void setLayout(LayoutManager arg0) {
		super.setLayout(arg0);
		this.y=0;
	}
}
