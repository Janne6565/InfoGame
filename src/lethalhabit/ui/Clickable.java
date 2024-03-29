package lethalhabit.ui;

import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

public abstract class Clickable {
    
    public Point position;
    private Hitbox hitbox;
    
    public Clickable(Point position, Hitbox hitbox) {
        this.position = position;
        this.hitbox = hitbox;
    }
    
    public Clickable(Point position) {
        this.position = position;
    }
    
    public Hitbox getHitbox() {
        return hitbox;
    }
    
    public abstract void onLeftClick(double timeDelta);
    
    public abstract void onHover(double timeDelta);
    
    public abstract void onReset(double timeDelta);
    
    public abstract void onRightClick(double timeDelta);
    
    public abstract void onOnlyHover(double timeDelta);
}
