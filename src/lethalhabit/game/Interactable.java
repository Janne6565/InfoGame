package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * An event area whose function is to interact with the player by pressing a key ({@link Interactable#INTERACTION_KEY})
 */
public abstract class Interactable extends EventArea {
    
    /**
     * The key that has to be pressed to interact
     */
    public static final int INTERACTION_KEY = KeyEvent.VK_F;
    
    /**
     * The message tooltip that is displayed when the player enters the interaction area
     */
    public static final String INTERACTION_MESSAGE = "Press " + KeyEvent.getKeyText(INTERACTION_KEY) + " to interact";
    
    /**
     * Constructs a new interactable object with a given position, hitbox and (optional) graphic
     *
     * @param position Absolute position
     * @param hitbox Relative hitbox of the interactable
     * @param graphic Display image (may be <code>null</code>)
     */
    public Interactable(Point position, Hitbox hitbox, BufferedImage graphic) {
        super(position, hitbox, graphic);
    }
    
    /**
     * @see EventArea#tick(Player)
     */
    @Override
    public void tick(Player player) {
        Main.GAME_PANEL.showTooltip(INTERACTION_MESSAGE, 0.3);
    }
    
    /**
     * @see EventArea#onKeyInput(Player, int, float)
     */
    @Override
    public void onKeyInput(Player player, int key, float timeDelta) {
        if (key == INTERACTION_KEY) {
            interact(player, timeDelta);
        }
    }
    
    /**
     * Abstract method called every tick the player is interacting (pressing {@link Interactable#INTERACTION_KEY}) with the interactable
     *
     * @param player The player that is interacting
     * @param timeDelta Time since last tick (in seconds)
     */
    public abstract void interact(Player player, float timeDelta);
    
}
