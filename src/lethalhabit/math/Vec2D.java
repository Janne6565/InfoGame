package lethalhabit.math;

/**
 * @desc create a vector from x and y
 */

public record Vec2D(double x, double y) implements TwoDimensional {

    /**
     *
     * @desc set this vector to another vector
     */
    public Vec2D(TwoDimensional other) {
        this(other.x(), other.y());
    }
    
    @Override

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    /**
     * @desc tools for vector manipulation
     */
    public double length() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
    
    public Vec2D plus(double x, double y) {
        return new Vec2D(this.x + x, this.y + y);
    }
    
    public Vec2D minus(double x, double y) {
        return new Vec2D(this.x - x, this.y - y);
    }
    
    public Vec2D plus(TwoDimensional other) {
        return new Vec2D(x + other.x(), y + other.y());
    }
    
    public Vec2D minus(TwoDimensional other) {
        return new Vec2D(x - other.x(), y - other.y());
    }
    
    public Vec2D scale(double n) {
        return new Vec2D(n * x, n * y);
    }
    
}
