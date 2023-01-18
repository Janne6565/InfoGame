package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.technical.Hitbox;
import lethalhabit.technical.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Tile {

    public static final Tile EMPTY = new Tile(-1, -1, -1);

    public final int block;
    public final int liquid;
    public final int interactable;

    public Tile(int block, int liquid, int interactable) {
        this.block = block;
        this.liquid = liquid;
        this.interactable = interactable;
    }

    public Tile(Tile other) {
        this(other.block, other.liquid, other.interactable);
    }
}
