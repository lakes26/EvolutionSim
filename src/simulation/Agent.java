package simulation;

import java.util.List;
import java.util.Random;

import utils.Matrix;
import utils.NeuralNetwork;

public class Agent extends CollidableObject{
	protected float speed;
	protected float direction;
	protected float energy;
	protected float age;
	protected byte[] DNA;
	private boolean add;
	
	private static int inputLength = 3;
	private float perceptiveRange;
	private NeuralNetwork neuralNet;
	private static Random rand = new Random();
	
	public Agent(float x, float y, float radius, float direction, float speed) {
		super(x, y, radius);
		this.add = rand.nextBoolean();
		this.direction = direction;
		this.speed = speed;
		this.age = 0;
		this.energy = 0;
		this.perceptiveRange = 150;
		
		this.neuralNet = new NeuralNetwork(inputLength, 6, 3);
		this.DNA = new byte[3];
		rand.nextBytes(this.DNA);
	}
	
	/**
	 * Creates a duplicate of given agent, spawned a given distance from it's original
	 * @param agent agent to be duplicated
	 * @param spawnDistance distance duplicate will be spawned away from original
	 */
	public Agent(Agent agent, float mutationRate) {
	    super(agent.getX() + (float) rand.nextFloat() * 200 - 100, agent.getY() + (float) rand.nextFloat() * 200 - 100, agent.getRadius());
	    int toAdd = rand.nextInt(4);
	    if(toAdd < 4) {
	    	this.add = agent.add;
	    } else {
	    	this.add = !agent.add;
	    }
	    this.radius = (float) (agent.getRadius() + mutationRate * new Random().nextGaussian());
	    if(this.radius < 1) {
	    	this.radius = 1;
	    }
	    this.add = agent.add;
	    this.neuralNet = agent.getNeuralNet().mutate(mutationRate);
	    this.DNA = agent.mutateDNA();
	    this.direction = 0;
	    this.speed = (float) (agent.getSpeed() + mutationRate * new Random().nextGaussian());
	    this.age = 0;
	    this.perceptiveRange = 150;
	}
	
	private NeuralNetwork getNeuralNet() {
		return this.neuralNet;
	}

	public void update(Environment e) {
		Matrix inputLayer = this.pollEnvironment(e);
		List<Float> outputLayer = this.neuralNet.propForward(inputLayer);
		
		int maxIndex = 0;
		float max = 0;
		for(int i = 0; i < outputLayer.size(); i++) {
			if(outputLayer.get(i) > max) {
				max = outputLayer.get(i);
				maxIndex = i;
			}
		}
		
		if(maxIndex == 0) {
			this.turnLeft();
		} else if(maxIndex == 1) {
			;
		} else {
			this.turnRight();
		}
		this.move(e.getTickrate(), (float) 1);
		this.addEnergy((float) (-this.getSpeed() * 0.002 * Math.log(this.getRadius())));
		
		
		Food closestFood = this.findClosestFood(e.getFood());
		if(closestFood != null) {
			if(this.isCollidingWith(closestFood)) {
				e.getFood().remove(closestFood);
				this.energy += closestFood.getEnergy();
			}
		}
		this.age = 1/e.getTickrate();
	}
	
	public float getEnergy() {
		return this.energy;
	}
	
	public float getAge() {
		return this.age;
	}

	protected void move(int tickrate, float steps) {
		this.x += Math.sin(this.direction) * this.speed/tickrate * steps;
		this.y += Math.cos(this.direction) * this.speed/tickrate * steps;
	}
	
	protected void turnLeft() {
		this.direction -= Math.PI / 64;
		if(direction <= 0) {
			direction = (float) (2 * Math.PI);
		}
	}
	
	protected void turnRight() {
		this.direction += Math.PI / 64;
		if(direction >= 2 * Math.PI) {
			direction = (float) 0;
		}
	}
	
	protected Food findClosestFood(List<Food> food) {
		Food closestFood;
		if(food == null || food.isEmpty()) {
			return null;
		} else {
			closestFood = food.get(0);
		}
		
		for(Food element : food) {
			if(this.getDistance(element) < this.getDistance(closestFood)) {
				closestFood = element;
			}
		}
		return closestFood;
	}
	
	private byte[] mutateDNA() {
		byte[] mutatedDNA = new byte[this.DNA.length];
		int mutationRate = 3;
		for(int i = 0; i < this.DNA.length; i++) {
			if(this.add) {
				if(this.DNA[i] >= Byte.MAX_VALUE - mutationRate) {
					this.add = false;
					mutatedDNA[i] = (byte) (this.DNA[i] - rand.nextInt(2) * mutationRate);
				} else {
					mutatedDNA[i] = (byte) (this.DNA[i] + (rand.nextInt(2) * mutationRate));
				}
			} 
			else {
				if(this.DNA[i] <= Byte.MIN_VALUE + mutationRate) {
					this.add = true;
					mutatedDNA[i] = (byte) (this.DNA[i] + rand.nextInt(2) * mutationRate);
				} else {
					mutatedDNA[i] = (byte) (this.DNA[i] - (rand.nextInt(2) * mutationRate));
				}
			}
		}
		return mutatedDNA;
	}
	
	private Matrix pollEnvironment(Environment e) {
		float[] inputArray = new float[inputLength];
		for(Food food : e.getFood()) {
			float angle = this.angleBetween(food);
			if(this.getDistance(food) <= this.perceptiveRange && angle <= this.direction + Math.PI/12 && angle >= this.direction - Math.PI/12) {
				inputArray[0]++;
			}
			else if(this.getDistance(food) <= this.perceptiveRange && angle <= this.direction - Math.PI/12 && angle <= this.direction - 3 * Math.PI/12) {
				inputArray[1]++;
			}
			else if(this.getDistance(food) <= this.perceptiveRange && angle <= this.direction + 3 * Math.PI/12 && angle >= this.direction + Math.PI/12) {
				inputArray[2]++;
			}
		}
		return Matrix.fromArray(inputArray);
	}
	
	private void pointTowards(CollidableObject o) {
		this.direction = (float) Math.atan2(o.x - this.x, o.y - this.y);
	}


    public float getSpeed() {
        return speed;
    }
    
    public void addEnergy(float e) {
        energy += e;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    } 
    
    public void setAge(float age) {
    	this.age = age;
    }
    
    public byte[] getDNA() {
    	return this.DNA;
    }

	public void setSpeed(int speed) {
		this.speed = speed;
		
	}
}
