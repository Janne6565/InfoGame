package lethalhabit.math;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @param x
 * @param y
 */

public record Point(double x, double y) implements TwoDimensional, Comparable<Point> {
    
    /**
     * Position of Spawn (hardcoded to fit onto start map)
     */
    public static Point SPAWN = new Point(8430, 750);
    /**
     * Position of Lava Falls (hardcoded to fit onto start map)
     */
    public static Point LAVA_FALLS = new Point(500, 1700);

    public static ArrayList<Point> spawnPointsForMobs = new ArrayList<>(Arrays.asList(
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
    ));
    
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

    public Point toInt() {
        return new Point((int) this.x, (int) this.y);
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
