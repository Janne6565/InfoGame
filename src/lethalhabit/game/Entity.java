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

/**
 * An abstract model for movable objects that interact with the world.
 */
public abstract class Entity implements Tickable, Drawable {
    
    public static final Dimension SPAWN_RANGE = new Dimension((int) (30 * Main.TILE_SIZE), (int) (20 * Main.TILE_SIZE));
    
    /**
     * <code>true</code> if the entity is affected by gravity, <code>false</code> otherwise
     */
    public boolean TAKES_GRAVITY = true;

    public boolean isAnimated = true;

    /**
     * Relative hitbox of the entity (relative to upper left corner)
     */
    public final Hitbox hitbox;
    
    /**
     * Current absolute position
     */
    public Point position;
    
    /**
     * Size of the entity
     */
    public Dimension size;
    
    /**
     * Current basic velocity (e.g. movement, gravity)
     */
    public Vec2D velocity = new Vec2D(0, 0);
    
    /**
     * Current additional velocity (e.g. knockback, wall recoil)
     */
    protected Vec2D recoil = new Vec2D(0, 0);
    
    /**
     * Current display image
     */
    protected BufferedImage graphic;
    
    /**
     * Current animation
     */
    protected Animation animation;
    
    /**
     *
     */
    protected double resetRecoil = 0;
    
    /**
     * Current viscosity, acts as velocity scalar <br>
     * 1 viscosity: No deceleration (full movement) <br>
     * 0 viscosity: Full deceleration (no movement)
     */
    protected double viscosity = 1;
    public double gravity = 1;
    public double maxGravity = Main.MAX_VELOCITY_SPEED;
    
    /**
     * Time since this entity was spawned into the game, in seconds
     */
    protected double age = 0;
    
    /**
     * <code>true</code> if the entity is on solid ground, <code>false</code> otherwise
     */
    protected boolean onGround = false;
    
    /**
     * Current direction
     */
    public Direction direction = Direction.NONE;
    
    /**
     * Previous direction
     */
    protected Direction lastDirection = Direction.NONE;
    
    /**
     * Constructs a new entity with given parameters (size is auto-calculated)
     *
     * @param width    Width (in in-game units)
     * @param graphic  Default image
     * @param position Absolute spawn position
     * @param hitbox   Relative hitbox
     */
    public Entity(double width, BufferedImage graphic, Point position, Hitbox hitbox) {
        this.size = new Dimension((int) width, (int) (graphic.getHeight() * width / graphic.getWidth()));
        this.graphic = graphic;
        this.position = position;
        this.hitbox = hitbox;
    }
    
    /**
     * Spawns the entity by registering it as an <code>Entity</code>, <code>Tickable</code> and <code>Drawable</code>
     *
     * @see Main#entities
     * @see Main#tickables
     * @see Main#drawables
     */
    public void spawn() {
        Main.entities.add(this);
        Main.tickables.add(this);
        Main.drawables.add(this);
    }
    
    /**
     * Spawns the entity by unregistering it and removing all references to it
     *
     * @see Main#entities
     * @see Main#tickables
     * @see Main#drawables
     */
    public void despawn() {
        Main.entities.remove(this);
        Main.tickables.remove(this);
        Main.drawables.remove(this);
    }
    
    /**
     * @see Tickable#tick(Double)
     */
    @Override
    public void tick(Double timeDelta) {
        if (isWallDown() && !onGround) {
            onGround = true;
        }
        
        checkViscosity();
        onMove(getTotalVelocity(), timeDelta);
        
        if (recoil.x() != 0) {
            recoil = recoil.x() < 0 ? new Vec2D(Math.min(recoil.x() + resetRecoil * timeDelta, 0), recoil.y()) : new Vec2D(Math.max(recoil.x() - resetRecoil * timeDelta, 0), recoil.y());
        }
        if (recoil.y() != 0) {
            recoil = recoil.y() < 0 ? new Vec2D(recoil.x(), Math.min(recoil.y() + resetRecoil * timeDelta, 0)) : new Vec2D(recoil.x(), Math.max(recoil.y() - resetRecoil * timeDelta, 0));
        }
        
        Hitbox hitboxBefore = hitbox.shift(position);
        
        int beforeMinX = (int) (hitboxBefore.minX() / Main.TILE_SIZE);
        int beforeMaxX = (int) (hitboxBefore.maxX() / Main.TILE_SIZE);
        int beforeMinY = (int) (hitboxBefore.minY() / Main.TILE_SIZE);
        int beforeMaxY = (int) (hitboxBefore.maxY() / Main.TILE_SIZE);
        if (recoil.x() != 0) {
            velocity = new Vec2D(0, velocity.y());
            direction = Direction.NONE;
        }
        
        moveY(timeDelta);
        moveX(timeDelta);
        
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
        if (isAnimated) {
            graphic = getCurrentFrame(getTimeAnimation());
        }
    }
    
    public BufferedImage getCurrentFrame(double timeAnimation) {
        animation = getAnimation();
        return animation.getCurrentFrame(getTimeAnimation());
    }
    
    public void changeTiles(Hitbox hitboxBefore, Hitbox hitboxAfter) { }
    
    public void onMove(Vec2D velocity, double timeDelta) { }
    
    public void onGroundReset() { }
    
    public void midAir(double timeDelta) { }
    
    /**
     * @return Current animation of the entity
     */
    public abstract Animation getAnimation();
    
    public double getTimeAnimation() {
        return age;
    }
    
    /**
     * @return Current graphic of the entity
     */
    @Override
    public BufferedImage getGraphic() {
        return graphic;
    }
    
    /**
     * @return Current size of the entity
     */
    @Override
    public Dimension getSize() {
        return size;
    }
    
    /**
     * @return Current absolute position of the entity
     */
    @Override
    public Point getPosition() {
        return position;
    }
    
    /**
     * Checks for surrounding liquids and adjusts movement according to viscosity
     */
    public void checkViscosity() {
        List<Liquid> surroundingLiquids = surroundingLiquids();
        surroundingLiquids.stream()
                .min(Comparator.comparing(liquid -> liquid.viscosity))
                .ifPresentOrElse(liquid -> viscosity = liquid.viscosity, () -> viscosity = 1);
        
        surroundingLiquids.stream()
                .min(Comparator.comparing(liquid -> liquid.gravity))
                .ifPresentOrElse(liquid -> gravity = liquid.gravity, () -> gravity = 1);
        
        surroundingLiquids.stream()
                .min(Comparator.comparing(liquid -> liquid.maxGravity))
                .ifPresentOrElse(liquid -> maxGravity = liquid.maxGravity, () -> maxGravity = Main.MAX_VELOCITY_SPEED);
    }
    
    public void knockback(double amountX, double amountY) {
        recoil = new Vec2D(amountX, amountY);
        resetRecoil = 300;
    }
    
    /**
     * Checks for surrounding liquids to determine whether the entity is submerged
     *
     * @return <code>true</code> if submerged, <code>false</code> otherwise
     */
    public final boolean isSubmerged() {
        return !surroundingLiquids().isEmpty();
    }
    
    /**
     * Checks the environment for liquids the entity is submerged in.
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
                velocity = new Vec2D(velocity.x(), Math.min(velocity.y() + (Main.GRAVITATIONAL_ACCELERATION * timeDelta * gravity), maxGravity));
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
            if (xVel.x() < -0.5) {
                onCrashLeft(velocity);
            }
            if (xVel.x() > 0.5) {
                onCrashRight(velocity);
            }
            position = position.plus(xVel.x() * timeUntilCollision, 0);
            velocity = new Vec2D(0, velocity.y());
        } else {
            position = position.plus(xVel.x() * timeDelta, 0);
        }
    }
    
    public void onCrashRight(Vec2D velocity) {
    
    }
    
    
    public void onCrashLeft(Vec2D velocity) {
    
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
            if (yVel.y() > Main.GRAVITATIONAL_ACCELERATION * gravity * (timeDelta * 1.5)) {
                onCrashDown(velocity);
            }
            if (yVel.y() < -Main.GRAVITATIONAL_ACCELERATION * gravity * (timeDelta * 1.5)) {
                onCrashUp(velocity);
            }
            position = position.plus(0, yVel.y() * timeUntilCollision);
            velocity = new Vec2D(velocity.x(), 0);
        } else {
            position = position.plus(0, yVel.y() * timeDelta);
        }
    }
    
    public void onCrashDown(Vec2D velocity) {
    
    }
    
    public void onCrashUp(Vec2D velocity) {
    
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
    
    /**
     * @return The current total absolute velocity of the entity (velocity + recoil, scaled with viscosity)
     */
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
