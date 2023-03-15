package lethalhabit.game.skills;

import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

import java.awt.*;

/**
 * A class describing the skills and abilities of a player (or enemy, unused tho)
 */
public final class Skills {
    
    /**
     * Default hitbox of a standard melee attack (level 0)
     */
    public static final Hitbox DEFAULT_ATTACK_HITBOX = new Hitbox(
            new Point(0, 5),
            new Point(15, 5),
            new Point(15, 25),
            new Point(0, 25)
    );
    
    /**
     * @return the outer size of a standard level-0 melee attack
     */
    public static Dimension getDefaultHitDimensions() {
        return new Dimension((int) (DEFAULT_ATTACK_HITBOX.maxX() - DEFAULT_ATTACK_HITBOX.minX()), (int) (DEFAULT_ATTACK_HITBOX.maxY() - DEFAULT_ATTACK_HITBOX.minY()));
    }
    
    /**
     * Cooldown between attacks
     */
    public double attackCooldown = 0.8;
    
    /**
     * Amount of jumps that can be performed while not on the ground
     */
    public int doubleJumpAmount = 0;
    
    /**
     * Cooldown between double jumps
     */
    public double doubleJumpCooldown = 3;
    
    /**
     * <code>true</code> if wall jumps are possible (on one side), <code>false</code> if the ability is not unlocked
     */
    public boolean canWallJump = false;
    
    /**
     * <code>true</code> if wall jumps are possible (one wall jump per side), <code>false</code> if the ability is not unlocked
     */
    public boolean canWallJumpBothSides = false;
    
    /**
     * Amount of dashes that can be performed consecutively
     */
    public int dashAmount = 0;
    
    /**
     * Cooldown between dashes
     */
    public double dashCooldown = 5;
    
    /**
     * <code>true</code> if the ability to fire fireballs is unlocked, <code>false</code> otherwise
     */
    public boolean canMakeFireball = false;
    
    /**
     * Cooldown between fireball attacks
     */
    public double fireballCooldown = 5;
    
    /**
     * Hitbox of a standard melee attack
     */
    public Hitbox attackHitbox = DEFAULT_ATTACK_HITBOX;
    
}
