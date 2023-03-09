package lethalhabit.game.enemy;

import lethalhabit.Main;
import lethalhabit.math.*;
import lethalhabit.game.*;
import lethalhabit.math.Point;
import lethalhabit.ui.*;
import lethalhabit.util.Util;

import java.awt.*;
import java.util.Random;

/**
 * An enemy that charges at the player and kills itself to deal damage
 */
public class Creeper extends Enemy {
    
    /**
     * Every creeper is 20 units wide
     */
    public static final int WIDTH = 20;

    /**
     * The distance (to the player) a creeper must have in order to attack
     */
    public static final int ATTACK_RANGE = 10;
    
    /**
     * Constructs a new creeper at the specified position
     * 
     * @param position Absolute position for the creeper to be instantiated at
     */
    public Creeper(Point position) {
        super(WIDTH, position, Player.HITBOX, new Point(9, 6),160, 9);
    }
    
    /**
     * @see Tickable#tick(Double)
     */
    @Override
    public void tick(Double timeDelta) {
        super.tick(timeDelta);
        if (canSeePlayer()) {
            if (Main.mainCharacter.position.distance(position) < ATTACK_RANGE) {
                this.velocity = new Vec2D(0, this.velocity.y());
                attack(timeDelta);
                return;
            }
            if (Main.mainCharacter.position.x() < position.x()) {
                velocity = new Vec2D(-speed, velocity.y());
            } else if (Main.mainCharacter.position.x() > position.x()) {
                velocity = new Vec2D(speed, velocity.y());
            } else {
                velocity = new Vec2D(0, velocity.y());
            }
        }
    }
    
    /**
     * @see Entity#getAnimation()
     */
    @Override
    public Animation getAnimation() {
        return Animation.PLAYER_IDLE_LEFT;
    }
    
    /**
     * Attacks a player by dealing a fixed amount of damage, then despawns.
     */
    private void attack(double timeDelta) {
        System.out.println("ATTACKING PLAYER");
        Main.mainCharacter.hp -= 1;
        System.out.println("PLAYER HP = " + Main.mainCharacter.hp);
        despawn();
    }
    
}
