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
    public ArrayList<SkillTreeNode> followingNodes = null;
    public abstract int maxLevel();
    public abstract boolean isUltimate();
    public abstract BufferedImage image();
    public abstract Point position();
    public abstract String name();

    public SkillTreeNode() {

    }

    public void onSkill(int level) {
        this.level = level;
    };

}
