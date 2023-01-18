package lethalhabit;

import lethalhabit.game.Fireball;
import lethalhabit.technical.*;
import lethalhabit.technical.Point;
import lethalhabit.ui.Animation;

import java.awt.*;
import java.awt.image.BufferedImage;

import static lethalhabit.Util.mirrorImage;

public class Player extends PhysicsObject implements Loadable {

    public double movementSpeed;
    public double jumpBoost;
    public double wallJumpBoost = 150;
    public double timeInGame = 0; // Used to calculate the current frame of the animation
    public double cooldownCameraShift = 0.5;
    public double speedOfAnimation = 5;

    public int hp = 10;
    
    /* Animations */
    public Animation idleAnimation;
    public Animation walkAnimation; // TODO: Walk Animation
    public Animation midAirAnimation; // TODO: Mid Air Animation
    public Animation currentAnimation = idleAnimation;
    public int direction = 0;
    BufferedImage defaultImage;
    public Vec2D recoil = new Vec2D(0, 0);

    @Override
    public void load() {
        idleAnimation = new Animation(0.0416, "playerIdle", width * Main.scaledPixelSize());
        midAirAnimation = new Animation(1, "playerWalk", width * Main.scaledPixelSize());
    }

    public Player(double width, String pathToImage, Point position, Hitbox hitbox, double movementSpeed, double jumpBoost) {
        super(width, pathToImage, position, hitbox);
        this.movementSpeed = movementSpeed;
        this.jumpBoost = jumpBoost;
        this.defaultImage = graphic;
        load();

        Main.tickables.add(this);
        Main.loadables.add(this);
    }

    public void moveLeft() {
        this.velocity = new Vec2D(movementSpeed * -1, this.velocity.y());
        this.direction = -1;
    }

    public void moveRight() {
        this.velocity = new Vec2D(movementSpeed, this.velocity.y());
        this.direction = 1;
    }

    public void standStill() {
        this.velocity = new Vec2D(0, this.velocity.y());
        if (velocity.y() == 0) {
            this.currentAnimation = idleAnimation;
        }
    }



    @Override
    public Vec2D getVelocity() {
        return velocity.plus(recoil);
    }

    private boolean jumped = false; // this is used to not let you hold your jump key and than jump more than once
    private int timesJumped = 0;

    public void makeFireball() {
        new Fireball(this.position, direction);
    }

    public boolean canJump() {
        if (timesJumped <= 0) {
            return true;
        }
        return (isWallDown());
    }

    private boolean hasJumpedLeft = false;
    private boolean hasJumpedRight = false;

    public void jump() {
        if (!jumped) {
            jumped = true;
            if (isWallDown()) {
                velocity = new Vec2D(velocity.x(), -jumpBoost);
                return;
            }
            if (direction == -1 && !hasJumpedLeft && isWallLeft()) {
                hasJumpedLeft = true;
                velocity = new Vec2D(velocity.x(), -jumpBoost);
                recoil = new Vec2D(wallJumpBoost, 0);
                return;
            } else if (direction == 1 && !hasJumpedRight && isWallRight()) {
                hasJumpedRight = true;
                velocity = new Vec2D(velocity.x(), -jumpBoost);
                recoil = new Vec2D(-wallJumpBoost, 0);
                return;
            }

            if (canJump()) {
                velocity = new Vec2D(velocity.x(), -jumpBoost);
                if (!isWallDown()) {
                    timesJumped += 1;
                }
            }
        }
    }

    public void resetJump() {
        jumped = false;
    }

    double moveCameraDownCooldown = cooldownCameraShift;
    public void moveCameraDown(double timeDelta) {
        if (moveCameraDownCooldown > 0) {
            moveCameraDownCooldown = Math.max(moveCameraDownCooldown - timeDelta, 0);
        } else {
            System.out.println(Main.camera.shift.y() - Main.camera.speed * timeDelta);
            Main.camera.shift = new Point(Main.camera.shift.x(), Math.max(Main.camera.shift.y() - Math.abs(-Main.camera.shiftLimit - Main.camera.shift.y()) * speedOfAnimation * timeDelta, -Main.camera.shiftLimit));
        }
    }

    public void resetCameraDown() {
        moveCameraDownCooldown = cooldownCameraShift;
    }

    double moveCameraUpCooldown = cooldownCameraShift;
    public void moveCameraUp(double timeDelta) {
        if (moveCameraUpCooldown > 0) {
            moveCameraUpCooldown = Math.max(moveCameraUpCooldown - timeDelta, 0);
        } else {
            Main.camera.shift = new Point(Main.camera.shift.x(), Math.min(Main.camera.shift.y() + Math.abs(Main.camera.shiftLimit - Main.camera.shift.y()) * speedOfAnimation * timeDelta, Main.camera.shiftLimit));
        }
    }

    public void resetCameraUp() {
        moveCameraUpCooldown = cooldownCameraShift;
    }

    public void resetCameraShift(double timeDelta) {
        if (Main.camera.shift.y() <= 0.5 && Main.camera.shift.y() >= -0.5) {
            Main.camera.shift = new Point(Main.camera.shift.x(), 0);
        } else {
            Main.camera.shift = new Point(Main.camera.shift.x(), Main.camera.shift.y() - Main.camera.shift.y() * speedOfAnimation * timeDelta);
        }
    }

    @Override
    public void tick(Double timeDelta) {
        timeInGame += timeDelta;
        int currentFrame = (int) ((timeInGame % currentAnimation.animationTime) / currentAnimation.frameTime);
        defaultImage = currentAnimation.frames.get(currentFrame);
        switch (direction) {
            case 1:
                this.graphic = defaultImage;
                break;
            case -1:
                this.graphic = mirrorImage(defaultImage);
                break;
            case 0:
                this.graphic = defaultImage;
                break;
        }
        double timeBeforeSuperTick = System.nanoTime();
        super.tick(timeDelta);
        if (recoil.x() != 0) {
            if (recoil.x() < 0) {
                recoil = new Vec2D(Math.min(recoil.x() + 500 * timeDelta, 0), recoil.y());
            } else if (recoil.x() > 0) {
                recoil = new Vec2D(Math.max(recoil.x() - 500 * timeDelta, 0), recoil.y());
            }
        }
        // System.out.println("Physics Tick: " + ((System.nanoTime() - timeBeforeSuperTick) / 1000000));
    }

    @Override
    public void onGroundReset() {
        hasJumpedLeft = false;
        hasJumpedRight = false;
        timesJumped = 0;
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);
        if (Main.DEBUG_HITBOX) {
            for (LineSegment line : hitbox.shift(position).edges()) {
                Point positionA = convertPositionToCamera(line.a());
                Point positionB = convertPositionToCamera(line.b());
                Graphics2D g2 = (Graphics2D) graphics;
                g2.setColor(Main.STROKE_COLOR_HITBOX);
                g2.setStroke(new BasicStroke(Main.STROKE_SIZE_HITBOXES));
                g2.drawLine((int) positionA.x(), (int) positionA.y(), (int) positionB.x(), (int) positionB.y());
            }
        }
    }

    private Point convertPositionToCamera(Point position) {
        double pixelPerPixel = (double) Main.screenWidth / (double) Main.getScreenWidthGame();
        double offsetX = relative ? Main.camera.getRealPosition().x() : 0;
        double offsetY = relative ? Main.camera.getRealPosition().y() : 0;
        int posXDisplay = (int) ((int) (position.x() - offsetX) * pixelPerPixel + (Main.screenWidth / 2));
        int posYDisplay = (int) ((int) (position.y() - offsetY) * pixelPerPixel + (Main.screenHeight / 2));
        return new Point(posXDisplay, posYDisplay);
    }
}
