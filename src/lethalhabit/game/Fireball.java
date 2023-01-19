package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.Tickable;
import lethalhabit.Util;
import lethalhabit.technical.Point;
import lethalhabit.technical.Vec2D;
import lethalhabit.ui.Drawable;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Fireball implements Tickable, Drawable {
    
    public static final double SPEED = 200;
    
    public static final Dimension SIZE = new Dimension(20, 20);
    public static final BufferedImage IMAGE = Util.getImage("/fireball.png");
    
    public Point position;
    public int direction;
    
    public Fireball(Point position, int direction) {
        this.position = position;
        this.direction = direction;
        Main.tickables.add(this);
        if (direction == 0) {
            delete();
        }
    }
    
    @Override
    public void tick(Double timeDelta) {
        Vec2D velocity = new Vec2D(0, 0);
        switch (direction) {
            case 1:
                velocity = new Vec2D(SPEED, 0);
                break;
            case -1:
                velocity = new Vec2D(-SPEED, 0);
                break;
        }
        Point distanceTraveled = new Point(velocity.scale(timeDelta).x(), velocity.scale(timeDelta).y());
        position = position.plus(distanceTraveled);
        if (position.getDistance(Main.mainCharacter.position) > 600) {
            delete();
        }
    }
    
    public void delete() {
        Main.tickables.remove(this);
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
    public boolean isRelative() {
        return true;
    }
    
}
