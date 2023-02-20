package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTree;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Hitbox;
import lethalhabit.math.LineSegment;
import lethalhabit.util.Util;
import lethalhabit.world.Block;
import lethalhabit.world.Liquid;
import lethalhabit.world.Tile;
import lethalhabit.math.Point;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * GamePanel used to draw game elements
 */
public final class GamePanel extends JPanel {
    
    public static final double FRAME_RATE = 280;
    private static final double FONT_SIZE_SKILL_TREE = 6;

    public static Minimap minimap;
    
    private final Tooltip tooltip = new Tooltip("", 0.0);
    
    private long lastTick = System.currentTimeMillis();

    public static Map<Integer, Map<Integer, BufferedImage>> mapBackgroundImages = new HashMap<>();
    public static ArrayList<String> pathsLoaded = new ArrayList<>();
    public static ArrayList<LazyBackgroundImageLoader> loadingThreads = new ArrayList<>();

    public static BufferedImage buffer;
    public boolean showed = false;

    public BufferedImage BACK_BUTTON = Util.getImage("/assets/hud/back.png");
    public float BACK_BUTTON_SCALE = 1;

    public BufferedImage SKILL_TREE_BACKGROUND = Util.getImage("/assets/hud/skillTree/background.png");
    public BufferedImage SKILL_TREE_CONNECTION = Util.getImage("/assets/hud/skillTree/connection_main.png");
    public BufferedImage SKILL_TREE_CONNECTION_SIDE = Util.getImage("/assets/hud/skillTree/connection_side.png");
    public Map<Integer, BufferedImage> SKILL_TREE_ICONS;
    public int SKILL_TREE_NODE_SIZE = 40;


    public static double SHIFT_SPEED = 200;
    public static double SHIFT_PER_ROW = 100;
    public SkillTreeNode nodeFocused = null;

    public GamePanel() {
        // Set up the update timer
        Timer updateTimer = new Timer((int) (1000.0 / FRAME_RATE), e -> repaint());
        updateTimer.start();
        SKILL_TREE_ICONS = new HashMap<>();

        for (int i = 0; i < 5; i++) {
            BufferedImage imageBeforeScale = Util.getImage("/assets/hud/skillTree/node" + i + ".png");
            BufferedImage SKILL_TREE_NODE_LEVEL = new BufferedImage((int) (SKILL_TREE_NODE_SIZE * Main.scaledPixelSize()), (int) (SKILL_TREE_NODE_SIZE * Main.scaledPixelSize()), BufferedImage.TYPE_INT_ARGB);
            SKILL_TREE_NODE_LEVEL.getGraphics().drawImage(imageBeforeScale, 0, 0, (int) (SKILL_TREE_NODE_SIZE * Main.scaledPixelSize()), (int) (SKILL_TREE_NODE_SIZE * Main.scaledPixelSize()), null);
            SKILL_TREE_ICONS.put(i, SKILL_TREE_NODE_LEVEL);
        }

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
                drawMapBackground(g);
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

                if (Main.DEVELOPER_MODE) {
                    if (Main.backgroundTileHovered != null) {
                        Hitbox hitbox = new Hitbox(
                                new Point((Main.backgroundTileHovered.x() + 1) * Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * minimap.scale, Main.backgroundTileHovered.y() * Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * minimap.scale),
                                new Point((Main.backgroundTileHovered.x()) * Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * minimap.scale, Main.backgroundTileHovered.y() * Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * minimap.scale),
                                new Point((Main.backgroundTileHovered.x()) * Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * minimap.scale, (Main.backgroundTileHovered.y() + 1) * Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * minimap.scale),
                                new Point((Main.backgroundTileHovered.x() + 1) * Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * minimap.scale, (Main.backgroundTileHovered.y() + 1) * Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * minimap.scale)
                        ).shift(minimap.positionDrawen);

                        for (LineSegment edge : hitbox.edges()) {
                            g.drawLine((int) edge.a().x(), (int) edge.a().y(), (int) edge.b().x(), (int) edge.b().y());
                        }
                    }
                }

                for (Drawable drawable : drawablesInLayer) {
                    drawable.draw(g);
                }
            }
            case Camera.LAYER_SKILL_TREE ->  {
                renderSkillTree(g);
            }
        }
    }

    private void drawMapBackground(Graphics g) {
        Point minRendered = Main.camera.getRealPosition().minus(Main.camera.width * 2, Main.camera.getHeight() * 2);
        Point maxRendered = Main.camera.getRealPosition().plus(Main.camera.width * 2, Main.camera.getHeight() * 2);

        Point renderMin = minRendered.divide(Main.TILE_SIZE).divide(Main.BACKGROUND_TILE_SIZE).toInt();
        Point renderMax = maxRendered.divide(Main.TILE_SIZE).divide(Main.BACKGROUND_TILE_SIZE).toInt();

        if (renderMin.x() == renderMax.x()) {
            renderMax = renderMax.plus(1, 0);
        }
        if (renderMin.y() == renderMax.y()) {
            renderMax = renderMax.plus(0, 1);
        }

        // TODO: Unload images not in range
        for (int x = (int) renderMin.x() - 1; x <= renderMax.x(); x++) {
            Map<Integer, String> images = Main.backgroundImages.getOrDefault(x, null);
            if (images != null) {
                for (int y = (int) renderMin.y() - 1; y <= renderMax.y(); y++) {
                    String path = images.getOrDefault(y, null);
                    Point positionOfImage = new Point(x, y).scale(Main.TILE_SIZE).scale(Main.BACKGROUND_TILE_SIZE).minus(Main.camera.getRealPosition()).plus(Main.camera.width / 2.0, Main.camera.getHeight() / 2).scale(Main.scaledPixelSize());
                    if (path != null) {
                        if (mapBackgroundImages.getOrDefault(x, null) != null) {
                            if (mapBackgroundImages.get(x).getOrDefault(y, null) != null) {
                                BufferedImage image = mapBackgroundImages.get(x).get(y);
                                if (
                                    positionOfImage.x() + (Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * Main.scaledPixelSize()) >= 0 &&
                                    positionOfImage.y() + (Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * Main.scaledPixelSize()) >= 0 &&
                                    positionOfImage.x() <= Main.screenWidth &&
                                    positionOfImage.y() <= Main.screenHeight
                                ) {
                                    double timeBefore = System.currentTimeMillis();
                                    g.drawImage(image, (int) positionOfImage.x(), (int) positionOfImage.y(), (int) (Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * Main.scaledPixelSize()), (int) (Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * Main.scaledPixelSize()), null);
                                }
                            } else {
                                if (!pathsLoaded.contains(path)) {
                                    LazyBackgroundImageLoader loader = new LazyBackgroundImageLoader(path, x, y, g);
                                    Thread thread = new Thread(loader);
                                    thread.start();
                                    pathsLoaded.add(path);
                                    loadingThreads.add(loader);
                                }
                            }
                        } else {
                            if (!pathsLoaded.contains(path)) {
                                LazyBackgroundImageLoader loader = new LazyBackgroundImageLoader(path, x, y, g);
                                Thread thread = new Thread(loader);
                                thread.start();
                                pathsLoaded.add(path);
                                loadingThreads.add(loader);
                            }
                        }
                    }
                }
            }
        }
    }


    ArrayList<Clickable> clickables = new ArrayList<>();

    public void clearClickables() {
        clickables.clear();
    }

    public void loadClickables() {
        switch (Main.camera.layerRendering) {
            case Camera.LAYER_SKILL_TREE -> {
                ArrayList<SkillTreeNode> skills;
                if (nodeFocused == null) {
                    SkillTree skillTree = Main.mainCharacter.PLAYER_SKILL_TREE;
                    skills = skillTree.startNodes;
                } else {
                    skills = nodeFocused.followingNodes;
                    Point position = new Point((int) (12.0 * Main.scaledPixelSize()), (int) (15.0 * Main.scaledPixelSize()));
                    Point pointBotRight = new Point((int) (BACK_BUTTON.getWidth() / 100.0 * 4.0 * Main.scaledPixelSize() * BACK_BUTTON_SCALE), (int) (BACK_BUTTON.getHeight() / 100.0 * 4.0 * Main.scaledPixelSize() * BACK_BUTTON_SCALE));
                    clickables.add(new Clickable(position, new Hitbox(new Point(0, 0), new Point(0, pointBotRight.y()), new Point(pointBotRight.x(), pointBotRight.y()), new Point(pointBotRight.x(), 0))) {
                        @Override
                        public void onClick(double timeDelta) {
                            nodeFocused = null;
                            clearClickables();
                            loadClickables();
                        }

                        @Override
                        public void onHover(double timeDelta) {
                            BACK_BUTTON_SCALE = (float) Math.min(BACK_BUTTON_SCALE + timeDelta * 0.3, 1.1);
                        }

                        @Override
                        public void onReset(double timeDelta) {
                            BACK_BUTTON_SCALE = (float) Math.max(BACK_BUTTON_SCALE - timeDelta * 0.3, 1);
                        }

                        @Override
                        public void onRightClick(double timeDelta) {

                        }

                        @Override
                        public void onOnlyHover(double timeDelta) {

                        }
                    });
                }

                for (SkillTreeNode skill : skills) {
                    BufferedImage skillTreeNode = SKILL_TREE_ICONS.get(skill.level);
                    BufferedImage imageBorder = new BufferedImage((int) (skillTreeNode.getWidth() * skill.scale), (int) (skillTreeNode.getHeight() * skill.scale), BufferedImage.TYPE_INT_ARGB);
                    imageBorder.getGraphics().drawImage(skillTreeNode, 0, 0, (int) (skillTreeNode.getWidth() * skill.scale), (int) (skillTreeNode.getHeight() * skill.scale), null);

                    Point pointToDrawNode = new Point(Main.screenWidth / 2.0, Main.screenHeight / 2.0)
                            .minus(imageBorder.getWidth() / 2.0, imageBorder.getHeight() / 2.0)
                            .plus(
                                skill.position.scale(SHIFT_PER_ROW).scale(Main.scaledPixelSize())
                            );

                    Hitbox hitbox = new Hitbox(new Point(0, 0), new Point(0, imageBorder.getWidth()), new Point(imageBorder.getHeight(), imageBorder.getWidth()), new Point(imageBorder.getHeight(), 0));
                    clickables.add(new UpgradeButtonSkilltree(pointToDrawNode, hitbox, skill));
                }
            }
        }
    }

    private void renderSkillTree(Graphics graphics) {
        SkillTree skillTree = Main.mainCharacter.PLAYER_SKILL_TREE;
        ArrayList<SkillTreeNode> skills;

        float skilltreeWidthProportion = (float) Main.screenWidth / SKILL_TREE_BACKGROUND.getWidth();
        float skilltreeHeightProportion = (float) Main.screenHeight / SKILL_TREE_BACKGROUND.getHeight();
        float proportionForImage = Math.min(skilltreeWidthProportion, skilltreeHeightProportion);

        int width = (int) (SKILL_TREE_BACKGROUND.getWidth() * proportionForImage);
        int height = (int) (SKILL_TREE_BACKGROUND.getHeight() * proportionForImage);
        Point pointForCentering = new Point(Main.screenWidth, Main.screenHeight).divide(2).minus(width / 2.0, height / 2.0);

        graphics.drawImage(SKILL_TREE_BACKGROUND, 0, 0, width, height, null);

        if (nodeFocused == null) {
            skills = skillTree.startNodes;
        } else {
            skills = nodeFocused.followingNodes;
            graphics.drawImage(BACK_BUTTON, (int) (12.0 * Main.scaledPixelSize()), (int) (15.0 * Main.scaledPixelSize()), (int) (BACK_BUTTON.getWidth() / 100.0 * 4.0 * Main.scaledPixelSize() * BACK_BUTTON_SCALE), (int) (BACK_BUTTON.getHeight() / 100.0 * 4.0 * Main.scaledPixelSize() * BACK_BUTTON_SCALE), null);
        }

        for (SkillTreeNode skill : skills) {
            BufferedImage imageInside = skill.image;
            BufferedImage icon = SKILL_TREE_ICONS.get(skill.level);
            BufferedImage imageBorder = new BufferedImage((int) (icon.getWidth() * skill.scale), (int) (icon.getHeight() * skill.scale), BufferedImage.TYPE_INT_ARGB);
            imageBorder.getGraphics().drawImage(icon, 0, 0, (int) (icon.getWidth() * skill.scale), (int) (icon.getHeight() * skill.scale), null);

            if (imageInside != null) {
                Graphics graphicsImageSmall = imageBorder.getGraphics();

                Point pointForCenteringBorderInside = new Point(imageBorder.getWidth() / 2.0 - imageInside.getWidth() / 2.0, imageBorder.getHeight() / 2.0 - imageInside.getHeight() / 2.0);
                graphicsImageSmall.drawImage(imageInside, (int) pointForCenteringBorderInside.x(), (int) pointForCenteringBorderInside.y(), null);
            }

            Point pointToDrawNode = new Point(Main.screenWidth / 2.0, Main.screenHeight / 2.0)
                    .minus(imageBorder.getWidth() / 2.0, imageBorder.getHeight() / 2.0)
                    .plus(
                            skill.position.scale(SHIFT_PER_ROW).scale(Main.scaledPixelSize())
                    );
            graphics.drawImage(imageBorder, (int) pointToDrawNode.x(), (int) pointToDrawNode.y(), null);

            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setFont(new Font(Font.MONOSPACED, Font.BOLD, (int) (FONT_SIZE_SKILL_TREE * Main.scaledPixelSize() * 16 / 12)));
            int textWidth = graphics2D.getFontMetrics().stringWidth(skill.name);
            int textHeight = graphics2D.getFontMetrics().getHeight();
            BufferedImage text = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) text.getGraphics();
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, (int) (FONT_SIZE_SKILL_TREE * Main.scaledPixelSize() * 16 / 12 /* Constant for px to pt */)));
            g.drawString(skill.name, 0, (int) (textHeight - FONT_SIZE_SKILL_TREE * Main.scaledPixelSize() * 0.5));

            Point pointToWriteTextCenter = pointToDrawNode.plus(imageBorder.getWidth() / 2.0, imageBorder.getHeight()).minus(textWidth / 2.0, 0);
            graphics.drawImage(text, (int) pointToWriteTextCenter.x(), (int) pointToWriteTextCenter.y(), null);
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

    public void handleMouseInputs(PointerInfo pointerInfo, List<Integer> mouseInputs, double timeDelta) {
        Point pointerPosition = new Point(pointerInfo.getLocation().x, pointerInfo.getLocation().y);
        for (Clickable clickable : new ArrayList<Clickable>(clickables)) {
            Hitbox shiftedHitbox = clickable.hitbox.shift(clickable.position);
            if (pointerPosition.x() > shiftedHitbox.minX() && pointerPosition.x() < shiftedHitbox.maxX() && pointerPosition.y() > shiftedHitbox.minY() && pointerPosition.y() < shiftedHitbox.maxY()) {
                clickable.onHover(timeDelta);
                if (mouseInputs.contains(3)) {
                    clickable.onRightClick(timeDelta);
                }

                if (mouseInputs.contains(1)) {
                    clickable.onClick(timeDelta);
                } else {
                    clickable.onOnlyHover(timeDelta);
                }
            } else {
                clickable.onReset(timeDelta);
            }
        }
    }
}
