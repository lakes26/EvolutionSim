package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import simulation.Agent;
import simulation.Environment;

public class Panel extends JPanel{
	private static final long serialVersionUID = -310866009165515372L;

	private static final int MODE_FREE = 1;
	private static final int MODE_TRACK = 2;	
	
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

	//private NeuralNetworkVisualizer neuralNetworkVisualizer;

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
		
		//neuralNetworkVisualizer = new NeuralNetworkVisualizer(this, new Dimension(300, 300));
		//neuralNetworkVisualizer.setLocation(25, 150);
		
		this.environmentRenderer = new EnvironmentRenderer(this);
		this.overlayManager = new OverlayManager(this);
		this.overlayManager.add(new StatisticPanel(this, 5, 15, 0));
		this.overlayManager.add(new NeuralNetworkVisualizer(this, new Dimension(300, 300)));
		this.overlayManager.add(new OffspringPanel(this, new Dimension(100, 300)));
		this.overlayManager.add(new VariablesPanel(this, new Dimension(200, 500)));
		this.overlayManager.add(new PopulationStatisticPanel(this));
	}

	@Override
	public void paintComponent(Graphics g) {		
		clear(g);		
		
		// set the and y offsets based on tracking
		setOffsets();
		//render the environment;
		environmentRenderer.renderEnvironment(g);
		overlayManager.renderOverlay(g);
		
		//draw save indicator if saving
		drawSaveIndicator(g);

		// draw tracking indicator if tracking an active agent
		drawTrackingIndicator(g);
	}
	
	private void drawTrackingIndicator(Graphics g) {
		int size = 15;  // TODO make class variables
		int indicatorWidth = 2;
		
		if (this.mode == Panel.MODE_TRACK && this.getTrack_id() != -1) {
			g.setColor(Color.RED);
			
			g.fillRect((width - indicatorWidth) / 2, (height - indicatorWidth) / 2 - size, 
					   indicatorWidth, 2 * size);
			g.fillRect((width - indicatorWidth) / 2 - size, (height - indicatorWidth) / 2,
					   2 * size, indicatorWidth);
		}
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
		
		if (this.mode == Panel.MODE_TRACK) {
			// loop thru the agents
			ArrayList<Agent> agents = environment.getAgents();

			for (int i = 0; i < agents.size(); ++i) {
				Agent agent = agents.get(i);
				
				double dist = Math.sqrt(Math.pow(p.x - agent.getX(), 2) - 
										Math.pow(p.y - agent.getY(), 2));
				
				// if clicking on this agent
				if (dist < agent.getRadius() * scale) {
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
