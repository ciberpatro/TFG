package domain.antlr.semantic.tfgGUI;

import java.util.ArrayList;

import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

import domain.antlr.semantic.tfg.Value;

public class GraphTFG extends mxGraph{
	private ArrayList<Integer> selectedColors;
	public GraphTFG(ArrayList<Value> list) {
		super();
		selectedColors = new ArrayList<Integer>();
		Object parent = this.getDefaultParent();
		Object v1 = this.insertVertex(parent, "I0", list.size()>0 ? list.get(0) : "  ", 0, 0, 0, 0);
		updateCellSize(v1);
		mxRectangle v1r = getCellBounds(v1);
		Object v2 = null;
		for (int i=1;i<list.size();i++){
			v2 = this.insertVertex(parent, "I"+i, list.get(i), v1r.getCenterX()+v1r.getWidth()/2, 0, 0, 0);
			updateCellSize(v2);
			v1r = getCellBounds(v2);
		}
	}

	public void setSelectColor(int index) {
		selectedColors.add(index);
	}

	public ArrayList<Integer> getSelectedColors() {
		return selectedColors;
	}
}
