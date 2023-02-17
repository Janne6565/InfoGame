package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.ui.GamePanel;
import lethalhabit.util.PlayerSkills;
import lethalhabit.math.*;
import lethalhabit.math.Point;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Camera;
import lethalhabit.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static lethalhabit.util.Util.mirrorImage;

public class Player extends Entity {
    
    public static final int WIDTH = 33;
    
    public static final Hitbox HITBOX = new Hitbox(new Point[]{
            new Point(18, 14).scale(WIDTH / 50.0),
            new Point(18, 42).scale(WIDTH / 50.0),
            new Point(36, 42).scale(WIDTH / 50.0),
            new Point(36, 14).scale(WIDTH / 50.0)
    });
    
    public static final Hitbox HIT_HITBOX = new Hitbox(new Point[] {
            new Point(0, 10),
            new Point(10, 10),
            new Point(10, 20),
            new Point(0, 20)
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
    public static final double TIME_NO_GRAVITY_AFTER_DASH = 0.2;
    public static final double ATTACK_COOLDOWN = 1;
    
    private double resetRecoil = 0;
    
    public int hp = 10; // Wow really great job you did here my friend :)))))))))
    
    public double timeInGame = 0; // Used to calculate the current frame of the animation
    public Direction direction = Direction.NONE;
    public Direction lastDirection = Direction.NONE;
    public Animation currentAnimation = Animation.PLAYER_IDLE;

    /**
     * Until end of the cooldown you will remain in a state where gravity isn't affecting you at all <3
     */
    public double gravityCooldown = 0.0;
    public double jumpBoost = 1.0;
    public double speedBoost = 1.0;
    
    private boolean hasJumpedLeft = false;
    private boolean hasJumpedRight = false;
    private boolean jumped = false; // this is used to not let you hold your jump key and then jump more than once
    
    private int timesJumped = 0;
    public PlayerSkills skills;
    
    public double dashCoolDown = 0;
    public double doubleJumpCooldown = 0;
    
    public double attackCooldown = 0;
    
    public Player(Point position, PlayerSkills skills) {
        super(WIDTH, Animation.PLAYER_IDLE.get(0), position, HITBOX);
        this.skills = skills;
    }
    
    private void resetCooldowns(double timeDelta) {
        fireBallCooldown = Math.max(fireBallCooldown - timeDelta, 0);
        dashCoolDown = Math.max(dashCoolDown - timeDelta, 0);
        doubleJumpCooldown = Math.max(doubleJumpCooldown - timeDelta, 0);
        gravityCooldown = Math.max(gravityCooldown - timeDelta, 0);
    }
    
    /**
     * Changes the move velocity of the player based on moveSpeed
     */
    public void moveLeft() {
        this.velocity = new Vec2D(-MOVEMENT_SPEED * speedBoost, this.velocity.y());
        this.direction = Direction.LEFT;
        this.lastDirection = Direction.LEFT;
        onMove();
    }
    
    /**
     * Changes the move velocity of the player based on moveSpeed
     */
    public void moveRight() {
        this.velocity = new Vec2D(MOVEMENT_SPEED * speedBoost, this.velocity.y());
        this.direction = Direction.RIGHT;
        this.lastDirection = Direction.RIGHT;
        onMove();
    }
    
    public void moveUp() {
        this.velocity = new Vec2D(this.velocity.x(), -MOVEMENT_SPEED * speedBoost);
        onMove();
    }
    
    public void moveDown() {
        this.velocity = new Vec2D(this.velocity.x(), MOVEMENT_SPEED * speedBoost);
        onMove();
    }
    
    public void onMove() {
        Main.camera.resetCameraDown();
        Main.camera.resetCameraUp();
    }
    
    /**
     * Resets the x velocity
     */
    public void stopMovementX() {
        this.velocity = new Vec2D(0, this.velocity.y());
        this.direction = Direction.NONE;
    }
    
    /**
     * Resets the y velocity
     */
    public void stopMovementY() {
        this.velocity = new Vec2D(this.velocity.x(), 0);
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
     * @return true if the player can jump, false otherwise
     */
    public boolean canJump() {
        return timesJumped <= 0 || isWallDown();
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
                recoil = new Vec2D(WALL_JUMP_BOOST * jumpBoost, 0);
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
     * @param timeDelta time since last tick (used for calculating the speed of the camera)
     */
    @Override
    public void tick(Double timeDelta) {
        super.tick(timeDelta);
        TAKES_GRAVITY = gravityCooldown == 0;
        resetCooldowns(timeDelta);
        timeInGame += timeDelta;

        currentAnimation = getCurrentAnimation();

        BufferedImage currentImage = currentAnimation.getCurrentFrame(timeInGame);
        switch (lastDirection) {
            case RIGHT -> this.graphic = currentImage;
            case LEFT -> this.graphic = mirrorImage(currentImage);
        }
        double timeBeforeSuperTick = System.nanoTime();
        if (recoil.x() != 0) {
            recoil = recoil.x() < 0 ? new Vec2D(Math.min(recoil.x() + resetRecoil * timeDelta, 0), recoil.y()) : new Vec2D(Math.max(recoil.x() - resetRecoil * timeDelta, 0), recoil.y());
        }
    }

    private Animation getCurrentAnimation() {
        return switch (direction) {
            case NONE -> Animation.PLAYER_IDLE;
            case LEFT -> Animation.PLAYER_WALK_LEFT;
            case RIGHT -> Animation.PLAYER_WALK_RIGHT;
        };
    }

    public void hit() {
        if (attackCooldown <= 0 && lastDirection != Direction.NONE) {
            Point pointBasedOnMotion = switch (lastDirection) {
                case LEFT -> new Point(-5, 0);
                case RIGHT -> new Point(20, 0);
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            };
            Hitbox hitbox = HIT_HITBOX.shift(position).shift(pointBasedOnMotion);
    
    
            List<Hittable> hitted = Util.getHittablesInHitbox(hitbox);
            for (Hittable hittable : hitted) {
                hittable.onHit();
                attackCooldown = ATTACK_COOLDOWN;
                System.out.println(attackCooldown);
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
                graphics.setColor(Main.HITBOX_STROKE_COLOR);
                Util.drawLineSegment(graphics, line);
//                Point positionA = Main.camera.getAbsolutePosition(line.a());
//                Point positionB = Main.camera.getAbsolutePosition(line.b());
//                Graphics2D g2 = (Graphics2D) graphics;
//                g2.setStroke(new BasicStroke(Main.STROKE_SIZE_HITBOXES));
//                g2.drawLine((int) positionA.x(), (int) positionA.y(), (int) positionB.x(), (int) positionB.y());
            }
        }
    }
    
    @Override
    public void midAir(double timeDelta) {
        Main.camera.resetCameraUp();
        Main.camera.resetCameraDown();
    }
    
    public void dash() {
        if (dashCoolDown <= 0 && direction != Direction.NONE) {
            recoil = switch (direction) {
                case LEFT -> new Vec2D(-DASH_BOOST, 0);
                case RIGHT -> new Vec2D(DASH_BOOST, 0);
                default -> null;
            };
            resetRecoil = RECOIL_RESET_DASH;
            velocity = new Vec2D(velocity.x(), 0);
            dashCoolDown = DASH_COOLDOWN;
            gravityCooldown = TIME_NO_GRAVITY_AFTER_DASH;
        }
    }
    
}
