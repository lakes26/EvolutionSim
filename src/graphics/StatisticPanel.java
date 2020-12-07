package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import simulation.Agent;

public class StatisticPanel {
	private static Font defaultFont = new Font("TimesNewRoman", Font.PLAIN, 20);
	
	private Font font;
	private Panel panel;
	private int x;
	private int y;
	private int lineSpacing;
	
	@SuppressWarnings("exports")
	public StatisticPanel(Panel p, int x, int y, int lineSpacing) {
		this.panel = p;
		this.setFont(defaultFont);
		this.x = x;
		this.y = y;
		this.lineSpacing = lineSpacing;
	}
	
	private List<String> generateStrings(Agent agent) {
		List<String> returnList = new ArrayList<>();
		
		returnList.add(this.panel.getMode() == Panel.getModeFree() ? "mode: free" : "mode: tracking");
		
		if(agent != null && this.panel.getTrackingID() != -1) {
			returnList.add(String.format("age: %f", agent.getAge()));
			returnList.add(String.format("speed: %.2f", agent.getSpeed()));
			returnList.add(String.format("number of offspring: %d", agent.getNumOffspring()));
			returnList.add(String.format("energy level: %f", agent.getEnergy() + 2));
		}
		
		return returnList;
	}
	
	private void drawStrings(Graphics g, List<String> strings) {
		g.setColor(Color.BLACK);
		g.setFont(this.font);
		int lineheight = g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
		int y_off = 0;
		for(String string : strings) {
			g.drawString(string, x, this.y + y_off);
			y_off += lineheight + lineSpacing;
		}
	}
	
	
	@SuppressWarnings("exports")
	public void draw(Graphics g) {
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
}
