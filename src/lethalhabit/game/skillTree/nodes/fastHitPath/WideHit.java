package lethalhabit.game.skillTree.nodes.fastHitPath;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;
import lethalhabit.util.Skills;

public class WideHit extends SkillTreeNode {
    
    public WideHit(Skills skills) {
        super("Strong Blow", null, new Point(0, 0), false, 2, skills.attackRange);
    }
    
}
