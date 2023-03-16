package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.game.skills.SkillTree;
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
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * GamePanel used to draw game elements
 */
public final class GamePanel extends JPanel {
    
    public static final double FRAME_RATE = 140;
    private static final double FONT_SIZE_SKILL_TREE = 6;
    
    public static Minimap minimap;
    
    private final Tooltip tooltip = new Tooltip("",  0.0);
    
    public float timeSinceXpGained = -1;
    private long lastTick = System.currentTimeMillis();
    
    // Background Image rendering
    public static Map<Integer, Map<Integer, BufferedImage>> mapBackgroundImages = new HashMap<>();
    public static ArrayList<String> pathsLoaded = new ArrayList<>();
    public static ArrayList<LazyBackgroundImageLoader> loadingThreads = new ArrayList<>();
    
    // Skill Tree Images
    public BufferedImage BACK_BUTTON = Util.getImage("/assets/hud/back.png");
    public float BACK_BUTTON_SCALE = 1;
    public BufferedImage SKILL_TREE_BACKGROUND = Util.getImage("/assets/hud/skillTree/background.png");
    public BufferedImage SKILL_TREE_CONNECTION = Util.getImage("/assets/hud/skillTree/connection_main.png");
    public BufferedImage SKILL_TREE_CONNECTION_SIDE = Util.getImage("/assets/hud/skillTree/connection_side.png");
    public int SKILL_TREE_NODE_SIZE = 40;
    public static Map<Integer, BufferedImage> SKILL_TREE_ICONS;
    
    public ArrayList<MainMenuButton> mainMenuButtons = new ArrayList<>();
    
    // Skill Tree variables
    public static double SHIFT_SPEED = 200;
    public static double SHIFT_PER_ROW = 100;
    public static double timeInGame = 0;
    public SkillTree.Node nodeFocused = null;
    
    
    public static int XP_BAR_PADDING_SIDE = 10;
    public static int XP_BAR_PADDING_TOP = 10;
    private static final double XP_BAR_ANIMATION_SPEED = 0.6;
    private static final double XP_BAR_FADE_IN_OUT_TIME = 0.3;
    private static final double XP_BAR_SHOW_TIME = 4;
    
    public Timer updateTimer;
    
    public GamePanel() {
        // Set up the update timer
        updateTimer = new Timer((int) (1000.0 / FRAME_RATE), e -> repaint());
        updateTimer.start();
        loadIcons();
    }
    
    private void loadIcons() {
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Main.tick(g);
        Graphics2D graphics2D = (Graphics2D) g;
        if (!Main.IS_GAME_LOADING) {
    
            double opacitySecondaryLayer = Main.camera.getOpacityOfSecondaryLayer();
            if (opacitySecondaryLayer > 0) {
                BufferedImage mainLayer = exportLayer(Main.camera.layerRendering, graphics2D.getClipBounds().width, graphics2D.getClipBounds().height);
                AlphaComposite ac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) ((float) 1.0 - opacitySecondaryLayer));
                graphics2D.setComposite(ac1);
                graphics2D.drawImage(mainLayer, 0, 0, null);
    
                BufferedImage secondaryLayer = exportLayer(Main.camera.layerBefore, graphics2D.getClipBounds().width, graphics2D.getClipBounds().height);
                AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacitySecondaryLayer);
                graphics2D.setComposite(ac);
                graphics2D.drawImage(secondaryLayer, 0, 0, null);
            } else {
                BufferedImage mainLayer = exportLayer(Main.camera.layerRendering, graphics2D.getClipBounds().width, graphics2D.getClipBounds().height);
                graphics2D.drawImage(mainLayer, 0, 0, null);
            }
            if (Main.DEVELOPER_MODE) {
                graphics2D.drawString(Main.mainCharacter.position.toString(), 100, 100);
                graphics2D.drawString("(" + (int) (Main.mainCharacter.position.x() / Main.TILE_SIZE) + "|" + (int) (Main.mainCharacter.position.y() / Main.TILE_SIZE) + ")", 100, 130);
            }
        } else {
            int height = 40;
            int width = 200;
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawRect((Main.screenWidth - width) / 2 - 1, (Main.screenHeight - height) / 2 - 1, width + 1, height + 1);
            graphics2D.setColor(Main.PROGRESS_BAR_COLOR);
            graphics2D.fillRect((Main.screenWidth - width) / 2, (Main.screenHeight - height) / 2, (int) (width * (Block.loadingProgress + Liquid.loadingProgress + Animation.loadingProgress) / 3.0), height);
        }
    }
    
    public void instantiateButtons() {
        mainMenuButtons.add(new MainMenuButton.PlayButton(new Point(50, 20)));
        mainMenuButtons.add(new MainMenuButton.QuitButton(new Point(50, 60)));
    }
    
    public ArrayList<Clickable> getClickables() {
        ArrayList<Clickable> clickis = new ArrayList<>();
        
        switch (Main.camera.layerRendering) {
            case Camera.LAYER_SKILL_TREE -> {
                List<SkillTree.Node> skills;
                if (nodeFocused == null) {
                    SkillTree skillTree = Main.mainCharacter.skillTree;
                    skills = skillTree.startNodes;
                } else {
                    skills = Arrays.asList(nodeFocused.nextNodes);
                    Point position = new Point((int) (12.0 * Main.scaledPixelSize()), (int) (15.0 * Main.scaledPixelSize()));
                    Point pointBotRight = new Point((int) (BACK_BUTTON.getWidth() / 100.0 * 4.0 * Main.scaledPixelSize() * BACK_BUTTON_SCALE), (int) (BACK_BUTTON.getHeight() / 100.0 * 4.0 * Main.scaledPixelSize() * BACK_BUTTON_SCALE));
                    clickis.add(new Clickable(position, new Hitbox(new Point(0, 0), new Point(0, pointBotRight.y()), new Point(pointBotRight.x(), pointBotRight.y()), new Point(pointBotRight.x(), 0))) {
                        @Override
                        public void onLeftClick(double timeDelta) {
                            nodeFocused = null;
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
            
                for (SkillTree.Node skill : skills) {
                    BufferedImage skillTreeNode = SKILL_TREE_ICONS.get(skill.getLevel());
                    BufferedImage imageBorder = new BufferedImage((int) (skillTreeNode.getWidth() * skill.scale), (int) (skillTreeNode.getHeight() * skill.scale), BufferedImage.TYPE_INT_ARGB);
                    imageBorder.getGraphics().drawImage(skillTreeNode, 0, 0, (int) (skillTreeNode.getWidth() * skill.scale), (int) (skillTreeNode.getHeight() * skill.scale), null);
                
                    Point pointToDrawNode = new Point(Main.screenWidth / 2.0, Main.screenHeight / 2.0)
                            .minus(imageBorder.getWidth() / 2.0, imageBorder.getHeight() / 2.0)
                            .plus(
                                    skill.position.scale(SHIFT_PER_ROW).scale(Main.scaledPixelSize())
                            );
                
                    Hitbox hitbox = new Hitbox(new Point(0, 0), new Point(0, imageBorder.getWidth()), new Point(imageBorder.getHeight(), imageBorder.getWidth()), new Point(imageBorder.getHeight(), 0));
                    clickis.add(new UpgradeButtonSkillTree(pointToDrawNode, hitbox, skill));
                }
            }
            case Camera.LAYER_MENU -> {
                clickis.addAll(mainMenuButtons);
            }
        }
        return clickis;
    }
    
    public BufferedImage exportLayer(int layer, int width, int height) {
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) outputImage.getGraphics();

        double timeDelta = (System.currentTimeMillis() - lastTick) / 1000.0;
        timeInGame += timeDelta;
        timeSinceXpGained += timeDelta;
        lastTick = System.currentTimeMillis();
        
        List<Drawable> drawablesInLayer = Main.drawables.stream().filter(drawable -> drawable.layer() == layer).toList();
        switch (layer) {
            case Camera.LAYER_GAME -> {
                drawMapBackground(g);
                Point maxTotal = new Point(Main.camera.getRealPosition().x() + (double) Main.camera.width / 2, Main.camera.getRealPosition().y() + (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
                Point minTotal = new Point(Main.camera.getRealPosition().x() - (double) Main.camera.width / 2, Main.camera.getRealPosition().y() - (Main.screenHeight * ((float) Main.getScreenWidthGame() / Main.screenWidth)) / 2);
                for (Drawable drawable : drawablesInLayer) {
                    Point minDrawable = drawable.getPosition().minus(drawable.getSize().width, drawable.getSize().height);
                    Point maxDrawable = drawable.getPosition().plus(drawable.getSize().width, drawable.getSize().height);
                    if ((maxDrawable.compareTo(minTotal) > 0 && minDrawable.compareTo(maxTotal) < 0)) {
                        drawable.draw(g);
                    }
                }
                drawMap(g);
                drawTooltip(g, timeDelta);
            }
            case Camera.LAYER_MENU -> {
                BufferedImage backgroundImage = Animation.MAIN_MENU_BACKGROUND_ANIMATION.getCurrentFrame(timeInGame);
                g.drawImage(backgroundImage, 0, 0, Main.screenWidth, Main.screenHeight, null);
                for (Drawable drawable : drawablesInLayer) {
                    drawable.draw(g);
                }
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
            case Camera.LAYER_SKILL_TREE -> {
                renderSkillTree(g);
                for (Drawable drawable : drawablesInLayer) {
                    drawable.draw(g);
                }
            }
        }
        
        drawXPBar(timeDelta, g);
        return outputImage;
    }
    
    private double displayedPercXPBar = 0;
    
    public double getOpacityXPBar(double timeSinceLastXPGained) {
        if (0 < timeSinceLastXPGained && timeSinceLastXPGained < XP_BAR_FADE_IN_OUT_TIME) {
            return timeSinceLastXPGained / XP_BAR_FADE_IN_OUT_TIME;
        }
        if (XP_BAR_FADE_IN_OUT_TIME < timeSinceLastXPGained && timeSinceLastXPGained < XP_BAR_FADE_IN_OUT_TIME + XP_BAR_SHOW_TIME) {
            return 1;
        }
        if (XP_BAR_FADE_IN_OUT_TIME + XP_BAR_SHOW_TIME < timeSinceLastXPGained && timeSinceLastXPGained < XP_BAR_SHOW_TIME + 2 * XP_BAR_FADE_IN_OUT_TIME) {
            return 1 - ((timeSinceLastXPGained - XP_BAR_SHOW_TIME - XP_BAR_FADE_IN_OUT_TIME) / XP_BAR_FADE_IN_OUT_TIME);
        }
        
        return 0;
    }
    
    
    public void drawXPBar(double timeDelta, Graphics g) {
        double opacity = getOpacityXPBar(timeSinceXpGained);
        if (timeSinceXpGained != -1 && opacity > 0) {
            int width = Main.screenWidth / 3;
            int height = (int) (20 * Main.scaledPixelSize());
    
            BufferedImage imageBar = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = imageBar.getGraphics();
            graphics.setColor(Color.GRAY);
            graphics.fillRoundRect(0, 0, width, height, (int) (5 * Main.scaledPixelSize()), (int) (5 * Main.scaledPixelSize()));
    
            double xp = Main.mainCharacter.xp;
            double maxXP = Main.mainCharacter.maxXp;
    
            double percentage = xp / maxXP;
    
            float widthBar = width - XP_BAR_PADDING_SIDE * 2;
            float heightBar = height - XP_BAR_PADDING_TOP * 2;
            
            if (displayedPercXPBar < percentage) {
                displayedPercXPBar = Math.min(displayedPercXPBar + timeDelta * XP_BAR_ANIMATION_SPEED, percentage);
            } else {
                displayedPercXPBar = Math.max(displayedPercXPBar - timeDelta * XP_BAR_ANIMATION_SPEED, percentage);
            }
    
            graphics.setColor(Color.WHITE);
            graphics.fillRect(XP_BAR_PADDING_SIDE, XP_BAR_PADDING_TOP, (int) (displayedPercXPBar * widthBar), (int) heightBar);
    
            BufferedImage imageDrawen = Util.getOpacityImage(imageBar, opacity);
    
            g.drawImage(imageDrawen, (int) ((Main.screenWidth - imageDrawen.getWidth()) / 2), (int) ((Main.screenHeight - imageDrawen.getHeight()) * 0.1), null);
        }
    }
    
    private void drawMapBackground(Graphics g) {
        Point minRendered = Main.camera.getRealPosition().minus(Main.camera.width * 2, Main.camera.getHeight() * 2);
        Point maxRendered = Main.camera.getRealPosition().plus(Main.camera.width * 2, Main.camera.getHeight() * 2);
        
        Point renderMin = minRendered.divide(Main.TILE_SIZE).divide(Main.BACKGROUND_TILE_SIZE).round();
        Point renderMax = maxRendered.divide(Main.TILE_SIZE).divide(Main.BACKGROUND_TILE_SIZE).round();
        
        if (renderMin.x() == renderMax.x()) {
            renderMax = renderMax.plus(1, 0);
        }
        if (renderMin.y() == renderMax.y()) {
            renderMax = renderMax.plus(0, 1);
        }
        
        // Unloading Images out of sight:

        for (int x = 0; x < renderMin.x() - 1; x++) {
            Map<Integer, String> images = Main.backgroundImages.getOrDefault(x, null);
    
            for (int y = 0; y < renderMin.y() - 1; y++) {
                Map<Integer, BufferedImage> mapOnX = mapBackgroundImages.getOrDefault(x, null);
                String path = images.getOrDefault(y, null);
                
                if (mapOnX != null) {
                    mapOnX.put(y, null);
                    mapBackgroundImages.put(x, mapOnX);
                    pathsLoaded.remove(path);
                }
            }
            for (int y = (int) (renderMax.y() + 1); y < minimap.size.height / Main.TILE_SIZE / Main.BACKGROUND_TILE_SIZE; y ++) {
                Map<Integer, BufferedImage> mapOnX = mapBackgroundImages.getOrDefault(x, null);
                String path = images.getOrDefault(y, null);
    
                if (mapOnX != null) {
                    mapOnX.put(y, null);
                    mapBackgroundImages.put(x, mapOnX);
                    pathsLoaded.remove(path);
                }
            }
        }

        for (int x = (int) (renderMax.x() + 1); x < minimap.size.width / Main.TILE_SIZE / Main.BACKGROUND_TILE_SIZE; x++) {
            Map<Integer, String> images = Main.backgroundImages.getOrDefault(x, null);
    
            for (int y = 0; y < renderMin.y() - 1; y++) {
                Map<Integer, BufferedImage> mapOnX = mapBackgroundImages.getOrDefault(x, null);
                String path = images.getOrDefault(y, null);
                
                if (mapOnX != null) {
                    mapOnX.put(y, null);
                    mapBackgroundImages.put(x, mapOnX);
                    pathsLoaded.remove(path);
                }
            }
            for (int y = (int) (renderMax.y() + 1); y < minimap.size.height / Main.TILE_SIZE / Main.BACKGROUND_TILE_SIZE; y ++) {
                Map<Integer, BufferedImage> mapOnX = mapBackgroundImages.getOrDefault(x, null);
                String path = images.getOrDefault(y, null);
    
                if (mapOnX != null) {
                    mapOnX.put(y, null);
                    mapBackgroundImages.put(x, mapOnX);
                    pathsLoaded.remove(path);
                }
            }
        }



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
                                g.setColor(Color.RED);
                                g.fillRect((int) positionOfImage.x(), (int) positionOfImage.y(), (int) (Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * Main.scaledPixelSize()), (int) (Main.TILE_SIZE * Main.BACKGROUND_TILE_SIZE * Main.scaledPixelSize()));
                                if (!pathsLoaded.contains(path)) {
                                    pathsLoaded.add(path);
                                    LazyBackgroundImageLoader loader = new LazyBackgroundImageLoader(path, x, y, g);
                                    Thread thread = new Thread(loader);
                                    thread.start();
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
    
    private void renderSkillTree(Graphics graphics) {
        SkillTree skillTree = Main.mainCharacter.skillTree;
        double scale = Math.min((double) Main.screenWidth / (double) SKILL_TREE_BACKGROUND.getWidth(), (double) Main.screenHeight / (double) SKILL_TREE_BACKGROUND.getHeight());
        
        int width = (int) (SKILL_TREE_BACKGROUND.getWidth() * scale);
        int height = (int) (SKILL_TREE_BACKGROUND.getHeight() * scale);
        
        graphics.drawImage(SKILL_TREE_BACKGROUND, 0, 0, (int) (SKILL_TREE_BACKGROUND.getWidth() * scale), (int) (SKILL_TREE_BACKGROUND.getHeight() * scale), null);
        
        List<SkillTree.Node> skills;
        if (nodeFocused == null) {
            skills = skillTree.startNodes;
        } else {
            skills = Arrays.asList(nodeFocused.nextNodes);
            graphics.drawImage(BACK_BUTTON, (int) (12.0 * Main.scaledPixelSize()), (int) (15.0 * Main.scaledPixelSize()), (int) (BACK_BUTTON.getWidth() / 100.0 * 4.0 * Main.scaledPixelSize() * BACK_BUTTON_SCALE), (int) (BACK_BUTTON.getHeight() / 100.0 * 4.0 * Main.scaledPixelSize() * BACK_BUTTON_SCALE), null);
        }
        
        for (SkillTree.Node skill : skills) {
            BufferedImage imageInside = skill.image;
            BufferedImage icon = SKILL_TREE_ICONS.get(skill.getLevel());
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
        
        String text = "Skill Points: " + Main.mainCharacter.spareLevel;
        int widthSpareLevels = graphics.getFontMetrics().stringWidth(text);
        graphics.drawString(text, (int) (Main.screenWidth * 0.95 - (widthSpareLevels)), (int) (Main.screenHeight * 0.1));
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
    
    public void handleMouseInputs(PointerInfo pointerInfo, List<Integer> mouseInputs, double timeDelta, Graphics graphics) {
        Point pointerPosition = new Point(pointerInfo.getLocation().x, pointerInfo.getLocation().y);
        for (Clickable clickable : getClickables()) {
            Hitbox shiftedHitbox = clickable.getHitbox().shift(clickable.position);
            if (pointerPosition.x() > shiftedHitbox.minX() && pointerPosition.x() < shiftedHitbox.maxX() && pointerPosition.y() > shiftedHitbox.minY() && pointerPosition.y() < shiftedHitbox.maxY()) {
                clickable.onHover(timeDelta);
                if (mouseInputs.contains(3)) {
                    clickable.onRightClick(timeDelta);
                }
                
                if (mouseInputs.contains(1)) {
                    clickable.onLeftClick(timeDelta);
                } else {
                    clickable.onOnlyHover(timeDelta);
                }
            } else {
                clickable.onReset(timeDelta);
            }
        }
    }
    
    public void xpGained() {
        timeSinceXpGained = 0;
    }
}
