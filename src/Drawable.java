import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Drawable {
    public BufferedImage graphic;
    public float width;
    public position position;
    public boolean relative; // true if its supposed to move with the camera false if its supposed to be fixed on the screen (for UI elements in example)


    public Drawable(float pWidth, String path, position pos) {
        width = pWidth;
        try {
            graphic = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        position = pos;
        Main.drawables.add(this);
    }
}
