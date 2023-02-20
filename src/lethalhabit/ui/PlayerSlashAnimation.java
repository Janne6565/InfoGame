package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.game.Player;
import lethalhabit.game.Tickable;
import lethalhabit.math.Direction;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;
import lethalhabit.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerSlashAnimation implements Drawable, Tickable {
    
    public float timeSinceStart = 0;
    public Animation animation;
    public Dimension dimension;
    public Direction direction;
    
    public PlayerSlashAnimation(Direction direction, Dimension dimension) {
        this.dimension = dimension;
        this.direction = direction;
        animation = switch (direction) {
            case LEFT -> Animation.PLAYER_SLASH_LEFT;
            case RIGHT -> Animation.PLAYER_SLASH_RIGHT;
            case NONE -> null;
        };
    }
    
    @Override
    public void tick(Double timeDelta) {
        timeSinceStart += timeDelta;
        if (timeSinceStart > animation.length) {
            remove();
        }
    }
    
    public void register() {
        if (animation != null) {
            Main.tickables.add(this);
            Main.drawables.add(this);
        }
    }
    
    public void remove() {
        Main.tickables.removeIf(el -> el == this);
        Main.drawables.removeIf(el -> el == this);
    }
    
    @Override
    public BufferedImage getGraphic() {
        return animation.getCurrentFrame(timeSinceStart);
    }
    
    @Override
    public Dimension getSize() {
        return dimension;
    }
    
    @Override
    public Point getPosition() {
        Point pointBasedOnMotion = switch (direction) {
            case LEFT ->
                    new Point(Main.mainCharacter.hitbox.minX() - Main.mainCharacter.getHitDimensions().width, (Main.mainCharacter.hitbox.maxY() - Main.mainCharacter.hitbox.minY()) - Main.mainCharacter.getHitDimensions().getHeight() / 2 - Main.mainCharacter.getAttackHitbox().minY());
            case RIGHT ->
                    new Point(Main.mainCharacter.hitbox.maxX(), (Main.mainCharacter.hitbox.maxY() - Main.mainCharacter.hitbox.minY()) - Main.mainCharacter.getHitDimensions().getHeight() / 2 - Main.mainCharacter.getAttackHitbox().minY());
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
        Hitbox hitbox = Main.mainCharacter.getAttackHitbox().shift(Main.mainCharacter.position).shift(pointBasedOnMotion);
        return new Point(hitbox.minX(), hitbox.minY());
    }
    
    @Override
    public int layer() {
        return 0;
    }
    
    @Override
    public void draw(Graphics graphics) {
        Drawable.super.draw(graphics);
        if (Main.DEBUG_HITBOX) {
            Util.drawHitbox(graphics, Main.mainCharacter.getAttackHitbox().shift(getPosition()).shift(-Main.mainCharacter.getAttackHitbox().minX(), -Main.mainCharacter.getAttackHitbox().minY()));
        }
    }
    
}
