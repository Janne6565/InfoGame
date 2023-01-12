package lethalhabit.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Animation {
    public double frameTime;
    public double animationTime; // Time needed for one run of the animation
    public ArrayList<BufferedImage> frames;

    public Animation(double frameTime, String animationPath) {
        this.frameTime = frameTime;
        frames = new ArrayList<>();
        int frameCount;
        for (frameCount = 0; ; frameCount++) {
            try {
                frames.add(ImageIO.read(new File("assets/animation/" + animationPath + "/ (" + (frameCount + 1) + ").gif")));
            } catch (IOException e) {
                break;
            }
        }
        animationTime = frameTime * frameCount;
    }
}
