import java.lang.Exception;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import gram.*;
import semantica.*;

public class Main {
	public static void main(String[] args) throws Exception {
		tfgLexer lexer = new tfgLexer(new ANTLRFileStream(args[0]));
		tfgParser parser = new tfgParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.start();
		EvalVisitor visitor = new EvalVisitor();
		visitor.visit(tree);
	}
}
