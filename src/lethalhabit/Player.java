package lethalhabit;

import lethalhabit.game.Fireball;
import lethalhabit.technical.*;
import lethalhabit.technical.Point;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;

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
    public static final double WALL_JUMP_BOOST = 200;
    public static final double DASH_BOOST = 300;
    public static final double DASH_COOLDOWN = 2;
    public static final double DOUBLE_JUMP_COOLDOWN = 1;
    public static final double RECOIL_RESET_DASH = 1000;
    public static final double RECOIL_RESET_WALL_JUMP = 1000;
    public static final double FIREBALL_COOLDOWN = 2;
    
    private double resetRecoil = 0;

    public int hp = 10; // Wow really great job you did here my friend :)))))))))
    
    public double timeInGame = 0; // Used to calculate the current frame of the animation
    public Direction direction = Direction.NONE;
    public Direction lastDirection = Direction.NONE;
    public Animation currentAnimation = Animation.PLAYER_IDLE;
    
    public double jumpBoost = 1.0;
    public double speedBoost = 1.0;

    private boolean hasJumpedLeft = false;
    private boolean hasJumpedRight = false;
    private boolean jumped = false; // this is used to not let you hold your jump key and then jump more than once
    private int timesJumped = 0;
    
    public PlayerSkills skills;
    
    public Player(Point position, PlayerSkills skills) {
        super(WIDTH, Animation.PLAYER_IDLE.get(0), position, HITBOX);
        this.skills = skills;
    }
    
    public double dashCoolDown = 0;
    public double doubleJumpCooldown = 0;
    
    private void resetCooldowns(double timeDelta) {
        fireBallCooldown = Math.max(fireBallCooldown - timeDelta, 0);
        dashCoolDown = Math.max(dashCoolDown - timeDelta, 0);
        doubleJumpCooldown = Math.max(doubleJumpCooldown - timeDelta, 0);
    }
    
    /**
     * Changes the move velocity of the player based on moveSpeed
     */
    public void moveLeft() {
        this.velocity = new Vec2D(-MOVEMENT_SPEED * speedBoost, this.velocity.y());
        this.direction = Direction.LEFT;
        this.lastDirection = Direction.LEFT;
    }
    
    /**
     * Changes the move velocity of the player based on moveSpeed
     */
    public void moveRight() {
        this.velocity = new Vec2D(MOVEMENT_SPEED * speedBoost, this.velocity.y());
        this.direction = Direction.RIGHT;
        this.lastDirection = Direction.RIGHT;
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
        this.direction = Direction.NONE;
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
    
    
    public double fireBallCooldown = 0.0;
    
    /**
     * Shoots fireball <3
     */
    public void makeFireball() {
        if (fireBallCooldown == 0.0) {
            fireBallCooldown = FIREBALL_COOLDOWN;
            new Fireball(this.position, lastDirection);
        }
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
        if (jumped || isSubmerged()) {
            return;
        }
        jumped = true;
        if (isWallDown()) {
            velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
            return;
        }
        if (skills.wallJump > 0) {
            if (direction == Direction.LEFT && !hasJumpedLeft && isWallLeft()) {
                hasJumpedLeft = true;
                velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
                recoil = new Vec2D(-WALL_JUMP_BOOST * jumpBoost, 0);
                resetRecoil = RECOIL_RESET_WALL_JUMP;
                return;
            } else if (direction == Direction.RIGHT && !hasJumpedRight && isWallRight()) {
                hasJumpedRight = true;
                velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
                recoil = new Vec2D(-WALL_JUMP_BOOST * jumpBoost, 0);
                resetRecoil = RECOIL_RESET_WALL_JUMP;
                return;
            }
        }
        if (canJump() && skills.doubleJump > 0) {
            velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
            if (!isWallDown()) {
                timesJumped += 1;
            }
        }
        jumpBoost = 1.0;
    }
    
    @Override
    public void checkViscosity() {
        super.checkViscosity();
        if (viscosity == 1) {
            jumpBoost = 1;
        } else {
            onGroundReset();
            jumpBoost = switch (skills.swim) {
                case 1 -> 0.3;
                case 2 -> 0.5;
                case 3 -> 0.8;
                default -> 0.0;
            };
            viscosity = switch (skills.swim) {
                case 1 -> 0.6 * viscosity;
                case 2 -> 0.8 * viscosity;
                case 3 -> viscosity;
                default -> 0.0;
            };
        }
    }
    
    /**
     * Resets jump to prevent re-jumping
     */
    public void resetJump() {
        jumped = false;
        jumpBoost = 1.0;
    }
    
    /**
     * Method called for handling animation and every other tick based mechanic
     *
     * @param timeDelta time since last tick (used for calculating the speed of the camera)
     */
    @Override
    public void tick(Double timeDelta) {
        super.tick(timeDelta);
        resetCooldowns(timeDelta);
        timeInGame += timeDelta;
        int currentFrameIndex = (int) ((timeInGame % currentAnimation.animationTime) / currentAnimation.frameTime);
        BufferedImage currentImage = currentAnimation.frames.get(currentFrameIndex);
        switch (direction) {
            case RIGHT -> this.graphic = currentImage;
            case LEFT -> this.graphic = mirrorImage(currentImage);
        }
        double timeBeforeSuperTick = System.nanoTime();
        if (recoil.x() != 0) {
            if (recoil.x() < 0) {
                recoil = new Vec2D(Math.min(recoil.x() + resetRecoil * timeDelta, 0), recoil.y());
            } else {
                recoil = new Vec2D(Math.max(recoil.x() - resetRecoil * timeDelta, 0), recoil.y());
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
    public void onLand(Vec2D velocity) {
        // TODO: play landing sound
        // TODO: standing up animation??
    }
    
    @Override
    public int layer() {
        return Camera.LAYER_GAME;
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
        double offsetX = Main.camera.getRealPosition().x();
        double offsetY = Main.camera.getRealPosition().y();
        int posXDisplay = (int) ((int) (position.x() - offsetX) * Main.scaledPixelSize() + (Main.screenWidth / 2));
        int posYDisplay = (int) ((int) (position.y() - offsetY) * Main.scaledPixelSize() + (Main.screenHeight / 2));
        return new Point(posXDisplay, posYDisplay);
    }
    
    public void dash() {
        if (dashCoolDown <= 0 && direction != Direction.NONE) {
            recoil = switch (direction) {
                case LEFT -> new Vec2D(-DASH_BOOST, 0);
                case RIGHT -> new Vec2D(DASH_BOOST, 0);
                default -> null;
            };
            resetRecoil = RECOIL_RESET_DASH;
            dashCoolDown = DASH_COOLDOWN;
        }
    }
    
}
