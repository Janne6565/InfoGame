package lethalhabit;

public record Position(double x, double y) {
    
    public boolean greaterThan(Position other) {
        return this.x > other.x && this.y > other.y;
    }
    
    public boolean lessThan(Position other) {
        return this.x < other.x && this.y < other.y;
    }
    
    public Position plus(double x, double y) {
        return new Position(this.x + x, this.y + y);
    }
    
    public Position minus(double x, double y) {
        return new Position(this.x - x, this.y - y);
    }
    
    public Position plus(Position other) {
        return new Position(this.x + other.x, this.y + other.y);
    }
    
    public Position minus(Position other) {
        return new Position(this.x - other.x, this.y - other.y);
    }
    
}
