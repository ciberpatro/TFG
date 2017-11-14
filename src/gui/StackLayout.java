package gui;
import java.awt.GridBagLayout;

public class StackLayout extends GridBagLayout {
	public StackLayout(int s) {
		super();
		super.columnWidths = new int[]{10, 0, 0, 10, 0};
		super.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		
		super.rowHeights = new int[s+2];
		super.rowWeights = new double[s+2];
		for (int i=0;i<super.rowWeights.length;i++){
			super.rowHeights[i]= i==0 ? 10 : 0;
			super.rowWeights[i]= i==super.rowWeights.length-1 ? Double.MIN_VALUE : 0.0;
		}
	}
}
