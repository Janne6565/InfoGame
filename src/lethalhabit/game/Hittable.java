package lethalhabit.game;

import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

public interface Hittable {
    public Hitbox getHitbox();
    public Point getPosition();
    
    default public void onHit(){
        System.out.println("lol stop");
    }
}
