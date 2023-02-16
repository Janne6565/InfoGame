package lethalhabit.game.enemy;

import lethalhabit.Main;
import lethalhabit.game.Entity;
import lethalhabit.game.Fireball;
import lethalhabit.math.Point;
import lethalhabit.math.*;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Camera;
import lethalhabit.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static lethalhabit.util.Util.mirrorImage;

public class Goomba extends Entity {

    public static final int WIDTH = 33;

    public static final Hitbox HITBOX = new Hitbox(new Point[]{
            new Point(18, 14).scale(WIDTH / 50.0),
            new Point(18, 42).scale(WIDTH / 50.0),
            new Point(36, 42).scale(WIDTH / 50.0),
            new Point(36, 14).scale(WIDTH / 50.0)
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

    private double resetRecoil = 0;

    public static final Point relativeEyes = new Point(9, 6);

    private double attackCooldown = 0;

    public static final int ATTACK_RANGE = 10;
    public static final int SIGHT_RANGE = 160;

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

    public double dashCoolDown = 0;
    public double doubleJumpCooldown = 0;

    public Goomba(Point position) {
        super(WIDTH, Animation.PLAYER_IDLE.get(0), position, HITBOX);
    }
    
    private void resetCooldowns(double timeDelta) {
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
        this.direction = Direction.NONE;
    }

    /**
     * Resets the y velocity
     */
    public void stopMovementY() {
        this.velocity = new Vec2D(this.velocity.x(), 0);
    }


    /**
     * Checks the player's ability to jump
     * @return true if the player can jump, false otherwise
     */
    public boolean canJump() {
        return timesJumped <= 0 || isWallDown();
    }



    @Override
    public void checkViscosity() {
        super.checkViscosity();
        if (viscosity == 1) {
            jumpBoost = 1;
        } else {
            onGroundReset();
            jumpBoost = 0.0;
            viscosity = 0.0;
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
        boolean until = false;
        
        
        
        if (direction != Direction.NONE) {
            velocity = switch (direction) {
                case LEFT -> new Vec2D(-MOVEMENT_SPEED, velocity.y());
                case RIGHT -> new Vec2D(MOVEMENT_SPEED, velocity.y());
                default -> throw new IllegalStateException("Unexpected Value: " + direction);
            };
    
            Point pointToCheck = switch (direction) {
                case LEFT -> new Point(-Main.TILE_SIZE, Main.TILE_SIZE);
                case RIGHT -> new Point(Main.TILE_SIZE, Main.TILE_SIZE);
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            };
    
            boolean isWallInDirection = switch(direction) {
                case RIGHT -> isWallRight();
                case LEFT -> isWallLeft();
                default -> false;
            };
            
            if (!isWallDown(pointToCheck) || isWallInDirection) {
                direction = switch (direction) {
                    case LEFT -> Direction.RIGHT;
                    case RIGHT -> Direction.LEFT;
                    default -> throw new IllegalStateException("Unexpected value: " + direction);
                };
                this.lastDirection = direction;
            }
        } else {
            direction = Direction.LEFT;
        }
        

        /*
        if (canSeePlayer()) {
            if (Main.mainCharacter.position.distance(position) < ATTACK_RANGE) {
                this.velocity = new Vec2D(0, this.velocity.y());
                attack(timeDelta);
                return;
            }
            if (Main.mainCharacter.position.x() < position.x()) {
                velocity = new Vec2D(-MOVEMENT_SPEED, velocity.y());
                this.direction = Direction.LEFT;
                this.lastDirection = Direction.LEFT;
                if (isWallLeft() && isWallDown() && !isSubmerged()) {
                    velocity = new Vec2D(velocity.x(), -JUMP_BOOST);
                }
            } else if (Main.mainCharacter.position.x() > position.x()) {
                velocity = new Vec2D(MOVEMENT_SPEED, velocity.y());
                this.direction = Direction.RIGHT;
                this.lastDirection = Direction.RIGHT;
                if (isWallRight() && isWallDown() && !isSubmerged()) {
                    velocity = new Vec2D(velocity.x(), -JUMP_BOOST);
                }
            } else {
                velocity = new Vec2D(0, velocity.y());
                this.direction = Direction.NONE;
                this.lastDirection = Direction.NONE;
            }
        }else{
            stopMovementX();
        }*/


        currentAnimation = getCurrentAnimation();

        int currentFrameIndex = (int) ((timeInGame % currentAnimation.animationTime) / currentAnimation.frameTime);
        BufferedImage currentImage = currentAnimation.frames.get(currentFrameIndex);

        switch (lastDirection) {

            case RIGHT -> this.graphic = currentImage;
            case LEFT -> this.graphic = mirrorImage(currentImage);

        }

        double timeBeforeSuperTick = System.nanoTime();
        if (recoil.x() != 0) {
            recoil = recoil.x() < 0 ? new Vec2D(Math.min(recoil.x() + resetRecoil * timeDelta, 0), recoil.y()) : new Vec2D(Math.max(recoil.x() - resetRecoil * timeDelta, 0), recoil.y());
        }
    }
    private boolean canSeePlayer() {
        Point absoluteEyes = position.plus(relativeEyes);
        for (Point playerVertex : Main.mainCharacter.hitbox.shift(Main.mainCharacter.position)) {
            LineSegment ray = new LineSegment(absoluteEyes, playerVertex);
            if (ray.length() <= SIGHT_RANGE && !Util.isLineObstructed(ray)) {
                return true;
            }
        }
        return false;
    }

    private void attack(double timeDelta) {
        if (attackCooldown <= 0) {
            System.out.println("ATTACKING PLAYER");
            Main.mainCharacter.hp -= 1;
            System.out.println("PLAYER HP = " + Main.mainCharacter.hp);
            //this.die();
        }
        attackCooldown -= timeDelta;
    }

    private void die() {
        Main.entities.remove(this);
        Main.drawables.remove(this);
        Main.tickables.remove(this);
        Main.eventAreas.remove(this);
    }

    private Animation getCurrentAnimation() {
        return switch (direction) {
            case NONE -> Animation.PLAYER_IDLE;
            case LEFT -> Animation.PLAYER_WALK_LEFT;
            case RIGHT -> Animation.PLAYER_WALK_RIGHT;
        };
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

    public void dash() {
        if (dashCoolDown <= 0 && direction != Direction.NONE) {
            recoil = switch (direction) {
                case LEFT -> new Vec2D(-DASH_BOOST, 0);
                case RIGHT -> new Vec2D(DASH_BOOST, 0);
                default -> null;
            };
            velocity = new Vec2D(velocity.x(), 0);
            resetRecoil = RECOIL_RESET_DASH;
            dashCoolDown = DASH_COOLDOWN;
            gravityCooldown = TIME_NO_GRAVITY_AFTER_DASH;
        }
    }

}
