package simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import utils.Matrix;
import utils.NeuralNetwork;

public class Agent extends CollidableObject implements Serializable{
    private static final long serialVersionUID = 1L;

    private static Random rand = new Random();

    protected float speed;
    protected float direction;
    protected byte[] DNA;
    private boolean add;
    private Matrix inputLayer;
    private NeuralNetwork neuralNet;
    private long id;
    private Matrix outputLayer;
    private List<Agent> offspring;

    public static float networkThreshold = (float) .5;

    private int numOffspring = 0;
    protected float age = 0;
    private int generation = 0;

    protected float energy = 2;

    private static float moveComponent = (float).5; //amount of energy burned from moving vs existing
    private static float totalBurnPerSecond = (float).3;//total amount burned per creature/second
    private static float burnPerMove = totalBurnPerSecond*moveComponent;
    private static float burnPerSecond = totalBurnPerSecond*(1-moveComponent);

    // constants for raytracing
    private static float[] rays = { -90, -60, -45, -30, -20, -10, 0, 10, 20, 30, 45, 60, 90 };  // degree values for each ray
    private static float rayLength = 150;
    private static int nRays = rays.length;

    private static int inputTypes = 3;  // food, walls, other agents 
    private static int rayStep = 5;

    
    private static int inputLength = (inputTypes + 1) * nRays + 1;
    private static int outputLength = 3;

    private static float turnSpeed = (float) 0.4;

    // create a new agent
    public Agent(float x, float y, float radius, float direction, float speed, TileMap tileMap) {
        super(x, y, radius);
    	
        this.add = rand.nextBoolean();
        this.direction = direction;
        this.speed = speed;

        this.neuralNet = new NeuralNetwork(inputLength, 6, outputLength);
        this.DNA = new byte[3];
        rand.nextBytes(DNA);
        this.offspring = new ArrayList<>();

        this.generateID();
    }

    /** Creates a duplicate of given agent, spawned a given distance from it's original
     *
     * @param agent         agent to be duplicated
     * @param spawnDistance distance duplicate will be spawned away from original
     * @param tileMap  		tileMap to make sure the new agent isn't in a wall */
    public Agent(Agent agent, TileMap tileMap, int envWidth, int envHeight) {
    	super(0, 0, agent.getRadius());
    
        // loop until we have valid x and y coordinates
    	float x, y;
    	while (true) {
        	x = agent.getX() + rand.nextFloat() * 200 - 100;
        	y = agent.getY() + rand.nextFloat() * 200 - 100;
        	
        	x = Math.max(x, 0);
        	x = Math.min(x, (float) envWidth);
        	y = Math.max(y, 0);
        	y = Math.min(y, (float) envHeight);
        	
        	// make sure agent isn't spawning in wall
        	if (!tileMap.inWall(x, y)) {
            	this.x = x;
            	this.y = y;
        		break;
        	}
        }

        int toAdd = rand.nextInt(4);
        if (toAdd < 4) {
            add = agent.add;
        } else {
            add = !agent.add;
        }

        radius = mutateRadius(agent.getRadius());
        speed = mutateSpeed(agent.getSpeed());

        add = agent.add;
        neuralNet = agent.getNeuralNet().mutate();
        DNA = agent.mutateDNA();
        direction = (float) (rand.nextFloat() * 2 * Math.PI);

        generation = agent.generation + 1;
        agent.numOffspring++;

        agent.offspring.add(this);
        offspring = new ArrayList<>();

        generateID();
    }

    // return a new radius mutated from param oldRadius
    private float mutateRadius(float oldRadius) {
        float radius = (float) (oldRadius + Environment.traitMutationRate * rand.nextGaussian());
        radius = Math.max(Math.min(radius, Environment.maxAgentSize), Environment.minAgentSize);
        return radius;
    }

    // return a new radius mutated from param oldSpeed
    private float mutateSpeed(float oldSpeed) {
        float speed = oldSpeed + Environment.traitMutationRate * (float) rand.nextGaussian();
        speed = Math.max(Math.min(speed, Environment.maxAgentSpeed), Environment.minAgentSpeed);
        return speed;
    }

    // update the agent
    //	  this is called once per tick for each agent
    public void update(Environment e) {
        pollEnvironment(e);
        outputLayer = neuralNet.propForward(inputLayer);
        List<Float> outputLayer = getOutputLayer().toArray();

        // turn the agent
        boolean networkLeft = outputLayer.get(0) > Agent.networkThreshold;
        boolean networkRight = outputLayer.get(1) > Agent.networkThreshold;

        if (networkLeft && !networkRight) {
            turn(1);
        } else if (networkRight && !networkLeft) {
            turn(-1);
        }

        float distScale = Math.max((float).5,outputLayer.get(2));
        float moveDist = speed*distScale;
        move(moveDist, e.getTileMap());
        // move the agent

        // check if we are on food
        Food closestFood = findClosestFood(e.getFood());
        if (closestFood != null) {
            if (isCollidingWith(closestFood)) {
                e.getFood().remove(closestFood);
                energy+= closestFood.getEnergy();
            }
        }

        age += 1 / Environment.tickRate;

        // burn a base amount of food
        addEnergy(burnEachSecond());
    }

    protected void move(float distance, TileMap tileMap) {
        // get new coords
        float xNew = x + (float) (Math.sin(direction) * distance / Environment.tickRate);
        float yNew = y + (float) (Math.cos(direction) * distance / Environment.tickRate);

        xNew = Math.min(Math.max(xNew, 0), Environment.getWidth() - 1);
        yNew = Math.min(Math.max(yNew, 0), Environment.getHeight() - 1);

        // check for collisions
        int tileSize = tileMap.getTileSize();
        int[][] tiles = tileMap.getTiles();

        int xTile = (int) (x / tileSize);
        int yTile = (int) (y / tileSize);
        int xNewTile = (int) (xNew / tileSize);
        int yNewTile = (int) (yNew / tileSize);
        int xDelta = xNewTile - xTile;
        int yDelta = yNewTile - yTile;

        // check if moving tiles
        // horizontal
        if (tiles[xNewTile][yTile] == 1) {
            if (xDelta == 1) {  // right
                xNew = tileSize * xNewTile - 1;
            } else if (xDelta == -1) {  // left
                xNew = tileSize * (xNewTile + 1) + 1;
            }
        }
        // vertical
        if (tiles[xTile][yNewTile] == 1) {
            if (yDelta == 1) {  // down
                yNew = tileSize * yNewTile - 1;
            } else if (yDelta == -1) {  // up
                yNew = tileSize * (yNewTile + 1) + 1;
            }
        }

        x = xNew;
        y = yNew;

        // update energy
        addEnergy(burnOnMove(distance));
    }

    public void keepInBounds() {
        x = Math.min(Math.max(x, 0), Environment.getWidth() - 1);
        y = Math.min(Math.max(y, 0), Environment.getHeight() - 1);
    }

    // turn, positive for left, negative for right
    protected void turn(float amount) {
        direction += Math.PI * amount * Agent.turnSpeed / Environment.tickRate;
    }

    protected Food findClosestFood(List<Food> food) {
        Food closestFood;
        float closestFoodDist;
        if (food == null || food.isEmpty()) {
            return null;
        } else {
            closestFood = food.get(0);
            closestFoodDist = getDistance(closestFood);
        }

        for (Food element : food) {
            float dist = getDistance(element);
            if (dist < closestFoodDist) {
                closestFood = element;
                closestFoodDist = dist;
            }
        }

        return closestFood;
    }

    private byte[] mutateDNA() {
        byte[] mutatedDNA= new byte[DNA.length];
        int mutationRate= 10;
        for (int i= 0; i < DNA.length; i++ ) {
            if (add) {
                if (DNA[i] >= Byte.MAX_VALUE - mutationRate) {
                    add= false;
                    mutatedDNA[i]= (byte) (DNA[i] - rand.nextInt(2) * mutationRate);
                } else {
                    mutatedDNA[i]= (byte) (DNA[i] + rand.nextInt(2) * mutationRate);
                }
            } else {
                if (DNA[i] <= Byte.MIN_VALUE + mutationRate) {
                    add= true;
                    mutatedDNA[i]= (byte) (DNA[i] + rand.nextInt(2) * mutationRate);
                } else {
                    mutatedDNA[i]= (byte) (DNA[i] - rand.nextInt(2) * mutationRate);
                }
            }
        }
        return mutatedDNA;
    }

    // setup the input layer for the neural network
    private void pollEnvironment(Environment e) {       
        float[] inputArray = new float[inputLength];

        // setup min dist arrays
        float[] minFoodDist = new float[rays.length];
        float[] minWallDist = new float[rays.length];
        float[] minAgentDist = new float[rays.length];
        for (int i = 0; i < rays.length; ++i) {
            minFoodDist[i] = rayLength;
            minWallDist[i] = rayLength;
            minAgentDist[i] = rayLength;
        }
        
        // pre-calculate ray directions
        float[] rayDirections = new float[rays.length];
        for (int i = 0; i < rays.length; ++i) {
            rayDirections[i] = normalizeDirection(direction + (float) (rays[i] * Math.PI / 180));
        }
        
        // get food distance
        ArrayList<Food> foods = e.getFood();
        for (Food element : foods) {
            // get distance to agent
            float dist = getDistance(element);
            
            // if within range
            if (dist < rayLength) {
                // get angle and arc length
                float foodDirection = (float) Math.atan2(element.x - x, element.y - y);
                float maxDirectionDeviation = Environment.foodRadius / dist;  // this is an approximation based on the arc length
                
                // loop thru each ray
                for (int i = 0; i < rays.length; ++i) {
                    float dirDiff = Math.abs(foodDirection - rayDirections[i]);
                    
                    if (dirDiff < maxDirectionDeviation && dist < minFoodDist[i]) {  // TODO there may be a small edge case here
                        minFoodDist[i] = dist;
                    }
                }
            }
        }
        
        // get wall distance
        TileMap tileMap = e.getTileMap();
        // check each ray individually
        for (int i = 0; i < rays.length; ++i) {
            // start at the agent
            float rayX = x;
            float rayY = y;
            float stepX = (float) (Math.sin(rayDirections[i]) * rayStep);
            float stepY = (float) (Math.cos(rayDirections[i]) * rayStep);
            
            // step along the ray
            for (int rayDist = 0; rayDist < rayLength; rayDist += rayStep) {
                if (tileMap.inWall(rayX, rayY)) {
                    minWallDist[i] = rayDist;
                    break;
                }
                                
                rayX += stepX;
                rayY += stepY;
            }
        }
        
        // get agent distance
        ArrayList<Agent> agents = e.getAgents();
        for (Agent element : agents) {
            // ignore this agent
            if (element.getId() == id) {
                continue;
            }
            
            // get distance to agent
            float dist = getDistance(element);
            
            // if within range
            if (dist < rayLength) {
                // get angle and arc length
                float foodDirection = (float) Math.atan2(element.x - x, element.y - y);
                float maxDirectionDeviation = Environment.foodRadius / dist;  // this is an approximation based on the arc length
                
                // loop thru each ray
                for (int i = 0; i < rays.length; ++i) {
                    float dirDiff = Math.abs(foodDirection - rayDirections[i]);
                    
                    if (dirDiff < maxDirectionDeviation && dist < minAgentDist[i]) {
                        minAgentDist[i] = dist;
                    }
                }
            }
        }
        
        // update the input array
        for (int i = 0; i < rays.length; ++i) {
            // TODO this is a bit sloppy, maybe use an enum?
                        
            // see nothing
            if (minFoodDist[i] == rayLength && minWallDist[i] == rayLength && minAgentDist[i] == rayLength) {
                inputArray[(inputTypes + 1) * i] = 1;
            // see something but need to decide what
            } else {
                // default to food closest
                int closestOffset = 1;
                float closestDist = minFoodDist[i];
                
                // check for wall closest
                if (minWallDist[i] < closestDist) {
                    closestOffset = 2;
                    closestDist = minWallDist[i];
                }
                // check for agent closest
                if (minAgentDist[i] < closestDist) {
                    closestOffset = 3;
                    closestDist = minAgentDist[i];
                }
                
                // set values in input array
                inputArray[(inputTypes + 1) * i] = closestDist / rayLength;
                inputArray[(inputTypes + 1) * i + closestOffset] = 1;
            }
        }
        
        // update the input layer class variable
        inputLayer = Matrix.fromArray(inputArray);      
    }


    public float burnEachSecond() {
        float avgSize = 15;//arbitrary
        float radiusRatio = (float) (Math.pow(radius,2)/Math.pow(avgSize,2));
        return -radiusRatio*burnPerSecond/(float)Environment.tickRate;
    }

    public float burnOnMove(float distance) {
        float avgSpeed = 50;//arbitrary
        float speedCost = (float)(Math.pow(distance,2)/Math.pow(avgSpeed,2));
        return -speedCost*burnPerMove/(float)Environment.tickRate;
        //(.002*50)^2 = .01/move
    }

    public void makeNormal() {
        setRadius(Math.min(Math.max(getRadius(), Environment.minAgentSize), Environment.maxAgentSize));
        setSpeed(Math.min(Math.max(getSpeed(), Environment.minAgentSpeed), Environment.maxAgentSpeed));
        keepInBounds();
    }

    private void pointTowards(CollidableObject o) {
        direction = (float) Math.atan2(o.x - x, o.y - y);
    }

    private void generateID() {
        Random rand = new Random();
        id = Math.abs(rand.nextLong());
    }

    public float[] getRays() {
        return rays;
    }

    public int getNRays() {
        return nRays;
    }

    public float getRayLength() {
        return rayLength;
    }

    private NeuralNetwork getNeuralNet() {
        return neuralNet;
    }

    public float getEnergy() {
        return energy;
    }

    public float getAge() {
        return age;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public float getDirection() {
        return direction;
    }

    public boolean isAdd() {
        return add;
    }

    public static int getInputLength() {
        return inputLength;
    }

    public static Random getRand() {
        return rand;
    }

    public long getId() {
        return id;
    }

    public float getSpeed() {
        return speed;
    }

    public void addEnergy(float e) {
        energy+= e;
    }

    public void setEnergy(float energy) {
        this.energy= energy;
    }

    public void setAge(float age) {
        this.age= age;
    }

    public byte[] getDNA() {
        return DNA;
    }

    public void setSpeed(int speed) {
        this.speed= speed;
    }

    public long getID() {
        return id;
    }

    public int getNumOffspring() {
        return numOffspring;
    }

    public Matrix getInputLayer() {
        return inputLayer;
    }

    public NeuralNetwork getNeuralNetwork() {
        return neuralNet;
    }

    public void setSpeed(float speed) {
        this.speed= speed;
    }

    public void setRadius(float rad) {
        radius= rad;
    }

    public Matrix getOutputLayer() {
        return outputLayer;
    }

    public int getGeneration() {
        return generation;
    }

    public List<Agent> getOffspring() {
        return offspring;
    }
    
    public int getInputTypes() {
    	return inputTypes;
    }
}
