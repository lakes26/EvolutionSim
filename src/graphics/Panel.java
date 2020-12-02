package graphics;


import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import simulation.Agent;
import simulation.Environment;
import simulation.Food;

public class Panel extends JPanel{
	private static final long serialVersionUID = -310866009165515372L;

	private Environment environment;
	private int width, height;
	
	public Panel(Environment environment) {
		this.environment = environment;
		
		this.width = 1000;  // TODO get from the environment
		this.height = 1000;
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
		
		//int[][] agents = {{10, 10}, {100, 200}};
		//int[][] foods = {{30, 50}, {150, 150}};
		ArrayList<Agent> agents = environment.getAgents();
		ArrayList<Food> foods = environment.getFood();
		
		// draw the agents
		g.setColor(Color.RED);
		for (Agent agent : agents) {
			g.fillOval((int)agent.getX(), (int)agent.getY(), (int)agent.getRadius() * 2, (int)agent.getRadius() * 2);  // TODO get the correct radius
		}
		
		// draw the food
		g.setColor(Color.BLUE);
		for (Food food : foods) {
		    g.fillOval((int)food.getX(), (int)food.getY(), (int)food.getRadius() * 2, (int)food.getRadius() * 2);
		}		
	}
}
