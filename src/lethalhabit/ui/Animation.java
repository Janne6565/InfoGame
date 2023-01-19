package lethalhabit.ui;

import lethalhabit.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Animation implements Iterable<BufferedImage> {
    
    public final double frameTime;
    public final double animationTime; // Time needed for one run of the animation
    public final ArrayList<BufferedImage> frames;

    /**
     * Structure class for all information needed to compute animations (loads the images)
     * @param frameTime time between each frame (1/fps)
     * @param animationPath path where the images of the animation are saved in
     * @param maxWidth max width the images are being displayed at
     */
    public Animation(double frameTime, String animationPath, double maxWidth) {
        this.frameTime = frameTime;
        frames = new ArrayList<>();
        int frameCount;
        for (frameCount = 0; ; frameCount++) {
            try {
                BufferedImage baseImage = ImageIO.read(new File("assets/animation/" + animationPath + "/(" + (frameCount + 1) + ").png"));
                int width = (int) (maxWidth * Main.scaledPixelSize());
                int height = (int) (maxWidth / baseImage.getWidth() * baseImage.getHeight() * Main.scaledPixelSize());
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
    
    @Override
    public Iterator<BufferedImage> iterator() {
        return frames.iterator();
    }
    
    @Override
    public void forEach(Consumer<? super BufferedImage> action) {
        frames.forEach(action);
    }
    
    @Override
    public Spliterator<BufferedImage> spliterator() {
        return frames.spliterator();
    }
    
    public BufferedImage get(int index) {
        return frames.get(index);
    }
    
}
