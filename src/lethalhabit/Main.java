package lethalhabit;

import lethalhabit.math.*;
import lethalhabit.math.Point;
import lethalhabit.ui.Camera;
import lethalhabit.ui.Drawable;
import lethalhabit.ui.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

public final class Main {
    public static final double collisionThreshold = 0.00001;
    public static final boolean debugHitbox = true;
    public static final int strokeSize = 2;
    public static final Color strokeColorPlayer = Color.RED;
    public static final Color strokeColorCollidable = Color.CYAN;


    public static final List<PhysicsObject> physicsObjects = new ArrayList<>();
    public static final List<Drawable> drawables = new ArrayList<>();
    public static final List<Collidable> collidables = new ArrayList<>();
    public static final List<LineSegment> linesToDraw = new ArrayList<>();
    

    private static final List<Integer> activeKeys = new ArrayList<>();

    public static final Camera camera = new Camera(new Point(0, 0), 500);
    
    public static int screenWidth; // In Pixels based on the screen size
    public static int screenHeight; // In Pixels based on the screen size
    public static Player mainCharacter;


    public static void main(String[] args) {
        setupCamera();
        createStartMenu();
        mainCharacter = new Player(
            50,
            "character.png",
            new Point( 0, 0),
            new Hitbox( new Point[]{
                    new Point(10, 10),
                    new Point(10, 60),
                    new Point(40, 60),
                    new Point(40, 10)
            }),
            80,
                200
        );
        new Collidable(
            new Hitbox(
                    new Point[] {
                        new Point(0, 12),
                        new Point(0, 0),
                        new Point(100, 0),
                        new Point(100, 12),
                    }
                ),
                new Point(0, 100),
            "ground.png",
            100
        ){};
        new Collidable(
                new Hitbox(
                        new Point[] {
                                new Point(0, 12),
                                new Point(0, 0),
                                new Point(100, 0),
                                new Point(100, 12),
                        }
                ),
                new Point(100, 80),
                "ground.png",
                100
        ){};
        new Collidable(
                new Hitbox(
                        new Point[] {
                                new Point(0, 12),
                                new Point(0, 0),
                                new Point(100, 0),
                                new Point(100, 12),
                        }
                ),
                new Point(-100, 80),
                "ground.png",
                100
        ){};
        new Collidable(
                new Hitbox(
                        new Point[] {
                                new Point(0, 12),
                                new Point(0, 0),
                                new Point(100, 0),
                                new Point(100, 12),
                        }
                ),
                new Point(-200, 80),
                "ground.png",
                100
        ){};
        new Collidable(
                new Hitbox(
                        new Point[] {
                                new Point(0, 12),
                                new Point(0, 0),
                                new Point(100, 0),
                                new Point(100, 12),
                        }
                ),
                new Point(-300, 80),
                "ground.png",
                100
        ){};
    }
    
    public static int getScreenWidthGame() {
        return camera.width;
    }
    
    private static long lastTick;

    public static void tick() {
        float timeDelta = (System.currentTimeMillis() - lastTick);
        lastTick = System.currentTimeMillis();
        if (mainCharacter != null) {
            if (activeKeys.contains(KeyEvent.VK_SPACE) && mainCharacter.canJump()) {
                mainCharacter.jump();
            } else {
                mainCharacter.resetJump();
            }

            if (activeKeys.contains(KeyEvent.VK_A)) {
                mainCharacter.moveLeft();
            } else if (activeKeys.contains(KeyEvent.VK_D)) {
                mainCharacter.moveRight();
            } else {
                mainCharacter.standStill();
            }
            mainCharacter.tick(timeDelta / 1000);
            camera.position = mainCharacter.position;
        }

    }
    
    // New Window
    // public
    public static JFrame frame;
    public static GamePanel screen;

    public static void setupCamera() {

        // Soll Startmenu davor starten? TODO:
        screen = new GamePanel();
        frame = new JFrame("Lethal Habit");
        
        // KeyListener 
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                activeKeys.add(e.getKeyCode());
            }
    
            public void keyReleased(KeyEvent e) {
                activeKeys.removeIf(el -> el == e.getKeyCode());
            }
        });

        //Frame Settings
        frame.setContentPane(screen);
        frame.setUndecorated(true);
        
        // Set the frame to full-screen mode and automatically resize the window to fit the screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
        frame.setVisible(true);
        screenWidth = frame.getWidth();
        screenHeight = frame.getHeight();
        System.out.println("Width: " + screenWidth + " Height: " + screenHeight);

       
        frame.setContentPane(screen);
    }

    //nonsense ----------------- 
    protected void createInternalFrame(String title) {
         JInternalFrame in = new JInternalFrame();
        
        //initialisation 
        JPanel p = new JPanel();
        JButton b = new JButton("button");
        JLabel l = new JLabel("This test");
        
        //add ons 
       

        p.add(b);
        p.add(l);
        //settings
        in.setTitle(title);

        
        // set visibility internal frame
        in.setVisible(true);
 
        // add panel to internal frame
        in.add(p);
       
 
        // add internal frame to frame
        frame.add(in);
 


    }

    // create Start Menu Scene JInternalFrame

    public static void createStartMenu() {
        JInternalFrame menu = new JInternalFrame();
        // menu.setUndecorated(true);

        // Set the menu to full-screen mode and automatically resize the window to fit the screen
        menu.toFront();
        menu.setResizable(false);
        menu.show();

        JLabel gameLabel = new JLabel("InfoGame");
        JButton startButton = new JButton("Start");
        JButton settingsButton = new JButton("Settings");
        JButton exitButton = new JButton("Exit");

        //New Panel mit Button Layout
        JPanel MenuPanel = new JPanel();
        MenuPanel.setLayout(new BoxLayout (MenuPanel, BoxLayout.Y_AXIS));

        //action event listeners:

        startButton.addActionListener(e -> {
            mainCharacter.position = new Point(0, 0);
            menu.dispose();
            try {
                menu.setSelected(false);
                setupCamera();
            } catch (PropertyVetoException ex) {
                throw new RuntimeException(ex);
            }
            // this.dispose();
        });

         settingsButton.addActionListener(e -> {
             //your actions
         });
         exitButton.addActionListener(e -> {
             //your actions

         });
        
        //
        MenuPanel.add(gameLabel);
        MenuPanel.add(startButton);
        MenuPanel.add(settingsButton);
        MenuPanel.add(exitButton);

        menu.add(MenuPanel);

        frame.add(menu);


    }

    public static Collidable[] getPossibleCollisions(PhysicsObject physicsObject, Vec2D velocity) {
        /* To make the code wey more efficient, instead of using an arraylist to hold all the unmovable Collidables we could simple use an HashMap with the map being seperated into little "boxes" and than for an PhysicsObject we would only need to check in which part of the map it is (could be more than one) and return all the collidables that are in that same area */
        return collidables.toArray(new Collidable[0]);
    }
}