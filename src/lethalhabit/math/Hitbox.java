package lethalhabit.math;

import lethalhabit.Main;

import java.awt.*;
import java.util.*;
import java.util.function.Consumer;


public final class Hitbox implements Iterable<Point> {

    public static final Hitbox TEST_HITBOX = new Hitbox(
            new Point(10, 10),
            new Point(10, -10),
            new Point(-10, -10),
            new Point(-10, 10)
    );

    /**
     * array of points to describe a vertex
     */
    public final Point[] vertices;
    
    public final Point maxPosition;
    public final Point minPosition;
    
    /**
     * create a hitbox, with vertices and set max and min Points
     *
     * @param vertices vertex made of Point arrays
     */
    public Hitbox(Point... vertices) {
        this.vertices = vertices;
        maxPosition = new Point(maxX(), maxY());
        minPosition = new Point(minX(), minY());
    }
    
    /**
     * get the edges of hitbox
     *
     * @return the edges of hitbox as a LineSegment[] type
     */
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
    
    /**
     * @param offset type TwoDimensional
     * @return a new hitbox shifted by the TwoDimensional
     */
    public Hitbox shift(TwoDimensional offset) {
        Point[] newVertices = new Point[vertices.length];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i] = new Point(vertices[i].x() + offset.x(), vertices[i].y() + offset.y());
        }
        return new Hitbox(newVertices);
    }
    
    public Hitbox shift(double x, double y) {
        return shift(new Point(x, y));
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
        return Arrays.hashCode(vertices);
    }
    
    @Override
    public String toString() {
        return "Hitbox[vertices=" + Arrays.toString(vertices) + ']';
    }
    
    /**
     * Only works for primitive square hitboxes, for precision, use intersects
     *
     * @param hitbox Hitbox to check
     * @return true if other hitbox is contained in this hitbox, false otherwise
     */
    public boolean isContained(Hitbox hitbox) {
        for (Point point : hitbox.vertices) {
            if (point.compareTo(minPosition) > 0 && point.compareTo(maxPosition) < 0) {
                return true;
            }
        }
        return hitbox.minPosition.x() < minPosition.x() && hitbox.maxPosition.x() > maxPosition.x()
                || hitbox.minPosition.y() < minPosition.y() && hitbox.maxPosition.y() > maxPosition.y();
        
    }
    
    /**
     * @param other Hitbox to check
     * @return true if other hitbox
     */
    public boolean intersects(Hitbox other) {
        for (Point point : this) {
            Set<Point> intersections = new HashSet<>();
            for (LineSegment segment : other.edges()) {
                double s = (point.y() - segment.a().y()) / (segment.b().y() - segment.a().y());
                Point intersection = segment.a().plus(segment.b().minus(segment.a()).scale(s));
                if (intersection.x() <= segment.maxX() && intersection.x() >= segment.minX() && intersection.y() <= segment.maxY() && intersection.y() >= segment.minY() && intersection.x() >= point.x()) {
                    intersections.add(intersection);
                }
            }
            if (intersections.size() % 2 == 1) {
                return true;
            }
        }
        for (Point point : other) {
            Set<Point> intersections = new HashSet<>();
            for (LineSegment segment : this.edges()) {
                double s = (point.y() - segment.a().y()) / (segment.b().y() - segment.a().y());
                Point intersection = segment.a().plus(segment.b().minus(segment.a()).scale(s));
                if (intersection.x() <= segment.maxX() && intersection.x() >= segment.minX() && intersection.y() <= segment.maxY() && intersection.y() >= segment.minY() && intersection.x() >= point.x()) {
                    intersections.add(intersection);
                }
            }
            if (intersections.size() % 2 == 1) {
                return true;
            }
        }
        return false;
    }
    
    public Dimension getSize() {
        return new Dimension((int) (maxX() - minX()), (int) (maxY() - minY()));
    }
    
    public enum Type {
        
        FULL(new Point[]{new Point(0, 0), new Point(Main.TILE_SIZE, 0), new Point(Main.TILE_SIZE, Main.TILE_SIZE), new Point(0, Main.TILE_SIZE)}),
        HALF_TOP(new Point[]{new Point(0, 0), new Point(Main.TILE_SIZE, 0), new Point(Main.TILE_SIZE, Main.TILE_SIZE / 2), new Point(0, Main.TILE_SIZE / 2)}),
        HALF_BOTTOM(new Point[]{new Point(0, Main.TILE_SIZE / 2), new Point(Main.TILE_SIZE, Main.TILE_SIZE / 2), new Point(Main.TILE_SIZE, Main.TILE_SIZE), new Point(0, Main.TILE_SIZE)}),
        LIQUID_SURFACE(new Point[]{new Point(0, Main.TILE_SIZE / 4), new Point(Main.TILE_SIZE, Main.TILE_SIZE / 4), new Point(Main.TILE_SIZE, Main.TILE_SIZE), new Point(0, Main.TILE_SIZE)}),
        SMALL_BLOCK(new Point[]{new Point(0, Main.TILE_SIZE / 4 * 3), new Point(Main.TILE_SIZE, Main.TILE_SIZE / 4 * 3), new Point(Main.TILE_SIZE, Main.TILE_SIZE / 4 * 3), new Point(Main.TILE_SIZE, Main.TILE_SIZE)}),
        NONE(new Point[0]);
        
        public final Hitbox hitbox;
        
        Type(Point[] vertices) {
            this.hitbox = new Hitbox(vertices);
        }
        
    }
    
}