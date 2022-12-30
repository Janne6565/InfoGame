package lethalhabit.math;

public record Vec2D(double x, double y) implements TwoDimensional {
    
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
