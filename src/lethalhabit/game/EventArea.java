package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.math.Hitbox;
import lethalhabit.math.LineSegment;
import lethalhabit.math.Point;
import lethalhabit.testing.TestEventArea;
import lethalhabit.ui.Camera;
import lethalhabit.ui.Drawable;
import lethalhabit.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public abstract class EventArea implements Drawable {
    
    public final Hitbox hitbox;
    public Point position;
    public final BufferedImage graphic;
    
    
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

        if (!(beforeMinX == afterMinX && beforeMaxX == afterMaxX && beforeMinY == afterMinY && beforeMaxY == afterMaxY)) {
            Util.removeEventArea(this);
            position = position.plus(offset);
            Util.registerEventArea(this);
        } else {
            position = position.plus(offset);
        }
    }
    
    public void onEnter(Player player) { }

    public void onLeave(Player player) { }

    public void onDeath(Player player) { }

    public void tick(Player player) { }

    public void onKeyInput(Player player, int key, float timeDelta) { }

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
