package simulation;

import graphics.Renderer;

public class Main {
	
	private static final int frame_rate = 30;
	
	public static void main(String[] args) {
		Environment env = new Environment();
		
		Renderer renderer = new Renderer();		
		renderer.init(env);
		
		// render forever
		long start_time;
		while (true) {			
			// start the timer
			start_time = System.currentTimeMillis();
			
			// render the next frame
			renderer.render();

			// wait
			while (System.currentTimeMillis() - start_time < 1000 / frame_rate) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();

					// if sleep is interrupted just close the program stupid java
					System.exit(1);
				}
			}
		}
	}
}
