package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import domain.Algorithm;
import gram.tfg.tfgLexer;
import gram.tfg.tfgParser;
import semantic.tfgGUI.EvalVisitorGUI;
import semantic.tfgGUI.State;

import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.util.ListIterator;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;

public class AlgorithmVisu extends JFrame {
	
	private final int TIMER_SPEED = 500;
	
	private JPanel contentPane;
	private JSplitPane splitPane;
	private JPanel panel;
	private JButton btnPlay;
	private JButton btnForward;
	private JPanel panelAlgo;
	private JSplitPane splitPane_1;
	private RSyntaxTextArea algoText;
	private RTextScrollPane sp;
	private JScrollPane scrollPane;
	private JTextArea textAreaDebug;
	private JLabel lblVisualization;
	private Timer timerVisualization;
	private tfgLexer lexer;
	private tfgParser parser;
	private ParseTree tree;
	private EvalVisitorGUI visitor;
	private ListIterator<State> states;
	private JButton btnBackward;
	private int mode = Mode.DEFAULT;
	private JButton btnStop;
	private JButton btnPause;

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
		
		algoText = new RSyntaxTextArea();
		algoText.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
		algoText.setCodeFoldingEnabled(true);
		panelAlgo.setLayout(new BorderLayout(0, 0));
		sp = new RTextScrollPane(algoText);
		panelAlgo.add(sp);
		
		panel = new JPanel();
		panelAlgo.add(panel, BorderLayout.SOUTH);
		
		btnPlay = new JButton("Play");
		btnPlay.addActionListener(new BtnPlayActionListener());
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.add(btnPlay);
		
		btnStop = new JButton("Stop");
		btnStop.addActionListener(new BtnStopActionListener());
		panel.add(btnStop);
		
		btnPause = new JButton("Pause");
		btnPause.addActionListener(new BtnPauseActionListener());
		
		btnBackward = new JButton("Backward");
		btnBackward.setEnabled(false);
		btnBackward.addActionListener(new BtnBackwardActionListener());
		panel.add(btnBackward);
		panel.add(btnPause);
		
		btnForward = new JButton("Forward");
		btnForward.addActionListener(new BtnStepByStepActionListener());
		panel.add(btnForward);
		
		algoText.setText(algorithm.getAlgorithm());
		
		timerVisualization = new Timer(TIMER_SPEED, new BtnPlayTimerActionListener ());
		
	}
	
	public void autoButtons(){
		switch (mode){
		case Mode.PAUSE:
			btnPlay.setEnabled(true);
			btnBackward.setEnabled(true);
			break;
		case Mode.DEFAULT:
			btnPlay.setEnabled(true);
			algoText.setEnabled(true);
			btnBackward.setEnabled(false);
			break;
		case Mode.PLAY:
			btnPlay.setEnabled(false);
			algoText.setEnabled(false);
			btnBackward.setEnabled(true);
		case Mode.STEP:
			algoText.setEnabled(false);
			btnBackward.setEnabled(true);
			break;
			
		}
		
	}
	
	public void parseAlgorithm(){
		if (mode == Mode.DEFAULT){
			lexer = new tfgLexer(CharStreams.fromString(algoText.getText()));
			parser = new tfgParser(new CommonTokenStream(lexer));
			tree = parser.start();
			visitor = new EvalVisitorGUI();
			visitor.visit(tree);
			states = visitor.getStates().listIterator();
			
			textAreaDebug.setText("");
		}
	}
	
	private class BtnPlayActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			parseAlgorithm();
			mode = Mode.PLAY;
			
			timerVisualization.start();
			
			autoButtons();
		}
	}
	private class BtnStepByStepActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			State s=null;
			parseAlgorithm();
			mode = Mode.STEP;
			autoButtons();
			if (states.hasNext()){
	    		s = states.next();
	    		try {
					algoText.moveCaretPosition(algoText.getLineStartOffset(s.getNline()));
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    		textAreaDebug.setText(textAreaDebug.getText()+s+"\n");
	    	}else{
	    		mode = Mode.DEFAULT;
	    		autoButtons();
	    	}
		}
	}
	private class BtnStopActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			mode = Mode.DEFAULT;
			autoButtons();
			timerVisualization.stop();
		}
	}
	private class BtnPauseActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (mode != Mode.DEFAULT){
				mode = Mode.PAUSE;
				timerVisualization.stop();
			}
			autoButtons();
		}
	}
	private class BtnBackwardActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			State s=null;
			mode = Mode.STEP;
			autoButtons();

			if (states.hasPrevious()){
	    		s = states.previous();
	    		try {
					algoText.moveCaretPosition(algoText.getLineStartOffset(s.getNline()));
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    		textAreaDebug.setText(textAreaDebug.getText()+s+"\n");
	    	}else{
	    		mode = Mode.DEFAULT;
	    		autoButtons();
	    	}
		}
	}

	private class BtnPlayTimerActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			State s = null;
			if (states.hasNext()) {
				s = states.next();
				try {
					algoText.moveCaretPosition(algoText.getLineStartOffset(s.getNline()));
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				textAreaDebug.setText(textAreaDebug.getText() + s + "\n");
			} else {
				mode = Mode.DEFAULT;
				autoButtons();
				timerVisualization.stop();
			}

		}
	}
	
}
