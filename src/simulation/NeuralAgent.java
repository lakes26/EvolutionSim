package simulation;

import java.util.List;

import utils.Matrix;
import utils.NeuralNetwork;

public class NeuralAgent extends Agent {
	
	private float speed;
	private	float direction;
	private	float energy;
	private float age;
	private static int inputLength = 6;
	private float perceptiveRange;
	private NeuralNetwork neuralNet;
	
	public NeuralAgent(float x, float y, float radius, float direction, float speed) {
		super(x, y, radius, direction, speed);
		this.energy = 0;
		this.age = 0;
		this.perceptiveRange = 100;
		this.neuralNet = new NeuralNetwork(inputLength,8,3);
	}
	
	public NeuralAgent(NeuralAgent a, float mutationRate) {
		super(a.x + 100, a.y + 100, a.radius, 0, a.speed);
		this.neuralNet = a.neuralNet.mutate(mutationRate);
		this.energy = 0;
		this.age = 0;
		this.perceptiveRange = 100;
	}
	
	public float getEnergy() {
		return this.energy;
	}
	
	public float getAge() {
		return this.age;
	}
	
	private float angleBetween(CollidableObject o) {
		return (float) Math.atan2(o.x - this.x, o.y - this.y);
	}
	
	private Matrix pollEnvironment(Environment e) {
		float[] inputArray = new float[inputLength];
		for(Food food : e.getFood()) {
			float angle = this.angleBetween(food);
			if(this.getDistance(food) <= this.perceptiveRange && angle <= this.direction + Math.PI/12 && angle >= this.direction - Math.PI/12) {
				inputArray[2] = 1;
			}
			else if(this.getDistance(food) <= this.perceptiveRange && angle <= this.direction - Math.PI/12 && angle <= this.direction - 3 * Math.PI/12) {
				inputArray[4] = 1;
			}
			else if(this.getDistance(food) <= this.perceptiveRange && angle <= this.direction + 3 * Math.PI/12 && angle >= this.direction + Math.PI/12) {
				inputArray[0] = 1;
			}
		}
		for(Agent agent : e.getAgents()) {
			float angle = this.angleBetween(agent);
			if(this.getDistance(agent) <= this.perceptiveRange && angle <= this.direction + Math.PI/12 && angle >= this.direction - Math.PI/12) {
				inputArray[3] = 1;
			}
			else if(this.getDistance(agent) <= this.perceptiveRange && angle <= this.direction - Math.PI/12 && angle <= this.direction - 3 * Math.PI/12) {
				inputArray[5] = 1;
			}
			else if(this.getDistance(agent) <= this.perceptiveRange && angle <= this.direction + 3 * Math.PI/12 && angle >= this.direction + Math.PI/12) {
				inputArray[1] = 1;
			}
		}
		return Matrix.fromArray(inputArray);
	}
	
	@Override
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
			this.move(e.getTickrate());
			this.energy -= speed * 0.01;
		} else {
			this.turnRight();
		}
		this.move(e.getTickrate());
		this.energy -= speed * 0.01;
		
		Food closestFood = this.findClosestFood(e.getFood());
		if(closestFood != null) {
			if(this.isCollidingWith(closestFood)) {
				e.getFood().remove(closestFood);
				this.energy += closestFood.getEnergy();
			}
		}
		this.age += 1/e.getTickrate();
	}

	
}
