package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.math.Point;

import java.awt.*;
import java.util.ArrayList;

public final class Camera {
    
    public Point position;
    public int width;
    public int threshhold;

    public Camera(Point position, int width, int threshhold) {
        this.position = position;
        this.width = width;
        this.threshhold = threshhold;
    }
    
    public void draw(ArrayList<Drawable> drawables, Graphics2D graphics2D) {
        for (Drawable draw : drawables) {
            graphics2D.drawImage(draw.graphic, (int) draw.position.x(), (int) draw.position.y(), null);
        }
    }

    public double getHeight() {
        return (float) width / Main.screenWidth * Main.screenHeight;
    }
    
}
