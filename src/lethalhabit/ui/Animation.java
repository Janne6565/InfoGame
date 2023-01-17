package lethalhabit.ui;

import lethalhabit.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Animation {
    public double frameTime;
    public double animationTime; // Time needed for one run of the animation
    public ArrayList<BufferedImage> frames;
    public int maxWidth;


    public Animation(double frameTime, String animationPath, double maxWidth) {
        this.frameTime = frameTime;
        frames = new ArrayList<>();
        int frameCount;
        for (frameCount = 0; ; frameCount++) {
            try {
                BufferedImage baseImage = ImageIO.read(new File("assets/animation/" + animationPath + "/(" + (frameCount + 1) + ").png"));
                int width = (int) (maxWidth * Main.scaledPixelSize());
                int height = (int) (maxWidth / baseImage.getWidth() * baseImage.getHeight());
                Image frame = baseImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                BufferedImage image = new BufferedImage(frame.getWidth(null), frame.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
                image.getGraphics().drawImage(frame, 0, 0, null);
                frames.add(image);
            } catch (IOException e) {
                break;
            }
        }
        animationTime = frameTime * frameCount;
    }
}
