package simulation;

import graphics.Renderer;

public class Main {
	
	private static final int frame_rate = 30;
	
	public static void main(String[] args) {
<<<<<<< HEAD
		// start the environment
=======
		
>>>>>>> refs/remotes/origin/master
		Environment env = new Environment();
		env.init();
		
<<<<<<< HEAD
		int start_ticks = 0;
		int print_every = 100;
		
		for(int i = 0; i < start_ticks; ++i) {
=======
		for(int i = 0; i < 400000; i++) {
>>>>>>> refs/remotes/origin/master
			env.tick();
			
			if (i % print_every == 0) {
				System.out.printf("%d/%d: %d agents, %d food\n", i, start_ticks, env.getAgents().size(),
  					   		      env.getFood().size());
			}
		}
<<<<<<< HEAD
		
		// setup the renderer
=======
				
>>>>>>> refs/remotes/origin/master
		Renderer renderer = new Renderer();		
		renderer.init(env);
<<<<<<< HEAD
		
		//render forever
=======


>>>>>>> refs/remotes/origin/master
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
