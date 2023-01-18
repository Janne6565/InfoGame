package lethalhabit;

import com.google.gson.Gson;
import lethalhabit.game.Tile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class Util {

    private Util() {}

    /**
     * Converts JSON to HashMap
     * @param stream Stream of the JSON File
     * @return A decoded Map that represents like this: map[xPosition][yPosition]
     */
    public static Map<Integer, Map<Integer, Tile>> readWorldData(InputStream stream) {
        Map<Integer, Map<Integer, Tile>> worldData = new HashMap<>();
        try {
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Map<String, Map<String, Map<String, Double>>> strings = gson.fromJson(json, Map.class);
            for (Map.Entry<String, Map<String, Map<String, Double>>> entry : strings.entrySet()) {
                int key = Integer.parseInt(entry.getKey());
                Map<Integer, Tile> value = new HashMap<>();
                for (Map.Entry<String, Map<String, Double>> entryInner : entry.getValue().entrySet()) {
                    int keyInner = Integer.parseInt(entryInner.getKey());
                    Tile valueInner = new Tile(
                            entryInner.getValue().getOrDefault("block", -1D).intValue(),
                            entryInner.getValue().getOrDefault("liquid", -1D).intValue(),
                            entryInner.getValue().getOrDefault("interactable", -1D).intValue()
                    );
                    value.put(keyInner, valueInner);
                }
                worldData.put(key, value);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "World data could not be loaded.", "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return worldData;
    }


    /**
     * Mirrors Image
     * @param image Image that needs to be Mirrored
     * @return Mirrored Image
     */
    public static BufferedImage mirrorImage(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(),0 ));
        return createTransformed(image, at);
    }

    private static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }


    /**
     * Loads Image and returns scaled version (based on TILE_SIZE and ScaledPixelSize())
     * @param path Path of the Image
     * @return Scaled BufferdImage
     * @throws IOException if Image cant be found on the specified path
     */
    public static BufferedImage loadScaledTileImage(String path) throws IOException {
        BufferedImage image = ImageIO.read(Util.class.getResourceAsStream(path));
        Image scaled = image.getScaledInstance((int) (Main.TILE_SIZE * Main.scaledPixelSize()), (int) (Main.TILE_SIZE * Main.scaledPixelSize()), Image.SCALE_DEFAULT);
        BufferedImage scaledBuffered = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
        scaledBuffered.getGraphics().drawImage(scaled, 0, 0, null);
        scaledBuffered.getGraphics().dispose();
        return scaledBuffered;
    }

}
