package lethalhabit;

import lethalhabit.math.*;
import lethalhabit.math.Point;
import lethalhabit.ui.Drawable;

import java.util.Date;

public abstract class PhysicsObject extends Drawable implements Tickable {
    
    public Hitbox hitbox;
    public boolean collidable = true;
    public Vec2D velocity = new Vec2D(0, 0);

    public PhysicsObject(float width, String path, Point pos, Hitbox hitbox) {
        super(width, path, pos);
        this.hitbox = hitbox;
        Main.physicsObjects.add(this);
    }

    @Override
    public void tick(float timeDelta) {
        if (!isWallDown()) {
            velocity = velocity.plus(0, 400 * timeDelta);
        } else {
            velocity = new Vec2D(velocity.x(), Math.min(velocity.y(), 0));
            onGroundReset();
        }

        if (isWallLeft()) {
            velocity = new Vec2D(Math.max(velocity.x(), 0), velocity.y());
        }

        if (isWallRight()) {
            velocity = new Vec2D(Math.min(velocity.x(), 0), velocity.y());
        }

        if (isFloorTop()) {
            velocity = new Vec2D(velocity.x(), Math.max(velocity.y(), 0));
        }


        move(timeDelta);
    }

    public void move(float timeDelta) {
        Collidable[] possibleCollisions = Main.getPossibleCollisions(this, velocity, timeDelta);
        Date date = new Date();
        double minTimeDelta = getFirstIntersection(hitbox.shiftAll(super.position), possibleCollisions, velocity, false);
        double min = timeDelta;


        double speed = Math.sqrt(velocity.x() * velocity.x() + velocity.y() * velocity.y());
        Vec2D velocityGrounded = velocity.scale(1 / speed);

        Point pointColliding = position.plus(velocityGrounded.scale(speed).scale(timeDelta));



        if (!Double.isNaN(minTimeDelta)) {
            if (minTimeDelta > 0) {
                if (minTimeDelta <= timeDelta) {
                    min = (minTimeDelta * 0.9);
                }
                if (min <= 0.001) {
                    min = 0;
                }
            }

        }
        super.position = super.position.plus(velocity.x() * min, velocity.y() * min);
    }

    private Double getFirstIntersection(Hitbox hitbox, Collidable[] collidables, Vec2D direction, boolean debug) {
        Double minTime = Double.NaN;
        for (Collidable collidable : collidables) {
            Point maxPositionCollidable = new Point(collidable.hitbox.shiftAll(collidable.position).maxX(), collidable.hitbox.shiftAll(collidable.position).maxY());
            Point minPositionCollidable = new Point(collidable.hitbox.shiftAll(collidable.position).minX(), collidable.hitbox.shiftAll(collidable.position).minY());

            Point maxPositionSelf = new Point(hitbox.maxX() + velocity.x(), hitbox.maxY() + velocity.y());
            Point minPositionSelf = new Point(hitbox.minX(), hitbox.minY());

            for (LineSegment edge : hitbox.edges()) {
                for (LineSegment edgeCollidingFor : collidable.getHitbox().edges()) {
                    Double newTd = minimumFactorUntilIntersection(edge, direction, edgeCollidingFor, debug);
                    if (newTd != null && Double.isFinite(newTd) && !Double.isNaN(newTd)) {
                        if (Double.isNaN(minTime)) {
                            minTime = newTd;
                        }
                        minTime = Math.min(newTd, minTime);
                    }
                }
            }

            for (LineSegment edge : collidable.getHitbox().edges()) {
                for (LineSegment edgeCollidingFor : hitbox.edges()) {
                    Double newTd = minimumFactorUntilIntersection(edge, direction.scale(-1), edgeCollidingFor, debug);
                    if (newTd != null && Double.isFinite(newTd) && !Double.isNaN(newTd)) {
                        if (Double.isNaN(minTime)) {
                            minTime = newTd;
                        }
                        minTime = Math.min(newTd, minTime);
                    }
                }
            }
        }
        return minTime;
    }

    private Double minimumFactorUntilIntersection(LineSegment s1, Vec2D direction, LineSegment s2, boolean debug) {
        Double min = null;
        for (Point p : s1) {
            double n = factorUntilIntersection(p, direction, s2, debug);
            if (n >= 0 && (min == null || n < min)) {
                Point solution = p.plus(direction.scale(n));
                if (solution.x() >= s2.minX() && solution.x() <= s2.maxX() && solution.y() >= s2.minY() && solution.y() <= s2.maxY()) {
                    min = n;
                }
            }
        }

        for (Point p : s2) {
            double n = factorUntilIntersection(p, direction.scale(-1), s1, debug);
            if (n >= 0 && (min == null || n < min)) {
                Point solution = p.plus(direction.scale(-n));
                if (solution.x() >= s1.minX() && solution.x() <= s1.maxX() && solution.y() >= s1.minY() && solution.y() <= s1.maxY()) {
                    min = n;
                }
            }
        }
        return min;
    }


    private Double factorUntilIntersection(Point point, Vec2D direction, LineSegment lineSegment, boolean debug) {
        Vec2D p = point.loc();
        Vec2D a = lineSegment.a().loc();
        Vec2D b = lineSegment.b().loc();
        Double answer = ((b.x() - a.x()) * (a.y() - p.y()) - (b.y() - a.y()) * (a.x() - p.x())) / (direction.y() * (b.x() - a.x()) - direction.x() * (b.y() - a.y()));
        if (debug && Double.isFinite(answer)) {
            System.out.println("Point: " + point.x() + " " + point.y() + " Direction " + direction.x() + " " + direction.y() + " Line: A:" + lineSegment.a().x() + " " + lineSegment.a().y() + " B: " + lineSegment.b().x() + " " + lineSegment.b().y() + " Answer: " + answer);
        }
        if (Double.isInfinite(answer)) {
            return Double.NaN;
        }
        return answer;
    }

    abstract void onGroundReset();

    public boolean isWallDown() {
        Double td = getFirstIntersection(hitbox.shiftAll(super.position), Main.getPossibleCollisions(this, new Vec2D(0, 1), 1), new Vec2D(0, 1), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td < Main.collisionThreshold * 100000);
    }


    public boolean isWallRight() {
        Double td = getFirstIntersection(hitbox.shiftAll(super.position), Main.getPossibleCollisions(this, new Vec2D(1, 0), 1), new Vec2D(1, 0), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td < Main.collisionThreshold * 100000);
    }

    public boolean isWallLeft() {
        Double td = getFirstIntersection(hitbox.shiftAll(super.position), Main.getPossibleCollisions(this, new Vec2D(-1, 0), 1), new Vec2D(-1, 0), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td < Main.collisionThreshold * 100000);
    }

    public boolean isFloorTop() {
        Double td = getFirstIntersection(hitbox.shiftAll(super.position), Main.getPossibleCollisions(this, new Vec2D(0, -1), 1), new Vec2D(0, -1), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td < Main.collisionThreshold * 100000);
    }
}