package domain;

import java.util.ArrayList;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import domain.antlr.gram.conf.confLexer;
import domain.antlr.gram.conf.confParser;
import domain.antlr.gram.conf.confParser.StartContext;
import domain.antlr.semantic.HandlerParserError;
import domain.antlr.semantic.ParserError;
import domain.antlr.semantic.conf.ConfVisitor;



public class ControllerParserConf implements ControllerParser{
	
	private HandlerParserError errParserHandler;
	private StartContext tree;
	private ArrayList<Algorithm> algorithms;

	public ControllerParserConf(String textFile,ArrayList<Algorithm> algorithms){
		confLexer lexer = new confLexer(CharStreams.fromString(textFile));
		confParser parser = new confParser(new CommonTokenStream(lexer));
		this.errParserHandler = new HandlerParserError();
		parser.addErrorListener(errParserHandler);
		this.tree = parser.start();
		this.algorithms = algorithms;
	}
	
	public void startParsing() throws ParseCancellationException {
		ConfVisitor visitor = new ConfVisitor(this);
		visitor.visit(this.tree);
	}
	
	public void addAlgorithm(Algorithm algo){
		this.algorithms.add(algo);
	}

	public ArrayList<ParserError> getErrors() {
		return this.errParserHandler.getErrors();
	}
}
