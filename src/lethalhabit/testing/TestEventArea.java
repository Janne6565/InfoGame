package lethalhabit.testing;

import lethalhabit.Main;
import lethalhabit.Player;
import lethalhabit.game.EventArea;
import lethalhabit.technical.Hitbox;
import lethalhabit.technical.LineSegment;
import lethalhabit.technical.Point;
import lethalhabit.ui.Drawable;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class TestEventArea extends EventArea implements Drawable {

    public TestEventArea(Point position, Hitbox hitbox) {
        super(position, hitbox);
        Main.drawables.add(this);
        System.out.println("Created area");
    }

    @Override
    public void onPlayerEnterArea(Player player) {
        System.out.println("Player entered Area");
    }

    @Override
    public void whilePlayerIn(Player player) {
        System.out.println("Player inside Area");
    }

    @Override
    public void onPlayerKeyPressInArea(Player player, int key) {
        System.out.println("Player pressed Key: " + KeyEvent.getKeyText(key));

    }

    @Override
    public void onPlayerDieInArea(Player player) {
        System.out.println("Player died inside area");
    }

    @Override
    public void onPlayerLeaveArea(Player mainCharacter) {
        System.out.println("Player leaved Area");
    }

    @Override
    public void draw(Graphics g) {
        for (LineSegment line : hitbox.shift(position).edges()) {
            LineSegment lineNew = line
                    .minus(Main.camera.position)
                    .scale(Main.scaledPixelSize())
                    .plus(Main.screenWidth / 2.0, Main.screenHeight / 2.0);

            g.drawLine((int) lineNew.a().x(), (int) lineNew.a().y(), (int) lineNew.b().x(), (int) lineNew.b().y());
        }
    }

    @Override
    public BufferedImage getGraphic() {
        return null;
    }

    @Override
    public Dimension getSize() {
        int width = (int) (hitbox.maxX() - hitbox.minX());
        int height = (int) (hitbox.maxY() - hitbox.minY());
        return new Dimension(width, height);
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public int layer() {
        return 0;
    }
}
