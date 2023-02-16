package lethalhabit.game.enemy;

import lethalhabit.Main;
import lethalhabit.game.Entity;
import lethalhabit.math.Hitbox;
import lethalhabit.math.LineSegment;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Camera;
import lethalhabit.util.Util;

import java.awt.*;
import java.util.Random;

public class Frog extends Entity {
    public static final int WIDTH = 33;
    public static final Hitbox HITBOX = new Hitbox(new Point[]{
            new Point(18, 14).scale(WIDTH / 50.0),
            new Point(18, 42).scale(WIDTH / 50.0),
            new Point(36, 42).scale(WIDTH / 50.0),
            new Point(36, 14).scale(WIDTH / 50.0)
    });
    public static final int MOVEMENT_SPEED = 40;
    public static final int JUMP_BOOST = 250;
    public double JUMP_COOLDOWN = 0;
    public static final int ATTACK_RANGE = 20;
    private double attackCooldown = 0;
    public static final int SIGHT_RANGE = 270;

    public static final Point relativeEyes = new Point(9, 6);

    public Frog(Point position) {
        super(WIDTH, Animation.PLAYER_IDLE.get(0), position, HITBOX);
    }

    public void tick(Double timeDelta) {
        super.tick(timeDelta);
        JUMP_COOLDOWN = Math.max(0, JUMP_COOLDOWN - timeDelta);
        attackCooldown = Math.max(0, attackCooldown - timeDelta);
        //Movement
        if (canSeePlayer() && JUMP_COOLDOWN == 0 && isWallDown()) {
            if (Main.mainCharacter.position.x() < position.x() && isWallDown()) {
                velocity = new Vec2D(-MOVEMENT_SPEED, -JUMP_BOOST);
                JUMP_COOLDOWN = 1.5;
            } else if (Main.mainCharacter.position.x() > position.x() && isWallDown()) {
                velocity = new Vec2D(MOVEMENT_SPEED, -JUMP_BOOST);
                JUMP_COOLDOWN = 1.5;
            }
        //random Movement if Frog !canSeePlayer but is in SIGHT_RANGE
        } else if (Main.mainCharacter.position.distance(position) < SIGHT_RANGE && JUMP_COOLDOWN == 0 && isWallDown()) {
            int ran = new Random().nextInt(3);
            if (ran == 0) {
                velocity = new Vec2D(MOVEMENT_SPEED, -JUMP_BOOST);
                if (!canSeePlayer()){setRandomJUMP_COOLDOWN();} else { JUMP_COOLDOWN = 1.5;}
            }
            if (ran == 1) {
                velocity = new Vec2D(-MOVEMENT_SPEED, -JUMP_BOOST);
                if (!canSeePlayer()){setRandomJUMP_COOLDOWN();} else { JUMP_COOLDOWN = 1.5;}
            }
            if (ran == 2) {
                velocity = new Vec2D(0, -JUMP_BOOST);
                if (!canSeePlayer()){setRandomJUMP_COOLDOWN();} else { JUMP_COOLDOWN = 1.5;}
            }
        }
        if(velocity.y() == 0) {
            velocity = new Vec2D(0, 0);
        }

        if(Main.mainCharacter.position.distance(position) < ATTACK_RANGE && attackCooldown == 0) {
            this.attack(timeDelta);
        }
    }
    
    private void setRandomJUMP_COOLDOWN(){
        this.JUMP_COOLDOWN = new Random().nextInt(100)/10;
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
        System.out.println("ATTACKING PLAYER");
        Main.mainCharacter.hp -= 1;
        System.out.println("PLAYER HP = " + Main.mainCharacter.hp);
        attackCooldown = 2;
    }

    private void die() {
        Main.entities.remove(this);
        Main.drawables.remove(this);
        Main.tickables.remove(this);
        Main.eventAreas.remove(this);
    }

    @Override
    public int layer() {
        return Camera.LAYER_GAME;
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        if (Main.DEBUG_HITBOX) {
            Point absoluteEyes = position.plus(relativeEyes);
            for (Point playerVertex : Main.mainCharacter.hitbox.shift(Main.mainCharacter.position)) {
                LineSegment ray = new LineSegment(absoluteEyes, playerVertex);
                Util.drawLineSegment(graphics, ray);
            }
        }
    }
}
