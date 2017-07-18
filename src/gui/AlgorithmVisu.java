package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import domain.Algorithm;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AlgorithmVisu extends JFrame {

	private JPanel contentPane;
	private JSplitPane splitPane;
	private JPanel panel;
	private JButton btnPlay;
	private JButton btnStepByStep;
	private JPanel panelAlgo;
	private JSplitPane splitPane_1;
	private RSyntaxTextArea textArea;
	private RTextScrollPane sp;
	private JScrollPane scrollPane;
	private JTextArea textAreaDebug;
	private JLabel lblVisualization;

	public AlgorithmVisu(Algorithm algorithm) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 864, 428);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.15);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		panelAlgo = new JPanel();
		splitPane.setLeftComponent(panelAlgo);
		
		splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);
		
		scrollPane = new JScrollPane();
		splitPane_1.setRightComponent(scrollPane);
		
		textAreaDebug = new JTextArea();
		scrollPane.setViewportView(textAreaDebug);
		
		lblVisualization = new JLabel("");
		splitPane_1.setLeftComponent(lblVisualization);
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{64, 125, 0};
		gbl_panel.rowHeights = new int[]{25, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		btnPlay = new JButton("Play");
		btnPlay.addActionListener(new BtnPlayActionListener());
		GridBagConstraints gbc_btnPlay = new GridBagConstraints();
		gbc_btnPlay.anchor = GridBagConstraints.NORTH;
		gbc_btnPlay.insets = new Insets(0, 0, 0, 5);
		gbc_btnPlay.gridx = 0;
		gbc_btnPlay.gridy = 0;
		panel.add(btnPlay, gbc_btnPlay);
		
		btnStepByStep = new JButton("Step by Step");
		GridBagConstraints gbc_btnStepByStep = new GridBagConstraints();
		gbc_btnStepByStep.anchor = GridBagConstraints.NORTH;
		gbc_btnStepByStep.gridx = 1;
		gbc_btnStepByStep.gridy = 0;
		panel.add(btnStepByStep, gbc_btnStepByStep);
		
		textArea = new RSyntaxTextArea();
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
		textArea.setCodeFoldingEnabled(true);
		panelAlgo.setLayout(new BorderLayout(0, 0));
		sp = new RTextScrollPane(textArea);
		panelAlgo.add(sp);
		
		textArea.setText(algorithm.getAlgorithm());
	}

	private class BtnPlayActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			
		}
	}
}
