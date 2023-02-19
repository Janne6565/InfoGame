package lethalhabit.game.skillTree.nodes.doubleJumpPath;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class WallJump extends SkillTreeNode {
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
        return false;
    }

    @Override
    public BufferedImage image() {
        return null;
    }

    @Override
    public Point position() {
        return new Point(0, 0);
    }

    @Override
    public String name() {
        return "Guitar Bounce";
    }

    @Override
    public void onSkill(int level) {
        super.onSkill(level);
        switch (level) {
            case 1 -> {
                Main.mainCharacter.CAN_WALL_JUMP = true;
            }
            case 2 -> {
                Main.mainCharacter.CAN_WALL_JUMP_ONCE_PER_SIDE = true;
            }
        }
    }
}
