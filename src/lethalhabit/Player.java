package lethalhabit;

import lethalhabit.math.Hitbox;
import lethalhabit.math.LineSegment;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.Animation;
import lethalhabit.ui.GraphicModule;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends PhysicsObject{

    public double movementSpeed;
    public double jumpBoost;
    public double timeInGame = 0; // Used to calculate the current frame of the animation


    /* Animations */
    public Animation idleAnimation = new Animation(0.0416, "playerIdle", 48);
    public Animation walkAnimation; // TODO: Walk Animation
    public Animation midAirAnimation; // TODO: Mid Air Animation

    public Animation currentAnimation = idleAnimation;

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
        this.currentAnimation = idleAnimation;
        this.graphic = defaultImage;
    }

    private boolean jumped = false; // this is used to not let you hold your jump key and than jump more than once


    private int timesJumped = 0;

    public boolean canJump() {
        if (timesJumped <= 0) {
            return true;
        }
        return isWallDown();
    }

    public void jump() {
        if (!jumped) {
            velocity = new Vec2D( velocity.x(), -jumpBoost);
            timesJumped += 1;
            jumped = true;
        }
    }

    public void resetJump() {
        jumped = false;
    }

    @Override
    public void tick(float timeDelta) {
        timeInGame += timeDelta;
        int currentFrame = (int) ((timeInGame % currentAnimation.animationTime) / currentAnimation.frameTime);
        defaultImage = currentAnimation.frames.get(currentFrame);
        super.tick(timeDelta);
    }

    @Override
    public void onGroundReset() {
        timesJumped = 0;
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        if (Main.debugHitbox) {
            for (LineSegment line : hitbox.shiftAll(position).edges()) {
                Point positionA = convertPositionToCamera(line.a());
                Point positionB = convertPositionToCamera(line.b());
                Graphics2D g2 = (Graphics2D) graphics;
                g2.setColor(Main.strokeColorPlayer);
                g2.setStroke(new BasicStroke(Main.strokeSize));
                g2.drawLine((int) positionA.x(), (int) positionA.y(), (int) positionB.x(), (int) positionB.y());
            }
        }
    }

    private Point convertPositionToCamera(Point position) {
        double pixelPerPixel = (double) Main.screenWidth / (double) Main.getScreenWidthGame();
        double offsetX = relative ? Main.camera.position.x() : 0;
        double offsetY = relative ? Main.camera.position.y() : 0;
        int posXDisplay = (int) ((int) (position.x() - offsetX) * pixelPerPixel + (Main.screenWidth / 2));
        int posYDisplay = (int) ((int) (position.y() - offsetY) * pixelPerPixel + (Main.screenHeight / 2));
        return new Point(posXDisplay, posYDisplay);
    }
}
