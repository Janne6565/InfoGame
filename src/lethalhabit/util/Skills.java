package lethalhabit.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class Skills {
    
    /**
     * 0: Player can not double Jump
     * 1: Player can double Jump
     */
    public AtomicInteger doubleJump = new AtomicInteger(1);
    
    /**
     * 0: Player can not Wall Jump
     * 1: Player can wall jump once until he has touched the ground
     * 2: Player can wall jump on each side one time
     */
    public AtomicInteger wallJump = new AtomicInteger(1);
    
    /**
     * 0: Player can not Sprint
     * 1: Player can Sprint
     * 2: Player can sprint faster
     */
    public AtomicInteger sprint = new AtomicInteger();
    
    /**
     * 0: Player can not Dash
     * 1: Player can Dash with 3s cooldown after he touched the ground
     * 2: Player can Dash with 3s cooldown
     * 3: Player can Dash with 1s cooldown
     */
    public AtomicInteger dash = new AtomicInteger(1);
    
    /**
     * Requires: dash >= 1
     * 0: Player dashes normally
     * 1: Player is while Dash now invulnerable and will Damage all Enemies touched while dashing (10s cooldown)
     * 2: Player is while Dash now invulnerable and will Damage all Enemies touched while dashing (5s cooldown)
     * 3: Player is while Dash now invulnerable and will Damage all Enemies touched while dashing (3s cooldown)
     */
    public AtomicInteger rush = new AtomicInteger();
    
    /**
     * 0: Player can not Swim and only flupp in Liquids
     * 1: Player can swim in Liquids
     * 2: Player can swim in Liquids faster
     */
    public AtomicInteger swim = new AtomicInteger(1);
    
    /**
     * 0: Player cant sneak
     * 1: Player can sneak for 2 Seconds -> decreased Movement (default: 1/4) Speed and decreased detect Range from enemies (20s cooldown)
     * 2: Player can sneak for 5 seconds -> decreased Movement Speed and decreased detect Range from enemies (7s cooldown)
     * 3: Player can sneak for undefined seconds -> decreased Movement Speed and decreased detect Range from enemies (5s cooldown)
     */
    public AtomicInteger sneak = new AtomicInteger();
    
    /**
     * Requires: sneak >= 1
     * 0: Player Sneaks normally
     * 1: Player Sneaks faster (1/3 of normal Movement speed)
     * 2: Player Sneaks more faster (1/2 of normal Movement speed)
     */
    public AtomicInteger swiftSneak = new AtomicInteger();
    
    /**
     * Requires: sneak >= 1
     * 0: Player Sneaks normally
     * 1: decreased detection Range
     * 2: decreased detection Range
     */
    public AtomicInteger silence = new AtomicInteger();
    public AtomicInteger invisibility = new AtomicInteger();
    
    public AtomicInteger scales = new AtomicInteger();
    public AtomicInteger poise = new AtomicInteger();
    public AtomicInteger stomp = new AtomicInteger();
    public AtomicInteger regeneration = new AtomicInteger();
    public AtomicInteger thorns = new AtomicInteger();
    public AtomicInteger slamRegeneration = new AtomicInteger();
    public AtomicInteger fireResistance = new AtomicInteger();
    public AtomicInteger projectileProtection = new AtomicInteger();
    public AtomicInteger respiration = new AtomicInteger();
    public AtomicInteger fireball = new AtomicInteger();
    public AtomicInteger attackSpeed = new AtomicInteger();
    public AtomicInteger attackRange = new AtomicInteger();
    
}
