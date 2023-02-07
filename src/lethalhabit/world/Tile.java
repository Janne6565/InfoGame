package lethalhabit.world;

public final class Tile {
    
    public static final Tile EMPTY = new Tile(-1, -1, -1, new int[0]);
    
    public final int block;
    public final int liquid;
    public final int entity;
    public final int[] interactables;
    
    public Tile(int block, int liquid, int entity, int[] interactables) {
        this.block = block;
        this.liquid = liquid;
        this.entity = entity;
        this.interactables = interactables;
    }
    
    public Tile(Tile other) {
        this(other.block, other.liquid, other.entity, other.interactables);
    }
    
}
