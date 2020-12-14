package graphics;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class OverlayManager {
	
	private int leftColumn;
	private int rightColumn;
	
	private int componantSpacing;
	private List<OverlayPanel> componants;
	private Panel panel;
	
	public OverlayManager(Panel p) {
		panel = p;
		this.leftColumn = 10;
		this.rightColumn = p.getWidth() - 10;
		componants = new ArrayList<>();
		componantSpacing = 10;
	}
	
	public void add(OverlayPanel componant) {
		componants.add(componant);
	}
	
	public void renderOverlay(Graphics g) {
		int yPosLeft = componantSpacing;
		int yPosRight = componantSpacing;
		
		this.leftColumn = 10;
		this.rightColumn = panel.getWidth() - 10;
		
		for(OverlayPanel componant : componants) {
		    if(componant instanceof VariablesPanel || componant instanceof PopulationStatisticPanel) {
		        componant.setLocation(rightColumn - componant.getDimension().width - componantSpacing, yPosRight);
		        yPosRight += componant.getDimension().height + componantSpacing;
		    } else {
		        componant.setLocation(leftColumn, yPosLeft);
		        yPosLeft += componant.getDimension().height + componantSpacing;
		    }	
			componant.render(g);
		}
	}
	
	public void checkClickComponants(int x, int y) {
		for(OverlayPanel componant : componants) {
			if(componant.isPointInPanel(x, y)) {
				componant.clicked(x - componant.x, y - componant.y);
			}
		}
	}
}
