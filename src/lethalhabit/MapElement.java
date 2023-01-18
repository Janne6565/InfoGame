package lethalhabit;

import lethalhabit.technical.Collidable;
import lethalhabit.technical.Hitbox;
import lethalhabit.technical.Point;

import java.awt.image.BufferedImage;

public class MapElement extends Collidable {
    public MapElement(Hitbox hitbox, Point position, String path, int width) {
        super(hitbox, position, path, width);
    }

    public MapElement(Hitbox hitbox, Point position, BufferedImage graphic, int width) {
        super(hitbox, position, graphic, width);
    }
}
