package simulation;

import java.util.List;

public class Agent extends CollidableObject{	
	private float speed;
	private	float direction;
	private	float energy;
	
	public Agent(float x, float y, float radius, float direction) {
		super(x, y, radius);
		this.direction = direction;
	}
	
	public void update(int tickrate, List<Food> food) {
		Food closestFood = this.findClosestFood(food);
		this.pointTowards(closestFood);
		this.move(tickrate);
		if(this.isCollidingWith(closestFood)) {
			food.remove(closestFood);
			this.energy += closestFood.getEnergy();
		}
	}

	private void move(int tickrate) {
		this.x += Math.cos(this.direction) * this.speed/tickrate;
		this.y += Math.sin(this.direction) * this.speed/tickrate;
	}
	
	private Food findClosestFood(List<Food> food) {
		Food closestFood;
		if(food == null) {
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
}
