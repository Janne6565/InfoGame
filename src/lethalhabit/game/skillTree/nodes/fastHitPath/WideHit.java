package lethalhabit.game.skillTree.nodes.fastHitPath;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class WideHit extends SkillTreeNode {
    @Override
    public ArrayList<SkillTreeNode> followingNodes() {
        return null;
    }

    @Override
    public int maxLevel() {
        return 2;
    }

    @Override
    public boolean isUltimate() {
        return false;
    }

    @Override
    public BufferedImage image() {
        return null;
    }

    @Override
    public Point position() {
        return new Point(0, 0);
    }

    @Override
    public String name() {
        return "Strong Blow";
    }

    @Override
    public void onSkill(int level) {
        switch (level) {
            case 1 -> {
                Main.mainCharacter.HIT_HITBOX = new Hitbox(
                        new Point(0, 5),
                        new Point(25, 5),
                        new Point(25, 25),
                        new Point(0, 25)
                );
            }
            case 2 -> {
                Main.mainCharacter.HIT_HITBOX = new Hitbox(
                        new Point(0, 5),
                        new Point(25, 5),
                        new Point(25, 35),
                        new Point(0, 35)
                );
            }
        }
    }
}
