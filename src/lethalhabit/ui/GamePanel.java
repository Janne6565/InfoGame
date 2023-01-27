package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.game.Block;
import lethalhabit.game.Liquid;
import lethalhabit.game.Tile;
import lethalhabit.technical.Point;
import lethalhabit.util.Util;

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
                Point maxPosition = new Point(Main.camera.getRealPosition().x() + (double) Main.camera.WIDTH / 2, Main.camera.getRealPosition().y() + (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
                Point minPosition = new Point(Main.camera.getRealPosition().x() - (double) Main.camera.WIDTH / 2, Main.camera.getRealPosition().y() - (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
                for (Drawable drawable : drawablesInLayer) {
                    Point posLeftTop = drawable.getPosition().minus(drawable.getSize().width, drawable.getSize().height);
                    Point posRightDown = drawable.getPosition().plus(drawable.getSize().width, drawable.getSize().height);
                    if ((posRightDown.compareTo(minPosition) > 0 && posLeftTop.compareTo(maxPosition) < 0)) { // Check if element is inside our camera
                        drawable.draw(g);
                    }
                }
            }
            case Camera.LAYER_MENU -> {
                g.drawString("Main Menu", Main.screenWidth / 2, Main.screenHeight / 2);
            }
            case Camera.LAYER_MAP -> {
                Dimension mapShift = new Dimension((Main.screenWidth - mapImage.getWidth()) / 2, (Main.screenHeight - mapImage.getHeight()) / 2);
                g.drawImage(mapImage, mapShift.width, mapShift.height, null);
                g.setColor(Color.RED);
                int circleRadius = 10;
                g.fillOval((int) (Main.mainCharacter.position.x() * minimapPixelScale) - circleRadius / 2 + mapShift.width, (int) (Main.mainCharacter.position.y() * minimapPixelScale) - circleRadius / 2 + mapShift.height, circleRadius, circleRadius);
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
        int xRange = (int) (Main.camera.WIDTH / Main.TILE_SIZE) + 1;
        int yRange = (int) (Main.camera.getHeight() / Main.TILE_SIZE) + 1;
        double scaledPixelSize = (float) Main.screenWidth / Main.camera.WIDTH;
        Point cameraPositionTopLeft = Main.camera.getRealPosition().minus((double) Main.camera.WIDTH / 2, Main.camera.getHeight() / 2);
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
                            g.drawImage(liquid.graphic, (int) (x * scaledPixelSize), (int) (y * scaledPixelSize), null);
                            if (Main.DEBUG_HITBOX) {
                                Util.drawHitbox(g, liquid.hitbox.shift(i * Main.TILE_SIZE, j * Main.TILE_SIZE));
                            }
                        }
                        Block block = Block.TILEMAP.get(tile.block);
                        if (block != null) {
                            g.drawImage(block.graphic, (int) (x * scaledPixelSize), (int) (y * scaledPixelSize), null);
                            if (Main.DEBUG_HITBOX) {
                                Util.drawHitbox(g, block.hitbox.shift(i * Main.TILE_SIZE, j * Main.TILE_SIZE));
                            }
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
        Integer maxValueX = null;
        Integer maxValueY = null;
        
        for (Map.Entry<Integer, Map<Integer, Tile>> entry : Main.map.entrySet()) {
            if (maxValueX == null) {
                maxValueX = entry.getKey();
            } else {
                maxValueX = Math.max(entry.getKey(), maxValueX);
            }
            for (Map.Entry<Integer, Tile> tileY : entry.getValue().entrySet()) {
                if (maxValueY == null) {
                    maxValueY = tileY.getKey();
                } else {
                    maxValueY = Math.max(tileY.getKey(), maxValueY);
                }
            }
        }
        
        if (maxValueX == null || maxValueY == null) {
            return null;
        }
        
        int tilePixelSize = Math.min(width / maxValueX, height / maxValueY);
        
        BufferedImage map = new BufferedImage((maxValueX + 1) * tilePixelSize, (maxValueY + 1) * tilePixelSize, BufferedImage.TYPE_INT_ARGB);
        minimapPixelScale = tilePixelSize / Main.TILE_SIZE;
        
        for (Map.Entry<Integer, Map<Integer, Tile>> row : Main.map.entrySet()) {
            for (Map.Entry<Integer, Tile> mapTile : row.getValue().entrySet()) {
                Tile tile = mapTile.getValue();
                Block block = Block.TILEMAP.get(tile.block);
                Liquid liquid = Liquid.TILEMAP.get(tile.liquid);
                
                Point position = new Point(row.getKey() * tilePixelSize, mapTile.getKey() * tilePixelSize);
                
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
