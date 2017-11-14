package domain.antlr.semantic.tfgGUI;
import java.util.ArrayList;
import java.util.BitSet;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
public class HandlerParserErrorGUI extends BaseErrorListener{
	private ArrayList<ParserError> errors;
	
	public HandlerParserErrorGUI(){
		super();
		this.errors= new ArrayList<ParserError>();
	}
	@Override
	public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3, boolean arg4, BitSet arg5,
			ATNConfigSet arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2, int arg3, BitSet arg4, ATNConfigSet arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2, int arg3, int arg4, ATNConfigSet arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void syntaxError(Recognizer<?, ?> arg0, Object arg1, int line, int column, String error,
			RecognitionException arg5) {
		this.errors.add(new ParserError(line, column, error));
	}

	public ArrayList<ParserError> getErrors() {
		return this.errors;
	}

}
