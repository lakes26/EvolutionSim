package simulation;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import utils.Matrix;
import utils.NeuralNetwork;


public class Agent extends CollidableObject implements Serializable{
    private static final long serialVersionUID = 1L;
    protected float speed;
    protected float direction;
    protected float energy;
    protected float age;
    protected byte[] DNA;
    private boolean add;
    private static int inputLength= 6;
    private float perceptiveRange;
    private float firstRange;
    private NeuralNetwork neuralNet;
    private static Random rand= new Random();

    private long id;

    public Agent(float x, float y, float radius, float direction, float speed) {
        super(x, y, radius);
        add= rand.nextBoolean();
        this.direction= direction;
        this.speed= speed;
        age= 0;
        energy= 0;
        perceptiveRange= 100;
        firstRange= perceptiveRange / 2;
        neuralNet= new NeuralNetwork(inputLength, 6, 3);
        DNA= new byte[3];
        rand.nextBytes(DNA);

        generateID();
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
        radius= (float) (agent.getRadius() + mutationRate * new Random().nextGaussian());
        if (radius < 1) {
            radius= 1;
        }
        add= agent.add;
        neuralNet= agent.getNeuralNet().mutate(mutationRate);
        DNA= agent.mutateDNA();
        direction= 0;
        speed= (float) (agent.getSpeed() + mutationRate * new Random().nextGaussian());
        age= 0;
        perceptiveRange= 150;
        firstRange= perceptiveRange / 2;

        generateID();
    }

    private void generateID() {
        Random rand = new Random();
        id = Math.abs(rand.nextLong());
    }

    private NeuralNetwork getNeuralNet() {
        return neuralNet;
    }

    public void update(Environment e) {
        Matrix inputLayer= pollEnvironment(e);
        List<Float> outputLayer= neuralNet.propForward(inputLayer);

        int maxIndex= 0;
        float max= 0;
        for (int i= 0; i < outputLayer.size(); i++ ) {
            if (outputLayer.get(i) > max) {
                max= outputLayer.get(i);
                maxIndex= i;
            }
        }

        if (maxIndex == 0) {
            turnLeft();
        } else if (maxIndex == 1) {

        } else {
            turnRight();
        }
        direction = normalizeDirection(direction);


        move(e.getTickrate(), 1);
        addEnergy(getBurnRate());

        Food closestFood= findClosestFood(e.getFood());
        if (closestFood != null) {
            if (isCollidingWith(closestFood)) {
                e.getFood().remove(closestFood);
                energy+= closestFood.getEnergy();
            }
        }
        age= 1 / e.getTickrate();
    }

    public float getEnergy() {
        return energy;
    }

    public float getAge() {
        return age;
    }

    protected void move(int tickrate, float steps) {
        x+= Math.sin(direction) * speed / tickrate * steps;
        y+= Math.cos(direction) * speed / tickrate * steps;
        keepInBounds();
    }

    private void keepInBounds() {
        x= Math.min(Math.max(x, 0), 800);
        y= Math.min(Math.max(y, 0), 800);
    }

    protected void turnLeft() {
        direction += Math.PI / 64;
    }

    protected void turnRight() {
        direction -= Math.PI / 64;
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
        int mutationRate= 3;
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

    private Matrix pollEnvironment(Environment e) {
        float[] inputArray= new float[inputLength];
        for (Food food : e.getFood()) {
            float dir = normalizeDirection(directionOf(food));
            float angle = normalizeDirection(direction-dir);
            if (getDistance(food) <= firstRange && angle < Math.PI / 12 && angle > -Math.PI / 12 ) {
                inputArray[0]++ ;
            } else if (getDistance(food) <= firstRange && angle >= Math.PI / 12 &&
                angle <= 3 * Math.PI / 12) {
                inputArray[1]++ ;
            } else if (getDistance(food) <= firstRange && angle <= -Math.PI / 12 &&
                angle >= -3 * Math.PI / 12) {
                inputArray[2]++ ;
            } else if (getDistance(food) <= perceptiveRange && angle < Math.PI / 12 && angle > -Math.PI / 12) {
                inputArray[3]++ ;
            } else if (getDistance(food) <= perceptiveRange &&
                angle >= Math.PI / 12 && angle <= 3 * Math.PI / 12) {
                inputArray[4]++ ;
            } else if (getDistance(food) <= perceptiveRange &&
                angle <= -Math.PI / 12 && angle >= -3 * Math.PI / 12) {
                inputArray[5]++ ;
            }
        }

        return Matrix.fromArray(inputArray);
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
        return (float) (-getSpeed() * 0.002 * Math.log(getRadius()));
    }



}
