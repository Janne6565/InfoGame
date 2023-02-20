package lethalhabit.game.skillTree.nodes.doubleJumpPath;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class WallJump extends SkillTreeNode {

    public WallJump() {
        super("Guitar Bounce", null, new Point(0, 0), false,2, null);
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
