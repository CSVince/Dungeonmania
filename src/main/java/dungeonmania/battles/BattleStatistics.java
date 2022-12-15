package dungeonmania.battles;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.enemies.Hydra;

public class BattleStatistics {
    public static final double DEFAULT_DAMAGE_MAGNIFIER = 1.0;
    public static final double DEFAULT_PLAYER_DAMAGE_REDUCER = 10.0;
    public static final double DEFAULT_ENEMY_DAMAGE_REDUCER = 5.0;

    private double health;
    private double attack;
    private double defence;
    private double magnifier;
    private double reducer;
    private boolean invincible;
    private boolean enabled;

    public BattleStatistics(
            double health,
            double attack,
            double defence,
            double attackMagnifier,
            double damageReducer) {
        this.health = health;
        this.attack = attack;
        this.defence = defence;
        this.magnifier = attackMagnifier;
        this.reducer = damageReducer;
        this.invincible = false;
        this.enabled = true;
    }

    public BattleStatistics(
            double health,
            double attack,
            double defence,
            double attackMagnifier,
            double damageReducer,
            boolean isInvincible,
            boolean isEnabled) {
        this.health = health;
        this.attack = attack;
        this.defence = defence;
        this.magnifier = attackMagnifier;
        this.reducer = damageReducer;
        this.invincible = isInvincible;
        this.enabled = isEnabled;
    }

    public static List<BattleRound> battle(BattleStatistics self, BattleStatistics target) {
        List<BattleRound> rounds = new ArrayList<>();
        if (self.invincible ^ target.invincible) {
            return checkInvincibleBattle(self, target, rounds);
        }

        while (self.getHealth() > 0 && target.getHealth() > 0) {
            battleOnce(self, target, rounds);
        }
        return rounds;
    }

    public static List<BattleRound> hydraBattle(BattleStatistics self, BattleStatistics target, Hydra hydra) {
        List<BattleRound> rounds = new ArrayList<>();
        if (self.invincible ^ target.invincible) {
            return checkInvincibleBattle(self, target, rounds);
        }

        while (self.getHealth() > 0 && target.getHealth() > 0) {
            if (hydra.growBack()) {
                battleHydraGrowBack(self, target, rounds, hydra);
            } else {
                battleOnce(self, target, rounds);
            }
        }
        return rounds;
    }

    public static List<BattleRound> checkInvincibleBattle(BattleStatistics self,
        BattleStatistics target, List<BattleRound> rounds) {
        double damageOnSelf = (self.invincible) ? 0 : self.getHealth();
        double damageOnTarget = (target.invincible) ? 0 : target.getHealth();
        self.setHealth((self.invincible) ? self.getHealth() : 0);
        target.setHealth((target.invincible) ? target.getHealth() : 0);
        rounds.add(new BattleRound(-damageOnSelf, -damageOnTarget));
        return rounds;
    }

    public static void battleOnce(BattleStatistics self,
        BattleStatistics target, List<BattleRound> rounds) {
        double damageOnSelf = target.getMagnifier() * (target.getAttack() - self.getDefence()) / self.getReducer();
        double damageOnTarget = self.getMagnifier() * (self.getAttack() - target.getDefence())
            / target.getReducer();
        self.setHealth(self.getHealth() - damageOnSelf);
        target.setHealth(target.getHealth() - damageOnTarget);
        rounds.add(new BattleRound(-damageOnSelf, -damageOnTarget));
    }

    public static void battleHydraGrowBack(BattleStatistics self,
        BattleStatistics target, List<BattleRound> rounds, Hydra hydra) {
        double damageOnSelf = target.getMagnifier() * (target.getAttack() - self.getDefence()) / self.getReducer();
        self.setHealth(self.getHealth() - damageOnSelf);
        target.setHealth(target.getHealth() + hydra.getHealthIncrease());
        rounds.add(new BattleRound(-damageOnSelf, hydra.getHealthIncrease()));
    }

    public static BattleStatistics applyBuff(BattleStatistics origin, BattleStatistics buff) {
        return new BattleStatistics(
                origin.health + buff.health,
                origin.attack + buff.attack,
                origin.defence + buff.defence,
                origin.magnifier,
                origin.reducer,
                buff.isInvincible(),
                buff.isEnabled());
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getAttack() {
        return attack;
    }

    public void setAttack(double attack) {
        this.attack = attack;
    }

    public double getDefence() {
        return defence;
    }

    public void setDefence(double defence) {
        this.defence = defence;
    }

    public double getMagnifier() {
        return magnifier;
    }

    public void setMagnifier(double magnifier) {
        this.magnifier = magnifier;
    }

    public double getReducer() {
        return reducer;
    }

    public void setReducer(double reducer) {
        this.reducer = reducer;
    }

    public boolean isInvincible() {
        return this.invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
