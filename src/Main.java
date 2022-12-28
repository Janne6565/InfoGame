import javax.swing.*;
import java.util.ArrayList;
import java.lang.*;

public class Main {
    static ArrayList<PhysicsObject> physicsObjects = new ArrayList<PhysicsObject>();
    static ArrayList<Drawable> drawables = new ArrayList<Drawable>();


    static Camera activeCamera = new Camera(new position(0, 0), 500);

    static int screenWidth; // In Pixels based on the screen size
    static int screenHeight; // In Pixels based on the screen size
    static int screenWidthGame = 500; // In coordinates (independent of the real size of the screen)
    static GamePanel screen;
    static Drawable mainCharacter;
    public static void main(String[] args) {
        setupCamera();

        mainCharacter = new Drawable(50, "image.png", new position(0, 0));
    }

    private static long lastTick;
    public static void tick() {
        float timeDelta = (System.currentTimeMillis() - lastTick);
        lastTick = System.currentTimeMillis();


        try {
            mainCharacter.position.x += 5 * (timeDelta) / 1000;
            mainCharacter.position.y += 5 * (timeDelta) / 1000;
        } catch (Exception e) {
            System.out.println("Character not instantiated yet");
        }
    }


    public static void setupCamera() {
        screen = new GamePanel();
        JFrame frame = new JFrame("Image Renderer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(screen);
        frame.pack();

        // Set the frame to full-screen mode and automatically resize the window to fit the screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
        frame.setVisible(true);
        screenWidth = frame.getWidth();
        screenHeight = frame.getHeight();
    }
}