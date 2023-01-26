package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.game.Block;
import lethalhabit.game.Liquid;
import lethalhabit.game.Tile;
import lethalhabit.technical.Hitbox;
import lethalhabit.technical.LineSegment;
import lethalhabit.technical.Point;

import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;

//TODO: #2 Maybe change to Layered Panes? Für das GUI um mehrere Ebenen zu haben

/**
 * GamePanel used to draw game elements
 */
public final class GamePanel extends JPanel {
    
    public static final double FRAME_RATE = 280;
    public static BufferedImage mapImage;
    public static double mapPixelSizeRelativeToInGameSize;

    public GamePanel() {
        // Set up the update timer
        Timer updateTimer = new Timer((int) (1000.0 / FRAME_RATE), e -> repaint());
        updateTimer.start();
    }

    public static void generateMap() {
        mapImage = renderMiniMap((int) (Main.screenWidth * Main.camera.MAP_SCALE.x()), (int) (Main.screenHeight * Main.camera.MAP_SCALE.y()));
    }

    @Override
    public void paintComponent(Graphics g) { // this is the stuff that's responsible for drawing all the drawables to the right position (not finished yet)
        super.paintComponent(g);
        Main.tick();
        if (!Main.IS_GAME_LOADING) {
            g.drawString(String.valueOf(Main.mainCharacter.position), 100, 100);

            renderLayer(g, Main.camera.layerRendering);

        } else {
            int height = 40;
            int width = 200;
            g.setColor(Color.BLACK);
            g.drawRect((Main.screenWidth - width) / 2 - 1, (Main.screenHeight - height) / 2 - 1, width + 1, height + 1);
            g.setColor(Main.PROGRESS_BAR_COLOR);
            g.fillRect((Main.screenWidth - width) / 2, (Main.screenHeight - height) / 2, (int) (width * (Block.loadingProgress + Liquid.loadingProgress + Animation.loadingProgress) / 3.0), height);
        }
    }

    public void renderLayer(Graphics g, int layer) {
        ArrayList<Drawable> arrayRenderedDrawables = new ArrayList<>(Main.drawables.stream().filter(drawable -> drawable.layer() == Main.camera.layerRendering).toList());
        switch (layer) {
            case Camera.LAYER_GAME:
                drawMap(g);
                Point maxPosition = new Point(Main.camera.getRealPosition().x() + (double) Main.camera.WIDTH / 2, Main.camera.getRealPosition().y() + (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
                Point minPosition = new Point(Main.camera.getRealPosition().x() - (double) Main.camera.WIDTH / 2, Main.camera.getRealPosition().y() - (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);

                for (Drawable drawable : arrayRenderedDrawables) {
                    Point posLeftTop = drawable.getPosition().minus(drawable.getSize().width, drawable.getSize().height);
                    Point posRightDown = drawable.getPosition().plus(drawable.getSize().width, drawable.getSize().height);
                    if ((posRightDown.compareTo(minPosition) > 0 && posLeftTop.compareTo(maxPosition) < 0)) { // Check if element is inside our camera
                        drawable.draw(g);
                    }
                }
                break;

            case Camera.LAYER_MENU:
                g.drawString("Main Menu", Main.screenWidth / 2, Main.screenHeight / 2);
                break;

            case Camera.LAYER_MAP:
                Dimension mapShiftage = new Dimension((Main.screenWidth - mapImage.getWidth()) / 2, (Main.screenHeight - mapImage.getHeight()) / 2);
                g.drawImage(mapImage,mapShiftage.width, mapShiftage.height, null);
                g.setColor(Color.RED);
                int circleRadius = 10;
                g.fillOval((int) (Main.mainCharacter.position.x() * mapPixelSizeRelativeToInGameSize) - circleRadius / 2 + mapShiftage.width, (int) (Main.mainCharacter.position.y() * mapPixelSizeRelativeToInGameSize) - circleRadius / 2 + mapShiftage.height, circleRadius, circleRadius);
                for (Drawable drawable : arrayRenderedDrawables) {
                    drawable.draw(g);
                }
                break;
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
                                drawHitbox(g, liquid.hitbox.shift(i * Main.TILE_SIZE, j * Main.TILE_SIZE));
                            }
                        }
                        Block block = Block.TILEMAP.get(tile.block);
                        if (block != null) {
                            g.drawImage(block.graphic, (int) (x * scaledPixelSize), (int) (y * scaledPixelSize), null);
                            if (Main.DEBUG_HITBOX) {
                                drawHitbox(g, block.hitbox.shift(i * Main.TILE_SIZE, j * Main.TILE_SIZE));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates an image of the Map
     * @param width the width of the returned image
     * @param height the height of the returned image
     * @return the rendered image
     */
    public static BufferedImage renderMiniMap(int width, int height) {
        Set<Map.Entry<Integer, Map<Integer, Tile>>> tilesX = Main.map.entrySet();

        Integer maxValueX = null;
        Integer maxValueY = null;

        for (Map.Entry<Integer, Map<Integer, Tile>> mapEntry : tilesX) {
            if (maxValueX == null) {
                maxValueX = mapEntry.getKey();
            } else {
                maxValueX = Math.max(mapEntry.getKey(), maxValueX);
            }
            Set<Map.Entry<Integer, Tile>> tilesY = mapEntry.getValue().entrySet();
            for (Map.Entry<Integer, Tile> tileY : tilesY) {
                if (maxValueY == null) {
                    maxValueY = tileY.getKey();
                } else {
                    maxValueY = Math.max(tileY.getKey(), maxValueY);
                }
            }
        }

        if (maxValueX == null || maxValueY == null) {
            try {
                throw(new Exception("Map Equals null"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int tilePixelSize = Math.min(width / maxValueX, height / maxValueY);

        BufferedImage map = new BufferedImage((maxValueX + 1) * tilePixelSize, (maxValueY + 1) * tilePixelSize, BufferedImage.TYPE_INT_ARGB);
        mapPixelSizeRelativeToInGameSize = tilePixelSize / Main.TILE_SIZE;

        for (Map.Entry<Integer, Map<Integer, Tile>> row : tilesX) {
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
        return map;
    }

    public static void drawHitbox(Graphics graphics, Hitbox hitbox) {
        for (LineSegment segment : hitbox.edges()) {
            drawLineSegment(graphics, segment);
        }
    }
    
    public static void drawLineSegment(Graphics graphics, LineSegment segment) {
        LineSegment lineRelativeToCamera = segment.minus(Main.camera.getRealPosition()).plus((float) Main.camera.WIDTH / 2, Main.camera.getHeight() / 2);
        
        graphics.setColor(Color.RED);
        graphics.drawLine((int) (lineRelativeToCamera.a().x() * Main.scaledPixelSize()), (int) (lineRelativeToCamera.a().y() * Main.scaledPixelSize()), (int) (lineRelativeToCamera.b().x() * Main.scaledPixelSize()), (int) (lineRelativeToCamera.b().y() * Main.scaledPixelSize()));
    }
    
}
