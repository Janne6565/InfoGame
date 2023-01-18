package lethalhabit;

import lethalhabit.game.Block;
import lethalhabit.game.Liquid;
import lethalhabit.game.Tile;
import lethalhabit.technical.*;
import lethalhabit.technical.Point;
import lethalhabit.ui.Drawable;

import java.util.ArrayList;
import java.util.Map;

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
        checkViscosity();
        moveX(timeDelta, getVelocity());
        moveY(timeDelta, getVelocity());
        checkDirections(timeDelta);
    }

    public void checkViscosity() {
        Hitbox absolute = hitbox.shift(position);
        double minViscosity = 1;
        for (int tileX = (int) (absolute.minPosition.x() / Main.TILE_SIZE) - 1; tileX <= (absolute.maxPosition.x() / Main.TILE_SIZE) + 1; tileX ++) {
            for (int tileY = (int) (absolute.minPosition.y() / Main.TILE_SIZE) - 1; tileY <= (absolute.maxPosition.y() / Main.TILE_SIZE) + 1; tileY ++) {
                Tile tile = Main.tileAt(tileX, tileY);
                if (tile != null) {
                    Liquid liquid = Liquid.TILEMAP.get(tile.liquid);
                    if (liquid != null) {
                        if (absolute.intersects(liquid.hitbox.shift(tileX * Main.TILE_SIZE, tileY * Main.TILE_SIZE))) {
                            minViscosity = Math.min(minViscosity, liquid.viscosity);
                        }
                    }
                }
            }
        }
        velocity = velocity.scale(minViscosity);
    }

    /**
     * Stops the velocity if there is an collidable the way of the Physics Object
     * @param timeDelta time between Frames
     */
    public void checkDirections(double timeDelta) {
        if (!isWallDown()) {
            velocity = new Vec2D(velocity.x(), Math.min(velocity.y() + (Main.GRAVITATIONAL_ACCELERATION * timeDelta), Main.MAX_VELOCITY_SPEED));
        } else {
            onGroundReset();
        }
    }

    /**
     * Used to collision dependent movement on the x axis
     * @param timeDelta time between frames
     * @param generalVelocity general velocity of physical object
     */
    public void moveX(double timeDelta, Vec2D generalVelocity) {
        Vec2D vel = new Vec2D(generalVelocity.x(), 0);

        ArrayList<Hitbox> collidables = Main.getPossibleCollisions(hitbox.shift(position), vel, timeDelta);
        Double minTime = getFirstIntersection(hitbox.shift(position), collidables, vel, false);

        Double timeWeTake = timeDelta;
        Double safeDistance = Main.SAFE_DISTANCE;
        Double timeUntilReachedSafeDistance = safeDistance / Math.abs(vel.x());

        if (!Double.isNaN(minTime)) {
            if (minTime <= timeDelta) {
                if (minTime < timeUntilReachedSafeDistance) {
                    timeWeTake = minTime - timeUntilReachedSafeDistance;
                } else {
                    timeWeTake = minTime - timeUntilReachedSafeDistance;
                }
            }
        }
        position = position.plus(vel.x() *  timeWeTake, 0);

        if (!Double.isNaN(minTime)) {
            if (minTime <= timeDelta){
                if (vel.x() > 0) {
                    velocity = new Vec2D(Math.min(0, velocity.x()), velocity.y());
                } else {
                    velocity = new Vec2D(Math.max(0, velocity.x()), velocity.y());
                }
            }
        }
    }

    /**
     * Used to collision dependent movement on the y axis
     * @param timeDelta time between frames
     * @param generalVelocity general velocity of physical object
     */
    public void moveY(double timeDelta, Vec2D generalVelocity) {
        Vec2D vel = new Vec2D(0, generalVelocity.y());

        ArrayList<Hitbox> collidables = Main.getPossibleCollisions(hitbox.shift(position), vel, timeDelta);
        Double minTime = getFirstIntersection(hitbox.shift(position), collidables, vel, false);

        Double timeWeTake = timeDelta;
        Double safeDistance = Main.SAFE_DISTANCE;
        Double timeWeNeed = safeDistance / Math.abs(vel.y());

        if (!Double.isNaN(minTime)) {
            if (minTime <= timeDelta) {
                if (minTime < timeWeNeed) {
                    timeWeTake = 0.0;
                } else {
                    timeWeTake = minTime - timeWeNeed;
                }
            }
        }
        position = position.plus(new Point(0, vel.y() * timeWeTake));
        if (!Double.isNaN(minTime)) {
            if (minTime <= timeDelta) {
                if (vel.y() > 0) {
                    velocity = new Vec2D(velocity.x(), Math.min(0, velocity.y()));
                } else {
                    velocity = new Vec2D(velocity.x(), Math.max(0, velocity.y()));
                }
            }
        }

    }

    /**
     * Calculates the first intersection
     * @param hitbox hitbox that are getting moved
     * @param collidables List of all elements the hitbox might be colliding to
     * @param direction the direction the hitbox is getting moved in
     * @param debug debug prints
     * @return times we need to multiply the direction vector to get to the first point they intersect
     */
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

    public Vec2D getVelocity() {
        return velocity;
    }

    /**
     * calculates the first intersection of two lines
     * @param s1 line moved
     * @param direction direction s1 is getting moved in
     * @param s2 line that might be colliding
     * @param debug debug prints
     * @return times we need to multiply the direction until s1 touches s2
     */
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

    /**
     * calculates the time taken to reach lineSegment from Point with velocity
     * @param point Start Point
     * @param direction Direction we move from the Point
     * @param lineSegment Line the Point might be crossing
     * @param debug debug prints
     * @return the factor we need to multiply the direction until we intersect with lineSegment
     */
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

    /**
     * checks if there is ground below the physics object
     * @return if there is ground below the physics object
     */
    public boolean isWallDown() {
        Double td = getFirstIntersection(hitbox.shift(super.position), Main.getPossibleCollisions(hitbox.shift(position), new Vec2D(0, 1), 1), new Vec2D(0, 1), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }

    /**
     * checks if there is a wall to the right
     * @return if there is a wall to the right
     */
    public boolean isWallRight() {
        Double td = getFirstIntersection(hitbox.shift(super.position), Main.getPossibleCollisions(hitbox.shift(position), new Vec2D(1, 0), 1), new Vec2D(1, 0), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }

    /**
     * checks if there is a wall to the left
     * @returnif there is a wall to the left
     */
    public boolean isWallLeft() {
        Double td = getFirstIntersection(hitbox.shift(super.position), Main.getPossibleCollisions(hitbox.shift(position), new Vec2D(-1, 0), 1), new Vec2D(-1, 0), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }

    /**
     * checks if there is a wall above the Physics Object
     * @return if there is a wall above the Physics Object
     */
    public boolean isWallUp() {
        Double td = getFirstIntersection(hitbox.shift(super.position), Main.getPossibleCollisions(hitbox.shift(position), new Vec2D(0, -1), 1), new Vec2D(0, -1), false);
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
}