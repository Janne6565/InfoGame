import java.awt.image.BufferedImage;

abstract public class PhysicsObject extends Drawable {
    public Hitbox hitbox;
    public boolean collidable = true;
    public vector2d velocity = new vector2d(0, 0);

    public PhysicsObject(float pWidth, String path, position pos) {
        super(pWidth, path, pos);
        Main.physicsObjects.add(this);
    }

    public void Tick(float timeDelta) {
        gravityStuff(timeDelta);
        position = position.addOn(velocity.x * timeDelta, velocity.y * timeDelta);
    }

    public void gravityStuff(float timeDelta) {
        if (onGround()) {
            if (velocity.y > 0) {
                velocity.y = 0;
            }
        } else {
            velocity.y += 100 * timeDelta;
        }
    }

    public boolean onGround() {
        System.out.println(position.y);
        if (position.y >= 100) {
            return true;
        } else {
            return false;
        }
    }
}