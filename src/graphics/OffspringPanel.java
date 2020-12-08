package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import simulation.Agent;
import utils.NeuralNetwork;

public class OffspringPanel extends OverlayPanel{
	
	private static final Color OUTLINE = Color.black;
	private EnvironmentRenderer envRenderer;
	
	public OffspringPanel(Panel p, Dimension d) {
		super(p, d);
		envRenderer = new EnvironmentRenderer(p);
	}
	
	private void drawAgentOffspringPanel(Graphics g, Agent agent) {
		drawOutline(g, OUTLINE);
		drawOffspring(g, agent);
	}
	
	private void drawOffspring(Graphics g, Agent agent) {
		List<Agent> offspring = agent.getOffspring();
		int partition = this.partition(this.dimension.height - (2 * borderBuffer), offspring.size() + 1);
		int widthGap = this.partition(this.dimension.width - (2 * borderBuffer), 2);
		
		for(int i = 0; i < offspring.size(); i++) {
			envRenderer.drawAgent(g, offspring.get(i), borderBuffer + this.x + widthGap, borderBuffer + this.y + (i + 1) * partition, (float) 1.5);
		}
	}
	
	@Override
	public void render(Graphics g) {
		Agent agent = this.panel.getSelectedAgent();
		
		if(agent != null && this.panel.getTrackingID() != -1) {
			this.renderTitle(g);
			drawAgentOffspringPanel(g, agent);
		}
	}

	public Dimension getDimension() {
		return this.dimension;
	}
}
