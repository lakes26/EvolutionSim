package graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import simulation.Agent;

public class StatisticPanel extends OverlayPanel {
	private static Font defaultFont = new Font("TimesNewRoman", Font.PLAIN, 20);
	
	private Font font;
	private int lineSpacing;

	private int yOff;
	
	@SuppressWarnings("exports")
	public StatisticPanel(Panel p, int x, int y, int lineSpacing) {
		super(p, new Dimension());

		this.setFont(defaultFont);
		this.lineSpacing = lineSpacing;
		this.yOff = 5;
	}
	
	private List<String> generateStrings(Agent agent) {
		List<String> returnList = new ArrayList<>();
		
		returnList.add(this.panel.getMode() == Panel.getModeFree() ? "mode: free" : "mode: tracking");
		
		if(agent != null && this.panel.getTrackingID() != -1) {
			returnList.add(String.format("age: %f", agent.getAge()));
			returnList.add(String.format("speed: %.2f", agent.getSpeed()));
			returnList.add(String.format("number of offspring: %d", agent.getNumOffspring()));
			returnList.add(String.format("energy level: %f", agent.getEnergy() + 2));
			returnList.add(String.format("size %.2f", agent.getRadius()));
		}
		
		return returnList;
	}
	
	private void drawStrings(Graphics g, List<String> strings) {
		g.setColor(Color.BLACK);
		g.setFont(this.font);
		int lineheight = g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
		for(String string : strings) {
			g.drawString(string, x, this.y + yOff);
			yOff += lineheight + lineSpacing;
		}
		
		this.dimension.height = yOff - lineheight + lineSpacing;
	}
	
	
	@SuppressWarnings("exports")
	@Override
	public void render(Graphics g) {
		this.yOff = y + 5;
		drawStrings(g, this.generateStrings(this.panel.getSelectedAgent()));
	}

	@SuppressWarnings("exports")
	public Font getFont() {
		return font;
	}

	@SuppressWarnings("exports")
	public void setFont(Font font) {
		this.font = font;
	}

	@Override
	public void clicked(int x, int y) {
		// TODO Auto-generated method stub
		
	}
}
