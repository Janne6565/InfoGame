package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.math.*;
import lethalhabit.math.Point;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Drawable;
import lethalhabit.util.Util;
import lethalhabit.world.Liquid;
import lethalhabit.world.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static lethalhabit.util.Util.getFirstIntersection;
import static lethalhabit.util.Util.mirrorImage;

/**
 * A movable object that interacts with the world.
 */
public abstract class Entity implements Tickable, Drawable {
    
    public static final Dimension SPAWN_RANGE = new Dimension((int) (30 * Main.TILE_SIZE), (int) (20 * Main.TILE_SIZE));
    
    /**
     * Velocity gets impacted by Gravity
     */
    public boolean TAKES_GRAVITY = true;
    
    public final Hitbox hitbox;
    
    public Point position;
    public Vec2D velocity = new Vec2D(0, 0);
    public Dimension size;
    
    protected BufferedImage graphic;
    protected Animation animation;
    protected Vec2D recoil = new Vec2D(0, 0);
    protected double resetRecoil = 0;
    protected double viscosity = 1;
    protected double age = 0;
    protected boolean onGround = false;
    
    public Direction direction = Direction.NONE;
    protected Direction lastDirection = Direction.NONE;
    
    public Entity(double width, BufferedImage graphic, Point position, Hitbox hitbox) {
        this.size = new Dimension((int) width, (int) (graphic.getHeight() * width / graphic.getWidth()));
        this.graphic = graphic;
        this.position = position;
        this.hitbox = hitbox;
    }
    
    public void spawn() {
        Main.entities.add(this);
        Main.tickables.add(this);
        Main.drawables.add(this);
    }
    
    public void despawn() {
        Main.entities.remove(this);
        Main.tickables.remove(this);
        Main.drawables.remove(this);
    }
    
    @Override
    public void tick(Double timeDelta) {
        if (isWallDown() && !onGround) {
            land(getTotalVelocity());
            onGround = true;
        }
        checkViscosity();
        onMove(getTotalVelocity(), timeDelta);

        if (recoil.x() != 0) {
            recoil = recoil.x() < 0 ? new Vec2D(Math.min(recoil.x() + resetRecoil * timeDelta, 0), recoil.y()) : new Vec2D(Math.max(recoil.x() - resetRecoil * timeDelta, 0), recoil.y());
        }

        Hitbox hitboxBefore = hitbox.shift(position);
        
        int beforeMinX = (int) (hitboxBefore.minX() / Main.TILE_SIZE);
        int beforeMaxX = (int) (hitboxBefore.maxX() / Main.TILE_SIZE);
        int beforeMinY = (int) (hitboxBefore.minY() / Main.TILE_SIZE);
        int beforeMaxY = (int) (hitboxBefore.maxY() / Main.TILE_SIZE);
        moveX(timeDelta);
        moveY(timeDelta);
        
        Hitbox hitboxAfter = hitbox.shift(position);
        int afterMinX = (int) (hitboxAfter.minX() / Main.TILE_SIZE);
        int afterMaxX = (int) (hitboxAfter.maxX() / Main.TILE_SIZE);
        int afterMinY = (int) (hitboxAfter.minY() / Main.TILE_SIZE);
        int afterMaxY = (int) (hitboxAfter.maxY() / Main.TILE_SIZE);
        
        if (beforeMinX != afterMinX || beforeMaxX != afterMaxX || beforeMinY != afterMinY || beforeMaxY != afterMaxY) {
            changeTiles(hitboxBefore, hitboxAfter);
        }
        checkDirections(timeDelta);
        age += timeDelta;
        animation = getAnimation();
        int frameIndex = (int) ((age % animation.length) / animation.frameTime);
        graphic = animation.frames.get(frameIndex);
        if (lastDirection == Direction.LEFT) {
            graphic = mirrorImage(graphic);
        }
    }
    
    public void changeTiles(Hitbox hitboxBefore, Hitbox hitboxAfter) { }
    
    public void onMove(Vec2D velocity, double timeDelta) { }
    
    public void onGroundReset() { }
    
    public void land(Vec2D velocity) { }
    
    public void midAir(double timeDelta) { }
    
    public abstract Animation getAnimation();
    
    @Override
    public BufferedImage getGraphic() {
        return graphic;
    }
    
    @Override
    public Dimension getSize() {
        return size;
    }
    
    @Override
    public Point getPosition() {
        return position;
    }
    
    /**
     * Checks for liquids and adjusts movement according to viscosity.
     */
    public void checkViscosity() {
        surroundingLiquids().stream()
                .min(Comparator.comparing(liquid -> liquid.viscosity))
                .ifPresentOrElse(liquid -> viscosity = liquid.viscosity, () -> viscosity = 1);
    }
    
    public final boolean isSubmerged() {
        return !surroundingLiquids().isEmpty();
    }
    
    /**
     * Checks the environment for liquids the object is submerged in.
     *
     * @return A complete list of liquids that surround the player (may be empty)
     */
    public List<Liquid> surroundingLiquids() {
        Hitbox absolute = hitbox.shift(getPosition());
        List<Liquid> liquids = new ArrayList<>();
        for (int tileX = (int) (absolute.minPosition.x() / Main.TILE_SIZE) - 1; tileX <= (absolute.maxPosition.x() / Main.TILE_SIZE) + 1; tileX++) {
            for (int tileY = (int) (absolute.minPosition.y() / Main.TILE_SIZE) - 1; tileY <= (absolute.maxPosition.y() / Main.TILE_SIZE) + 1; tileY++) {
                Tile tile = Main.tileAt(tileX, tileY);
                if (tile != null) {
                    Liquid liquid = Liquid.TILEMAP.get(tile.liquid);
                    if (liquid != null) {
                        if (absolute.intersects(liquid.hitbox.shift(tileX * Main.TILE_SIZE, tileY * Main.TILE_SIZE))) {
                            liquids.add(liquid);
                        }
                    }
                }
            }
        }
        return liquids;
    }
    
    /**
     * Stops movement if a collidable is hit.
     *
     * @param timeDelta time between Frames
     */
    public void checkDirections(double timeDelta) {
        if (!isWallDown()) {
            onGround = false;
            midAir(timeDelta);
            if (TAKES_GRAVITY) {
                velocity = new Vec2D(velocity.x(), Math.min(velocity.y() + (Main.GRAVITATIONAL_ACCELERATION * timeDelta * viscosity), Main.MAX_VELOCITY_SPEED * viscosity));
            }
        } else {
            onGroundReset();
        }
    }
    
    /**
     * Movement on the x-axis
     *
     * @param timeDelta time between frames
     */
    public void moveX(double timeDelta) {
        Vec2D xVel = new Vec2D(getTotalVelocity().x(), 0);
        List<Hitbox> collidables = Util.getPossibleCollisions(hitbox.shift(position), xVel, timeDelta);
        Double minTime = getFirstIntersection(hitbox.shift(position), collidables, xVel);
        if (!Double.isNaN(minTime) && minTime <= timeDelta) {
            double timeUntilCollision = Math.max(0, minTime - (Main.SAFE_DISTANCE / Math.abs(xVel.x())));
            position = position.plus(xVel.x() * timeUntilCollision, 0);
            velocity = xVel.x() > 0 ? new Vec2D(Math.min(0, velocity.x()), velocity.y()) : new Vec2D(Math.max(0, velocity.x()), velocity.y());
        } else {
            position = position.plus(xVel.x() * timeDelta, 0);
        }
    }
    
    /**
     * Movement on the y-axis
     *
     * @param timeDelta time between frames
     */
    public void moveY(double timeDelta) {
        Vec2D yVel = new Vec2D(0, getTotalVelocity().y());
        List<Hitbox> collidables = Util.getPossibleCollisions(hitbox.shift(position), yVel, timeDelta);
        Double minTime = getFirstIntersection(hitbox.shift(position), collidables, yVel);
        if (!Double.isNaN(minTime) && minTime <= timeDelta) {
            double timeUntilCollision = Math.max(0, minTime - (Main.SAFE_DISTANCE / Math.abs(yVel.y())));
            position = position.plus(0, yVel.y() * timeUntilCollision);
            velocity = yVel.y() > 0 ? new Vec2D(velocity.x(), Math.min(0, velocity.y())) : new Vec2D(velocity.x(), Math.max(0, velocity.y()));
        } else {
            position = position.plus(0, yVel.y() * timeDelta);
        }
    }
    
    /**
     * Checks downward ground collision
     *
     * @return true in case of collision with a ground, false otherwise
     */
    public boolean isWallDown() {
        Double td = getFirstIntersection(hitbox.shift(position), Util.getPossibleCollisions(hitbox.shift(position), new Vec2D(0, 1), 1), new Vec2D(0, 1));
        return !Double.isNaN(td) && (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    public boolean isWallDown(Point offset) {
        Hitbox hitboxToCheck = hitbox.shift(position).shift(offset);
        Double td = getFirstIntersection(hitboxToCheck, Util.getPossibleCollisions(hitboxToCheck, new Vec2D(0, 1), 1), new Vec2D(0, 1));
        return !Double.isNaN(td) && (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    /**
     * Checks wall collision to the right
     *
     * @return true in case of collision with a right wall, false otherwise
     */
    public boolean isWallRight() {
        Double td = getFirstIntersection(hitbox.shift(position), Util.getPossibleCollisions(hitbox.shift(position), new Vec2D(1, 0), 1), new Vec2D(1, 0));
        return !Double.isNaN(td) && (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    public boolean isWallRight(Point offset) {
        Hitbox hitboxToCheck = hitbox.shift(position).shift(offset);
        Double td = getFirstIntersection(hitboxToCheck, Util.getPossibleCollisions(hitboxToCheck, new Vec2D(1, 0), 1), new Vec2D(1, 0));
        return !Double.isNaN(td) && (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    /**
     * Checks wall collision to the left
     *
     * @return true in case of collision with a left wall, false otherwise
     */
    public boolean isWallLeft() {
        Double td = getFirstIntersection(hitbox.shift(position), Util.getPossibleCollisions(hitbox.shift(position), new Vec2D(-1, 0), 1), new Vec2D(-1, 0));
        return !Double.isNaN(td) && (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    public boolean isWallLeft(Point offset) {
        Hitbox hitboxToCheck = hitbox.shift(position).shift(offset);
        Double td = getFirstIntersection(hitboxToCheck, Util.getPossibleCollisions(hitboxToCheck, new Vec2D(-1, 0), 1), new Vec2D(-1, 0));
        return !Double.isNaN(td) && (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    /**
     * Checks upward ceiling collision
     *
     * @return true in case of collision with a ceiling, false otherwise
     */
    public boolean isWallUp() {
        Double td = getFirstIntersection(hitbox.shift(position), Util.getPossibleCollisions(hitbox.shift(position), new Vec2D(0, -1), 1), new Vec2D(0, -1));
        return !Double.isNaN(td) && (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    public boolean isWallUp(Point offset) {
        Hitbox hitboxToCheck = hitbox.shift(position).shift(offset);
        Double td = getFirstIntersection(hitboxToCheck, Util.getPossibleCollisions(hitboxToCheck, new Vec2D(0, -1), 1), new Vec2D(0, -1));
        return !Double.isNaN(td) && (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    public Vec2D getTotalVelocity() {
        return velocity.plus(recoil).scale(viscosity);
    }
    
    @Override
    public void draw(Graphics graphics) {
        Drawable.super.draw(graphics);
        if (Main.DEBUG_HITBOX) {
            for (LineSegment line : hitbox.shift(getPosition()).edges()) {
                graphics.setColor(Main.HITBOX_STROKE_COLOR);
                Util.drawLineSegment(graphics, line);
            }
        }
    }
    
}
