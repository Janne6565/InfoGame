package lethalhabit.ui;

import lethalhabit.math.Point;

import java.awt.*;
import java.util.ArrayList;

public final class Camera {
    
    public Point position;
    public int width;
    
    public Camera(Point position, int width) {
        this.position = position;
        this.width = width;
    }
    
    public void draw(ArrayList<Drawable> drawables, Graphics2D graphics2D) {
        for (Drawable draw : drawables) {
            graphics2D.drawImage(draw.graphic, (int) draw.position.x(), (int) draw.position.y(), null);
        }
    }
    
}
