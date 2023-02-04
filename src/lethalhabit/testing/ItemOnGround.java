package lethalhabit.testing;

import lethalhabit.Player;
import lethalhabit.game.Interactable;
import lethalhabit.technical.Hitbox;
import lethalhabit.technical.Point;

public abstract class ItemOnGround implements Interactable {

    public static Hitbox HITBOX_ITEMS = new Hitbox(new Point[]{
            new Point(0, 0),
            new Point(20, 0),
            new Point(20, 20),
            new Point(0, 20),
    });

    public Point position;

    public ItemOnGround(Point position) {
        this.position = position;
        init();
    }

    @Override
    public Hitbox getHitbox() {
        return HITBOX_ITEMS;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void interact(Player player) {
        System.out.println("Player took Item");
    }
}
