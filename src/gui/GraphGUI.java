package gui;

import java.util.ArrayList;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

import domain.antlr.semantic.tfgGUI.ListGraph;

public class GraphGUI extends mxGraphComponent{
	private final String DEFAULT_COLOR = "lightblue";
	private final String SELECTED_COLOR = "orange";
	public GraphGUI(ListGraph graphList) {
		super(new mxGraph());
		mxGraph listGraph = this.getGraph();
		ArrayList<Integer> selectedIndex = graphList.getSelectedColors();
		Object parent = listGraph.getDefaultParent();
		Object v1 = listGraph.insertVertex(parent, "I0",graphList.getList().size()>0 ? graphList.getList().get(0) : "  ",
				0, 0, 0, 0, selectedIndex.contains(0) ? "fillColor="+SELECTED_COLOR : "fillColor="+DEFAULT_COLOR);
		listGraph.updateCellSize(v1);
		mxRectangle v1r = listGraph.getCellBounds(v1);
		Object v2 = null;
		for (int i=1;i<graphList.getList().size();i++){
			v2 = listGraph.insertVertex(parent, "I"+i, graphList.getList().get(i), v1r.getCenterX()+v1r.getWidth()/2, 0, 0,
						 0,selectedIndex.contains(i) ? "fillColor="+SELECTED_COLOR : "fillColor="+DEFAULT_COLOR);
			listGraph.updateCellSize(v2);
			v1r = listGraph.getCellBounds(v2);
		}
	}
}
