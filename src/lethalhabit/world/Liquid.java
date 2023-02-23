package lethalhabit.world;

import com.google.gson.Gson;
import lethalhabit.util.Util;
import lethalhabit.math.Hitbox;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class Liquid {
    
    public static final Map<Integer, Liquid> TILEMAP = new HashMap<>();
    
    public static double loadingProgress = 0;
    
    public static void loadLiquids() {
        try {
            String json = new String(Liquid.class.getResourceAsStream("/liquids.json").readAllBytes(), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Map<String, Map<String, Object>> raw = gson.fromJson(json, Map.class);
            for (Map.Entry<String, Map<String, Object>> entry : raw.entrySet()) {
                Integer key = Integer.parseInt(entry.getKey());
                Double viscosity = (Double) entry.getValue().get("viscosity");
                Double gravity = (Double) entry.getValue().get("gravity");
                Double maxGravity = (Double) entry.getValue().get("maxGravity");
                Hitbox.Type hitboxType = Hitbox.Type.valueOf((String) entry.getValue().get("hitbox"));
                Liquid liquid = new Liquid(viscosity, gravity, maxGravity, hitboxType.hitbox, Util.loadScaledTileImage("/assets/liquids/liquid" + key + ".png"));
                TILEMAP.put(key, liquid);
                loadingProgress += 1.0 / raw.size();
            }
        } catch (IOException ex) {
        }
    }
    
    public final double viscosity;
    public final double gravity;
    public final double maxGravity;
    public final Hitbox hitbox;
    public final BufferedImage graphic;
    
    public Liquid(double viscosity, double gravity, double maxGravity, Hitbox hitbox, BufferedImage graphic) {
        this.gravity = gravity;
        this.viscosity = viscosity;
        this.maxGravity = maxGravity;
        this.hitbox = hitbox;
        this.graphic = graphic;
    }
    
}
