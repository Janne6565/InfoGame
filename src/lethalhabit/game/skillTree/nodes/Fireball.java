package lethalhabit.game.skillTree.nodes;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Point;
import lethalhabit.util.Skills;

public class Fireball extends SkillTreeNode {
    
    public Fireball(Skills skills) {
        super("Breath of the Kings", null, new Point(0.71, -0.71), false, 2, skills.fireball);
    }
    
}
