package gui;

import java.awt.Component;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;

import domain.antlr.semantic.tfgGUI.GraphTFG;

public class GraphGUI extends mxGraphComponent{
	private final String DEFAULT_COLOR = "lightblue";
	private final String SELECTED_COLOR = "orange";
	private GraphTFG graph;
	public GraphGUI(GraphTFG graph) {
		super(graph);
		graph.list2graph(DEFAULT_COLOR);
		mxGraphModel a = (mxGraphModel) graph.getModel();
		for (int index:graph.getSelectedColors()){
			graph.setCellStyle("fillColor="+SELECTED_COLOR,new Object[]{a.getCell("I"+index)});
		}
	}
	public Component getComponent(){
		return new mxGraphComponent(graph);
	}
}
