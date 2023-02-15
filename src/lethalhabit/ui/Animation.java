package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.game.Player;
import lethalhabit.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Animation implements Iterable<BufferedImage> {
    
    public static Animation PLAYER_IDLE;
    public static Animation PLAYER_WALK_LEFT;
    public static Animation PLAYER_WALK_RIGHT;
    
    public static double loadingProgress = 0;
    
    public final ArrayList<BufferedImage> frames;
    public final double frameTime;
    public final double animationTime; // Time needed to run the animation
    public final double animationOffset;
    
    public static void loadAnimations() {
        PLAYER_IDLE = new Animation(0.0416, "playerIdle", Player.WIDTH * Main.scaledPixelSize(), 0);
        PLAYER_WALK_LEFT = new Animation(0.0416, "playerWalkLeft", Player.WIDTH * Main.scaledPixelSize(), 0);
        PLAYER_WALK_RIGHT = new Animation(0.0416, "playerWalkLeft", Player.WIDTH * Main.scaledPixelSize(), 0);
    }
    
    /**
     * Structure class for all information needed to compute animations (loads the images)
     * @param frameTime       time between each frame (1/fps)
     * @param animationPath   path where the images of the animation are saved in
     * @param maxWidth        max width the images are being displayed at
     * @param animationOffset offset of the frames in seconds
     */
    public Animation(double frameTime, String animationPath, double maxWidth, double animationOffset) {
        this.frameTime = frameTime;
        this.animationOffset = animationOffset;
        frames = new ArrayList<>();
        int frameCount;
        for (frameCount = 0; ; frameCount++) {
            if (getClass().getResourceAsStream("/assets/animation/" + animationPath + "/" + (frameCount + 1) + ".png") == null) {
                break;
            }
        }
        this.animationTime = frameTime * frameCount;
        for (int i = 0; i <= frameCount; i++) {
            BufferedImage baseImage = Util.getImage("/assets/animation/" + animationPath + "/" + (i + 1) + ".png");
            if (baseImage != null) {
                int width = (int) (maxWidth * Main.scaledPixelSize());
                int height = (int) (maxWidth / baseImage.getWidth() * baseImage.getHeight() * Main.scaledPixelSize());
                BufferedImage frame = Util.bufferedImage(baseImage.getScaledInstance(width, height, Image.SCALE_SMOOTH));
                frames.add(frame);
                loadingProgress += (1.0 / frameCount) / 3.0;
            }
        }
    }
    
    public BufferedImage getCurrentFrame(double time) {
        int currentFrameIndex = (int) (((time + animationOffset) % animationTime) / frameTime);
        return frames.get(currentFrameIndex);
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
