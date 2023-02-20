package lethalhabit.game.skillTree.nodes;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Swim extends SkillTreeNode {

    public Swim() {
        super("Swishing Guitar", null, new Point(-0.71, -0.71), false,2, null);
    }

    @Override
    public void onSkill(int level) {
        super.onSkill(level);
        switch (level) {
            case 1 -> {

            }
            case 2 -> {

            }
        }
    }
}
