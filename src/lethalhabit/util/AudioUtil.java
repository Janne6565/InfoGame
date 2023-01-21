package lethalhabit.util;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public final class AudioUtil {
    
    private AudioUtil() { }
    
    /**
     * Plays an audio from at a certain volume.
     * @param path   the path of the audio file/stream
     * @param volume the volume of the audio, in percent
     */
    public static void play(String path, int volume) {
        if (volume > 0) {
            new Thread(() -> {
                try {
                    AudioInputStream stream = AudioSystem.getAudioInputStream(AudioUtil.class.getResourceAsStream(path));
                    Clip clip = AudioSystem.getClip();
                    clip.open(stream);
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue((float) volume * 0.3f - 30.0f);
                    clip.start();
                } catch (Exception ignored) { }
            }).start();
        }
    }
    
}
