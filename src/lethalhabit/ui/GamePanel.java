package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.game.MapTile;
import lethalhabit.game.Tile;
import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.text.Position;
import java.awt.*;

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
        Main.tick();
        drawMap(g);
        
        Point maxPosition = new Point(Main.camera.position.x() + (float) (Main.getScreenWidthGame()) / 2, Main.camera.position.y() + (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
        Point minPosition = new Point(Main.camera.position.x() - (float) (Main.getScreenWidthGame()) / 2, Main.camera.position.y() - (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
        long timeBefore = System.nanoTime();
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
    }

    public void drawMap(Graphics g) {
        int xRange = (int) (Main.camera.width / Main.tileSize) + 1;
        int yRange = (int) (Main.camera.getHeight() / Main.tileSize) + 1;
        double pixelPerPixel = (float) Main.screenWidth / Main.camera.width;
        Point cameraPositionTopLeft = Main.camera.position.minus((float) Main.camera.width / 2, (float) Main.camera.getHeight() / 2);
        Point indexTopLeft = cameraPositionTopLeft.scale(Main.tileSize).minus(1, 1);

        for (int i = (int) indexTopLeft.x(); i < xRange + indexTopLeft.x(); i++) {
            for (int j = (int) indexTopLeft.y(); j < yRange + indexTopLeft.y(); j++) {
                if (Main.map.containsKey(i) && Main.map.get(i).containsKey(j)) {
                    Tile tile = Main.map.get(i).get(j);
                    System.out.println(tile.block);
                    if (Tile.TILEMAP.containsKey(tile.block)) {
                        BufferedImage image = Tile.TILEMAP.get(tile.block).graphic;
                        Image img = image.getScaledInstance((int) (Main.tileSize * pixelPerPixel), (int) (Main.tileSize * pixelPerPixel), Image.SCALE_FAST);
                        g.drawImage(img, x, (int) (y - Main.tileSize), null);
                    }
                }
            }
        }

    }

    @Override
    public void update(Graphics g) {
        // Perform custom updates to the panel here
        super.update(g);
    }
    
}