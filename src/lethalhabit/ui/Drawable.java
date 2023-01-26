package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.technical.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Drawable {
    
    BufferedImage getGraphic();
    Dimension getSize();
    Point getPosition();
    int layer();
    
    default void draw(Graphics graphics) {
        double pixelPerPixel = (double) Main.screenWidth / (double) Main.getScreenWidthGame();
        double offsetX = Main.camera.getRealPosition().x();
        double offsetY = Main.camera.getRealPosition().y();
        int posXDisplay = (int) ((int) (getPosition().x() - offsetX) * pixelPerPixel + (Main.screenWidth / 2));
        int posYDisplay = (int) ((int) (getPosition().y() - offsetY) * pixelPerPixel + (Main.screenHeight / 2));
        Image img = getGraphic().getScaledInstance((int) (getSize().width * pixelPerPixel), (int) (getSize().height * pixelPerPixel), Image.SCALE_DEFAULT);
        graphics.drawImage(img, posXDisplay, posYDisplay, (int) (getSize().width * pixelPerPixel), (int) (getSize().height * pixelPerPixel), null);
    }
    
}
