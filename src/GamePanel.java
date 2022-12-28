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


    public boolean isVisable(Drawable draw) {
        position centerOfCam = new position(Main.activeCamera.position.x, Main.activeCamera.position.y);
        position positionToCheck = draw.position;

        return false;
    }

    @Override
    public void paintComponent(Graphics g) { // this is the stuff that's responsible for Drawing all the Drawables to the right position (not finished yet)
        super.paintComponent(g);
        Main.tick();
        float pixelPerPixel = Main.screenWidth / Main.activeCamera.width; // Computes the relative size based on the Display size


        for (Drawable draw : Main.drawables) {
            BufferedImage image = draw.graphic;
            int posX = (int) ((int) draw.position.x - (Main.activeCamera.position.x)) * 2;
            int posY = (int) ((int) draw.position.y - (Main.activeCamera.position.y)) * 2;

            int pixelPerPixelForThisDraw = () (pixelPerPixel * (draw.width / image.getWidth()));
            int positionOnScreenX = (posX * pixelPerPixelForThisDraw);
            int positionOnScreenY = (posY * pixelPerPixelForThisDraw);

            Image imgScaled = image.getScaledInstance(image.getWidth() * pixelPerPixelForThisDraw, image.getHeight() * pixelPerPixelForThisDraw, Image.SCALE_FAST);
            g.drawImage(imgScaled, positionOnScreenX, positionOnScreenY, null);
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