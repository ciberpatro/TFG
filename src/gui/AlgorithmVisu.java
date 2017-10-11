package gui;

import java.awt.BorderLayout;
import java.awt.Component;

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
import semantic.tfg.Value;
import semantic.tfgGUI.EvalVisitorGUI;
import semantic.tfgGUI.State;

import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AlgorithmVisu extends JFrame {
	
	private final int TIMER_SPEED = 500;
	
	private JPanel contentPane;
	private JSplitPane splitAlgoMenu;
	private JPanel panel;
	private JButton btnPlay;
	private JButton btnForward;
	private JPanel panelAlgo;
	private JSplitPane splitStackLine;
	private RSyntaxTextArea algoText;
	private RTextScrollPane sp;
	private JScrollPane scrollLine;
	private JTextArea textAreaDebug;
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
	private JScrollPane scrollStack;
	private StackPanel pnlStack;
	private JFrame parent;
	
	public AlgorithmVisu(Algorithm algorithm, JFrame parent) {
		this.parent = parent;
		addWindowListener(new ThisWindowListener());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 864, 428);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		splitAlgoMenu = new JSplitPane();
		splitAlgoMenu.setResizeWeight(0.15);
		contentPane.add(splitAlgoMenu, BorderLayout.CENTER);
		
		panelAlgo = new JPanel();
		splitAlgoMenu.setLeftComponent(panelAlgo);
		
		splitStackLine = new JSplitPane();
		splitStackLine.setResizeWeight(0.5);
		splitStackLine.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitAlgoMenu.setRightComponent(splitStackLine);
		
		scrollLine = new JScrollPane();
		splitStackLine.setRightComponent(scrollLine);
		
		textAreaDebug = new JTextArea();
		scrollLine.setViewportView(textAreaDebug);
		
		scrollStack = new JScrollPane();
		splitStackLine.setLeftComponent(scrollStack);
		
		pnlStack = new StackPanel();
		scrollStack.setViewportView(pnlStack);
		GridBagLayout gbl_pnlStack = new GridBagLayout();
		gbl_pnlStack.columnWidths = new int[]{0};
		gbl_pnlStack.columnWeights = new double[]{Double.MIN_VALUE};
		
		gbl_pnlStack.rowHeights = new int[]{0};
		gbl_pnlStack.rowWeights = new double[]{Double.MIN_VALUE};
		pnlStack.setLayout(gbl_pnlStack);
		
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
	
	public void stackVisualization(State s){
		Set<Entry<String, Value>> stack = s.getVariables().entrySet();
		Set<Entry<String, GraphTFG>> graph_list = s.getGraphs().entrySet();
		StackLayout stackLayout = new StackLayout(stack.size()+graph_list.size());
		pnlStack.removeAll();
		pnlStack.setLayout(stackLayout);
		for (Entry<String, GraphTFG> e: graph_list){
			Component graphComp = e.getValue().getComponent();
			graphComp.setEnabled(false);
			pnlStack.addRow(new JLabel(e.getKey() +": "), graphComp);
		}
		for (Entry<String, Value> e: stack){
			pnlStack.addRow(new JLabel(e.getKey() +": "), new JLabel(e.getValue().toString()));
		}
		pnlStack.revalidate();
		pnlStack.repaint();
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
	    		stackVisualization(s);
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
	    		stackVisualization(s);
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
				stackVisualization(s);
			} else {
				mode = Mode.DEFAULT;
				autoButtons();
				timerVisualization.stop();
			}

		}
	}
	private class ThisWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent arg0) {
			parent.setVisible(true);
		}
	}
	
}
