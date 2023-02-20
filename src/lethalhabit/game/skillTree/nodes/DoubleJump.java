package lethalhabit.game.skillTree.nodes;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.game.skillTree.nodes.doubleJumpPath.WallJump;
import lethalhabit.math.Point;
import lethalhabit.util.Skills;

import java.util.ArrayList;
import java.util.List;

public class DoubleJump extends SkillTreeNode {
    
    public DoubleJump(Skills skills) {
        super("Fluuup", null, new Point(0.71, 0.71), true, 3, skills.doubleJump, new WallJump(skills));
    }
    
}
