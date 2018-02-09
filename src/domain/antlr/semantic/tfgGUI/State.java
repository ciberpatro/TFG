package domain.antlr.semantic.tfgGUI;

import java.util.Map;

import domain.antlr.semantic.tfg.Value;

public class State {
	private int nline;
	private int cline=StateConstants.DEFAULT;
	private int ntabs;
	private int type=StateConstants.DEFAULT;
	private String sline;
	private Map<String, Value> variables;
	private Map<String, ListGraph> graph_list;
	
	public State (int nline, int ntabs, String sline, Map<String, Value> variables, Map<String, ListGraph> graph_list){
		this.nline=nline-1;
		this.sline=sline;
		this.ntabs=ntabs;
		this.variables=variables;
		this.graph_list=graph_list;
	}
	
	public void setColorBool(boolean colorBool){
		this.cline=colorBool ? StateConstants.CONDITION_TRUE :StateConstants.CONDITION_FALSE;
	}
	
	public int getColor(){
		return this.cline;
	}
	
	public int getNline() {
		return this.nline;
	}
	
	public int getNtabs(){
		return this.ntabs;
	}
	
	public int setNtabs(int ntabs){
		return this.ntabs=ntabs;
	}

	public String getSline() {
		return this.sline;
	}

	public Map<String, Value> getVariables() {
		return this.variables;
	}
	public Map<String, ListGraph> getListsState(){
		return this.graph_list;
	}
	
	public void setSline(String sline) {
		this.sline=sline;
	}

	public void updateList(String id, int index){
		ListGraph graph = null;
		if (this.graph_list.get(id) != null){
			graph = this.graph_list.get(id);
			graph.addSelectColor(index);
		}
	}
	public String toString(){
		return this.sline;
	}

	public void setError(String error) {
		this.sline=error;
		this.type=StateConstants.ERROR;
	}
	public boolean isError(){
		return this.type==StateConstants.ERROR;
	}
	public void setFinal(boolean isFinal){
		this.type=isFinal?StateConstants.FINAL:StateConstants.DEFAULT;
	}
	public boolean isFinal(){
		return this.type==StateConstants.FINAL;
	}
}
