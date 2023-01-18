package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.technical.Point;

import java.awt.*;
import java.util.ArrayList;

/**
 * Structure Class for Informations of the Position, Speed, Threshhold, Shift and Width of the game
 */
public final class Camera {
    
    public Point position;
    public int width;
    public int threshhold;
    public double speed;
    public Point shift;
    public double shiftLimit;

    public Camera(Point position, int width, int threshhold, double speed, double shiftLimit) {
        this.position = position;
        this.width = width;
        this.threshhold = threshhold;
        this.speed = speed;
        this.shift = new Point(0, 0);
        this.shiftLimit = shiftLimit;
    }

    /**
     * Calculates the Real Position of the camera (with the shift)
     * @return Real Position of the Camera
     */
    public Point getRealPosition() {
        return position.plus(shift);
    }

    /**
     * Calculates the Height based on the width and the Screen Size
     * @return the Calculated Height of the Screen
     */
    public double getHeight() {
        return (float) width / Main.screenWidth * Main.screenHeight;
    }
}
