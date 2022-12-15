package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;

public class BaseState extends PlayerState {
    public BaseState(Player player) {
        super(player);
    }

    public void triggerNext(int currentTick) {
        if (!isPotionAvailable()) {
            return;
        }

        getPlayer().setInEffective(getPlayer().getQueue().remove());
        if (getPlayer().getInEffective() instanceof InvincibilityPotion) {
            getPlayer().changeState(getPlayer().getInvincibleState());
        } else {
            getPlayer().changeState(getPlayer().getInvisibleState());
        }
        getPlayer().setNextTrigger(currentTick);
    }

    public BattleStatistics applyBuff(BattleStatistics origin) {
        return origin;
    }
}
