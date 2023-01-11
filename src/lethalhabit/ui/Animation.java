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

    public Animation(double frameTime, String animationPath, int frameCount) {
        this.frameTime = frameTime;
        animationTime = frameCount * frameTime;
        frames = new ArrayList<>();
        for (int i = 1; i <= frameCount; i ++) {
            String path = animationPath + " (" + i + ").gif";
            try {
                frames.add(ImageIO.read(new File("assets/animation/" + path)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
