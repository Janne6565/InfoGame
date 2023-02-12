package lethalhabit.world;

import lethalhabit.math.Point;

import java.util.ArrayList;

public final class Tile {
    
    public static final Tile EMPTY = new Tile(-1, -1, -1, new int[0], new double[0], new double[0]);
    
    public final int block;
    public final int liquid;
    public final int entity;
    public final int[] interactables;
    public final Point[] interactablePosition;
    
    public Tile(int block, int liquid, int entity, int[] interactables, double[] xPositionInteratables, double[] yPositionInteractables) {
        this.block = block;
        this.liquid = liquid;
        this.entity = entity;
        this.interactables = interactables;
        this.interactablePosition = new Point[interactables.length];
        for (int i = 0; i < interactables.length; i ++) {
            this.interactablePosition[i] = new Point(xPositionInteratables[i], yPositionInteractables[i]);
        }
    }

    public Tile(int block, int liquid, int entity, int[] interactables, Point[] interactablePosition) {
        this.block = block;
        this.liquid = liquid;
        this.entity = entity;
        this.interactables = interactables;
        this.interactablePosition = interactablePosition;
    }
    
    public Tile(Tile other) {
        this(other.block, other.liquid, other.entity, other.interactables, other.interactablePosition);
    }
}
