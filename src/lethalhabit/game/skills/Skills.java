package lethalhabit.game.skills;

import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

import java.awt.*;

public final class Skills {
    
    public static Hitbox defaultAttackHitbox = new Hitbox(
            new Point(0, 5),
            new Point(15, 5),
            new Point(15, 25),
            new Point(0, 25)
    );
    
    public static Dimension getDefaultHitDimensions() {
        return new Dimension((int) (defaultAttackHitbox.maxX() - defaultAttackHitbox.minX()), (int) (defaultAttackHitbox.maxY() - defaultAttackHitbox.minY()));
    }
    
    public double attackCooldown = 0.8;
    
    public int doubleJumpAmount = 0;
    public double doubleJumpCooldown = 3;
    
    public boolean canWallJump = false;
    public boolean canWallJumpBothSides = false;
    
    public int dashAmount = 0;
    public double dashCooldown = 5;
    
    public boolean canMakeFireball = false;
    public double fireballCooldown = 5;
    
    public Hitbox attackHitbox = defaultAttackHitbox;
    
}
