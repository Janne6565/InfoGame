package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.math.Hitbox;

import javax.imageio.ImageIO;
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
            graphic = ImageIO.read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
