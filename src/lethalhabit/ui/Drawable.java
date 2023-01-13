package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.math.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public abstract class Drawable {
    
    public BufferedImage graphic;
    public double width;
    public double height;   
    
    public Point position;
    public boolean relative = true; // true if it's supposed to move with the camera false if it's supposed to be fixed on the screen (for UI elements in example)
    public String path;

    public Drawable(double width, String path, Point position) {
        try {
            graphic = ImageIO.read(new File("assets/" + path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.path = path;
        this.width = width;
        this.height = (this.width / graphic.getWidth()) * graphic.getHeight();
        this.position = position;
        Main.drawables.add(this);
    }

    public Drawable(double width, BufferedImage graphic, Point position) {
        this.graphic = graphic;
        this.path = "null";
        this.width = width;
        this.height = (this.width / graphic.getWidth()) * graphic.getHeight();
        this.position = position;
        Main.drawables.add(this);
    }

    public void draw(Graphics graphics) {
        double pixelPerPixel = (double) Main.screenWidth / (double) Main.getScreenWidthGame();
        double offsetX = relative ? Main.camera.position.x() : 0;
        double offsetY = relative ? Main.camera.position.y() : 0;
        int posXDisplay = (int) ((int) (position.x() - offsetX) * pixelPerPixel + (Main.screenWidth / 2));
        int posYDisplay = (int) ((int) (position.y() - offsetY) * pixelPerPixel + (Main.screenHeight / 2));
        Image img = graphic.getScaledInstance((int) (width * pixelPerPixel), (int) (height * pixelPerPixel), Image.SCALE_DEFAULT);
        graphics.drawImage(img, posXDisplay, posYDisplay, (int) (width * pixelPerPixel), (int) (height * pixelPerPixel), null);
    }
}
