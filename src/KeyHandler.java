public class KeyHandler {

    public static boolean keyPressed(int key) {
        try {
            return Main.keyPressed.get(key);
        } catch (Exception e) {
            return false;
        }
    }
}
