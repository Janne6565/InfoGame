package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.math.Point;
import lethalhabit.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class LazyBackgroundImageLoader implements Runnable {

    public String path;
    public int x, y;
    public Graphics graphics;

    public LazyBackgroundImageLoader(String path, int x, int y, Graphics graphics) {
        this.x = x;
        this.y = y;
        this.path = path;
        this.graphics = graphics;

    }

    @Override
    public void run() {
        System.out.println("Starting to load");
        BufferedImage image = Util.getImage(path);
        System.out.println("IMAGE LOADED: " + x + " " + y);
        BufferedImage bufferImage = new BufferedImage((int) (Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * Main.scaledPixelSize()), (int) (Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * Main.scaledPixelSize()), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferImage.createGraphics();
        g2.drawImage(image, 0, 0, (int) (Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * Main.scaledPixelSize()), (int) (Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * Main.scaledPixelSize()), null);
    
        Map<Integer, BufferedImage> mapX = GamePanel.mapBackgroundImages.getOrDefault(x, new HashMap<>());
        mapX.put(y, bufferImage);
        GamePanel.mapBackgroundImages.put(x, mapX);
    }
}
