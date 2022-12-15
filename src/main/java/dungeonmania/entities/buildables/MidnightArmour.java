package dungeonmania.entities.buildables;

// import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.BattleItem;

public class MidnightArmour extends Buildable implements BattleItem {

    public static final double DEFAULT_ATTACK = 1;
    public static final double DEFAULT_DEFENCE = 1;

    private double defence;
    private double attack;

    public MidnightArmour(double defence, double attack) {
        super();
        this.defence = defence;
        this.attack = attack;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            attack,
            defence,
            1,
            1));
    }


}
