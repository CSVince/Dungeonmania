package dungeonmania.entities.collectables;

import dungeonmania.util.Position;

public class Key extends CollectableEntity {
    private int number;

    public Key(Position position, int number) {
        super(position);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

}
