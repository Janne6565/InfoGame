package lethalhabit.testing;

import lethalhabit.Main;
import lethalhabit.game.EventArea;
import lethalhabit.game.Interactable;
import lethalhabit.game.Player;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GrowShroom extends Interactable {
    public static Hitbox hitbox = new Hitbox(new Point[]{
            new Point(0, 0),
            new Point(40, 0),
            new Point(40, 40),
            new Point(0, 40)
    });
    public static BufferedImage graphic = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
    
    public GrowShroom(Point position) {
        super(position, hitbox, graphic);
    }
    
    @Override
    public void interact(Player player, float timeDelta) {
        player.size = new Dimension((int) (player.size.width + timeDelta * Main.SCALING_SPEED_GROWSHROOM), (int) (player.size.height + timeDelta * Main.SCALING_SPEED_GROWSHROOM));
    }
}
