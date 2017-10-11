package gui;

import java.awt.Component;
import java.util.ArrayList;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

import semantic.tfg.Value;

public class GraphTFG extends mxGraph{
	public GraphTFG(ArrayList<Value> list) {
		super();
		Object parent = this.getDefaultParent();
		Object v1;
		this.getModel().beginUpdate();
		try
		{
			v1 = this.insertVertex(parent, "I0", list.size()>0 ? list.get(0) : "  ", 0, 0, 0, 0,"fillColor=lightblue");
		}
		finally
		{
			this.getModel().endUpdate();
		}
		
		updateCellSize(v1);
		for (int i=1;i<list.size();i++){
			mxRectangle v1r = getCellBounds(v1);
			Object v2 = this.insertVertex(parent, "I"+i, list.get(i), v1r.getCenterX()+v1r.getWidth()/2, 0, 0, 0,"fillColor=lightblue");
			//this.insertEdge(parent, null, "", v1, v2);
			v1 = v2;
			updateCellSize(v1);
		}
	}
	public Component getComponent(){
		return new mxGraphComponent(this);
	}
}
