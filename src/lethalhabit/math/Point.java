package lethalhabit.math;

public record Point(double x, double y) implements TwoDimensional {
    
    public boolean greaterThan(Point other) {
        return this.x > other.x && this.y > other.y;
    }
    
    public boolean lessThan(Point other) {
        return this.x < other.x && this.y < other.y;
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
    
}
