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
    private static int startingNumFoodSpawned = 40;
    private static int ticksToDecrementNumFoodSpawned = 10000;
    private static int minNumFoodSpawned = 40;
    public static int ticksBetweenFoodSpawn = 50;
    
    //public static double foodPerTick = 1.7;
    public static double foodPerSecond = 40;
    
    private static int startingNumAgents = 60;
    private static int startingNumFood = 200;
    private static int minAgentSize = 14;
    private static int maxAgentSize = 16;
    private static int minAgentSpeed = 50;
    private static int maxAgentSpeed = 200;
    public static float mutationRate = (float) 0.05;
    private static int maxAge = Integer.MAX_VALUE;
    private static int width = 800;
    private static int height = 800;
    public static double tickRate = 5;

    private long numTicks = 0;
    private int numFoodSpawned;
    private boolean paused = false;
    
    private int splitThreshold, deathThreshold, ticksUntilFoodSpawn;

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
        numFoodSpawned = Environment.startingNumFoodSpawned;
        foodList = new ArrayList<>();
        agentList = new ArrayList<>();
        rand = new Random();
        splitThreshold = 3;
        deathThreshold = -2;
        ticksUntilFoodSpawn = ticksBetweenFoodSpawn;
    }

    public void tick() {
    	if (!this.paused) {
    		PriorityQueue<Integer> toRemove= new PriorityQueue<>(100, Collections.reverseOrder());
            List<Agent> toAdd= new ArrayList<>();

            int limit = agentList.size();
            for (int i = 0; i < limit; i++ ) {
                Agent agent = agentList.get(i);
                agent.update(this);

                if (agent.getEnergy() > splitThreshold) {
                    Agent newAgent= new Agent(agent, mutationRate);
                    toAdd.add(newAgent);
                    makeAgentNormal(newAgent);
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

            if (numTicks % ticksToDecrementNumFoodSpawned == 0) {
                // System.out.print(String.format("%d:%d:%d:%d\n", this.numTicks, this.agentList.size(),
                if (numFoodSpawned > Environment.minNumFoodSpawned) {
                    numFoodSpawned-- ;
                }
            }
            
            spawnInFood();
            
            numTicks++;
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
            float energy = 1;
            foodList.add(new Food(x, y, foodRadius, energy));
        }
    }

    private void spawnInFood() {
    	double foodPerTick = (float) this.foodPerSecond / tickRate;
        double decimalPart = foodPerTick - Math.floor(foodPerTick);
        float randDecider = rand.nextFloat();
        if (randDecider < decimalPart) {
            spawnRandomNewFood((int) Math.floor(foodPerTick) + 1);
        } else {
            spawnRandomNewFood((int) Math.floor(foodPerTick));
        }
    }

    public void saveToFile(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);
        file.delete();
        ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(filename));
        for(Agent agent: agentList) {
            objOut.writeObject(agent);
        }
        for(Food food: foodList) {
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
    
    public int getCarryingCapacity() {
    	// TODO this needs to be redone
    	
//        float capacity;
//        float averageBurn= 0;
//        for (Agent a : getAgents()) {
//            averageBurn+= -1 * a.getBurnRate();
//        }
//        averageBurn= averageBurn / getAgents().size();
//        float foodPerTick= (float) numFoodSpawned / (float) ticksBetweenFoodSpawn;
//        capacity= foodPerTick / averageBurn;
//        return (int) capacity;
    	return -1;
    }

    public int averageGeneration() {
        float total = 0;
        for (Agent a: getAgents()) {
            total += a.getGeneration();
        }
        float average = total / getAgents().size();
        return (int) average;
    }

    public float getAverageSpeed() {
        float totalSpeed = 0;
        for(int i = 0; i < agentList.size(); i++) {
            totalSpeed += agentList.get(i).getSpeed();
        }
        return totalSpeed / agentList.size();
    }

    public float getAverageSize() {
        float totalSize = 0;
        for(int i = 0; i < agentList.size(); i++) {
            totalSize += agentList.get(i).getRadius();
        }
        return totalSize / agentList.size();
    }

    private void makeAgentNormal(Agent a) {
        a.setRadius(Math.min(Math.max(a.getRadius(), minAgentSize), maxAgentSize));
        a.setSpeed(Math.min(Math.max(a.getSpeed(), minAgentSpeed), maxAgentSpeed));
        a.keepInBounds();
    }
    
    public void togglePaused() {
    	this.paused = !this.paused;
    }
    
    public ArrayList<Agent> getAgents() {
        return agentList;
    }

    public ArrayList<Food> getFood() {
        return foodList;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean getPaused() {
    	return this.paused;
    }
}
