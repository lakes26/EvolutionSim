package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import input.KeyInput;
import simulation.Agent;
import simulation.Environment;
import simulation.Food;

public class Panel extends JPanel{
	private static final long serialVersionUID = -310866009165515372L;

	private static final double scroll_speed = 20;
	private static final double zoom_speed = 0.03;
	
	private Environment environment;

	private int width, height, off_x, off_y, env_width, env_height;
	private float scale;
	
	public Panel(Environment environment, int width, int height) {
		this.environment = environment;
		
		
		this.width = width;
		this.height = height;
		
		this.scale = (float) 1;
		this.off_x = 0;
		this.off_y = 0;
		
		this.env_height = this.environment.getHeight();
		this.env_width = this.environment.getWidth();
	}
		
	@Override
	public void paintComponent(Graphics g) {
		// add the background
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, this.width, this.height);
		
		ArrayList<Agent> agents = environment.getAgents();
		ArrayList<Food> foods = environment.getFood();
		
		// draw the agents
		g.setColor(Color.RED);
		for(int i = 0; i < agents.size(); i++){
			Agent agent = agents.get(i);
			int radius = (int) agent.getRadius();
			byte[] DNA = agent.getDNA();
			g.setColor(new Color(DNA[0] - Byte.MIN_VALUE, DNA[1] - Byte.MIN_VALUE, DNA[2] - Byte.MIN_VALUE));
			g.fillOval((int) (this.scale * (agent.getX() - radius - off_x)), (int) (this.scale * (agent.getY() - radius - off_y)), 
				       (int) (2 * radius * this.scale), (int) (2 * radius * this.scale));
		}
		
		// draw the food
		g.setColor(Color.BLUE);
		for(int i = 0; i < foods.size(); i++){
			Food food = foods.get(i);
			int radius = (int) food.getRadius();
			
			g.fillOval((int) (this.scale * (food.getX() - radius - off_x)), (int) (this.scale * (food.getY() - radius - off_y)), 
				       (int) (2 * radius * this.scale), (int) (2 * radius * this.scale));
		}
		
		// draw the border of the environment
		g.setColor(Color.BLACK);
		g.drawRect((int) (this.scale * -off_x), (int) (this.scale * -off_y), 
				   (int) (this.scale * this.env_width), (int) (this.scale * this.env_height));
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
			this.off_x += this.width * Panel.zoom_speed / this.scale / 2;
			this.off_y += this.height * Panel.zoom_speed / this.scale / 2;
			this.scale *= 1 + Panel.zoom_speed;
		}
		if (action == KeyEvent.VK_M) {
			this.off_x -= this.width * Panel.zoom_speed / this.scale / 2;
			this.off_y -= this.height * Panel.zoom_speed / this.scale / 2;
			this.scale *= 1 - Panel.zoom_speed;
		}
		if(action == KeyEvent.VK_Q) {
			JFrame f1 = (JFrame) SwingUtilities.windowForComponent(this);	
		}
	}
}
