package domain.antlr.semantic;

public class ParserError {
	private int line;
	private int column;
	private String error;
	public ParserError(int line, int column, String error) {
		this.line = line;
		this.column = column;
		this.error = error;
	}
	public int getLine() {
		return line;
	}
	public int getColumn() {
		return column;
	}
	public String getError() {
		return error;
	}
	
}
