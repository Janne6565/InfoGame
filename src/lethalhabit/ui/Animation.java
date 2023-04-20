package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.game.Player;
import lethalhabit.game.enemy.Goomba;
import lethalhabit.game.skills.Skills;
import lethalhabit.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class Animation implements Iterable<BufferedImage> {
    
    public static final Animation EMPTY = new Animation(1, "", 0, 0);

    public static Animation PLAYER_IDLE_LEFT;
    public static Animation PLAYER_IDLE_RIGHT;
    public static Animation PLAYER_WALK_LEFT;
    public static Animation PLAYER_WALK_RIGHT;
    public static Animation PLAYER_FALL_LEFT;
    public static Animation PLAYER_FALL_RIGHT;
    public static Animation PLAYER_DOUBLE_JUMP_LEFT;
    public static Animation PLAYER_DOUBLE_JUMP_RIGHT;
    public static Animation PLAYER_SLASH_LEFT;
    public static Animation PLAYER_SLASH_RIGHT;
    
    public static Animation SLASH_LEFT;
    public static Animation SLASH_RIGHT;
    
    public static Animation SLAYA_WALK_LEFT;
    public static Animation SLAYA_WALK_RIGHT;
    public static Animation SLAYA_HIT_LEFT;
    public static Animation SLAYA_HIT_RIGHT;
    public static Animation SLAYA_IDLE_LEFT;
    public static Animation SLAYA_IDLE_RIGHT;
    
    public static Animation MAIN_MENU_BACKGROUND_ANIMATION;

    public static double loadingProgress = 0;
    
    public final ArrayList<BufferedImage> frames;
    public final double frameTime;
    public final double length; // Time needed to run the animation
    public final double animationOffset;
    
    public static void loadAnimations() {
        // Player Animations
        PLAYER_IDLE_RIGHT = new Animation(0.0416, "playerIdle", Player.WIDTH * Main.scaledPixelSize(), 0);
        PLAYER_IDLE_LEFT = new Animation(0.0416, PLAYER_IDLE_RIGHT.getMirroredAnimation(), Player.WIDTH * Main.scaledPixelSize(), 0);
        PLAYER_WALK_LEFT = new Animation(0.0416, "playerWalkLeft", Player.WIDTH * Main.scaledPixelSize(), 0);
        PLAYER_WALK_RIGHT = new Animation(0.0416, "playerWalkRight", Player.WIDTH * Main.scaledPixelSize(), 0);
        PLAYER_FALL_RIGHT = new Animation(0.0416, "playerFall", Player.WIDTH * Main.scaledPixelSize(), 0);
        PLAYER_FALL_LEFT = new Animation(0.0416, PLAYER_FALL_RIGHT.getMirroredAnimation(), Player.WIDTH * Main.scaledPixelSize(), 0);
        PLAYER_DOUBLE_JUMP_RIGHT = new Animation(0.0416 * 0.5, "playerDoubleJump", Player.WIDTH * Main.scaledPixelSize(), 0);
        PLAYER_DOUBLE_JUMP_LEFT = new Animation(0.0416 * 0.5, PLAYER_DOUBLE_JUMP_RIGHT.getMirroredAnimation(), Player.WIDTH * Main.scaledPixelSize(), 0);
        PLAYER_SLASH_RIGHT = new Animation(0.0416 * 0.3, "playerSlash", Player.WIDTH * Main.scaledPixelSize(), 0);
        PLAYER_SLASH_LEFT = new Animation(0.0416 * 0.3, PLAYER_SLASH_RIGHT.getMirroredAnimation(), Player.WIDTH * Main.scaledPixelSize(), 0);
        
        
        // Slash Animations
        SLASH_LEFT = new Animation(0.0416 * 0.65, "slash", Skills.getDefaultHitDimensions().getWidth() * Main.scaledPixelSize() * 5, 0);
        SLASH_RIGHT = new Animation(0.0416 * 0.65, SLASH_LEFT.getMirroredAnimation(), Skills.getDefaultHitDimensions().getWidth() * Main.scaledPixelSize() * 5, 0);
        
        // Others
        MAIN_MENU_BACKGROUND_ANIMATION = new Animation(0.0416, "backgroundAnimation", Main.screenWidth, 0);
        
        // Slaya Animations
        SLAYA_WALK_LEFT = new Animation(0.0416, "slayaWalk", Goomba.WIDTH, 0);
        SLAYA_WALK_RIGHT = new Animation(0.0416, SLAYA_WALK_LEFT.getMirroredAnimation(), Goomba.WIDTH * Main.scaledPixelSize(), 0);
        SLAYA_HIT_RIGHT = new Animation(0.0416, "slayaHit", Goomba.WIDTH * Main.scaledPixelSize(), 0);
        SLAYA_HIT_LEFT = new Animation(0.0416, SLAYA_HIT_RIGHT.getMirroredAnimation(), Goomba.WIDTH * Main.scaledPixelSize(), 0);
        SLAYA_IDLE_LEFT = new Animation(0.0416, "slayaIdle", Goomba.WIDTH * Main.scaledPixelSize(), 0);
        SLAYA_IDLE_RIGHT = new Animation(0.0416, SLAYA_IDLE_LEFT.getMirroredAnimation(), Goomba.WIDTH * Main.scaledPixelSize(), 0);
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
        this.length = frameTime * frameCount;
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

    public Animation(double frameTime, ArrayList<BufferedImage> frames, double maxWidth, double animationOffset) {
        this.frameTime = frameTime;
        this.animationOffset = animationOffset;
        int frameCount = frames.size();
        this.length = frameTime * frameCount;
        this.frames = frames;
    }
    
    public Animation(BufferedImage singleImage) {
        this.frameTime = 1;
        this.length = 1;
        this.animationOffset = 0;
        this.frames = new ArrayList<>(Arrays.asList(singleImage));
    }
        
    public ArrayList<BufferedImage> getMirroredAnimation() {
        ArrayList<BufferedImage> newFrames = new ArrayList<>();
        for (BufferedImage image : frames) {
            newFrames.add(Util.mirrorImage(image));
        }
        return newFrames;
    }
    
    public BufferedImage getCurrentFrame(double time) {
        int currentFrameIndex = (int) (((time + animationOffset) % length) / frameTime);
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
    
    public static void load() {
        loadAnimations();
    }
}
