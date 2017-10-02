package semantic.tfgGUI;

import java.util.Map;

import semantic.tfg.Value;

public class State {
	private final String SPACE_WIDTH = "   ";
	private int nline;
	private String sline="";
	private Map<String, Value> variables;
	
	public State (int nline, int ntabs, String sline, Map<String, Value> variables){
		this.nline=nline-1;
		for (int i=0;i<ntabs;i++)
			this.sline+=SPACE_WIDTH;
		this.sline+=sline;
		this.variables=variables;
	}
	
	public int getNline() {
		return nline;
	}

	public String getSline() {
		return sline;
	}

	public Map<String, Value> getVariables() {
		return variables;
	}
	public String toString(){
		return sline;
	}
}
