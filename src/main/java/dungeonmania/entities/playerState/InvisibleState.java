package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;

public class InvisibleState extends PlayerState {
    public InvisibleState(Player player) {
        super(player);
    }

    public void triggerNext(int currentTick) {
        if (!isPotionAvailable()) {
            getPlayer().changeState(getPlayer().getBaseState());
            return;
        }

        getPlayer().setInEffective(getPlayer().getQueue().remove());
        if (getPlayer().getInEffective() instanceof InvincibilityPotion) {
            getPlayer().changeState(getPlayer().getInvincibleState());
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
            false,
            false));
    }
}
