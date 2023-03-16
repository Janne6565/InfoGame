package lethalhabit.math;

/**
 * Two-dimensional vector with an x and y direction
 *
 * @param x X direction of the vector
 * @param y Y direction of the vector
 */
public record Vec2D(double x, double y) implements TwoDimensional {
    
    /**
     * Clones another vector
     * @param other Vector to clone
     */
    public Vec2D(TwoDimensional other) {
        this(other.x(), other.y());
    }
    
    /**
     * Creates a string representation of the vector
     *
     * @return The vector, represented by a string, in semi-mathematical notation < X,Y >
     */
    @Override
    public String toString() {
        return "<" + x + "," + y + ">";
    }
    
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
