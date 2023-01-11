package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.MapElement;
import lethalhabit.math.Collidable;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tile {

    public static final Tile EMPTY = new Tile(-1, -1, -1);

    public static final Hitbox HITBOX_1x1 = new Hitbox(new Point[]{
            new Point(0,0),
            new Point(0, Main.tileSize),
            new Point(Main.tileSize, Main.tileSize),
            new Point(Main.tileSize, 0)
    });

    public static final Map<Integer, MapTile> TILEMAP = new HashMap<>();

    public final int block;
    public final int liquid;
    public final int interactable;

    public static void loadMapTiles() {
        // Add your other tiles like this: TILEMAP.put(0, new MapTile(HITBOX_1x1, "0.png"));

        for (int i = 0; i < 47; i++) {
            TILEMAP.put(i, new MapTile(HITBOX_1x1, "tile" + i + ".png"));
        }
    }

    public Tile(int block, int liquid, int interactable) {
        this.block = block;
        this.liquid = liquid;
        this.interactable = interactable;
    }

    public Tile(Tile other) {
        this(other.block, other.liquid, other.interactable);
    }
}
