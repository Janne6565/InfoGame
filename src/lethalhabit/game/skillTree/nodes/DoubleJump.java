package lethalhabit.game.skillTree.nodes;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.game.skillTree.nodes.doubleJumpPath.WallJump;
import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DoubleJump extends SkillTreeNode {

    @Override
    public ArrayList<SkillTreeNode> followingNodes() {
        return new ArrayList<>(List.of(new WallJump[]{
            new WallJump(),
        }));
    }

    @Override
    public int maxLevel() {
        return 3;
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
        return new Point(0.71, 0.71);
    }

    @Override
    public String name() {
        return "Fluup";
    }

    @Override
    public void onSkill(int level) {
        switch (level) {
            case 1 -> {
                Main.mainCharacter.DOUBLE_JUMP_AMOUNT = 1;
            }
            case 2 -> {
                Main.mainCharacter.DOUBLE_JUMP_COOLDOWN = 1;
            }
            case 3 -> {
                Main.mainCharacter.DOUBLE_JUMP_COOLDOWN = 0;
                Main.mainCharacter.DOUBLE_JUMP_AMOUNT = 2;
            }
        }
    }
}
