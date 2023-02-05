package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.math.Direction;
import lethalhabit.ui.Camera;
import lethalhabit.util.Util;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.Drawable;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Fireball implements Tickable, Drawable {
    
    public static final double SPEED = 200;
    
    public static final Dimension SIZE = new Dimension(20, 20);
    public static final BufferedImage IMAGE = Util.getImage("/assets/fireball.png");
    
    public Point position;
    public Direction direction;
    
    public Fireball(Point position, Direction direction) {
        this.position = position;
        this.direction = direction;
        Main.tickables.add(this);
        Main.drawables.add(this);
        if (direction == Direction.NONE) {
            delete();
        }
    }
    
    @Override
    public void tick(Double timeDelta) {
        new Vec2D(0, 0);
        Vec2D velocity = switch (direction) {
            case RIGHT -> new Vec2D(SPEED, 0);
            case LEFT -> new Vec2D(-SPEED, 0);
            default -> new Vec2D(0, 0);
        };
        Point distanceTraveled = new Point(velocity.scale(timeDelta).x(), velocity.scale(timeDelta).y());
        position = position.plus(distanceTraveled);
        if (position.distance(Main.mainCharacter.position) > 600) {
            delete();
        }
    }
    
    public void delete() {
        Main.tickables.remove(this);
        Main.drawables.remove(this);
    }
    
    @Override
    public BufferedImage getGraphic() {
        return IMAGE;
    }
    
    @Override
    public Dimension getSize() {
        return SIZE;
    }
    
    @Override
    public Point getPosition() {
        return position;
    }
    
    @Override
    public int layer() {
        return Camera.LAYER_GAME;
    }
    
}
