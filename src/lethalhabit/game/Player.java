package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.game.skills.SkillTree;
import lethalhabit.ui.*;
import lethalhabit.game.skills.Skills;
import lethalhabit.math.*;
import lethalhabit.math.Point;
import lethalhabit.util.Util;

import java.awt.*;
import java.util.List;

public class Player extends Entity {
    
    public static final int WIDTH = 33;
    
    public static final Hitbox HITBOX = new Hitbox(
            new Point(18, 14).scale(WIDTH / 50.0),
            new Point(18, 42).scale(WIDTH / 50.0),
            new Point(33, 42).scale(WIDTH / 50.0),
            new Point(33, 14).scale(WIDTH / 50.0)
    );
    
    public static final double MAX_MOVEMENT_SPEED = 80;
    public static final double MOVEMENT_SPEED_ACCELERATION = 900;
    public static final double JUMP_BOOST = 200;
    public static final double WALL_JUMP_BOOST = 200;
    public static final double DASH_BOOST = 300;
    public static final double RECOIL_RESET_DASH = 1000;
    public static final double RECOIL_RESET_WALL_JUMP = 1000;
    public static final double TIME_NO_GRAVITY_AFTER_DASH = 0.2;
    
    public int hp = 10;
    
    public final Skills skills = new Skills(); // TODO: load from file
    
    public final SkillTree skillTree = new SkillTree(skills);
    
    /**
     * Until end of the cooldown you will remain in a state where gravity isn't affecting you at all <3
     */
    public double gravityCooldown = 0.0;
    public double jumpBoost = 1.0;
    public double speedBoost = 1.0;
    public int spareLevel = 100;
    
    private boolean hasJumpedLeft = false;
    private boolean hasJumpedRight = false;
    private boolean jumped = false; // used to detect only the first press and not instantly re-trigger the jump
    
    private int timesJumped = 0;
    private int timesDashed = 0;
    
    public double dashCoolDown = 0;
    public double dashCooldownSafety = 0;
    public double doubleJumpCooldown = 3;
    public double attackCooldown = 0;
    public double fireballCooldown = 0.0;
    
    public Player(Point position) {
        super(WIDTH, Animation.PLAYER_IDLE_LEFT.get(0), position, HITBOX);
    }
    
    public Dimension getHitDimensions() {
        return new Dimension((int) (skills.attackHitbox.maxX() - skills.attackHitbox.minX()), (int) (skills.attackHitbox.maxY() - skills.attackHitbox.minY()));
    }
    
    private void resetCooldowns(double timeDelta) {
        fireballCooldown = Math.max(fireballCooldown - timeDelta, 0);
        dashCoolDown = Math.max(dashCoolDown - timeDelta, 0);
        doubleJumpCooldown = Math.max(doubleJumpCooldown - timeDelta, 0);
        gravityCooldown = Math.max(gravityCooldown - timeDelta, 0);
        attackCooldown = Math.max(attackCooldown - timeDelta, 0);
        dashCooldownSafety = Math.max(dashCooldownSafety - timeDelta, 0);
        
        if (dashCoolDown <= 0 && timesDashed < skills.dashAmount) {
            dashCoolDown = skills.dashCooldown;
            timesDashed += 1;
        }
    }
    
    /**
     * Changes the move velocity of the player based on moveSpeed
     */
    public void moveLeft(double timeDelta) {
        this.velocity = new Vec2D(Math.max(velocity.x() - MOVEMENT_SPEED_ACCELERATION * timeDelta, -MAX_MOVEMENT_SPEED), this.velocity.y());
        this.direction = Direction.LEFT;
        this.lastDirection = Direction.LEFT;
        onMove();
    }
    
    /**
     * Changes the move velocity of the player based on moveSpeed
     */
    public void moveRight(double timeDelta) {
        this.velocity = new Vec2D(Math.min(velocity.x() + MOVEMENT_SPEED_ACCELERATION * timeDelta, MAX_MOVEMENT_SPEED), this.velocity.y());
        this.direction = Direction.RIGHT;
        this.lastDirection = Direction.RIGHT;
        onMove();
    }
    
    public void moveUp(double timeDelta) {
        this.velocity = new Vec2D(this.velocity.x(), Math.max(velocity.y() - MOVEMENT_SPEED_ACCELERATION * timeDelta, -MAX_MOVEMENT_SPEED));
        onMove();
    }
    
    public void moveDown(double timeDelta) {
        this.velocity = new Vec2D(this.velocity.x(), Math.min(velocity.y() + MOVEMENT_SPEED_ACCELERATION * timeDelta, MAX_MOVEMENT_SPEED));
        onMove();
    }
    
    public void onMove() {
        Main.camera.resetCameraDown();
        Main.camera.resetCameraUp();
    }
    
    /**
     * Resets the x velocity
     */
    public void stopMovementX(double timeDelta) {
        if (velocity.x() > 0) {
            this.velocity = new Vec2D(Math.max(velocity.x() - MOVEMENT_SPEED_ACCELERATION * timeDelta, 0), this.velocity.y());
        }
        if (velocity.x() < 0) {
            this.velocity = new Vec2D(Math.min(velocity.x() + MOVEMENT_SPEED_ACCELERATION * timeDelta, 0), this.velocity.y());
        }
        
        this.direction = Direction.NONE;
    }
    
    /**
     * Resets the y velocity
     */
    public void stopMovementY(double timeDelta) {
        if (velocity.y() > 0) {
            this.velocity = new Vec2D(this.velocity.x(), Math.max(velocity.y() - MOVEMENT_SPEED_ACCELERATION * timeDelta, 0));
        }
        if (velocity.y() < 0) {
            this.velocity = new Vec2D(this.velocity.x(), Math.min(velocity.y() + MOVEMENT_SPEED_ACCELERATION * timeDelta, 0));
        }
    }
    
    /**
     * Shoots fireball <3
     */
    public void makeFireball() {
        if (fireballCooldown <= 0 && skills.canMakeFireball) {
            fireballCooldown = skills.fireballCooldown;
            new Fireball(this.position, lastDirection);
        }
    }
    
    /**
     * Checks the player's ability to jump
     * @return true if the player can jump, false otherwise
     */
    public boolean canJump() {
        int possibleDoubleJumps = skills.doubleJumpAmount;
        return (timesJumped < possibleDoubleJumps && doubleJumpCooldown <= 0) || isWallDown();
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
        if (direction == Direction.LEFT && ((!hasJumpedLeft && !hasJumpedRight) || (skills.canWallJumpBothSides && !hasJumpedLeft)) && isWallLeft() && skills.canWallJump) {
            hasJumpedLeft = true;
            velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
            recoil = new Vec2D(WALL_JUMP_BOOST * jumpBoost, 0);
            resetRecoil = RECOIL_RESET_WALL_JUMP;
            return;
        } else if (direction == Direction.RIGHT && ((!hasJumpedLeft && !hasJumpedRight) || (skills.canWallJumpBothSides && !hasJumpedRight)) && isWallRight() && skills.canWallJump) {
            hasJumpedRight = true;
            velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
            recoil = new Vec2D(-WALL_JUMP_BOOST * jumpBoost, 0);
            resetRecoil = RECOIL_RESET_WALL_JUMP;
            return;
        }
        if (canJump()) {
            velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
            if (!isWallDown()) {
                timesJumped += 1;
                doubleJumpCooldown = skills.doubleJumpCooldown;
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
    }
    
    @Override
    public Animation getAnimation() {
        Direction directionToCalculate = lastDirection; // Might be useful for something like a moonwalk implementation
        if (velocity.y() <= 0) {
            if (velocity.x() == 0) {
                return switch (directionToCalculate) {
                    case LEFT, NONE -> Animation.PLAYER_IDLE_LEFT;
                    case RIGHT -> Animation.PLAYER_IDLE_RIGHT;
                };
            } else {
                return velocity.x() < 0 ? Animation.PLAYER_WALK_RIGHT : Animation.PLAYER_WALK_LEFT;
            }
        } else {
            if (velocity.x() == 0) {
                return switch (directionToCalculate) {
                    case LEFT, NONE -> Animation.PLAYER_FALL_LEFT;
                    case RIGHT -> Animation.PLAYER_FALL_RIGHT;
                };
            } else {
                return velocity.x() > 0 ? Animation.PLAYER_FALL_RIGHT : Animation.PLAYER_FALL_LEFT;
            }
        }
    }
    
    public void hit() {
        if (lastDirection != Direction.NONE) {
            Point pointBasedOnMotion = switch (lastDirection) {
                case LEFT ->
                        new Point(hitbox.minX() - getHitDimensions().getWidth(), (hitbox.maxY() - hitbox.minY()) - getHitDimensions().getHeight() / 2 - skills.attackHitbox.minY());
                case RIGHT ->
                        new Point(hitbox.maxX(), (hitbox.maxY() - hitbox.minY()) - getHitDimensions().getHeight() / 2 - skills.attackHitbox.minY());
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            };
            Hitbox hitbox = skills.attackHitbox.shift(position).shift(pointBasedOnMotion);
            
            if (attackCooldown <= 0) {
                attackCooldown = skills.attackCooldown;
                
                List<Hittable> struck = Util.getHittablesInHitbox(hitbox);
                
                if (struck.size() > 0) {
                    double xKnockback = switch (lastDirection) {
                        case RIGHT -> -50;
                        case LEFT -> 50;
                        case NONE -> 0.0;
                    };
                    double yKnockback = 0;
                    knockback(xKnockback, yKnockback);
                }
                
                for (Hittable hittable : struck) {
                    hittable.onHit(DamageSource.standard(this, 1));
                }
                
                PlayerSlashAnimation slashAnimation = new PlayerSlashAnimation(lastDirection, getHitDimensions());
                slashAnimation.register();
            }
        }
    }
    
    public void knockback(double amountX, double amountY) {
        recoil = new Vec2D(amountX, amountY);
        resetRecoil = 300;
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
    public void land(Vec2D velocity) {
        // TODO: play landing sound
        // TODO: standing up animation??
    }
    
    @Override
    public int layer() {
        return Camera.LAYER_GAME;
    }
    
    @Override
    public void midAir(double timeDelta) {
        Main.camera.resetCameraUp();
        Main.camera.resetCameraDown();
    }
    
    public void dash() {
        if (direction != Direction.NONE && timesDashed > 0 && dashCooldownSafety <= 0) {
            dashCooldownSafety = 1;
            recoil = switch (direction) {
                case LEFT -> new Vec2D(-DASH_BOOST, 0);
                case RIGHT -> new Vec2D(DASH_BOOST, 0);
                default -> null;
            };
            resetRecoil = RECOIL_RESET_DASH;
            velocity = new Vec2D(velocity.x(), 0);
            timesDashed -= 1;
            gravityCooldown = TIME_NO_GRAVITY_AFTER_DASH;
        }
    }
    
}
