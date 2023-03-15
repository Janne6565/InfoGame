package lethalhabit.game.enemy;

import lethalhabit.Main;
import lethalhabit.game.Entity;
import lethalhabit.game.Tickable;
import lethalhabit.math.Hitbox;
import lethalhabit.math.LineSegment;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.Animation;
import lethalhabit.util.Util;

import java.util.Random;

public class Slipknot extends Enemy {
    
    /**
     * Every slipknot is 33 units wide
     */
    public static final int WIDTH = 33;
    
    /**
     * Quadrilateral hitbox of a slipknot, auto-scaled to its size
     */
    public static final Hitbox HITBOX = new Hitbox(
        new Point(18, 14).scale(WIDTH / 50.0),
        new Point(18, 42).scale(WIDTH / 50.0),
        new Point(36, 42).scale(WIDTH / 50.0),
        new Point(36, 14).scale(WIDTH / 50.0)
    );
    
    /**
     * The distance (to the player) a slipknot must have in order to attack
     */
    public static final int ATTACK_RANGE = 150;
    
    /**
     * Cooldown until this slipknot can attack again
     */
    private double attackCooldown = 0;
    
    /**
     * Constructs a new slipknot at the specified position
     * @param position absolute position for the slipknot to be instantiated at
     */
    public Slipknot(Point position) {
        super(WIDTH, position, HITBOX, new Point(9, 6), 270, 40);
    }
    
    /**
     * @see Tickable#tick(Double)
     */
    public void tick(Double timeDelta) {
        super.tick(timeDelta);
        attackCooldown -= timeDelta;
        if (canSeePlayer() && Main.mainCharacter.position.distance(position) <= ATTACK_RANGE) {
            attack(timeDelta);
        }
    }
    
    /**
     * Attacks the player, dealing exactly 1 damage
     * @param timeDelta time since last tick (in seconds)
     */
    private void attack(double timeDelta) {
        if (attackCooldown <= 0) {
            System.out.println("ATTACKING PLAYER");
            Main.mainCharacter.hp -= 1;
            System.out.println("PLAYER HP = " + Main.mainCharacter.hp);
        }
        attackCooldown = 2;
    }
    
    /**
     * @see Entity#getAnimation()
     */
    public Animation getAnimation() {
        return switch (direction) {
            case NONE -> Animation.PLAYER_IDLE_LEFT;
            case LEFT -> Animation.PLAYER_WALK_LEFT;
            case RIGHT -> Animation.PLAYER_WALK_RIGHT;
        };
    }
    
}
