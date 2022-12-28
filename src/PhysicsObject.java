import java.awt.image.BufferedImage;

abstract public class PhysicsObject extends Drawable {
    public Hitbox hitbox;
    public boolean collidable = true;

    public vector2d velocity = new vector2d(0, 0);
    public PhysicsObject(float pWidth, String path, position pos) {
        super(pWidth, path, pos);
        Main.physicsObjects.add(this);
    }

}
