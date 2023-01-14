package lethalhabit;

import lethalhabit.math.*;
import lethalhabit.math.Point;
import lethalhabit.ui.Drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public abstract class PhysicsObject extends Drawable implements Tickable {

    public Hitbox hitbox;
    public boolean collidable = true;
    public Vec2D velocity = new Vec2D(0, 0);

    public PhysicsObject(double width, String path, Point pos, Hitbox hitbox) {
        super(width, path, pos);
        this.hitbox = hitbox;
        Main.physicsObjects.add(this);
    }

    @Override
    public void tick(Double timeDelta) {
        if (!isWallDown()) {
            velocity = velocity.plus(0, 400 * timeDelta);
        } else {
            velocity = new Vec2D(velocity.x(), Math.min(velocity.y(), 0));
            onGroundReset();
        }

        if (isWallLeft()) {
            velocity = new Vec2D(Math.max(0, velocity.x()), velocity.y());
        }

        if (isWallRight()) {
            velocity = new Vec2D(Math.min(0, velocity.x()), velocity.y());
        }

        if (isWallUp()) {
            velocity = new Vec2D(velocity.x(), Math.max(0, velocity.y()));
        }
        moveX(timeDelta, velocity);
        moveY(timeDelta, velocity);
    }

    public void moveX(double timeDelta, Vec2D generalVelocity) {
        Vec2D vel = new Vec2D(generalVelocity.x(), 0);

        ArrayList<Hitbox> collidables = Main.getPossibleCollisions(hitbox.shiftAll(position), vel, timeDelta);
        Double minTime = getFirstIntersection(hitbox.shiftAll(position), collidables, vel, false);

        Double timeWeTake = Double.valueOf(timeDelta);
        Double distance = generalVelocity.x() * minTime;
        Double safeDistance = 1.0;
        Double timeWeNeed = safeDistance / vel.x();

        if (!Double.isNaN(minTime)) {
            if (minTime <= timeDelta) {
                timeWeTake = minTime - timeWeNeed;
            }
        }
        position = position.plus(vel.scale(timeWeTake));
    }

    public void moveY(double timeDelta, Vec2D generalVelocity) {
        Vec2D vel = new Vec2D(0, generalVelocity.y());

        ArrayList<Hitbox> collidables = Main.getPossibleCollisions(hitbox.shiftAll(position), vel, timeDelta);
        Double minTime = getFirstIntersection(hitbox.shiftAll(position), collidables, vel, false);

        Double timeWeTake = Double.valueOf(timeDelta);
        Double distance = vel.y() * minTime;
        Double safeDistance = 1.0;
        Double timeWeNeed = safeDistance / vel.y();

        if (!Double.isNaN(minTime)) {
            if (minTime <= timeDelta) {
                timeWeTake = minTime - timeWeNeed;
            }
        }

        position = position.plus(vel.scale(timeWeTake));
    }


    public ArrayList<Hitbox> drawnHitboxes = new ArrayList<>();
    private Double getFirstIntersection(Hitbox hitbox, ArrayList<Hitbox> collidables, Vec2D direction, boolean debug) {
        Double minTime = Double.NaN;
        for (Hitbox collidable : collidables) {
            for (LineSegment edge : collidable.edges()) {
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

            for (LineSegment edge : hitbox.edges()) {
                for (LineSegment edgeCollidingFor : collidable.edges()) {
                    Double newTd = minimumFactorUntilIntersection(edge, direction, edgeCollidingFor, debug);
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
        Double td = getFirstIntersection(hitbox.shiftAll(super.position), Main.getPossibleCollisions(hitbox.shiftAll(position), new Vec2D(0, 1), 1), new Vec2D(0, 1), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td < Main.collisionThreshold);
    }


    public boolean isWallRight() {
        Double td = getFirstIntersection(hitbox.shiftAll(super.position), Main.getPossibleCollisions(hitbox.shiftAll(position), new Vec2D(1, 0), 1), new Vec2D(1, 0), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td < Main.collisionThreshold);
    }

    public boolean isWallLeft() {
        Double td = getFirstIntersection(hitbox.shiftAll(super.position), Main.getPossibleCollisions(hitbox.shiftAll(position), new Vec2D(-1, 0), 1), new Vec2D(-1, 0), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td < Main.collisionThreshold);
    }

    public boolean isWallUp() {
        Double td = getFirstIntersection(hitbox.shiftAll(super.position), Main.getPossibleCollisions(hitbox.shiftAll(position), new Vec2D(0, -1), 1), new Vec2D(0, -1), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td < Main.collisionThreshold);
    }
}