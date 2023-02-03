package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.game.Block;
import lethalhabit.game.Liquid;
import lethalhabit.game.Tile;
import lethalhabit.technical.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

//TODO: #2 Maybe change to Layered Panes? Für das GUI um mehrere Ebenen zu haben

/**
 * GamePanel used to draw game elements
 */
public final class GamePanel extends JPanel {
    
    public static final double FRAME_RATE = 280;
    public static BufferedImage mapImage;
    public static double minimapPixelScale;
    
    public GamePanel() {
        // Set up the update timer
        Timer updateTimer = new Timer((int) (1000.0 / FRAME_RATE), e -> repaint());
        updateTimer.start();
    }
    
    public static void generateMap() {
        mapImage = generateMinimap((int) (Main.screenWidth * Main.camera.MAP_SCALE.x()), (int) (Main.screenHeight * Main.camera.MAP_SCALE.y()));
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Main.tick();
        if (Main.IS_GAME_LOADING) {
            int height = 40;
            int width = 200;
            g.setColor(Color.BLACK);
            g.drawRect((Main.screenWidth - width) / 2 - 1, (Main.screenHeight - height) / 2 - 1, width + 1, height + 1);
            g.setColor(Main.PROGRESS_BAR_COLOR);
            g.fillRect((Main.screenWidth - width) / 2, (Main.screenHeight - height) / 2, (int) (width * (Block.loadingProgress + Liquid.loadingProgress + Animation.loadingProgress) / 3.0), height);
        } else {
            g.drawString(String.valueOf(Main.mainCharacter.position), 100, 100);
            renderLayer(g, Main.camera.layerRendering);
        }
    }
    
    public void renderLayer(Graphics g, int layer) {
        List<Drawable> drawablesInLayer = Main.drawables.stream().filter(drawable -> drawable.layer() == Main.camera.layerRendering).toList();
        switch (layer) {
            case Camera.LAYER_GAME -> {
                drawMap(g);
                Point maxTotal = new Point(Main.camera.getRealPosition().x() + (double) Main.camera.width / 2, Main.camera.getRealPosition().y() + (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
                Point minTotal = new Point(Main.camera.getRealPosition().x() - (double) Main.camera.width / 2, Main.camera.getRealPosition().y() - (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
                for (Drawable drawable : drawablesInLayer) {
                    Point minDrawable = drawable.getPosition().minus(drawable.getSize().width, drawable.getSize().height);
                    Point maxDrawable = drawable.getPosition().plus(drawable.getSize().width, drawable.getSize().height);
                    if ((maxDrawable.compareTo(minTotal) > 0 && minDrawable.compareTo(maxTotal) < 0)) { // Check if element is inside our camera
                        drawable.draw(g);
                    }
                }
            }
            case Camera.LAYER_MENU -> {
                g.drawString("Main Menu", Main.screenWidth / 2, Main.screenHeight / 2);
            }
            case Camera.LAYER_MAP -> {
                int shiftX = (Main.screenWidth - mapImage.getWidth()) / 2;
                int shiftY = (Main.screenHeight - mapImage.getHeight()) / 2;
                g.drawImage(mapImage, shiftX, shiftY, null);
                g.setColor(Color.RED);
                int radius = 10;
                g.fillOval((int) (Main.mainCharacter.position.x() * minimapPixelScale) - radius / 2 + shiftX, (int) (Main.mainCharacter.position.y() * minimapPixelScale) - radius / 2 + shiftY, radius, radius);
                for (Drawable drawable : drawablesInLayer) {
                    drawable.draw(g);
                }
            }
        }
    }
    
    /**
     * Draws the physical map (blocks and liquids).
     */
    private void drawMap(Graphics g) {
        int xRange = (int) (Main.camera.width / Main.TILE_SIZE) + 1;
        int yRange = (int) (Main.camera.getHeight() / Main.TILE_SIZE) + 1;
        Point cameraPositionTopLeft = Main.camera.getRealPosition().minus((double) Main.camera.width / 2, Main.camera.getHeight() / 2);
        Point indexTopLeft = cameraPositionTopLeft.scale(1 / Main.TILE_SIZE).minus(1, 1);
        for (int i = (int) indexTopLeft.x() - 1; i <= xRange + indexTopLeft.x() + 1; i++) {
            for (int j = (int) indexTopLeft.y() - 1; j <= yRange + indexTopLeft.y() + 1; j++) {
                int x = (int) (i * Main.TILE_SIZE - cameraPositionTopLeft.x());
                int y = (int) (j * Main.TILE_SIZE - cameraPositionTopLeft.y());
                Map<Integer, Tile> column = Main.map.get(i);
                if (column != null) {
                    Tile tile = column.get(j);
                    if (tile != null) {
                        Liquid liquid = Liquid.TILEMAP.get(tile.liquid);
                        if (liquid != null) {
                            g.drawImage(liquid.graphic, (int) (x * Main.scaledPixelSize()), (int) (y * Main.scaledPixelSize()), null);
                        }
                        Block block = Block.TILEMAP.get(tile.block);
                        if (block != null) {
                            g.drawImage(block.graphic, (int) (x * Main.scaledPixelSize()), (int) (y * Main.scaledPixelSize()), null);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Creates an image of the Map
     * @param width  the width of the returned image
     * @param height the height of the returned image
     * @return the rendered image
     */
    public static BufferedImage generateMinimap(int width, int height) {
        Integer maxX = null;
        Integer maxY = null;
        for (Map.Entry<Integer, Map<Integer, Tile>> entry : Main.map.entrySet()) {
            if (maxX == null) {
                maxX = entry.getKey();
            } else {
                maxX = Math.max(entry.getKey(), maxX);
            }
            for (Map.Entry<Integer, Tile> tileY : entry.getValue().entrySet()) {
                if (maxY == null) {
                    maxY = tileY.getKey();
                } else {
                    maxY = Math.max(tileY.getKey(), maxY);
                }
            }
        }
        
        if (maxX == null || maxY == null) {
            return null;
        }
        
        int tilePixelSize = Math.min(width / maxX, height / maxY);
        
        BufferedImage map = new BufferedImage((maxX + 1) * tilePixelSize, (maxY + 1) * tilePixelSize, BufferedImage.TYPE_INT_ARGB);
        minimapPixelScale = tilePixelSize / Main.TILE_SIZE;
        
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
        return map;
    }
    
}
