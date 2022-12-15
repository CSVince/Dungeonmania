package dungeonmania.entities.buildables;

public class Sceptre extends Buildable {

    public static final int DEFAULT_DURATION = 1;
    private int duration;

    public Sceptre(int duration) {
        super();
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
