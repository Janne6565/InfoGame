package lethalhabit.game.skillTree.nodes;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Point;

public class Fireball extends SkillTreeNode {

    public Fireball() {
        super("Breath of the Kings", null, new Point(0.71, -0.71), false,2, null);
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
