package simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import utils.Matrix;
import utils.NeuralNetwork;

public class Agent extends CollidableObject implements Serializable{
    private static final long serialVersionUID = 1L;
    protected float speed;
    protected float direction;
    protected byte[] DNA;
    private boolean add;
    private Matrix inputLayer;
    private NeuralNetwork neuralNet;
    private static Random rand= new Random();
    private long id;
    private Matrix outputLayer;
    private List<Agent> offspring;
    
    private int numOffspring = 0;
    protected float age = 0;
    private int generation = 0;

    protected float energy = 2;
    private float perceptiveRange = 150;
    private float firstRange = 75;

    private float fov = (float) (Math.PI/4);
    private static int verticalVisionSlices = 2;
    private static int horizontalVisionSlices = 4;
    private static int inputLength = verticalVisionSlices*horizontalVisionSlices;
    
    private static int turnSpeed = 2;
    
    public Agent(float x, float y, float radius, float direction, float speed) {
        super(x, y, radius);
        
        this.add = rand.nextBoolean();
        this.direction = direction;
        this.speed = speed;

        this.neuralNet= new NeuralNetwork(inputLength, 8, 3);
        this.DNA = new byte[3];
        rand.nextBytes(DNA);
        this.offspring = new ArrayList<>();

        this.generateID();
    }

    /** Creates a duplicate of given agent, spawned a given distance from it's original
     *
     * @param agent         agent to be duplicated
     * @param spawnDistance distance duplicate will be spawned away from original */
    public Agent(Agent agent, float mutationRate) {
        super(agent.getX() + rand.nextFloat() * 200 - 100,
            agent.getY() + rand.nextFloat() * 200 - 100, agent.getRadius());
        int toAdd= rand.nextInt(4);
        if (toAdd < 4) {
            add= agent.add;
        } else {
            add= !agent.add;
        }
        radius= (float) (agent.getRadius() + mutationRate * rand.nextGaussian());
        if (radius < 1) {
            radius= 1;
        }
        add= agent.add;
        neuralNet= agent.getNeuralNet().mutate(mutationRate);
        DNA= agent.mutateDNA();
        direction= 0;
        speed = agent.getSpeed() + mutationRate * (float) rand.nextGaussian();
        age= 0;
        perceptiveRange= 150;
        firstRange= perceptiveRange / 2;
        generation = agent.generation + 1;
        agent.numOffspring++;
        numOffspring = 0;
        offspring = new ArrayList<>();

        generateID();

        agent.offspring.add(this);
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

    public float getPerceptiveRange() {
        return perceptiveRange;
    }

    public float getFirstRange() {
        return firstRange;
    }

    public static Random getRand() {
        return rand;
    }

    public long getId() {
        return id;
    }

    private void generateID() {
        Random rand = new Random();
        id = Math.abs(rand.nextLong());
    }

    private NeuralNetwork getNeuralNet() {
        return neuralNet;
    }

    public void update(Environment e) {
        pollEnvironment(e);
        outputLayer= neuralNet.propForward(inputLayer);
        List<Float> outputLayer = getOutputLayer().toArray();

        int maxIndex= 0;
        float max= 0;
        for (int i= 0; i < outputLayer.size(); i++ ) {
            if (outputLayer.get(i) > max) {
                max= outputLayer.get(i);
                maxIndex= i;
            }
        }

        if (maxIndex == 0) {
            move(1);
            turnLeft();
        } else if (maxIndex == 1) {
            move(1);
        } else {
            move(1);
            turnRight();
        }
        direction = normalizeDirection(direction);

        Food closestFood= findClosestFood(e.getFood());
        if (closestFood != null) {
            if (isCollidingWith(closestFood)) {
                e.getFood().remove(closestFood);
                energy+= closestFood.getEnergy();
            }
        }
        age += (float) 1 / e.tickRate;
    }

    public float getEnergy() {
        return energy;
    }

    public float getAge() {
        return age;
    }

    protected void move(float dist) {
        x+= Math.sin(direction) * speed * dist / Environment.tickRate;
        y+= Math.cos(direction) * speed * dist / Environment.tickRate;
        addEnergy((float) getBurnRate() * dist);
        keepInBounds();
    }

    public void keepInBounds() {
        x= Math.min(Math.max(x, 0), 800);
        y= Math.min(Math.max(y, 0), 800);
    }

    protected void turnLeft() {
        direction += Math.PI / this.turnSpeed / Environment.tickRate;
    }

    protected void turnRight() {
        direction -= Math.PI / this.turnSpeed / Environment.tickRate;
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

    private void pollEnvironment(Environment e) {
        ArrayList<Food> food = e.getFood();
        int totalSlices = verticalVisionSlices*horizontalVisionSlices;
        float[] inputArray = new float[totalSlices];
        float fovLeft = -fov/2;
        float fovStep = fov/horizontalVisionSlices;
        float rangeStep = perceptiveRange/verticalVisionSlices;

        for (Food f:food) {
            float dir = normalizeDirection(directionOf(f));
            float angle = normalizeDirection(direction-dir);
            float dist = getDistance(f);
            int vSlice = (int)(dist/rangeStep);
            int hSlice = (int)((angle+fovLeft)/fovStep);
            if (vSlice >=0 && vSlice < verticalVisionSlices) {
                if (hSlice >= 0 && hSlice < horizontalVisionSlices) {
                    int index = vSlice + hSlice*verticalVisionSlices;
                    inputArray[index] += 1;
                }
            }
        }
        inputLayer =  Matrix.fromArray(inputArray);
    }


    private void pointTowards(CollidableObject o) {
        direction= (float) Math.atan2(o.x - x, o.y - y);
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
        return (float) (-getSpeed() * 0.002 * Math.log(getRadius()) / Environment.tickRate);
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
