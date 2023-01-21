package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.technical.Point;

/**
 * Structure class for information of the position, speed, threshold, shift and width of the game
 */
public final class Camera {
    
    public final int width;
    public final int threshold;
    public final double speed;
    public final double shiftLimit;
    
    public Point position;
    public Point shift;
    
    public Camera(Point position, int width, int threshold, double speed, double shiftLimit) {
        this.position = position;
        this.width = width;
        this.threshold = threshold;
        this.speed = speed;
        this.shift = new Point(0, 0);
        this.shiftLimit = shiftLimit;
    }
    
    /**
     * Calculates the real position of the camera (with the shift)
     * @return real position of the camera
     */
    public Point getRealPosition() {
        return position.plus(shift);
    }
    
    /**
     * Calculates the height based on the width and the screen size
     * @return the calculated height of the screen
     */
    public double getHeight() {
        return (double) width / Main.screenWidth * Main.screenHeight;
    }
    
}
