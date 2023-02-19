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
        Point position = Main.mainCharacter.getPosition();
        Point pointBasedOnMotion = switch (direction) {
            case LEFT -> new Point(Main.mainCharacter.hitbox.minX() - (Player.HIT_HITBOX.maxX() - Player.HIT_HITBOX.minX()), (Main.mainCharacter.hitbox.maxY() - Main.mainCharacter.hitbox.minY()) - (Player.HIT_HITBOX.maxY() - Player.HIT_HITBOX.minY()) / 2 - Player.HIT_HITBOX.minY());
            case RIGHT -> new Point(Main.mainCharacter.hitbox.maxX(), (Main.mainCharacter.hitbox.maxY() - Main.mainCharacter.hitbox.minY()) - (Player.HIT_HITBOX.maxY() - Player.HIT_HITBOX.minY()) / 2 - Player.HIT_HITBOX.minY());
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
        Hitbox hitbox = Player.HIT_HITBOX.shift(position).shift(pointBasedOnMotion);
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
            Util.drawHitbox(graphics, Player.HIT_HITBOX.shift(getPosition()).shift(-Player.HIT_HITBOX.minX(), -Player.HIT_HITBOX.minY()));
        }
    }
}
