package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.Player;
import lethalhabit.technical.Hitbox;
import lethalhabit.technical.Point;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public interface Interactable {

    public static int INTERACTION_KEY = KeyEvent.VK_F;
    public static String INTERACTION_MESSAGE = "PRESS " + KeyEvent.getKeyText(INTERACTION_KEY) + " TO INTERACT";

    Hitbox getHitbox();
    Point getPosition();

    public default void init() {
        EventArea eventArea = new EventArea(getPosition(), getHitbox()) {
            @Override
            public BufferedImage getGraphic() {
                return null;
            }

            @Override
            public Dimension getSize() {
                return new Dimension((int) (getHitbox().maxX() - getHitbox().minX()), (int) (getHitbox().maxY() - getHitbox().minY()));
            }

            @Override
            public Point getPosition() {
                return position;
            }

            @Override
            public int layer() {
                return 0;
            }

            @Override
            public void onPlayerEnterArea(Player player) {
                return;
            }

            @Override
            public void whilePlayerIn(Player player) {
                Main.GAME_PANEL.summonToolTip(INTERACTION_MESSAGE, .3);
                return;
            }

            @Override
            public void onPlayerKeyPressInArea(Player player, int key) {
                if (key == INTERACTION_KEY) {
                    interact(player);
                }
            }

            @Override
            public void onPlayerDieInArea(Player player) {
                return;
            }

            @Override
            public void onPlayerLeaveArea(Player mainCharacter) {
                return;
            }
        };
        Main.registerEventArea(eventArea);
    }

    public void interact(Player player);
}
