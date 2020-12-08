package graphics;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class OverlayManager {
	
	private static int LEFT_COLUMN = 10;
	private static int RIGHT_COLUMN = 1300;
	
	private int componantSpacing;
	private int nextLocation;
	private List<OverlayPanel> componants;
	
	public OverlayManager() {
		componants = new ArrayList<>();
		componantSpacing = 10;
		nextLocation = componantSpacing;
	}
	
	public void add(OverlayPanel componant) {
		componants.add(componant);
	}
	
	public void renderOverlay(Graphics g) {
		int yPos = componantSpacing;
		for(OverlayPanel componant : componants) {
		    if(componant instanceof VariablesPanel) {
		        componant.setLocation(RIGHT_COLUMN, componantSpacing);
		    } else {
		        componant.setLocation(LEFT_COLUMN, yPos);
		    }
			yPos += componant.getDimension().height + componantSpacing;
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
