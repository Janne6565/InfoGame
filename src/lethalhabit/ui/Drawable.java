package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.math.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Drawable {
    
    BufferedImage getGraphic();
    Dimension getSize();
    Point getPosition();
    int layer();
    
    default void draw(Graphics graphics) {
        int posXDisplay = (int) ((int) (getPosition().x() - Main.camera.getRealPosition().x()) * Main.scaledPixelSize() + (Main.screenWidth / 2));
        int posYDisplay = (int) ((int) (getPosition().y() - Main.camera.getRealPosition().y()) * Main.scaledPixelSize() + (Main.screenHeight / 2));
        Image img = getGraphic().getScaledInstance((int) (getSize().width * Main.scaledPixelSize()), (int) (getSize().height * Main.scaledPixelSize()), Image.SCALE_DEFAULT);
        graphics.drawImage(img, posXDisplay, posYDisplay, (int) (getSize().width * Main.scaledPixelSize()), (int) (getSize().height * Main.scaledPixelSize()), null);
    }
    
}
