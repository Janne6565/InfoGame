package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class MainMenuButton extends Clickable implements Drawable {
    
    
    public static double TIME_MAX_HOVER = 0.2;
    public static double MAX_SCALE = 0.1;
    
    
    public String text;
    public BufferedImage image;
    public double timeHovered = 0;
    public Hitbox hitbox;
    
    private Point relativeScreenPosition;

    public MainMenuButton(Point position, String text) {
        super(position);
        
        this.relativeScreenPosition = position;
        this.text = text;
        
        
        int size = (int) (30 * Main.scaledPixelSize());
        
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, size);
        FontMetrics fontMetrics = Main.GAME_PANEL.getFontMetrics(font);
        int width = fontMetrics.stringWidth(text);
        int height = fontMetrics.getHeight();
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);
        graphics.drawString(text, 0, height);
        graphics.dispose();
        this.image = image;
    
        int positionX = (int) (relativeScreenPosition.x() / 100.0 * Main.GAME_PANEL.getWidth() - image.getWidth() / 2.0);
        int positionY = (int) (relativeScreenPosition.y() / 100.0 * Main.GAME_PANEL.getHeight() - image.getHeight() / 2.0);
        
        this.position = new Point(positionX, positionY);
        
        this.hitbox = new Hitbox(new Point(0, 0), new Point(0, height), new Point(width, height), new Point(width, 0));
        Main.drawables.add(this);
    }
    
    public abstract void onClick();
    
    @Override
    public Hitbox getHitbox() {
        return hitbox;
    }
    
    @Override
    public void onLeftClick(double timeDelta) {
        onClick();
    }
    
    @Override
    public void onHover(double timeDelta) {
        timeHovered = Math.min(timeHovered + timeDelta, TIME_MAX_HOVER);
    }
    
    @Override
    public void onReset(double timeDelta) {
        timeHovered = Math.max(0, timeHovered - timeDelta);
    }
    
    @Override
    public void onRightClick(double timeDelta) {}
    
    @Override
    public void onOnlyHover(double timeDelta) {}
    
    @Override
    public BufferedImage getGraphic() {
        return image;
    }
    
    @Override
    public Dimension getSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }
    
    @Override
    public Point getPosition() {
        return position;
    }
    
    @Override
    public int layer() {
        return Camera.LAYER_MENU;
    }
    
    @Override
    public void draw(Graphics graphics) {
        double scale = Math.min(1, 0.5 + timeHovered / TIME_MAX_HOVER * MAX_SCALE);
    
        int width = (int) (image.getWidth() * scale);
        int height = (int) (image.getHeight() * scale);
        
        this.hitbox = new Hitbox(new Point(0, 0), new Point(0, height), new Point(width, height), new Point(width, 0));
        
        int positionX = (int) (relativeScreenPosition.x() / 100.0 * Main.GAME_PANEL.getWidth() - image.getWidth() * scale / 2.0);
        int positionY = (int) (relativeScreenPosition.y() / 100.0 * Main.GAME_PANEL.getHeight() - image.getHeight() * scale / 2.0);
    
        this.position = new Point(positionX, positionY);
        graphics.drawImage(image, (int) position.x(), (int) position.y(), (int) (image.getWidth() * scale), (int) (image.getHeight() * scale), null);
    }
    
    
    public static class PlayButton extends MainMenuButton {
        
        public PlayButton(Point position) {
            super(position, "Start Game");
        }
    
        @Override
        public void onClick() {
            Main.play();
        }
    }
    
    public static class QuitButton extends MainMenuButton {
        
        public QuitButton(Point position) {
            super(position, "Quit Game");
        }
        
        @Override
        public void onClick() {
            Main.close();
        }
    }
}
