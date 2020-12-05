package simulation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class Environment {
    private static int foodRadius = 10;
    private static int startingNumFoodSpawned = 60;
    private static int ticksToDecrementNumFoodSpawned = 10000;
    private static int minNumFoodSpawned = 40;
    private static int ticksBetweenFoodSpawn = 50;
    private static int startingNumAgents = 30;
    private static int startingNumFood = 200;
    private static int minAgentSize = 14;
    private static int maxAgentSize = 15;
    private static int minAgentSpeed = 3;
    private static int maxAgentSpeed = 4;
    private static float mutationRate = (float) 0.05;
    private static int maxAge = 800;
    private static int width = 800;
    private static int height = 800;

    private long numTicks;
    private int numFoodSpawned;

    private int tickrate, splitThreshold, deathThreshold, ticksUntilFoodSpawn;

    private ArrayList<Food> foodList;
    private ArrayList<Agent> agentList;
    private Random rand;

    public int getSplitThreshold() {
        return splitThreshold;
    }

    public void setSplitThreshold(int splitThreshold) {
        this.splitThreshold= splitThreshold;
    }

    public Environment() {
        numTicks = 0;
        numFoodSpawned = Environment.startingNumFoodSpawned;
        foodList = new ArrayList<>();
        agentList = new ArrayList<>();
        rand = new Random();
        tickrate = 1;
        splitThreshold = 3;
        deathThreshold = -2;
        ticksUntilFoodSpawn = ticksBetweenFoodSpawn;
    }

    public void tick() {

        PriorityQueue<Integer> toRemove= new PriorityQueue<>(100, Collections.reverseOrder());
        List<Agent> toAdd= new ArrayList<>();

        int limit= agentList.size();
        for (int i= 0; i < limit; i++ ) {
            Agent agent= agentList.get(i);
            agent.update(this);

            //	        for(int f = 0; f < foodList.size(); f++) {
            //	            Food food = foodList.get(f);
            //	            if(agent.isCollidingWith(food)) {
            //	                agent.addEnergy(food.getEnergy());
            //	                foodList.remove(f);
            //	                f--;
            //	            }
            //	        }

            if (agent.getEnergy() > splitThreshold) {
                // 100 is spawndistance. probably shouldn't be a literal, but who cares
                Agent newAgent= new Agent(agent, mutationRate);
                newAgent.setSpeed(Environment.minAgentSpeed);
                toAdd.add(newAgent);
                agent.setEnergy(0);
            }

            if (agent.getEnergy() < deathThreshold || agent.getAge() >= maxAge) {
                toRemove.add(i);
            }
        }

        while (!toRemove.isEmpty()) {
            int index= toRemove.poll();
            agentList.remove(index);
        }
        agentList.addAll(toAdd);

        ticksUntilFoodSpawn-- ;
        if (ticksUntilFoodSpawn <= 0) {
            spawnRandomNewFood(numFoodSpawned);
            ticksUntilFoodSpawn= ticksBetweenFoodSpawn;
        }

        numTicks++ ;
        if (numTicks % ticksToDecrementNumFoodSpawned == 0) {
            // System.out.print(String.format("%d:%d:%d:%d\n", this.numTicks, this.agentList.size(),
            // this.foodList.size(), this.numFoodSpawned));
            if (numFoodSpawned > Environment.minNumFoodSpawned) {
                numFoodSpawned-- ;
            }
            // maxAge++;
        }
    }

    public void init() {
        for (int i= 0; i < startingNumAgents; i++ ) {
            agentList.add(createRandomAgent());
        }
        spawnRandomNewFood(startingNumFood);
    }

    public Agent createRandomAgent() {
        float x= rand.nextInt(width);
        float y= rand.nextInt(height);
        float radius= rand.nextInt(maxAgentSize - minAgentSize) + minAgentSize;
        float speed= rand.nextInt(maxAgentSpeed - minAgentSpeed) + minAgentSpeed;
        return new Agent(x, y, radius, 0, speed);
    }
	
	public void spawnRandomNewFood(int amt) {
	    for(int i = 0; i < amt; i++) {
	        float x = rand.nextInt(width);
	        float y = rand.nextInt(height);
	        float energy = (float) 1;
	        foodList.add(new Food(x, y, foodRadius, energy));
	    }
	}
	
	public void saveToFile(String filename) throws FileNotFoundException, IOException {
	    File file = new File(filename);
	    file.delete();
	    ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(filename));
	    for(Agent agent: this.agentList) {
	        objOut.writeObject(agent);
	    }
	    for(Food food: this.foodList) {
	        objOut.writeObject(food);
	    }
	}
	
	public void loadFromFile(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
	    ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(filename));
	    while(true) {
	        Object obj = null;
	        try {
	            obj = objIn.readObject();
	        } catch(Exception e) {
	            break;
	        }
	        if(obj instanceof Agent) {
	            agentList.add((Agent) obj);
	        } else if(obj instanceof Food) {
	            foodList.add((Food) obj);
	        }
	    }
	}
	
	public ArrayList<Agent> getAgents() {
	    return agentList;
	}
	
	public ArrayList<Food> getFood() {
	    return foodList;
	}
	
	public int getTickrate() {
		return this.tickrate;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}


    public int getCarryingCapacity() {
        float capacity;
        float averageBurn= 0;
        for (Agent a : getAgents()) {
            averageBurn+= -1 * a.getBurnRate();
        }
        averageBurn= averageBurn / getAgents().size();
        float foodPerTick= (float) numFoodSpawned / (float) ticksBetweenFoodSpawn;
        capacity= foodPerTick / averageBurn;
        return (int) capacity;
    }
}
