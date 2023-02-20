package lethalhabit.game.skillTree.nodes;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.game.skillTree.nodes.fastHitPath.WideHit;
import lethalhabit.math.Point;
import lethalhabit.util.Skills;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FastHit extends SkillTreeNode {
    
    public FastHit(Skills skills) {
        super("Super Shred", null, new Point(-0.71, 0.71), false, 2, skills.attackSpeed, new WideHit(skills));
    }
    
}
