package domain.antlr.semantic.tfgGUI;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import domain.antlr.semantic.tfg.Value;

import java.util.Set;
import java.util.Stack;

public class State {
	private int nline;
	private boolean isError=false;
	private int cline=StateConstants.DEFAULT;
	private String sline="";
	private Map<String, Value> variables;
	private Map<String, GraphTFG> graph_list;
	
	public State (int nline, int ntabs, String sline, Stack<Map<String, Value>> variables){
		this.nline=nline-1;
		for (int i=0;i<ntabs;i++)
			this.sline+=StateConstants.SPACE_WIDTH;
		this.sline+=sline;
		this.variables=new HashMap<String, Value>();
		this.graph_list=new HashMap<String, GraphTFG>();
		Map<String, Value> firstStack = variables.pop();
		Map<String, Value> secondStack = null;
		
		if(!variables.isEmpty())
			secondStack = variables.peek();
		
		Set<Entry<String, Value>> var = firstStack.entrySet();
		for (Entry<String, Value> e : var ){
			if (e.getValue().isList()){
				this.graph_list.put(e.getKey(), new GraphTFG(e.getValue().asList()));
			}else if (secondStack!=null&&
			          secondStack.containsKey(e.getKey())&&
			          secondStack.get(e.getKey())==e.getValue()){
				this.variables.put(e.getKey(), e.getValue());
			}else{
				this.variables.put(e.getKey(), new Value(e.getValue()));
			}
		}
		variables.push(firstStack);
	}
	
	public void setColorBool(boolean colorBool){
		this.cline=colorBool ? StateConstants.CONDITION_TRUE :StateConstants.CONDITION_FALSE;
	}
	
	public int getColor(){
		return this.cline;
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
	public Map<String, GraphTFG> getGraphs(){
		return graph_list;
	}
	
	public void setSline(String sline) {
		this.sline+=sline;
	}

	public void updateGraph(String id, int index){
		GraphTFG graph = null;
		if (graph_list.get(id) != null){
			graph = graph_list.get(id);
			graph.setSelectColor(index);
		}
	}
	public String toString(){
		return sline;
	}

	public void setError(String error) {
		this.sline=error;
		this.isError=true;
	}
	public boolean isError(){
		return this.isError;
	}
}
