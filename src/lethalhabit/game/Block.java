package lethalhabit.game;

import com.google.gson.Gson;
import lethalhabit.Main;
import lethalhabit.Util;
import lethalhabit.technical.Hitbox;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class Block {

    public static final Map<Integer, Block> TILEMAP = new HashMap<>();

    public static void loadBlocks() {
        try {
            String json = new String(Tile.class.getResourceAsStream("/blocks.json").readAllBytes(), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Map<String, Map<String, Object>> raw = gson.fromJson(json, Map.class);
            for (Map.Entry<String, Map<String, Object>> entry : raw.entrySet()) {
                Integer key = Integer.parseInt(entry.getKey());
                Hitbox.Type hitboxType = Hitbox.Type.valueOf((String) entry.getValue().get("hitbox"));
                Block block = new Block(hitboxType.hitbox, Util.loadScaledTileImage("/tiles/tile" + key + ".png"));
                TILEMAP.put(key, block);
            }
        } catch (IOException ex) { }
    }

    public final Hitbox hitbox;
    public final BufferedImage graphic;

    public Block(Hitbox hitbox, BufferedImage graphic) {
        this.hitbox = hitbox;
        this.graphic = graphic;
    }

}
