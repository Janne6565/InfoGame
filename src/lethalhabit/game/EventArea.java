package lethalhabit.game;

import lethalhabit.Main;
import lethalhabit.Player;
import lethalhabit.technical.Hitbox;
import lethalhabit.technical.Point;

import java.util.ArrayList;

abstract public class EventArea {

    public Point position;
    public Hitbox hitbox;

    public ArrayList<Player> playersInArea = new ArrayList<>();
    public ArrayList<Enemy> enemiesInArea = new ArrayList<>();

    public EventArea(Point position, Hitbox hitbox) {
        this.position = position;
        this.hitbox = hitbox;
        Main.registerEventArea(this);
    }

    /**
     * Player entered area
     * @param player Player that entered the area
     */
    public abstract void onPlayerEnterArea(Player player);

    /**
     * Gets called every tick while a player is inside area
     * @param player Player inside the area
     */
    public abstract void whilePlayerIn(Player player);

    /**
     * called on Player press key
     * @param player Player that pressed the key
     * @param key Key pressed
     */
    public abstract void onPlayerKeyPressInArea(Player player, int key);

    /**
     * TODO: IMPLEMENT HP AND DEATH
     * @param player Player died
     */
    public abstract void onPlayerDieInArea(Player player);

    public abstract void onPlayerLeaveArea(Player mainCharacter);
}
