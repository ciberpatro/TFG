import java.io.File;
import java.lang.Exception;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import gram.*;
import semantica.*;
import gui.*;

public class Main {
	public static void main(String[] args) throws Exception {
		tfgLexer lexer = new tfgLexer(CharStreams.fromFileName(args[0]));
		tfgParser parser = new tfgParser(new CommonTokenStream(lexer));
		ParseTree tree = parser.start();
		EvalVisitor visitor = new EvalVisitor();
		visitor.visit(tree);
	}
}
