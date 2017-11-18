package domain.antlr.semantic.tfgGUI;

import java.util.ArrayList;

import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

import domain.antlr.semantic.tfg.Value;

public class GraphTFG extends mxGraph{
	private boolean isGraph;
	private ArrayList<Integer> selectedColors;
	private ArrayList<Value> list;
	public GraphTFG(ArrayList<Value> list) {
		super();
		this.list=new ArrayList<Value>(list);
		selectedColors = new ArrayList<Integer>();
	}

	public ArrayList<Value> getList() {
		return list;
	}

	public void setSelectColor(int index) {
		selectedColors.add(index);
	}
	
	public boolean list2graph(String defaultColor){
		if (!isGraph){
			Object parent = this.getDefaultParent();
			Object v1 = this.insertVertex(parent, "I0",this.list.size()>0 ? this.list.get(0) : "  ", 0, 0, 0, 0,"fillColor="+defaultColor);
			updateCellSize(v1);
			mxRectangle v1r = getCellBounds(v1);
			Object v2 = null;
			for (int i=1;i<this.list.size();i++){
				v2 = this.insertVertex(parent, "I"+i, this.list.get(i), v1r.getCenterX()+v1r.getWidth()/2, 0, 0, 0,"fillColor="+defaultColor);
				updateCellSize(v2);
				v1r = getCellBounds(v2);
			}
			isGraph=true;
		}
		return isGraph;
	}
	
	public ArrayList<Integer> getSelectedColors() {
		return selectedColors;
	}
}
