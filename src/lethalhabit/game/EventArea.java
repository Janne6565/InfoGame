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

public abstract class EventArea implements Drawable {
    
    public final Hitbox hitbox;
    public final Point position;
    public final BufferedImage graphic;
    
    public EventArea(Point position, Hitbox hitbox, BufferedImage graphic) {
        this.position = position;
        this.hitbox = hitbox;
        this.graphic = graphic;
        Main.drawables.add(this);
    }
    
    public void onEnter(Player player) { }
    
    public void onLeave(Player player) { }
    
    public void onDeath(Player player) { }
    
    public void tick(Player player) { }
    
    public void onKeyInput(Player player, int key) { }
    
    @Override
    public void draw(Graphics g) {
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
