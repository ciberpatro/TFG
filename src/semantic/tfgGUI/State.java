package semantic.tfgGUI;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mxgraph.model.mxGraphModel;

import gui.GraphTFG;
import semantic.tfg.Value;

public class State {
	private final String SPACE_WIDTH = "   ";
	private int nline;
	private String sline="";
	private Map<String, Value> variables;
	private Map<String, GraphTFG> graph_list;
	
	public State (int nline, int ntabs, String sline, Map<String, Value> variables){
		this.nline=nline-1;
		for (int i=0;i<ntabs;i++)
			this.sline+=SPACE_WIDTH;
		this.sline+=sline;
		this.variables=new HashMap<String, Value>();
		this.graph_list=new HashMap<String, GraphTFG>();
		Set<Entry<String, Value>> var = variables.entrySet();
		for (Entry<String, Value> e : var ){
			if (e.getValue().isList()){
				this.graph_list.put(e.getKey(), new GraphTFG(e.getValue().asList()));
			}else{
				this.variables.put(e.getKey(), new Value(e.getValue()));
			}
		}
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
	public void updateGraph(String id, int index){
		mxGraphModel a = (mxGraphModel) graph_list.get(id).getModel();
		
		graph_list.get(id).setCellStyle("fillColor=orange",new Object[]{a.getCell("I"+index)});
	}
	public String toString(){
		return sline;
	}
}
