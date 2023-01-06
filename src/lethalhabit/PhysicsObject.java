package lethalhabit;

import lethalhabit.math.*;
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
    
    public abstract double getSpeed();
    
    @Override
    public void tick(float timeDelta) {
        if (!onGround()) {
            velocity = velocity.plus(0, 100 * timeDelta);
        } else {
            velocity = new Vec2D(velocity.x(), Math.min(velocity.y(), 0));
        }
        move(timeDelta);
    }

    public void moveLeft() {
        this.velocity = new Vec2D(getSpeed() * -1, this.velocity.y());
    }

    public void moveRight() {
        this.velocity = new Vec2D(getSpeed(), this.velocity.y());
    }

    public void standStill() {
        this.velocity = new Vec2D(0, this.velocity.y());
    }

    private void move(float timeDelta) {
        Collidable[] possibleCollisions = Main.getPossibleCollisions(this, velocity);
        double minTimeDelta = getFirstIntersection(hitbox.shiftAll(super.position), possibleCollisions, velocity);
        float min = timeDelta;
        double threshold = 0.001;
        if (!Double.isNaN(minTimeDelta)) {
            if (minTimeDelta >= 0) {
                if (timeDelta >= minTimeDelta) {
                    min = threshold;
                }
            }
        }
        System.out.println(velocity.y());
        super.position = super.position.plus(velocity.x() * min, velocity.y() * min);
    }

    public void jump() {
        velocity = velocity.minus(0, 100);
    }

    public double getFirstIntersection(Hitbox hitbox, Collidable[] collidables, Vec2D direction) {
        double minTime = Double.NaN;
        for (Collidable collidable : collidables) {
            for (LineSegment edge : hitbox.edges()) {
                for (LineSegment edgeCollidingFor : collidable.getHitbox().edges()) {
                    Double newTd = minimumFactorUntilIntersection(edge, velocity, edgeCollidingFor);
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
                    Double newTd = minimumFactorUntilIntersection(edge, velocity, edgeCollidingFor);
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

    public Double minimumFactorUntilIntersection(LineSegment s1, Vec2D direction, LineSegment s2) {
        Double min = null;
        for (Point p : s1) {
            double n = factorUntilIntersection(p, direction, s2);
            if (n >= 0 && (min == null || n < min)) {
                Point solution = p.plus(direction.scale(n));
                if (solution.x() >= s2.minX() && solution.x() <= s2.maxX() && solution.y() >= s2.minY() && solution.y() <= s2.maxY()) {
                    min = n;
                }
            }
        }
        
        for (Point p : s2) {
            double n = factorUntilIntersection(p, direction.scale(-1), s1);
            if (n >= 0 && (min == null || n < min)) {
                Point solution = p.plus(direction.scale(-n));
                if (solution.x() >= s1.minX() && solution.x() <= s1.maxX() && solution.y() >= s1.minY() && solution.y() <= s1.maxY()) {
                    min = n;
                }
            }
        }
        return min;
    }


    public double factorUntilIntersection(Point point, Vec2D direction, LineSegment lineSegment) {
        Vec2D p = point.loc();
        Vec2D a = lineSegment.a().loc();
        Vec2D b = lineSegment.b().loc();
        double answer = ((b.x() - a.x()) * (a.y() - p.y()) - (b.y() - a.y()) * (a.x() - p.x())) / (direction.y() * (b.x() - a.x()) - direction.x() * (b.y() - a.y()));
        //System.out.println("Point: " + point.x() + " " + point.y() + " Direction " + direction.x() + " " + direction.y() + " Line: A:" + lineSegment.a().x() + " " + lineSegment.a().y() + " B: " + lineSegment.b().x() + " " + lineSegment.b().y() + " Answer: " + answer);

        return answer;
    }

    public boolean onGround() {
        double td = getFirstIntersection(hitbox.shiftAll(super.position), Main.getPossibleCollisions(this, new Vec2D(0, 1)), new Vec2D(0, 1));
        return (td == Double.valueOf(-0.0) || td == Double.valueOf(0.0));
    }
    
}