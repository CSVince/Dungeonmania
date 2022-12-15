package dungeonmania.entities;

import dungeonmania.map.GameMap;

public interface Overlappable {

    public abstract void onOverlap(GameMap map, PositionalEntity entity);

}
