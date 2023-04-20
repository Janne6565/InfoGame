package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;
import lethalhabit.math.Vec2D;
import lethalhabit.ui.Animation;
import lethalhabit.util.Util;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class Coin extends Entity {
    public static Hitbox COIN_HITBOX = new Hitbox(
            new Point(0, 0),
            new Point(0, 5),
            new Point(5, 5),
            new Point(5, 0)
    );
    
    public static Map<Integer, Animation> GRAPHIC_COINS;
    
    public int value;
    
    /**
     * Constructs a new entity with given parameters (size is auto-calculated)
     *
     * @param position Absolute spawn position
     * @param value value of this coin
     */
    public Coin(Point position, int value) {
        super(COIN_HITBOX.maxX() - COIN_HITBOX.minX(), GRAPHIC_COINS.get(value).get(0), position, COIN_HITBOX);
        System.out.println(value);
        this.recoil = new Vec2D(200, 20);
        this.resetRecoil = 200;
        this.value = value;
    }
    
    public static void loadCoinImages() {
        int width = (int) ((COIN_HITBOX.maxX() - COIN_HITBOX.minX()) * Main.scaledPixelSize());
        int height = (int) ((COIN_HITBOX.maxX() - COIN_HITBOX.minX()) * Main.scaledPixelSize());
        
         GRAPHIC_COINS = Map.of(
             1, new Animation(Util.getImage("/assets/coins/coin_1.png", width, height)),
             2, new Animation(Util.getImage("/assets/coins/coin_2.png", width, height)),
             5, new Animation(Util.getImage("/assets/coins/coin_5.png", width, height))
        );
    }
    
    @Override
    public Animation getAnimation() {
        return GRAPHIC_COINS.get(value);
    }
    
    @Override
    public int layer() {
        return 0;
    }
}
