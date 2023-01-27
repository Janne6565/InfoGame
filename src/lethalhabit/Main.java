package lethalhabit;

import lethalhabit.game.Block;
import lethalhabit.game.Enemy;
import lethalhabit.game.Liquid;
import lethalhabit.game.Tile;
import lethalhabit.sound.Sound;
import lethalhabit.technical.Hitbox;
import lethalhabit.technical.Loadable;
import lethalhabit.technical.Point;
import lethalhabit.technical.Vec2D;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Camera;
import lethalhabit.ui.Drawable;
import lethalhabit.ui.GamePanel;
import lethalhabit.util.Settings;
import lethalhabit.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.awt.event.KeyEvent.*;

/**
 * Main program
 */
public final class Main {
    
    public static final boolean DEMO_MODE = false;
    public static final boolean MINIMIZED = true;
    
    public static final Color HITBOX_STROKE_COLOR = Color.RED;
    public static final Color PROGRESS_BAR_COLOR = new Color(0x7030e0);
    
    public static final boolean DEBUG_HITBOX = false;
    public static final int STROKE_SIZE_HITBOXES = 2;
    public static final double COLLISION_THRESHOLD = 1;
    public static final double MAX_VELOCITY_SPEED = 800;
    public static final double GRAVITATIONAL_ACCELERATION = 400;
    public static final double TILE_SIZE = 20;
    public static final double SAFE_DISTANCE = 0.05;
    
    public static final List<PhysicsObject> physicsObjects = new ArrayList<>();
    public static final List<Drawable> drawables = new ArrayList<>();
    public static final List<Tickable> tickables = new ArrayList<>();
    public static final List<Loadable> loadables = new ArrayList<>();
    
    private static final List<Integer> activeKeys = new ArrayList<>();
    private static final List<Integer> listKeysHolding = new ArrayList<>();
    
    public static boolean IS_GAME_LOADING = true;
    public static boolean IS_GAME_RUNNING = false;
    
    public static final Camera camera = new Camera(new Point(0, 0), 400, 40, 80, 60, 0);
    
    public static int screenWidth; // In Pixels based on the screen size
    public static int screenHeight; // In Pixels based on the screen size
    public static Player mainCharacter;
    public static Enemy enemy;
    
    public static Map<Integer, Map<Integer, Tile>> map;
    public static Settings settings;
    
    private static long lastTick;
    
    public static void main(String[] args) {
        gameInit();
    }
    
    public static Tile tileAt(int tileX, int tileY) {
        Map<Integer, Tile> column = Main.map.get(tileX);
        if (column != null) {
            return column.get(tileY);
        } else {
            return null;
        }
    }
    
    /**
     * Initiates the game: <br>
     * - camera <br>
     * - tiles <br>
     * - map <br>
     * - assets <br>
     */
    public static void gameInit() {
        loadMap();
        loadSettings();
        setupCamera();
        Liquid.loadLiquids();
        Animation.loadAnimations();
        Block.loadBlocks();
        //playSoundtrack();
        GamePanel.generateMap();
        mainCharacter = new Player(Point.SPAWN, new PlayerSkills()); // TODO: load skills from file
        IS_GAME_LOADING = false;
        IS_GAME_RUNNING = true;
        // enemy = new Enemy(new Point(100, 700));
        // enemy.setMovementSpeed(30);
    }
    
    public static void playSoundtrack() {
        try {
            Sound soundtrack = new Sound("/assets/music/soundtrack1.wav");
            soundtrack.loop();
        } catch (Exception ignored) {
        }
    }
    
    /**
     * Tick only used for demo mechanics
     * @param timeDelta time since the last tick happened
     */
    public static void demoGameTick(double timeDelta) {
        if (mainCharacter.position.y() >= 500) {
            mainCharacter.position = new Point(0, 300);
            mainCharacter.velocity = new Vec2D(0, 0);
        }
    }
    
    /**
     * Loads the map (map.json).
     */
    public static void loadMap() {
        map = Util.readWorldData(Main.class.getResourceAsStream("/map.json"));
    }
    
    /**
     *
     */
    public static void loadSettings() {
        settings = new Settings();
        // TODO: Read settings from some settings.json file
    }
    
    /**
     * Calculate camera width
     * @return camera width in in-game unit
     */
    public static int getScreenWidthGame() {
        return camera.WIDTH;
    }
    
    /**
     * Method called on game tick
     */
    public static void tick() {
        double timeDelta = (double) (System.currentTimeMillis() - lastTick) / 1000.0;
        lastTick = System.currentTimeMillis();
        handleKeyInput(timeDelta);
        
        // enemyMovement(timeDelta);
        if (IS_GAME_RUNNING) {
            for (Tickable tickable : new ArrayList<>(tickables)) {
                if (tickable != null) {
                    tickable.tick(timeDelta);
                }
            }
            if (DEMO_MODE) {
                demoGameTick(timeDelta);
            }
            moveCamera(timeDelta);
        }
        
        
    }
    
    /**
     * Executes commands depending on the key input
     */
    private static boolean active = false;
    
    private static float count = 0;
    
    private static void enemyMovement(double timeDelta) {
        if (enemy.isWallDown()) {
            active = true;
        }
        if (active && count < 1) {
            enemy.moveLeft();
            if (enemy.isWallLeft() && count < 1) {
                enemy.jump();
            } else {
                enemy.resetJump();
            }
            count += timeDelta;
            if (count > 1) {
                active = false;
            }
        }
        if (count > 1) {
            count = 0;
            enemy.moveRight();
            
            if (enemy.isWallRight() && count < 1) {
                enemy.jump();
            } else {
                enemy.resetJump();
            }
            count += timeDelta;
        }
    }
    
    public static void handleKeyInput(double timeDelta) {
        if (mainCharacter != null) {
            if (activeKeys.contains(VK_BACK_SPACE) && !listKeysHolding.contains(VK_BACK_SPACE)) {
                IS_GAME_RUNNING = !IS_GAME_RUNNING;
                listKeysHolding.add(VK_BACK_SPACE);
            } else {
                listKeysHolding.remove(Integer.valueOf(VK_BACK_SPACE));
            }
            
            if (IS_GAME_RUNNING) {
                
                if (activeKeys.contains(VK_SPACE) && activeKeys.contains(VK_CONTROL) && mainCharacter.isSubmerged()) {
                    mainCharacter.stopMovementY();
                } else if (activeKeys.contains(VK_SPACE)) {
                    if (mainCharacter.isSubmerged()) {
                        mainCharacter.moveUp();
                    } else {
                        mainCharacter.jump();
                    }
                } else if (activeKeys.contains(VK_CONTROL)) {
                    if (mainCharacter.isSubmerged() && mainCharacter.skills.swim > 0) {
                        mainCharacter.moveDown();
                    }
                } else {
                    if (mainCharacter.isSubmerged()) {
                        mainCharacter.moveDown();
                    }
                    mainCharacter.resetJump();
                }
                
                if (activeKeys.contains(VK_SHIFT) && mainCharacter.skills.dash > 0) {
                    mainCharacter.dash();
                }
                
                if (activeKeys.contains(VK_A) && !activeKeys.contains(VK_D)) {
                    if (!mainCharacter.isSubmerged() || mainCharacter.skills.swim > 0) {
                        mainCharacter.moveLeft();
                    }
                } else if (activeKeys.contains(VK_D) && !activeKeys.contains(VK_A)) {
                    if (!mainCharacter.isSubmerged() || mainCharacter.skills.swim > 0) {
                        mainCharacter.moveRight();
                    }
                } else {
                    mainCharacter.stopMovementX();
                }
                
                if (activeKeys.contains(VK_F)) {
                    mainCharacter.makeFireball();
                }
                
                if (activeKeys.contains(VK_W)) {
                    camera.moveCameraDown(timeDelta);
                } else if (activeKeys.contains(VK_S)) {
                    camera.moveCameraUp(timeDelta);
                } else {
                    camera.resetCameraShift(timeDelta);
                    camera.resetCameraUp();
                    camera.resetCameraDown();
                }
            }
            
            if (activeKeys.contains(VK_0)) {
                camera.layerRendering = 0;
                IS_GAME_RUNNING = true;
            }
            if (activeKeys.contains(VK_1)) {
                camera.layerRendering = 1;
                IS_GAME_RUNNING = false;
            }
            if (activeKeys.contains(VK_2)) {
                camera.layerRendering = 2;
                IS_GAME_RUNNING = false;
            }
            if (activeKeys.contains(VK_3)) {
                camera.layerRendering = 3;
                IS_GAME_RUNNING = false;
            }
        }
    }
    
    /**
     * Calculates the pixel size based on screen width
     * @return relative pixel/position ratio based on the screen width
     */
    public static double scaledPixelSize() {
        return (double) screenWidth / (double) camera.WIDTH;
    }
    
    /**
     * Shifts the camera based on player position
     * @param timeDelta time since last tick
     */
    public static void moveCamera(double timeDelta) {
        if (mainCharacter != null) {
            Point relative = camera.position.minus(mainCharacter.position.plus(mainCharacter.getSize().width / 2.0, mainCharacter.getSize().height / 2.0));
            double moveX = 0;
            double moveY = 0;
            if (relative.x() < -camera.THRESHOLD) {
                moveX = -camera.THRESHOLD - relative.x();
            }
            if (relative.x() > camera.THRESHOLD) {
                moveX = camera.THRESHOLD - relative.x();
            }
            
            if (relative.y() < -camera.THRESHOLD) {
                moveY = -camera.THRESHOLD - relative.y();
            }
            if (relative.y() > camera.THRESHOLD) {
                moveY = camera.THRESHOLD - relative.y();
            }
            
            camera.position = camera.position.plus(moveX, moveY);
        }
    }
    
    /**
     * Initiates the screen
     */
    public static void setupCamera() {
        // TODO: start menu?
        GamePanel panel = new GamePanel();
        JFrame frame = new JFrame("Lethal Habit");
        
        // KeyListener 
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                activeKeys.add(e.getKeyCode());
            }
            
            public void keyReleased(KeyEvent e) {
                activeKeys.removeIf(key -> key == e.getKeyCode());
            }
        });
        
        // Frame Settings
        frame.setContentPane(panel);
        frame.setUndecorated(true);
        
        // Set the frame to full-screen mode and automatically resize the window to fit the screen
        if (MINIMIZED) {
            frame.setSize(500, 500);
        } else {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        
        frame.setResizable(false);
        frame.setVisible(true);
        
        screenWidth = frame.getWidth();
        screenHeight = frame.getHeight();
    }
    
    public static void createStartWindow() {
        JFrame startFrame = new JFrame("Lethal Habit");
        JPanel panel = new JPanel();
        // menu.setUndecorated(true);
        
        // Set the menu to full-screen mode and automatically resize the window to fit the screen
        
        startFrame.setUndecorated(true);
        
        startFrame.setSize(200, 200);
        startFrame.setResizable(false);
        startFrame.setVisible(true);
        
        JLabel gameLabel = new JLabel("Lethal Habit");
        JButton startButton = new JButton("Start");
        JButton settingsButton = new JButton("Settings");
        JButton exitButton = new JButton("Exit");
        
        // New Panel mit Button Layout
        panel.setLayout(new BorderLayout());
        
        // action event listeners:
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
        
        panel.add(gameLabel, BorderLayout.PAGE_START);
        panel.add(startButton, BorderLayout.CENTER);
        panel.add(settingsButton, BorderLayout.EAST);
        panel.add(exitButton, BorderLayout.SOUTH);
        
        startFrame.add(panel);
        startFrame.setLocationRelativeTo(null);
        startFrame.setVisible(true);
    }
    
    public static void createSettingsWindow() {
        String[] petStrings = {"Bird", "Cat", "Dog", "Rabbit", "Pig"};
        
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
        
        settingsFrame.setSize(200, 200);
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
        backButton.addActionListener(e -> {
            settingsFrame.dispose();
            createStartWindow();
        });
        
        panel.add(gameLabel, BorderLayout.NORTH);
        panel.add(petList, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);
        
        settingsFrame.add(panel);
        settingsFrame.setLocationRelativeTo(null);
        settingsFrame.setVisible(true);
    }
    
}
