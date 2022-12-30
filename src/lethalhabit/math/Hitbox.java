package lethalhabit.math;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public final class Hitbox implements Iterable<Position> {
    
    public Position[] points;
    
    public Hitbox(Position[] points) {
        this.points = points;
    }

    @Override
    public Iterator iterator() {
        return Arrays.stream(points).toList().iterator();
    }

    @Override
    public void forEach(Consumer<? super Position> action) {
        Arrays.stream(points).toList().forEach(action);
    }
}