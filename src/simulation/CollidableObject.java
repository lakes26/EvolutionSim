package simulation;

import java.io.Serializable;

public class CollidableObject implements Serializable{

    private static final long serialVersionUID = 1L;
    protected float x;
	protected float y;
	protected float radius;
	
	public CollidableObject(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	public float getDistance(CollidableObject o) {
		return (float) Math.sqrt(Math.pow(this.x - o.x, 2) + Math.pow(this.y - o.y, 2));
	}
	
	/**
	 * Determines whether or not two objects are colliding.
	 * 
	 * @param o the object for which we are checking collision
	 * @return Whether the object is colliding with o.
	 */
	public boolean isCollidingWith(CollidableObject o) {
		if(this.getDistance(o) <= this.radius + o.radius) {
			return true;
		} else {
			return false;
		}
	}
	
	protected float angleBetween(CollidableObject o) {
		return (float) Math.atan2(o.x - this.x, o.y - this.y);
	}

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }
	
	
	
	
}
