import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class GamePanel extends JPanel {
    private Timer updateTimer;
    public float FrameRate = 244;

    public GamePanel() {
        // Set up the update timer
        updateTimer = new Timer((int) (1000 / FrameRate), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        updateTimer.start();
    }

    @Override
    public void paintComponent(Graphics g) { // this is the stuff that's responsible for Drawing all the Drawables to the right position (not finished yet)
        super.paintComponent(g);
        Main.tick();

        position maxPosition = new position(Main.activeCamera.position.x + (float) (Main.getScreenWidthGame()) / 2, Main.activeCamera.position.y + (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
        position minPosition = new position(Main.activeCamera.position.x - (float) (Main.getScreenWidthGame()) / 2, Main.activeCamera.position.y - (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);

        for (Drawable draw : Main.drawables) {
            position posLeftTop = draw.position.addOn(draw.width * -1, draw.height * -1);
            position posRightDown = draw.position.addOn(draw.width, draw.height);

            if ((posRightDown.greaterThan(minPosition) && posLeftTop.lessThan(maxPosition)) || !draw.relative) { // Check if element is inside our camera
                // Image in our Camera Frame -> render Graphic
                draw.draw(g);
            } else {
                // Image not in our Camera Frame -> dont render Graphic
            }
        }
    }


    @Override
    public void update(Graphics g) {
        // Perform custom updates to the panel here
        super.update(g);
    }
}