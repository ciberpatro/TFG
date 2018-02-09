package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import domain.Algorithm;
import domain.ControllerParserTfgGUI;
import domain.antlr.semantic.tfgGUI.ListGraph;
import domain.antlr.semantic.ParserError;
import domain.antlr.semantic.tfg.Value;
import domain.antlr.semantic.tfgGUI.State;
import domain.antlr.semantic.tfgGUI.StateConstants;

import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.util.ArrayList;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AlgorithmVisu extends JFrame {
	
	private final int TIMER_DEFAULT_SPEED = -500;
	private final int TIMER_MIN_SPEED = -1000;
	private final int TIMER_MAX_SPEED = -10;
	
	private final String SPACE_WIDTH="···";
	
	private final Color YELLOW = new Color(255,255,170);
	private final Color GREEN = new Color(144, 238, 144);
	private final Color RED = new Color(255,193,193);
	
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
	private ControllerParserTfgGUI controllerParser;
	
	
	public AlgorithmVisu(Algorithm algorithm, JFrame parent) {
		this.parent = parent;
		this.setTitle(parent.getTitle()+" - "+algorithm.getName());
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
		textAreaDebug.setEditable(false);
		textAreaDebug.setTabSize(4);
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
		algoText.addMouseListener(new AlgoTextMouseListener());
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
		btnPause.setEnabled(false);
		
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
			btnForward.setEnabled(true);
			algoText.setEditable(false);
			btnPause.setEnabled(false);
			break;
		case Mode.DEFAULT:
			btnPause.setEnabled(false);
			btnPlay.setEnabled(true);
			algoText.setEnabled(true);
			btnBackward.setEnabled(false);
			btnForward.setEnabled(true);
			algoText.setEditable(true);
			break;
		case Mode.PLAY:
			btnPlay.setEnabled(false);
			btnPause.setEnabled(true);
			algoText.setEnabled(false);
			btnBackward.setEnabled(false);
			btnForward.setEnabled(false);
			algoText.setEditable(false);
			break;
		case Mode.STEP_BACKWARD:
		case Mode.STEP_FORWARD:
			algoText.setEnabled(false);
			btnBackward.setEnabled(true);
			algoText.setEditable(false);
			break;
		}
		
	}
	
	public void parseAlgorithm(){
		ArrayList<State> stateListAux =null;
		if (mode == Mode.DEFAULT){
			textAreaDebug.setText("");
			controllerParser= new ControllerParserTfgGUI(algoText.getText());
			showParserErrors(controllerParser.getErrors());
			try{
				controllerParser.startParsing();
				stateListAux=controllerParser.getStatesTfgGUI();
				states = stateListAux.listIterator();
			}catch (ParseCancellationException e){
				stateListAux=controllerParser.getStatesTfgGUI();
				State err = stateListAux.get(stateListAux.size()-1);
				showSemanticError(err);
				states = stateListAux.listIterator();
			}
		}
	}
	
	public void showParserErrors(ArrayList<ParserError> errors){
		String str="";
		if (errors.size()>0){
			for (ParserError e : errors){
				str+=e.getError()+ "\tLine: "+ (e.getLine())+":"+e.getColumn()+"\n";
			}
			textAreaDebug.setText("%%%%% SYNTAX ERROR %%%%%\n"+str+"\nTrying to execute..."+
								  "\n\n%%%%%%%%%%%%%%%%%%%\n\n");
		}
	}
	
	public void showSemanticError(State err){
		JOptionPane.showMessageDialog(this,
				err.getSline() + "\nLine: "+ (err.getNline()+1),
				"Error",
				JOptionPane.ERROR_MESSAGE);
	}
	
	public void stackVisualization(State s){
		Set<Entry<String, Value>> stack = s.getVariables().entrySet();
		Set<Entry<String, ListGraph>> graph_list = s.getListsState().entrySet();
		StackLayout stackLayout = new StackLayout(stack.size()+graph_list.size());
		pnlStack.removeAll();
		pnlStack.setLayout(stackLayout);
		for (Entry<String, ListGraph> e: graph_list){
			Component graphComp = new GraphGUI(e.getValue());
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
		String textAD = textAreaDebug.getText();
		String indentation = "";
		for (int i =0;i<s.getNtabs();i++){
			indentation+=SPACE_WIDTH;
		}
		try {
			if (s.isError()){
				showSemanticError(s);
			}else{
				algoText.moveCaretPosition(algoText.getLineStartOffset(s.getNline()));
				int c = s.getColor();
				if (c==StateConstants.DEFAULT)
					algoText.setCurrentLineHighlightColor(YELLOW);
				else if (c==StateConstants.CONDITION_TRUE)
					algoText.setCurrentLineHighlightColor(GREEN);
				else
					algoText.setCurrentLineHighlightColor(RED);
				if (s.isFinal()){
					timerVisualization.stop();
					JOptionPane.showMessageDialog(this,
							s.getSline(),
							"The exectuion has finished",
							JOptionPane.INFORMATION_MESSAGE);
					mode=Mode.DEFAULT;
					autoButtons();
				}
			}
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (mode!=Mode.STEP_BACKWARD)
			textAreaDebug.setText(textAD+indentation+s+'\n');
		else
			textAreaDebug.setText(textAD.substring(0, textAD.substring(0,textAD.length()-1).lastIndexOf("\n")+1));
		stackVisualization(s);
	}
	
	private void endVisualization(){
		algoText.setCurrentLineHighlightColor(YELLOW);
		timerVisualization.stop();
		mode = Mode.DEFAULT;
		autoButtons();
	}
	
	private class BtnPlayActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			parseAlgorithm();
			timerVisualization.start();
			autoButtons();
		}
	}
	
	private class BtnStepByStepActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			State s=null;
			parseAlgorithm();
			
			if (states!=null&&states.hasNext()){
				s = states.next();
				if (states.hasNext()&&mode==Mode.STEP_BACKWARD)
					s=states.next();
				mode = Mode.STEP_FORWARD;
				autoButtons();
				autoLine(s);
			}else{
				endVisualization();
			}
		}
	}
	private class BtnStopActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			endVisualization();
		}
	}
	private class BtnPauseActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (mode != Mode.DEFAULT){
				mode = Mode.PAUSE;
				timerVisualization.stop();
				algoText.setCurrentLineHighlightColor(YELLOW);
			}
			autoButtons();
		}
	}
	private class BtnBackwardActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			State s=null;
			
			if (states!=null&&states.hasPrevious()){
				s = states.previous();
				if (states.hasPrevious()){
					if (mode == Mode.STEP_FORWARD||mode == Mode.PAUSE)
						s = states.previous();
					mode = Mode.STEP_BACKWARD;
					autoButtons();
					autoLine(s);
				}else 
					endVisualization();
			}else{
				endVisualization();
			}
		}
	}

	private class BtnPlayTimerActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			State s = null;
			if (states!=null&&states.hasNext()) {
				s = states.next();
				if (states.hasNext()&&mode == Mode.STEP_BACKWARD)
					s = states.next();
				mode = Mode.PLAY;
				autoButtons();
				autoLine(s);
			} else {
				endVisualization();
			}
		}
	}
	private class ThisWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent arg0) {
			timerVisualization.stop();
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
	private class AlgoTextMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			algoText.setCurrentLineHighlightColor(YELLOW);
		}
		@Override
		public void mousePressed(MouseEvent e) {
			algoText.setCurrentLineHighlightColor(YELLOW);
		}
	}
}
