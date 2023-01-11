package lethalhabit;

import lethalhabit.game.MapTile;
import lethalhabit.game.Tile;
import lethalhabit.math.*;
import lethalhabit.math.Point;
import lethalhabit.ui.Camera;
import lethalhabit.ui.Drawable;
import lethalhabit.ui.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Main {
    public static final double collisionThreshold = 0.00001;
    public static final boolean debugHitbox = false;
    public static final int strokeSize = 2;
    public static final Color strokeColorPlayer = Color.RED;
    public static final Color strokeColorCollidable = Color.CYAN;
    public static double tileSize = 20;

    public static final List<PhysicsObject> physicsObjects = new ArrayList<>();
    public static final List<Drawable> drawables = new ArrayList<>();
    public static final List<Collidable> collidables = new ArrayList<>();
    public static final List<Tickable> tickables = new ArrayList<>();


    private static final List<Integer> activeKeys = new ArrayList<>();

    public static final Camera camera = new Camera(new Point(0, 0), 500, 40);

    public static int screenWidth; // In Pixels based on the screen size
    public static int screenHeight; // In Pixels based on the screen size
    public static Player mainCharacter;
    public static Map<Integer, Map<Integer, Tile>> map;


    public static void main(String[] args) {
        Tile.loadMapTiles();
        loadMap("resources/map.json");
        setupCamera();
        createStartMenu();
        mainCharacter = new Player(
            50,
            "character.png",
            new Point( 100, -50),
            new Hitbox( new Point[]{
                    new Point(10, 10),
                    new Point(10, 57),
                    new Point(40, 57),
                    new Point(40, 10)
            }),
            80,
                200
        );
    }

    public static void loadMap(String path) {
        map = Util.readWorldData(Objects.requireNonNull(Main.class.getResourceAsStream(path)));
    }

    public static int getScreenWidthGame() {
        return camera.width;
    }
    
    private static long lastTick;

    public static void tick() {
        float timeDelta = (float) (System.currentTimeMillis() - lastTick) / 1000;
        lastTick = System.currentTimeMillis();
        handleKeyInput();

        for (Tickable tickable : new ArrayList<Tickable>(tickables)) {
            if (tickable != null) {
                tickable.tick(timeDelta);
            }
        }
        moveCamera();
        screenWidth = frame.getWidth();
        screenHeight = frame.getHeight();
        // System.out.println("Width: " + screenWidth + " Height: " + screenHeight);

    }

    public static void handleKeyInput() {
        if (mainCharacter != null) {
            if (activeKeys.contains(KeyEvent.VK_SPACE) && mainCharacter.canJump()) {
                mainCharacter.jump();
            } else {
                mainCharacter.resetJump();
            }

            if (activeKeys.contains(KeyEvent.VK_A) && !activeKeys.contains(KeyEvent.VK_D)) {
                mainCharacter.moveLeft();
            } else if (activeKeys.contains(KeyEvent.VK_D) && !activeKeys.contains(KeyEvent.VK_A)) {
                mainCharacter.moveRight();
            } else {
                mainCharacter.standStill();
            }

            if (activeKeys.contains(KeyEvent.VK_F)) {
                mainCharacter.makeFireball();
            }
        }
    }

    public static void moveCamera() {
        if (mainCharacter != null) {

            Point relative = camera.position.minus(mainCharacter.position.plus(mainCharacter.width / 2, mainCharacter.height / 2).plus(0, -30 ));
            double moveX = 0;
            double moveY = 0;
            if (relative.x() < -camera.threshhold) {
                moveX = -camera.threshhold - relative.x();
            }
            if (relative.x() > camera.threshhold) {
                moveX = camera.threshhold - relative.x();
            }

            if (relative.y() < -camera.threshhold) {
                moveY = -camera.threshhold - relative.y();
            }
            if (relative.y() > camera.threshhold) {
                moveY = camera.threshhold  - relative.y();
            }

            camera.position = camera.position.plus(moveX, moveY);
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

        frame.setResizable(true);
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
            //your actions
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
        menu.setVisible(false);


    }

    public static ArrayList<Hitbox> getPossibleCollisions(PhysicsObject physicsObject, Vec2D velocity, double timeDelta) {
        /* To make the code wey more efficient, instead of using an arraylist to hold all the unmovable Collidables we could simple use an HashMap with the map being seperated into little "boxes" and than for an PhysicsObject we would only need to check in which part of the map it is (could be more than one) and return all the collidables that are in that same area */

        ArrayList<Collidable> possibleCollisions = new ArrayList<>();

        Hitbox startedHitbox = physicsObject.hitbox.shiftAll(physicsObject.position);

        Hitbox hitboxAfterVelocity = new Hitbox(new Point[] {
                startedHitbox.minPosition.plus(velocity.scale(timeDelta)),
                new Point(startedHitbox.minPosition.x(), startedHitbox.maxPosition.y()).plus(velocity.scale(timeDelta)),
                startedHitbox.maxPosition.plus(velocity.scale(timeDelta)),
                new Point(startedHitbox.maxPosition.x(), startedHitbox.minPosition.y()).plus(velocity.scale(timeDelta)),
        });

        Hitbox hitboxRange = new Hitbox(
            new Point[] {
                new Point(Math.min(hitboxAfterVelocity.minPosition.x(), startedHitbox.minPosition.x()), Math.min(hitboxAfterVelocity.minPosition.y(), startedHitbox.minPosition.y())),
                new Point(Math.min(hitboxAfterVelocity.minPosition.x(), startedHitbox.minPosition.x()), Math.max(hitboxAfterVelocity.maxPosition.y(), startedHitbox.maxPosition.y())),
                new Point(Math.max(hitboxAfterVelocity.maxPosition.x(), startedHitbox.maxPosition.x()), Math.max(hitboxAfterVelocity.maxPosition.y(), startedHitbox.maxPosition.y())),
                new Point(Math.max(hitboxAfterVelocity.maxPosition.x(), startedHitbox.maxPosition.x()), Math.min(hitboxAfterVelocity.minPosition.y(), startedHitbox.minPosition.y()))
            }
        );

        ArrayList<Hitbox> hitboxesMightBeCollidingTo = new ArrayList<>();

        Point minPosition = new Point((int) hitboxRange.vertices[0].x() / tileSize, (int) hitboxRange.vertices[0].y() / tileSize);
        Point maxPosition = new Point((int) hitboxRange.vertices[2].x() / tileSize, (int) hitboxRange.vertices[2].y() / tileSize);

        for (int xIndex = (int) minPosition.x() - 1; xIndex <= maxPosition.x(); xIndex ++) {
            for (int yIndex = (int) minPosition.y() - 1; yIndex <= maxPosition.y(); yIndex ++) {
                Point position = new Point(xIndex * tileSize, yIndex * tileSize);
                if (map.containsKey(xIndex) && map.get(xIndex).containsKey(yIndex)) {
                    Tile tile = map.get(xIndex).get(yIndex);
                    if (tile.block != Tile.EMPTY.block && Tile.TILEMAP.containsKey(tile.block)) {
                        MapTile mapTile = Tile.TILEMAP.get(tile.block);
                        Hitbox hitbox = mapTile.hitbox.shiftAll(position);
                        hitboxesMightBeCollidingTo.add(hitbox);
                    }
                }
            }
        }

        return hitboxesMightBeCollidingTo;
    }
}