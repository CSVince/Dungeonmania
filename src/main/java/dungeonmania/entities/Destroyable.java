package dungeonmania.entities;

import dungeonmania.map.GameMap;

public interface Destroyable {

    public abstract void onDestroy(GameMap gameMap);

}
