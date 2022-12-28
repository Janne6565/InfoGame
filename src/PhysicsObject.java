import java.awt.image.BufferedImage;

abstract public class PhysicsObject extends Drawable {
    public position position;
    public Hitbox hitbox;
    public boolean collidable = true;

    public PhysicsObject(float pWidth, String path, position pos) {
        super(pWidth, path, pos);
        Main.physicsObjects.add(this);
    }
}
