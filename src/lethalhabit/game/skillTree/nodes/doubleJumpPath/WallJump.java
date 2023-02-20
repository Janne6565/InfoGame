package lethalhabit.game.skillTree.nodes.doubleJumpPath;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Point;
import lethalhabit.util.Skills;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class WallJump extends SkillTreeNode {
    
    public WallJump(Skills skills) {
        super("Guitar Bounce", null, new Point(0, 0), false, 2, skills.wallJump);
    }
    
}
