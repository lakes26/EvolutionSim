package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class PopulationStatisticPanel extends OverlayPanel{
	
	private static Font defaultFont = new Font("TimesNewRoman", Font.PLAIN, 20);
	private static int defaultLineSpacing = 10;
	
	
	private Font font;
	private int lineSpacing;

	public PopulationStatisticPanel(Panel p) {
		super(p, new Dimension(300, 0));
		this.setFont(defaultFont);
		lineSpacing = defaultLineSpacing;
	}

	@Override
	public void clicked(int x, int y) {
		return;
	}
	
	public List<String> generateStrings() {
		List<String> returnList = new ArrayList<>();
		
		returnList.add(String.format("Average Speed: %.2f", panel.getEnvironment().getAverageSpeed()));
		returnList.add(String.format("Average Size: %.2f", panel.getEnvironment().getAverageSize()));
		returnList.add(String.format("Average Generation: %.2f", panel.getEnvironment().averageGeneration()));
		returnList.add(String.format("Seconds Simulated: %.2f", panel.getEnvironment().getSecondsElapsed()));
				
		return returnList;
	}
	
	private void drawStrings(Graphics g, List<String> strings) {
		int yOff = 5;
		g.setColor(Color.BLACK);
		g.setFont(this.font);
		int lineheight = g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
		for(String string : strings) {
			g.drawString(string, this.x + 5, this.y + yOff + lineheight/2);
			yOff += lineheight + lineSpacing;
		}
		 
		this.dimension.height = yOff - lineheight + lineSpacing;
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		drawStrings(g, this.generateStrings());
	}
	
	private void setFont(Font font) {
		this.font = font;	
	}
}
