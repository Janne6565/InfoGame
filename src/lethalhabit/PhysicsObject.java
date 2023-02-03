package lethalhabit;

import lethalhabit.game.Liquid;
import lethalhabit.game.Tile;
import lethalhabit.technical.Hitbox;
import lethalhabit.technical.Point;
import lethalhabit.technical.Vec2D;
import lethalhabit.ui.Drawable;
import lethalhabit.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static lethalhabit.util.Util.getFirstIntersection;

/**
 * A movable object that interacts with the world.
 */
public abstract class PhysicsObject implements Tickable, Drawable {
    
    /**
     * Velocity gets impacted by Gravity
     */
    public boolean TAKES_GRAVITY = true;
    
    public final Dimension size;
    public final Hitbox hitbox;
    
    public BufferedImage graphic;
    public Point position;
    public Vec2D velocity = new Vec2D(0, 0);
    protected double viscosity = 1;
    /**
     * Velocity added onto the normal velocity, you cant change this
     */
    public Vec2D recoil = new Vec2D(0, 0);
    
    public PhysicsObject(double width, BufferedImage graphic, Point position, Hitbox hitbox) {
        this.size = new Dimension((int) width, (int) (graphic.getHeight() * width / graphic.getWidth()));
        this.graphic = graphic;
        this.position = position;
        this.hitbox = hitbox;
        Main.physicsObjects.add(this);
        Main.tickables.add(this);
        Main.drawables.add(this);
    }
    
    private boolean onGround = false;
    
    @Override
    public void tick(Double timeDelta) {
        if (isWallDown() && !onGround) {
            onLand(getTotalVelocity());
            onGround = true;
        }
        checkViscosity();
        moveX(timeDelta);
        moveY(timeDelta);
        checkDirections(timeDelta);
    }
    
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
     * @param timeDelta time between Frames
     */
    public void checkDirections(double timeDelta) {
        if (!isWallDown()) {
            onGround = false;
            if (TAKES_GRAVITY) {
                velocity = new Vec2D(velocity.x(), Math.min(velocity.y() + (Main.GRAVITATIONAL_ACCELERATION * timeDelta * viscosity), Main.MAX_VELOCITY_SPEED * viscosity));
            }
        } else {
            onGroundReset();
        }
    }
    
    /**
     * Movement on the x-axis
     * @param timeDelta time between frames
     */
    public void moveX(double timeDelta) {
        Vec2D vel = new Vec2D(getTotalVelocity().x(), 0);
        
        List<Hitbox> collidables = Util.getPossibleCollisions(hitbox.shift(position), vel, timeDelta);
        Double minTime = getFirstIntersection(hitbox.shift(position), collidables, vel);
        
        double timeWeTake = timeDelta;
        double safeDistance = Main.SAFE_DISTANCE;
        Double timeUntilReachedSafeDistance = safeDistance / Math.abs(vel.x());
        
        if (!Double.isNaN(minTime)) {
            if (minTime <= timeDelta) {
                timeWeTake = minTime < timeUntilReachedSafeDistance ? 0.0 : minTime - timeUntilReachedSafeDistance;
            }
        }
        position = position.plus(vel.x() * timeWeTake, 0);
        
        if (!Double.isNaN(minTime)) {
            if (minTime <= timeDelta) {
                this.velocity = vel.x() > 0 ? new Vec2D(Math.min(0, this.velocity.x()), this.velocity.y()) : new Vec2D(Math.max(0, this.velocity.x()), this.velocity.y());
            }
        }
    }
    
    /**
     * Movement on the y-axis
     * @param timeDelta time between frames
     */
    public void moveY(double timeDelta) {
        Vec2D vel = new Vec2D(0, getTotalVelocity().y());
        
        List<Hitbox> collidables = Util.getPossibleCollisions(hitbox.shift(position), vel, timeDelta);
        Double minTime = getFirstIntersection(hitbox.shift(position), collidables, vel);
        
        Double timeWeTake = timeDelta;
        Double safeDistance = Main.SAFE_DISTANCE;
        Double timeWeNeed = safeDistance / Math.abs(vel.y());
        
        if (!Double.isNaN(minTime)) {
            if (minTime <= timeDelta) {
                timeWeTake = minTime < timeWeNeed ? 0.0 : minTime - timeWeNeed;
            }
        }
        position = position.plus(new Point(0, vel.y() * timeWeTake));
        if (!Double.isNaN(minTime)) {
            if (minTime <= timeDelta) {
                this.velocity = vel.y() > 0 ? new Vec2D(this.velocity.x(), Math.min(0, this.velocity.y())) : new Vec2D(this.velocity.x(), Math.max(0, this.velocity.y()));
            }
        }
        
    }
    
    public void onGroundReset() { }
    
    public void onLand(Vec2D velocity) { }
    
    /**
     * Checks downward ground collision
     * @return true in case of collision with a ground, false otherwise
     */
    public boolean isWallDown() {
        Double td = getFirstIntersection(hitbox.shift(position), Util.getPossibleCollisions(hitbox.shift(position), new Vec2D(0, 1), 1), new Vec2D(0, 1));
        return !Double.isNaN(td) && (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    /**
     * Checks wall collision to the right
     * @return true in case of collision with a right wall, false otherwise
     */
    public boolean isWallRight() {
        Double td = getFirstIntersection(hitbox.shift(position), Util.getPossibleCollisions(hitbox.shift(position), new Vec2D(1, 0), 1), new Vec2D(1, 0));
        return !Double.isNaN(td) && (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    /**
     * Checks wall collision to the left
     * @return true in case of collision with a left wall, false otherwise
     */
    public boolean isWallLeft() {
        Double td = getFirstIntersection(hitbox.shift(position), Util.getPossibleCollisions(hitbox.shift(position), new Vec2D(-1, 0), 1), new Vec2D(-1, 0));
        return !Double.isNaN(td) && (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    /**
     * Checks upward ceiling collision
     * @return true in case of collision with a ceiling, false otherwise
     */
    public boolean isWallUp() {
        Double td = getFirstIntersection(hitbox.shift(position), Util.getPossibleCollisions(hitbox.shift(position), new Vec2D(0, -1), 1), new Vec2D(0, -1));
        return !Double.isNaN(td) && (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    public Vec2D getTotalVelocity() {
        return velocity.plus(recoil).scale(viscosity);
    }
    
}