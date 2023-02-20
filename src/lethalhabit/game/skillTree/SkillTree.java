package lethalhabit.game.skillTree;

import lethalhabit.game.skillTree.nodes.*;
import lethalhabit.util.Skills;

import java.util.ArrayList;
import java.util.List;

public class SkillTree {
    
    public final Skills skills;
    public final List<SkillTreeNode> startNodes;
    
    public SkillTree(Skills skills) {
        this.skills = skills;
        this.startNodes = List.of(
                new Dash(skills),
                new DoubleJump(skills),
                new FastHit(skills),
                new Fireball(skills),
                new Swim(skills)
        );
    }
    
    
}
