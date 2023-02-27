package lethalhabit.game.enemy;

import lethalhabit.Main;
import lethalhabit.game.DamageSource;
import lethalhabit.game.Entity;
import lethalhabit.game.Hittable;
import lethalhabit.math.Hitbox;
import lethalhabit.math.LineSegment;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Camera;
import lethalhabit.util.Util;

import java.awt.*;

public abstract class Enemy extends Entity implements Hittable {
    
    public final Animation animation;
    
    protected final Point eyePosition;
    protected final double sightRange;
    protected final double speed;
    
    public int hp = 10;
    
    protected Enemy(double width, Animation animation, Point position, Hitbox hitbox, Point eyePosition, double sightRange, double speed) {
        super(width, animation.get(0), position, hitbox);
        this.animation = animation;
        this.eyePosition = eyePosition;
        this.sightRange = sightRange;
        this.speed = speed;
    }

    @Override
    public void despawn() {
        super.despawn();
        Util.removeHittable(this);
    }

    @Override
    public Hitbox getHitbox() {
        return hitbox;
    }
    
    @Override
    public int layer() {
        return Camera.LAYER_GAME;
    }
    
    @Override
    public void onHit(DamageSource source) {

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

    @Override
    public void changeTiles(Hitbox hitboxBefore, Hitbox hitboxAfter) {
        Util.removeHittable(this, hitboxBefore);
        Util.registerHittable(this);
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

    @Override
    public void onCrashDown(Vec2D velocity) {

    }

    @Override
    public void onCrashLeft(Vec2D velocity) {

    }

    @Override
    public void onCrashRight(Vec2D velocity) {

    }

    @Override
    public void onCrashUp(Vec2D velocity) {

    }
}
