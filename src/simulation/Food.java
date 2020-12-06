package simulation;

import java.io.Serializable;

public class Food extends CollidableObject implements Serializable{

    private static final long serialVersionUID = 1L;
    private float energy;
	
	public Food(float x, float y, float radius, float energy) {
		super(x, y, radius);
		this.energy = energy;
	}
	
	public float getEnergy() {
		return this.energy;
	}
}
