package lethalhabit;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public final class Main {
    
    public static final List<PhysicsObject> physicsObjects = new ArrayList<>();
    public static final List<Drawable> drawables = new ArrayList<>();
    
    public static final Camera camera = new Camera(new Position(0, 0), 500);
    
    public static int screenWidth; // In Pixels based on the screen size
    public static int screenHeight; // In Pixels based on the screen size
    public static PhysicsObject mainCharacter;
    
    public static void main(String[] args) {
        setupCamera();
        mainCharacter = new PhysicsObject(50, "image.png", new Position(0, 0)) {
            @Override
            public double getSpeed() {
                return 100;
            }
        };
    }
    
    public static int getScreenWidthGame() {
        return camera.width;
    }
    
    private static long lastTick;
    
    public static void tick() {
        float timeDelta = (System.currentTimeMillis() - lastTick);
        lastTick = System.currentTimeMillis();
        
        try {
            if (KeyHandler.keyPressed(KeyEvent.VK_SPACE) && mainCharacter.onGround()) {
                mainCharacter.jump();
            }
            
            if (KeyHandler.keyPressed(KeyEvent.VK_A)) {
                mainCharacter.moveX(-mainCharacter.getSpeed());
            } else if (KeyHandler.keyPressed(KeyEvent.VK_D)) {
                mainCharacter.moveX(mainCharacter.getSpeed());
            } else {
                mainCharacter.moveX(0);
            }
            
            mainCharacter.tick(timeDelta / 1000);
            
            // camera.position = camera.position.plus(2 * timeDelta / 1000, 0);
            
        } catch (Exception e) {
            System.out.println("Character not instantiated yet");
        }
    }
    
    public static void setupCamera() {
        GamePanel screen = new GamePanel();
        JFrame frame = new JFrame("Image Renderer");
        
        frame.addKeyListener(KeyHandler.INSTANCE);
        frame.setContentPane(screen);
        frame.setUndecorated(true);
        
        // Set the frame to full-screen mode and automatically resize the window to fit the screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
        frame.setVisible(true);
        screenWidth = frame.getWidth();
        screenHeight = frame.getHeight();
        System.out.println("Width: " + screenWidth + " Height: " + screenHeight);
    }
    
}