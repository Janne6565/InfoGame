package lethalhabit.math;

import lethalhabit.Main;
import lethalhabit.ui.Drawable;

import java.awt.*;
import java.awt.image.BufferedImage;

abstract public class Collidable {
    public Hitbox hitbox;
    public Point position;
    public Drawable drawable;

    public double minX;
    public double maxX;
    public double minY;
    public double maxY;


    public Collidable(Hitbox hitbox, Point position, String path, int width) {
        Main.collidables.add(this);

        this.hitbox = hitbox;
        this.position = position;
        Point pos = position;
        this.drawable = new Drawable(width, path, position) {
            public void draw(Graphics g) {
                super.draw(g);
                super.position = pos;
                if (Main.debugHitbox) {
                    for (LineSegment line : hitbox.shiftAll(position).edges()) {
                        Point positionA = convertPositionToCamera(line.a());
                        Point positionB = convertPositionToCamera(line.b());
                        Graphics2D g2 = (Graphics2D) g;

                        g2.setColor(Main.strokeColorCollidable);
                        g2.setStroke(new BasicStroke(Main.strokeSize));
                        g2.drawLine((int) positionA.x(), (int) positionA.y(), (int) positionB.x(), (int) positionB.y());
                    }
                }

            }

            private Point convertPositionToCamera(Point position) {
                double pixelPerPixel = (double) Main.screenWidth / (double) Main.getScreenWidthGame();
                double offsetX = relative ? Main.camera.position.x() : 0;
                double offsetY = relative ? Main.camera.position.y() : 0;
                int posXDisplay = (int) ((int) (position.x() - offsetX) * pixelPerPixel + (Main.screenWidth / 2));
                int posYDisplay = (int) ((int) (position.y() - offsetY) * pixelPerPixel + (Main.screenHeight / 2));
                return new Point(posXDisplay, posYDisplay);
            }
        };
        calulateLimits();
    }

    public Collidable(Hitbox hitbox, Point position, BufferedImage graphic, int width) {
        Main.collidables.add(this);

        this.hitbox = hitbox;
        this.position = position;
        Point pos = position;
        this.drawable = new Drawable(width, graphic, position) {
            public void draw(Graphics g) {
                super.draw(g);
                super.position = pos;
                if (Main.debugHitbox) {
                    for (LineSegment line : hitbox.shiftAll(position).edges()) {
                        Point positionA = convertPositionToCamera(line.a());
                        Point positionB = convertPositionToCamera(line.b());
                        Graphics2D g2 = (Graphics2D) g;

                        g2.setColor(Main.strokeColorCollidable);
                        g2.setStroke(new BasicStroke(Main.strokeSize));
                        g2.drawLine((int) positionA.x(), (int) positionA.y(), (int) positionB.x(), (int) positionB.y());
                    }
                }
            }

            private Point convertPositionToCamera(Point position) {
                double pixelPerPixel = (double) Main.screenWidth / (double) Main.getScreenWidthGame();
                double offsetX = relative ? Main.camera.position.x() : 0;
                double offsetY = relative ? Main.camera.position.y() : 0;
                int posXDisplay = (int) ((int) (position.x() - offsetX) * pixelPerPixel + (Main.screenWidth / 2));
                int posYDisplay = (int) ((int) (position.y() - offsetY) * pixelPerPixel + (Main.screenHeight / 2));
                return new Point(posXDisplay, posYDisplay);
            }
        };
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
        Hitbox hitboxDisplaced = new Hitbox(hitbox.vertices());
        return hitboxDisplaced.shiftAll(position);
    }

    public Point getMinPosition() {
        return new Point(minX, minY).plus(position);
    }

    public Point getMaxPosition() {
        return new Point(maxX, maxY).plus(position);
    }
}
