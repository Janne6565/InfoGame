package lethalhabit.technical;

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
    
    @Override
    public String toString() {
        return "(" + x + "|" + y + ")";
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
    
    public Point scale(double factor) {
        return new Point(this.x * factor, this.y * factor);
    }
    
    public Vec2D loc() {
        return new Vec2D(x, y);
    }
    
    public double distance(Point point) {
        return minus(point).loc().length();
    }
    
}
