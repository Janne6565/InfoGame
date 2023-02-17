package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.game.Player;
import lethalhabit.game.Tickable;
import lethalhabit.math.Direction;
import lethalhabit.math.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SlashAnimation implements Drawable, Tickable {

    public Point position;
    public float timeSinceStart = 0;
    public Animation animation;


    public SlashAnimation(Direction direction, Point position) {
        this.position = position;
        animation = switch (direction) {
            case LEFT -> Animation.PLAYER_SLASH_LEFT;
            case RIGHT -> Animation.PLAYER_SLASH_RIGHT;
            case NONE -> null;
        };
    }

    @Override
    public void tick(Double timeDelta) {
        timeSinceStart += timeDelta;
        System.out.println(animation.length);
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
        System.out.println(timeSinceStart);
        return animation.getCurrentFrame(timeSinceStart);
    }

    @Override
    public Dimension getSize() {
        return Player.getHitDimensions();
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public int layer() {
        return 0;
    }

    @Override
    public void draw(Graphics graphics) {
        Drawable.super.draw(graphics);
    }
}
