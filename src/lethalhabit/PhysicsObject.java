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
            velocity = new Vec2D(velocity.x(), 0);
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
        double minTimeDelta = getFirstIntersection(this.hitbox.shiftAll(super.position), possibleCollisions, velocity);
        if (!(minTimeDelta == Double.NaN)) {
            float min = timeDelta;
            if (minTimeDelta >= 0) {
                min = (float) Math.min(timeDelta, minTimeDelta);
            }
            super.position = super.position.plus(velocity.x() * min, velocity.y() * min);
        }
    }

    public void jump() {
        velocity = velocity.minus(0, 100);
    }

    public double getFirstIntersection(Hitbox hitbox, Collidable[] collidables, Vec2D direction) {
        double minTime = Double.NaN;
        for (Collidable collidable : collidables) {
            double dt = minimumFactorUntilIntersection(hitbox, direction, collidable.getHitbox());
            minTime = Math.min(dt, minTime);
            if (minTime == Double.NaN && dt != Double.NaN) {
                minTime = dt;
            }
        }
        return minTime;
    }

    public Double minimumFactorUntilIntersection(Hitbox hitbox1, Vec2D direction, Hitbox hitbox2) {
        Double min = Double.NaN;
        for (Point p : hitbox1.vertices()) {
            for (LineSegment edge : hitbox2.edges()) {
                double n = factorUntilIntersection(p, direction, edge);
                if (n >= 0 && (min == Double.NaN || n < min)) {
                    Point solution = p.plus(direction.scale(n));
                    if (solution.x() >= edge.minX() && solution.x() <= edge.maxX() && solution.y() >= edge.minY() && solution.y() <= edge.maxY()) {
                        min = n;
                    }
                }
            }
        }

        for (Point p : hitbox2.vertices()) {
            for (LineSegment edge : hitbox1.edges()) {
                double n = factorUntilIntersection(p, direction.scale(-1), edge);
                if (n >= 0 && (min == Double.NaN || n < min)) {
                    Point solution = p.plus(direction.scale(-n));
                    if (solution.x() >= edge.minX() && solution.x() <= edge.maxX() && solution.y() >= edge.minY() && solution.y() <= edge.maxY()) {
                        min = n;
                    }
                }
            }
        }
        System.out.println(min);
        return min;
    }


    public double factorUntilIntersection(Point point, Vec2D direction, LineSegment lineSegment) {
        Vec2D p = point.loc();
        Vec2D a = lineSegment.a().loc();
        Vec2D b = lineSegment.b().loc();
        double answer = ((b.x() - a.x()) * (a.y() - p.y()) - (b.y() - a.y()) * (a.x() - p.x())) / (direction.y() * (b.x() - a.x()) - direction.x() * (b.y() - a.y()));
        System.out.println("Point: " + point.x() + " " + point.y() + " Direction " + direction.x() + " " + direction.y() + " Line: A:" + lineSegment.a().x() + " " + lineSegment.a().y() + " B: " + lineSegment.b().x() + " " + lineSegment.b().y() + " Answer: " + answer);

        return answer;
    }

    public boolean onGround() {
        double td = getFirstIntersection(this.hitbox.shiftAll(super.position), Main.getPossibleCollisions(this, new Vec2D(0, 1)), new Vec2D(0, 1));
        return td == 0;
    }
    
}