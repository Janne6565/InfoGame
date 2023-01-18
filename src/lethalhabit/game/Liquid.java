package lethalhabit.game;

import com.google.gson.Gson;
import lethalhabit.technical.Hitbox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class Liquid {

    public static final Map<Integer, Liquid> TILEMAP = new HashMap<>();

    public static void loadLiquids() {
        try {
            String json = new String(Tile.class.getResourceAsStream("/liquids.json").readAllBytes(), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Map<String, Map<String, Object>> raw = gson.fromJson(json, Map.class);
            for (Map.Entry<String, Map<String, Object>> entry : raw.entrySet()) {
                Integer key = Integer.parseInt(entry.getKey());
                Double viscosity = (Double) entry.getValue().get("viscosity");
                Hitbox.Type hitboxType = Hitbox.Type.valueOf((String) entry.getValue().get("hitbox"));
                Liquid liquid = new Liquid(viscosity, hitboxType.hitbox, ImageIO.read(Tile.class.getResourceAsStream("/liquids/liquid" + key + ".png")));
                TILEMAP.put(key, liquid);
            }
        } catch (IOException ex) { }
    }

    public final double viscosity;
    public final Hitbox hitbox;
    public final BufferedImage graphic;

    public Liquid(double viscosity, Hitbox hitbox, BufferedImage graphic) {
        this.viscosity = viscosity;
        this.hitbox = hitbox;
        this.graphic = graphic;
    }

}
