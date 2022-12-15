package dungeonmania.entities;

import dungeonmania.Game;

public interface BreakableItem {
    public void use(Game game);
    public int getDurability();
    public void setDurability(int durability);
}
