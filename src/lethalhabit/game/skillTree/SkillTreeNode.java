package lethalhabit.game.skillTree;

import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class SkillTreeNode {

    public int level = 0;

    public float scale = 1;

    /**
     * Maximum 3 Nodes else we have some graphic problems :)
     */
    public ArrayList<SkillTreeNode> followingNodes;
    public int maxLevel;
    public boolean isUltimate;
    public BufferedImage image;
    public Point position;
    public String name;

    public SkillTreeNode(String name, BufferedImage image, Point position, boolean isUltimate, int maxLevel, ArrayList<SkillTreeNode> followingNodes) {
        this.name = name;
        this.image = image;
        this.position = position;
        this.isUltimate = isUltimate;
        this.maxLevel = maxLevel;
        this.followingNodes = followingNodes;
    }

    public void onSkill(int level) {
        this.level = level;
    };
}
