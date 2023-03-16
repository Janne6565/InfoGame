package lethalhabit.game;

import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

/**
 * An object that has a hitbox and can be hit (reacts to attack hitboxes and damage sources).
 */
public interface Hittable {
    
    /**
     * @return The relative hitbox of the hittable object
     */
    public Hitbox getHitbox();
    
    /**
     * @return The absolute position of the object
     */
    public Point getPosition();
    
    /**
     * Handles attacks and damage taken by the hittable object
     *
     * @param source The source of the attack/damage
     */
    public void onHit(DamageSource source);
    
}
