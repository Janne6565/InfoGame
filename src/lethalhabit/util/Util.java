package lethalhabit.util;

import com.google.gson.Gson;
import lethalhabit.Main;
import lethalhabit.game.Entity;
import lethalhabit.game.Hittable;
import lethalhabit.game.Player;
import lethalhabit.testing.TestEventArea;
import lethalhabit.ui.GamePanel;
import lethalhabit.world.Block;
import lethalhabit.game.EventArea;
import lethalhabit.world.Liquid;
import lethalhabit.world.Tile;
import lethalhabit.math.Hitbox;
import lethalhabit.math.LineSegment;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public final class Util {
    
    private Util() {
    }
    
    /**
     * Reads the world data from an <code>InputStream</code>,
     * converting JSON to a <code>Map</code> where an <code>Integer</code> (column index) maps
     * to a Map, which in return maps <code>Integer</code>s (row indices) to <code>Tile</code>s
     *
     * @param stream an <code>InputStream</code> of the map file (JSON)
     * @return The world, represented as a <code>Map</code> of columns and rows
     */
    public static Map<Integer, Map<Integer, Tile>> readWorldData(InputStream stream) {
        Map<Integer, Map<Integer, Tile>> worldData = new HashMap<>();
        try {
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Map<String, Map<String, Map<String, Object>>> strings = gson.fromJson(json, Map.class);
            for (Map.Entry<String, Map<String, Map<String, Object>>> entry : strings.entrySet()) {
                int key = Integer.parseInt(entry.getKey());
                Map<Integer, Tile> value = new HashMap<>();
                for (Map.Entry<String, Map<String, Object>> entryInner : entry.getValue().entrySet()) {
                    int keyInner = Integer.parseInt(entryInner.getKey());
                    Tile valueInner = new Tile(
                            ((Double) entryInner.getValue().getOrDefault("block", -1D)).intValue(),
                            ((Double) entryInner.getValue().getOrDefault("liquid", -1D)).intValue(),
                            ((Double) entryInner.getValue().getOrDefault("entity", -1D)).intValue(),
                            ((List<Double>) entryInner.getValue().getOrDefault("interactables", new ArrayList<>())).stream().mapToInt(Double::intValue).toArray(),
                            ((List<Double>) entryInner.getValue().getOrDefault("interactablesXPosition", new ArrayList<>())).stream().mapToDouble(Double::valueOf).toArray(),
                            ((List<Double>) entryInner.getValue().getOrDefault("interactablesYPosition", new ArrayList<>())).stream().mapToDouble(Double::valueOf).toArray()
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
    
    public static Map<Integer, Map<Integer, List<EventArea>>> eventAreasFromMap(Map<Integer, Map<Integer, Tile>> map) {
        Map<Integer, Map<Integer, List<EventArea>>> eventAreas = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Tile>> column : map.entrySet()) {
            int x = column.getKey();
            for (Map.Entry<Integer, Tile> row : column.getValue().entrySet()) {
                int y = row.getKey();
                Arrays.stream(row.getValue().interactables).mapToObj(id -> {
                    try {
                        Class<? extends EventArea> eventAreaClass = Main.EVENT_AREA_TYPES.get(id);
                        return Main.EVENT_AREA_TYPES.get(id).getDeclaredConstructor(Point.class).newInstance(new Point(x * Main.TILE_SIZE, y * Main.TILE_SIZE));
                    } catch (Exception ex) {
                        return null;
                    }
                }).filter(Objects::nonNull).forEach(Util::registerEventArea);
                if (row.getValue().entity >= 0) {
                    Class<? extends Entity> entityClass = Main.ENTITY_TYPES.get(row.getValue().entity);
                    Dimension spawnRange;
                    try {
                        spawnRange = (Dimension) entityClass.getField("SPAWN_RANGE").get(null);
                    } catch (Exception ex) {
                        spawnRange = Entity.SPAWN_RANGE;
                    }
                    int minX = (int) (x - (spawnRange.width / 2) / Main.TILE_SIZE);
                    int minY = (int) (y - (spawnRange.height / 2) / Main.TILE_SIZE);
                    try {
                        Entity entity = entityClass.getDeclaredConstructor(Point.class).newInstance(new Point(x, y));
                        EventArea spawnArea = new EventArea(new Point(minX, minY), new Hitbox(new Point[]{
                                new Point(0, 0), new Point(spawnRange.width, 0), new Point(spawnRange.width, spawnRange.height), new Point(0, spawnRange.height)
                        }), null) {
                            @Override
                            public void onEnter(Player player) {
                                entity.spawn();
                            }
                            
                            @Override
                            public void onLeave(Player player) {
                                entity.despawn();
                            }
                        };
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return eventAreas;
    }
    
    public static void registerEventArea(EventArea eventArea) {
        if (Main.eventAreas == null) {
            Main.eventAreas = new HashMap<>();
        }

        Hitbox shiftedHitbox = eventArea.hitbox.shift(eventArea.position);
        int minX = (int) (shiftedHitbox.minX() / Main.TILE_SIZE);
        int maxX = (int) (shiftedHitbox.maxX() / Main.TILE_SIZE);
        int minY = (int) (shiftedHitbox.minY() / Main.TILE_SIZE);
        int maxY = (int) (shiftedHitbox.maxY() / Main.TILE_SIZE);
        for (int x = minX - 1; x < maxX + 1; x++) {
            Map<Integer, List<EventArea>> column = Main.eventAreas.get(x);
            if (column == null) {
                column = new HashMap<>();
            }
            for (int y = minY - 1; y < maxY + 1; y++) {
                List<EventArea> row = column.get(y);
                if (row == null) {
                    row = new ArrayList<>();
                }
                row.add(eventArea);
                column.put(y, row);
            }
            Main.eventAreas.put(x, column);
        }
    }

    public static void removeEventArea(EventArea eventAreaToRemove) {
        if (Main.eventAreas == null) {
            Main.eventAreas = new HashMap<>();
        }

        Hitbox shiftedHitbox = eventAreaToRemove.hitbox.shift(eventAreaToRemove.position);
        int minX = (int) (shiftedHitbox.minX() / Main.TILE_SIZE);
        int maxX = (int) (shiftedHitbox.maxX() / Main.TILE_SIZE);
        int minY = (int) (shiftedHitbox.minY() / Main.TILE_SIZE);
        int maxY = (int) (shiftedHitbox.maxY() / Main.TILE_SIZE);
        for (int x = minX - 1; x < maxX + 1; x++) {
            Map<Integer, List<EventArea>> column = Main.eventAreas.get(x);
            if (column == null) {
                column = new HashMap<>();
            }
            for (int y = minY - 1; y < maxY + 1; y++) {
                List<EventArea> row = column.get(y);
                if (row == null) {
                    row = new ArrayList<>();
                }
                row.removeIf(el -> el == eventAreaToRemove);
                column.put(y, row);
            }
            Main.eventAreas.put(x, column);
        }

    }
    
    
    /**
     * Registers hittable to Map
     * @param hittable hittable to add
     */
    public static void registerHittable(Hittable hittable) {
        if (Main.hittables == null) {
            Main.hittables = new HashMap<>();
        }
        
        Hitbox shiftedHitbox = hittable.getHitbox().shift(hittable.getPosition());
        int minX = (int) (shiftedHitbox.minX() / Main.TILE_SIZE);
        int maxX = (int) (shiftedHitbox.maxX() / Main.TILE_SIZE);
        int minY = (int) (shiftedHitbox.minY() / Main.TILE_SIZE);
        int maxY = (int) (shiftedHitbox.maxY() / Main.TILE_SIZE);
        for (int x = minX - 1; x < maxX + 1; x++) {
            Map<Integer, List<Hittable>> column = Main.hittables.get(x);
            if (column == null) {
                column = new HashMap<>();
            }
            for (int y = minY - 1; y < maxY + 1; y++) {
                List<Hittable> row = column.get(y);
                if (row == null) {
                    row = new ArrayList<>();
                }
                row.add(hittable);
                column.put(y, row);
            }
            Main.hittables.put(x, column);
        }
    }
    
    /**
     * Removes Hittable from Map
     * @param hittable hittable to remove
     */
    public static void removeHittable(Hittable hittable) {
        if (Main.hittables == null) {
            Main.hittables = new HashMap<>();
        }
        
        Hitbox shiftedHitbox = hittable.getHitbox().shift(hittable.getPosition());
        int minX = (int) (shiftedHitbox.minX() / Main.TILE_SIZE);
        int maxX = (int) (shiftedHitbox.maxX() / Main.TILE_SIZE);
        int minY = (int) (shiftedHitbox.minY() / Main.TILE_SIZE);
        int maxY = (int) (shiftedHitbox.maxY() / Main.TILE_SIZE);
        for (int x = minX - 1; x < maxX + 1; x++) {
            Map<Integer, List<Hittable>> column = Main.hittables.get(x);
            if (column == null) {
                column = new HashMap<>();
            }
            for (int y = minY - 1; y < maxY + 1; y++) {
                List<Hittable> row = column.get(y);
                if (row == null) {
                    row = new ArrayList<>();
                }
                row.removeIf(el -> el ==  hittable);
                column.put(y, row);
            }
            Main.hittables.put(x, column);
        }
        
    }
    
    /**
     * Removes Hittable from Map
     * @param hittable hittable to remove
     */
    public static void removeHittable(Hittable hittable, Hitbox hitboxBefore) {
        if (Main.hittables == null) {
            Main.hittables = new HashMap<>();
        }
        
        Hitbox shiftedHitbox = hitboxBefore;
        int minX = (int) (shiftedHitbox.minX() / Main.TILE_SIZE);
        int maxX = (int) (shiftedHitbox.maxX() / Main.TILE_SIZE);
        int minY = (int) (shiftedHitbox.minY() / Main.TILE_SIZE);
        int maxY = (int) (shiftedHitbox.maxY() / Main.TILE_SIZE);
        for (int x = minX - 1; x < maxX + 1; x++) {
            Map<Integer, List<Hittable>> column = Main.hittables.get(x);
            if (column == null) {
                column = new HashMap<>();
            }
            for (int y = minY - 1; y < maxY + 1; y++) {
                List<Hittable> row = column.get(y);
                if (row == null) {
                    row = new ArrayList<>();
                }
                row.removeIf(el -> el ==  hittable);
                column.put(y, row);
            }
            Main.hittables.put(x, column);
        }
        
    }
    
    /**
     * Mirrors an <code>Image</code> horizontally.
     *
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
     *
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
     *
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
     *
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
            return ImageIO.read(Objects.requireNonNull(Util.class.getResourceAsStream(path)));
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static List<Hitbox> getPossibleCollisions(Hitbox hitbox, Vec2D velocity, double timeDelta) {
        Hitbox shiftedHitbox = hitbox.shift(velocity.scale(timeDelta));
        Hitbox totalHitbox = new Hitbox(
                new Point[]{
                        new Point(Math.min(shiftedHitbox.minPosition.x(), hitbox.minPosition.x()), Math.min(shiftedHitbox.minPosition.y(), hitbox.minPosition.y())),
                        new Point(Math.min(shiftedHitbox.minPosition.x(), hitbox.minPosition.x()), Math.max(shiftedHitbox.maxPosition.y(), hitbox.maxPosition.y())),
                        new Point(Math.max(shiftedHitbox.maxPosition.x(), hitbox.maxPosition.x()), Math.max(shiftedHitbox.maxPosition.y(), hitbox.maxPosition.y())),
                        new Point(Math.max(shiftedHitbox.maxPosition.x(), hitbox.maxPosition.x()), Math.min(shiftedHitbox.minPosition.y(), hitbox.minPosition.y()))
                }
        );
        List<Hitbox> possibleCollisions = new ArrayList<>();
        Point minPosition = new Point((int) totalHitbox.vertices[0].x() / Main.TILE_SIZE, (int) totalHitbox.vertices[0].y() / Main.TILE_SIZE);
        Point maxPosition = new Point((int) totalHitbox.vertices[2].x() / Main.TILE_SIZE, (int) totalHitbox.vertices[2].y() / Main.TILE_SIZE);
        
        for (int xIndex = (int) minPosition.x() - 1; xIndex <= maxPosition.x() + 1; xIndex++) {
            for (int yIndex = (int) minPosition.y() - 1; yIndex <= maxPosition.y() + 1; yIndex++) {
                Point position = new Point(xIndex * Main.TILE_SIZE, yIndex * Main.TILE_SIZE);
                Map<Integer, Tile> column = Main.map.get(xIndex);
                if (column != null) {
                    Tile tile = column.get(yIndex);
                    if (tile != null && tile.block >= 0) {
                        Block block = Block.TILEMAP.get(tile.block);
                        if (block != null) {
                            Hitbox newHitbox = block.hitbox.shift(position);
                            possibleCollisions.add(newHitbox);
                        }
                    }
                }
            }
        }
        return possibleCollisions;
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
     *
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
     *
     * @param point       active point
     * @param direction   direction of the point
     * @param lineSegment passive line segment
     * @return the minimum of <code>n</code>, so that moving the point by <code>direction.scale(n)</code> makes it collide
     */
    public static Double factorUntilIntersection(Point point, Vec2D direction, LineSegment lineSegment) {
        Vec2D a = lineSegment.a().loc();
        Vec2D b = lineSegment.b().loc();
        double answer = ((b.x() - a.x()) * (a.y() - point.y()) - (b.y() - a.y()) * (a.x() - point.x())) / (direction.y() * (b.x() - a.x()) - direction.x() * (b.y() - a.y()));
        return Double.isInfinite(answer) ? Double.NaN : answer;
    }
    
    public static void drawHitbox(Graphics graphics, Hitbox hitbox) {
        for (LineSegment segment : hitbox.edges()) {
            drawLineSegment(graphics, segment);
        }
    }
    
    public static void drawLineSegment(Graphics graphics, LineSegment segment) {
        LineSegment relativeLineSegment = segment.minus(Main.camera.getRealPosition()).plus((float) Main.camera.width / 2, Main.camera.getHeight() / 2);
        graphics.setColor(Color.RED);
        graphics.drawLine((int) (relativeLineSegment.a().x() * Main.scaledPixelSize()), (int) (relativeLineSegment.a().y() * Main.scaledPixelSize()), (int) (relativeLineSegment.b().x() * Main.scaledPixelSize()), (int) (relativeLineSegment.b().y() * Main.scaledPixelSize()));
    }
    
    public static boolean isLineObstructed(LineSegment line) {
        Hitbox lineEnvironment = new Hitbox(new Point[]{
                new Point(line.minX(), line.minY()),
                new Point(line.maxX(), line.minY()),
                new Point(line.maxX(), line.maxY()),
                new Point(line.minX(), line.maxY())
        });
        List<Hitbox> possibleCollisions = getPossibleCollisions(lineEnvironment, new Vec2D(0, 0), 0);
        for (Hitbox hitbox : possibleCollisions) {
            for (LineSegment line2 : hitbox.edges()) {
                if (line.intersects(line2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static List<EventArea> getEventAreasPlayerIn(Player player) {
        Hitbox hitbox = player.hitbox.shift(player.position);
        int minX = (int) (hitbox.minX() / Main.TILE_SIZE);
        int maxX = (int) (hitbox.maxX() / Main.TILE_SIZE);
        int minY = (int) (hitbox.minY() / Main.TILE_SIZE);
        int maxY = (int) (hitbox.maxY() / Main.TILE_SIZE);
        List<EventArea> eventAreas = new ArrayList<>();
        for (int x = minX - 1; x < maxX + 1; x++) {
            Map<Integer, List<EventArea>> column = Main.eventAreas.get(x);
            if (column != null) {
                for (int y = minY - 1; y < maxY + 1; y++) {
                    List<EventArea> row = column.get(y);
                    if (row != null) {
                        for (EventArea area : row) {
                            if (!eventAreas.contains(area) && hitbox.intersects(area.hitbox.shift(area.position))) {
                                eventAreas.add(area);
                            }
                        }
                    }
                }
            }
        }
        return eventAreas;
    }
    
    public static List<Hittable> getHittablesInHitbox(Hitbox hitbox) {
        if (Main.hittables == null) {
            Main.hittables = new HashMap<>();
        }
        int minX = (int) (hitbox.minX() / Main.TILE_SIZE);
        int maxX = (int) (hitbox.maxX() / Main.TILE_SIZE);
        int minY = (int) (hitbox.minY() / Main.TILE_SIZE);
        int maxY = (int) (hitbox.maxY() / Main.TILE_SIZE);
        List<Hittable> hittables = new ArrayList<>();
        for (int x = minX - 1; x < maxX + 1; x++) {
            Map<Integer, List<Hittable>> column = Main.hittables.get(x);
            if (column != null) {
                for (int y = minY - 1; y < maxY + 1; y++) {
                    List<Hittable> row = column.get(y);
                    if (row != null) {
                        for (Hittable hittable : row) {
                            if (!hittables.contains(hittable) && hitbox.intersects(hittable.getHitbox().shift(hittable.getPosition()))) {
                                hittables.add(hittable);
                                System.out.println(hittable);
                            }
                        }
                    }
                }
            }
        }
        return hittables;
    }

    public static void exportMapBackgroundTile(Point backgroundTileIndex) {
        BufferedImage imageExport = new BufferedImage((int) (Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * Main.scaledPixelSize()), (int) (Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * Main.scaledPixelSize()), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) imageExport.getGraphics();

        Point minTile = backgroundTileIndex.scale(Main.BACKGROUND_TILE_SIZE);
        Point maxTile = backgroundTileIndex.plus(1, 1).scale(Main.BACKGROUND_TILE_SIZE);

        for (int x = (int) minTile.x(); x < maxTile.x(); x++) {
            for (int y= (int) minTile.y(); y < maxTile.y(); y++) {
                Tile tile = Main.tileAt(x, y);
                Point relativeTilePoint = new Point(x, y).minus(minTile);

                if (tile != null) {
                    Liquid liquid = Liquid.TILEMAP.getOrDefault(tile.liquid, null);
                    if (liquid != null) {
                        graphics.drawImage((Image) liquid.graphic, (int) (relativeTilePoint.x() * Main.TILE_SIZE * Main.scaledPixelSize()), (int) (relativeTilePoint.y() * Main.TILE_SIZE * Main.scaledPixelSize()), (int) (Main.TILE_SIZE * Main.scaledPixelSize()), (int) (Main.TILE_SIZE * Main.scaledPixelSize()), null);
                    }

                    Block block = Block.TILEMAP.getOrDefault(tile.block, null);
                    if (block != null) {
                        graphics.drawImage((Image) block.graphic, (int) (relativeTilePoint.x() * Main.TILE_SIZE * Main.scaledPixelSize()), (int) (relativeTilePoint.y() * Main.TILE_SIZE * Main.scaledPixelSize()), (int) (Main.TILE_SIZE * Main.scaledPixelSize()), (int) (Main.TILE_SIZE * Main.scaledPixelSize()), null);
                    }
                }
            }
        }
        try {
            String path = Main.BACKGROUND_EXPORT_PATH + "Exported_Map_Tile_X" + backgroundTileIndex.x() + "_Y" + backgroundTileIndex.y() + ".png";
            System.out.println(path);
            File outputfile = new File(path);
            ImageIO.write(imageExport, "png", outputfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Integer, Map<Integer, String>> getBackgroundImages() {
        Dimension lastTile = GamePanel.minimap.size;
        Map<Integer, Map<Integer, String>> map = new HashMap<>();

        for (int x = 0; x < lastTile.width / Main.BACKGROUND_TILE_SIZE; x++) {
            Map<Integer, String> mapY = new HashMap<>();
            for (int y = 0; y < lastTile.height / Main.BACKGROUND_TILE_SIZE; y++) {
                String path = "/assets/backgrounds/X" + x + "_Y" + y +".png";
                Image backgroundImage = Util.getImage(path);
                if (backgroundImage != null) {
                    mapY.put(y, path);
                }
            }
            map.put(x, mapY);
        }
        return map;
    }
}