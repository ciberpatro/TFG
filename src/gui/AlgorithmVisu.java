package gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
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
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class AlgorithmVisu extends JFrame {
	
	private final int TIMER_DEFAULT_SPEED = -500;
	private final int TIMER_MIN_SPEED = -1000;
	private final int TIMER_MAX_SPEED = -10;
	
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
	private JPanel panel_1;
	private JSlider slider;
	
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
		panel.setLayout(new BorderLayout(0, 0));
		
		panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.NORTH);
		
		btnPlay = new JButton("Play");
		panel_1.add(btnPlay);
		
		btnStop = new JButton("Stop");
		panel_1.add(btnStop);
		
		btnBackward = new JButton("Backward");
		panel_1.add(btnBackward);
		btnBackward.setEnabled(false);
		
		btnPause = new JButton("Pause");
		panel_1.add(btnPause);
		
		btnForward = new JButton("Forward");
		panel_1.add(btnForward);
		
		slider = new JSlider();
		slider.setMinimum(TIMER_MIN_SPEED);
		slider.setMaximum(TIMER_MAX_SPEED);
		slider.setValue(TIMER_DEFAULT_SPEED);
		slider.addChangeListener(new SliderChangeListener());
		slider.setBorder(new TitledBorder(null, "Speed", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer(TIMER_MIN_SPEED), new JLabel("Slow") );
		labelTable.put( new Integer(TIMER_MAX_SPEED), new JLabel("Fast") );
		
		slider.setLabelTable( labelTable );
		slider.setPaintLabels(true);
		
		panel.add(slider, BorderLayout.SOUTH);
		btnForward.addActionListener(new BtnStepByStepActionListener());
		btnPause.addActionListener(new BtnPauseActionListener());
		btnBackward.addActionListener(new BtnBackwardActionListener());
		btnStop.addActionListener(new BtnStopActionListener());
		btnPlay.addActionListener(new BtnPlayActionListener());
		
		algoText.setText(algorithm.getAlgorithm());
		
		timerVisualization = new Timer(-TIMER_DEFAULT_SPEED, new BtnPlayTimerActionListener ());
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
			try{
				visitor.visit(tree);
				states = visitor.getStates().listIterator();
			}catch (ParseCancellationException e){
				states = null;
			}
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
	
	public void autoLine(State s){
		try {
			algoText.moveCaretPosition(algoText.getLineStartOffset(s.getNline()));
			algoText.setCurrentLineHighlightColor(s.getColor());
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		textAreaDebug.setText(textAreaDebug.getText()+s+"\n");
		stackVisualization(s);
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
			if (states!=null&&states.hasNext()){
				s = states.next();
				autoLine(s);
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
			if (states!=null&&states.hasPrevious()){
				s = states.previous();
				autoLine(s);
			}else{
				mode = Mode.DEFAULT;
				autoButtons();
			}
		}
	}

	private class BtnPlayTimerActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			State s = null;
			if (states!=null&&states.hasNext()) {
				s = states.next();
				autoLine(s);
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
	private class SliderChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			if (!source.getValueIsAdjusting()) {
				int fps = (int)source.getValue();
				timerVisualization.setDelay(-fps);
			}
		}
	}
	
}
