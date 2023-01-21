package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.Player;
import lethalhabit.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Animation implements Iterable<BufferedImage> {
    
    public static Animation PLAYER_IDLE;
    public static Animation PLAYER_WALK;
    public static Animation PLAYER_MID_AIR;
    
    public static double loadingProgress = 0;
    
    public final ArrayList<BufferedImage> frames;
    public final double frameTime;
    public final double animationTime; // Time needed to run the animation
    
    public static void loadAnimations() {
        PLAYER_IDLE = new Animation(0.0416, "playerIdle", Player.WIDTH * Main.scaledPixelSize());
        PLAYER_WALK = new Animation(1, "playerWalk", Player.WIDTH * Main.scaledPixelSize());
        PLAYER_MID_AIR = new Animation(1, "playerMidAir", Player.WIDTH * Main.scaledPixelSize());
    }
    
    /**
     * Structure class for all information needed to compute animations (loads the images)
     * @param frameTime     time between each frame (1/fps)
     * @param animationPath path where the images of the animation are saved in
     * @param maxWidth      max width the images are being displayed at
     */
    public Animation(double frameTime, String animationPath, double maxWidth) {
        this.frameTime = frameTime;
        frames = new ArrayList<>();
        int frameCount;
        for (frameCount = 0; ; frameCount++) {
            if (getClass().getResourceAsStream("/assets/animation/" + animationPath + "/(" + (frameCount + 1) + ").png") == null) {
                break;
            }
        }
        this.animationTime = frameTime * frameCount;
        for (int i = 0; i <= frameCount; i++) {
            BufferedImage baseImage = Util.getImage("/assets/animation/" + animationPath + "/(" + (i + 1) + ").png");
            if (baseImage != null) {
                int width = (int) (maxWidth * Main.scaledPixelSize());
                int height = (int) (maxWidth / baseImage.getWidth() * baseImage.getHeight() * Main.scaledPixelSize());
                BufferedImage frame = Util.bufferedImage(baseImage.getScaledInstance(width, height, Image.SCALE_SMOOTH));
                frames.add(frame);
                loadingProgress += (1.0 / frameCount) / 3.0;
            }
        }
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
