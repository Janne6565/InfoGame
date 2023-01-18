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
    
}
