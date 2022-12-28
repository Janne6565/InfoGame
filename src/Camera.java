import java.awt.*;
import java.util.ArrayList;

public class Camera {
    public position position;
    public float width;

    public Camera(position pPosition, float pWidth) {
        position = pPosition;
        width = pWidth;
    }

    public void draw(ArrayList<Drawable> drawables, Graphics2D graphics2D) {
        for (Drawable draw : drawables) {
            graphics2D.drawImage((Image) draw.graphic, (int) draw.position.x, (int) draw.position.y, null);
        }
    }
}
