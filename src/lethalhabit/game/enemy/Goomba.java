package lethalhabit.game.enemy;

import lethalhabit.Main;
import lethalhabit.game.DamageSource;
import lethalhabit.game.Entity;
import lethalhabit.game.Hittable;
import lethalhabit.game.Tickable;
import lethalhabit.math.Point;
import lethalhabit.math.*;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Camera;
import lethalhabit.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;

import static lethalhabit.util.Util.mirrorImage;

public class Goomba extends Enemy {
    
    /**
     * Every goomba is 33 units wide
     */
    private static final int WIDTH = 33;
    
    /**
     * Quadrilateral hitbox of a goomba, auto-scaled to its size
     */
    private static final Hitbox HITBOX = new Hitbox(
        new Point(18, 14).scale(WIDTH / 50.0),
        new Point(18, 42).scale(WIDTH / 50.0),
        new Point(36, 42).scale(WIDTH / 50.0),
        new Point(36, 14).scale(WIDTH / 50.0)
    );
    
    /**
     * The distance (to the player) a goomba must have in order to attack
     */
    private static final double ATTACK_RANGE = 10;
    
    /**
     * Cooldown until this goomba can attack again
     */
    private double attackCooldown = 0;
    
    /**
     * Constructs a new goomba at the specified position
     * @param position absolute position for the goomba to be instantiated at
     */
    public Goomba(Point position) {
        super(WIDTH, position, HITBOX, new Point(9, 6), 160, 80);
    }
    
    /**
     * @see Entity#checkViscosity()
     */
    @Override
    public void checkViscosity() {
        super.checkViscosity();
        if (viscosity < 1) {
            onGroundReset();
            viscosity = 0.0;
        }
    }
    
    /**
     * @see Tickable#tick(Double)
     */
    @Override
    public void tick(Double timeDelta) {
        super.tick(timeDelta);
        
        attackCooldown -= timeDelta;
        
        if (!isWallDown()) {
            direction = Direction.NONE;
        }
        
        if (attackCooldown <= 0) {
            if (Main.mainCharacter.position.distance(position) < ATTACK_RANGE) {
                this.velocity = new Vec2D(0, this.velocity.y());
                attack(timeDelta);
                return;
            }
        }
        
        // platform movement
        if (direction != Direction.NONE) {
            
            // movement - left and right
            velocity = switch (direction) {
                case LEFT -> new Vec2D(-speed, velocity.y());
                case RIGHT -> new Vec2D(speed, velocity.y());
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            };
            
            // shifted hitbox check
            Point pointToCheck = switch (direction) {
                case LEFT -> new Point(-Main.TILE_SIZE / 2, 0);
                case RIGHT -> new Point(Main.TILE_SIZE / 2, 0);
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            };
            
            // check if wall is left or right based on direction
            boolean isWallInDirection = switch (direction) {
                case RIGHT -> isWallRight(pointToCheck) || isWallRight();
                case LEFT -> isWallLeft(pointToCheck) || isWallLeft();
                default -> false;
            };
            
            // if there is no wall down of shifted hitbox, or wall is in direction, change direction, else stay same
            if (!isWallDown(pointToCheck) || isWallInDirection) {
                direction = switch (direction) {
                    case LEFT -> Direction.RIGHT;
                    case RIGHT -> Direction.LEFT;
                    default -> throw new IllegalStateException("Unexpected value: " + direction);
                };
                this.lastDirection = direction;
            }
        } else {
            direction = Direction.LEFT;
        }
        
        if (recoil.x() != 0) {
            recoil = recoil.x() < 0 ? new Vec2D(Math.min(recoil.x(), 0), recoil.y()) : new Vec2D(Math.max(recoil.x(), 0), recoil.y());
        }
    }
    
    /**
     * @see Hittable#onHit(DamageSource)
     */
    @Override
    public void onHit(DamageSource source) {
        this.hp -= 1;
        
        if (source.source.position.x() > position.x()) {
            knockback(-source.knockback);
        } else {
            knockback(source.knockback);
        }
        
        if (hp <= 0) {
            despawn();
        }
        System.out.println("Enemy hp: " + this.hp);
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
            attackCooldown = 2;
        }
        attackCooldown -= timeDelta;
    }
    
    /**
     * @see Entity#getAnimation()
     */
    @Override
    public Animation getAnimation() {
        return switch (direction) {
            case NONE -> Animation.PLAYER_IDLE_LEFT;
            case LEFT -> Animation.PLAYER_WALK_LEFT;
            case RIGHT -> Animation.PLAYER_WALK_RIGHT;
        };
    }
    
}
