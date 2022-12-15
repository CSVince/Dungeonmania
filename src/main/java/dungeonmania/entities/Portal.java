package dungeonmania.entities;

import dungeonmania.util.Position;

public abstract class Portal extends PositionalEntity implements Overlappable {

    public Portal(Position position) {
        super(position);
    }
}
