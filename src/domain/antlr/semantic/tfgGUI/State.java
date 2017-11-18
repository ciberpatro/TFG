package domain.antlr.semantic.tfgGUI;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import domain.antlr.semantic.tfg.Value;

import java.util.Set;
import java.util.Stack;

public class State {
	private int nline;
	private int cline=StateConstants.DEFAULT;
	private int ntabs;
	private int type=StateConstants.DEFAULT;
	private String sline;
	private Map<String, Value> variables;
	private Map<String, GraphTFG> graph_list;
	
	public State (int nline, int ntabs, String sline, Stack<Map<String, Value>> variables){
		this.nline=nline-1;
		this.sline=sline;
		this.ntabs=ntabs;
		this.variables=new HashMap<String, Value>();
		this.graph_list=new HashMap<String, GraphTFG>();
		Map<String, Value> firstStack = variables.pop();
		Map<String, Value> secondStack = null;
		
		if(!variables.isEmpty())
			secondStack = variables.peek();
		if(firstStack!=null){
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
	public Map<String, GraphTFG> getGraphs(){
		return this.graph_list;
	}
	
	public void setSline(String sline) {
		this.sline=sline;
	}

	public void updateGraph(String id, int index){
		GraphTFG graph = null;
		if (this.graph_list.get(id) != null){
			graph = this.graph_list.get(id);
			graph.setSelectColor(index);
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
