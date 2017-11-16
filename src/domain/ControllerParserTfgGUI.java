package domain;

import java.util.ArrayList;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import domain.antlr.gram.tfg.tfgLexer;
import domain.antlr.gram.tfg.tfgParser;
import domain.antlr.semantic.HandlerParserError;
import domain.antlr.semantic.ParserError;
import domain.antlr.semantic.tfgGUI.EvalVisitorGUI;
import domain.antlr.semantic.tfgGUI.State;



public class ControllerParserTfgGUI implements ControllerParser{
	private tfgLexer lexer;
	private tfgParser parser;
	private ParseTree tree;
	private HandlerParserError errParserHandler;
	private EvalVisitorGUI visitor;
	public ControllerParserTfgGUI(String textFile){
		this.lexer = new tfgLexer(CharStreams.fromString(textFile));
		this.parser = new tfgParser(new CommonTokenStream(lexer));
		this.errParserHandler = new HandlerParserError();
		this.parser.addErrorListener(errParserHandler);
		this.tree = parser.start();
	}
	public ArrayList<ParserError> getErrors() {
		return this.errParserHandler.getErrors();
	}
	public void startParsing() throws ParseCancellationException{
		this.visitor = new EvalVisitorGUI();
		visitor.visit(this.tree);
	}
	
	public ArrayList<State> getStatesTfgGUI(){
		return visitor.getStates();
	}
}
