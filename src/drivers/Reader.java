package drivers;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Reader {
	
	private static Reader reader;
	
	private Reader(){}
	
	public static Reader getReader(){
		if (reader==null)
			reader = new Reader();
		return reader;
	}
	
	public String read(String path) throws FileNotFoundException{
		Scanner sc = new Scanner (getClass().getResourceAsStream(path));
		String fileText="";
		while (sc.hasNextLine()){
			fileText+=sc.nextLine()+"\n";
		}
		sc.close();
		return fileText;
	}
}
