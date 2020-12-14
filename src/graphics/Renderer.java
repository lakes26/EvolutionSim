package graphics;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;


import input.KeyInput;
import input.MouseInput;
import simulation.Environment;
import simulation.Main;

public class Renderer {	
	private Environment environment;
	private Panel panel;
	private JFrame frame;	
	private KeyInput keyInput;
	private MouseListener mouseListener;
	private MouseInput mouseInput;
	
	private double framesToRender;
	
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
		
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				panel.setWidth(frame.getWidth());
				panel.setHeight(frame.getHeight());
			}
		});

		frame.setVisible(true);

		
		// setup the panel
		panel.setFocusable(true);
		panel.requestFocusInWindow();
		
		// setup the key input
		this.keyInput = new KeyInput(this.panel);
		this.panel.addKeyListener(this.keyInput);
		this.mouseInput = new MouseInput(this.panel);
		this.panel.addMouseListener(this.mouseInput);
		
		this.framesToRender = 0;
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
