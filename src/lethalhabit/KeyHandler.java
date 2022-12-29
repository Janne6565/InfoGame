package lethalhabit;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public final class KeyHandler implements KeyListener {
    
    public static final KeyHandler INSTANCE = new KeyHandler();
    private static final List<Integer> activeKeys = new ArrayList<>();
    
    private KeyHandler() { }
    
    public static boolean keyPressed(int keyCode) {
        return activeKeys.contains(keyCode);
    }
    
    @Override
    public void keyTyped(KeyEvent e) { }
    
    @Override
    public void keyPressed(KeyEvent e) {
        activeKeys.add(e.getKeyCode());
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        activeKeys.removeIf(el -> el == e.getKeyCode());
    }
    
}
