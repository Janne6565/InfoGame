package lethalhabit.game.enemy;

import lethalhabit.Main;
import lethalhabit.math.Hitbox;
import lethalhabit.math.LineSegment;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.Animation;
import lethalhabit.util.Util;

import java.util.Random;

public class Slipknot extends Enemy{
    public static final int WIDTH = 33;
    public static final Hitbox HITBOX = new Hitbox(
            new Point(18, 14).scale(WIDTH / 50.0),
            new Point(18, 42).scale(WIDTH / 50.0),
            new Point(36, 42).scale(WIDTH / 50.0),
            new Point(36, 14).scale(WIDTH / 50.0)
    );

    public static final int JUMP_BOOST = 250;
    public static final int ATTACK_RANGE = 150;

    private double jumpCooldown = 0;
    private double attackCooldown = 0;
    public Slipknot(Point position) {
        super(WIDTH, Animation.PLAYER_IDLE_LEFT, position, HITBOX, new Point(9, 6), 270, 40);
        Util.registerHittable(this);
    }

    public void tick(Double timeDelta) {
        super.tick(timeDelta);
        if(canSeePlayer() && Main.mainCharacter.position.distance(position) <= ATTACK_RANGE){
            attack(timeDelta);
        }
    }

    protected boolean canSeePlayer() {
        Point absoluteEyes = position.plus(eyePosition);
        for (Point playerVertex : Main.mainCharacter.hitbox.shift(Main.mainCharacter.position)) {
            LineSegment ray = new LineSegment(absoluteEyes, playerVertex);
            if (ray.length() <= sightRange && !Util.isLineObstructed(ray)) {
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

    public Animation getAnimation() {
        return switch (direction) {
            case NONE -> Animation.PLAYER_IDLE_LEFT;
            case LEFT -> Animation.PLAYER_WALK_LEFT;
            case RIGHT -> Animation.PLAYER_WALK_RIGHT;
        };
    }
}
