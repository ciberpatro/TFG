package drivers;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import domain.Algorithm;
import gram.conf.confLexer;
import gram.conf.confParser;
import gram.conf.confVisitor;
import gram.conf.confParser.StartContext;
import semantic.conf.ConfVisitor;

public class AlgorithmReader implements ReaderAlgorithmController{
	private final String PATH = "/resources/algorithms/";
	private final String ALGOCONF = "algorithms.conf";
	private ArrayList<Algorithm> algorithms;

	public ArrayList<Algorithm> getAlgorithms() throws FileNotFoundException{
		if (algorithms == null){
			Reader r= Reader.getReader();
			String fileText = r.read(PATH+ALGOCONF);
			algorithms = new ArrayList<Algorithm>();
			confLexer lexer = new confLexer(CharStreams.fromString(fileText));
			confParser parser = new confParser(new CommonTokenStream(lexer));
			StartContext tree = parser.start();
			confVisitor<String> visitor = new ConfVisitor(this);
			visitor.visit(tree);
			for (Algorithm algorithm : algorithms){
				String folder = algorithm.getFolderName()+"/";
				algorithm.setDescription(r.read(PATH+folder+algorithm.getDescription()));
				algorithm.setAlgorithm(r.read(PATH+folder+algorithm.getAlgorithm()));
			}
		}
		return algorithms;
	}

	public void add(Algorithm algorithm) {
		algorithms.add(algorithm);
	}
}
