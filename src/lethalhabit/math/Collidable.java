package lethalhabit.math;

import lethalhabit.Main;
import lethalhabit.ui.Drawable;

abstract public class Collidable {
    public Hitbox hitbox;
    public Position position;
    public Drawable drawable;

    public double minX;
    public double maxX;
    public double minY;
    public double maxY;


    public Collidable(Hitbox hitbox, Position position, Drawable drawable) {
        Main.collidables.add(this);

        this.hitbox = hitbox;
        this.position = position;
        this.drawable = drawable;
        calulateLimits();
    }

    private double min(double a1, double a2) {
        if (a1 > a2) {
            return a2;
        } else {
            return a1;
        }
    }

    private double max(double a1, double a2) {
        if (a1 < a2) {
            return a2;
        } else {
            return a1;
        }
    }


    private void calulateLimits() {
        minX = hitbox.points[0].x();
        maxX = hitbox.points[0].x();
        minY = hitbox.points[0].y();
        maxY = hitbox.points[0].y();

        for (Position point : hitbox) {
            minX = min(minX, point.x());
            minY = min(minY, point.y());
            maxX = max(maxX, point.x());
            maxY = max(maxY, point.y());
        }
    }

    public Position getMinPosition() {
        return new Position(minX, minY).plus(position);
    }

    public Position getMaxPosition() {
        return new Position(maxX, maxY).plus(position);
    }
}
