package lethalhabit.game.skillTree;

import lethalhabit.game.skillTree.nodes.*;

import java.util.ArrayList;
import java.util.List;

public class SkillTree {

    public ArrayList<SkillTreeNode> startNodes = new ArrayList<>(List.of(new SkillTreeNode[]{
            new Dash(),
            new DoubleJump(),
            new FastHit(),
            new Fireball(),
            new Swim(),
    }));
}
