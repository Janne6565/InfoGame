package lethalhabit;

import lethalhabit.game.*;
import lethalhabit.game.enemy.Frog;
import lethalhabit.game.enemy.Goomba;
import lethalhabit.sound.Sound;
import lethalhabit.math.Point;
import lethalhabit.testing.GrowShroom;
import lethalhabit.testing.TestItem;
import lethalhabit.ui.Animation;
import lethalhabit.ui.Camera;
import lethalhabit.ui.Drawable;
import lethalhabit.ui.GamePanel;
import lethalhabit.util.Settings;
import lethalhabit.util.Util;
import lethalhabit.world.Block;
import lethalhabit.world.Liquid;
import lethalhabit.world.Tile;
import org.ietf.jgss.GSSManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

import static java.awt.event.KeyEvent.*;

/**
 * Main program
 */
public final class Main {
    
    public static final Map<Integer, Class<? extends EventArea>> EVENT_AREA_TYPES = Map.of(
            0, TestItem.class
    );
    
    public static final Map<Integer, Class<? extends Entity>> ENTITY_TYPES = Map.of(
            0, Frog.class
    );
    
    public static final Settings SETTINGS = new Settings();
    
    public static final boolean MINIMIZED = false;
    public static final boolean DEBUG_HITBOX = false;

    /**
     * if true you gain following ability's:
     *  - Teleport
     *  - Map Renderer
     */
    public static final boolean DEVELOPER_MODE = true;
    
    public static final Color HITBOX_STROKE_COLOR = Color.RED;
    public static final Color PROGRESS_BAR_COLOR = new Color(0x7030e0);
    
    public static final int STROKE_SIZE_HITBOXES = 2;
    public static final double COLLISION_THRESHOLD = 1;
    public static final double MAX_VELOCITY_SPEED = 800;
    public static final double GRAVITATIONAL_ACCELERATION = 400;
    public static final double SAFE_DISTANCE = 0.05;

    public static final double TILE_SIZE = 20;
    public static final double BACKGROUND_TILE_SIZE = 200 / 20;
    public static final String BACKGROUND_EXPORT_PATH = "";

    public static final Set<Entity> entities = new HashSet<>();
    public static final Set<Drawable> drawables = new HashSet<>();
    public static final Set<Tickable> tickables = new HashSet<>();
    public static final float SCALING_SPEED_GROWSHROOM = 100;

    private static final List<Integer> activeKeys = new ArrayList<>();
    private static final List<Integer> activeMouseButtons = new ArrayList<>();
    private static final List<Integer> listKeysHolding = new ArrayList<>();
    public static boolean IS_GAME_LOADING = true;
    public static boolean IS_GAME_RUNNING = false;
    
    public static final Camera PLAYER_FOLLOWING_CAMERA = new Camera(new Point(0, 0), 400, 40, 80, 100, Camera.LAYER_MENU);
    
    public static Camera camera = PLAYER_FOLLOWING_CAMERA;
    
    public static int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width; // In Pixels based on the screen size
    public static int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height; // In Pixels based on the screen size
    public static Player mainCharacter;
    
    public static Map<Integer, Map<Integer, Tile>> map;
    public static Map<Integer, Map<Integer, String>> backgroundImages;
    public static Map<Integer, Map<Integer, List<EventArea>>> eventAreas;
    public static Map<Integer, Map<Integer, List<Hittable>>> hittables;
    
    public static Settings settings;
    
    private static long lastTick;

    public static Point backgroundTileHovered = null;
    
    public static void main(String[] args) {
        gameInit();
    }
    
    public static Tile tileAt(int tileX, int tileY) {
        Map<Integer, Tile> column = Main.map.get(tileX);
        return column != null ? column.get(tileY) : null;
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
        Animation.load();
        Block.load();
        Liquid.load();
        GamePanel.generateMinimap();
        loadBackgrounds();
        GAME_PANEL.instantiateButtons();
        mainCharacter = new Player(new Point(3616, 200)); // TODO: load skills from file
        mainCharacter.spawn();
        IS_GAME_LOADING = false;
        IS_GAME_RUNNING = true;
        /*
        testEventArea = new TestEventArea(new Point(3816, 500), new Hitbox(new Point[]{new Point(0, 0), new Point(100, 0), new Point(100, 100), new Point(0, 100)}));
        Util.registerEventArea(testEventArea);
         */
        Goomba enemy = new Goomba(new Point(3916, 500));
        enemy.spawn();
        // playSoundtrack();
        
        EventArea growshroom = new GrowShroom(new Point(3718, 500));
        Util.registerEventArea(growshroom);
    }

    private static void loadBackgrounds() {
        backgroundImages = Util.getBackgroundImages();
    }

    public static void playSoundtrack() {
        try {
            Sound soundtrack = new Sound("/assets/music/soundtrack1.wav");
            soundtrack.loop();
        } catch (Exception ignored) {
        }
    }
    
    /**
     * Loads the map (map.json).
     */
    public static void loadMap() {
        map = Util.readWorldData(Main.class.getResourceAsStream("/map.json"));
        eventAreas = Util.eventAreasFromMap(map);
    }
    
    public static void loadSettings() {
        settings = new Settings();
        // TODO: Read settings from some settings.json file
    }
    
    /**
     * Calculate camera width
     *
     * @return camera width in in-game unit
     */
    public static int getScreenWidthGame() {
        return camera.width;
    }
    
    /**
     * Method called on game tick
     */
    public static void tick(Graphics graphics) {
        double timeDelta = (double) (System.currentTimeMillis() - lastTick) / 1000.0;
        lastTick = System.currentTimeMillis();
        handleKeyInput(timeDelta, graphics);
        if (IS_GAME_RUNNING) {
            // testEventArea.moveAndRegister(new Point(10 * timeDelta, 0));
            
            for (Tickable tickable : new ArrayList<>(tickables)) {
                if (tickable != null) {
                    tickable.tick(timeDelta);
                }
            }
            checkEventAreas(timeDelta);
            moveCamera(timeDelta);
        }
    }
    
    public static List<EventArea> enteredEventAreas = new ArrayList<>();
    
    public static void checkEventAreas(double timeDelta) {
        List<EventArea> eventAreasBefore = new ArrayList<>(enteredEventAreas);
        enteredEventAreas = Util.getEventAreasPlayerIn(mainCharacter);
        for (EventArea area : enteredEventAreas) {
            area.tick(mainCharacter);
            if (!eventAreasBefore.contains(area)) {
                area.onEnter(mainCharacter);
            }
        }
        
        for (EventArea area : eventAreasBefore) {
            if (!enteredEventAreas.contains(area)) {
                area.onLeave(mainCharacter);
            }
        }
    }
    
    public static void handleKeyInput(double timeDelta, Graphics graphics) {
        GAME_PANEL.handleMouseInputs(MouseInfo.getPointerInfo(), activeMouseButtons, timeDelta, graphics);

        if (mainCharacter != null) {
            if (activeKeys.contains(VK_0)) {
                camera.changeLayer(0);
                IS_GAME_RUNNING = true;
            }
            if (activeKeys.contains(VK_1)) {
                camera.changeLayer(1);
                IS_GAME_RUNNING = false;
            }
            if (activeKeys.contains(VK_2)) {
                camera.changeLayer(2);
                IS_GAME_RUNNING = false;
            }
            if (activeKeys.contains(VK_3)) {
                camera.changeLayer(3);
                IS_GAME_RUNNING = false;
            }
            
            switch (camera.layerRendering) {
                case Camera.LAYER_MAP -> {
                    if (DEVELOPER_MODE) {
                        if (GamePanel.minimap.positionDrawen != null) {
                            Point mouseCoordinates = new Point(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
                            Point relativePointToMap = mouseCoordinates.minus(GamePanel.minimap.positionDrawen);
                            Point positionOfMouse = relativePointToMap.divide(GamePanel.minimap.scale);
                            // Teleport
                            if (activeKeys.contains(VK_T)) {
                                if (positionOfMouse.compareTo(new Point(0, 0)) > 0 && positionOfMouse.compareTo(new Point(GamePanel.minimap.size.width, GamePanel.minimap.size.height)) < 0) {
                                    mainCharacter.position = positionOfMouse;
                                }
                            }

                            // Render Map background Tiles:
                            if (positionOfMouse.compareTo(new Point(0, 0)) > 0 && positionOfMouse.compareTo(new Point(GamePanel.minimap.size.width, GamePanel.minimap.size.height)) < 0) {
                                backgroundTileHovered = new Point((int) positionOfMouse.x(), (int) positionOfMouse.y()).divide(TILE_SIZE).divide(BACKGROUND_TILE_SIZE).toInt();
                                if (activeKeys.contains(VK_R)) {
                                    Util.exportMapBackgroundTile(backgroundTileHovered);
                                }
                            }
                        }
                    }
                }
                
                case Camera.LAYER_GAME -> {
                    for (EventArea area : enteredEventAreas) {
                        for (Integer key : activeKeys) {
                            area.onKeyInput(mainCharacter, key, (float) timeDelta);
                        }
                    }
                    
                    if (IS_GAME_RUNNING) {
                        if (activeKeys.contains(VK_B)) {
                            mainCharacter.hit();
                        }
                        if (activeKeys.contains(VK_SPACE) && activeKeys.contains(VK_CONTROL) && mainCharacter.isSubmerged()) {
                            mainCharacter.stopMovementY(timeDelta);
                        } else if (activeKeys.contains(VK_SPACE)) {
                            if (mainCharacter.isSubmerged()) {
                                mainCharacter.moveUp(timeDelta);
                            } else {
                                mainCharacter.jump();
                            }
                        } else if (activeKeys.contains(VK_CONTROL)) {
                            if (mainCharacter.isSubmerged()) {
                                mainCharacter.moveDown(timeDelta);
                            }
                        } else {
                            mainCharacter.resetJump();
                        }
                        
                        if (activeKeys.contains(VK_SHIFT)) {
                            mainCharacter.dash();
                        }
                        
                        if (activeKeys.contains(VK_A) && !activeKeys.contains(VK_D)) {
                            if (!mainCharacter.isSubmerged()) {
                                mainCharacter.moveLeft(timeDelta);
                            } else {
                                mainCharacter.moveLeft(timeDelta);
                            }
                        } else if (activeKeys.contains(VK_D) && !activeKeys.contains(VK_A)) {
                            if (!mainCharacter.isSubmerged()) {
                                mainCharacter.moveRight(timeDelta);
                            } else {
                                mainCharacter.moveRight(timeDelta);
                            }
                        } else {
                            mainCharacter.stopMovementX(timeDelta);
                        }
                        
                        if (!(activeKeys.contains(VK_W) && activeKeys.contains(VK_S))) {
                            if (activeKeys.contains(VK_W)) {
                                camera.moveCameraDown(timeDelta);
                            } else {
                                camera.resetCameraShift(timeDelta);
                                camera.resetCameraDown();
                            }
                            if (activeKeys.contains(VK_S)) {
                                camera.moveCameraUp(timeDelta);
                            } else {
                                camera.resetCameraShift(timeDelta);
                                camera.resetCameraUp();
                            }
                        }
                    }
                }
                case Camera.LAYER_SKILL_TREE -> {
                    if (activeKeys.contains(VK_ESCAPE)) {
                        GAME_PANEL.nodeFocused = null;
                    }
                }
            }
        }
    }
    
    /**
     * Calculates the pixel size based on screen width
     *
     * @return relative pixel/position ratio based on the screen width
     */
    public static double scaledPixelSize() {
        return (double) screenWidth / (double) camera.width;
    }
    
    /**
     * Shifts the camera based on player position
     *
     * @param timeDelta time since last tick
     */
    public static void moveCamera(double timeDelta) {
        if (mainCharacter != null) {
            Point relative = PLAYER_FOLLOWING_CAMERA.position.minus(mainCharacter.position.plus(mainCharacter.getSize().width / 2.0, mainCharacter.getSize().height / 2.0));
            double moveX = 0;
            double moveY = 0;
            if (relative.x() < -PLAYER_FOLLOWING_CAMERA.threshold) {
                moveX = -PLAYER_FOLLOWING_CAMERA.threshold - relative.x();
            }
            if (relative.x() > PLAYER_FOLLOWING_CAMERA.threshold) {
                moveX = PLAYER_FOLLOWING_CAMERA.threshold - relative.x();
            }
            
            if (relative.y() < -PLAYER_FOLLOWING_CAMERA.threshold) {
                moveY = -PLAYER_FOLLOWING_CAMERA.threshold - relative.y();
            }
            if (relative.y() > PLAYER_FOLLOWING_CAMERA.threshold) {
                moveY = PLAYER_FOLLOWING_CAMERA.threshold - relative.y();
            }
            
            PLAYER_FOLLOWING_CAMERA.position = PLAYER_FOLLOWING_CAMERA.position.plus(moveX, moveY);
        }
    }
    
    
    public static GamePanel GAME_PANEL;
    
    public static Frame frame;
    
    /**
     * Initiates the screen
     */
    public static void setupCamera() {
        // TODO: start menu?
        GAME_PANEL = new GamePanel();
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

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                activeMouseButtons.add(e.getButton());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                activeMouseButtons.removeIf(key -> key == e.getButton());
            }
        });
        
        // Frame Settings
        frame.setContentPane(GAME_PANEL);
        frame.setUndecorated(true);
        
        // Set the frame to full-screen mode and automatically resize the window to fit the screen
        if (MINIMIZED) {
            screenWidth = 500;
            screenHeight = 500;
            frame.setSize(screenWidth, screenHeight);
        } else {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        frame.setResizable(false);
        frame.setVisible(true);
        frame.setBackground(Color.DARK_GRAY);
        Main.frame = frame;
    }
    
    public static void play() {
        camera.changeLayer(Camera.LAYER_GAME);
    }
    
    public static void close() {
        GAME_PANEL.updateTimer.stop();
        GAME_PANEL.setEnabled(false);
        GAME_PANEL.setVisible(false);
        Main.frame.dispose();
    }
}
