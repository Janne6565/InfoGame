package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.Player;
import lethalhabit.game.Block;
import lethalhabit.game.Liquid;
import lethalhabit.game.Tile;
import lethalhabit.technical.Hitbox;
import lethalhabit.technical.LineSegment;
import lethalhabit.technical.Point;

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

//TODO: #2 Maybe change to Layered Panes? Für das GUI um mehrere Ebenen zu haben

/**
 * GamePanel used to draw game elements
 */
public final class GamePanel extends JPanel {
    
    public static final double FRAME_RATE = 144;
    
    public GamePanel() {
        // Set up the update timer
        Timer updateTimer = new Timer((int) (1000.0 / FRAME_RATE), e -> repaint());
        updateTimer.start();
    }
    
    @Override
    public void paintComponent(Graphics g) { // this is the stuff that's responsible for drawing all the drawables to the right position (not finished yet)
        super.paintComponent(g);
        if (Main.IS_GAME_RUNNING) {
            Main.tick();
            drawMap(g);
            Point maxPosition = new Point(Main.camera.getRealPosition().x() + (double) Main.camera.width / 2, Main.camera.getRealPosition().y() + (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
            Point minPosition = new Point(Main.camera.getRealPosition().x() - (double) Main.camera.width / 2, Main.camera.getRealPosition().y() - (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
            
            for (Drawable drawable : new ArrayList<>(Main.drawables)) {
                Point posLeftTop = drawable.getPosition().minus(drawable.getSize().width, drawable.getSize().height);
                Point posRightDown = drawable.getPosition().plus(drawable.getSize().width, drawable.getSize().height);
                if ((posRightDown.compareTo(minPosition) > 0 && posLeftTop.compareTo(maxPosition) < 0) || !drawable.isRelative()) { // Check if element is inside our camera
                    drawable.draw(g);
                }
            }
        } else {
            int height = 40;
            int width = 200;
            g.setColor(Color.BLACK);
            g.drawRect((Main.screenWidth - width) / 2 - 1, (Main.screenHeight - height) / 2 - 1, width + 1, height + 1);
            g.setColor(Main.PROGRESS_BAR_COLOR);
            g.fillRect((Main.screenWidth - width) / 2, (Main.screenHeight - height) / 2, (int) (width * (Block.loadingProgress + Liquid.loadingProgress + Animation.loadingProgress) / 3.0), height);
        }
    }
    
    /**
     * Draws the physical map (blocks and liquids).
     */
    private void drawMap(Graphics g) {
        if (Main.IS_GAME_RUNNING) {
            int xRange = (int) (Main.camera.width / Main.TILE_SIZE) + 1;
            int yRange = (int) (Main.camera.getHeight() / Main.TILE_SIZE) + 1;
            double scaledPixelSize = (float) Main.screenWidth / Main.camera.width;
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
    }
    
    public static void drawHitbox(Graphics graphics, Hitbox hitbox) {
        for (LineSegment segment : hitbox.edges()) {
            drawLineSegment(graphics, segment);
        }
    }
    
    public static void drawLineSegment(Graphics graphics, LineSegment segment) {
        LineSegment lineRelativeToCamera = segment.minus(Main.camera.getRealPosition()).plus((float) Main.camera.width / 2, Main.camera.getHeight() / 2);
        
        graphics.setColor(Color.RED);
        graphics.drawLine((int) (lineRelativeToCamera.a().x() * Main.scaledPixelSize()), (int) (lineRelativeToCamera.a().y() * Main.scaledPixelSize()), (int) (lineRelativeToCamera.b().x() * Main.scaledPixelSize()), (int) (lineRelativeToCamera.b().y() * Main.scaledPixelSize()));
    }
    
}