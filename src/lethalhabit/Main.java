package lethalhabit;

import lethalhabit.game.Block;
import lethalhabit.game.Liquid;
import lethalhabit.game.Tile;
import lethalhabit.technical.Point;
import lethalhabit.technical.*;
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

/**
 * Main Programm
 *
 */
public final class Main {
    public static final boolean DEMO_MODE = false;
    public static final boolean MINIMIZED = true;

    public static final boolean DEBUG_HITBOX = false    ;
    public static final int STROKE_SIZE_HITBOXES = 2;
    public static final Color STROKE_COLOR_HITBOX = Color.RED;
    public static final double COLLISION_THRESHOLD = 1;
    public static final double MAX_VELOCITY_SPEED = 800;
    public static final double GRAVITATIONAL_ACCELERATION = 400;
    public static final double TILE_SIZE = 20;
    public static final double SAFE_DISTANCE = 0.05;

    public static final List<PhysicsObject> physicsObjects = new ArrayList<>();
    public static final List<Drawable> drawables = new ArrayList<>();
    public static final List<Collidable> collidables = new ArrayList<>();
    public static final List<Tickable> tickables = new ArrayList<>();
    public static final List<Loadable> loadables = new ArrayList<>();

    private static final List<Integer> activeKeys = new ArrayList<>();

    public static boolean IS_GAME_RUNNING = false;


    public static final Camera camera = new Camera(new Point(0, 0), 400, 40, 80, 40);

    public static int screenWidth; // In Pixels based on the screen size
    public static int screenHeight; // In Pixels based on the screen size
    public static Player mainCharacter;
    public static Map<Integer, Map<Integer, Tile>> map;


    public static void main(String[] args) {
        gameInit();
        IS_GAME_RUNNING = true;
        System.out.println("Pixel Per Pixel: " + scaledPixelSize());
    }


    /**
     * Initiates the game:
     *  - Camera
     *  - Tiles
     *  - Map
     *  - Loads Assets
     */
    public static void gameInit() {
        loadMap("/map.json");
        setupCamera();
        Block.loadBlocks();
        Liquid.loadLiquids();
        // System.out.println("Finished Loading");
        double size = 0.4;
        mainCharacter = new Player(
                (double) (50.0 * size),
                "character.png",
                new Point( 100, 816.2),
                new Hitbox( new Point[]{
                        new Point(10, 10).scale(size),
                        new Point(10, 57).scale(size),
                        new Point(40, 57).scale(size),
                        new Point(40, 10).scale(size)
                }),
                80,
                200
        );
    }

    /**
     * Tick only used for Demo mechanics
     * @param timeDelta time since the last tick happened
     */
    public static void demoGameTick(double timeDelta) {
        if (mainCharacter.position.y() >= 500) {
            mainCharacter.position = new Point(0, 300);
            mainCharacter.velocity = new Vec2D(0, 0);
        }
    }

    /**
     * Loads Map
     * @param path Path where to find the map
     */
    public static void loadMap(String path) {
        map = Util.readWorldData(Objects.requireNonNull(Main.class.getResourceAsStream(path)));
    }

    /**
     * Calculate Camera Width
     * @return camera width in ingame unit
     */
    public static int getScreenWidthGame() {
        return camera.width;
    }
    
    private static long lastTick;


    /**
     * Method called on Game Tick
     */
    public static void tick() {
        double timeDelta = (float) (System.currentTimeMillis() - lastTick) / 1000;
        lastTick = System.currentTimeMillis();
        handleKeyInput(timeDelta);
        double tickTime = System.nanoTime();
        for (Tickable tickable : new ArrayList<Tickable>(tickables)) {
            if (tickable != null) {
                tickable.tick(timeDelta);
            }
        }

        moveCamera(timeDelta);
        screenWidth = frame.getWidth();
        screenHeight = frame.getHeight();
        // System.out.println("Width: " + screenWidth + " Height: " + screenHeight);

        if (DEMO_MODE) {
            demoGameTick(timeDelta);
        }
    }


    /**
     * Executes commands depending on the key input
     * @param timeDelta time since last tick
     */

    public static void handleKeyInput(double timeDelta) {
        if (mainCharacter != null) {
            if (activeKeys.contains(KeyEvent.VK_SPACE)) {
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
            if (activeKeys.contains(KeyEvent.VK_W)) {
                camera.shift = new Point(camera.shift.x(), Math.max(camera.shift.y() + camera.speed * timeDelta * -1, -camera.shiftLimit));
            } else if (activeKeys.contains(KeyEvent.VK_S)) {
                camera.shift = new Point(camera.shift.x(), Math.min(camera.shift.y() + camera.speed * timeDelta, camera.shiftLimit));
            } else {
                camera.shift = new Point(camera.shift.x(), camera.shift.y() < 0.5 || camera.shift.y() > -0.5 ? camera.shift.y() * 0.8 : 0);
            }
        }
    }

    /**
     * calculates the pixel size based on screen width
     * @return relative pixel/position ratio based on the screen width
     */
    public static float scaledPixelSize() {
        return (float) screenWidth / camera.width;
    }


    /**
     * Shifts the camera based on player position
     * @param timeDelta time since last tick
     */
    public static void moveCamera(double timeDelta) {
        if (mainCharacter != null) {
            Point relative = camera.position.minus(mainCharacter.position.plus(mainCharacter.width / 2, mainCharacter.height / 2));
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
    
    public static JFrame frame;
    public static GamePanel panel;

    /**
     * Initiates the screen
     */
    public static void setupCamera() {
        panel = new GamePanel();
        // Soll Startmenu davor starten? TODO:
        frame = new JFrame("Lethal Habit");
        
        // KeyListener 
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                activeKeys.add(e.getKeyCode());
            }
    
            public void keyReleased(KeyEvent e) {
                activeKeys.removeIf(key -> key == e.getKeyCode());
            }
        });

        //Frame Settings
        frame.setContentPane(panel);
        frame.setUndecorated(true);
        
        // Set the frame to full-screen mode and automatically resize the window to fit the screen

        if (!MINIMIZED) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            frame.setSize(1920, 1080);
        }

        frame.setResizable(true);
        frame.setVisible(true);
        screenWidth = frame.getWidth();
        screenHeight = frame.getHeight();
        // System.out.println("Width: " + screenWidth + " Height: " + screenHeight);

        frame.setContentPane(panel);
    }

    public static void createStartWindow() {
        JFrame startFrame = new JFrame("Lethal Habit");
        JPanel panel = new JPanel();
        // menu.setUndecorated(true);

        // Set the menu to full-screen mode and automatically resize the window to fit the screen

        startFrame.setUndecorated(true);

        startFrame.setSize(200,200);
        startFrame.setResizable(false);
        startFrame.show();

        JLabel gameLabel = new JLabel("Lethal Habit");
        JButton startButton = new JButton("Start");
        JButton settingsButton = new JButton("Settings");
        JButton exitButton = new JButton("Exit");

        //New Panel mit Button Layout
        panel.setLayout(new BorderLayout());

        //action event listeners:

        startButton.addActionListener(e -> {
            IS_GAME_RUNNING = true;
            startFrame.dispose();
            gameInit();
        });

        settingsButton.addActionListener(e -> {
            startFrame.dispose();
            createSettingsWindow();
        });
        exitButton.addActionListener(e -> {
            startFrame.dispose();

        });

        //
        panel.add(gameLabel, BorderLayout.PAGE_START);
        panel.add(startButton, BorderLayout.CENTER);
        panel.add(settingsButton, BorderLayout.EAST);
        panel.add(exitButton, BorderLayout.SOUTH);

        startFrame.add(panel);
        startFrame.setLocationRelativeTo(null);
        startFrame.setVisible(true);




    }
    public static void createSettingsWindow() {

        String[] petStrings = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };

        //Create the combo box, select item at index 4.
        //Indices start at 0, so 4 specifies the pig.
        JComboBox petList = new JComboBox(petStrings);
        petList.setSelectedIndex(4);

        JFrame settingsFrame = new JFrame();
        JPanel panel = new JPanel();
        // menu.setUndecorated(true);

        //New Panel mit Button Layout
        panel.setLayout(new BorderLayout());

        // Set the menu to full-screen mode and automatically resize the window to fit the screen
        settingsFrame.setUndecorated(true);

        settingsFrame.setSize(200,200);
        settingsFrame.setResizable(false);
        settingsFrame.show();

        JLabel gameLabel = new JLabel("Lethal Habit");
        JButton startButton = new JButton("Start");
        JButton backButton = new JButton("Back");




        //action event listeners:

        startButton.addActionListener(e -> {
            settingsFrame.dispose();
            setupCamera();
        });
        backButton.addActionListener( e -> {
            settingsFrame.dispose();
            createStartWindow();
        });


        //
        panel.add(gameLabel, BorderLayout.NORTH);
        panel.add(petList, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);


        settingsFrame.add(panel);
        settingsFrame.setLocationRelativeTo(null);
        settingsFrame.setVisible(true);




    }



    public static ArrayList<Hitbox> getPossibleCollisions(Hitbox hitbox, Vec2D velocity, double timeDelta) {
        /* To make the code wey more efficient, instead of using an arraylist to hold all the unmovable Collidables we could simple use an HashMap with the map being seperated into little "boxes" and than for an PhysicsObject we would only need to check in which part of the map it is (could be more than one) and return all the collidables that are in that same area */

        ArrayList<Collidable> possibleCollisions = new ArrayList<>();

        Hitbox startedHitbox = hitbox;

        Vec2D scaledVel = velocity.scale(timeDelta);

        Hitbox hitboxAfterVelocity = startedHitbox.shift(scaledVel.x(), scaledVel.y());

        Hitbox hitboxRange = new Hitbox(
                new Point[] {
                        new Point(Math.min(hitboxAfterVelocity.minPosition.x(), startedHitbox.minPosition.x()), Math.min(hitboxAfterVelocity.minPosition.y(), startedHitbox.minPosition.y())),
                        new Point(Math.min(hitboxAfterVelocity.minPosition.x(), startedHitbox.minPosition.x()), Math.max(hitboxAfterVelocity.maxPosition.y(), startedHitbox.maxPosition.y())),
                        new Point(Math.max(hitboxAfterVelocity.maxPosition.x(), startedHitbox.maxPosition.x()), Math.max(hitboxAfterVelocity.maxPosition.y(), startedHitbox.maxPosition.y())),
                        new Point(Math.max(hitboxAfterVelocity.maxPosition.x(), startedHitbox.maxPosition.x()), Math.min(hitboxAfterVelocity.minPosition.y(), startedHitbox.minPosition.y()))
                }
        );


        ArrayList<Hitbox> hitboxesMightBeCollidingTo = new ArrayList<>();

        Point minPosition = new Point((int) hitboxRange.vertices[0].x() / TILE_SIZE, (int) hitboxRange.vertices[0].y() / TILE_SIZE);
        Point maxPosition = new Point((int) hitboxRange.vertices[2].x() / TILE_SIZE, (int) hitboxRange.vertices[2].y() / TILE_SIZE);

        for (int xIndex = (int) minPosition.x() - 1; xIndex <= maxPosition.x() + 1; xIndex ++) {
            for (int yIndex = (int) minPosition.y() - 1; yIndex <= maxPosition.y() + 1; yIndex ++) {
                Point position = new Point(xIndex * TILE_SIZE, yIndex * TILE_SIZE);
                Map<Integer, Tile> column = map.get(xIndex);
                if (column != null) {
                    Tile tile = column.get(yIndex);
                    if (tile != null && tile.block >= 0) {
                        Block block = Block.TILEMAP.get(tile.block);
                        if (block != null) {
                            Hitbox newHitbox = block.hitbox.shift(position);
                            hitboxesMightBeCollidingTo.add(newHitbox);
                        }
                    }
                }
            }
        }
        mainCharacter.drawnHitboxes = new ArrayList<>(hitboxesMightBeCollidingTo);
        mainCharacter.drawnHitboxes.add(hitboxRange);
        return hitboxesMightBeCollidingTo;
    }
}