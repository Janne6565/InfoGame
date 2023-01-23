package lethalhabit.sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Sound {
    
    public static final float MAX_VOLUME = 6.0206f;
    public static final float MIN_VOLUME = -80f;
    
    private final Clip clip;
    
    private float previousVolume = MIN_VOLUME;
    private int previousPosition = 0;
    
    public Sound(AudioInputStream audio) throws IOException, LineUnavailableException {
        this.clip = AudioSystem.getClip();
        clip.open(audio);
    }
    
    public Sound(String path) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        this(AudioSystem.getAudioInputStream(Sound.class.getResourceAsStream(path)));
    }
    
    public Sound(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this(AudioSystem.getAudioInputStream(file));
    }
    
    public Sound(URL url) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this(AudioSystem.getAudioInputStream(url));
    }
    
    public Sound(InputStream stream) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this(AudioSystem.getAudioInputStream(stream));
    }
    
    /**
     * @return The length of the sound in milliseconds
     */
    public long getLengthMillis() {
        return clip.getMicrosecondLength() / 1000;
    }
    
    /**
     * @return The current volume of the sound
     */
    public float getVolume() {
        return ((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN)).getValue();
    }
    
    /**
     * Sets the volume of this sound
     * @param volume desired volume, between <code>MIN_VOLUME</code> and <code>MAX_VOLUME</code>
     */
    public void setVolume(float volume) {
        volume = Math.min(MAX_VOLUME, Math.max(MIN_VOLUME, volume));
        ((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN)).setValue(volume);
    }
    
    /**
     * Mutes the sound if it is active, or unmutes it if it's muted.
     */
    public void toggleMute() {
        float previous = getVolume();
        setVolume(previousVolume);
        previousVolume = previous;
    }
    
    /**
     * Starts looping the sound from the last position it was stopped, or from the beginning.
     */
    public void loop() {
        clip.setFramePosition(previousPosition);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
    /**
     * Starts playing the sound from the last position it was stopped, or from the beginning.
     */
    public void play() {
        clip.setFramePosition(previousPosition);
        clip.start();
    }
    
    /**
     * Stops the sound.
     */
    public void stop() {
        previousPosition = clip.getFramePosition();
        clip.stop();
    }
    
    /**
     * Closes the audio definitively, freeing used resources.
     */
    public void close() {
        clip.close();
    }
    
    /**
     * Adds a listener to this sound. Whenever the line's status changes, the
     * listener's {@code update()} method is called with a {@code LineEvent}
     * object that describes the change.
     *
     * @param  listener the object to add as a listener to this line
     */
    public void addLineListener(LineListener listener) {
        clip.addLineListener(listener);
    }
    
    /**
     * Removes the specified listener from this line's list of listeners.
     *
     * @param  listener listener to remove
     */
    public void removeLineListener(LineListener listener) {
        clip.removeLineListener(listener);
    }
    
}
