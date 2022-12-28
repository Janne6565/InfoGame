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
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Main.tick();
        Graphics2D g2d = (Graphics2D) g;
        float pixelPerPixel = (float) (Main.activeCamera.width / Main.screenWidth); // Computes the relative size based on the Display size
        for (Drawable draw : Main.drawables) {
            BufferedImage image = draw.graphic;
            int posX = (int) ((int) draw.position.x - Main.activeCamera.position.x);
            int posY = (int) ((int) draw.position.y - Main.activeCamera.position.y);

            int pixelPerPixelForThisDraw = (int) (pixelPerPixel * (draw.width / image.getWidth()));

            int positionOnScreenX = (int) (posX * pixelPerPixel);
            int positionOnScreenY = (int) (posY * pixelPerPixel);
            g2d.scale(pixelPerPixel, pixelPerPixel);
            g2d.drawImage(image, positionOnScreenX, positionOnScreenY, null);
        }
    }


    @Override
    public void update(Graphics g) {
        // Perform custom updates to the panel here

        // Call the superclass's update method to repaint the panel
        System.out.println("Updated");
        super.update(g);
    }
}