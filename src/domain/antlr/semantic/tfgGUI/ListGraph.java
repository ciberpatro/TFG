package domain.antlr.semantic.tfgGUI;

import java.util.ArrayList;

import domain.antlr.semantic.tfg.Value;

public class ListGraph{
	private ArrayList<Integer> selectedColors;
	private ArrayList<Value> list;
	public ListGraph(ArrayList<Value> newlist) {
		this.list=newlist;
		selectedColors = new ArrayList<Integer>();
	}

	public ArrayList<Value> getList() {
		return list;
	}

	public void addSelectColor(int index) {
		if (index<list.size())
			selectedColors.add(index);
	}
	public ArrayList<Integer> getSelectedColors() {
		return selectedColors;
	}
}
