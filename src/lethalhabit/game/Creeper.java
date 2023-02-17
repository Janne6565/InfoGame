package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.game.enemy.Enemy;
import lethalhabit.math.LineSegment;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Camera;
import lethalhabit.util.Util;

import java.awt.*;
import java.util.Random;

public class Creeper extends Enemy {
    
    public static final int WIDTH = 20;
    public static final int JUMP_BOOST = 150;
    public static final int ATTACK_RANGE = 10;
    
    private double attackCooldown = 0;
    
    public Creeper(Point position) {
        super(WIDTH, Animation.PLAYER_IDLE, position, Player.HITBOX, new Point(9, 6), 160, 30);
    }
    
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
                if (isWallLeft() && isWallDown() && !isSubmerged()) {
                    velocity = new Vec2D(velocity.x(), -JUMP_BOOST);
                }
            } else if (Main.mainCharacter.position.x() > position.x()) {
                velocity = new Vec2D(speed, velocity.y());
                if (isWallRight() && isWallDown() && !isSubmerged()) {
                    velocity = new Vec2D(velocity.x(), -JUMP_BOOST);
                }
            } else {
                velocity = new Vec2D(0, velocity.y());
            }
        }
    }
    
    @Override
    public Animation getAnimation() {
        return Animation.PLAYER_IDLE;
    }
    
    private void attack(double timeDelta) {
        if (attackCooldown <= 0) {
            System.out.println("ATTACKING PLAYER");
            Main.mainCharacter.hp -= 1;
            System.out.println("PLAYER HP = " + Main.mainCharacter.hp);
            despawn();
        }
        attackCooldown -= timeDelta;
    }
    
    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        if (Main.DEBUG_HITBOX) {
            Point absoluteEyes = position.plus(eyePosition);
            for (Point playerVertex : Main.mainCharacter.hitbox.shift(Main.mainCharacter.position)) {
                LineSegment ray = new LineSegment(absoluteEyes, playerVertex);
                Util.drawLineSegment(graphics, ray);
            }
        }
    }
    
}
