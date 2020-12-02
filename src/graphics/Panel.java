package graphics;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import simulation.Environment;

public class Panel extends JPanel{
	private static final long serialVersionUID = -310866009165515372L;

	private Environment environment;
	private int width, height;
	
	public Panel(Environment environment) {
		this.environment = environment;
		
		this.width = 300;  // TODO get from the environment
		this.height = 300;
	}
		
	@Override
	public void paintComponent(Graphics g) {		
		// add the background
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, this.width, this.height);
		
		/*
		 * TODO get the agents and foods as 2d arrays from the environment
		 * environment.getAgentArray
		 * environment.getFoodArray
		 */
		
		int[][] agents = {{10, 10}, {100, 200}};
		int[][] foods = {{30, 50}, {150, 150}};
		
		// draw the agents
		g.setColor(Color.RED);
		for (int[] agent : agents) {
			g.fillOval(agent[0], agent[1], 2 * 20, 2 * 20);  // TODO get the correct radius
		}
		
		// draw the food
		g.setColor(Color.BLUE);
		for (int[] food : foods) {
			g.fillOval(food[0], food[1], 2 * 10, 2 * 10);  // TODO get the correct radius
		}		
	}
}
