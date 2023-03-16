package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.math.Point;
import lethalhabit.util.Util;
import lethalhabit.world.Block;
import lethalhabit.world.Liquid;
import lethalhabit.world.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class Minimap {
    
    public final BufferedImage image;
    public final double scale;
    public final Dimension size;

    public Point positionDrawen;

    public Minimap() {
        Integer maxX = Main.map.keySet().stream().max(Integer::compareTo).orElse(null);
        Integer maxY = Main.map.values().stream().flatMap(column -> column.keySet().stream()).max(Integer::compareTo).orElse(null);
        size = new Dimension((int) (maxX * Main.TILE_SIZE), (int) (maxY * Main.TILE_SIZE));

        if (maxX == null || maxY == null) {
            scale = 0;
            image = null;
        } else {
            int tilePixelSize = Math.min((int) (Main.screenWidth * Main.camera.MAP_SCALE.x()) / maxX, (int) (Main.screenHeight * Main.camera.MAP_SCALE.y()) / maxY);
            BufferedImage map = new BufferedImage((maxX + 1) * tilePixelSize, (maxY + 1) * tilePixelSize, BufferedImage.TYPE_INT_ARGB);

            // Draw Backgrounds
            int maxXBackground = (int) (maxX / Main.BACKGROUND_TILE_SIZE) + 1;
            int maxYBackground = (int) (maxY / Main.BACKGROUND_TILE_SIZE) + 1;
            for (int x = 0; x < maxXBackground; x++) {
                for (int y = 0; y < maxYBackground; y++) {
                    Image background = Util.getImage("/assets/backgrounds/X" + x + "_Y" + y + ".png");
                    Point position = new Point(x * tilePixelSize * Main.BACKGROUND_TILE_SIZE, y * tilePixelSize * Main.BACKGROUND_TILE_SIZE);
                    if (background != null) {
                        map.getGraphics().drawImage(background, (int) position.x(), (int) position.y(), (int) (tilePixelSize * Main.BACKGROUND_TILE_SIZE), (int) (tilePixelSize * Main.BACKGROUND_TILE_SIZE), null);
                    }
                }
            }

            // Draw Tiles
            for (Map.Entry<Integer, Map<Integer, Tile>> column : Main.map.entrySet()) {
                for (Map.Entry<Integer, Tile> row : column.getValue().entrySet()) {
                    Tile tile = row.getValue();
                    Block block = Block.TILEMAP.get(tile.block);
                    Liquid liquid = Liquid.TILEMAP.get(tile.liquid);
                    Point position = new Point(column.getKey() * tilePixelSize, row.getKey() * tilePixelSize);

                    if (liquid != null) {
                        map.getGraphics().drawImage(liquid.graphic, (int) position.x(), (int) position.y(), tilePixelSize, tilePixelSize, null);
                    }
                    if (block != null) {
                        map.getGraphics().drawImage(block.graphic, (int) position.x(), (int) position.y(), tilePixelSize, tilePixelSize, null);
                    }
                }
            }

            map.getGraphics().dispose();
            scale = tilePixelSize / Main.TILE_SIZE;
            image = map;


        }
    }
    
    public void draw(Graphics graphics) {
        int shiftX = (Main.screenWidth - image.getWidth()) / 2;
        int shiftY = (Main.screenHeight - image.getHeight()) / 2;

        positionDrawen = new Point(shiftX, shiftY);

        graphics.drawImage(image, shiftX, shiftY, null);
        graphics.setColor(Color.RED);
        int radius = 10;
        graphics.fillOval((int) (Main.mainCharacter.position.x() * scale) - radius / 2 + shiftX, (int) (Main.mainCharacter.position.y() * scale) - radius / 2 + shiftY, radius, radius);
    }
    
}
