package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.math.Hitbox;
import lethalhabit.util.Util;
import lethalhabit.world.Block;
import lethalhabit.world.Liquid;
import lethalhabit.world.Tile;
import lethalhabit.math.Point;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GamePanel used to draw game elements
 */
public final class GamePanel extends JPanel {
    
    public static final double FRAME_RATE = 280;
    
    public static Minimap minimap;
    
    private final Tooltip tooltip = new Tooltip("", 0.0);
    
    private long lastTick = System.currentTimeMillis();
    
    public GamePanel() {
        // Set up the update timer
        Timer updateTimer = new Timer((int) (1000.0 / FRAME_RATE), e -> repaint());
        updateTimer.start();
    }
    
    public static void generateMinimap() {
        minimap = new Minimap();
    }
    
    public static ArrayList<Hitbox> drawenHitboxesForDebugs = new ArrayList<>();
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Main.tick();
        
        for (Hitbox hitbox : drawenHitboxesForDebugs) {
            Util.drawHitbox(g, hitbox);
        }
        
        if (!Main.IS_GAME_LOADING) {
            if (Main.DEBUG_HITBOX) {
                g.drawString(Main.mainCharacter.position.toString(), 100, 100);
                g.drawString("(" + (int) (Main.mainCharacter.position.x() / Main.TILE_SIZE) + "|" + (int) (Main.mainCharacter.position.y() / Main.TILE_SIZE) + ")", 100, 130);
            }
            renderLayer(g, Main.camera.layerRendering);
        } else {
            int height = 40;
            int width = 200;
            g.setColor(Color.BLACK);
            g.drawRect((Main.screenWidth - width) / 2 - 1, (Main.screenHeight - height) / 2 - 1, width + 1, height + 1);
            g.setColor(Main.PROGRESS_BAR_COLOR);
            g.fillRect((Main.screenWidth - width) / 2, (Main.screenHeight - height) / 2, (int) (width * (Block.loadingProgress + Liquid.loadingProgress + Animation.loadingProgress) / 3.0), height);
        }
    }
    
    public void renderLayer(Graphics g, int layer) {
        double timeDelta = (System.currentTimeMillis() - lastTick) / 1000.0;
        lastTick = System.currentTimeMillis();
        
        List<Drawable> drawablesInLayer = Main.drawables.stream().filter(drawable -> drawable.layer() == Main.camera.layerRendering).toList();
        switch (layer) {
            case Camera.LAYER_GAME -> {
                drawMap(g);
                Point maxTotal = new Point(Main.camera.getRealPosition().x() + (double) Main.camera.width / 2, Main.camera.getRealPosition().y() + (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
                Point minTotal = new Point(Main.camera.getRealPosition().x() - (double) Main.camera.width / 2, Main.camera.getRealPosition().y() - (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
                for (Drawable drawable : drawablesInLayer) {
                    Point minDrawable = drawable.getPosition().minus(drawable.getSize().width, drawable.getSize().height);
                    Point maxDrawable = drawable.getPosition().plus(drawable.getSize().width, drawable.getSize().height);
                    if ((maxDrawable.compareTo(minTotal) > 0 && minDrawable.compareTo(maxTotal) < 0)) {
                        drawable.draw(g);
                    }
                }
                drawTooltip(g, timeDelta);
            }
            case Camera.LAYER_MENU -> {
                g.drawString("Main Menu", Main.screenWidth / 2, Main.screenHeight / 2);
            }
            case Camera.LAYER_MAP -> {
                minimap.draw(g);
                for (Drawable drawable : drawablesInLayer) {
                    drawable.draw(g);
                }
            }
        }
    }
    
    private void drawTooltip(Graphics g, double timeDelta) {
        tooltip.duration -= timeDelta;
        if (tooltip.opacity <= 1.0 && tooltip.duration > 0.0) {
            tooltip.opacity = Math.min(1, tooltip.opacity + timeDelta / Tooltip.FADE_DURATION);
        }
        if (tooltip.opacity != 0.0) {
            if (tooltip.duration <= 0.0) {
                tooltip.opacity = Math.max(0.0, tooltip.opacity - timeDelta / Tooltip.FADE_DURATION);
            }
            tooltip.draw(g);
        }
    }
    
    public void showTooltip(String text, double duration) {
        tooltip.text = text;
        tooltip.duration = duration;
    }
    
    /**
     * Draws the physical map (blocks and liquids).
     */
    private void drawMap(Graphics g) {
        int xRange = (int) (Main.camera.width / Main.TILE_SIZE) + 1;
        int yRange = (int) (Main.camera.getHeight() / Main.TILE_SIZE) + 1;
        Point cameraPositionTopLeft = Main.camera.getRealPosition().minus((double) Main.camera.width / 2, Main.camera.getHeight() / 2);
        Point indexTopLeft = cameraPositionTopLeft.scale(1 / Main.TILE_SIZE).minus(1, 1);
        for (int i = (int) indexTopLeft.x() - 1; i <= xRange + indexTopLeft.x() + 1; i++) {
            for (int j = (int) indexTopLeft.y() - 1; j <= yRange + indexTopLeft.y() + 1; j++) {
                double x = (i * Main.TILE_SIZE) - cameraPositionTopLeft.x();
                double y = (j * Main.TILE_SIZE) - cameraPositionTopLeft.y();
                Map<Integer, Tile> column = Main.map.get(i);
                if (column != null) {
                    Tile tile = column.get(j);
                    if (tile != null) {
                        Liquid liquid = Liquid.TILEMAP.get(tile.liquid);
                        if (liquid != null) {
                            g.drawImage(liquid.graphic, (int) (x * Main.scaledPixelSize()), (int) (y * Main.scaledPixelSize()), null);
                        }
                        Block block = Block.TILEMAP.get(tile.block);
                        if (block != null) {
                            g.drawImage(block.graphic, (int) (x * Main.scaledPixelSize()), (int) (y * Main.scaledPixelSize()), null);
                        }
                    }
                }
            }
        }
    }
    
}
