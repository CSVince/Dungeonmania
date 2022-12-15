package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Player;

public abstract class PlayerState {
    private Player player;

    public PlayerState(Player player) {
        this.player = player;
    }

    public abstract void triggerNext(int currentTick);
    public abstract BattleStatistics applyBuff(BattleStatistics origin);

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isPotionAvailable() {
        if (getPlayer().getQueue().isEmpty()) {
            getPlayer().setInEffective(null);
            return false;
        }
        return true;
    }
}
