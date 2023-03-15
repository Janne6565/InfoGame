package lethalhabit.game;

import lethalhabit.ui.Animation;

public enum DamageType {
    
    /**
     * Standard damage, e.g. melee or ranged attacks
     */
    STANDARD(Animation.EMPTY),
    /**
     * Fire damage, e.g. fireballs or lava
     */
    FIRE(Animation.EMPTY),
    /**
     * Poison damage
     */
    POISON(Animation.EMPTY),
    /**
     * Water damage, e.g. drowning
     */
    WATER(Animation.EMPTY),
    /**
     * Magic damage
     */
    MAGIC(Animation.EMPTY),
    /**
     * Electricity damage
     */
    ELECTRICITY(Animation.EMPTY);
    
    /**
     * Animation that plays when this particular type of damage is dealt
     */
    public final Animation animation; // TODO: animations for damage types
    
    DamageType(Animation animation) {
        this.animation = animation;
    }
    
}
