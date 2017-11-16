package domain;

import java.util.ArrayList;

import org.antlr.v4.runtime.misc.ParseCancellationException;

import domain.antlr.semantic.ParserError;

public interface ControllerParser {
	public void startParsing() throws ParseCancellationException;
	public ArrayList<ParserError> getErrors();
}
