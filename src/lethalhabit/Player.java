package lethalhabit;

import lethalhabit.game.Fireball;
import lethalhabit.technical.*;
import lethalhabit.technical.Point;
import lethalhabit.ui.Animation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;

import static lethalhabit.util.Util.mirrorImage;

public class Player extends PhysicsObject {
    
    public static final int WIDTH = 20;
    
    public static final Hitbox HITBOX = new Hitbox(new Point[]{
            new Point(10, 10).scale(WIDTH / 50.0),
            new Point(10, 57).scale(WIDTH / 50.0),
            new Point(40, 57).scale(WIDTH / 50.0),
            new Point(40, 10).scale(WIDTH / 50.0)
    });
    
    public static final double MOVEMENT_SPEED = 80;
    public static final double JUMP_BOOST = 200;
    public static final double WALL_JUMP_BOOST = 150;
    public static final double COOLDOWN_CAMERA_SHIFT = 0.5;
    public static final double ANIMATION_SPEED = 5;
    
    public int hp = 10; // Wow really great job you did here my friend :)))))))))
    
    public double timeInGame = 0; // Used to calculate the current frame of the animation
    public int direction = 0;
    public Animation currentAnimation = Animation.PLAYER_IDLE;
    public Vec2D recoil = new Vec2D(0, 0);
    
    public double jumpBoost = 1.0;
    public double speedBoost = 1.0;
    
    public double moveCameraUpCooldown = COOLDOWN_CAMERA_SHIFT;
    public double moveCameraDownCooldown = COOLDOWN_CAMERA_SHIFT;
    
    private boolean hasJumpedLeft = false;
    private boolean hasJumpedRight = false;
    private boolean jumped = false; // this is used to not let you hold your jump key and then jump more than once
    private int timesJumped = 0;
    
    public Player(Point position) {
        super(WIDTH, Animation.PLAYER_IDLE.get(0), position, HITBOX);
    }
    
    /**
     * Changes the move velocity of the player based on moveSpeed
     */
    public void moveLeft() {
        this.velocity = new Vec2D(-MOVEMENT_SPEED * speedBoost, this.velocity.y());
        this.direction = -1;
    }
    
    /**
     * Changes the move velocity of the player based on moveSpeed
     */
    public void moveRight() {
        this.velocity = new Vec2D(MOVEMENT_SPEED * speedBoost, this.velocity.y());
        this.direction = 1;
    }
    
    public void moveUp() {
        this.velocity = new Vec2D(this.velocity.x(), -MOVEMENT_SPEED * speedBoost);
    }
    
    public void moveDown() {
        this.velocity = new Vec2D(this.velocity.x(), MOVEMENT_SPEED * speedBoost);
    }
    
    /**
     * Resets the x velocity
     */
    public void stopMovementX() {
        this.velocity = new Vec2D(0, this.velocity.y());
        if (this.velocity.y() == 0) {
            this.currentAnimation = Animation.PLAYER_IDLE;
        }
    }
    
    /**
     * Resets the y velocity
     */
    public void stopMovementY() {
        this.velocity = new Vec2D(this.velocity.x(), 0);
        if (this.velocity.x() == 0) {
            this.currentAnimation = Animation.PLAYER_IDLE;
        }
    }
    
    /**
     * Shoots fireball <3
     */
    public void makeFireball() {
        new Fireball(this.position, direction);
    }
    
    /**
     * Checks the player's ability to jump
     *
     * @return true if the player can jump, false otherwise
     */
    public boolean canJump() {
        if (timesJumped <= 0) {
            return true;
        }
        return isWallDown();
    }
    
    public void jump() {
        if (jumped || !surroundingLiquids().isEmpty()) {
            return;
        }
        jumped = true;
        if (isWallDown()) {
            velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
            return;
        }
        if (direction == -1 && !hasJumpedLeft && isWallLeft()) {
            hasJumpedLeft = true;
            velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
            recoil = new Vec2D(WALL_JUMP_BOOST * jumpBoost, 0);
            return;
        } else if (direction == 1 && !hasJumpedRight && isWallRight()) {
            hasJumpedRight = true;
            velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
            recoil = new Vec2D(-WALL_JUMP_BOOST * jumpBoost, 0);
            return;
        }
        if (canJump()) {
            velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
            if (!isWallDown()) {
                timesJumped += 1;
            }
        }
        jumpBoost = 1.0;
    }
    
    @Override
    public void checkViscosity() {
        surroundingLiquids().stream()
                .min(Comparator.comparing(liquid -> liquid.viscosity))
                .ifPresent(liquid -> {
                    onGroundReset();
                    velocity = velocity.scale(liquid.viscosity);
                    jumpBoost = 0.6;
                });
    }
    
    /**
     * Resets jump to prevent re-jumping
     */
    public void resetJump() {
        jumped = false;
        jumpBoost = 1.0;
    }
    
    /**
     * Shifts the camera downwards
     *
     * @param timeDelta time since last tick (used to calculate the speed of the camera)
     */
    public void moveCameraDown(double timeDelta) {
        if (moveCameraDownCooldown > 0) {
            moveCameraDownCooldown = Math.max(moveCameraDownCooldown - timeDelta, 0);
        } else {
            Main.camera.shift = new Point(Main.camera.shift.x(), Math.max(Main.camera.shift.y() - Math.abs(-Main.camera.shiftLimit - Main.camera.shift.y()) * ANIMATION_SPEED * timeDelta, -Main.camera.shiftLimit));
        }
    }
    
    /**
     * Resets the cooldown for the camera downward movement
     */
    public void resetCameraDown() {
        moveCameraDownCooldown = COOLDOWN_CAMERA_SHIFT;
    }
    
    /**
     * Shifts the camera upwards
     *
     * @param timeDelta time since last tick (used to calculate the speed of the camera)
     */
    public void moveCameraUp(double timeDelta) {
        if (moveCameraUpCooldown > 0) {
            moveCameraUpCooldown = Math.max(moveCameraUpCooldown - timeDelta, 0);
        } else {
            Main.camera.shift = new Point(Main.camera.shift.x(), Math.min(Main.camera.shift.y() + Math.abs(Main.camera.shiftLimit - Main.camera.shift.y()) * ANIMATION_SPEED * timeDelta, Main.camera.shiftLimit));
        }
    }
    
    /**
     * Resets the cooldown for the camera upward movement
     */
    public void resetCameraUp() {
        moveCameraUpCooldown = COOLDOWN_CAMERA_SHIFT;
    }
    
    /**
     * Resets the shift of the camera to go back to (0, 0)
     *
     * @param timeDelta time since last tick (used to calculate the speed of the camera)
     */
    public void resetCameraShift(double timeDelta) {
        if (Main.camera.shift.y() <= 0.5 && Main.camera.shift.y() >= -0.5) {
            Main.camera.shift = new Point(Main.camera.shift.x(), 0);
        } else {
            Main.camera.shift = new Point(Main.camera.shift.x(), Main.camera.shift.y() - Main.camera.shift.y() * ANIMATION_SPEED * timeDelta);
        }
    }
    
    /**
     * Method called for handling animation and every other tick based mechanic
     *
     * @param timeDelta time since last tick (used for calculating the speed of the camera)
     */
    @Override
    public void tick(Double timeDelta) {
        timeInGame += timeDelta;
        int currentFrameIndex = (int) ((timeInGame % currentAnimation.animationTime) / currentAnimation.frameTime);
        BufferedImage currentImage = currentAnimation.frames.get(currentFrameIndex);
        switch (direction) {
            case 1:
                this.graphic = currentImage;
                break;
            case -1:
                this.graphic = mirrorImage(currentImage);
                break;
            case 0:
                this.graphic = currentImage;
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
    }
    
    /**
     * Resets all the jumps on ground touch
     */
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
            for (LineSegment line : hitbox.shift(getPosition()).edges()) {
                Point positionA = convertPositionToCamera(line.a());
                Point positionB = convertPositionToCamera(line.b());
                Graphics2D g2 = (Graphics2D) graphics;
                g2.setColor(Main.HITBOX_STROKE_COLOR);
                g2.setStroke(new BasicStroke(Main.STROKE_SIZE_HITBOXES));
                g2.drawLine((int) positionA.x(), (int) positionA.y(), (int) positionB.x(), (int) positionB.y());
            }
        }
    }
    
    private Point convertPositionToCamera(Point position) {
        double offsetX = isRelative() ? Main.camera.getRealPosition().x() : 0;
        double offsetY = isRelative() ? Main.camera.getRealPosition().y() : 0;
        int posXDisplay = (int) ((int) (position.x() - offsetX) * Main.scaledPixelSize() + (Main.screenWidth / 2));
        int posYDisplay = (int) ((int) (position.y() - offsetY) * Main.scaledPixelSize() + (Main.screenHeight / 2));
        return new Point(posXDisplay, posYDisplay);
    }
    
}
