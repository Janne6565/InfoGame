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

/**
 * The player is the main character of the game. <br>
 * It is an entity and therefore drawable, and - as a tickable - is ticked every frame. <br>
 * Only one player should exist, and it should only be controlled by user inputs
 */
public class Player extends Entity {
    
    /**
     * Width of the player (33 in-game units)
     */
    public static final int WIDTH = 33;
    
    /**
     * The relative hitbox of the player, scaled to its size
     */
    public static final Hitbox HITBOX = new Hitbox(
            new Point(18, 14).scale(WIDTH / 50.0),
            new Point(18, 42).scale(WIDTH / 50.0),
            new Point(33, 42).scale(WIDTH / 50.0),
            new Point(33, 14).scale(WIDTH / 50.0)
    );
    
    /**
     * Value that the player's absolute movement speed cannot exceed (80)
     */
    public static final double MAX_MOVEMENT_SPEED = 80;
    
    public static final double MOVEMENT_SPEED_ACCELERATION = 1200;
    
    /**
     * On every jump, the player gains +200 up-velocity (negative y-velocity)
     */
    public static final double JUMP_BOOST = 200;
    
    /**
     * On every wall jump, the player gains 200 x-velocity in the direction the wall faces (opposite direction of the player)
     */
    public static final double WALL_JUMP_BOOST = 200;
    
    /**
     * Dashing boosts the player's velocity by 300 in its current x-direction
     */
    public static final double DASH_BOOST = 300;
    
    public static final double RECOIL_RESET_DASH = 1000;
    public static final double RECOIL_RESET_WALL_JUMP = 1000;
    public static final double TIME_NO_GRAVITY_AFTER_DASH = 0.2;
    
    /**
     * The current health points of the player <br>
     * At 0 hp or less, the player dies
     */
    public int hp = 10;
    
    /**
     * Individual skill and ability set of the player, updated and controlled by the {@link Player#skillTree}
     */
    public final Skills skills = new Skills(); // TODO: load from file
    
    /**
     * Individual skill tree of the player, controlling its {@link Player#skills}
     */
    public final SkillTree skillTree = new SkillTree(skills);
    
    /**
     * Time until the player is affected by gravity again
     */
    public double gravityCooldown = 0.0;
    
    /**
     * Current jump boost, e.g. jumping is slowed down in liquids
     */
    public double jumpBoost = 1.0;
    
    /**
     * Current experience points of the player, relative to the level. <br>
     * For example, if the amount of xp required until next level ({@link Player#maxXp}) is 5, the player has 4 and gains 2,
     * the new xp will not be 6, but instead the {@link Player#level} will be increased and the xp will be set to 1 (1 = 4 + 2 - 5)
     */
    public double xp = 0;
    
    /**
     * Amount of xp required until the next {@link Player#level}
     */
    public double maxXp = 5;
    
    /**
     * Amount of 'skill points', i.e. {@link Player#level}s the player has unlocked but not used to learn new skills
     */
    public int spareLevel = 100;
    
    /**
     * Current level of the player, can be upgraded by gaining {@link Player#xp} and exceeding the xp cap ({@link Player#maxXp})
     */
    private int level = 1;
    
    private boolean hasJumpedLeft = false;
    private boolean hasJumpedRight = false;
    private boolean jumped = false; // used to detect only the first press and not instantly re-trigger the jump
    
    private int timesJumped = 0;
    private int timesDashed = 0;
    
    public double timeSinceLastHit = -1;
    public double timeSinceLastDoubleJump = -1;
    
    public double dashCoolDown = 0;
    public double dashCooldownSafety = 0;
    public double doubleJumpCooldown = 3;
    public double attackCooldown = 0;
    public double fireballCooldown = 0.0;
    
    /**
     * Constructs the player and sets its position to the given one. <br>
     * This should not be called more than once, because only one player should exist.
     *
     * @param position Position to place the player
     */
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
        
        if (timeSinceLastHit != -1) {
            timeSinceLastHit += timeDelta;
        }
        
        if (timeSinceLastDoubleJump != -1) {
            timeSinceLastDoubleJump += timeDelta;
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
     *
     * @return <code>true</code> if the player can jump, <code>false</code> otherwise
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
            timeSinceLastDoubleJump = 0;
            return;
        } else if (direction == Direction.RIGHT && ((!hasJumpedLeft && !hasJumpedRight) || (skills.canWallJumpBothSides && !hasJumpedRight)) && isWallRight() && skills.canWallJump) {
            hasJumpedRight = true;
            velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
            recoil = new Vec2D(-WALL_JUMP_BOOST * jumpBoost, 0);
            resetRecoil = RECOIL_RESET_WALL_JUMP;
            timeSinceLastDoubleJump = 0;
            return;
        }
        if (canJump()) {
            velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
            if (!isWallDown()) {
                timesJumped += 1;
                doubleJumpCooldown = skills.doubleJumpCooldown;
                timeSinceLastDoubleJump = 0;
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
     * @see Tickable#tick(Double)
     */
    @Override
    public void tick(Double timeDelta) {
        super.tick(timeDelta);
        TAKES_GRAVITY = gravityCooldown == 0;
        resetCooldowns(timeDelta);
    }
    
    @Override
    public double getTimeAnimation() {
        if (timeSinceLastHit != -1 && timeSinceLastHit < Animation.PLAYER_SLASH_LEFT.length) {
            return timeSinceLastHit;
        }
        if (timeSinceLastDoubleJump != -1 && timeSinceLastDoubleJump < Animation.PLAYER_DOUBLE_JUMP_RIGHT.length) {
            return timeSinceLastDoubleJump;
        }
        return age;
    }
    
    @Override
    public Animation getAnimation() {
        Direction directionToCalculate = lastDirection; // Might be useful for something like a moonwalk implementation
        if (timeSinceLastHit != -1 && timeSinceLastHit < Animation.PLAYER_SLASH_LEFT.length) {
            return switch (directionToCalculate) {
                case LEFT, NONE -> Animation.PLAYER_SLASH_LEFT;
                case RIGHT -> Animation.PLAYER_SLASH_RIGHT;
            };
        }
        if (timeSinceLastDoubleJump != -1 && timeSinceLastDoubleJump < Animation.PLAYER_DOUBLE_JUMP_LEFT.length) {
            return switch (directionToCalculate) {
                case LEFT, NONE -> Animation.PLAYER_DOUBLE_JUMP_LEFT;
                case RIGHT -> Animation.PLAYER_DOUBLE_JUMP_RIGHT;
            };
        }
        
        if (velocity.y() <= 0) {
            if (velocity.x() == 0) {
                return switch (directionToCalculate) {
                    case LEFT, NONE -> Animation.PLAYER_IDLE_LEFT;
                    case RIGHT -> Animation.PLAYER_IDLE_RIGHT;
                };
            } else {
                return velocity.x() < 0 ? Animation.PLAYER_WALK_LEFT : Animation.PLAYER_WALK_RIGHT;
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
        if (lastDirection != Direction.NONE && attackCooldown <= 0) {
            Point pointBasedOnMotion = switch (lastDirection) {
                case LEFT ->
                        new Point(hitbox.minX() - getHitDimensions().getWidth(), (hitbox.maxY() - hitbox.minY()) - getHitDimensions().getHeight() / 2 - skills.attackHitbox.minY());
                case RIGHT ->
                        new Point(hitbox.maxX(), (hitbox.maxY() - hitbox.minY()) - getHitDimensions().getHeight() / 2 - skills.attackHitbox.minY());
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            };
            Hitbox hitbox = skills.attackHitbox.shift(position).shift(pointBasedOnMotion);
            timeSinceLastHit = 0;
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
    public void onCrashDown(Vec2D velocity) {
        super.onCrashDown(velocity);
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
            recoil = switch (lastDirection) {
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
    
    public void registerKill(int xpGain) {
        if (xp + xpGain < maxXp) {
            this.xp += xpGain;
        } else {
            double xpLeft = (xp + xpGain) - maxXp;
            spareLevel += 1;
            level += 1;
            this.xp = xpLeft;
        }
        System.out.println("XP Gained: " + xpGain);
        System.out.println("XP: " + xp);
        Main.GAME_PANEL.xpGained();
    }
    
    public void takeHit(DamageSource damageSource) {
        System.out.println("Ouch, Player HP: " + hp);
        recoil = damageSource.source.position.x() > position.x() ? new Vec2D(-damageSource.knockback, 0) : new Vec2D(damageSource.knockback, 0);
        hp -= damageSource.damage;
        resetRecoil = 300;
    }
    
}
