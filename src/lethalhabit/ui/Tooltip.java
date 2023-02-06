package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.math.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tooltip {
    
    public static final double FADE_DURATION = 1;
    public static final int FONT_SIZE = 5;
    public static final double Y_POSITION = 0.8; // Represented as by the distance from top of screen (no problem for the detailed explanation <3)
    
    public String text;
    public double duration;
    public double opacity = 0.0;
    
    public Tooltip(String text, double duration) {
        this.text = text;
        this.duration = duration;
    }
    
    public void draw(Graphics g) {
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, (int) (FONT_SIZE * Main.scaledPixelSize() * 16 / 12)));
        int width = g.getFontMetrics().stringWidth(text);
        int height = g.getFontMetrics().getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) img.getGraphics();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, (int) (FONT_SIZE * Main.scaledPixelSize() * 16 / 12 /* Constant for px to pt */)));
        graphics.drawString(text, 0, (int) (height - FONT_SIZE * Main.scaledPixelSize() * 0.5));
        g.drawImage(img, (Main.screenWidth - width) / 2, (int) (Main.screenHeight * Y_POSITION - height / 2), null);
    }
    
}