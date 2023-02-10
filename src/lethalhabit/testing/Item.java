package lethalhabit.testing;

import lethalhabit.game.Player;
import lethalhabit.game.Interactable;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;
import lethalhabit.util.Util;

import java.awt.image.BufferedImage;

public abstract class Item extends Interactable {
    
    public static Hitbox HITBOX = new Hitbox(new Point[]{
            new Point(0, 0),
            new Point(20, 0),
            new Point(20, 20),
            new Point(0, 20),
    });
    
    public Item(Point position, BufferedImage graphic) {
        super(position, HITBOX, graphic);
    }
    
    @Override
    public void interact(Player player) {
        System.out.println("Player took Item");
    }
    
}
