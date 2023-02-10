package lethalhabit.math;

import java.awt.*;

/**
 *
 * @param x
 * @param y
 */

public record Point(double x, double y) implements TwoDimensional, Comparable<Point> {
    
    /**
     * Position of Spawn (hardcoded to fit onto start map)
     */
    public static Point SPAWN = new Point(10000, 0);
    /**
     * Position of Lava Falls (hardcoded to fit onto start map)
     */
    public static Point LAVA_FALLS = new Point(500, 1700);

    /**
     *
     * @param other the object to be compared.
     * @return true, if point x and y of instance point is bigger than param
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


    @Override
    public String toString() {
        return "(" + x + "|" + y + ")";
    }


    /**
     *  @desc utils for point, addition, subtraction and scai
     *
     */

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

    public Point divide(double dividerX, double dividerY) {
        return new Point(this.x / dividerX, this.y / dividerY);
    }

    public Point divide(TwoDimensional twoDimensional) {
        return new Point(this.x / twoDimensional.x(), this.y / twoDimensional.y());
    }


    /**
     *
     * @return x and y of instance
     */
    public Vec2D loc() {
        return new Vec2D(x, y);
    }

    /**
     *
     * @param point
     * @return distance from instance point to param
     */

    public double distance(Point point) {
        return minus(point).loc().length();
    }
    
}
