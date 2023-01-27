package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.technical.Point;
import lethalhabit.technical.TwoDimensional;

/**
 * Structure class for information of the position, speed, threshold, shift and width of the game
 */
public final class Camera {
    
    public static final int LAYER_GAME = 0;
    public static final int LAYER_MENU = 1;
    public static final int LAYER_MAP = 2;
    public static final int LAYER_SKILL_TREE = 3;
    
    public static final double ANIMATION_SPEED = 5;
    
    public final int WIDTH;
    public final int THRESHOLD;
    public final double SPEED;
    public final double SHIFT_LIMIT;
    public final double COOLDOWN_CAMERA_SHIFT = 0.5;
    
    /**
     * How much of the screen should be filled with the Map
     */
    public final TwoDimensional MAP_SCALE = new TwoDimensional() {
        @Override
        public double x() {
            return 0.9;
        }
        
        @Override
        public double y() {
            return 0.8;
        }
    };
    
    /**
     * Select the Layer that you want to Render \n
     * Map is only getting rendered on Layer 0
     */
    public int layerRendering = LAYER_GAME;
    
    public double moveCameraUpCooldown = COOLDOWN_CAMERA_SHIFT;
    public double moveCameraDownCooldown = COOLDOWN_CAMERA_SHIFT;
    
    public Point position;
    public Point shift;
    
    public Camera(Point position, int width, int threshold, double speed, double shiftLimit, int layerRendering) {
        this.position = position;
        this.WIDTH = width;
        this.THRESHOLD = threshold;
        this.SPEED = speed;
        this.shift = new Point(0, 0);
        this.SHIFT_LIMIT = shiftLimit;
        this.layerRendering = layerRendering;
    }
    
    /**
     * Calculates the real position of the camera (with the shift)
     *
     * @return real position of the camera
     */
    public Point getRealPosition() {
        return position.plus(shift);
    }
    
    /**
     * Calculates the height based on the width and the screen size
     *
     * @return the calculated height of the screen
     */
    public double getHeight() {
        return (double) WIDTH / Main.screenWidth * Main.screenHeight;
    }
    
    /**
     * Shifts the camera downwards
     *
     * @param timeDelta time since last tick (used to calculate the speed of the camera)
     */
    public void moveCameraDown(double timeDelta) {
        if (moveCameraDownCooldown > 0) {
            moveCameraDownCooldown = Math.max(moveCameraDownCooldown - timeDelta, 0);
        } else {
            shift = new Point(shift.x(), Math.max(shift.y() - Math.abs(-SHIFT_LIMIT - shift.y()) * ANIMATION_SPEED * timeDelta, -SHIFT_LIMIT));
        }
    }
    
    /**
     * Resets the cooldown for the camera downward movement
     */
    public void resetCameraDown() {
        moveCameraDownCooldown = COOLDOWN_CAMERA_SHIFT;
    }
    
    /**
     * Shifts the camera upwards
     *
     * @param timeDelta time since last tick (used to calculate the speed of the camera)
     */
    public void moveCameraUp(double timeDelta) {
        if (moveCameraUpCooldown > 0) {
            moveCameraUpCooldown = Math.max(moveCameraUpCooldown - timeDelta, 0);
        } else {
            shift = new Point(shift.x(), Math.min(shift.y() + Math.abs(SHIFT_LIMIT - shift.y()) * ANIMATION_SPEED * timeDelta, SHIFT_LIMIT));
        }
    }
    
    /**
     * Resets the cooldown for the camera upward movement
     */
    public void resetCameraUp() {
        moveCameraUpCooldown = COOLDOWN_CAMERA_SHIFT;
    }
    
    /**
     * Resets the shift of the camera to go back to (0, 0)
     *
     * @param timeDelta time since last tick (used to calculate the speed of the camera)
     */
    public void resetCameraShift(double timeDelta) {
        if (shift.y() <= 0.5 && shift.y() >= -0.5) {
            shift = new Point(shift.x(), 0);
        } else {
            shift = new Point(shift.x(), shift.y() - shift.y() * ANIMATION_SPEED * timeDelta);
        }
    }
    
    public Point getAbsolutePosition(Point position) {
        double offsetX = getRealPosition().x();
        double offsetY = getRealPosition().y();
        int posXDisplay = (int) ((int) (position.x() - offsetX) * Main.scaledPixelSize() + (Main.screenWidth / 2));
        int posYDisplay = (int) ((int) (position.y() - offsetY) * Main.scaledPixelSize() + (Main.screenHeight / 2));
        return new Point(posXDisplay, posYDisplay);
    }
    
}
