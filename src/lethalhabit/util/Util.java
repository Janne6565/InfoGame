package lethalhabit.util;

import com.google.gson.Gson;
import lethalhabit.Main;
import lethalhabit.game.Tile;
import lethalhabit.technical.Hitbox;
import lethalhabit.technical.LineSegment;
import lethalhabit.technical.Point;
import lethalhabit.technical.Vec2D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Util {
    
    private Util() { }
    
    /**
     * Reads the world data from an <code>InputStream</code>,
     * converting JSON to a <code>Map</code> where an <code>Integer</code> (column index) maps
     * to a Map, which in return maps <code>Integer</code>s (row indices) to <code>Tile</code>s
     * @param stream an <code>InputStream</code> of the map file (JSON)
     * @return The world, represented as a <code>Map</code> of columns and rows
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
     * Mirrors an <code>Image</code> horizontally.
     * @param image <code>Image</code> to be mirrored
     * @return <code>BufferedImage</code> of the mirrored image
     */
    public static BufferedImage mirrorImage(Image image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(null), 0));
        return createTransformed(image, at);
    }
    
    /**
     * Transforms an <code>Image</code> according to an <code>AffineTransform</code>
     * @param image     <code>Image</code> to be transformed
     * @param transform <code>AffineTransform</code> to be applied
     * @return <code>BufferedImage</code> of the transformed image
     */
    private static BufferedImage createTransformed(Image image, AffineTransform transform) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = newImage.createGraphics();
        g.transform(transform);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
    
    /**
     * Loads the image and returns scaled version (based on TILE_SIZE and scaledPixelSize())
     * @param path path of the image
     * @return scaled instance of the image, or <code>null</code> if it can't be loaded
     */
    public static BufferedImage loadScaledTileImage(String path) {
        BufferedImage image = getImage(path);
        if (image == null) {
            return null;
        }
        Image scaled = image.getScaledInstance((int) (Main.TILE_SIZE * Main.scaledPixelSize()), (int) (Main.TILE_SIZE * Main.scaledPixelSize()), Image.SCALE_DEFAULT);
        return bufferedImage(scaled);
    }
    
    /**
     * Converts an <code>Image</code> to a <code>BufferedImage</code>
     * @param image the image to be buffered
     * @return the <code>image</code> as a <code>BufferedImage</code>
     */
    public static BufferedImage bufferedImage(Image image) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(image, 0, 0, null);
        bufferedImage.getGraphics().dispose();
        return bufferedImage;
    }
    
    public static BufferedImage getImageAbsolute(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException ex) {
            return null;
        }
    }
    
    public static BufferedImage getImage(String path) {
        try {
            return ImageIO.read(Util.class.getResourceAsStream(path));
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Calculates the time until first intersection of the active hitbox with any of the passive hitboxes.
     *
     * @param hitbox      active hitbox
     * @param collidables list of passive hitboxes to check
     * @param direction   the direction of the hitbox
     * @return the minimum of <code>n</code>, so that moving the hitbox by <code>direction.scale(n)</code> makes it collide
     */
    public static Double getFirstIntersection(Hitbox hitbox, List<Hitbox> collidables, Vec2D direction) {
        double minTime = Double.NaN;
        for (Hitbox collidable : collidables) {
            for (LineSegment edge : collidable.edges()) {
                for (LineSegment edgeCollidingFor : hitbox.edges()) {
                    Double newTd = minimumFactorUntilIntersection(edge, direction.scale(-1), edgeCollidingFor);
                    if (newTd != null && Double.isFinite(newTd) && !Double.isNaN(newTd)) {
                        if (Double.isNaN(minTime)) {
                            minTime = newTd;
                        }
                        minTime = Math.min(newTd, minTime);
                    }
                }
            }
            
            for (LineSegment edge : hitbox.edges()) {
                for (LineSegment edgeCollidingFor : collidable.edges()) {
                    Double newTd = minimumFactorUntilIntersection(edge, direction, edgeCollidingFor);
                    if (newTd != null && Double.isFinite(newTd) && !Double.isNaN(newTd)) {
                        if (Double.isNaN(minTime)) {
                            minTime = newTd;
                        }
                        minTime = Math.min(newTd, minTime);
                    }
                }
            }
        }
        return minTime;
    }
    
    /**
     * Calculates the time until first intersection of two line segments
     * @param s1        active line
     * @param direction direction of s1
     * @param s2        passive line
     * @return the minimum of <code>n</code>, so that moving s1 by <code>direction.scale(n)</code> makes it collide
     */
    public static Double minimumFactorUntilIntersection(LineSegment s1, Vec2D direction, LineSegment s2) {
        Double min = null;
        for (Point p : s1) {
            double n = factorUntilIntersection(p, direction, s2);
            if (n >= 0 && (min == null || n < min)) {
                Point solution = p.plus(direction.scale(n));
                if (solution.x() >= s2.minX() && solution.x() <= s2.maxX() && solution.y() >= s2.minY() && solution.y() <= s2.maxY()) {
                    min = n;
                }
            }
        }
        
        for (Point p : s2) {
            double n = factorUntilIntersection(p, direction.scale(-1), s1);
            if (n >= 0 && (min == null || n < min)) {
                Point solution = p.minus(direction.scale(n));
                if (solution.x() >= s1.minX() && solution.x() <= s1.maxX() && solution.y() >= s1.minY() && solution.y() <= s1.maxY()) {
                    min = n;
                }
            }
        }
        return min;
    }
    
    /**
     * Calculates the time until first intersection of a point with a line segment.
     * @param point       active point
     * @param direction   direction of the point
     * @param lineSegment passive line segment
     * @return the minimum of <code>n</code>, so that moving the point by <code>direction.scale(n)</code> makes it collide
     */
    public static Double factorUntilIntersection(Point point, Vec2D direction, LineSegment lineSegment) {
        Vec2D a = lineSegment.a().loc();
        Vec2D b = lineSegment.b().loc();
        double answer = ((b.x() - a.x()) * (a.y() - point.y()) - (b.y() - a.y()) * (a.x() - point.x())) / (direction.y() * (b.x() - a.x()) - direction.x() * (b.y() - a.y()));
        if (Double.isInfinite(answer)) {
            return Double.NaN;
        }
        return answer;
    }
    
}
