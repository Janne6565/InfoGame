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
    public boolean relative = true; // true if it's supposed to move with the camera false if it's supposed to be fixed on the screen (for UI elements in example)

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

            float pixelPerPixel = (float) (Main.screenWidth) / (float) (Main.getScreenWidthGame());
            int posXDisplay = (int) ((int) ((position.x - cameraPosition.x) * pixelPerPixel + (Main.screenWidth / 2)) - (width * pixelPerPixel) / 2);
            int posYDisplay = (int) ((int) ((position.y - cameraPosition.y) * pixelPerPixel + (Main.screenHeight / 2)) - (height * pixelPerPixel) / 2);

            Image img = graphic.getScaledInstance((int) (width * pixelPerPixel), (int) (height * pixelPerPixel), Image.SCALE_FAST);
            graphics.drawImage((Image) img, posXDisplay, posYDisplay, (int) (width * pixelPerPixel), (int) (height * pixelPerPixel), null);


        } else { // Render absolute (for ui elements or something)
            float pixelPerPixel = (float) (Main.screenWidth) / (float) (Main.getScreenWidthGame());
            int posXDisplay = (int) ((int) (position.x) * pixelPerPixel + (Main.screenWidth / 2) - (width * pixelPerPixel) / 2);
            int posYDisplay = (int) ((int) (position.y) * pixelPerPixel + (Main.screenHeight / 2) - (height * pixelPerPixel) / 2);

            Image img = graphic.getScaledInstance((int) (width * pixelPerPixel), (int) (height * pixelPerPixel), Image.SCALE_FAST);
            graphics.drawImage((Image) img, posXDisplay, posYDisplay, (int) (width * pixelPerPixel), (int) (height * pixelPerPixel), null);
        }
    }
}
