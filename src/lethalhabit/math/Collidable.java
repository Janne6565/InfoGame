package lethalhabit.math;

import lethalhabit.Main;
import lethalhabit.ui.Drawable;

abstract public class Collidable {
    public Hitbox hitbox;
    public Point position;
    public Drawable drawable;

    public double minX;
    public double maxX;
    public double minY;
    public double maxY;


    public Collidable(Hitbox hitbox, Point position, Drawable drawable) {
        Main.collidables.add(this);

        this.hitbox = hitbox;
        this.position = position;
        this.drawable = drawable;
        calulateLimits();
    }

    private double min(double a1, double a2) {
        return Math.min(a1, a2);
    }

    private double max(double a1, double a2) {
        return Math.max(a1, a2);
    }


    private void calulateLimits() {
        minX = hitbox.vertices()[0].x();
        maxX = hitbox.vertices()[0].x();
        minY = hitbox.vertices()[0].y();
        maxY = hitbox.vertices()[0].y();

        for (Point point : hitbox) {
            minX = min(minX, point.x());
            minY = min(minY, point.y());
            maxX = max(maxX, point.x());
            maxY = max(maxY, point.y());
        }
    }

    public Hitbox getHitbox() {
        return hitbox.shiftAll(position);
    }

    public Point getMinPosition() {
        return new Point(minX, minY).plus(position);
    }

    public Point getMaxPosition() {
        return new Point(maxX, maxY).plus(position);
    }
}
