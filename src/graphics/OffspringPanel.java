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
	private float scale;
	
	public OffspringPanel(Panel p, Dimension d) {
		super(p, d);
		envRenderer = new EnvironmentRenderer(p);
		this.scale = (float) 1.5;
		this.setTitle("Offspring");
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
			envRenderer.drawAgent(g, offspring.get(i), borderBuffer + this.x + widthGap, borderBuffer + this.y + (i + 1) * partition, scale);
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
	
	public void clicked(int x, int y) {
		Agent agent = this.panel.getSelectedAgent();
		List<Agent> offspring = agent.getOffspring();
		
		int deltaY = this.partition(this.dimension.height - (2 * borderBuffer), offspring.size() + 1);
		int deltaX = this.partition(this.dimension.width - (2 * borderBuffer), 2);
		
		int currX = borderBuffer + deltaX;
		int currY = borderBuffer + deltaY;
		
		for(int i = 0; i < offspring.size(); i++) {
			Agent child = offspring.get(i);
			
			double dist = Math.sqrt(Math.pow(x - currX, 2) - Math.pow(y - currY, 2));
			
			if (dist < agent.getRadius() * scale * 2) {
				panel.setTrack_id(child.getID());
				panel.setSelectedAgent(child);
				return;
			}
			currY += deltaY;
		}
	}

	public Dimension getDimension() {
		return this.dimension;
	}
}
