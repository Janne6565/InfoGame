package lethalhabit.game.enemy;

import lethalhabit.Main;
import lethalhabit.game.DamageSource;
import lethalhabit.game.Entity;
import lethalhabit.game.Hittable;
import lethalhabit.math.Hitbox;
import lethalhabit.math.LineSegment;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Camera;
import lethalhabit.util.Util;

import java.awt.*;

/**
 * Abstract model for all enemies. All enemies are hittable entities (and therefore tickable and drawable)
 */
public abstract class Enemy extends Entity implements Hittable {
    
    /**
     * The position of the enemy's eyes, relative to its hitbox (used to check player visibility)
     */
    protected final Point eyePosition;

    /**
     * The maximum distance this enemy can see
     */
    protected final double sightRange;

    /**
     * The speed this enemy normally moves at
     */
    protected final double speed;
    
    /**
     * The current health points of this enemy <br>
     * Enemies die at 0 hp or less
     */
    public int hp = 10;
    
    /**
     * Constructs an enemy with the given parameters <br>
     * Only accessible from subclasses to prevent instantiation of unspecified enemies
     * 
     * @param width Width of the enemy's graphic (in in-game units)
     * @param position Absolute position for the enemy to be instantiated at
     * @param hitbox Relative hitbox for the enemy
     * @param eyePosition Relative position for the enemy's eyes 
     * @param sightRange Maximum sight range for the enemy
     * @param speed Movement speed for the enemy
     */
    protected Enemy(double width, Point position, Hitbox hitbox, Point eyePosition, double sightRange, double speed) {
        super(width, Animation.PLAYER_IDLE_LEFT.get(0), position, hitbox);
        this.eyePosition = eyePosition;
        this.sightRange = sightRange;
        this.speed = speed;
    }

    /**
     * @see Entity#despawn()
     */
    @Override
    public void despawn() {
        super.despawn();
        Util.removeHittable(this);
    }

    /**
     * @see Hittable#getHitbox()
     */
    @Override
    public Hitbox getHitbox() {
        return hitbox;
    }
    
    /**
     * @see Drawable#layer()
     */
    @Override
    public int layer() {
        return Camera.LAYER_GAME;
    }
    
    /**
     * @see Hittable#onHit(DamageSource)
     */
    @Override
    public void onHit(DamageSource source) {

    }

    /**
     * Sets the enemy's x-knockback
     *
     * @param value value of the knockback (only x-direction)
     */
    public void knockback(double value) {
        recoil = new Vec2D(value, 0);
        resetRecoil = 300;
    }

    /**
     * @see Drawable#draw(Graphics)
     */
    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        if (Main.DEBUG_HITBOX) {
            Point absoluteEyes = position.plus(eyePosition);
            for (Point playerVertex : Main.mainCharacter.hitbox.shift(Main.mainCharacter.position)) {
                LineSegment ray = new LineSegment(absoluteEyes, playerVertex);
                Util.drawLineSegment(graphics, ray);
            }
        }
    }

    /**
     * @see Entity#changeTiles(Hitbox, Hitbox)
     */
    @Override
    public void changeTiles(Hitbox hitboxBefore, Hitbox hitboxAfter) {
        Util.removeHittable(this, hitboxBefore);
        Util.registerHittable(this);
    }
    
    /**
     * Checks if this enemy can see the player by checking if it can see any of their vertices
     * 
     * @return <code>true</code> if the player can be seen, <code>false</code> otherwise
     */
    protected boolean canSeePlayer() {
        Point absoluteEyes = position.plus(eyePosition);
        for (Point playerVertex : Main.mainCharacter.hitbox.shift(Main.mainCharacter.position)) {
            LineSegment ray = new LineSegment(absoluteEyes, playerVertex);
            if (ray.length() <= sightRange && !Util.isLineObstructed(ray)) {
                return true;
            }
        }
        return false;
    }

}
