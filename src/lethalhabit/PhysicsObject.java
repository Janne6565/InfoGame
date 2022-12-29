package lethalhabit;

public abstract class PhysicsObject extends Drawable implements Tickable {
    
    public Hitbox hitbox;
    public boolean collidable = true;
    public Vec2D velocity = new Vec2D(0, 0);
    
    public PhysicsObject(float width, String path, Position pos) {
        super(width, path, pos);
        Main.physicsObjects.add(this);
    }
    
    public abstract double getSpeed();
    
    @Override
    public void tick(float timeDelta) {
        if (onGround()) {
            if (position.y() > 100) {
                position = new Position(position.x(), 100);
            }
            if (velocity.y() > 0) {
                velocity = new Vec2D(velocity.x(), 0);
            }
        } else {
            velocity = velocity.plus(0, 100 * timeDelta);
        }
        position = position.plus(velocity.x() * timeDelta, velocity.y() * timeDelta);
    }
    
    public void moveX(double amount) {
        velocity = new Vec2D(amount, velocity.y());
    }
    
    public void jump() {
        velocity = velocity.minus(0, 100);
    }
    
    public boolean onGround() {
        return position.y() >= 100;
    }
    
}