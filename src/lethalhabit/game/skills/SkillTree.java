package lethalhabit.game.skills;

import lethalhabit.math.Hitbox;
import lethalhabit.math.Point;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * A class to control the backend and frontend of a skill tree
 */
public class SkillTree {
    
    /**
     * The particular instance of skills that are controlled by this skill tree
     */
    public final Skills skills;
    
    /**
     * Root nodes of the skill tree (list of nodes on the first level)
     */
    public final List<Node> startNodes;
    
    /**
     * Constructs a new skill tree connected to a certain set of skills
     *
     * @param skills Skill set to control
     */
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
    
    /**
     * Skill tree node to control graphical and logical behavior of specific skills
     */
    public static abstract class Node {
        
        /**
         * Current level of this skill
         */
        private int level = 0;
        
        /**
         * Size of the node
         */
        public double scale = 1;
        
        /**
         * Name of this skill
         */
        public final String name;
        
        /**
         * Icon of this skill
         */
        public final BufferedImage image;
        
        /**
         * Relative position in the skill tree menu
         */
        public final Point position;
        
        /**
         * <code>true</code> if the skill is an ultimate ability, <code>false</code> otherwise
         */
        public final boolean ultimate;
        
        /**
         * Maximum level of this skill
         */
        public final int maxLevel;
        
        /**
         * Child nodes
         */
        public final Node[] nextNodes;
        
        /**
         * Time the node has been hovered on
         */
        public double hoverTime;
        
        /**
         * Constructs a new skill tree node
         *
         * @param name      Skill name
         * @param image     Skill icon
         * @param position  Relative position in the skill tree menu
         * @param ultimate  Skill is ultimate
         * @param maxLevel  Maximum skill level
         * @param nextNodes Child nodes
         */
        protected Node(String name, BufferedImage image, Point position, boolean ultimate, int maxLevel, Node... nextNodes) {
            this.name = name;
            this.image = image;
            this.position = position;
            this.ultimate = ultimate;
            this.maxLevel = maxLevel;
            this.nextNodes = nextNodes;
            this.hoverTime = 0;
        }
        
        /**
         * @return Current level of this skill in the skill tree
         */
        public int getLevel() {
            return level;
        }
        
        /**
         * Upgrades this skill node, increasing the level and updating the skills controlled by the skill tree
         */
        public void upgrade() {
            if (level < maxLevel) {
                onLevel(++level);
            }
        }
        
        /**
         * Updates the skills controlled by the skill tree, according to node and level
         *
         * @param level New level to upgrade to
         */
        protected abstract void onLevel(int level);
        
    }
    
    /**
     * Skill tree node representing the ability to dash <br>
     * - Level 0: No dash <br>
     * - Level 1: One dash, 5 s cooldown <br>
     * - Level 2: One dash, 3 s cooldown <br>
     * - Level 3: Two dashes, 3 s cooldown
     */
    public final class DashNode extends Node {
        
        /**
         * Constructs a new dash skill tree node (display name "Schwuuup")
         */
        public DashNode() {
            super("Schwuuup", null, new Point(0, 0), true, 3);
        }
        
        /**
         * Updates the skills controlled by the node and its skill tree, specifically <code>dashAmount</code> and <code>dashCooldown</code>
         *
         * @param level New level to upgrade to
         * @see Skills#dashAmount
         * @see Skills#dashCooldown
         */
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
    
    /**
     * Skill tree node representing the ability to double jump <br>
     * - Level 0: No double jump <br>
     * - Level 1: One double jump, 3 s cooldown <br>
     * - Level 2: One double jump, 1 s cooldown <br>
     * - Level 3: Two double jumps, no cooldown
     */
    public final class DoubleJumpNode extends Node {
        
        /**
         * Constructs a new double jump skill tree node (display name "Fluuup")
         */
        public DoubleJumpNode() {
            super("Fluuup", null, new Point(0.71, 0.71), true, 3, new WallJumpNode());
        }
        
        /**
         * Updates the skills controlled by the node and its skill tree, specifically <code>doubleJumpAmount</code> and <code>doubleJumpCooldown</code>
         *
         * @param level New level to upgrade to
         * @see Skills#doubleJumpAmount
         * @see Skills#doubleJumpCooldown
         */
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
    
    /**
     * Skill tree node representing the ability to wall jump <br>
     * - Level 0: No wall jump <br>
     * - Level 1: One wall jump <br>
     * - Level 2: Two wall jumps (one per side)
     */
    public final class WallJumpNode extends Node {
        
        /**
         * Constructs a new wall jump skill tree node (display name "Guitar Bounce")
         */
        public WallJumpNode() {
            super("Guitar Bounce", null, new Point(0, 0), false, 2);
        }
        
        /**
         * Updates the skills controlled by the node and its skill tree, specifically <code>canWallJump</code> and <code>canWallJumpBothSides</code>
         *
         * @param level New level to upgrade to
         * @see Skills#canWallJump
         * @see Skills#canWallJumpBothSides
         */
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
    
    /**
     * Skill tree node representing attack speed <br>
     * - Level 0: 0.8 s attack cooldown <br>
     * - Level 1: 0.7 s attack cooldown <br>
     * - Level 2: 0.5 s attack cooldown
     */
    public final class FastHitNode extends Node {
        
        /**
         * Constructs a new attack speed skill tree node (display name "Super Shred")
         */
        public FastHitNode() {
            super("Super Shred", null, new Point(-0.71, 0.71), false, 2, new WideHitNode());
        }
        
        /**
         * Updates the skills controlled by the node and its skill tree, specifically <code>attackCooldown</code>
         *
         * @param level New level to upgrade to
         * @see Skills#attackCooldown
         */
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
    
    /**
     * Skill tree node representing attack range <br>
     * - Level 0: Standard attack range (15x20) <br>
     * - Level 1: Medium attack range (25x20) <br>
     * - Level 2: Large attack range (25x30)
     */
    public final class WideHitNode extends Node {
        
        /**
         * Constructs a new attack range skill tree node (display name "Strong Blow")
         */
        public WideHitNode() {
            super("Strong Blow", null, new Point(0, 0), false, 2);
        }
        
        /**
         * Updates the skills controlled by the node and its skill tree, specifically <code>attackHitbox</code>
         *
         * @param level New level to upgrade to
         * @see Skills#attackHitbox
         */
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
    
    /**
     * Skill tree node representing the ability to shoot fireballs <br>
     * - Level 0: No fireballs <br>
     * - Level 1: Can shoot fireballs, 5 s cooldown <br>
     * - Level 2: Can shoot fireballs, 3 s cooldown
     */
    public final class FireballNode extends Node {
        
        /**
         * Constructs a new fireball skill tree node (display name "Breath of the Kings")
         */
        public FireballNode() {
            super("Breath of the Kings", null, new Point(0.71, -0.71), false, 2);
        }
        
        /**
         * Updates the skills controlled by the node and its skill tree, specifically <code>canMakeFireball</code> and <code>fireballCooldown</code>
         *
         * @param level New level to upgrade to
         * @see Skills#canMakeFireball
         * @see Skills#fireballCooldown
         */
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
    
    /**
     * Skill tree node representing the ability to swim (NOT YET IMPLEMENTED)
     */
    public final class SwimNode extends Node {
        
        /**
         * Constructs a new swim skill tree node (display name "Swishing Guitar")
         */
        public SwimNode() {
            super("Swishing Guitar", null, new Point(-0.71, -0.71), false, 2);
        }
        
        protected void onLevel(int level) {
        
        }
        
    }
    
}
