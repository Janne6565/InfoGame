package lethalhabit.game.skillTree.nodes;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Point;
import lethalhabit.util.Skills;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Dash extends SkillTreeNode {
    
    public Dash(Skills skills) {
        super("Schwuuup", null, new Point(0, 0), true, 2, skills.dash);
    }
    
}
