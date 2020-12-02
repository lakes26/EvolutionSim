package simulation;

public class Food extends CollidableObject{
	
	private float energy;
	
	public Food(float x, float y, float radius, float energy) {
		super(x, y, radius);
		this.energy = energy;
	}
	
	public float getEnergy() {
		return this.energy;
	}
}
