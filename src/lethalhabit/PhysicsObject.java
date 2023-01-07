package lethalhabit;

import lethalhabit.math.*;
import lethalhabit.math.Point;
import lethalhabit.ui.Drawable;

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
        if (!onGround()) {
            velocity = velocity.plus(0, 100 * timeDelta);
        } else {
            velocity = new Vec2D(velocity.x(), Math.min(velocity.y(), 0));
            onGroundReset();
        }
        move(timeDelta);
    }

    public void move(float timeDelta) {
        Collidable[] possibleCollisions = Main.getPossibleCollisions(this, velocity);
        double minTimeDelta = getFirstIntersection(hitbox.shiftAll(super.position), possibleCollisions, velocity, false);
        float min = timeDelta;
        if (!Double.isNaN(minTimeDelta)) {
            if (minTimeDelta >= 0) {
                if (minTimeDelta <= timeDelta) {
                    while (minTimeDelta - Main.collisionThreshold <= 0) {
                        minTimeDelta += 0.001;
                    }
                    min = (float) (minTimeDelta - Main.collisionThreshold);
                }
            }

        }
        super.position = super.position.plus(velocity.x() * min, velocity.y() * min);
    }

    private Double getFirstIntersection(Hitbox hitbox, Collidable[] collidables, Vec2D direction, boolean debug) {
        Double minTime = Double.NaN;
        for (Collidable collidable : collidables) {
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
        if (Double.isInfinite(answer)) {
            if (debug) {
                System.out.println("Point: " + point.x() + " " + point.y() + " Direction " + direction.x() + " " + direction.y() + " Line: A:" + lineSegment.a().x() + " " + lineSegment.a().y() + " B: " + lineSegment.b().x() + " " + lineSegment.b().y() + " Answer: " + answer);
            }
            return Double.NaN;
        }
        return answer;
    }

    abstract void onGroundReset();

    public boolean onGround() {
        Double td = getFirstIntersection(hitbox.shiftAll(super.position), Main.getPossibleCollisions(this, new Vec2D(0, 1)), new Vec2D(0, 1), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td < Main.collisionThreshold * 10000);
    }
}