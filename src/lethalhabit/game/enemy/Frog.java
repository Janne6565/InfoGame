package lethalhabit.game.enemy;

import lethalhabit.Main;
import lethalhabit.math.*;
import lethalhabit.game.*;
import lethalhabit.ui.*;
import lethalhabit.util.Util;

import java.awt.*;
import java.util.Random;

/**
 * An enemy that follows the player by jumping and deals some damage
 */
public class Frog extends Enemy {
    
    /**
     * Every frog is 33 units wide
     */
    public static final int WIDTH = 33;

    /**
     * Quadrilateral hitbox of a frog, auto-scaled to its size
     */
    public static final Hitbox HITBOX = new Hitbox(
            new Point(18, 14).scale(WIDTH / 50.0),
            new Point(18, 42).scale(WIDTH / 50.0),
            new Point(36, 42).scale(WIDTH / 50.0),
            new Point(36, 14).scale(WIDTH / 50.0)
    );
    
    /**
     * On every jump, a frog gains +250 up-velocity (negative y-velocity)
     */
    public static final int JUMP_BOOST = 250;

    /**
     * The distance (to the player) a frog must have in order to attack
     */
    public static final int ATTACK_RANGE = 20;
    
    /**
     * Cooldown until this frog can jump again
     */
    private double jumpCooldown = 0;

    /**
     * Cooldown until this frog can attack again
     */
    private double attackCooldown = 0;
    
    /**
     * Constructs a new frog at the specified position
     * 
     * @param position Absolute position for the frog to be instantiated at
     */
    public Frog(Point position) {
        super(WIDTH, position, HITBOX, new Point(9, 6), 270, 40);
        Util.registerHittable(this);
    }
    
    /**
     * @see Tickable#tick(Double)
     */
    @Override
    public void tick(Double timeDelta) {
        super.tick(timeDelta);
        jumpCooldown = Math.max(0, jumpCooldown - timeDelta);
        attackCooldown = Math.max(0, attackCooldown - timeDelta);
        if (jumpCooldown > 1.5 && canSeePlayer()) {
            jumpCooldown = 1.5;
        }
        //Movement
        if (canSeePlayer() && jumpCooldown == 0 && isWallDown()) {
            if (Main.mainCharacter.position.x() < position.x() && isWallDown()) {
                velocity = new Vec2D(-speed, -JUMP_BOOST);
                jumpCooldown = 1.5;
            } else if (Main.mainCharacter.position.x() > position.x() && isWallDown()) {
                velocity = new Vec2D(speed, -JUMP_BOOST);
                jumpCooldown = 1.5;
            }
            //random Movement if Frog !canSeePlayer but is in sightRange
        } else if (Main.mainCharacter.position.distance(position) < sightRange && jumpCooldown == 0 && isWallDown()) {
            int ran = new Random().nextInt(3);
            if (ran == 0) {
                velocity = new Vec2D(speed, -JUMP_BOOST);
                if (!canSeePlayer()) {
                    setRandomJumpCooldown();
                } else {
                    jumpCooldown = 1.5;
                }
            }
            if (ran == 1) {
                velocity = new Vec2D(-speed, -JUMP_BOOST);
                if (!canSeePlayer()) {
                    setRandomJumpCooldown();
                } else {
                    jumpCooldown = 1.5;
                }
            }
            if (ran == 2) {
                velocity = new Vec2D(0, -JUMP_BOOST);
                if (!canSeePlayer()) {
                    setRandomJumpCooldown();
                } else {
                    jumpCooldown = 1.5;
                }
            }
        }
        
        if (velocity.y() == 0) {
            velocity = new Vec2D(0, 0);
        }
        
        if (Main.mainCharacter.position.distance(position) < ATTACK_RANGE && attackCooldown == 0) {
            this.attack(timeDelta);
        }
    }
    
    /**
     * @see Drawable#getAnimation()
     */
    @Override
    public Animation getAnimation() {
        return switch (direction) {
            case NONE -> Animation.PLAYER_IDLE_LEFT;
            case LEFT -> Animation.PLAYER_WALK_LEFT;
            case RIGHT -> Animation.PLAYER_WALK_RIGHT;
        };
    }
    
    /**
     * Sets this frog's jump cooldown to a random time span between 1.5 and 5 seconds <br>
     * (Frogs jump rather randomly)
     */
    private void setRandomJumpCooldown() {
        this.jumpCooldown = new Random().nextDouble() * 3.5 + 1.5;
    }
    
    /**
     * @see Hittable#getHitbox()
     */
    @Override
    public Hitbox getHitbox() {
        return hitbox;
    }
    
    public void onHit() {
        hp -= 1;
        System.out.println(hp);
        if (hp <= 0) {
            despawn();
        }
    }
    
    private void attack(double timeDelta) {
        System.out.println("ATTACKING PLAYER");
        Main.mainCharacter.hp -= 1;
        System.out.println("PLAYER HP = " + Main.mainCharacter.hp);
        attackCooldown = 2;
    }
    
}
