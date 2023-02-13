package lethalhabit.util;

public final class PlayerSkills {
    
    /**
     * 0: Player can not double Jump
     * 1: Player can double Jump
     */
    public int doubleJump = 1;
    
    /**
     * 0: Player can not Wall Jump
     * 1: Player can wall jump once until he has touched the ground
     * 2: Player can wall jump on each side one time
     */
    public int wallJump = 1;
    
    /**
     * 0: Player can not Sprint
     * 1: Player can Sprint
     * 2: Player can sprint faster
     */
    public int sprint;
    
    /**
     * 0: Player can not Dash
     * 1: Player can Dash with 3s cooldown after he touched the ground
     * 2: Player can Dash with 3s cooldown
     * 3: Player can Dash with 1s cooldown
     */
    public int dash = 1;
    
    
    /**
     * Requires: dash >= 1
     * 0: Player dashes normally
     * 1: Player is while Dash now invulnerable and will Damage all Enemies touched while dashing (10s cooldown)
     * 2: Player is while Dash now invulnerable and will Damage all Enemies touched while dashing (5s cooldown)
     * 3: Player is while Dash now invulnerable and will Damage all Enemies touched while dashing (3s cooldown)
     */
    public int rush;
    
    /**
     * 0: Player can not Swim and only flupp in Liquids
     * 1: Player can swim in Liquids
     * 2: Player can swim in Liquids faster
     */
    public int swim = 1;
    
    /**
     * 0: Player cant sneak
     * 1: Player can sneak for 2 Seconds -> decreased Movement (default: 1/4) Speed and decreased detect Range from enemies (20s cooldown)
     * 2: Player can sneak for 5 seconds -> decreased Movement Speed and decreased detect Range from enemies (7s cooldown)
     * 3: Player can sneak for undefined seconds -> decreased Movement Speed and decreased detect Range from enemies (5s cooldown)
     */
    public int sneak;
    
    /**
     * Requires: sneak >= 1
     * 0: Player Sneaks normally
     * 1: Player Sneaks faster (1/3 of normal Movement speed)
     * 2: Player Sneaks more faster (1/2 of normal Movement speed)
     */
    public int swiftSneak;
    
    /**
     * Requires: sneak >= 1
     * 0: Player Sneaks normally
     * 1: decreased detection Range
     * 2: decreased detection Range
     */
    public int silence;
    public int invisibility;
    
    public int scales;
    public int poise;
    public int stomp;
    public int regeneration;
    public int thorns;
    public int slamRegeneration;
    public int fireResistance;
    public int projectileProtection;
    public int respiration;
    
}
