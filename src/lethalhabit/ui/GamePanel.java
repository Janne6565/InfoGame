package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.game.Block;
import lethalhabit.game.Liquid;
import lethalhabit.game.Tile;
import lethalhabit.technical.Point;

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

//TODO: #2 Maybe change to Layered Panes? Für das GUI um mehrere Ebenen zu haben
public final class GamePanel extends JPanel {

    private final Timer updateTimer;
    public float frameRate = 144;
    
    public GamePanel() {
        // Set up the update timer
        updateTimer = new Timer((int) (1000 / frameRate), e -> repaint());
        updateTimer.start();
    }
    
    @Override
    public void paintComponent(Graphics g) { // this is the stuff that's responsible for drawing all the drawables to the right position (not finished yet)
        super.paintComponent(g);
        if (Main.IS_GAME_RUNNING) {
            long timeBeforeTick = System.nanoTime();
            Main.tick();
            //  System.out.println("Tick: " + ((System.nanoTime() - timeBeforeTick) / 1000000));
            long timeBefore = System.nanoTime();
            drawMap(g);

            Point maxPosition = new Point(Main.camera.getRealPosition().x() + (float) (Main.getScreenWidthGame()) / 2, Main.camera.getRealPosition().y() + (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
            Point minPosition = new Point(Main.camera.getRealPosition().x() - (float) (Main.getScreenWidthGame()) / 2, Main.camera.getRealPosition().y() - (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
            for (Drawable draw : new ArrayList<>(Main.drawables)) {
                Point posLeftTop = draw.position.plus(draw.width * -1, draw.height * -1);
                Point posRightDown = draw.position.plus(draw.width, draw.height);

                if ((posRightDown.compareTo(minPosition) > 0 && posLeftTop.compareTo(maxPosition) < 0) || !draw.relative) { // Check if element is inside our camera
                    // Image in our lethalhabit.ui.Camera Frame -> render Graphic
                    draw.draw(g);
                } else {
                    // Image not in our lethalhabit.ui.Camera Frame -> dont render Graphic
                }
            }
            // System.out.println("Drawables: " + ((System.nanoTime() - timeBefore) / 1000000));
        }
    }

    public void drawMap(Graphics g) {
        if (Main.IS_GAME_RUNNING) {
            long timeBefore = System.nanoTime();
            int xRange = (int) (Main.camera.width / Main.TILE_SIZE) + 1;
            int yRange = (int) (Main.camera.getHeight() / Main.TILE_SIZE) + 1;
            double scaledPixelSize = (float) Main.screenWidth / Main.camera.width;
            Point cameraPositionTopLeft = Main.camera.getRealPosition().minus((float) Main.camera.width / 2, (float) Main.camera.getHeight() / 2);
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
                            }
                            Block block = Block.TILEMAP.get(tile.block);
                            if (block != null) {
                                g.drawImage(block.graphic, (int) (x * scaledPixelSize), (int) (y * scaledPixelSize), null);
                            }
                        }
                    }
                }
            }
            // System.out.println("Tiles: " + ((System.nanoTime() - timeBefore) / 1000000));
        }
    }

    @Override
    public void update(Graphics g) {
        // Perform custom updates to the panel here
        super.update(g);
    }
    
}