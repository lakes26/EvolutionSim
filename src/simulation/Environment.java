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
	public static double foodPerSecond = 20;
    public static double tickRate = 8;
    
    public static float traitMutationRate = (float) 0.1;  // rate for changing traits such as size
    public static float networkMutationProbability = (float) 0.8;  // probability network will be changed
    public static float networkPerturbationProbability = (float) 0.5;  // probability of weight getting perturbed
    public static float networkPerturbationAmount = (float) 0.1;  // scaler for weight perturbation
    public static float networkNewValueProbability = (float) 0.05;  // probability of weight getting a new value
    public static float networkValueRandRange = (float) 3;  // range for new weights and biases    
    
	private static int foodRadius = 10;
    private static float foodEnergy = (float) 1;
    private static int startingNumFood = 200;

    private static int tileSize = 50;
    
    private static int width = 1200;
    private static int height = 1200;
    
    private static int startingNumAgents = 60;
    private static int splitThreshold = 3;
    private static int deathThreshold = -2;
    public static int minAgentSize = 5;
    public static int maxAgentSize = 50;
    public static int minAgentSpeed = 10;
    public static int maxAgentSpeed = 500;
    private static int maxAge = Integer.MAX_VALUE;
    
    private float secondsElapsed = 0;
    private boolean paused = false;
    
    private ArrayList<Food> foodList;
    private ArrayList<Agent> agentList;
    private Random rand;
    private TileMap tileMap;

    public Environment() {
    	assert(width % tileSize == 0 && height % tileSize == 0);
    	
        foodList = new ArrayList<>();
        agentList = new ArrayList<>();
        rand = new Random();
        
        setupTileMap();
    }  
    
    public void tick() {
    	if (!this.paused) {
    		PriorityQueue<Integer> toRemove= new PriorityQueue<>(100, Collections.reverseOrder());
            List<Agent> toAdd = new ArrayList<>();

            int limit = agentList.size();
            for (int i = 0; i < limit; i++ ) {
                Agent agent = agentList.get(i);
                agent.update(this);

                if (agent.getEnergy() > splitThreshold) {
                    Agent newAgent = new Agent(agent, tileMap, width, height);
                    toAdd.add(newAgent);
                    makeAgentNormal(newAgent);
                    agent.setEnergy(0);
                }

                if (agent.getEnergy() < deathThreshold || agent.getAge() >= maxAge) {
                    toRemove.add(i);
                }
            }

            // remove dead agents
            while (!toRemove.isEmpty()) {
                int index = toRemove.poll();
                agentList.remove(index);
            }
            agentList.addAll(toAdd);
            
            // add food
            spawnInFood();
            
            // add to seconds elapsed
            secondsElapsed += 1 / tickRate;
    	}
    }

    public void init() {
        for (int i= 0; i < startingNumAgents; i++ ) {
            agentList.add(createRandomAgent());
        }
        spawnRandomNewFood(startingNumFood);
    }

    public Agent createRandomAgent() {
        float radius = rand.nextInt(maxAgentSize - minAgentSize) + minAgentSize;
        float speed = rand.nextInt(maxAgentSpeed - minAgentSpeed) + minAgentSpeed;
        
        // loop until we have valid x and y coordinates
    	float x, y;
    	while (true) {
        	x = rand.nextInt(width);
        	y = rand.nextInt(height);
        	
        	// make sure agent isn't spawning in wall
        	if (!tileMap.inWall(x, y)) {
        		break;
        	}
        }
        
        return new Agent(x, y, radius, 0, speed, tileMap);
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

    public void spawnRandomNewFood(int amt) {    	
    	for(int i = 0; i < amt; i++) {            
            // loop until food not spawning in wall
            float x, y;
    		while (true) {
            	x = rand.nextInt(width);
                y = rand.nextInt(height);
                
                if (!tileMap.inWall(x, y)) {
                	break;
                }
            }
            
            foodList.add(new Food(x, y, foodRadius, foodEnergy));
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
    
    public void setupTileMap() {
    	tileMap = new TileMap(width / tileSize, height / tileSize, tileSize);
        //tileMap.addSplitWall(7);
    	//tileMap.addBorder();
        tileMap.randomTiles(.1);
    }
    
    public float getCarryingCapacity() {
        // TODO this needs to be redone -- this doesn't seem to work
    	
//    	float averageBurn = 0;
//        for (Agent a : getAgents()) {
//            averageBurn += -1 * a.getBurnRate();
//        }
//        averageBurn = averageBurn / getAgents().size();
//        
//        return (float) (foodPerSecond / averageBurn);
    	
    	return (float) -1;
    }

    public float averageGeneration() {
        float total = 0;
        for (Agent a: getAgents()) {
            total += a.getGeneration();
        }

        return total / getAgents().size();
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
    
    public void resetAgents() {
    	this.agentList.clear();
    }
    
    public void resetFood() {
    	this.foodList.clear();
    }
    
    public void resetSecondsElapsed() {
    	this.secondsElapsed = 0;
    }
    
    public float getSecondsElapsed() {
    	return secondsElapsed;
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

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public boolean getPaused() {
    	return this.paused;
    }
    
    public int getSplitThreshold() {
        return splitThreshold;
    }

    public void setSplitThreshold(int splitThreshold) {
        this.splitThreshold = splitThreshold;
    }
    
    public TileMap getTileMap() {
    	return tileMap;
    }

	public void setTickRate(double tickRate) {
		Environment.tickRate = tickRate;		
	}
}
