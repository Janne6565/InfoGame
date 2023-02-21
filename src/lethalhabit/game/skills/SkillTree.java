package lethalhabit.game.skills;

import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.List;

public class SkillTree {
    
    public final Skills skills;
    public final List<Node> startNodes;
    
    public SkillTree(Skills skills) {
        this.skills = skills;
        this.startNodes = List.of(
            new DashNode(),
            new DoubleJumpNode(),
            new FastHitNode(),
            new FireballNode(),
            new SwimNode()
        );
    }
    
    public static abstract class Node {
        
        private int level = 0;
        
        public double scale = 1;
        
        public final String name;
        public final BufferedImage image;
        public final Point position;
        public final boolean ultimate;
        public final int maxLevel;
        
        /**
         * Maximum 3 Nodes else we have some graphic problems :)
         */
        public final Node[] nextNodes;
        
        protected Node(String name, BufferedImage image, Point position, boolean ultimate, int maxLevel, Node... nextNodes) {
            this.name = name;
            this.image = image;
            this.position = position;
            this.ultimate = ultimate;
            this.maxLevel = maxLevel;
            this.nextNodes = nextNodes;
        }
        
        public int getLevel() {
            return level;
        }
        
        public void upgrade() {
            if (level < maxLevel) {
                onLevel(++level);
            }
        }
        
        protected abstract void onLevel(int level);
        
    }
    
    public final class DashNode extends Node {
        
        public DashNode() {
            super("Schwuuup", null, new Point(0, 0), true, 2);
        }
        
        protected void onLevel(int level) {
            switch (level) {
                case 1 -> {
                    skills.dashAmount = 1;
                }
                case 2 -> {
                    skills.dashCooldown = 3;
                }
                case 3 -> {
                    skills.dashAmount = 2;
                }
            }
        }
        
    }
    
    public final class DoubleJumpNode extends Node {
        
        public DoubleJumpNode() {
            super("Fluuup", null, new Point(0.71, 0.71), true, 3, new WallJumpNode());
        }
        
        protected void onLevel(int level) {
            switch (level) {
                case 1 -> {
                    skills.doubleJumpAmount = 1;
                }
                case 2 -> {
                    skills.doubleJumpCooldown = 1;
                }
                case 3 -> {
                    skills.doubleJumpCooldown = 0;
                    skills.doubleJumpAmount = 2;
                }
            }
        }
        
    }
    
    public final class WallJumpNode extends Node {
        
        public WallJumpNode() {
            super("Guitar Bounce", null, new Point(0, 0), false, 2);
        }
        
        protected void onLevel(int level) {
            switch (level) {
                case 1 -> {
                    skills.canWallJump = true;
                }
                case 2 -> {
                    skills.canWallJumpBothSides = true;
                }
            }
        }
        
    }
    
    public final class FastHitNode extends Node {
        
        public FastHitNode() {
            super("Super Shred", null, new Point(-0.71, 0.71), false, 2, new WideHitNode());
        }
        
        protected void onLevel(int level) {
            switch (level) {
                case 1 -> {
                    skills.attackCooldown = 0.7;
                }
                case 2 -> {
                    skills.attackCooldown = 0.5;
                }
            }
        }
        
    }
    
    public final class WideHitNode extends Node {
        
        public WideHitNode() {
            super("Strong Blow", null, new Point(0, 0), false, 2);
        }
        
        protected void onLevel(int level) {
            switch (level) {
                case 1 -> {
                    skills.attackHitbox = new Hitbox(
                        new Point(0, 5),
                        new Point(25, 5),
                        new Point(25, 25),
                        new Point(0, 25)
                    );
                }
                case 2 -> {
                    skills.attackHitbox = new Hitbox(
                        new Point(0, 5),
                        new Point(25, 5),
                        new Point(25, 35),
                        new Point(0, 35)
                    );
                }
            }
        }
        
    }
    
    public final class FireballNode extends Node {
        
        public FireballNode() {
            super("Breath of the Kings", null, new Point(0.71, -0.71), false, 2);
        }
        
        protected void onLevel(int level) {
            switch (level) {
                case 1 -> {
                    skills.canMakeFireball = true;
                }
                case 2 -> {
                    skills.fireballCooldown = 3;
                }
            }
        }
        
    }
    
    public final class SwimNode extends Node {
        
        public SwimNode() {
            super("Swishing Guitar", null, new Point(-0.71, -0.71), false, 2);
        }
        
        protected void onLevel(int level) {
        
        }
        
    }
    
}
