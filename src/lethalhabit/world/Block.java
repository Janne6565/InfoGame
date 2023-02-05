package lethalhabit.world;

import com.google.gson.Gson;
import lethalhabit.util.Util;
import lethalhabit.math.Hitbox;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class Block {
    
    public static final Map<Integer, Block> TILEMAP = new HashMap<>();
    
    public static double loadingProgress = 0;
    
    public static void loadBlocks() {
        try {
            String json = new String(Tile.class.getResourceAsStream("/blocks.json").readAllBytes(), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Map<String, Map<String, Object>> raw = gson.fromJson(json, Map.class);
            for (Map.Entry<String, Map<String, Object>> entry : raw.entrySet()) {
                Integer key = Integer.parseInt(entry.getKey());
                Hitbox.Type hitboxType = Hitbox.Type.valueOf((String) entry.getValue().get("hitbox"));
                Block block = new Block(hitboxType.hitbox, Util.loadScaledTileImage("/assets/tiles/tile" + key + ".png"));
                TILEMAP.put(key, block);
                loadingProgress += 1.0 / raw.size();
            }
        } catch (IOException ex) {
        }
    }
    
    public final Hitbox hitbox;
    public final BufferedImage graphic;
    
    public Block(Hitbox hitbox, BufferedImage graphic) {
        this.hitbox = hitbox;
        this.graphic = graphic;
    }
    
}
