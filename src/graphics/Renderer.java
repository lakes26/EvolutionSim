package graphics;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import input.KeyInput;
import simulation.Environment;

public class Renderer {	
	private Environment environment;
	private Panel panel;
	private JFrame frame;	
	private KeyInput keyInput;
	
	public Renderer() {

	}
	
	/**
	 * 
	 * @param environment Environment to be rendered
	 */
	
	public void init(Environment environment) {
				
		this.environment = environment;
		
		//create the Panel and JFrame
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		int width = d.width;
		int height = d.height;
		
		this.panel = new Panel(environment, width, height);
		this.frame = new JFrame();
	
		
		// setup the JFrame		
		this.frame.add(this.panel);		
		this.frame.setSize(width, height);
		this.frame.setResizable(true);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);

		
		// setup the panel
		panel.setFocusable(true);
		panel.requestFocusInWindow();
		
		// setup the key input
		this.keyInput = new KeyInput(this.panel);
		this.panel.addKeyListener(this.keyInput);
	}
	
	/**
	 * Render the environment
	 */
	public void render() {
		// check for key input
		this.keyInput.tick();
		
		// tick the environment
		this.environment.tick();
		
		// rerender the game
		this.panel.repaint();
	}
	
	@SuppressWarnings("exports")
	public JFrame getFrame() {
		return frame;
	}
	
}
