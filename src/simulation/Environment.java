package simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class Environment {
    private static int foodRadius = 10;
    private static int numFoodSpawned = 40;
    private static int ticksBetweenFoodSpawn = 50;
    private static int startingNumAgents = 300;
    private static int startingNumFood = 160;
    private static int minAgentSize = 5;
    private static int maxAgentSize = 20;
    private static int minAgentSpeed = 1;
    private static int maxAgentSpeed = 10;
    private static float mutationRate = (float) 0.05;
    private static int maxAge = 800;
	private int tickrate, height, width, splitThreshold, deathThreshold, ticksUntilFoodSpawn;

	public int getSplitThreshold() {
		return splitThreshold;
	}

	public void setSplitThreshold(int splitThreshold) {
		this.splitThreshold = splitThreshold;
	}

	private ArrayList<Food> foodList;
	private ArrayList<NeuralAgent> agentList;
	private Random rand;
	
	public Environment() {
	    foodList = new ArrayList<Food>();
	    agentList = new ArrayList<NeuralAgent>();
	    rand = new Random();
	    tickrate = 1;
	    height = 800;
	    width = 800;
	    splitThreshold = 3;
	    deathThreshold = -2;
	    ticksUntilFoodSpawn = ticksBetweenFoodSpawn;
	}
	
	public void tick() {
		
		PriorityQueue<Integer> toRemove = new PriorityQueue<>(100, Collections.reverseOrder());
		List<NeuralAgent> toAdd = new ArrayList<>();
		
		int limit = agentList.size();
	    for(int i = 0; i < limit; i++) {
	        NeuralAgent agent = agentList.get(i);
	        agent.update(this);
//	        for(int f = 0; f < foodList.size(); f++) {
//	            Food food = foodList.get(f);
//	            if(agent.isCollidingWith(food)) {
//	                agent.addEnergy(food.getEnergy());
//	                foodList.remove(f);
//	                f--;
//	            }
//	        }
	
	        if(agent.getEnergy() > splitThreshold) {
	            //100 is spawndistance. probably shouldn't be a literal, but who cares
	            NeuralAgent newAgent = new NeuralAgent(agent, mutationRate);
	            toAdd.add(newAgent);
	            agent.setEnergy(0);
	        }
	  
	        if(agent.getEnergy() < deathThreshold || agent.getAge() >= maxAge) {
	            toRemove.add(i);
	        }
	    }
	    
	    while(!toRemove.isEmpty()) {
	    	int index = toRemove.poll();
	    	agentList.remove(index);
	    }
	    agentList.addAll(toAdd);
	    
	    ticksUntilFoodSpawn--;
        if(ticksUntilFoodSpawn <= 0) {
            spawnRandomNewFood(numFoodSpawned);
            ticksUntilFoodSpawn = ticksBetweenFoodSpawn;
        }
	}
	
	public void init() {
	    for(int i = 0; i < startingNumAgents; i++) {
	        agentList.add(createRandomAgent());
	    }
	    spawnRandomNewFood(startingNumFood);
	}
	
	public NeuralAgent createRandomAgent() {
	    float x = rand.nextInt(width);
        float y = rand.nextInt(height);
        float radius = rand.nextInt(maxAgentSize - minAgentSize) + minAgentSize;
        float speed = rand.nextInt(maxAgentSpeed - minAgentSpeed) + minAgentSpeed;
        return new NeuralAgent(x, y, radius, 0, speed);
    }
	
	public void spawnRandomNewFood(int amt) {
	    for(int i = 0; i < amt; i++) {
	        float x = rand.nextInt(width);
	        float y = rand.nextInt(height);
	        float energy = (float) 1;
	        foodList.add(new Food(x, y, foodRadius, energy));
	    }
	}
	
	public ArrayList<NeuralAgent> getAgents() {
	    return agentList;
	}
	
	public ArrayList<Food> getFood() {
	    return foodList;
	}
	
	public int getTickrate() {
		return this.tickrate;
	}
}
