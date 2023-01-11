package lethalhabit;

import lethalhabit.math.Collidable;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;
import lethalhabit.ui.Drawable;

import java.awt.image.BufferedImage;

public class MapElement extends Collidable {
    public MapElement(Hitbox hitbox, Point position, String path, int width) {
        super(hitbox, position, path, width);
    }

    public MapElement(Hitbox hitbox, Point position, BufferedImage graphic, int width) {
        super(hitbox, position, graphic, width);
    }
}
