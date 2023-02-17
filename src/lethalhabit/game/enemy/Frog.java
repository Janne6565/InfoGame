package lethalhabit.game.enemy;

import lethalhabit.Main;
import lethalhabit.math.Hitbox;
import lethalhabit.math.LineSegment;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Camera;
import lethalhabit.util.Util;

import java.awt.*;
import java.util.Random;

public class Frog extends Enemy {
    
    public static final int WIDTH = 33;
    public static final Hitbox HITBOX = new Hitbox(
            new Point(18, 14).scale(WIDTH / 50.0),
            new Point(18, 42).scale(WIDTH / 50.0),
            new Point(36, 42).scale(WIDTH / 50.0),
            new Point(36, 14).scale(WIDTH / 50.0)
    );
    
    public static final int JUMP_BOOST = 250;
    public static final int ATTACK_RANGE = 20;
    
    private double jumpCooldown = 0;
    private double attackCooldown = 0;
    
    public Frog(Point position) {
        super(WIDTH, Animation.PLAYER_IDLE, position, HITBOX, new Point(9, 6), 270, 40);
        Util.registerHittable(this);
    }
    
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
    
    @Override
    public Animation getAnimation() {
        return switch (direction) {
            case NONE -> Animation.PLAYER_IDLE;
            case LEFT -> Animation.PLAYER_WALK_LEFT;
            case RIGHT -> Animation.PLAYER_WALK_RIGHT;
        };
    }
    
    private void setRandomJumpCooldown() {
        this.jumpCooldown = new Random().nextInt(100) / 10;
    }
    
    @Override
    public void changeTiles(Hitbox hitboxBefore, Hitbox hitboxAfter) {
        Util.removeHittable(this, hitboxBefore);
        Util.registerHittable(this);
    }
    
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
