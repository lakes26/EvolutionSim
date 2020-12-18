package simulation;

import java.util.Scanner;

import graphics.Renderer;

public class Main {

    private static final int frameRate = 30;

    public static void main(String[] args) {
        // start the environment
        Environment env = new Environment();
    	env.init();

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
        
        int startTicks = 0;
        int printEvery = 1000;

        for(int i = 0; i < startTicks; ++i) {
            env.tick();

            if (i % printEvery == 0) {
                System.out.printf("%d/%d: %d agents, %d food CC: %.1f, Generation: %d\n ", i, startTicks, env.getAgents().size(),
                    env.getFood().size(), env.getCarryingCapacity(), env.averageGeneration());
            }
        }

        // setup the renderer
        Renderer renderer = new Renderer();
        renderer.init(env);

        // render forever
        long startTime;
        while (true) {
            // start the timer
        	startTime = System.currentTimeMillis();

            // render the next frame
            renderer.render();

            // wait
            while (System.currentTimeMillis() - startTime < 1000 / frameRate) {

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

    public static int getFramerate() {
        return frameRate;
    }
}
