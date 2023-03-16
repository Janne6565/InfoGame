package lethalhabit.math;

import lethalhabit.Main;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A polygonal hitbox that is defined by a set of two-dimensional points that represent its vertices.
 */
public final class Hitbox implements Iterable<Point> {
    
    public static final Hitbox TEST_HITBOX = new Hitbox(
            new Point(10, 10),
            new Point(10, -10),
            new Point(-10, -10),
            new Point(-10, 10)
    );
    
    /**
     * Ordered set of vertices (corner points) of the hitbox <br>
     * The order of the vertices defines where the hitbox's edges lie
     */
    public final Point[] vertices;
    
    /**
     * Point with the hitbox's maximum x and y position <br>
     * If a rectangle were drawn around the hitbox, its bottom right vertex would be this point
     * @see Hitbox#maxX()
     * @see Hitbox#maxY()
     */
    public final Point maxPosition;
    
    /**
     * Point with the hitbox's maximum x and y position <br>
     * If a rectangle were drawn around the hitbox, its top left vertex would be this point
     * @see Hitbox#minX()
     * @see Hitbox#minY()
     */
    public final Point minPosition;
    
    /**
     * Constructs a new hitbox from a set of vertices
     *
     * @param vertices Vertices/Corners of the hitbox
     */
    public Hitbox(Point... vertices) {
        this.vertices = vertices;
        maxPosition = new Point(maxX(), maxY());
        minPosition = new Point(minX(), minY());
    }
    
    /**
     * Calculates the edges of the hitbox by drawing lines from each vertex to the next one
     *
     * @return The outer edges of the hitbox
     */
    public LineSegment[] edges() {
        LineSegment[] edges = new LineSegment[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            int j = (i + 1) % vertices.length;
            edges[i] = new LineSegment(vertices[i], vertices[j]);
        }
        return edges;
    }
    
    /**
     * @return The x coordinate of the hitbox's rightmost point
     */
    public double maxX() {
        return Arrays.stream(vertices).mapToDouble(Point::x).max().orElse(0);
    }
    
    /**
     * @return The x coordinate of the hitbox's leftmost point
     */
    public double minX() {
        return Arrays.stream(vertices).mapToDouble(Point::x).min().orElse(0);
    }
    
    /**
     * @return The y coordinate of the hitbox's lowermost point
     */
    public double maxY() {
        return Arrays.stream(vertices).mapToDouble(Point::y).max().orElse(0);
    }
    
    /**
     * @return The y coordinate of the hitbox's uppermost point
     */
    public double minY() {
        return Arrays.stream(vertices).mapToDouble(Point::y).min().orElse(0);
    }
    
    /**
     * Offsets the hitbox in two dimensions
     *
     * @param offset Offset to move the hitbox by
     * @return A new hitbox, shifted by the given offset
     */
    public Hitbox shift(TwoDimensional offset) {
        Point[] newVertices = new Point[vertices.length];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i] = new Point(vertices[i].x() + offset.x(), vertices[i].y() + offset.y());
        }
        return new Hitbox(newVertices);
    }
    
    /**
     * Offsets the hitbox in two dimensions
     *
     * @param x X offset to move the hitbox by
     * @param y Y offset to move the hitbox by
     * @return A new hitbox, shifted by the given offsets
     */
    public Hitbox shift(double x, double y) {
        return shift(new Point(x, y));
    }
    
    /**
     * Performs the given action for every vertex of the hitbox
     *
     * @param action The action to be performed
     * @see Stream#forEach(Consumer)
     */
    @Override
    public void forEach(Consumer<? super Point> action) {
        Arrays.stream(vertices).forEach(action);
    }
    
    /**
     * Creates a {@link Spliterator} from the vertices of the hitbox
     *
     * @return A {@link Spliterator} over the vertex points of the hitbox
     * @see Stream#spliterator()
     */
    @Override
    public Spliterator<Point> spliterator() {
        return Arrays.stream(vertices).spliterator();
    }
    
    /**
     * Creates an {@link Iterator} from the vertices of the hitbox
     *
     * @return An {@link Iterator} over the vertex points of the hitbox
     * @see Stream#iterator()
     */
    @Override
    public Iterator<Point> iterator() {
        return Arrays.stream(vertices).iterator();
    }
    
    /**
     * @return The vertices of the hitbox
     */
    public Point[] vertices() {
        return vertices;
    }
    
    /**
     * Checks equality between the hitbox and another object
     *
     * @param obj The object to be compared
     * @return <code>true</code> if <code>obj</code> is a hitbox and both sets of vertices are equal, <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Hitbox other && Arrays.equals(this.vertices, other.vertices);
    }
    
    /**
     * @return A hash of the vertex array
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(vertices);
    }
    
    /**
     * @return A string representation of the hitbox including string representations of all vertices
     */
    @Override
    public String toString() {
        return "Hitbox[vertices=" + Arrays.toString(vertices) + ']';
    }
    
    /**
     * Checks whether the hitbox is fully contained in another one <br>
     * WARNING: Only works for orthogonal rectangular hitboxes
     *
     * @param hitbox Hitbox to check
     * @return <code>true</code> if other hitbox is contained in this hitbox, <code>false</code> otherwise
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
     * Checks whether the hitbox intersects with another one by casting rays from each point
     * and counting their intersections with the other hitbox. <br>
     * For even amounts of intersections the point is not contained in the hitbox,
     * for odd amounts the point is contained. <br>
     * If any point of the two hitboxes is contained in the other one, the hitboxes are defined as intersecting.
     *
     * @param other Hitbox to check
     * @return <code>true</code> if the hitboxes intersect, <code>false</code> otherwise
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
    
    /**
     * @return The total size of the hitbox (width between leftmost and rightmost points; height between uppermost and lowermost points)
     */
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