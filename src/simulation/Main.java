package simulation;

import java.util.Scanner;

import graphics.Renderer;

public class Main {
	
    private static final double frameRate = 30;

    private static int startTicks = 0;
    private static int printEvery = 1000;
    		
    public static void main(String[] args) {
        // start the environment
        Environment env = new Environment();
    	env.init();

    	// TODO saving/loading no longer working
//        Scanner scnr = new Scanner(System.in);
//        System.out.print("Load from save.txt?: ");
//        if (scnr.nextLine().equalsIgnoreCase("y")) {
//            try {
//                env.loadFromFile("save.txt");
//            } catch (Exception e1) {
//                e1.printStackTrace();
//                System.exit(1);
//            }
//        }
//        scnr.close();        
        
    	if (startTicks > 0) {
    		System.out.printf("Simulating %d ticks\n", startTicks);
    	}
    	
        long startTime = System.currentTimeMillis();
        	
        for(int i = 1; i < startTicks + 1; ++i) {
            env.tick();

            if (i % printEvery == 0) {
                if (env.getAgents().size() == 0) {
                	System.out.println("All agents dead, exiting early");
                	System.exit(0);
                }
            	
            	long curTime = System.currentTimeMillis();
                
                double propDone = (double) i / startTicks;
                double secRemaining = (double) (curTime - startTime) / 1000 * (1 / propDone - 1);
                double minRemaining = Math.floor(secRemaining / 60);
                double hourRemaining = Math.floor(minRemaining / 60);
                secRemaining -= 60 * minRemaining;
                minRemaining -= 60 * hourRemaining;                
                
            	System.out.printf("%.1f %% - %.0f h %.0f m %.0f s remaining - Agents: %d, Food: %d, Average Generation: %d\n ", 
            			100 * propDone, hourRemaining, minRemaining, secRemaining, env.getAgents().size(),
                    env.getFood().size(), (int) env.averageGeneration());
            }
        }

        // print info
        if (startTicks > 0) { 
        	double secElapsed = (double) (System.currentTimeMillis() - startTime) / 1000;
        	double minElapsed = Math.floor(secElapsed / 60);
        	double hourElapsed = Math.floor(minElapsed / 60);
        	secElapsed -= 60 * minElapsed;
        	minElapsed -= 60 * hourElapsed;
        	
            System.out.printf("Simulated %d ticks in %.0f h %.0f m %.0f s\n", 
            			      startTicks, hourElapsed, minElapsed, secElapsed);
        }
        
        // set the tickrate to match the framerate
        env.setTickRate(frameRate);
        
        // setup the renderer
        Renderer renderer = new Renderer();
        renderer.init(env);

        // render forever
        long frameTimer;
        while (true) {
            // start the timer
        	frameTimer = System.currentTimeMillis();

            // render the next frame
            renderer.render();

            // wait
            while (System.currentTimeMillis() - frameTimer < 1000 / frameRate) {

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

    public static double getFramerate() {
        return frameRate;
    }
}
