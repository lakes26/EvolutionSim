package simulation;


import java.awt.Point;
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
        return (float) Math.sqrt(Math.pow(x - o.x, 2) + Math.pow(y - o.y, 2));
    }

    public float getDistance(@SuppressWarnings("exports") Point p) {
        return (float) Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2));
    }

    /**
     * Determines whether or not two objects are colliding.
     *
     * @param o the object for which we are checking collision
     * @return Whether the object is colliding with o.
     */
    public boolean isCollidingWith(CollidableObject o) {
        if(this.getDistance(o) <= radius + o.radius) {
            return true;
        } else {
            return false;
        }
    }

    protected float directionOf(CollidableObject o) {
        float angle = (float) Math.atan2(o.x - x, o.y - y);
        return normalizeDirection(angle);
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

    public static float normalizeDirection(float direction) {
        if (direction > -Math.PI && direction < Math.PI) {
            return direction;
        }
        if (direction < -Math.PI) {
            direction += 2*Math.PI;
            direction = normalizeDirection(direction);
        }else if (direction > Math.PI) {
            direction -= 2*Math.PI;
            direction = normalizeDirection(direction);
        }
        return direction;
    }



}
