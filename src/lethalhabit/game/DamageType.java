package lethalhabit.game;

import lethalhabit.ui.Animation;

public enum DamageType {
    
    STANDARD(Animation.EMPTY),
    FIRE(Animation.EMPTY),
    POISON(Animation.EMPTY),
    WATER(Animation.EMPTY),
    MAGIC(Animation.EMPTY),
    ELECTRICITY(Animation.EMPTY);
    
    public final Animation animation; // TODO: animations for damage types
    
    DamageType(Animation animation) {
        this.animation = animation;
    }
    
}
