package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.technical.Point;

/**
 * Structure class for information of the position, speed, threshold, shift and width of the game
 */
public final class Camera {
    
    public Point position;
    public int width;
    public int threshold;
    public double speed;
    public Point shift;
    public double shiftLimit;

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
        return (float) width / Main.screenWidth * Main.screenHeight;
    }
}
