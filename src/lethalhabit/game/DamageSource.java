package lethalhabit.game;

/**
 * A class that describes a particular damage source: <br>
 * - Entity (if there is one) the damage originated from <br>
 * - Type of damage <br>
 * - Damage amount <br>
 * - Knockback amount
 */
public final class DamageSource {
    
    /**
     * The source entity of the damage (<code>null</code> if there is none)
     */
    public final Entity source;
    
    /**
     * The specific type of the damage (standard, fire, poison, water, magic or electricity)
     */
    public final DamageType type;
    
    /**
     * The absolute amount of the damage
     */
    public final double damage;
    
    /**
     * The absolute knockback amount of the damage
     */
    public final double knockback;
    
    /**
     * Creates a new damage source with the given parameters <br>
     * (private access to only allow creation of damage sources that actually exist in the game)
     * @param source source entity
     * @param type damage type
     * @param damage damage amount
     * @param knockback knockback amount
     */
    public DamageSource(Entity source, DamageType type, double damage, double knockback) {
        this.source = source;
        this.type = type;
        this.damage = damage;
        this.knockback = knockback;
    }
    
    /**
     * Standard damage, e.g. melee or ranged attacks
     * @param source source entity
     * @param damage damage amount
     * @return a new standard damage source with the given source entity and damage amount
     */
    public static DamageSource standard(Entity source, double damage) {
        return new DamageSource(source, DamageType.STANDARD, damage, 200);
    }
    
    /**
     * Fire damage, e.g. fireballs or lava
     * @param source source entity
     * @param damage damage amount
     * @return a new fire damage source with the given source entity and damage amount
     */
    public static DamageSource fire(Entity source, double damage) {
        return new DamageSource(source, DamageType.FIRE, damage, 0);
    }
    
    /**
     * Poison damage
     * @param source source entity
     * @param damage damage amount
     * @return a new poison damage source with the given source entity and damage amount
     */
    public static DamageSource poison(Entity source, double damage) {
        return new DamageSource(source, DamageType.POISON, damage, 0);
    }
    
    /**
     * Water damage, e.g. drowning
     * @param source source entity
     * @param damage damage amount
     * @return a new water damage source with the given source entity and damage amount
     */
    public static DamageSource water(Entity source, double damage) {
        return new DamageSource(source, DamageType.WATER, damage, 0);
    }
    
    /**
     * Magic damage
     * @param source source entity
     * @param damage damage amount
     * @return a new magic damage source with the given source entity and damage amount
     */
    public static DamageSource magic(Entity source, double damage) {
        return new DamageSource(source, DamageType.MAGIC, damage, 0);
    }
    
    /**
     * Electricity damage
     * @param source source entity
     * @param damage damage amount
     * @return a new electricity damage source with the given source entity and damage amount
     */
    public static DamageSource electricity(Entity source, double damage) {
        return new DamageSource(source, DamageType.ELECTRICITY, damage, 0);
    }
    
}
