package simulation;

import java.util.ArrayList;
import java.util.Random;

public class Environment {
    private static int foodRadius = 10;
    private static int numFoodSpawned = 10;
    private static int ticksBetweenFoodSpawn = 500;
    private static int startingNumAgents = 90;
    private static int startingNumFood = 60;
    private static int minAgentSize = 5;
    private static int maxAgentSize = 20;
    private static int minAgentSpeed = 1;
    private static int maxAgentSpeed = 10;
	private int tickrate, height, width, splitThreshold, deathThreshold, ticksUntilFoodSpawn;
	private ArrayList<Food> foodList;
	private ArrayList<Agent> agentList;
	private Random rand;
	
	public Environment() {
	    foodList = new ArrayList<Food>();
	    agentList = new ArrayList<Agent>();
	    rand = new Random();
	    tickrate = 1;
	    height = 800;
	    width = 800;
	    splitThreshold = 3;
	    deathThreshold = -2;
	    ticksUntilFoodSpawn = ticksBetweenFoodSpawn;
	}
	
	public void tick() {
	    for(int i = 0; i < agentList.size(); i++) {
	        Agent agent = agentList.get(i);
	        agent.update(tickrate, foodList);
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
	            Agent newAgent = new Agent(agent, 100);
	            agentList.add(newAgent);
	            agent.setEnergy(0);
	        }
	        if(agent.getEnergy() < deathThreshold) {
	            agentList.remove(i);
	            i--;
	        }
	        ticksUntilFoodSpawn--;
	        if(ticksUntilFoodSpawn <= 0) {
	            spawnRandomNewFood(numFoodSpawned);
	            ticksUntilFoodSpawn = ticksBetweenFoodSpawn;
	        }
	    }
	}
	
	public void init() {
	    for(int i = 0; i < startingNumAgents; i++) {
	        agentList.add(createRandomAgent());
	    }
	    spawnRandomNewFood(startingNumFood);
	    for(Food food: foodList) {
	        System.out.println(food.getX() + ", " + food.getY());
	    }
	}
	
	public Agent createRandomAgent() {
	    float x = rand.nextInt(width);
        float y = rand.nextInt(height);
        float radius = rand.nextInt(maxAgentSize - minAgentSize) + minAgentSize;
        float speed = rand.nextInt(maxAgentSpeed - minAgentSpeed) + minAgentSpeed;
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
	
	public ArrayList<Agent> getAgents() {
	    return agentList;
	}
	
	public ArrayList<Food> getFood() {
	    return foodList;
	}
}
