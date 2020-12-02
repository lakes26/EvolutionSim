package simulation;

public class Food extends CollidableObject{
	
	private int energy;
	
	public Food(float x, float y, float radius, int energy) {
		super(x, y, radius);
		this.energy = energy;
	}
	
	public int getEnergy() {
		return this.energy;
	}
}
