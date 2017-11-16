package domain;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import drivers.Reader;

public class AlgorithmReader {
	
	private final String PATH = "/resources/algorithms/";
	private final String ALGOCONF = "algorithms.conf";
	
	private ArrayList<Algorithm> algorithms;

	public ArrayList<Algorithm> getAlgorithms() throws FileNotFoundException{
		if (algorithms == null){
			Reader r= Reader.getReader();
			String fileText = Reader.getReader().read(PATH+ALGOCONF);
			algorithms = new ArrayList<Algorithm>();
			ControllerParserConf cpc=new ControllerParserConf(fileText, algorithms);
			cpc.startParsing();
			for (Algorithm algorithm : algorithms){
				String folder = algorithm.getFolderName()+"/";
				algorithm.setDescription(r.read(PATH+folder+algorithm.getDescription()));
				algorithm.setAlgorithm(r.read(PATH+folder+algorithm.getAlgorithm()));
			}
		}
		return algorithms;
	}
}
