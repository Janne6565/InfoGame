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

        position maxPosition = new position(Main.activeCamera.position.x + (float) (Main.screenWidthGame / 2), Main.activeCamera.position.y + (Main.screenHeight * ((float) Main.screenWidthGame / Main.screenWidth)));
        position minPosition = new position(Main.activeCamera.position.x - (float) (Main.screenWidthGame) / 2, Main.activeCamera.position.y - (Main.screenHeight * ((float) Main.screenWidthGame / Main.screenWidth)));
        //  System.out.println(maxPosition.x + " " + maxPosition.y);
        //  System.out.println(minPosition.x + " " + minPosition.y);

        for (Drawable draw : Main.drawables) {

            //  System.out.println(draw.position.addOn(draw.width, draw.height).x + " " + draw.position.addOn(draw.width, draw.height).x);
            //  System.out.println(minPosition.x + " " + minPosition.y);
            //  System.out.println(draw.position.addOn(draw.width, draw.height).greaterThan(minPosition));

            if (draw.position.addOn(draw.width, draw.height).greaterThan(minPosition) && draw.position.addOn(draw.width * -1, draw.height * -1).lessThan(maxPosition)) { // Check if element is inside our camera
                System.out.println("Inside => we render that");
                draw.draw(g);
            } else {
                System.out.println("Outside => we dont render that");
            }

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