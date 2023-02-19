package lethalhabit.game.skillTree.nodes;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Swim extends SkillTreeNode {

    @Override
    public ArrayList<SkillTreeNode> followingNodes() {
        return null;
    }

    @Override
    public int maxLevel() {
        return 2;
    }

    @Override
    public boolean isUltimate() {
        return true;
    }

    @Override
    public BufferedImage image() {
        return null;
    }

    @Override
    public Point position() {
        return new Point(-0.71, -0.71);
    }

    @Override
    public String name() {
        return "Swishing Guitar";
    }

    @Override
    public void onSkill(int level) {
        super.onSkill(level);
        switch (level) {
            case 1 -> {

            }
            case 2 -> {

            }
        }
    }
}
