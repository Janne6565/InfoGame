package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.Tickable;
import lethalhabit.technical.Point;
import lethalhabit.technical.Vec2D;
import lethalhabit.ui.Drawable;

public class Fireball extends Drawable implements Tickable {

    int direction;
    double speed = 200;

    public Fireball(Point position, int direction) {
        super(20, "fireball.png", position);
        Main.tickables.add(this);
        this.direction = direction;
        if (direction == 0) {
            delete();
        }
    }

    @Override
    public void tick(Double timeDelta) {
        Vec2D velocity = new Vec2D(0,0);
        switch (direction) {
            case 1:
                velocity = new Vec2D(speed, 0);
                break;
            case -1:
                velocity = new Vec2D(-speed, 0);
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
        Main.drawables.remove(this);
    }
}
