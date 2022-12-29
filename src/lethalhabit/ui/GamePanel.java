package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.math.*;

import javax.swing.*;
import java.awt.*;

public final class GamePanel extends JPanel {
    
    private Timer updateTimer;
    public float frameRate = 244;
    
    public GamePanel() {
        // Set up the update timer
        updateTimer = new Timer((int) (1000 / frameRate), e -> repaint());
        updateTimer.start();
    }
    
    @Override
    public void paintComponent(Graphics g) { // this is the stuff that's responsible for drawing all the drawables to the right position (not finished yet)
        super.paintComponent(g);
        Main.tick();
        
        Position maxPosition = new Position(Main.camera.position.x() + (float) (Main.getScreenWidthGame()) / 2, Main.camera.position.y() + (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
        Position minPosition = new Position(Main.camera.position.x() - (float) (Main.getScreenWidthGame()) / 2, Main.camera.position.y() - (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
        
        for (Drawable draw : Main.drawables) {
            Position posLeftTop = draw.position.plus(draw.width * -1, draw.height * -1);
            Position posRightDown = draw.position.plus(draw.width, draw.height);
            
            if ((posRightDown.greaterThan(minPosition) && posLeftTop.lessThan(maxPosition)) || !draw.relative) { // Check if element is inside our camera
                // Image in our lethalhabit.ui.Camera Frame -> render Graphic
                draw.draw(g);
            } else {
                // Image not in our lethalhabit.ui.Camera Frame -> dont render Graphic
            }
        }
    }
    
    @Override
    public void update(Graphics g) {
        // Perform custom updates to the panel here
        super.update(g);
    }
    
}