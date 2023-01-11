package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.math.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import java.awt.*;

//TODO: #2 Maybe change to Layered Panes? Für das GUI um mehrere Ebenen zu haben
public final class GamePanel extends JPanel {

    private final Timer updateTimer;
    public float frameRate = 100;
    
    public GamePanel() {
        // Set up the update timer
        updateTimer = new Timer((int) (1000 / frameRate), e -> repaint());
        updateTimer.start();
    }
    
    @Override
    public void paintComponent(Graphics g) { // this is the stuff that's responsible for drawing all the drawables to the right position (not finished yet)
        super.paintComponent(g);
        Main.tick();
        
        Point maxPosition = new Point(Main.camera.position.x() + (float) (Main.getScreenWidthGame()) / 2, Main.camera.position.y() + (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
        Point minPosition = new Point(Main.camera.position.x() - (float) (Main.getScreenWidthGame()) / 2, Main.camera.position.y() - (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
        long timeBefore = System.nanoTime();
        for (Drawable draw : getDrawables()) {
            Point posLeftTop = draw.position.plus(draw.width * -1, draw.height * -1);
            Point posRightDown = draw.position.plus(draw.width, draw.height);
            
            if ((posRightDown.compareTo(minPosition) > 0 && posLeftTop.compareTo(maxPosition) < 0) || !draw.relative) { // Check if element is inside our camera
                // Image in our lethalhabit.ui.Camera Frame -> render Graphic
                draw.draw(g);
            } else {
                // Image not in our lethalhabit.ui.Camera Frame -> dont render Graphic
            }
        }
        System.out.println("Time For Drawing: " + ((System.nanoTime() - timeBefore) / 1000000));
    }

    private static List<Drawable> getDrawables() {
        return new ArrayList<>(Main.drawables);
    }
    
    @Override
    public void update(Graphics g) {
        // Perform custom updates to the panel here
        super.update(g);
    }
    
}