import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Drawable {
    public BufferedImage graphic;
    public float width;
    public float height;

    public position position;
    public boolean relative; // true if it's supposed to move with the camera false if it's supposed to be fixed on the screen (for UI elements in example)

    public void loadImage(String imageUrl) {
        try {
            graphic = ImageIO.read(new File("assets/" + imageUrl));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Drawable(float pWidth, String path, position pos) {
        width = pWidth;
        loadImage(path);
        height = (width / graphic.getWidth()) * graphic.getHeight();
        position = pos;
        Main.drawables.add(this);
    }
    public void draw(Graphics graphics) {
        if (relative) { // Render relative to camera Position
            position cameraPosition = Main.activeCamera.position;
            graphics.drawImage((Image) graphic, (int) position.x, (int) position.y, null);


        } else { // Render absolute (for ui elements or something)

        }
    }
}
