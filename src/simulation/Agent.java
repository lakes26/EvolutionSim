package simulation;

import java.util.List;
import java.util.Random;

public class Agent extends CollidableObject{
	protected float speed;
	protected float direction;
	protected float energy;
	protected float age;
	
	public Agent(float x, float y, float radius, float direction, float speed) {
		super(x, y, radius);
		this.direction = direction;
		this.speed = speed;
		this.age = 0;
	}
	
	/**
	 * Creates a duplicate of given agent, spawned a given distance from it's original
	 * @param agent agent to be duplicated
	 * @param spawnDistance distance duplicate will be spawned away from original
	 */
	public Agent(Agent agent, float spawnDistance, float mutationRate) {
	    super(agent.getX() + spawnDistance, agent.getY() + spawnDistance, agent.getRadius());
	    this.direction = 0;
	    this.speed = (float) (agent.getSpeed() + mutationRate * new Random().nextGaussian());
	    this.age = 0;
	}
	
	public void update(Environment e) {
		Food closestFood = this.findClosestFood(e.getFood());
		if(closestFood != null) {
    		this.pointTowards(closestFood);
    		this.move(e.getTickrate(), 1);
    		this.energy -= speed * 0.01;
    		if(this.isCollidingWith(closestFood)) {
    			e.getFood().remove(closestFood);
    			this.energy += closestFood.getEnergy();
    		}
		}
		this.age += 1/e.getTickrate();
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
}
