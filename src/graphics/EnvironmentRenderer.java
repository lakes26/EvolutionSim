package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import simulation.Agent;
import simulation.Food;

public class EnvironmentRenderer {
	
	private Panel panel;
	
	public EnvironmentRenderer(Panel p) {
		this.panel = p;
	}
	
	public void renderEnvironment(Graphics g) {
		drawFood(g);
		drawAgents(g);
		drawOutline(g);
	}
	
	private void drawOutline(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawRect((int) (panel.getScale() * -panel.getOff_x()), (int) (panel.getScale() * -panel.getOff_y()), 
				   (int) (panel.getScale() * panel.getEnv_width()), (int) (panel.getScale() * panel.getEnv_height()));
	}
	
	private void drawAgents(Graphics g) {
		ArrayList<Agent> agents = panel.getEnvironment().getAgents();
		
		for(int i = 0; i < agents.size(); i++){
			Agent agent = agents.get(i);
			int radius = (int) agent.getRadius();
			byte[] DNA = agent.getDNA();
			g.setColor(new Color(DNA[0] - Byte.MIN_VALUE, DNA[1] - Byte.MIN_VALUE, DNA[2] - Byte.MIN_VALUE));
			g.fillOval(	(int) (panel.getScale() * (agent.getX() - radius - panel.getOff_x())), 
						(int) (panel.getScale() * (agent.getY() - radius - panel.getOff_y())), 
						(int) (2 * radius * panel.getScale()), (int) (2 * radius * panel.getScale()));
		}
	}
	
	private void drawFood(Graphics g) {
		ArrayList<Food> foods = panel.getEnvironment().getFood();
		
		g.setColor(Color.BLUE);
		for(int i = 0; i < foods.size(); i++){
			Food food = foods.get(i);
			int radius = (int) food.getRadius();
			
			g.fillOval((int) (panel.getScale() * (food.getX() - radius - panel.getOff_x())), 
						(int) (panel.getScale() * (food.getY() - radius - panel.getOff_y())), 
				       (int) (2 * radius * panel.getScale()), (int) (2 * radius * panel.getScale()));
		}
	}
}
