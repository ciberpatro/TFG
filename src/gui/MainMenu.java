package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import domain.Algorithm;
import drivers.AlgorithmReader;

import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ScrollPaneConstants;

public class MainMenu extends JFrame {

	private JPanel contentPane;
	private JPanel panel;
	private JComboBox<Vector<Algorithm>> cbAlgorithms;
	private JPanel panel_1;
	private JButton btnOk;
	private JButton btnExit;
	private JScrollPane scrollPane;
	private JTextArea txtDescription;
	/**
	 * Create the frame.
	 */
	public MainMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Select algorithm", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		contentPane.add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{10, 430, 10, 0};
		gbl_panel.rowHeights = new int[]{24, 219, 25, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 1;
		panel.add(scrollPane, gbc_scrollPane);
		
		txtDescription = new JTextArea();
		txtDescription.setLineWrap(true);
		txtDescription.setWrapStyleWord(true);
		scrollPane.setViewportView(txtDescription);
		
		
		panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.anchor = GridBagConstraints.NORTH;
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 2;
		panel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{53, 59, 0};
		gbl_panel_1.rowHeights = new int[]{25, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		btnOk = new JButton("Ok");
		btnOk.addActionListener(new BtnOkActionListener());
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnOk.anchor = GridBagConstraints.NORTH;
		gbc_btnOk.insets = new Insets(0, 0, 0, 5);
		gbc_btnOk.gridx = 0;
		gbc_btnOk.gridy = 0;
		panel_1.add(btnOk, gbc_btnOk);
		
		btnExit = new JButton("Exit");
		btnExit.addActionListener(new BtnExitActionListener());
		GridBagConstraints gbc_btnExit = new GridBagConstraints();
		gbc_btnExit.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnExit.anchor = GridBagConstraints.NORTH;
		gbc_btnExit.gridx = 1;
		gbc_btnExit.gridy = 0;
		panel_1.add(btnExit, gbc_btnExit);
		
		cbAlgorithms = new JComboBox();
		cbAlgorithms.addItemListener(new CbAlgorithmsItemListener());
		
		GridBagConstraints gbc_cbAlgorithms = new GridBagConstraints();
		gbc_cbAlgorithms.anchor = GridBagConstraints.NORTH;
		gbc_cbAlgorithms.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbAlgorithms.insets = new Insets(0, 0, 5, 5);
		gbc_cbAlgorithms.gridx = 1;
		gbc_cbAlgorithms.gridy = 0;
		panel.add(cbAlgorithms, gbc_cbAlgorithms);
		
		try {
			ArrayList<Algorithm> algoconfs =new AlgorithmReader().getAlgorithms();
			cbAlgorithms.setModel(new DefaultComboBoxModel(new Vector<Algorithm>(algoconfs)));
			Algorithm algorithm = (Algorithm) cbAlgorithms.getSelectedItem();
			txtDescription.setText(algorithm.getDescription());
		} catch (FileNotFoundException e) {
			//TODO ADD JDialog to show the FileNotFoundException
			e.printStackTrace();
		}
		
	}
	private class CbAlgorithmsItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent arg0) {
			Algorithm algorithm = (Algorithm)arg0.getItem();
			txtDescription.setText(algorithm.getDescription());
		}
	}
	private class BtnOkActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			Algorithm algorithm = (Algorithm)cbAlgorithms.getSelectedItem();
			AlgorithmVisu algovisu =new AlgorithmVisu(algorithm, MainMenu.this);
			algovisu.setVisible(true);
			setVisible(false);
		}
	}
	private class BtnExitActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}
}
