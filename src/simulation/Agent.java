package simulation;

import java.util.List;

public class Agent extends CollidableObject{
	private float speed;
	private	float direction;
	private	float energy;
	private float age;
	
	public Agent(float x, float y, float radius, float direction, float speed) {
		super(x, y, radius);
		this.direction = direction;
		this.speed = speed;
	}
	
	/**
	 * Creates a duplicate of given agent, spawned a given distance from it's original
	 * @param agent agent to be duplicated
	 * @param spawnDistance distance duplicate will be spawned away from original
	 */
	public Agent(Agent agent, float spawnDistance) {
	    super(agent.getX() + spawnDistance, agent.getY() + spawnDistance, agent.getRadius());
	    this.direction = 0;
	    this.speed = agent.getSpeed();
	}
	
	
	public void update(int tickrate, List<Food> food) {
		Food closestFood = this.findClosestFood(food);
		if(closestFood != null) {
    		this.pointTowards(closestFood);
    		this.move(tickrate);
    		this.energy -= speed * 0.01;
    		if(this.isCollidingWith(closestFood)) {
    			food.remove(closestFood);
    			this.energy += closestFood.getEnergy();
    		}
		}
		this.age += 1/tickrate;
	}
	
	public float getEnergy() {
		return this.energy;
	}
	
	public float getAge() {
		return this.age;
	}

	private void move(int tickrate) {
		this.x += Math.sin(this.direction) * this.speed/tickrate;
		this.y += Math.cos(this.direction) * this.speed/tickrate;
	}
	
	private Food findClosestFood(List<Food> food) {
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
    
    
	
	
}
