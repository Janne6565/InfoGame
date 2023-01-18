package lethalhabit.technical;

import lethalhabit.Main;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public final class Hitbox implements Iterable<Point> {

    public static final Hitbox HITBOX_1x1 = new Hitbox(new Point[]{
            new Point(0, 0),
            new Point(0, Main.TILE_SIZE),
            new Point(Main.TILE_SIZE, Main.TILE_SIZE),
            new Point(Main.TILE_SIZE, 0)
    });

    public final Point[] vertices;

    public final Point maxPosition;
    public final Point minPosition;

    public Hitbox(Point[] vertices) {
        this.vertices = vertices;
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
        return Arrays.stream(vertices).mapToDouble(Point::x).max().orElse(0);
    }

    public double minX() {
        return Arrays.stream(vertices).mapToDouble(Point::x).min().orElse(0);
    }

    public double maxY() {
        return Arrays.stream(vertices).mapToDouble(Point::y).max().orElse(0);
    }

    public double minY() {
        return Arrays.stream(vertices).mapToDouble(Point::y).min().orElse(0);
    }

    public Hitbox shift(Point shiftFor) {
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
        return obj instanceof Hitbox other && Arrays.equals(this.vertices, other.vertices);
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

        return hitbox.minPosition.x() < minPosition.x() && hitbox.maxPosition.x() > maxPosition.x()
                || hitbox.minPosition.y() < minPosition.y() && hitbox.maxPosition.y() > maxPosition.y();

    }

    public Hitbox shift(double x, double y) {
        return shift(new Point(x, y));
    }

    public enum Type {

        FULL(new Point[]{new Point(0, 0), new Point(Main.TILE_SIZE, 0), new Point(Main.TILE_SIZE, Main.TILE_SIZE), new Point(0, Main.TILE_SIZE)}),
        HALF_TOP(new Point[]{new Point(0, 0), new Point(Main.TILE_SIZE, 0), new Point(Main.TILE_SIZE, Main.TILE_SIZE / 2), new Point(0, Main.TILE_SIZE / 2)}),
        HALF_BOTTOM(new Point[]{new Point(0, Main.TILE_SIZE / 2), new Point(Main.TILE_SIZE, Main.TILE_SIZE / 2), new Point(Main.TILE_SIZE, Main.TILE_SIZE), new Point(0, Main.TILE_SIZE)}),
        LIQUID_SURFACE(new Point[]{new Point(0, Main.TILE_SIZE / 4), new Point(Main.TILE_SIZE, Main.TILE_SIZE / 4), new Point(Main.TILE_SIZE, Main.TILE_SIZE), new Point(0, Main.TILE_SIZE)}),
        NONE(new Point[0]);

        public final Hitbox hitbox;

        Type(Point[] vertices) {
            this.hitbox = new Hitbox(vertices);
        }

    }

}