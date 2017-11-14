import java.lang.Exception;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import domain.antlr.gram.tfg.tfgLexer;
import domain.antlr.gram.tfg.tfgParser;
import gui.MainMenu;
import domain.antlr.semantic.tfg.EvalVisitor;

public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length==0){
			MainMenu frame = new MainMenu();
			frame.setVisible(true);
		}else {
			for (String arg : args){
				System.out.println(arg);
				tfgLexer lexer = new tfgLexer(CharStreams.fromFileName(arg));
				tfgParser parser = new tfgParser(new CommonTokenStream(lexer));
				ParseTree tree = parser.start();
				EvalVisitor visitor = new EvalVisitor();
				try{
					visitor.visit(tree);
				}catch (ParseCancellationException e){}
			}
		}
	}
}
