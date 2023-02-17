package lethalhabit.game;

public final class DamageSource {
    
    public final Entity source;
    public final DamageType type;
    public final double damage;
    
    private DamageSource(Entity source, DamageType type, double damage) {
        this.source = source;
        this.type = type;
        this.damage = damage;
    }
    
    public static DamageSource standard(Entity source, double damage) {
        return new DamageSource(source, DamageType.STANDARD, damage);
    }
    
    public static DamageSource fire(Entity source, double damage) {
        return new DamageSource(source, DamageType.FIRE, damage);
    }
    
    public static DamageSource poison(Entity source, double damage) {
        return new DamageSource(source, DamageType.POISON, damage);
    }
    
    public static DamageSource water(Entity source, double damage) {
        return new DamageSource(source, DamageType.WATER, damage);
    }
    
    public static DamageSource magic(Entity source, double damage) {
        return new DamageSource(source, DamageType.MAGIC, damage);
    }
    
    public static DamageSource electricity(Entity source, double damage) {
        return new DamageSource(source, DamageType.ELECTRICITY, damage);
    }
    
}
