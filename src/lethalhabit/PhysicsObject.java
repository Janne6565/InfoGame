package lethalhabit;

import lethalhabit.game.Liquid;
import lethalhabit.game.Tile;
import lethalhabit.technical.Hitbox;
import lethalhabit.technical.Point;
import lethalhabit.technical.Vec2D;
import lethalhabit.ui.Drawable;

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
    
    public final Dimension size;
    public final Hitbox hitbox;
    
    public BufferedImage graphic;
    public Point position;
    public Vec2D velocity = new Vec2D(0, 0);
    
    public PhysicsObject(double width, BufferedImage graphic, Point position, Hitbox hitbox) {
        this.size = new Dimension((int) width, (int) (graphic.getHeight() * width / graphic.getWidth()));
        this.graphic = graphic;
        this.position = position;
        this.hitbox = hitbox;
        Main.physicsObjects.add(this);
        Main.tickables.add(this);
        Main.drawables.add(this);
    }
    
    @Override
    public void tick(Double timeDelta) {
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
    
    @Override
    public boolean isRelative() {
        return true;
    }
    
    /**
     * Checks for liquids and adjusts movement according to viscosity.
     */
    public void checkViscosity() {
        surroundingLiquids().stream()
                .min(Comparator.comparing(liquid -> liquid.viscosity))
                .ifPresent(liquid -> velocity = velocity.scale(liquid.viscosity));
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
            velocity = new Vec2D(velocity.x(), Math.min(velocity.y() + (Main.GRAVITATIONAL_ACCELERATION * timeDelta), Main.MAX_VELOCITY_SPEED));
        } else {
            onGroundReset();
        }
    }
    
    /**
     * Movement on the x-axis
     * @param timeDelta time between frames
     */
    public void moveX(double timeDelta) {
        Vec2D vel = new Vec2D(this.velocity.x(), 0);
        
        List<Hitbox> collidables = Main.getPossibleCollisions(hitbox.shift(position), vel, timeDelta);
        Double minTime = getFirstIntersection(hitbox.shift(position), collidables, vel);
        
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
        position = position.plus(vel.x() * timeWeTake, 0);
        
        if (!Double.isNaN(minTime)) {
            if (minTime <= timeDelta) {
                if (vel.x() > 0) {
                    this.velocity = new Vec2D(Math.min(0, this.velocity.x()), this.velocity.y());
                } else {
                    this.velocity = new Vec2D(Math.max(0, this.velocity.x()), this.velocity.y());
                }
            }
        }
    }
    
    /**
     * Movement on the y-axis
     * @param timeDelta time between frames
     */
    public void moveY(double timeDelta) {
        Vec2D vel = new Vec2D(0, this.velocity.y());
        
        List<Hitbox> collidables = Main.getPossibleCollisions(hitbox.shift(position), vel, timeDelta);
        Double minTime = getFirstIntersection(hitbox.shift(position), collidables, vel);
        
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
                    this.velocity = new Vec2D(this.velocity.x(), Math.min(0, this.velocity.y()));
                } else {
                    this.velocity = new Vec2D(this.velocity.x(), Math.max(0, this.velocity.y()));
                }
            }
        }
        
    }
    
    abstract void onGroundReset();
    
    /**
     * Checks downward ground collision
     * @return true in case of collision with a ground, false otherwise
     */
    public boolean isWallDown() {
        Double td = getFirstIntersection(hitbox.shift(position), Main.getPossibleCollisions(hitbox.shift(position), new Vec2D(0, 1), 1), new Vec2D(0, 1));
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    /**
     * Checks wall collision to the right
     * @return true in case of collision with a right wall, false otherwise
     */
    public boolean isWallRight() {
        Double td = getFirstIntersection(hitbox.shift(position), Main.getPossibleCollisions(hitbox.shift(position), new Vec2D(1, 0), 1), new Vec2D(1, 0));
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    /**
     * Checks wall collision to the left
     * @return true in case of collision with a left wall, false otherwise
     */
    public boolean isWallLeft() {
        Double td = getFirstIntersection(hitbox.shift(position), Main.getPossibleCollisions(hitbox.shift(position), new Vec2D(-1, 0), 1), new Vec2D(-1, 0));
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
    /**
     * Checks upward ceiling collision
     * @return true in case of collision with a ceiling, false otherwise
     */
    public boolean isWallUp() {
        Double td = getFirstIntersection(hitbox.shift(position), Main.getPossibleCollisions(hitbox.shift(position), new Vec2D(0, -1), 1), new Vec2D(0, -1));
        if (Double.isNaN(td)) {
            return false;
        }
        return (td >= 0 && td <= Main.COLLISION_THRESHOLD);
    }
    
}