package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

	private static final int MODE_FREE = 1;
	private static final int MODE_TRACK = 2;	
	
	private static final double scroll_speed = 20;
	private static final double zoom_speed = 0.03;
	
	private Environment environment;

	private int width, height, off_x, off_y, env_width, env_height, mode, saveIndCountdown;
	private float scale;
	private Agent selectedAgent;
	
	private long track_id;

	private StatisticPanel statisticPanel;

	
	public Panel(Environment environment, int width, int height) {
		this.environment = environment;
		
		this.selectedAgent = null;
		
		this.width = width;
		this.height = height;
		
		this.scale = (float) 1;
		this.off_x = 0;
		this.off_y = 0;
		
		this.env_height = this.environment.getHeight();
		this.env_width = this.environment.getWidth();
		
		this.mode = getModeFree();
		this.track_id = -1;
		this.statisticPanel = new StatisticPanel(this, 5, 15, 0);
	}
	
	// go from graphical coords to environment coords
	private Point getEnvironmentCoordinates(int x, int y) {
		Point p = new Point();
		p.setLocation(x / this.scale + this.off_x, 
					  y / this.scale + this.off_y);
		
		return p;
	}
	
	// go from environment coords to graphical coords
	private Point getGraphicalCoords(float x, float y) {
		Point p = new Point();
		p.setLocation((int) (this.scale * (x - off_x)), 
					  (int) (this.scale * (y - off_y)));
				
		return p;
	}
		
	@Override
	public void paintComponent(Graphics g) {		
		// add the background
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, this.width, this.height);
		
		// get agents and food
		ArrayList<Agent> agents = environment.getAgents();
		ArrayList<Food> foods = environment.getFood();
		
		// set the and y offsets based on tracking
		if (this.mode == Panel.MODE_TRACK && this.track_id != -1) {
			boolean dead = true;
			
			for (int i = 0; i < agents.size(); ++i) {
				Agent agent = agents.get(i);
				
				if (agent.getID() == this.track_id) {
					dead = false;
										
					this.off_x = (int) (agent.getX() + agent.getRadius() / 2 - (this.width) / this.scale / 2); 
					this.off_y = (int) (agent.getY() + agent.getRadius() / 2 - (this.height) / this.scale / 2 ); 
					
					break;
				}
			}
			
			if (dead) {
				this.track_id = -1;
			}
		}
		
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
	
		//draw save indicator
		if(this.saveIndCountdown > 0) {
		    g.setColor(Color.RED);
		    g.setFont(new Font("TimesNewRoman", Font.BOLD, 40));
		    g.drawString("Saving...", width - 200, height - 100);
		    saveIndCountdown--;
		}
		statisticPanel.draw(g);
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
		// zoom in
		if (action == KeyEvent.VK_N) {
			this.off_x += this.width * Panel.zoom_speed / this.scale / 2;
			this.off_y += this.height * Panel.zoom_speed / this.scale / 2;
			this.scale *= 1 + Panel.zoom_speed;
		}
		// zoom out
		if (action == KeyEvent.VK_M) {
			this.off_x -= this.width * Panel.zoom_speed / this.scale / 2;
			this.off_y -= this.height * Panel.zoom_speed / this.scale / 2;
			this.scale *= 1 - Panel.zoom_speed;
		}
		// set mode to free
		if (action == KeyEvent.VK_F) {
			this.mode = Panel.getModeFree();
			this.track_id = -1;
		}
		// set mode to tracking
		if (action == KeyEvent.VK_T) {
			this.mode = Panel.MODE_TRACK;
		}
		if(action == KeyEvent.VK_I) {
		    try {
                this.environment.saveToFile("save.txt");
                this.saveIndCountdown = 90;
            } catch (Exception e) {
                e.printStackTrace();
            } 
		}
	}

	public void mouseClicked(int x, int y) {
		Point p = this.getEnvironmentCoordinates(x, y);
		
		if (this.mode == Panel.MODE_TRACK) {
			// loop thru the agents
			ArrayList<Agent> agents = environment.getAgents();

			for (int i = 0; i < agents.size(); ++i) {
				Agent agent = agents.get(i);
				
				double dist = Math.sqrt(Math.pow(p.x - agent.getX(), 2) - 
										Math.pow(p.y - agent.getY(), 2));
				
				// if clicking on this agent
				if (dist < agent.getRadius()) {
					this.track_id = agent.getID();
					this.selectedAgent = agent;
					return;
				}
			}
		}		
	}

	public Agent getSelectedAgent() {
		return this.selectedAgent;
	}

	public int getMode() {
		// TODO Auto-generated method stub
		return this.mode;
	}

	public static int getModeFree() {
		return MODE_FREE;
	}

	public long getTrackingID() {
		return this.track_id;
	}
}
