package simulation;

import java.io.Serializable;
import java.util.ArrayList;
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
    
    private static float networkThreshold = (float) .5;
    
    private int numOffspring = 0;
    protected float age = 0;
    private int generation = 0;

    protected float energy = 2;
    private static float burnRate = (float) 0.002;
    private static float burnPerSecond = (float) 0;
    
    private static float baseMoveSpeed = (float) 1;
    private static float baseTurnSpeed = (float) 1;
    
    private static float perceptiveRangeFood = 200;
    private static int verticalVisionSlicesFood = 2;
    private static int horizontalVisionSlicesFood = 3;
    private static float fovFood = (float) (Math.PI / 2);

    private static float perceptiveRangeWall = 200;
    private static int verticalVisionSlicesWall = 1;
    private static int horizontalVisionSlicesWall = 7;
    private static float fovWall = (float) (Math.PI);
    
    private static int outputLength = 2;
    private static int inputLength = verticalVisionSlicesFood * horizontalVisionSlicesFood
    								 + verticalVisionSlicesWall * horizontalVisionSlicesWall;

    private static int turnSpeed = 1;
    
    // create a new agent
    public Agent(float x, float y, float radius, float direction, float speed, TileMap tileMap) {
        super(x, y, radius);
    	
        this.add = rand.nextBoolean();
        this.direction = direction;
        this.speed = speed;

        this.neuralNet = new NeuralNetwork(inputLength, outputLength);
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
        outputLayer= neuralNet.propForward(inputLayer);
        List<Float> outputLayer = getOutputLayer().toArray();

//        int maxIndex = 0;
//        float max = 0;
//        for (int i = 0; i < outputLayer.size(); i++ ) {
//            if (outputLayer.get(i) > max) {
//                max = outputLayer.get(i);
//                maxIndex = i;
//            }
//        }
//
//        if (maxIndex == 0) {
//            move(Agent.baseMoveSpeed, e.getTileMap());
//            turn(Agent.baseTurnSpeed);  // turn left
//            direction = normalizeDirection(direction);
//        } else if (maxIndex == 1) {
//            move(Agent.baseMoveSpeed, e.getTileMap());
//        } else {
//            move(Agent.baseMoveSpeed, e.getTileMap());
//            turn(-Agent.baseTurnSpeed);  // turn right
//            direction = normalizeDirection(direction);
//        }

        // turn the agent
        boolean networkLeft = outputLayer.get(0) > Agent.networkThreshold;
        boolean networkRight = outputLayer.get(1) > Agent.networkThreshold;
        if (networkLeft && !networkRight) {
        	turn(Agent.baseTurnSpeed);
        } else if (networkRight && !networkLeft) {
        	turn(-Agent.baseTurnSpeed);
        }
        
        // move the agent
        move(Agent.baseMoveSpeed, e.getTileMap());
//        boolean networkMove = outputLayer.get(2) > Agent.networkThreshold;
//        if (networkMove) {
//            move(Agent.baseMoveSpeed, e.getTileMap());        	
//        }

        // check if we are on food
        Food closestFood = findClosestFood(e.getFood());
        if (closestFood != null) {
            if (isCollidingWith(closestFood)) {
                e.getFood().remove(closestFood);
                energy+= closestFood.getEnergy();
            }
        }
        
        age += (float) 1 / Environment.tickRate;
    
        // burn a base amount of food
        this.energy -= burnPerSecond;
    }

    protected void move(float dist, TileMap tileMap) {
    	// get new coords
    	float xNew = x + (float) (Math.sin(direction) * speed * dist / Environment.tickRate);
        float yNew = y + (float) (Math.cos(direction) * speed * dist / Environment.tickRate);

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
        addEnergy((float) getBurnRate() * dist);
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
        if (food == null || food.isEmpty()) {
            return null;
        } else {
            closestFood= food.get(0);
        }

        for (Food element : food) {
            if (getDistance(element) < getDistance(closestFood)) {
                closestFood= element;
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
        int totalSlicesFood = verticalVisionSlicesFood * horizontalVisionSlicesFood;       
        float[] inputArray = new float[inputLength];
        float fovLeftFood = -fovFood / 2;
        float fovStepFood = fovFood / horizontalVisionSlicesFood;
        float rangeStepFood = perceptiveRangeFood / verticalVisionSlicesFood;
        
        // process food inputs
        ArrayList<Food> food = e.getFood();
        for (Food f : food) {
            float dir = normalizeDirection(directionOf(f));
            float angle = normalizeDirection(direction - dir);
            float dist = getDistance(f);
            int vSlice = (int) (dist / rangeStepFood);
            int hSlice = (int) ((angle + fovLeftFood) / fovStepFood);
            
            if (vSlice >= 0 && vSlice < verticalVisionSlicesFood) {
                if (hSlice >= 0 && hSlice < horizontalVisionSlicesFood) {
                    int index = vSlice + hSlice * verticalVisionSlicesFood;
                    inputArray[index] += 1;
                }
            }
        }
        
        // process wall inputs 
        //    just checks the middle of each slice
        float fovLeftWall = -fovWall / 2;
        float fovStepWall = fovWall / horizontalVisionSlicesWall;
        float rangeStepWall = perceptiveRangeWall / verticalVisionSlicesWall;
        
        // loop thru vision slices
        TileMap tileMap = e.getTileMap();
        for (float i = 0; i < horizontalVisionSlicesWall; ++i) {
        	for (float j = 0; j < verticalVisionSlicesWall; ++j) {
        		// get the x and y of the center of the slice
        		float centerAngle = normalizeDirection(direction + fovLeftWall + ((float) .5 + i) * fovStepWall);  // get the angle of the center of the slice
        		float dist = ((float) .5 + j) * rangeStepWall;  // get the distance to the center of the slice

        		float xCenter = this.x + (float) (Math.sin(centerAngle) * dist);
                float yCenter = this.y + (float) (Math.cos(centerAngle) * dist);
        		
        		if (tileMap.inWall(xCenter, yCenter)) {
        			inputArray[(int) (totalSlicesFood + i + verticalVisionSlicesWall * j)] = 1;
        		}
        	}
        }
        
        // update input layer
        inputLayer = Matrix.fromArray(inputArray);
    }

    private void pointTowards(CollidableObject o) {
        direction= (float) Math.atan2(o.x - x, o.y - y);
    }

    private void generateID() {
        Random rand = new Random();
        id = Math.abs(rand.nextLong());
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

    public float getBurnRate() {
        return (float) (-getSpeed() * Agent.burnRate * Math.log(getRadius()) / Environment.tickRate);
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
}
