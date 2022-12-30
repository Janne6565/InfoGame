package lethalhabit;

import lethalhabit.math.Collidable;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.Camera;
import lethalhabit.ui.Drawable;
import lethalhabit.ui.GamePanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public final class Main {
    public static final List<PhysicsObject> physicsObjects = new ArrayList<>();
    public static final List<Drawable> drawables = new ArrayList<>();
    public static final List<Collidable> collidables = new ArrayList<>();

    
    public static final Camera camera = new Camera(new Point(0, 0), 500);
    
    public static int screenWidth; // In Pixels based on the screen size
    public static int screenHeight; // In Pixels based on the screen size
    public static PhysicsObject mainCharacter;
    
    public static void main(String[] args) {
        setupCamera();
        mainCharacter = new PhysicsObject(50, "image.png", new Point(0, 0)) {
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
                mainCharacter.moveRight();
            } else if (KeyHandler.keyPressed(KeyEvent.VK_D)) {
                mainCharacter.moveLeft();
            } else {
                mainCharacter.standStill();
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

    public static Collidable[] getPossibleCollisions(PhysicsObject physicsObject, Vec2D velocity) {
        /* To make the code wey more efficient, instead of using an arraylist to hold all the unmovable Collidables we could simple use an HashMap with the map being seperated into little "boxes" and than for an PhysicsObject we would only need to check in which part of the map it is (could be more than one) and return all the collidables that are in that same area */
        return collidables.toArray(new Collidable[0]);
    }
    
}