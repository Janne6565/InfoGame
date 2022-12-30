package lethalhabit.math;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public record Hitbox(Point[] vertices) implements Iterable<Point> {
    
    public LineSegment[] edges() {
        LineSegment[] edges = new LineSegment[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            int j = (i + 1) % vertices.length;
            edges[i] = new LineSegment(vertices[i], vertices[j]);
        }
        return edges;
    }
    
    public double maxX() {
        return Arrays.stream(vertices).mapToDouble(Point::x).max().orElseThrow();
    }
    
    public double minX() {
        return Arrays.stream(vertices).mapToDouble(Point::x).min().orElseThrow();
    }
    
    public double maxY() {
        return Arrays.stream(vertices).mapToDouble(Point::y).max().orElseThrow();
    }
    
    public double minY() {
        return Arrays.stream(vertices).mapToDouble(Point::y).min().orElseThrow();
    }

    public Hitbox shiftAll(Point shiftFor) {
        Point[] newVertices = new Point[vertices.length];
        for (int i = 0; i < newVertices.length; i ++) {
            newVertices[i] = new Point(vertices[i].x() + shiftFor.x(), vertices[i].y() + shiftFor.y());
        }
        return new Hitbox(newVertices);
    }

    @Override
    public void forEach(Consumer<? super Point> action) {
        Arrays.stream(vertices).forEach(action);
    }
    
    @Override
    public Spliterator<Point> spliterator() {
        return Arrays.stream(vertices).spliterator();
    }
    
    @Override
    public Iterator<Point> iterator() {
        return Arrays.stream(vertices).iterator();
    }
}