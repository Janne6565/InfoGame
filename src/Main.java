import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.lang.*;
import java.util.HashMap;

public class Main {
    static ArrayList<PhysicsObject> physicsObjects = new ArrayList<PhysicsObject>();
    static ArrayList<Drawable> drawables = new ArrayList<Drawable>();

    static Camera activeCamera = new Camera(new position(0, 0), 500);

    static int screenWidth; // In Pixels based on the screen size
    static int screenHeight; // In Pixels based on the screen size
    static GamePanel screen;
    static PhysicsObject mainCharacter;

    static HashMap<Integer, Boolean> keyPressed = new HashMap<Integer, Boolean>();

    public static void main(String[] args) {
        setupCamera();
        mainCharacter = new PhysicsObject(50, "image.png", new position(0, 0)) {        };
    }

    public static int getScreenWidthGame() {
        return (int) activeCamera.width;
    }

    private static long lastTick;
    public static void tick() {
        float timeDelta = (System.currentTimeMillis() - lastTick);
        lastTick = System.currentTimeMillis();

        try {
            if (KeyHandler.keyPressed(KeyEvent.VK_SPACE) && mainCharacter.onGround()) {
                mainCharacter.velocity.y -= 100;
            }

            if (KeyHandler.keyPressed(KeyEvent.VK_A)) {
                mainCharacter.velocity.x = -100;
            }

            if (KeyHandler.keyPressed(KeyEvent.VK_D)) {
                mainCharacter.velocity.x = 100;
            }

            if ((KeyHandler.keyPressed(KeyEvent.VK_A) && KeyHandler.keyPressed(KeyEvent.VK_D)) || (!KeyHandler.keyPressed(KeyEvent.VK_A) && !KeyHandler.keyPressed(KeyEvent.VK_D))) {
                mainCharacter.velocity.x = 0;
            }

            mainCharacter.Tick(timeDelta / 1000);


            // activeCamera.position.x += 2 * (timeDelta) / 1000; // Test to move the Camera


        } catch (Exception e) {
            System.out.println("Character not instantiated yet");
        }
    }


    public static void setupCamera() {
        screen = new GamePanel();
        JFrame frame = new JFrame("Image Renderer");
        frame.addKeyListener(new KeyListener());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(screen);
        frame.pack();

        // Set the frame to full-screen mode and automatically resize the window to fit the screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
        frame.setVisible(true);
        screenWidth = frame.getWidth();
        screenHeight = frame.getHeight();
        System.out.println("Width: " + screenWidth + " Height: " + screenHeight);
    }
}