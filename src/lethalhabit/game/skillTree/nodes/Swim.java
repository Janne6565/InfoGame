package lethalhabit.game.skillTree.nodes;

import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Point;
import lethalhabit.util.Skills;

public class Swim extends SkillTreeNode {
    
    public Swim(Skills skills) {
        super("Swishing Guitar", null, new Point(-0.71, -0.71), false, 2, skills.swim);
    }
    
}
