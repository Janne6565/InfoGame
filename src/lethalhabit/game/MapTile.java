package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.math.Hitbox;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class MapTile {
    public Hitbox hitbox;
    public BufferedImage graphic;

    public MapTile(Hitbox hitbox, BufferedImage bufferedImage) {
        this.hitbox = hitbox;
        graphic = bufferedImage;


    }

    public MapTile(Hitbox hitbox, String path) {
        InputStream stream = Main.class.getResourceAsStream("resources/tiles/" + path);
        this.hitbox = hitbox;
        try {
            assert stream != null;
            BufferedImage baseImage = ImageIO.read(Main.class.getResourceAsStream("/tiles/" + path));
            int width = (int) (Main.tileSize * Main.scaledPixelSize());
            int height = (int) (Main.tileSize * Main.scaledPixelSize());
            Image frame = baseImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage image = new BufferedImage(frame.getWidth(null), frame.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
            image.getGraphics().drawImage(frame, 0, 0, null);
            graphic = image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
