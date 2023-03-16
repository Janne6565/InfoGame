package lethalhabit.math;

import java.util.List;

/**
 * Two-dimensional point with an x and y value
 *
 * @param x x coordinate of the point
 * @param y y coordinate of the point
 */
public record Point(double x, double y) implements TwoDimensional, Comparable<Point> {
    
    /**
     * Position of spawn (hardcoded to fit onto start map)
     */
    public static Point SPAWN = new Point(8430, 750);
    
    /**
     * Position of Lava Falls (hardcoded to fit onto start map)
     */
    public static Point LAVA_FALLS = new Point(500, 1700);
    
    public static List<Point> spawnPointsForMobs = List.of(
            new Point(8648, 670),
            new Point(8743, 750),
            new Point(8580, 530),
            new Point(8453, 410),
            new Point(8127, 290),
            new Point(7958, 350),
            new Point(8161, 770),
            new Point(8435, 850),
            new Point(8717, 950),
            new Point(9087, 1110),
            new Point(9226, 1070),
            new Point(9259, 610),
            new Point(8535, 1690),
            new Point(8462, 1570),
            new Point(8417, 2810),
            new Point(8641, 2410),
            new Point(9092, 1110)
    );
    
    /**
     * Compares the point to another one
     *
     * @param other Point to be compared
     * @return <code>1</code> if both coordinates of this point are greater,
     * <code>-1</code> if both coordinates of this point are smaller,
     * or <code>0</code> if one of the two coordinates (x or y) are the same in both points
     */
    @Override
    public int compareTo(Point other) {
        if (this.x > other.x && this.y > other.y) {
            return 1;
        } else if (this.x < other.x && this.y < other.y) {
            return -1;
        } else {
            return 0;
        }
    }
    
    /**
     * Creates a string representation of the point
     *
     * @return The point, represented by a string, in conventional mathematical notation (X|Y)
     */
    @Override
    public String toString() {
        return "(" + x + "|" + y + ")";
    }
    
    public Point plus(double x, double y) {
        return new Point(this.x + x, this.y + y);
    }
    
    public Point minus(double x, double y) {
        return new Point(this.x - x, this.y - y);
    }
    
    public Point plus(TwoDimensional other) {
        return new Point(this.x + other.x(), this.y + other.y());
    }
    
    public Point minus(TwoDimensional other) {
        return new Point(this.x - other.x(), this.y - other.y());
    }
    
    public Point scale(double factor) {
        return new Point(this.x * factor, this.y * factor);
    }
    
    public Point divide(double divider) {
        return new Point(this.x / divider, this.y / divider);
    }
    
    /**
     * Rounds (floors) the point to the nearest integer x and y coordinates
     *
     * @return A new point with floored coordinates
     */
    public Point round() {
        return new Point((int) this.x, (int) this.y);
    }
    
    /**
     * Returns the 'location vector' of the point (vector pointing from coordinate origin (0|0) to the point)
     *
     * @return A new {@link Vec2D} with the point's x and y coordinate as its direction
     */
    public Vec2D loc() {
        return new Vec2D(x, y);
    }
    
    /**
     * Calculates the distance to another point
     *
     * @param point Other point
     * @return The absolute distance from the point to the other one
     */
    public double distance(Point point) {
        return minus(point).loc().length();
    }
    
}
