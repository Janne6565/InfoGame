package lethalhabit.ui;

import lethalhabit.Main;
import lethalhabit.game.skillTree.SkillTreeNode;
import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

public class UpgradeButtonSkilltree extends Clickable {
    public SkillTreeNode node;
    public static final double HOLD_THRESHHOLD = 1.5;
    public double timeHeld = 0;

    public UpgradeButtonSkilltree(Point position, Hitbox hitbox, SkillTreeNode node) {
        super(position, hitbox);
        this.node = node;
    }

    @Override
    public void onClick(double timeDelta) {
        timeHeld += timeDelta;
        if (timeHeld >= HOLD_THRESHHOLD && Main.mainCharacter.spareLevel >= 1 && node.level < node.maxLevel()) {
            timeHeld = 0;
            node.onSkill(node.level + 1);
            Main.mainCharacter.spareLevel -= 1;
        }
    }

    @Override
    public void onHover(double timeDelta) {
        node.scale = (float) Math.min(node.scale + timeDelta * 0.3, 1.1);
    }

    @Override
    public void onReset(double timeDelta) {
        timeHeld = 0;
        node.scale = (float) Math.max(node.scale - timeDelta * 0.3, 1);
    }

    @Override
    public void onRightClick(double timeDelta) {
        if (node.followingNodes != null) {
            Main.GAME_PANEL.clearClickables();
            Main.GAME_PANEL.nodeFocused = node;
            Main.GAME_PANEL.loadClickables();
            System.out.println(Main.GAME_PANEL.clickables.get(0).hitbox.shift(Main.GAME_PANEL.clickables.get(0).position));
        }
    }

    @Override
    public void onOnlyHover(double timeDelta) {
        timeHeld = 0;
    }
}
