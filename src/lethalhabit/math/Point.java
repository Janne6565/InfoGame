package lethalhabit.math;

import java.lang.Comparable;

public record Point(double x, double y) implements TwoDimensional, Comparable<Point> {
    
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
    
    public Vec2D pos() {
        return new Vec2D(x, y);
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

    public Vec2D loc() {
        return new Vec2D(x, y);
    }

}
