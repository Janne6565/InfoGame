package lethalhabit.game.skillTree.nodes;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.game.skillTree.nodes.fastHitPath.WideHit;
import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FastHit extends SkillTreeNode {

    @Override
    public ArrayList<SkillTreeNode> followingNodes() {
        return new ArrayList<>(List.of(new WideHit[]{
            new WideHit(),
        }));
    }

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
        return new Point(-0.71, 0.71);
    }

    @Override
    public String name() {
        return "Super Shred";
    }

    @Override
    public void onSkill(int level) {
        super.onSkill(level);
        switch (level) {
            case 1 -> {
                Main.mainCharacter.ATTACK_COOLDOWN = 0.7;
            }
            case 2 -> {
                Main.mainCharacter.ATTACK_COOLDOWN = 0.5;
            }
        }
    }
}
