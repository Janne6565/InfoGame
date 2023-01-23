package lethalhabit.sound;

import java.util.List;

public final class AudioUtil {
    
    private AudioUtil() { }
    
    public void playSounds(List<Sound> sounds) {
        try {
            SoundQueue queue = new SoundQueue(sounds);
            Thread thread = new Thread(queue);
            thread.start();
            queue.waitFor();
        } catch (InterruptedException ignored) { }
    }
    
}
