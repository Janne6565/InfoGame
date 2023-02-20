package lethalhabit.game.skillTree;

import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SkillTreeNode {
    
    public float scale = 1;
    
    public final String name;
    public final BufferedImage image;
    public final Point position;
    public final boolean ultimate;
    public final int maxLevel;
    
    private final AtomicInteger skill;
    
    /**
     * Maximum 3 Nodes else we have some graphic problems :)
     */
    public final SkillTreeNode[] nextNodes;
    
    public SkillTreeNode(String name, BufferedImage image, Point position, boolean ultimate, int maxLevel, AtomicInteger skill, SkillTreeNode... nextNodes) {
        this.name = name;
        this.image = image;
        this.position = position;
        this.ultimate = ultimate;
        this.maxLevel = maxLevel;
        this.skill = skill;
        this.nextNodes = nextNodes;
    }
    
    public final int getLevel() {
        return skill.get();
    }
    
    public void upgrade() {
        if (skill.get() < maxLevel) {
            skill.incrementAndGet();
        }
    }
    
}
