package graphics;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class OverlayManager {
	
	private static int LEFT_COLLUMN = 10;
	
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
			componant.setLocation(LEFT_COLLUMN, yPos);
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
