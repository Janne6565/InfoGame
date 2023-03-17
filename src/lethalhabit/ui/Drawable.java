package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Graphical object that can be drawn on screen
 */
public interface Drawable {
    
    /**
     * @return The current display image of the drawable object
     */
    BufferedImage getGraphic();
    
    /**
     * @return The absolute display size of the drawable object (not necessarily the size of {@link Drawable#getGraphic()})
     */
    Dimension getSize();
    
    /**
     * @return The current absolute position of the drawable object
     */
    Point getPosition();
    
    /**
     * @return The layer the drawable object belongs to and is drawn in (one of {@link Camera#LAYER_GAME}, {@link Camera#LAYER_MAP}, {@link Camera#LAYER_MENU}, {@link Camera#LAYER_SKILL_TREE})
     */
    int layer();
    
    /**
     * Draws the drawable object with its current display image ({@link Drawable#getGraphic()}) at the current size ({@link Drawable#getSize()})
     * and position ({@link Drawable#getPosition()}), in the corresponding layer ({@link Drawable#layer()})
     *
     * @param graphics The graphics object to draw the drawable into
     */
    default void draw(Graphics graphics) {
        int posXDisplay = (int) ((getPosition().x() - Main.camera.getRealPosition().x()) * Main.scaledPixelSize() + (Main.screenWidth / 2));
        int posYDisplay = (int) ((getPosition().y() - Main.camera.getRealPosition().y()) * Main.scaledPixelSize() + (Main.screenHeight / 2));
        Image img = getGraphic().getScaledInstance((int) (getSize().width * Main.scaledPixelSize()), (int) (getSize().height * Main.scaledPixelSize()), Image.SCALE_DEFAULT);
        graphics.drawImage(img, posXDisplay, posYDisplay, (int) (getSize().width * Main.scaledPixelSize()), (int) (getSize().height * Main.scaledPixelSize()), null);
    }
    
}
