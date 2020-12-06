package simulation;

import java.util.Scanner;

import graphics.Renderer;

public class Main {

    private static final int frame_rate = 30;

    public static void main(String[] args) {
        // start the environment
        Environment env = new Environment();
        Scanner scnr = new Scanner(System.in);
        System.out.print("Load from save.txt?: ");
        if (scnr.nextLine().equalsIgnoreCase("y")) {
            try {
                env.loadFromFile("save.txt");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            env.init();
        }
        int start_ticks = 0;
        
        int print_every = 1000;

        for(int i = 0; i < start_ticks; ++i) {
            env.tick();

            if (i % print_every == 0) {
                System.out.printf("%d/%d: %d agents, %d food CC: %d\n ", i, start_ticks, env.getAgents().size(),
                    env.getFood().size(), env.getCarryingCapacity());
            }
        }


        // setup the renderer
        Renderer renderer= new Renderer();
        renderer.init(env);

        // render forever
        long start_time;
        while (true) {

            // start the timer
            start_time= System.currentTimeMillis();

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
