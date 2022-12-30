import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Test {

    public static void main(String[] args) {
        distanceTest();
    }

    /**
     * Calculates the minimum factor that the direction vector has to be multiplied with, so that s1 (moved by the new direction vector) and s2 intersect.
     * @param s1        The moving line segment
     * @param direction The vector that s1 moves on
     * @param s2        The static line segment
     * @return The smallest factor n so that (s1 + n * direction) intersects s2, or null if there is no solution
     */
    public static Double minimumFactorUntilIntersection(LineSegment s1, Vec2D direction, LineSegment s2) {
        Double min = null;
        for (Point p : s1) {
            double n = factorUntilIntersection(p, direction, s2);
            if (n >= 0 && (min == null || n < min)) {
                Point solution = p.plus(direction.scale(n));
                if (solution.x >= s2.minX() && solution.x <= s2.maxX() && solution.y >= s2.minY() && solution.y <= s2.maxY()) {
                    min = n;
                }
            }
        }
        for (Point p : s2) {
            double n = factorUntilIntersection(p, direction.scale(-1), s1);
            if (n >= 0 && (min == null || n < min)) {
                Point solution = p.plus(direction.scale(-n));
                if (solution.x >= s1.minX() && solution.x <= s1.maxX() && solution.y >= s1.minY() && solution.y <= s1.maxY()) {
                    min = n;
                }
            }
        }
        return min;
    }

    public static void distanceTest() {
        // LineSegment ab = new LineSegment(new Point(0, -4), new Point(-1, 3));
        // LineSegment cd = new LineSegment(new Point(1, 0), new Point(2, 3));
        // Vec2D direction = new Vec2D(1, 1);
        //System.out.println("Solution: " + minimumFactorUntilIntersection(ab, direction, cd));
        double td = factorUntilIntersection(new Point(10, 90), new Vec2D(0, 1), new LineSegment(new Point(50, 50), new Point(50, -50)));
        System.out.println("Soultion: " + td);
    }

    /**
     * Calculates the minimum factor that the direction vector has to be multiplied with, so that the point (moved by the new direction vector) lies on the line that the segment is part of,
     * regardless of whether the point actually lies on the segment or just the line.
     * @param point       The moving point
     * @param direction   The vector that the point moves on
     * @param lineSegment The line segment to be intersected
     * @return The smallest factor n so that (point + n * direction) intersects the line that the line segment is part of, regardless if it lies on the actual segment.
     */
    private static double factorUntilIntersection(Point point, Vec2D direction, LineSegment lineSegment) {
        Vec2D p = point.loc();
        Vec2D a = lineSegment.a.loc();
        Vec2D b = lineSegment.b.loc();

        return ((b.x - a.x) * (a.y - p.y) - (b.y - a.y) * (a.x - p.x)) / (direction.y * (b.x - a.x) - direction.x * (b.y - a.y));
    }

    private static record LineSegment(Point a, Point b) implements Iterable<Point> {
        public double maxX() {
            return Math.max(a.x, b.x);
        }

        public double minX() {
            return Math.min(a.x, b.x);
        }

        public double maxY() {
            return Math.max(a.y, b.y);
        }

        public double minY() {
            return Math.min(a.y, b.y);
        }

        @Override
        public Iterator<Point> iterator() {
            return List.of(a, b).iterator();
        }

        @Override
        public void forEach(Consumer<? super Point> action) {
            List.of(a, b).forEach(action);
        }

        @Override
        public Spliterator<Point> spliterator() {
            return List.of(a, b).spliterator();
        }
    }

    private static record Point(double x, double y) implements TwoDimensional {
        public Vec2D loc() {
            return new Vec2D(x, y);
        }

        public Point plus(TwoDimensional other) {
            return new Point(x + other.getX(), y + other.getY());
        }

        public Point minus(TwoDimensional other) {
            return new Point(x - other.getX(), y - other.getY());
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }
    }

    private static record Vec2D(double x, double y) implements TwoDimensional {
        public double length() {
            return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        }

        public Vec2D plus(TwoDimensional other) {
            return new Vec2D(x + other.getX(), y + other.getY());
        }

        public Vec2D minus(TwoDimensional other) {
            return new Vec2D(x - other.getX(), y - other.getY());
        }

        public Vec2D scale(double n) {
            return new Vec2D(n * x, n * y);
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }
    }

    private interface TwoDimensional {
        double getX();

        double getY();
    }

}
