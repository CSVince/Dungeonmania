package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;

public class InvincibleState extends PlayerState {
    public InvincibleState(Player player) {
        super(player);
    }

    public void triggerNext(int currentTick) {
        if (!isPotionAvailable()) {
            getPlayer().changeState(getPlayer().getBaseState());
            return;
        }

        getPlayer().setInEffective(getPlayer().getQueue().remove());
        if (getPlayer().getInEffective() instanceof InvisibilityPotion) {
            getPlayer().changeState(getPlayer().getInvisibleState());
        }
        getPlayer().setNextTrigger(currentTick);
    }

    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            0,
            0,
            1,
            1,
            true,
            true));
    }
}
