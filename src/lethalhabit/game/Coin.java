package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.math.Direction;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.Animation;
import lethalhabit.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class Coin extends Entity {

    public static Hitbox COIN_HITBOX = new Hitbox(
            new Point(0, 0),
            new Point(0, 5),
            new Point(5, 5),
            new Point(5, 0)
    );
    
    public static Map<Integer, Animation> GRAPHIC_COINS;

    public static void loadCoinImages() {
        int width = (int) ((COIN_HITBOX.maxX() - COIN_HITBOX.minX()) * Main.scaledPixelSize());
        int height = (int) ((COIN_HITBOX.maxX() - COIN_HITBOX.minX()) * Main.scaledPixelSize());

        GRAPHIC_COINS = Map.of(
                1, new Animation(Util.getImage("/assets/coins/coin_1.png", width, height)),
                2, new Animation(Util.getImage("/assets/coins/coin_2.png", width, height)),
                5, new Animation(Util.getImage("/assets/coins/coin_5.png", width, height))
        );
    }

    public EventArea eventArea;
    public int value;
    
    /**
     * Constructs a new entity with given parameters (size is auto-calculated)
     *
     * @param position Absolute spawn position
     * @param value value of this coin
     */
    public Coin(Point position, int value) {
        super(COIN_HITBOX.maxX() - COIN_HITBOX.minX(), GRAPHIC_COINS.get(value).get(0), position, COIN_HITBOX);
        eventArea = new EventArea(position, COIN_HITBOX){
            @Override
            public void onEnter(Player player) {
                player.coins += value;
                despawn();
                Util.removeEventArea(this);
            }
        };

        Util.registerEventArea(eventArea);

        Random random = new Random();

        double randomDirection = (random.nextDouble() - 0.5) * 2;

        this.isAnimated = false;
        this.recoil = new Vec2D(randomDirection * 100, -200);
        this.resetRecoil = 100;
        this.value = value;
        this.graphic = GRAPHIC_COINS.get(value).get(0);
    }

    @Override
    public void changeTiles(Hitbox hitboxBefore, Hitbox hitboxAfter) {
        eventArea.moveAndRegister(hitboxAfter.vertices()[0].minus(hitboxBefore.vertices[0]));
        super.changeTiles(hitboxBefore, hitboxAfter);
    }

    public static double timeTakenToDraw = 0;

    @Override
    public void draw(Graphics graphics) {
        float time = System.currentTimeMillis();
        super.draw(graphics);
        timeTakenToDraw += System.currentTimeMillis() - time;
        eventArea.position = position;
    }

    @Override
    public void tick(Double timeDelta) {
        // float time = System.currentTimeMillis();
        super.tick(timeDelta);
        System.out.println("Drawen Time: " + timeTakenToDraw);
        timeTakenToDraw = 0;
        // System.out.println(time - System.currentTimeMillis());
    }

    @Override
    public Animation getAnimation() {
        return null;
    }

    @Override
    public int layer() {
        return 0;
    }

    @Override
    public void onCrashUp(Vec2D velocity) {
        recoil = new Vec2D(recoil.x(), 0);
        super.onCrashUp(velocity);
    }

    @Override
    public void onCrashRight(Vec2D velocity) {
        recoil = new Vec2D(0, recoil.y());
        super.onCrashRight(velocity);
    }

    @Override
    public void onCrashLeft(Vec2D velocity) {
        recoil = new Vec2D(0, recoil.y());
        super.onCrashRight(velocity);
    }

}
