package lethalhabit.game;

/**
 * An object that can and will be ticked every time the game panel is refreshed (every frame).
 */
public interface Tickable {
    /**
     * Ticks the tickable object, telling it to update by providing the elapsed time since the last tick was executed. <br>
     * Any tick-based or real-time-based game logic is performed in this method.
     *
     * @param timeDelta Time since last tick (in seconds)
     */
    void tick(Double timeDelta);
}
