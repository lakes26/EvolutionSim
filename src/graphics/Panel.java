package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import simulation.Agent;
import simulation.CollidableObject;
import simulation.Environment;

public class Panel extends JPanel{
	private static final long serialVersionUID = -310866009165515372L;

	private static final int MODE_FREE = 1;
	private static final int MODE_TRACK = 2;	
	
	private static int trackingIndicatorThickness = 3;
	
	private static final double scroll_speed = 20;
	private static final double zoom_speed = 0.03;
	
	private int off_x = 0;
	private int off_y = 0;
	private float scale = (float) 1;
	
	private Environment environment;
	
	private int width, height, env_width, env_height, mode, saveIndCountdown;
	private Agent selectedAgent;
	
	private long track_id;

	private StatisticPanel statisticPanel;
	private EnvironmentRenderer environmentRenderer;
	private OverlayManager overlayManager;

	public Panel(Environment environment, int width, int height) {
		this.environment = environment;
		
		this.setSelectedAgent(null);
		
		this.width = width;
		this.height = height;
		
		this.env_height = Environment.getHeight();
		this.env_width = Environment.getWidth();
		
		this.mode = getModeFree();
		this.setTrack_id(-1);
		this.statisticPanel = new StatisticPanel(this, 5, 15, 0);
		
		this.environmentRenderer = new EnvironmentRenderer(this);
		this.overlayManager = new OverlayManager(this);
		this.overlayManager.add(new StatisticPanel(this, 5, 15, 0));
		this.overlayManager.add(new NeuralNetworkVisualizer(this, new Dimension(300, 300)));
		this.overlayManager.add(new OffspringPanel(this, new Dimension(100, 300)));
		this.overlayManager.add(new VariablesPanel(this, new Dimension(220, 500)));
		this.overlayManager.add(new PopulationStatisticPanel(this));
	}

	@Override
	public void paintComponent(Graphics g) {		
		clear(g);		
		
		// set the and y offsets based on tracking
		setOffsets();
		
		// render the environment;
		environmentRenderer.renderEnvironment(g);
		overlayManager.renderOverlay(g);
		
		// draw save indicator if saving
		drawSaveIndicator(g);

		// draw tracking indicator if tracking an active agent
		drawTrackingInfo(g);
	}
	
	// draw extra info for an agent that is being tracked
	private void drawTrackingInfo(Graphics g) {		
		// check if tracking an active agent
		if (this.mode == Panel.MODE_TRACK && this.getTrack_id() != -1) {
			// get the tracked agent
			Agent agent = getSelectedAgent();
			
			// draw the raytracing lines
			float[] rays = agent.getRays();
			int nRays = agent.getNRays();
			float direction = agent.getDirection();
			float fullRayLength = agent.getRayLength();
			List<Float> inputLayer = agent.getInputLayer().toArray(); 			
			for (int i = 0; i < nRays; ++i) {
				float rayDir = CollidableObject.normalizeDirection(direction + (float) (rays[i] * Math.PI / 180));
				float rayLength = inputLayer.get(3 * i) * fullRayLength;
				float food = inputLayer.get(3 * i + 1);
				float wall = inputLayer.get(3 * i + 2);
				
				// draw the ray
				drawRay(g, rayDir, rayLength, food, wall);
			}

			// draw the tracking indicator
			drawTrackingIndicator(g);
		}
	}
	
	// draw a raytracing ray from the agent at the center of the screen
	private void drawRay(Graphics g, float rayDir, float rayLength, float food, float wall) {
		// get the graphical coords of the end of the ray
		Agent a = getSelectedAgent();
		float x = a.getX() + (float) (Math.sin(rayDir) * rayLength);
		float y = a.getY() + (float) (Math.cos(rayDir) * rayLength);
		Point p = getGraphicalCoords(x, y);
		
		// set color
		if (food != 0 || wall != 0) {
			g.setColor(Color.WHITE);
		} else {
			g.setColor(Color.BLACK);
		}
		
		g.drawLine(width / 2, height / 2, p.x, p.y);
	}
	
	// draw a indicator on the tracked agent
	private void drawTrackingIndicator(Graphics g) {		
		g.setColor(Color.RED);	
		Agent a = getSelectedAgent();
		float radius = a.getRadius();
		
		// set the stroke width
	    Graphics2D g2 = (Graphics2D) g;
	    g2.setStroke(new BasicStroke((int) (trackingIndicatorThickness * scale)));
	    
	    // draw circle around the agent
		g2.drawOval((int) (width / 2 - radius * scale) - 1, 
				    (int) (height / 2 - radius * scale) - 1, 
				    (int) (2 * radius * scale) + 2, 
				    (int) (2 * radius * scale) + 2);
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
	
	private void drawSaveIndicator(Graphics g) {
		if(this.saveIndCountdown > 0) {
		    g.setColor(Color.RED);
		    g.setFont(new Font("TimesNewRoman", Font.BOLD, 40));
		    g.drawString("Saving...", width - 200, height - 100);
		    saveIndCountdown--;
		}
	}
	
	private void clear(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, this.width, this.height);
	}
	
	private void setOffsets() {
		if (this.mode == Panel.MODE_TRACK && this.getTrack_id() != -1) {
			boolean dead = true;
			
			for (int i = 0; i < environment.getAgents().size(); ++i) {
				Agent agent = environment.getAgents().get(i);
				
				if (agent.getID() == this.getTrack_id()) {
					dead = false;
										
					this.off_x = (int) (agent.getX() - this.width / this.scale / 2); 
					this.off_y = (int) (agent.getY() - this.height / this.scale / 2); 
					
					break;
				}
			}
			
			if (dead) {
				this.setTrack_id(-1);
			}
		}
	}

	// process keyboard actions
	public void keyAction(int action) {
		// process a pan or zoom
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
			this.setTrack_id(-1);
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
		// pause
		if (action == KeyEvent.VK_SPACE) {
			this.environment.togglePaused();
		}
		// restart
		if (action == KeyEvent.VK_R) {
			environment.setupTileMap();
			environment.resetAgents();
			environment.resetFood();
			environment.resetSecondsElapsed();
			environment.init();
		}
	}

	public void mouseClicked(int x, int y) {		
		this.overlayManager.checkClickComponants(x, y);
		Point p = this.getEnvironmentCoordinates(x, y);
		
		// check for tracking a new agent
		if (this.mode == Panel.MODE_TRACK) {
			// loop thru the agents
			ArrayList<Agent> agents = environment.getAgents();

			for (int i = 0; i < agents.size(); ++i) {
				Agent agent = agents.get(i);
				float dist = agent.getDistance(p);
				
				// if clicking on this agent
				if (dist < agent.getRadius()) {
					this.setTrack_id(agent.getID());
					this.setSelectedAgent(agent);
					return;
				}
			}
		}		
	}

	public Agent getSelectedAgent() {
		return this.selectedAgent;
	}

	public int getMode() {
		return this.mode;
	}

	public static int getModeFree() {
		return MODE_FREE;
	}

	public long getTrackingID() {
		return this.getTrack_id();
	}
	
	public int getOff_x() {
		return off_x;
	}

	public int getOff_y() {
		return off_y;
	}

	public int getEnv_width() {
		return env_width;
	}

	public int getEnv_height() {
		return env_height;
	}

	public float getScale() {
		return scale;
	}

	public Environment getEnvironment() {
		return this.environment;
	}

	public long getTrack_id() {
		return track_id;
	}

	public void setTrack_id(long track_id) {
		this.track_id = track_id;
	}

	public void setSelectedAgent(Agent selectedAgent) {
		this.selectedAgent = selectedAgent;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static int getModeTrack() {
		return MODE_TRACK;
	}

	public static double getScrollSpeed() {
		return scroll_speed;
	}

	public static double getZoomSpeed() {
		return zoom_speed;
	}

	public int getSaveIndCountdown() {
		return saveIndCountdown;
	}

	public StatisticPanel getStatisticPanel() {
		return statisticPanel;
	}

	public EnvironmentRenderer getEnvironmentRenderer() {
		return environmentRenderer;
	}

	public OverlayManager getOverlayManager() {
		return overlayManager;
	}
}
