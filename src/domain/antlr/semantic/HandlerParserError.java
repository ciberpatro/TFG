package domain.antlr.semantic;
import java.util.ArrayList;

import org.antlr.v4.runtime.*;

public class HandlerParserError extends BaseErrorListener{
	private ArrayList<ParserError> errors;
	
	public HandlerParserError(){
		super();
		this.errors= new ArrayList<ParserError>();
	}
	public void syntaxError(Recognizer<?, ?> arg0, Object arg1, int line, int column, String error,
			RecognitionException arg5) {
		this.errors.add(new ParserError(line, column, error));
	}

	public ArrayList<ParserError> getErrors() {
		return this.errors;
	}
}
