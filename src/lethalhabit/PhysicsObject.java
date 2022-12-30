package lethalhabit;

import lethalhabit.math.*;
import lethalhabit.ui.Drawable;

public abstract class PhysicsObject extends Drawable implements Tickable {
    
    public Hitbox hitbox;
    public boolean collidable = true;
    public Vec2D velocity = new Vec2D(0, 0);
    
    public PhysicsObject(float width, String path, Point pos) {
        super(width, path, pos);
        Main.physicsObjects.add(this);
    }
    
    public abstract double getSpeed();
    
    @Override
    public void tick(float timeDelta) {
        if (onGround()) {
            if (position.y() > 100) {
                position = new Point(position.x(), 100);
            }
            if (velocity.y() > 0) {
                velocity = new Vec2D(velocity.x(), 0);
            }
        } else {
            velocity = velocity.plus(0, 100 * timeDelta);
        }
        position = position.plus(velocity.x() * timeDelta, velocity.y() * timeDelta);
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
        float minTimeDelta = (float) getFirstIntersection(hitbox.shiftAll(position), possibleCollisions, velocity);
        timeDelta = Math.min(timeDelta, minTimeDelta);
        position = position.plus(velocity.x() * timeDelta, velocity.y() * timeDelta);
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
        double minTime = factorUntilIntersection(hitbox1.vertices()[0], direction, new LineSegment(hitbox2.vertices()[0], hitbox2.vertices()[1]));
        for (Point point : hitbox1) { // Every possible point from hitbox 1
            for (LineSegment edge : hitbox2.edges()) { // Every combination of the Points that are next to each other
                double timeDelta = factorUntilIntersection(point, direction, edge);
                minTime = Math.min(timeDelta, minTime);
            }
        }
        for (Point point : hitbox2) { // Every possible point from hitbox 2
            for (LineSegment edge : hitbox1.edges()) { // Every combination of the Points that are next to each other
                double timeDelta = factorUntilIntersection(point, direction, edge);
                minTime = Math.min(timeDelta, minTime);
            }
        }
        return minTime;
    }

    public double factorUntilIntersection(Point point, Vec2D direction, LineSegment lineSegment) {
        Vec2D p = point.loc();
        Vec2D a = lineSegment.a().loc();
        Vec2D b = lineSegment.b().loc();

        return ((b.x() - a.x()) * (a.y() - p.y()) - (b.y() - a.y()) * (a.x() - p.x())) / (direction.y() * (b.x() - a.x()) - direction.x() * (b.y() - a.y()));
    }

    public boolean onGround() {
        return getFirstIntersection(hitbox.shiftAll(position), Main.getPossibleCollisions(this, new Vec2D(0, 1)), new Vec2D(0, 1)) == 0;
    }
    
}