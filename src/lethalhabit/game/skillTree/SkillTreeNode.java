package lethalhabit.game.skillTree;

import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class SkillTreeNode {

    /**
     * Maximum 3 Nodes else we have some graphic problems :)
     */
    public abstract ArrayList<SkillTreeNode> followingNodes();
    public abstract int maxLevel();
    public abstract boolean isUltimate();
    public abstract BufferedImage image();
    public abstract Point position();
    public abstract String name();

    public SkillTreeNode() {

    }

    public abstract void onSkill(int level);

}
