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
    protected float energy;
    protected float age;
    protected byte[] DNA;
    private boolean add;
    private static int inputLength= 6;
    private Matrix inputLayer;
    private float perceptiveRange;
    private float firstRange;
    private NeuralNetwork neuralNet;
    private static Random rand= new Random();
    private int numOffspring;
    private int generation;
    private long id;
	private Matrix outputLayer;
	private List<Agent> offspring;

    public Agent(float x, float y, float radius, float direction, float speed) {
        super(x, y, radius);
        add= rand.nextBoolean();
        this.direction= direction;
        this.speed= speed;
        age= 0;
        energy= 0;
        perceptiveRange= 150;
        firstRange= perceptiveRange / 2;
        neuralNet= new NeuralNetwork(inputLength, 8, 3);
        DNA= new byte[3];
        rand.nextBytes(DNA);
        numOffspring = 0;
        generation = 0;
        generateID();
        offspring = new ArrayList<>();
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
        speed = (float) (agent.getSpeed() + mutationRate * (float) rand.nextGaussian());
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
        this.pollEnvironment(e);
        this.outputLayer= neuralNet.propForward(this.inputLayer);       
        List<Float> outputLayer = this.getOutputLayer().toArray();
        
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
        age += (float) 1 / e.getTickrate();
    }

    public float getEnergy() {
        return energy;
    }

    public float getAge() {
        return age;
    }

    protected void move(int tickrate, float steps) {
        x+= Math.sin(direction) * speed;
        y+= Math.cos(direction) * speed;
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

        this.inputLayer =  Matrix.fromArray(inputArray);
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

	public int getNumOffspring() {
		return this.numOffspring;
	}
	
	public Matrix getInputLayer() {
		return this.inputLayer;
	}
	
	public NeuralNetwork getNeuralNetwork() {
		return this.neuralNet;
	}
	
    
	public Matrix getOutputLayer() {
		return this.outputLayer;
	}
	
    public int getGeneration() {
        return generation;
    }
    
    public List<Agent> getOffspring() {
 		return offspring;
 	}
}
