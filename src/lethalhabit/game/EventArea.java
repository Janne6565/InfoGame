package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.math.Hitbox;
import lethalhabit.math.LineSegment;
import lethalhabit.math.Point;
import lethalhabit.ui.Camera;
import lethalhabit.ui.Drawable;
import lethalhabit.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An event area - an area in the game that defines reactions to certain actions (like entering or leaving the event area)
 */
public abstract class EventArea implements Drawable {
    
    /**
     * Relative hitbox of the event area
     */
    public final Hitbox hitbox;
    
    /**
     * Display image of the event area, <code>null</code> if there is none
     */
    public final BufferedImage graphic;
    
    /**
     * Absolute position of the event area (top left corner)
     */
    public Point position;
    
    /**
     * Constructs a new event area from a position, hitbox and graphic
     *
     * @param position Absolute position
     * @param hitbox   Relative hitbox
     * @param graphic  Display image
     */
    public EventArea(Point position, Hitbox hitbox, BufferedImage graphic) {
        this.position = position;
        this.hitbox = hitbox;
        this.graphic = graphic;
        Main.drawables.add(this);
    }
    
    public void moveAndRegister(Point offset) {
        Hitbox hitboxBefore = hitbox.shift(position);
        Hitbox hitboxAfter = hitboxBefore.shift(offset);
        
        int beforeMinX = (int) (hitboxBefore.minX() / Main.TILE_SIZE);
        int beforeMaxX = (int) (hitboxBefore.maxX() / Main.TILE_SIZE);
        int beforeMinY = (int) (hitboxBefore.minY() / Main.TILE_SIZE);
        int beforeMaxY = (int) (hitboxBefore.maxY() / Main.TILE_SIZE);
        
        int afterMinX = (int) (hitboxAfter.minX() / Main.TILE_SIZE);
        int afterMaxX = (int) (hitboxAfter.maxX() / Main.TILE_SIZE);
        int afterMinY = (int) (hitboxAfter.minY() / Main.TILE_SIZE);
        int afterMaxY = (int) (hitboxAfter.maxY() / Main.TILE_SIZE);
        
        if (beforeMinX != afterMinX || beforeMaxX != afterMaxX || beforeMinY != afterMinY || beforeMaxY != afterMaxY) {
            Util.removeEventArea(this);
            position = position.plus(offset);
            Util.registerEventArea(this);
        }
    }
    
    /**
     * Method called when the player enters the event area
     *
     * @param player The player that entered the event area
     */
    public void onEnter(Player player) {
    }
    
    /**
     * Method called when the player leaves the event area
     *
     * @param player The player that left the event area
     */
    public void onLeave(Player player) {
    }
    
    /**
     * Method called when the player dies in the event area
     *
     * @param player The player that died in the event area
     */
    public void onDeath(Player player) {
    }
    
    /**
     * Method called every tick that the player remains in the event area
     *
     * @param player The player inside the event area
     */
    public void tick(Player player) {
    }
    
    /**
     * Method called when the player presses a key in the event area
     *
     * @param player The player inside the event area
     */
    public void onKeyInput(Player player, int key, float timeDelta) {
    }
    
    @Override
    public void draw(Graphics g) {
        Drawable.super.draw(g);
        if (Main.DEBUG_HITBOX) {
            for (LineSegment line : hitbox.shift(position).edges()) {
                Util.drawLineSegment(g, line);
            }
        }
    }
    
    @Override
    public BufferedImage getGraphic() {
        return graphic;
    }
    
    @Override
    public Dimension getSize() {
        return hitbox.getSize();
    }
    
    @Override
    public Point getPosition() {
        return position;
    }
    
    @Override
    public int layer() {
        return Camera.LAYER_GAME;
    }
    
}
