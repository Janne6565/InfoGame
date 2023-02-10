package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public abstract class Interactable extends EventArea {
    
    public static final int INTERACTION_KEY = KeyEvent.VK_F;
    public static final String INTERACTION_MESSAGE = "Press " + KeyEvent.getKeyText(INTERACTION_KEY) + " to interact";
    
    public Interactable(Point position, Hitbox hitbox, BufferedImage graphic) {
        super(position, hitbox, graphic);
    }
    
    @Override
    public void tick(Player player) {
        Main.GAME_PANEL.showTooltip(INTERACTION_MESSAGE, 0.3);
    }
    
    @Override
    public void onKeyInput(Player player, int key) {
        if (key == INTERACTION_KEY) {
            interact(player);
        }
    }
    
    public abstract void interact(Player player);
    
}
