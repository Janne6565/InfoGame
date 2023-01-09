package lethalhabit.math;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public final class Hitbox implements Iterable<Point> {
    public final Point[] vertices;

    public Point maxPosition;
    public Point minPosition;

    public Hitbox(Point[] vertices) {
        this.vertices = vertices;
        calculateMaxMin();
    }

    public void calculateMaxMin() {
        maxPosition = new Point(maxX(), maxY());
        minPosition = new Point(minX(), minY());
    }

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
        for (int i = 0; i < newVertices.length; i++) {
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

    public Point[] vertices() {
        return vertices;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Hitbox) obj;
        return Objects.equals(this.vertices, that.vertices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices);
    }

    @Override
    public String toString() {
        return "Hitbox[" +
                "vertices=" + Arrays.toString(vertices) + ']';
    }


    public boolean liesIn(Hitbox hitbox) {
        for (Point point : hitbox.vertices) {
            if (point.compareTo(minPosition) > 0 && point.compareTo(maxPosition) < 0) {
                return true;
            }
        }

        if (hitbox.minPosition.x() < minPosition.x() && hitbox.maxPosition.x() > maxPosition.x()) {
            return true;
        }

        if (hitbox.minPosition.y() < minPosition.y() && hitbox.maxPosition.y() > maxPosition.y()) {
            return true;
        }

        return false;
    }
}