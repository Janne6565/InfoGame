package lethalhabit.game.skillTree.nodes.fastHitPath;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

public class WideHit extends SkillTreeNode {
    public WideHit() {
        super("Strong Blow", null, new Point(0, 0), false, 2, null);
    }

    @Override
    public void onSkill(int level) {
        super.onSkill(level);
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
