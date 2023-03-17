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
    
    /**
     * @return The absolute length of the vector
     */
    public double length() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
    
    /**
     * Adds x and y offsets to the vector
     *
     * @param x X offset to be added
     * @param y Y offset to be added
     * @return New vector, offset by the specified values
     */
    public Vec2D plus(double x, double y) {
        return new Vec2D(this.x + x, this.y + y);
    }
    
    /**
     * Subtracts x and y offsets from the vector
     *
     * @param x X offset to be subtracted
     * @param y Y offset to be subtracted
     * @return New vector, offset by the specified values
     */
    public Vec2D minus(double x, double y) {
        return new Vec2D(this.x - x, this.y - y);
    }
    
    /**
     * Adds a two-dimensional object to the vector
     *
     * @param other two-dimensional offset to be added ({@link Point} or {@link Vec2D})
     * @return New vector, offset by the specified value
     */
    public Vec2D plus(TwoDimensional other) {
        return new Vec2D(x + other.x(), y + other.y());
    }
    
    /**
     * Subtracts a two-dimensional object from the vector
     *
     * @param other two-dimensional offset to be subtracted ({@link Point} or {@link Vec2D})
     * @return New vector, offset by the specified value
     */
    public Vec2D minus(TwoDimensional other) {
        return new Vec2D(x - other.x(), y - other.y());
    }
    
    /**
     * Scales the vector by a factor, essentially elongating it (factor > 1) or shortening it (factor < 1)
     *
     * @param factor Factor to scale the vector by
     * @return A new vector, scaled by the specified factor
     */
    public Vec2D scale(double factor) {
        return new Vec2D(factor * x, factor * y);
    }
    
}
