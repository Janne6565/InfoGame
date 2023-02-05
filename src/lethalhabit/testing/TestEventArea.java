package lethalhabit.testing;

import lethalhabit.Main;
import lethalhabit.game.Player;
import lethalhabit.game.EventArea;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

import java.awt.event.KeyEvent;

public class TestEventArea extends EventArea {
    
    public TestEventArea(Point position, Hitbox hitbox) {
        super(position, hitbox, null);
        Main.drawables.add(this);
    }
    
    @Override
    public void onEnter(Player player) {
        System.out.println("Player entered area");
    }
    
    @Override
    public void tick(Player player) {
        Main.GAME_PANEL.showTooltip("Test tooltip", 1);
    }
    
    @Override
    public void onKeyInput(Player player, int key) {
        System.out.println("Player pressed key (" + KeyEvent.getKeyText(key) + ")");
    }
    
    @Override
    public void onDeath(Player player) {
        System.out.println("Player died inside area");
    }
    
    @Override
    public void onLeave(Player player) {
        System.out.println("Player left area");
    }
    
}
