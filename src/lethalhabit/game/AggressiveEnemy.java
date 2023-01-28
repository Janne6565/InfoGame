package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.PhysicsObject;
import lethalhabit.Player;
import lethalhabit.technical.LineSegment;
import lethalhabit.technical.Point;
import lethalhabit.technical.Vec2D;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Camera;
import lethalhabit.util.Util;

import java.awt.*;
import java.util.Random;

public class AggressiveEnemy extends PhysicsObject {
    
    public static final int WIDTH = 20;
    public static final int MOVEMENT_SPEED = 30;
    public static final int JUMP_BOOST = 150;
    public static final int ATTACK_RANGE = 3;
    public static final int SIGHT_RANGE = 160;
    
    public static final Point relativeEyes = new Point(9, 6);
    
    private double attackCooldown = 0;
    
    public AggressiveEnemy(Point position) {
        super(WIDTH, Animation.PLAYER_IDLE.get(0), position, Player.HITBOX);
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
                velocity = new Vec2D(-MOVEMENT_SPEED, velocity.y());
                if (isWallLeft() && isWallDown() && !isSubmerged()) {
                    velocity = new Vec2D(velocity.x(), -JUMP_BOOST);
                }
            } else if (Main.mainCharacter.position.x() > position.x()) {
                velocity = new Vec2D(MOVEMENT_SPEED, velocity.y());
                if (isWallRight() && isWallDown() && !isSubmerged()) {
                    velocity = new Vec2D(velocity.x(), -JUMP_BOOST);
                }
            } else {
                velocity = new Vec2D(0, velocity.y());
            }
        } else {
            // random movement
            if (new Random().nextInt(1000) < 10) {
                velocity = new Vec2D(-velocity.x(), velocity.y());
                return;
            }
            if (new Random().nextInt(1000) < (velocity.x() == 0 ? 40 : 100)) {
                velocity = new Vec2D(velocity.x() == 0 ? (new Random().nextBoolean() ? MOVEMENT_SPEED : -MOVEMENT_SPEED) : 0, velocity.y());
                return;
            }
            if ((isWallLeft() || isWallRight()) && isWallDown() && !isSubmerged()) {
                if (new Random().nextInt(1000) < 100) {
                    velocity = new Vec2D(velocity.x(), -JUMP_BOOST);
                } else {
                    velocity = new Vec2D(new Random().nextBoolean() ? 0 : -velocity.x(), velocity.y());
                }
            }
        }
    }
    
    private boolean canSeePlayer() {
        Point absoluteEyes = position.plus(relativeEyes);
        for (Point playerVertex : Main.mainCharacter.hitbox.shift(Main.mainCharacter.position)) {
            LineSegment ray = new LineSegment(absoluteEyes, playerVertex);
            if (ray.length() <= SIGHT_RANGE && !Util.isLineObstructed(ray)) {
                return true;
            }
        }
        return false;
    }
    
    private void attack(double timeDelta) {
        if (attackCooldown <= 0) {
            System.out.println("ATTACKING PLAYER");
            attackCooldown = 2;
        }
        attackCooldown -= timeDelta;
    }
    
    @Override
    public int layer() {
        return Camera.LAYER_GAME;
    }
    
    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        Point absoluteEyes = position.plus(relativeEyes);
        for (Point playerVertex : Main.mainCharacter.hitbox.shift(Main.mainCharacter.position)) {
            LineSegment ray = new LineSegment(absoluteEyes, playerVertex);
            Util.drawLineSegment(graphics, ray);
        }
    }
    
}
