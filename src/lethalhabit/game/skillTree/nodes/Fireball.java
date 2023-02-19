package lethalhabit.game.skillTree.nodes;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Fireball extends SkillTreeNode {

    @Override
    public int maxLevel() {
        return 2;
    }

    @Override
    public boolean isUltimate() {
        return true;
    }

    @Override
    public BufferedImage image() {
        return null;
    }

    @Override
    public Point position() {
        return new Point(0.71, -0.71);
    }

    @Override
    public String name() {
        return "Breath of the Kings";
    }


    @Override
    public void onSkill(int level) {
        super.onSkill(level);
        switch (level) {
            case 1 -> {
                Main.mainCharacter.CAN_MAKE_FIREBALL = true;
            }
            case 2 -> {
                Main.mainCharacter.FIREBALL_COOLDOWN = 3;
            }
        }

    }
}
