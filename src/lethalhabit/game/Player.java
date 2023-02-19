package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTree;
import lethalhabit.ui.*;
import lethalhabit.util.PlayerSkills;
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


    public static final double MOVEMENT_SPEED = 80;
    public static final double JUMP_BOOST = 200;
    public static final double WALL_JUMP_BOOST = 200;
    public static final double DASH_BOOST = 300;
    public static final double RECOIL_RESET_DASH = 1000;
    public static final double RECOIL_RESET_WALL_JUMP = 1000;
    public static final double TIME_NO_GRAVITY_AFTER_DASH = 0.2;


    public int hp = 10; // Wow, really great job you did here my friend :)))))))))


    public Hitbox HIT_HITBOX = new Hitbox(
            new Point(0, 5),
            new Point(15, 5),
            new Point(15, 25),
            new Point(0, 25)
    );

    public SkillTree PLAYER_SKILL_TREE = new SkillTree();

    public boolean CAN_MAKE_FIREBALL = false;
    public double FIREBALL_COOLDOWN = 5;
    public double ATTACK_COOLDOWN = 1;
    public double DOUBLE_JUMP_COOLDOWN = 3;
    public int DASH_AMOUNTS = 0;
    public double DASH_COOLDOWN = 5;
    public int DOUBLE_JUMP_AMOUNT = 0;
    public boolean CAN_WALL_JUMP = false;
    public boolean CAN_WALL_JUMP_ONCE_PER_SIDE = false;

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

    public double doubleJumpCooldown = 3;
    public double dashCooldownSafety = 0;

    public double dashCoolDown = 0;
    public double attackCooldown = 0;
    private int dashes = DASH_AMOUNTS;



    public Player(Point position) {
        super(WIDTH, Animation.PLAYER_IDLE.get(0), position, HITBOX);
    }

    public Dimension getHitDimensions() {
        return new Dimension((int) (HIT_HITBOX.maxX() - HIT_HITBOX.minX()), (int) (HIT_HITBOX.maxY() - HIT_HITBOX.minY()));
    }

    private void resetCooldowns(double timeDelta) {
        fireBallCooldown = Math.max(fireBallCooldown - timeDelta, 0);
        dashCoolDown = Math.max(dashCoolDown - timeDelta, 0);
        doubleJumpCooldown = Math.max(doubleJumpCooldown - timeDelta, 0);
        gravityCooldown = Math.max(gravityCooldown - timeDelta, 0);
        attackCooldown = Math.max(attackCooldown - timeDelta, 0);
        dashCooldownSafety = Math.max(dashCooldownSafety - timeDelta, 0);

        if (dashCoolDown == 0 && dashes < DASH_AMOUNTS) {
            dashCoolDown = DASH_COOLDOWN;
            dashes += 1;
            System.out.println(dashes);
        }
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
        if (fireBallCooldown == 0.0 && CAN_MAKE_FIREBALL) {
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
        return (timesJumped < DOUBLE_JUMP_AMOUNT && doubleJumpCooldown == 0) || isWallDown();
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
        if (direction == Direction.LEFT && ((!hasJumpedLeft && !hasJumpedRight) || (CAN_WALL_JUMP_ONCE_PER_SIDE && !hasJumpedLeft)) && isWallLeft() && CAN_WALL_JUMP) {
            hasJumpedLeft = true;
            velocity = new Vec2D(velocity.x(), -JUMP_BOOST * jumpBoost);
            recoil = new Vec2D(WALL_JUMP_BOOST * jumpBoost, 0);
            resetRecoil = RECOIL_RESET_WALL_JUMP;
            return;
        } else if (direction == Direction.RIGHT && ((!hasJumpedLeft && !hasJumpedRight) || (CAN_WALL_JUMP_ONCE_PER_SIDE && !hasJumpedRight)) && isWallRight() && CAN_WALL_JUMP) {
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
                doubleJumpCooldown = DOUBLE_JUMP_COOLDOWN;
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
     *
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
        return switch (direction) {
            case NONE -> Animation.PLAYER_IDLE;
            case LEFT -> Animation.PLAYER_WALK_LEFT;
            case RIGHT -> Animation.PLAYER_WALK_RIGHT;
        };
    }

    public void hit() {
        if (lastDirection != Direction.NONE) {
            Point pointBasedOnMotion = switch (lastDirection) {
                case LEFT -> new Point(hitbox.minX() - (HIT_HITBOX.maxX() - HIT_HITBOX.minX()), (Main.mainCharacter.hitbox.maxY() - Main.mainCharacter.hitbox.minY()) - (Main.mainCharacter.HIT_HITBOX.maxY() - Main.mainCharacter.HIT_HITBOX.minY()) / 2 - Main.mainCharacter.HIT_HITBOX.minY());
                case RIGHT -> new Point(hitbox.maxX(), (Main.mainCharacter.hitbox.maxY() - Main.mainCharacter.hitbox.minY()) - (Main.mainCharacter.HIT_HITBOX.maxY() - Main.mainCharacter.HIT_HITBOX.minY()) / 2 - Main.mainCharacter.HIT_HITBOX.minY());
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            };
            Hitbox hitbox = HIT_HITBOX.shift(position).shift(pointBasedOnMotion);

            if (attackCooldown == 0) {
                attackCooldown = ATTACK_COOLDOWN;

                List<Hittable> hitted = Util.getHittablesInHitbox(hitbox);

                if (hitted.size() > 0) {
                    double xKnockback = switch (lastDirection) {
                        case RIGHT -> -50;
                        case LEFT -> 50;
                        case NONE -> 0.0;
                    };
                    double yKnockback = 0;
                    knockback(xKnockback, yKnockback);
                }

                for (Hittable hittable : hitted) {
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
    public void draw(Graphics graphics) {
        super.draw(graphics);

        if (Main.DEBUG_HITBOX) {
            for (LineSegment line : hitbox.shift(getPosition()).edges()) {
                graphics.setColor(Main.HITBOX_STROKE_COLOR);
                Util.drawLineSegment(graphics, line);
            }
        }
    }
    
    @Override
    public void midAir(double timeDelta) {
        Main.camera.resetCameraUp();
        Main.camera.resetCameraDown();
    }

    public void dash() {
        System.out.println(dashes);
        if (direction != Direction.NONE && dashes > 0 && dashCooldownSafety == 0) {
            dashCooldownSafety = 1;
            recoil = switch (direction) {
                case LEFT -> new Vec2D(-DASH_BOOST, 0);
                case RIGHT -> new Vec2D(DASH_BOOST, 0);
                default -> null;
            };
            resetRecoil = RECOIL_RESET_DASH;
            velocity = new Vec2D(velocity.x(), 0);
            dashes -= 1;
            gravityCooldown = TIME_NO_GRAVITY_AFTER_DASH;
        }
    }
    
}
