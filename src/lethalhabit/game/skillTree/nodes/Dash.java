package lethalhabit.game.skillTree.nodes;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Dash extends SkillTreeNode {

    public Dash() {
        super("Schwuuup", null, new Point(0, 0), true,2, null);
    }

    @Override
    public void onSkill(int level) {
        super.onSkill(level);
        switch (level) {
            case 1 -> {
                Main.mainCharacter.DASH_AMOUNTS = 1;
            }
            case 2 -> {
                Main.mainCharacter.DASH_COOLDOWN = 3;
            }
            case 3 -> {
                Main.mainCharacter.DASH_AMOUNTS = 2;
            }
        }
    }
}
