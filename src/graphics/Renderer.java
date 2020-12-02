package graphics;

import javax.swing.JFrame;

import simulation.Environment;

public class Renderer {	
	private Environment environment;
	private Panel panel;
	private JFrame frame;	
	
	public Renderer() {

	}
	
	/**
	 * 
	 * @param environment Environment to be rendered
	 */
	public void init(Environment environment) {
		this.environment = environment;
		
		// create the Panel and JFrame
		this.panel = new Panel(environment);
		this.frame = new JFrame();

		// setup the JFrame		
		this.frame.add(this.panel);		
		this.frame.setSize(300, 300);  // TODO add the width and height from the environment
		this.frame.setResizable(false);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setVisible(true);
	}
	
	/**
	 * Render the environment
	 */
	public void render() {
		// tick the environment
		//this.environment.tick();  // TODO uncomment..
		
		// rerender the game
		this.panel.repaint();
	}
	
	
}
