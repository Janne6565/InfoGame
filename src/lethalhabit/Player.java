package lethalhabit;

import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.GraphicModule;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends PhysicsObject{

    public double movementSpeed;
    public double jumpBoost;

    public Player(float width, String pathToImage, Point position, Hitbox hitbox, double movementSpeed, double jumpBoost) {
        super(width, pathToImage, position, hitbox);
        this.movementSpeed = movementSpeed;
        this.jumpBoost = jumpBoost;
        defaultImage = graphic;
    }

    public void moveLeft() {
        this.graphic = GraphicModule.mirrorImage(defaultImage);
        this.velocity = new Vec2D(movementSpeed * -1, this.velocity.y());
    }


    BufferedImage defaultImage;
    public void moveRight() {
        this.graphic = defaultImage;
        this.velocity = new Vec2D(movementSpeed, this.velocity.y());
    }

    public void standStill() {
        this.velocity = new Vec2D(0, this.velocity.y());
    }

    private boolean jumped = false; // this is used to not let you hold your jump key and than jump more than once


    public boolean canJump() {
        return onGround();
    }

    public void jump() {
        if (!jumped) {
            velocity = velocity.minus(0, jumpBoost);
            jumped = true;
        }
    }

    public void resetJump() {
        jumped = false;
    }

    @Override
    void onGroundReset() {

    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
    }
}
