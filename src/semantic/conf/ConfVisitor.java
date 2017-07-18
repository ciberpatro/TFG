package semantic.conf;

import domain.Algorithm;
import drivers.ReaderAlgorithmController;
import gram.conf.*;

public class ConfVisitor extends confBaseVisitor <String> {

	private Algorithm algo;
	private ReaderAlgorithmController algoReader;
	
	public ConfVisitor(ReaderAlgorithmController algoConfReader){
		super();
		this.algoReader = algoConfReader;
	}
	
	public String visitExpressionList(confParser.ExpressionListContext ctx) {
		algo = new Algorithm();
		visitChildren(ctx);
		algoReader.add(algo);
		return "";
	}
	
	public String visitExpressionStatement(confParser.ExpressionStatementContext ctx){
		String exp = ctx.exp.getText();
		String val = this.visit(ctx.val);
		switch (exp){
		case "NAME":
			algo.setName(val);
			break;
		case "FOLDER_NAME":
			algo.setFolderName(val);
			break;
		case "ALGORITHM":
			algo.setAlgorithm(val);
			break;
		case "DESCRIPTION":
			algo.setDescription(val);
			break;
		}
		return visitChildren(ctx);
	}

	public String visitValueStatement(confParser.ValueStatementContext ctx) {
		return ctx.str.getText();
	}
}
