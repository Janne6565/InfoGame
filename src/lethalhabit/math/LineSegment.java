package lethalhabit.math;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Line segment from one {@link Point} to another
 *
 * @param a Point A (from)
 * @param b Point B (to)
 */
public record LineSegment(Point a, Point b) implements Iterable<Point> {
    
    /**
     * @return Absolute length of the line segment
     */
    public double length() {
        return b.minus(a).loc().length();
    }
    
    /**
     * @return Maximum x position of the line segment
     */
    public double maxX() {
        return Math.max(a.x(), b.x());
    }
    
    /**
     * @return Minimum x position of the line segment
     */
    public double minX() {
        return Math.min(a.x(), b.x());
    }
    
    /**
     * @return Maximum y position of the line segment
     */
    public double maxY() {
        return Math.max(a.y(), b.y());
    }
    
    /**
     * @return Minimum y position of the line segment
     */
    public double minY() {
        return Math.min(a.y(), b.y());
    }
    
    /**
     * Subtracts a two-dimensional object from the line segment
     *
     * @param other two-dimensional offset to be subtracted ({@link Point} or {@link Vec2D})
     * @return New line segment, offset by the specified value
     */
    public LineSegment minus(TwoDimensional other) {
        return new LineSegment(a.minus(other), b.minus(other));
    }
    
    /**
     * Adds a two-dimensional object to the line segment
     *
     * @param other two-dimensional offset to be added ({@link Point} or {@link Vec2D})
     * @return New line segment, offset by the specified value
     */
    public LineSegment plus(TwoDimensional other) {
        return new LineSegment(a.plus(other), b.plus(other));
    }
    
    /**
     * Performs the given action for both points of the line segment
     *
     * @param action The action to be performed
     * @see List#forEach(Consumer)
     */
    @Override
    public void forEach(Consumer<? super Point> action) {
        List.of(a, b).forEach(action);
    }
    
    /**
     * Creates a {@link Spliterator} from the two points of the line segment
     *
     * @return A {@link Spliterator} over the two points of the line segment
     * @see List#spliterator()
     */
    @Override
    public Spliterator<Point> spliterator() {
        return List.of(a, b).spliterator();
    }
    
    /**
     * Creates an {@link Iterator} from the two points of the line segment
     *
     * @return An {@link Iterator} over the two points of the line segment
     * @see List#iterator()
     */
    @Override
    public Iterator<Point> iterator() {
        return List.of(a, b).iterator();
    }
    
    /**
     * Adds x and y offsets to the line segment
     *
     * @param x X offset to be added
     * @param y Y offset to be added
     * @return New line segment, offset by the specified values
     */
    public LineSegment plus(double x, double y) {
        return plus(new Point(x, y));
    }
    
    /**
     * Subtracts x and y offsets from the line segment
     *
     * @param x X offset to be subtracted
     * @param y Y offset to be subtracted
     * @return New line segment, offset by the specified values
     */
    public LineSegment minus(double x, double y) {
        return minus(new Point(x, y));
    }
    
    /**
     * Checks whether the line segment intersects another
     *
     * @param other Line segment to check
     * @return <code>true</code> if the two line segments intersect, <code>false</code> otherwise
     */
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
