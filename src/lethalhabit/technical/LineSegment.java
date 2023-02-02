package lethalhabit.technical;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public record LineSegment(Point a, Point b) implements Iterable<Point> {
    
    public double length() {
        return b.minus(a).pos().length();
    }
    
    public double maxX() {
        return Math.max(a.x(), b.x());
    }
    
    public double minX() {
        return Math.min(a.x(), b.x());
    }
    
    public double maxY() {
        return Math.max(a.y(), b.y());
    }
    
    public double minY() {
        return Math.min(a.y(), b.y());
    }
    
    public LineSegment minus(TwoDimensional other) {
        return new LineSegment(a.minus(other), b.minus(other));
    }
    
    public LineSegment plus(TwoDimensional other) {
        return new LineSegment(a.plus(other), b.plus(other));
    }
    
    @Override
    public void forEach(Consumer<? super Point> action) {
        List.of(a, b).forEach(action);
    }
    
    @Override
    public Spliterator<Point> spliterator() {
        return List.of(a, b).spliterator();
    }
    
    @Override
    public Iterator<Point> iterator() {
        return List.of(a, b).iterator();
    }
    
    public LineSegment plus(double x, double y) {
        return plus(new Point(x, y));
    }
    
    public LineSegment minus(double x, double y) {
        return minus(new Point(x, y));
    }

    public LineSegment scale(double scale) {
        return new LineSegment(a.scale(scale), b.scale(scale));
    }

    public LineSegment divide(double div) {
        return new LineSegment(a.divide(div), b.divide(div));
    }
    
    public boolean intersects(LineSegment other) {
        double r = ((other.a.x() - a.x()) * (other.a.y() - other.b.y()) - (other.a.y() - a.y()) * (other.a.x() - other.b.x())) / ((b.x() - a.x()) * (other.a.y() - other.b.y()) - (b.y() - a.y()) * (other.a.x() - other.b.x()));
        if (!Double.isFinite(r)) {
            return false;
        }
        Vec2D solution = a.loc().plus(b.minus(a).scale(r));
        return solution.x() >= minX() && solution.x() <= maxX() && solution.y() >= minY() && solution.y() <= maxY()
               && solution.x() >= other.minX() && solution.x() <= other.maxX() && solution.y() >= other.minY() && solution.y() <= other.maxY();
    }
    
}
