package lethalhabit.sound;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SoundQueue implements Runnable {
    
    private final List<Sound> queue;
    private final ReentrantLock lock;
    private final Condition playCondition;
    
    public SoundQueue(List<Sound> queue) {
        this.queue = new ArrayList<>(queue);
        this.lock = new ReentrantLock();
        this.playCondition = lock.newCondition();
    }
    
    @Override
    public void run() {
        playNext();
    }
    
    public void waitFor() throws InterruptedException {
        lock.lock();
        if (!queue.isEmpty()) {
            try {
                playCondition.await();
            } finally {
                lock.unlock();
            }
        } else {
            lock.unlock();
        }
    }
    
    private void playNext() {
        if (queue.size() > 0) {
            lock.lock();
            try {
                Sound sound = queue.remove(0);
                sound.addLineListener(new LineListener() {
                    public void update(LineEvent event) {
                        if (event.getType().equals(LineEvent.Type.STOP)) {
                            sound.removeLineListener(this);
                            playNext();
                        }
                    }
                });
                sound.play();
            } finally {
                lock.unlock();
            }
        } else {
            lock.lock();
            try {
                playCondition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

}
