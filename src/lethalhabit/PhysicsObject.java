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
        if (onGround()) {
            if (super.position.y() > 100) {
                super.position = new Point(super.position.x(), 100);
            }
            if (velocity.y() > 0) {
                velocity = new Vec2D(velocity.x(), 0);
            }
        } else {
            velocity = velocity.plus(0, 100 * timeDelta);
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
        float minTimeDelta = (float) getFirstIntersection(this.hitbox.shiftAll(super.position), possibleCollisions, velocity);
        float min = Math.min(timeDelta, minTimeDelta);
        super.position = super.position.plus(velocity.x() * min, velocity.y() * min);
    }

    public void jump() {
        velocity = velocity.minus(0, 100);
    }

    public double getFirstIntersection(Hitbox hitbox, Collidable[] collidables, Vec2D direction) {
        double minTime = getTheShortestPossibleIntersection(hitbox, collidables[0].getHitbox(), direction);
        for (Collidable collidable : collidables) {
            double dt = getTheShortestPossibleIntersection(hitbox, collidable.getHitbox(), direction);
            minTime = Math.min(dt, minTime);
        }
        return minTime;
    }

    public double getTheShortestPossibleIntersection(Hitbox hitbox1, Hitbox hitbox2, Vec2D direction) {
        double minTime = Double.NaN;
        // System.out.println("Me point");
        for (Point point : hitbox1.vertices()) { // Every possible point from hitbox 1
            for (LineSegment edge : hitbox2.edges()) { // Every combination of the Points that are next to each other
                double timeDelta = factorUntilIntersection(point, direction, edge);
                if (!Double.isInfinite(timeDelta) && !Double.isNaN(timeDelta)) {
                    minTime = Math.min(timeDelta, minTime);
                }
            }
        }
        // System.out.println("Him point");
        for (Point point : hitbox2.vertices()) { // Every possible point from hitbox 2
            for (LineSegment edge : hitbox1.edges()) { // Every combination of the Points that are next to each other
                double timeDelta = factorUntilIntersection(point, direction, edge);
                if (!Double.isInfinite(timeDelta) && !Double.isNaN(timeDelta)) {
                    minTime = Math.min(timeDelta, minTime);
                }
            }
        }
        return minTime;
    }

    public double factorUntilIntersection(Point point, Vec2D direction, LineSegment lineSegment) {
        Vec2D p = point.loc();
        Vec2D a = lineSegment.a().loc();
        Vec2D b = lineSegment.b().loc();
        double answer = ((b.x() - a.x()) * (a.y() - p.y()) - (b.y() - a.y()) * (a.x() - p.x())) / (direction.y() * (b.x() - a.x()) - direction.x() * (b.y() - a.y()));
        // System.out.println("Point: " + point.x() + " " + point.y() + " Direction " + direction.x() + " " + direction.y() + " Line: A:" + lineSegment.a().x() + " " + lineSegment.a().y() + " B: " + lineSegment.b().x() + " " + lineSegment.b().y() + " Answer: " + answer);

        return answer;
    }

    public boolean onGround() {
        double td = getFirstIntersection(this.hitbox.shiftAll(super.position), Main.getPossibleCollisions(this, new Vec2D(0, 1)), new Vec2D(0, 1));
        return td == 0;
    }
    
}