package lethalhabit.game;

public final class DamageSource {
    
    public final Entity source;
    public final DamageType type;
    public final double damage;
    public final double knockback;
    
    private DamageSource(Entity source, DamageType type, double damage, double knockback) {
        this.source = source;
        this.type = type;
        this.damage = damage;
        this.knockback = knockback;
    }
    
    public static DamageSource standard(Entity source, double damage) {
        return new DamageSource(source, DamageType.STANDARD, damage, 200);
    }
    
    public static DamageSource fire(Entity source, double damage) {
        return new DamageSource(source, DamageType.FIRE, damage, 0);
    }
    
    public static DamageSource poison(Entity source, double damage) {
        return new DamageSource(source, DamageType.POISON, damage, 0);
    }
    
    public static DamageSource water(Entity source, double damage) {
        return new DamageSource(source, DamageType.WATER, damage, 0);
    }
    
    public static DamageSource magic(Entity source, double damage) {
        return new DamageSource(source, DamageType.MAGIC, damage, 0);
    }
    
    public static DamageSource electricity(Entity source, double damage) {
        return new DamageSource(source, DamageType.ELECTRICITY, damage, 0);
    }
    
}
