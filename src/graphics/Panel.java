package graphics;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

import input.KeyInput;
import simulation.Agent;
import simulation.Environment;
import simulation.Food;

public class Panel extends JPanel{
	private static final long serialVersionUID = -310866009165515372L;

	private static final double scroll_speed = 10;
	private static final double zoom_speed = 0.1;
	
	private Environment environment;

	private int width, height, off_x, off_y;
	private float scale;
	
	public Panel(Environment environment, int width, int height) {
		this.environment = environment;
		
		this.width = width;
		this.height = height;
		
		this.scale = (float) 1;
		this.off_x = 0;
		this.off_y = 0;
	}
		
	@Override
	public void paintComponent(Graphics g) {			
		// add the background
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, this.width, this.height);
		
<<<<<<< HEAD
		// get food and agents from the environment
		ArrayList<NeuralAgent> agents = environment.getAgents();
=======
		/*
		 * TODO get the agents and foods as 2d arrays from the environment
		 * environment.getAgentArray
		 * environment.getFoodArray
		 */
		
		//int[][] agents = {{10, 10}, {100, 200}};
		//int[][] foods = {{30, 50}, {150, 150}};
		ArrayList<Agent> agents = environment.getAgents();
>>>>>>> refs/remotes/origin/master
		ArrayList<Food> foods = environment.getFood();
		
		// draw the agents
		g.setColor(Color.RED);
		Iterator<Agent> agentIter = agents.iterator();
		while (agentIter.hasNext()){
<<<<<<< HEAD
			NeuralAgent agent = agentIter.next();
			int radius = (int) agent.getRadius();
			
			g.fillOval((int) (this.scale * (agent.getX() - radius - off_x)), (int) (this.scale * (agent.getY() - radius - off_y)), 
				       (int) (2 * radius * this.scale), (int) (2 * radius * this.scale));
=======
		   Agent agent = agentIter.next();
		   byte[] DNA = agent.getDNA();
		   g.setColor(new Color(DNA[0] - Byte.MIN_VALUE, DNA[1] - Byte.MIN_VALUE, DNA[2] - Byte.MIN_VALUE));
		   g.fillOval((int)agent.getX(), (int)agent.getY(), (int)agent.getRadius() * 2, (int)agent.getRadius() * 2);  // TODO get the correct radius
>>>>>>> refs/remotes/origin/master
		}
		
		// draw the food
		g.setColor(Color.BLUE);
		Iterator<Food> foodIter = foods.iterator();
		while (foodIter.hasNext()){
			Food food = foodIter.next();
			int radius = (int) food.getRadius();
			
			g.fillOval((int) (this.scale * (food.getX() - radius - off_x)), (int) (this.scale * (food.getY() - radius - off_y)), 
				       (int) (2 * radius * this.scale), (int) (2 * radius * this.scale));
		}
		
		// draw the border of the environment
		// TODO
	}
	
	// process a pan or zoom
	public void keyAction(int action) {
		if (action == KeyEvent.VK_UP) {
			this.off_y -= Panel.scroll_speed / this.scale;
		}
		if (action == KeyEvent.VK_DOWN) {
			this.off_y += Panel.scroll_speed / this.scale;
		}
		if (action == KeyEvent.VK_LEFT) {
			this.off_x -= Panel.scroll_speed / this.scale;
		}
		if (action == KeyEvent.VK_RIGHT) {
			this.off_x += Panel.scroll_speed / this.scale;
		}
		if (action == KeyEvent.VK_N) {
			this.scale *= 1 + Panel.zoom_speed;
			
			// TODO zoom to the center
		}
		if (action == KeyEvent.VK_M) {
			this.scale *= 1 - Panel.zoom_speed;
		}
	}
}
