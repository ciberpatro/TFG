package drivers;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import domain.Algorithm;

public interface ReaderAlgorithmController {
	
	public ArrayList<Algorithm> getAlgorithms() throws FileNotFoundException;
	public void add(Algorithm algoconf);
}
